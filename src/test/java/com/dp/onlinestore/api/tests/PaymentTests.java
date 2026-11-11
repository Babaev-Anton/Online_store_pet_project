package com.dp.onlinestore.api.tests;

import com.dp.onlinestore.api.base.BaseTest;
import com.dp.onlinestore.api.requests.*;
import com.dp.onlinestore.api.utils.AuthHelper;
import com.dp.onlinestore.api.utils.TestDataGenerator;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@DisplayName("Тесты модуля Оплаты")
public class PaymentTests extends BaseTest {

    private Long prepareOrder(String userToken, String adminToken) {
        Response catResp = CategoryRequest.createCategory(adminToken, "Pay Cat " + TestDataGenerator.randomSku(), "cat-" + TestDataGenerator.randomSku());
        Long categoryId = catResp.jsonPath().getLong("id");
        Response prodResp = ProductRequest.createProduct(adminToken, TestDataGenerator.randomSku(), "Pay Product", 100.0, 10, categoryId);
        Long productId = prodResp.jsonPath().getLong("id");
        CartRequest.addItem(userToken, productId, 1);
        Response orderResp = OrderRequest.createOrder(userToken);
        return orderResp.jsonPath().getLong("id");
    }

    @Test
    @DisplayName("PAY-01: Инициация оплаты и успешный вебхук")
    public void webhook_shouldChangeStatusToPaid_whenSucceeded() {
        String adminToken = AuthHelper.getAdminToken();
        String userToken = AuthHelper.getUserToken();
        Long orderId = prepareOrder(userToken, adminToken);

        // 1. Инициируем платеж
        Response initResp = PaymentRequest.initiatePayment(userToken, orderId);
        initResp.then().statusCode(200).body("confirmationUrl", notNullValue());
        String paymentId = initResp.jsonPath().getString("paymentId");

        // 2. Отправляем успешный вебхук
        Response webhookResp = PaymentRequest.sendWebhook(paymentId, "payment.succeeded");
        webhookResp.then().statusCode(200);

        // 3. Проверяем статус заказа
        Response orderResp = OrderRequest.getOrderById(userToken, orderId);
        orderResp.then().body("status", equalTo("PAID"));
    }

    @Test
    @DisplayName("PAY-02: Идемпотентность вебхука")
    public void webhook_shouldBeIdempotent_whenCalledTwice() {
        String adminToken = AuthHelper.getAdminToken();
        String userToken = AuthHelper.getUserToken();
        Long orderId = prepareOrder(userToken, adminToken);

        Response initResp = PaymentRequest.initiatePayment(userToken, orderId);
        String paymentId = initResp.jsonPath().getString("paymentId");

        // Отправляем вебхук дважды
        PaymentRequest.sendWebhook(paymentId, "payment.succeeded").then().statusCode(200);
        PaymentRequest.sendWebhook(paymentId, "payment.succeeded").then().statusCode(200);

        // Проверяем, что заказ оплачен (без ошибок)
        OrderRequest.getOrderById(userToken, orderId).then().body("status", equalTo("PAID"));
    }
}