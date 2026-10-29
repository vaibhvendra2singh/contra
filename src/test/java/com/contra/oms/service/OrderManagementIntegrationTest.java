package com.contra.oms.service;

import com.contra.oms.api.ApiModels;
import com.contra.oms.domain.OrderStatus;
import com.contra.oms.domain.Product;
import com.contra.oms.repository.PaymentAttemptRepository;
import com.contra.oms.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:oms-test;DB_CLOSE_DELAY=-1",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@Transactional
class OrderManagementIntegrationTest {
    @Autowired private OrderManagementService service;
    @Autowired private ProductRepository products;
    @Autowired private PaymentAttemptRepository payments;

    @Test
    void successfulCheckoutAllocatesInventoryAndCancellationReleasesAndRefunds() {
        Product product = newProduct(4);
        ApiModels.CheckoutResponse checkout = service.checkout(request(product, ""));
        assertEquals(OrderStatus.INVENTORY_ALLOCATED.name(), checkout.status());
        assertEquals(1, products.findById(product.getId()).orElseThrow().getAllocatedQuantity());

        ApiModels.OrderView cancelled = service.cancel(checkout.orderId(), "customer@example.test");
        assertEquals(OrderStatus.CANCELLED.name(), cancelled.status());
        assertEquals(0, products.findById(product.getId()).orElseThrow().getAllocatedQuantity());
        assertEquals(2, payments.count()); // capture and cancellation refund
    }

    @Test
    void failedPaymentCanBeRetriedAndInventoryIsHeldOnlyAfterCapture() {
        Product product = newProduct(3);
        ApiModels.CheckoutResponse checkout = service.checkout(request(product, "fail-first"));
        assertEquals(OrderStatus.PAYMENT_FAILED.name(), checkout.status());
        assertEquals(0, products.findById(product.getId()).orElseThrow().getAllocatedQuantity());

        ApiModels.OrderView retried = service.retryPayment(checkout.orderId(), "success", "customer@example.test");
        assertEquals(OrderStatus.INVENTORY_ALLOCATED.name(), retried.status());
        assertEquals(1, retried.paymentRetries());
        assertEquals(1, products.findById(product.getId()).orElseThrow().getAllocatedQuantity());
        assertEquals(2, payments.count());
    }

    @Test
    void deliveredOrderCanCompleteReturnAndRefundLifecycle() {
        Product product = newProduct(5);
        ApiModels.CheckoutResponse checkout = service.checkout(request(product, ""));
        service.beginFulfillment(checkout.orderId());
        service.ship(checkout.orderId(), "CT-DEMO-123");
        service.markDelivered(checkout.orderId());
        service.requestReturn(checkout.orderId(), "customer@example.test");
        service.markReturned(checkout.orderId(), "admin");
        ApiModels.OrderView refunded = service.refundReturn(checkout.orderId(), "support");

        assertEquals(OrderStatus.REFUNDED.name(), refunded.status());
        Product afterReturn = products.findById(product.getId()).orElseThrow();
        assertEquals(5, afterReturn.getStockQuantity());
        assertEquals(0, afterReturn.getAllocatedQuantity());
        assertEquals(2, payments.count()); // capture and return refund
    }

    private Product newProduct(int stock) {
        return products.save(new Product(UUID.randomUUID(), "TEST-" + UUID.randomUUID().toString().substring(0, 8),
                "Test item", "Used for order lifecycle integration tests", new BigDecimal("19.99"), stock));
    }

    private ApiModels.CheckoutRequest request(Product product, String token) {
        return new ApiModels.CheckoutRequest("Test Customer", "customer@example.test", "CARD", token,
                List.of(new ApiModels.ItemRequest(product.getId(), 1)));
    }
}
