package com.contra.oms.domain;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class OrderStateMachineTest {
    private static final Instant NOW = Instant.parse("2026-01-01T00:00:00Z");

    @Test
    void acceptsFullOrderAndReturnLifecycleAndRecordsEveryTransition() {
        Order order = new Order(java.util.UUID.randomUUID(), "customer-1", NOW);
        OrderStatus[] path = {OrderStatus.PAYMENT_PENDING, OrderStatus.PAID, OrderStatus.INVENTORY_ALLOCATED,
                OrderStatus.FULFILLING, OrderStatus.SHIPPED, OrderStatus.DELIVERED,
                OrderStatus.RETURN_REQUESTED, OrderStatus.RETURNED, OrderStatus.REFUNDED};
        for (OrderStatus status : path) order.transitionTo(status, "operator", 3, NOW.plusSeconds(1));

        assertEquals(OrderStatus.REFUNDED, order.getStatus());
        assertEquals(10, order.getTransitions().size()); // creation plus each of the nine transitions
        assertEquals("operator", order.getTransitions().get(1).getActor());
        assertEquals(NOW.plusSeconds(1), order.getTransitions().get(1).getOccurredAt());
    }

    @Test
    void permitsPaymentRetriesOnlyUpToConfiguredMaximum() {
        Order order = Order.create("customer");
        order.transitionTo(OrderStatus.PAYMENT_PENDING, "customer", 2, NOW);
        order.transitionTo(OrderStatus.PAYMENT_FAILED, "gateway", 2, NOW);
        order.transitionTo(OrderStatus.PAYMENT_PENDING, "customer", 2, NOW);
        order.transitionTo(OrderStatus.PAYMENT_FAILED, "gateway", 2, NOW);
        order.transitionTo(OrderStatus.PAYMENT_PENDING, "customer", 2, NOW);
        order.transitionTo(OrderStatus.PAYMENT_FAILED, "gateway", 2, NOW);
        assertThrows(InvalidOrderTransitionException.class,
                () -> order.transitionTo(OrderStatus.PAYMENT_PENDING, "customer", 2, NOW));
        assertEquals(2, order.getPaymentRetries());
    }

    @Test
    void cancellationIsAllowedBeforeShippingAndNotAfter() {
        Order cancelBeforePayment = Order.create("customer");
        cancelBeforePayment.transitionTo(OrderStatus.CANCELLED, "customer", 3, NOW);
        assertEquals(OrderStatus.CANCELLED, cancelBeforePayment.getStatus());

        Order shipped = Order.create("customer");
        shipped.transitionTo(OrderStatus.PAYMENT_PENDING, "customer", 3, NOW);
        shipped.transitionTo(OrderStatus.PAID, "gateway", 3, NOW);
        shipped.transitionTo(OrderStatus.INVENTORY_ALLOCATED, "warehouse", 3, NOW);
        shipped.transitionTo(OrderStatus.FULFILLING, "warehouse", 3, NOW);
        shipped.transitionTo(OrderStatus.SHIPPED, "warehouse", 3, NOW);
        assertThrows(InvalidOrderTransitionException.class,
                () -> shipped.transitionTo(OrderStatus.CANCELLED, "customer", 3, NOW));
    }

    @Test
    void rejectsSkippingRequiredStates() {
        Order order = Order.create("customer");
        assertThrows(InvalidOrderTransitionException.class,
                () -> order.transitionTo(OrderStatus.PAID, "gateway", 3, NOW));
        assertEquals(OrderStatus.CREATED, order.getStatus());
        assertEquals(1, order.getTransitions().size());
    }
}
