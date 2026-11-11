package com.dp.onlinestore.api.utils;

import java.util.UUID;

public class TestDataGenerator {
    public static String randomEmail() {
        return "qa_" + UUID.randomUUID().toString().substring(0, 8) + "@example.com";
    }

    public static String randomSku() {
        return "SKU-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}