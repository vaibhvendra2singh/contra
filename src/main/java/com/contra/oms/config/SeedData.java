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
    public CommandLineRunner seedCatalog(ProductRepository products, FeatureFlagRepository flags) {
        return args -> {
            seedProduct(products, "CONTRA-MUG", "Contra Ceramic Mug", "A sturdy 350 ml ceramic mug.", "12.00", 40,
                    "00000000-0000-0000-0000-000000000001");
            seedProduct(products, "CONTRA-BAG", "Everyday Canvas Tote", "Reusable cotton canvas tote bag.", "18.50", 25,
                    "00000000-0000-0000-0000-000000000002");
            seedProduct(products, "CONTRA-TEE", "Contra Team T-shirt", "Soft cotton team shirt.", "24.00", 18,
                    "00000000-0000-0000-0000-000000000003");
            seedFlag(flags, "self-service-returns", "Customer return request flow", "OM2-32", "OM2-49");
            seedFlag(flags, "new-checkout", "Updated checkout experience", "OM2-9", "OM2-34");
            seedFlag(flags, "tracking-notifications", "Shipping status notifications", "OM2-20", "OM2-44");
        };
    }

    @Bean
    @Order(2)
    public CommandLineRunner demoOrder(OrderManagementService orders,
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

    @SuppressWarnings("null")
    private void seedProduct(ProductRepository repository, String sku, String name, String description,
                             String price, int stock, String id) {
        UUID uuid = UUID.fromString(id);
        if (!repository.existsById(uuid)) repository.save(new Product(uuid, sku, name, description, new BigDecimal(price), stock));
    }

    @SuppressWarnings("null")
    private void seedFlag(FeatureFlagRepository repository, String key, String description,
                          String storyKey, String qaIssueKey) {
        FeatureFlag flag = repository.findById(key).orElseGet(() -> new FeatureFlag(key, description, false));
        flag.setJiraIssueKey(storyKey);
        flag.setQaIssueKey(qaIssueKey);
        repository.save(flag);
    }
}
