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

@Configuration
public class SeedData {
    @Bean
    CommandLineRunner seedCatalog(ProductRepository products, FeatureFlagRepository flags) {
        return args -> {
            seedProduct(products, "CONTRA-MUG", "Contra Ceramic Mug", "A sturdy 350 ml ceramic mug.", "12.00", 40,
                    "00000000-0000-0000-0000-000000000001");
            seedProduct(products, "CONTRA-BAG", "Everyday Canvas Tote", "Reusable cotton canvas tote bag.", "18.50", 25,
                    "00000000-0000-0000-0000-000000000002");
            seedProduct(products, "CONTRA-TEE", "Contra Team T-shirt", "Soft cotton team shirt.", "24.00", 18,
                    "00000000-0000-0000-0000-000000000003");
            flags.findById("self-service-returns").orElseGet(() -> flags.save(new FeatureFlag("self-service-returns", "Customer return request flow", true)));
            flags.findById("new-checkout").orElseGet(() -> flags.save(new FeatureFlag("new-checkout", "Updated checkout experience", false)));
            flags.findById("tracking-notifications").orElseGet(() -> flags.save(new FeatureFlag("tracking-notifications", "Shipping status notifications", true)));
        };
    }

    private void seedProduct(ProductRepository repository, String sku, String name, String description,
                             String price, int stock, String id) {
        UUID uuid = UUID.fromString(id);
        if (!repository.existsById(uuid)) repository.save(new Product(uuid, sku, name, description, new BigDecimal(price), stock));
    }
}
