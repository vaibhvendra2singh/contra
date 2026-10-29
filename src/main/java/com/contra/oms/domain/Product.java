package com.contra.oms.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "products")
public class Product {
    @Id
    private UUID id;
    private String sku;
    private String name;
    private String description;
    private BigDecimal price;
    private int stockQuantity;
    private int allocatedQuantity;
    private boolean active = true;
    @Version
    private long version;

    protected Product() { }

    public Product(UUID id, String sku, String name, String description, BigDecimal price, int stockQuantity) {
        this.id = id;
        this.sku = sku;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stockQuantity = stockQuantity;
    }

    public void allocate(int quantity) {
        if (quantity <= 0 || availableQuantity() < quantity) {
            throw new IllegalStateException("Insufficient inventory for " + sku);
        }
        allocatedQuantity += quantity;
    }

    public void release(int quantity, boolean restock) {
        if (quantity <= 0 || allocatedQuantity < quantity) {
            throw new IllegalStateException("Cannot release unallocated quantity for " + sku);
        }
        allocatedQuantity -= quantity;
        if (restock) stockQuantity += quantity;
    }

    public void restock(int quantity) {
        if (quantity <= 0) throw new IllegalArgumentException("Restock quantity must be positive");
        stockQuantity += quantity;
    }

    public void ship(int quantity) {
        if (quantity <= 0 || allocatedQuantity < quantity || stockQuantity < quantity) {
            throw new IllegalStateException("Cannot ship unallocated quantity for " + sku);
        }
        allocatedQuantity -= quantity;
        stockQuantity -= quantity;
    }

    public int availableQuantity() { return stockQuantity - allocatedQuantity; }
    public UUID getId() { return id; }
    public String getSku() { return sku; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public BigDecimal getPrice() { return price; }
    public int getStockQuantity() { return stockQuantity; }
    public int getAllocatedQuantity() { return allocatedQuantity; }
    public boolean isActive() { return active; }
}
