package com.dp.onlinestore.controller;

import com.dp.onlinestore.dto.PaymentInitResponse;
import com.dp.onlinestore.dto.YooKassaWebhookPayload;
import com.dp.onlinestore.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/{orderId}/initiate")
    public ResponseEntity<PaymentInitResponse> initiatePayment(
            Authentication authentication,
            @PathVariable Long orderId) {
        return ResponseEntity.ok(paymentService.initiatePayment(orderId, authentication.getName()));
    }

    @PostMapping("/webhook")
    public ResponseEntity<Void> handleWebhook(@RequestBody YooKassaWebhookPayload payload) {
        // В реальной ЮKassa здесь нужно проверять IP-адрес отправителя и подпись
        paymentService.handleWebhook(payload);
        return ResponseEntity.ok().build();
    }
}