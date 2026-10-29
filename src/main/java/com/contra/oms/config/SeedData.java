package com.contra.oms.config;

import com.contra.oms.domain.FeatureFlag;
import com.contra.oms.domain.Product;
import com.contra.oms.repository.FeatureFlagRepository;
import com.contra.oms.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.UUID;
import com.contra.oms.service.OrderManagementService;
import com.contra.oms.api.ApiModels;
import org.springframework.core.annotation.Order;

@Configuration
public class SeedData {
    @Bean
    @Order(1)
    CommandLineRunner seedCatalog(ProductRepository products, FeatureFlagRepository flags) {
        return args -> {
            seedProduct(products, "CONTRA-MUG", "Contra Ceramic Mug", "A sturdy 350 ml ceramic mug.", "12.00", 40,
                    "00000000-0000-0000-0000-000000000001");
            seedProduct(products, "CONTRA-BAG", "Everyday Canvas Tote", "Reusable cotton canvas tote bag.", "18.50", 25,
                    "00000000-0000-0000-0000-000000000002");
            seedProduct(products, "CONTRA-TEE", "Contra Team T-shirt", "Soft cotton team shirt.", "24.00", 18,
                    "00000000-0000-0000-0000-000000000003");
            flags.findById("self-service-returns").orElseGet(() -> flags.save(new FeatureFlag("self-service-returns", "Customer return request flow", false, "OM2-32", null)));
            flags.findById("new-checkout").orElseGet(() -> flags.save(new FeatureFlag("new-checkout", "Updated checkout experience", false, "OM2-9", null)));
            flags.findById("tracking-notifications").orElseGet(() -> flags.save(new FeatureFlag("tracking-notifications", "Shipping status notifications", false, "OM2-20", null)));
        };
    }

    @Bean
    @Order(2)
    CommandLineRunner demoOrder(OrderManagementService orders,
                                @org.springframework.beans.factory.annotation.Value("${oms.demo-mode:false}") boolean demoMode) {
        return args -> {
            if (!demoMode || orders.allOrders().stream().anyMatch(o -> "demo@contra.test".equals(o.customerEmail()))) return;
            UUID productId = UUID.fromString("00000000-0000-0000-0000-000000000001");
            ApiModels.CheckoutResponse created = orders.checkout(new ApiModels.CheckoutRequest("Contra Demo Customer",
                    "demo@contra.test", "CARD", "", java.util.List.of(new ApiModels.ItemRequest(productId, 1))));
            UUID id = created.orderId();
            orders.beginFulfillment(id);
            orders.ship(id, "DEMO-TRACK-001");
            orders.markDelivered(id);
        };
    }

    private void seedProduct(ProductRepository repository, String sku, String name, String description,
                             String price, int stock, String id) {
        UUID uuid = UUID.fromString(id);
        if (!repository.existsById(uuid)) repository.save(new Product(uuid, sku, name, description, new BigDecimal(price), stock));
    }
}
