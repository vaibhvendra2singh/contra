package com.contra.oms.api;

import com.contra.oms.domain.Order;
import com.contra.oms.domain.OrderLine;
import com.contra.oms.domain.OrderTransition;
import com.contra.oms.domain.Product;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class ApiModels {
    private ApiModels() { }

    public record ProductView(UUID id, String sku, String name, String description, BigDecimal price,
                              int stockQuantity, int allocatedQuantity, int availableQuantity) {
        public static ProductView from(Product p) {
            return new ProductView(p.getId(), p.getSku(), p.getName(), p.getDescription(), p.getPrice(),
                    p.getStockQuantity(), p.getAllocatedQuantity(), p.availableQuantity());
        }
    }
    public record ItemRequest(UUID productId, int quantity) { }
    public record CheckoutRequest(String customerName, String customerEmail, String paymentMethod,
                                  String paymentToken, List<ItemRequest> items) { }
    public record PaymentRetryRequest(String paymentToken) { }
    public record CheckoutResponse(UUID orderId, String status, String message) { }
    public record OrderLineView(UUID productId, String sku, String productName, int quantity,
                                BigDecimal unitPrice, BigDecimal lineTotal) {
        static OrderLineView from(OrderLine line) {
            return new OrderLineView(line.getProductId(), line.getSku(), line.getProductName(), line.getQuantity(),
                    line.getUnitPrice(), line.getLineTotal());
        }
    }
    public record TimelineEntry(String fromStatus, String toStatus, String actor, Instant occurredAt) {
        static TimelineEntry from(OrderTransition t) {
            return new TimelineEntry(t.getFromStatus() == null ? null : t.getFromStatus().name(),
                    t.getToStatus().name(), t.getActor(), t.getOccurredAt());
        }
    }
    public record OrderView(UUID id, String status, String customerName, String customerEmail,
                            String paymentMethod, BigDecimal total, String trackingNumber, int paymentRetries,
                            List<OrderLineView> items, List<TimelineEntry> timeline) {
        public static OrderView from(Order order) {
            return new OrderView(order.getId(), order.getStatus().name(), order.getCustomerName(),
                    order.getCustomerEmail(), order.getPaymentMethod(), order.getTotal(), order.getTrackingNumber(),
                    order.getPaymentRetries(), order.getItems().stream().map(OrderLineView::from).toList(),
                    order.getTransitions().stream().map(TimelineEntry::from).toList());
        }
    }
    public record TrackingRequest(String trackingNumber) { }
    public record ShipRequest(String trackingNumber) { }
    public record RestockRequest(int quantity) { }
    public record DashboardView(Map<String, Long> ordersByStatus, long stuckOrders, long failedPayments,
                                List<OrderView> fulfillmentQueue, List<NotificationView> recentNotifications) { }
    public record NotificationView(UUID orderId, String recipient, String channel, String subject,
                                   String message, Instant createdAt) { }
    public record FeatureFlagView(String key, String description, boolean enabled, String jiraIssueKey,
                                  String qaIssueKey) { }
    public record ToggleRequest(boolean enabled) { }
    public record JiraIssueView(String key, String summary, String status, int points, boolean openBlocker,
                                String url) { }
    public record ReleaseReadinessView(String version, boolean jiraAvailable, boolean ready, List<String> reasons,
                                       Map<String, List<JiraIssueView>> issuesByStatus, int donePoints,
                                       int totalPoints, List<JiraIssueView> blockers) { }
    public record JiraPanelView(boolean jiraAvailable, String boardUrl, String sprintName, String sprintGoal,
                                int donePoints, int totalPoints, List<JiraIssueView> recentIssues) { }
    public record ErrorView(String error, String message) { }
}
