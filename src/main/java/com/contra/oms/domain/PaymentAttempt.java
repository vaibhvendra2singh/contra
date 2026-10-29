package com.contra.oms.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "payment_attempts")
public class PaymentAttempt {
    public enum Outcome { CAPTURED, FAILED, REFUNDED }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Order order;
    private String method;
    private BigDecimal amount;
    @Enumerated(EnumType.STRING)
    private Outcome outcome;
    private String reference;
    private Instant occurredAt;

    protected PaymentAttempt() { }

    public PaymentAttempt(Order order, String method, BigDecimal amount, Outcome outcome, String reference, Instant occurredAt) {
        this.order = order;
        this.method = method;
        this.amount = amount;
        this.outcome = outcome;
        this.reference = reference;
        this.occurredAt = occurredAt;
    }

    public Outcome getOutcome() { return outcome; }
    public String getMethod() { return method; }
    public BigDecimal getAmount() { return amount; }
    public String getReference() { return reference; }
    public Instant getOccurredAt() { return occurredAt; }
}
