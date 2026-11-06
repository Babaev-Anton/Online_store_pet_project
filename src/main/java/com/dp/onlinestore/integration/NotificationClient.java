package com.dp.onlinestore.integration;

public interface NotificationClient {
    void sendEmail(String to, String subject, String body);
}