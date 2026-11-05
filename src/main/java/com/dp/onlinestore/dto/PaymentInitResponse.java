package com.dp.onlinestore.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PaymentInitResponse {
    private String paymentId;
    private String confirmationUrl;
}