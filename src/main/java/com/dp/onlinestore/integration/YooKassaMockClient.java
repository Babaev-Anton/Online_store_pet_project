package com.dp.onlinestore.integration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.util.UUID;

@Component
@Slf4j
public class YooKassaMockClient implements PaymentClient {

    @Override
    public String[] createPayment(Long orderId, BigDecimal amount, String idempotenceKey) {
        // Имитируем задержку сети
        try { Thread.sleep(100); } catch (InterruptedException e) {}

        String fakePaymentId = UUID.randomUUID().toString();
        // Имитируем ссылку на оплату ЮKassa
        String fakeConfirmationUrl = "https://yoomoney.ru/checkout/payments/v2/contract?orderId=" + fakePaymentId;

        log.info("Создан mock-платеж в ЮKassa. ID: {}, Сумма: {} RUB", fakePaymentId, amount);
        return new String[]{fakePaymentId, fakeConfirmationUrl};
    }
}