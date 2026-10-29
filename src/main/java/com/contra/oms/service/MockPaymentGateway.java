package com.contra.oms.service;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

/** Demo gateway: tokens prefixed with fail- simulate a decline; no real payment data is processed. */
@Component
public class MockPaymentGateway implements PaymentGateway {
    @Override
    public GatewayResult capture(BigDecimal amount, String method, String token) {
        if (token != null && token.startsWith("fail-")) {
            return new GatewayResult(false, "DECLINED-" + UUID.randomUUID(), "Mock payment was declined");
        }
        return new GatewayResult(true, "CAP-" + UUID.randomUUID(), "Payment captured by mock gateway");
    }

    @Override
    public GatewayResult refund(BigDecimal amount, String reference) {
        return new GatewayResult(true, "REF-" + UUID.randomUUID(), "Refund issued by mock gateway");
    }
}
