package com.dp.onlinestore.api.tests;

import com.dp.onlinestore.api.base.BaseTest;
import com.dp.onlinestore.api.requests.AuthRequest;
import com.dp.onlinestore.api.utils.TestDataGenerator;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.*;

@DisplayName("Тесты модуля Аутентификации")
public class AuthTests extends BaseTest {

    @Test
    @DisplayName("AUTH-01: Успешная регистрация")
    public void register_shouldReturn201_whenValidData() {
        String email = TestDataGenerator.randomEmail();

        Response response = AuthRequest.register(email, "password123");

        response.then()
                .statusCode(201)
                .body("accessToken", notNullValue())
                .body("refreshToken", notNullValue());
    }

    @Test
    @DisplayName("AUTH-02: Ошибка при коротком пароле")
    public void register_shouldReturn400_whenPasswordTooShort() {
        String email = TestDataGenerator.randomEmail();

        Response response = AuthRequest.register(email, "123");

        response.then()
                .statusCode(400)
                .body("status", equalTo(400))
                .body("message", containsString("8"));
    }

    @Test
    @DisplayName("AUTH-03: Успешный вход по валидным данным")
    public void login_shouldReturn200_whenValidCredentials() {
        String email = TestDataGenerator.randomEmail();
        String password = "password123";

        // Сначала регистрируем
        AuthRequest.register(email, password);

        // Затем логинимся
        Response response = AuthRequest.login(email, password);

        response.then()
                .statusCode(200)
                .body("accessToken", notNullValue())
                .body("refreshToken", notNullValue());
    }
}