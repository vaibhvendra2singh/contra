package com.contra.oms.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "notification_logs")
public class NotificationLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private UUID orderId;
    private String recipient;
    private String channel;
    private String subject;
    private String message;
    private Instant createdAt;

    protected NotificationLog() { }

    public NotificationLog(UUID orderId, String recipient, String channel, String subject, String message, Instant createdAt) {
        this.orderId = orderId;
        this.recipient = recipient;
        this.channel = channel;
        this.subject = subject;
        this.message = message;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public UUID getOrderId() { return orderId; }
    public String getRecipient() { return recipient; }
    public String getChannel() { return channel; }
    public String getSubject() { return subject; }
    public String getMessage() { return message; }
    public Instant getCreatedAt() { return createdAt; }
}
