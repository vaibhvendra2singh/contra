package com.contra.oms.api;

import com.contra.oms.service.OrderManagementService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api")
public class OrderController {
    private final OrderManagementService service;
    public OrderController(OrderManagementService service) { this.service = service; }

    @PostMapping("/orders")
    public ApiModels.CheckoutResponse checkout(@RequestBody ApiModels.CheckoutRequest request) { return service.checkout(request); }

    @GetMapping("/orders/{id}")
    public ApiModels.OrderView order(@PathVariable UUID id) { return service.getOrder(id); }

    @PostMapping("/orders/{id}/retry-payment")
    public ApiModels.OrderView retry(@PathVariable UUID id, @RequestBody(required = false) ApiModels.PaymentRetryRequest request,
                                     @RequestHeader(value = "X-Actor", required = false) String actor) {
        return service.retryPayment(id, request == null ? null : request.paymentToken(), actor);
    }

    @PostMapping("/orders/{id}/cancel")
    public ApiModels.OrderView cancel(@PathVariable UUID id, @RequestHeader(value = "X-Actor", required = false) String actor) {
        return service.cancel(id, actor);
    }

    @PostMapping("/orders/{id}/returns")
    public ApiModels.OrderView requestReturn(@PathVariable UUID id, @RequestHeader(value = "X-Actor", required = false) String actor) {
        return service.requestReturn(id, actor);
    }

    @PostMapping("/orders/{id}/refund")
    public ApiModels.OrderView refund(@PathVariable UUID id, @RequestHeader(value = "X-Actor", required = false) String actor) {
        return service.refundReturn(id, actor);
    }

    @GetMapping("/track/{id}")
    public ApiModels.OrderView tracking(@PathVariable UUID id) { return service.getOrder(id); }
}
