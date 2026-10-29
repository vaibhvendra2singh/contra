package com.contra.oms.service;

import com.contra.oms.api.ApiModels;
import com.contra.oms.domain.FeatureFlag;
import com.contra.oms.domain.InvalidOrderTransitionException;
import com.contra.oms.domain.NotificationLog;
import com.contra.oms.domain.Order;
import com.contra.oms.domain.OrderLine;
import com.contra.oms.domain.OrderStatus;
import com.contra.oms.domain.PaymentAttempt;
import com.contra.oms.domain.PaymentMethod;
import com.contra.oms.domain.Product;
import com.contra.oms.repository.FeatureFlagRepository;
import com.contra.oms.repository.NotificationLogRepository;
import com.contra.oms.repository.OrderRepository;
import com.contra.oms.repository.PaymentAttemptRepository;
import com.contra.oms.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class OrderManagementService {
    private static final String SYSTEM_ACTOR = "system";
    private final OrderRepository orders;
    private final ProductRepository products;
    private final PaymentAttemptRepository payments;
    private final NotificationLogRepository notifications;
    private final FeatureFlagRepository flags;
    private final PaymentGateway gateway;
    private final int maxRetries;
    private final long stuckThresholdHours;

    public OrderManagementService(OrderRepository orders, ProductRepository products,
                                  PaymentAttemptRepository payments, NotificationLogRepository notifications,
                                  FeatureFlagRepository flags, PaymentGateway gateway,
                                  @Value("${oms.payment.max-retries:3}") int maxRetries,
                                  @Value("${oms.orders.stuck-threshold-hours:24}") long stuckThresholdHours) {
        this.orders = orders;
        this.products = products;
        this.payments = payments;
        this.notifications = notifications;
        this.flags = flags;
        this.gateway = gateway;
        this.maxRetries = maxRetries;
        this.stuckThresholdHours = stuckThresholdHours;
    }

    @Transactional(readOnly = true)
    public List<ApiModels.ProductView> catalog() {
        return products.findByActiveTrueOrderByNameAsc().stream().map(ApiModels.ProductView::from).toList();
    }

    @Transactional
    public ApiModels.CheckoutResponse checkout(ApiModels.CheckoutRequest request) {
        if (request == null || request.customerName() == null || request.customerName().isBlank()
                || request.customerEmail() == null || request.customerEmail().isBlank()
                || request.items() == null || request.items().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Customer and at least one item are required");
        }
        PaymentMethod method;
        try { method = PaymentMethod.valueOf(request.paymentMethod().toUpperCase()); }
        catch (Exception ex) { throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Payment method must be CARD or WALLET"); }

        Map<UUID, Integer> requested = new HashMap<>();
        for (ApiModels.ItemRequest item : request.items()) {
            if (item.quantity() <= 0) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Item quantities must be positive");
            requested.merge(item.productId(), item.quantity(), Integer::sum);
        }
        List<Product> lockedProducts = products.findAllForUpdate(requested.keySet());
        if (lockedProducts.size() != requested.size()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "One or more products do not exist");
        Map<UUID, Product> byId = lockedProducts.stream().collect(Collectors.toMap(Product::getId, Function.identity()));
        for (Map.Entry<UUID, Integer> item : requested.entrySet()) {
            Product product = byId.get(item.getKey());
            if (!product.isActive() || product.availableQuantity() < item.getValue()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Insufficient stock for " + product.getSku());
            }
        }

        Order order = Order.create(request.customerEmail());
        order.setCheckoutDetails(request.customerName(), request.customerEmail(), method.name());
        requested.forEach((productId, quantity) -> order.addItem(new OrderLine(byId.get(productId), quantity)));
        orders.save(order);
        notifyStatus(order, "Order created");
        transition(order, OrderStatus.PAYMENT_PENDING, request.customerEmail());
        capturePayment(order, request.paymentToken(), request.customerEmail());
        orders.save(order);
        return new ApiModels.CheckoutResponse(order.getId(), order.getStatus().name(),
                order.getStatus() == OrderStatus.PAYMENT_FAILED ? "Order created; payment failed. Retry payment to continue." : "Order paid and inventory allocated.");
    }

    @Transactional
    public ApiModels.OrderView retryPayment(UUID id, String token, String actor) {
        Order order = requireOrder(id);
        transition(order, OrderStatus.PAYMENT_PENDING, actorOrCustomer(actor, order));
        capturePayment(order, token, actorOrCustomer(actor, order));
        return ApiModels.OrderView.from(orders.save(order));
    }

    private void capturePayment(Order order, String token, String actor) {
        PaymentGateway.GatewayResult result = gateway.capture(order.getTotal(), order.getPaymentMethod(), token);
        payments.save(new PaymentAttempt(order, order.getPaymentMethod(), order.getTotal(),
                result.success() ? PaymentAttempt.Outcome.CAPTURED : PaymentAttempt.Outcome.FAILED,
                result.reference(), Instant.now()));
        if (!result.success()) {
            transition(order, OrderStatus.PAYMENT_FAILED, "payment-gateway");
            return;
        }
        transition(order, OrderStatus.PAID, actor);
        for (OrderLine line : order.getItems()) products.findById(line.getProductId()).orElseThrow().allocate(line.getQuantity());
        transition(order, OrderStatus.INVENTORY_ALLOCATED, "inventory-service");
    }

    @Transactional
    public ApiModels.OrderView cancel(UUID id, String actor) {
        Order order = requireOrder(id);
        OrderStatus old = order.getStatus();
        transition(order, OrderStatus.CANCELLED, actorOrCustomer(actor, order));
        if (old == OrderStatus.PAID || old == OrderStatus.INVENTORY_ALLOCATED || old == OrderStatus.FULFILLING) {
            releaseInventory(order, false);
            refund(order, "Cancellation refund");
        }
        return ApiModels.OrderView.from(order);
    }

    @Transactional
    public ApiModels.OrderView requestReturn(UUID id, String actor) {
        Order order = requireOrder(id);
        transition(order, OrderStatus.RETURN_REQUESTED, actorOrCustomer(actor, order));
        return ApiModels.OrderView.from(order);
    }

    @Transactional
    public ApiModels.OrderView markReturned(UUID id, String actor) {
        Order order = requireOrder(id);
        transition(order, OrderStatus.RETURNED, actorOrCustomer(actor, order));
        for (OrderLine line : order.getItems()) products.findById(line.getProductId()).orElseThrow().restock(line.getQuantity());
        return ApiModels.OrderView.from(order);
    }

    @Transactional
    public ApiModels.OrderView refundReturn(UUID id, String actor) {
        Order order = requireOrder(id);
        transition(order, OrderStatus.REFUNDED, actorOrCustomer(actor, order));
        refund(order, "Return refund");
        return ApiModels.OrderView.from(order);
    }

    @Transactional
    public ApiModels.OrderView beginFulfillment(UUID id) {
        Order order = requireOrder(id);
        transition(order, OrderStatus.FULFILLING, "admin");
        return ApiModels.OrderView.from(order);
    }

    @Transactional
    public ApiModels.OrderView ship(UUID id, String trackingNumber) {
        if (trackingNumber == null || trackingNumber.isBlank()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tracking number is required");
        Order order = requireOrder(id);
        transition(order, OrderStatus.SHIPPED, "admin");
        for (OrderLine line : order.getItems()) products.findById(line.getProductId()).orElseThrow().ship(line.getQuantity());
        order.setTrackingNumber(trackingNumber);
        return ApiModels.OrderView.from(order);
    }

    @Transactional
    public ApiModels.OrderView markDelivered(UUID id) {
        Order order = requireOrder(id);
        transition(order, OrderStatus.DELIVERED, "carrier");
        return ApiModels.OrderView.from(order);
    }

    @Transactional(readOnly = true)
    public ApiModels.OrderView getOrder(UUID id) { return ApiModels.OrderView.from(requireOrder(id)); }

    @Transactional(readOnly = true)
    public List<ApiModels.OrderView> allOrders() {
        return orders.findAll(Sort.by(Sort.Direction.DESC, "id")).stream().map(ApiModels.OrderView::from).toList();
    }

    @Transactional(readOnly = true)
    public ApiModels.DashboardView dashboard() {
        List<Order> all = orders.findAll();
        Map<String, Long> byStatus = new HashMap<>();
        for (OrderStatus status : OrderStatus.values()) byStatus.put(status.name(), 0L);
        all.forEach(o -> byStatus.compute(o.getStatus().name(), (k, n) -> n + 1));
        Instant stuckBefore = Instant.now().minus(Duration.ofHours(stuckThresholdHours));
        long stuck = all.stream().filter(o -> !List.of(OrderStatus.DELIVERED, OrderStatus.CANCELLED, OrderStatus.REFUNDED).contains(o.getStatus()))
                .filter(o -> latestTransitionTime(o).isBefore(stuckBefore)).count();
        long failed = all.stream().filter(o -> o.getStatus() == OrderStatus.PAYMENT_FAILED).count();
        List<ApiModels.OrderView> queue = all.stream().filter(o -> o.getStatus() == OrderStatus.INVENTORY_ALLOCATED || o.getStatus() == OrderStatus.FULFILLING)
                .map(ApiModels.OrderView::from).toList();
        List<ApiModels.NotificationView> recent = notifications.findAll(Sort.by(Sort.Direction.DESC, "createdAt")).stream().limit(10)
                .map(n -> new ApiModels.NotificationView(n.getOrderId(), n.getRecipient(), n.getChannel(), n.getSubject(), n.getMessage(), n.getCreatedAt())).toList();
        return new ApiModels.DashboardView(byStatus, stuck, failed, queue, recent);
    }

    @Transactional(readOnly = true)
    public List<ApiModels.NotificationView> notificationLog() {
        return notifications.findAll(Sort.by(Sort.Direction.DESC, "createdAt")).stream()
                .map(n -> new ApiModels.NotificationView(n.getOrderId(), n.getRecipient(), n.getChannel(), n.getSubject(), n.getMessage(), n.getCreatedAt())).toList();
    }

    @Transactional
    public List<ApiModels.ProductView> restock(UUID id, int quantity) {
        if (quantity <= 0) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Quantity must be positive");
        Product p = products.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
        p.restock(quantity);
        return products.findByActiveTrueOrderByNameAsc().stream().map(ApiModels.ProductView::from).toList();
    }

    @Transactional(readOnly = true)
    public List<ApiModels.FeatureFlagView> listFlags() {
        return flags.findAll(Sort.by("key")).stream().map(f -> new ApiModels.FeatureFlagView(f.getKey(), f.getDescription(), f.isEnabled())).toList();
    }

    @Transactional
    public ApiModels.FeatureFlagView toggleFlag(String key, boolean enabled) {
        FeatureFlag flag = flags.findById(key).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Feature flag not found"));
        flag.setEnabled(enabled);
        return new ApiModels.FeatureFlagView(flag.getKey(), flag.getDescription(), flag.isEnabled());
    }

    private Order requireOrder(UUID id) {
        return orders.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
    }

    private String actorOrCustomer(String actor, Order order) {
        return actor == null || actor.isBlank() ? order.getCustomerEmail() : actor;
    }

    private void transition(Order order, OrderStatus status, String actor) {
        order.transitionTo(status, actor, maxRetries, Instant.now());
        notifyStatus(order, status.name().replace('_', ' '));
    }

    private void notifyStatus(Order order, String description) {
        if (order.getCustomerEmail() == null) return;
        notifications.save(new NotificationLog(order.getId(), order.getCustomerEmail(), "EMAIL + SMS",
                "Order " + description, "Order " + order.getId() + " is now " + order.getStatus() + ".", Instant.now()));
    }

    private void releaseInventory(Order order, boolean restock) {
        for (OrderLine line : order.getItems()) products.findById(line.getProductId()).orElseThrow().release(line.getQuantity(), restock);
    }

    private void refund(Order order, String reason) {
        PaymentGateway.GatewayResult result = gateway.refund(order.getTotal(), order.getId().toString());
        if (!result.success()) throw new IllegalStateException("Refund gateway failed: " + result.message());
        payments.save(new PaymentAttempt(order, order.getPaymentMethod(), order.getTotal(), PaymentAttempt.Outcome.REFUNDED,
                result.reference() + " " + reason, Instant.now()));
    }

    private Instant latestTransitionTime(Order order) {
        return order.getTransitions().get(order.getTransitions().size() - 1).getOccurredAt();
    }
}
