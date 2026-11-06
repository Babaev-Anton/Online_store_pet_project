package com.dp.onlinestore.service;

import com.dp.onlinestore.event.OrderStatusChangedEvent;
import com.dp.onlinestore.integration.NotificationClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationClient notificationClient;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOrderStatusChanged(OrderStatusChangedEvent event) {
        try {
            String subject = "Статус вашего заказа изменен";
            String body = String.format("Здравствуйте! Статус вашего заказа №%d изменен на: %s",
                    event.orderId(), event.status());

            notificationClient.sendEmail(event.email(), subject, body);
            log.info("Уведомление об изменении статуса заказа {} отправлено", event.orderId());

        } catch (Exception e) {
            // Логируем ошибку, но не пробрасываем её дальше, чтобы не ломать основной процесс
            log.error("Ошибка при отправке email для заказа {}", event.orderId(), e);
        }
    }
}