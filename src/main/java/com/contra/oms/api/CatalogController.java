package com.contra.oms.api;

import com.contra.oms.service.OrderManagementService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CatalogController {
    private final OrderManagementService service;
    public CatalogController(OrderManagementService service) { this.service = service; }
    @GetMapping("/products")
    public List<ApiModels.ProductView> products() { return service.catalog(); }
}
