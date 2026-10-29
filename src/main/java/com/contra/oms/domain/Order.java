package com.contra.oms.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import java.time.Instant;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/** Order aggregate. All status changes pass through transitionTo so invalid moves cannot be persisted. */
@Entity
@Table(name = "orders")
public class Order {
    @Id
    private UUID id;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private int paymentRetries;

    private String customerName;
    private String customerEmail;
    private String paymentMethod;
    private String trackingNumber;
    private BigDecimal total = BigDecimal.ZERO;

    @Version
    private long version;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("occurredAt ASC")
    private List<OrderTransition> transitions = new ArrayList<>();

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderLine> items = new ArrayList<>();

    protected Order() { }

    public Order(UUID id, String actor, Instant createdAt) {
        this.id = id;
        this.status = OrderStatus.CREATED;
        this.transitions.add(new OrderTransition(this, null, OrderStatus.CREATED, actor, createdAt));
    }

    public void setCheckoutDetails(String customerName, String customerEmail, String paymentMethod) {
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.paymentMethod = paymentMethod;
    }

    public void addItem(OrderLine item) {
        items.add(item);
        item.setOrder(this);
        total = total.add(item.getLineTotal());
    }

    public void setTrackingNumber(String trackingNumber) { this.trackingNumber = trackingNumber; }

    public static Order create(String actor) {
        return new Order(UUID.randomUUID(), actor, Instant.now());
    }

    public void transitionTo(OrderStatus next, String actor, int maxPaymentRetries, Instant occurredAt) {
        if (next == null || actor == null || actor.isBlank() || occurredAt == null) {
            throw new IllegalArgumentException("Next status, actor, and timestamp are required");
        }
        if (!isAllowed(next, maxPaymentRetries)) {
            throw new InvalidOrderTransitionException("Cannot move order " + id + " from " + status + " to " + next);
        }
        if (status == OrderStatus.PAYMENT_FAILED && next == OrderStatus.PAYMENT_PENDING) paymentRetries++;
        OrderStatus previous = status;
        status = next;
        transitions.add(new OrderTransition(this, previous, next, actor, occurredAt));
    }

    private boolean isAllowed(OrderStatus next, int maxPaymentRetries) {
        if (maxPaymentRetries < 0) throw new IllegalArgumentException("maxPaymentRetries cannot be negative");
        if (next == OrderStatus.CANCELLED) {
            return status != OrderStatus.SHIPPED && status != OrderStatus.DELIVERED
                    && status != OrderStatus.CANCELLED && status != OrderStatus.RETURN_REQUESTED
                    && status != OrderStatus.RETURNED && status != OrderStatus.REFUNDED;
        }
        return switch (status) {
            case CREATED -> next == OrderStatus.PAYMENT_PENDING;
            case PAYMENT_PENDING -> next == OrderStatus.PAID || next == OrderStatus.PAYMENT_FAILED;
            case PAYMENT_FAILED -> next == OrderStatus.PAYMENT_PENDING && paymentRetries < maxPaymentRetries;
            case PAID -> next == OrderStatus.INVENTORY_ALLOCATED;
            case INVENTORY_ALLOCATED -> next == OrderStatus.FULFILLING;
            case FULFILLING -> next == OrderStatus.SHIPPED;
            case SHIPPED -> next == OrderStatus.DELIVERED;
            case DELIVERED -> next == OrderStatus.RETURN_REQUESTED;
            case RETURN_REQUESTED -> next == OrderStatus.RETURNED;
            case RETURNED -> next == OrderStatus.REFUNDED;
            case CANCELLED, REFUNDED -> false;
        };
    }

    public UUID getId() { return id; }
    public OrderStatus getStatus() { return status; }
    public int getPaymentRetries() { return paymentRetries; }
    public List<OrderTransition> getTransitions() { return Collections.unmodifiableList(transitions); }
    public List<OrderLine> getItems() { return Collections.unmodifiableList(items); }
    public String getCustomerName() { return customerName; }
    public String getCustomerEmail() { return customerEmail; }
    public String getPaymentMethod() { return paymentMethod; }
    public String getTrackingNumber() { return trackingNumber; }
    public BigDecimal getTotal() { return total; }
}
