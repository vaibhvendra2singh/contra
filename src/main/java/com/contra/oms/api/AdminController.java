package com.contra.oms.api;

import com.contra.oms.service.OrderManagementService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final OrderManagementService service;
    public AdminController(OrderManagementService service) { this.service = service; }

    @GetMapping("/dashboard")
    public ApiModels.DashboardView dashboard() { return service.dashboard(); }

    @GetMapping("/orders")
    public List<ApiModels.OrderView> orders() { return service.allOrders(); }

    @GetMapping("/fulfillment")
    public List<ApiModels.OrderView> fulfillment() { return service.dashboard().fulfillmentQueue(); }

    @PostMapping("/orders/{id}/fulfill")
    public ApiModels.OrderView fulfill(@PathVariable UUID id) { return service.beginFulfillment(id); }

    @PostMapping("/orders/{id}/ship")
    public ApiModels.OrderView ship(@PathVariable UUID id, @RequestBody ApiModels.ShipRequest request) {
        return service.ship(id, request.trackingNumber());
    }

    @PostMapping("/orders/{id}/deliver")
    public ApiModels.OrderView deliver(@PathVariable UUID id) { return service.markDelivered(id); }

    @PostMapping("/orders/{id}/returned")
    public ApiModels.OrderView returned(@PathVariable UUID id) { return service.markReturned(id, "admin"); }

    @GetMapping("/notifications")
    public List<ApiModels.NotificationView> notifications() { return service.notificationLog(); }

    @GetMapping("/flags")
    public List<ApiModels.FeatureFlagView> flags() { return service.listFlags(); }

    @PutMapping("/flags/{key}")
    public ApiModels.FeatureFlagView toggle(@PathVariable String key, @RequestBody ApiModels.ToggleRequest request) {
        return service.toggleFlag(key, request.enabled());
    }

    @PostMapping("/products/{id}/restock")
    public List<ApiModels.ProductView> restock(@PathVariable UUID id, @RequestBody ApiModels.RestockRequest request) {
        return service.restock(id, request.quantity());
    }
}
