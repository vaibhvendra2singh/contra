package com.contra.oms.service;

import java.math.BigDecimal;

public interface PaymentGateway {
    GatewayResult capture(BigDecimal amount, String method, String token);
    GatewayResult refund(BigDecimal amount, String reference);

    record GatewayResult(boolean success, String reference, String message) { }
}
