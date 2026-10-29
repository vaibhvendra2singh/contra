package com.contra.oms.domain;

public enum OrderStatus {
    CREATED,
    PAYMENT_PENDING,
    PAYMENT_FAILED,
    PAID,
    INVENTORY_ALLOCATED,
    FULFILLING,
    SHIPPED,
    DELIVERED,
    CANCELLED,
    RETURN_REQUESTED,
    RETURNED,
    REFUNDED
}
