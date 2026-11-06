package com.dp.onlinestore.service;

import com.dp.onlinestore.dto.PaymentInitResponse;
import com.dp.onlinestore.dto.YooKassaWebhookPayload;
import com.dp.onlinestore.entity.*;
import com.dp.onlinestore.event.OrderStatusChangedEvent;
import com.dp.onlinestore.exception.ResourceNotFoundException;
import com.dp.onlinestore.integration.PaymentClient;
import com.dp.onlinestore.repository.OrderRepository;
import com.dp.onlinestore.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final PaymentClient paymentClient;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public PaymentInitResponse initiatePayment(Long orderId, String userEmail) {
        Order order = orderRepository.findByIdAndUserEmail(orderId, userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Заказ не найден"));

        if (order.getStatus() != OrderStatus.CREATED && order.getStatus() != OrderStatus.AWAITING_PAYMENT) {
            throw new RuntimeException("Заказ не может быть оплачен в текущем статусе");
        }

        // Если платеж уже создан и висит в PENDING, возвращаем его
        if (order.getStatus() == OrderStatus.AWAITING_PAYMENT) {
            // В реальном проекте мы бы искали существующий Payment по orderId
            // Для простоты пропускаем этот шаг
        }

        String idempotenceKey = UUID.randomUUID().toString();

        // Вызываем клиент (в реальности — отправляем POST запрос в ЮKassa)
        String[] clientResponse = paymentClient.createPayment(order.getId(), order.getTotalAmount(), idempotenceKey);
        String providerPaymentId = clientResponse[0];
        String confirmationUrl = clientResponse[1];

        Payment payment = Payment.builder()
                .order(order)
                .provider("YooKassa")
                .providerPaymentId(providerPaymentId)
                .status(PaymentStatus.PENDING)
                .amount(order.getTotalAmount())
                .confirmationUrl(confirmationUrl)
                .build();

        paymentRepository.save(payment);

        order.setStatus(OrderStatus.AWAITING_PAYMENT);
        orderRepository.save(order);

        return new PaymentInitResponse(providerPaymentId, confirmationUrl);
    }

    @Transactional
    public void handleWebhook(YooKassaWebhookPayload payload) {
        log.info("Получен вебхук от ЮKassa: событие {}", payload.getEvent());

        String providerPaymentId = payload.getObject().getId();
        Payment payment = paymentRepository.findByProviderPaymentId(providerPaymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Платеж не найден"));

        // ИДЕМПОТЕНТНОСТЬ: Если платеж уже обработан, игнорируем повторный вебхук
        if (payment.getStatus() == PaymentStatus.SUCCEEDED) {
            log.info("Платеж {} уже обработан. Игнорируем повтор.", providerPaymentId);
            return;
        }

        Order order = payment.getOrder();

        if ("payment.succeeded".equals(payload.getEvent())) {
            payment.setStatus(PaymentStatus.SUCCEEDED);
            order.setStatus(OrderStatus.PAID);
            log.info("Заказ {} успешно оплачен!", order.getId());
        } else if ("payment.canceled".equals(payload.getEvent())) {
            payment.setStatus(PaymentStatus.FAILED);
            order.setStatus(OrderStatus.FAILED);
            log.info("Платеж по заказу {} отменен.", order.getId());
        }

        paymentRepository.save(payment);
        orderRepository.save(order);

        // Публикуем событие о смене статуса заказа (PAID или FAILED)
        eventPublisher.publishEvent(new OrderStatusChangedEvent(
                order.getId(),
                order.getUser().getEmail(),
                order.getStatus()
        ));
    }
}