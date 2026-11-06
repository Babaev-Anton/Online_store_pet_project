package com.dp.onlinestore.integration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ConsoleEmailClient implements NotificationClient {

    @Override
    public void sendEmail(String to, String subject, String body) {
        // В реальном проекте здесь был бы вызов JavaMailSender или SendGrid API
        log.info("========== SEND EMAIL ==========");
        log.info("WHO: {}", to);
        log.info("TITLE: {}", subject);
        log.info("TEXT: {}", body);
        log.info("=====================================");
    }
}