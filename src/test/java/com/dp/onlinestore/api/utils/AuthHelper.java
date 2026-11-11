package com.dp.onlinestore.api.utils;

import com.dp.onlinestore.api.requests.AuthRequest;
import io.restassured.response.Response;

public class AuthHelper {

    public static String getAdminToken() {
        Response response = AuthRequest.login("test@example.com", "password123");
        if (response.statusCode() != 200) {
            AuthRequest.register("test@example.com", "password123");
            response = AuthRequest.login("test@example.com", "password123");
        }
        return response.jsonPath().getString("accessToken");
    }

    public static String getUserToken() {
        String email = TestDataGenerator.randomEmail();
        String password = "password123";

        Response registerResponse = AuthRequest.register(email, password);
        if (registerResponse.statusCode() != 201) {
            System.out.println("Ошибка регистрации: " + registerResponse.getBody().asString());
        }

        Response loginResponse = AuthRequest.login(email, password);
        if (loginResponse.statusCode() != 200) {
            System.out.println("Ошибка логина: " + loginResponse.getBody().asString());
        }

        return loginResponse.jsonPath().getString("accessToken");
    }
}