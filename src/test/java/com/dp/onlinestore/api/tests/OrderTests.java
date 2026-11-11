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

@DisplayName("Тесты модуля Заказов")
public class OrderTests extends BaseTest {

    // Хелпер: создает товар, добавляет в корзину и создает заказ. Возвращает ID заказа.
    private Long prepareOrder(String userToken, String adminToken) {
        // 1. Создаем категорию и товар
        Response catResp = CategoryRequest.createCategory(adminToken, "Order Cat " + TestDataGenerator.randomSku(), "cat-" + TestDataGenerator.randomSku());
        Long categoryId = catResp.jsonPath().getLong("id");
        Response prodResp = ProductRequest.createProduct(adminToken, TestDataGenerator.randomSku(), "Order Product", 500.0, 10, categoryId);
        Long productId = prodResp.jsonPath().getLong("id");

        // 2. Добавляем в корзину
        CartRequest.addItem(userToken, productId, 2);

        // 3. Создаем заказ
        Response orderResp = OrderRequest.createOrder(userToken);
        return orderResp.jsonPath().getLong("id");
    }

    @Test
    @DisplayName("ORD-01: Успешное создание заказа из корзины")
    public void createOrder_shouldReturn201_whenValidCart() {
        String adminToken = AuthHelper.getAdminToken();
        String userToken = AuthHelper.getUserToken();
        Long orderId = prepareOrder(userToken, adminToken);

        Response response = OrderRequest.getOrderById(userToken, orderId);

        response.then()
                .statusCode(200)
                .body("status", equalTo("CREATED"))
                .body("totalAmount", equalTo(1000.0f)); // 500.0 * 2
    }

    @Test
    @DisplayName("ORD-02: Ошибка при создании заказа из пустой корзины")
    public void createOrder_shouldReturn400_whenCartIsEmpty() {
        String userToken = AuthHelper.getUserToken(); // Новый юзер, корзина пуста

        Response response = OrderRequest.createOrder(userToken);

        response.then()
                .statusCode(400);
    }

    @Test
    @DisplayName("ORD-03: Защита от IDOR (просмотр чужого заказа)")
    public void getOrderById_shouldReturn403_whenAnotherUser() {
        String adminToken = AuthHelper.getAdminToken();
        String user1Token = AuthHelper.getUserToken();
        String user2Token = AuthHelper.getUserToken();

        Long orderId = prepareOrder(user1Token, adminToken);

        // Пытаемся посмотреть заказ user1 под токеном user2
        Response response = OrderRequest.getOrderById(user2Token, orderId);

        response.then()
                .statusCode(403); // Spring Security должен заблокировать
    }
}