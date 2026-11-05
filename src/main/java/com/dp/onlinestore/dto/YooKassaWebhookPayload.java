package com.dp.onlinestore.dto;

import lombok.Data;

@Data
public class YooKassaWebhookPayload {
    private String type; // "notification"
    private String event; // "payment.succeeded" или "payment.canceled"
    private PaymentObject object;

    @Data
    public static class PaymentObject {
        private String id; // provider_payment_id
        private String status; // "succeeded", "canceled"
        private Boolean paid;
    }
}