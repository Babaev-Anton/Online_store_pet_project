package com.dp.onlinestore.integration;

import java.math.BigDecimal;

public interface PaymentClient {

    String[] createPayment(Long orderId, BigDecimal amount, String idempotenceKey);
}