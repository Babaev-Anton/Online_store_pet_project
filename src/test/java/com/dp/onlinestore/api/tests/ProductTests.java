package com.dp.onlinestore.api.tests;

import com.dp.onlinestore.api.base.BaseTest;
import com.dp.onlinestore.api.requests.CategoryRequest;
import com.dp.onlinestore.api.requests.ProductRequest;
import com.dp.onlinestore.api.utils.AuthHelper;
import com.dp.onlinestore.api.utils.TestDataGenerator;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.notNullValue;

@DisplayName("Тесты модуля Товаров")
public class ProductTests extends BaseTest {

    // Вспомогательный метод для создания категории (чтобы не дублировать код)
    private Long createTestCategory(String adminToken) {
        Response catResponse = CategoryRequest.createCategory(adminToken, "Cat " + TestDataGenerator.randomSku(), "cat-" + TestDataGenerator.randomSku());
        return catResponse.jsonPath().getLong("id");
    }

    @Test
    @DisplayName("PROD-01: Создание товара админом (Позитив)")
    public void createProduct_shouldReturn201_whenAdmin() {
        String adminToken = AuthHelper.getAdminToken();
        Long categoryId = createTestCategory(adminToken);
        String sku = TestDataGenerator.randomSku();

        Response response = ProductRequest.createProduct(adminToken, sku, "Test Product", 99.99, 10, categoryId);

        response.then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("sku", org.hamcrest.Matchers.equalTo(sku));
    }

    @Test
    @DisplayName("PROD-02: Создание товара с отрицательной ценой (Негатив)")
    public void createProduct_shouldReturn400_whenNegativePrice() {
        String adminToken = AuthHelper.getAdminToken();
        Long categoryId = createTestCategory(adminToken);

        Response response = ProductRequest.createProduct(adminToken, TestDataGenerator.randomSku(), "Bad Product", -10.0, 5, categoryId);

        response.then()
                .statusCode(400);
    }

    @Test
    @DisplayName("PROD-03: Просмотр списка товаров без токена (Позитив)")
    public void getProducts_shouldReturn200_whenGuest() {
        Response response = ProductRequest.getProducts();

        response.then()
                .statusCode(200)
                .body("content", notNullValue());
    }
}