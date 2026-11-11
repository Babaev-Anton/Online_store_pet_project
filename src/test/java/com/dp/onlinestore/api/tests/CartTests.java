package com.dp.onlinestore.api.tests;

import com.dp.onlinestore.api.base.BaseTest;
import com.dp.onlinestore.api.requests.CartRequest;
import com.dp.onlinestore.api.requests.CategoryRequest;
import com.dp.onlinestore.api.requests.ProductRequest;
import com.dp.onlinestore.api.utils.AuthHelper;
import com.dp.onlinestore.api.utils.TestDataGenerator;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@DisplayName("Тесты модуля Корзины")
public class CartTests extends BaseTest {

    // Хелпер для создания товара и возврата его ID
    private Long createProductForTest(String adminToken) {
        Response catResp = CategoryRequest.createCategory(adminToken, "Cat " + TestDataGenerator.randomSku(), "cat-" + TestDataGenerator.randomSku());
        Long categoryId = catResp.jsonPath().getLong("id");

        Response prodResp = ProductRequest.createProduct(adminToken, TestDataGenerator.randomSku(), "Cart Product", 100.0, 50, categoryId);
        return prodResp.jsonPath().getLong("id");
    }

    @Test
    @DisplayName("CART-02: Суммирование количества при повторном добавлении")
    public void addItem_shouldIncrementQuantity_whenSameProductAdded() {
        String adminToken = AuthHelper.getAdminToken();
        String userToken = AuthHelper.getUserToken();
        Long productId = createProductForTest(adminToken);

        // Добавляем 2 штуки
        CartRequest.addItem(userToken, productId, 2);

        // Добавляем еще 3 штуки
        Response response = CartRequest.addItem(userToken, productId, 3);

        response.then()
                .statusCode(200)
                .body("items[0].quantity", equalTo(5)) // 2 + 3 = 5
                .body("totalPrice", equalTo(500.0f)); // 100.0 * 5
    }

    @Test
    @DisplayName("CART-03: Доступ без токена (Негатив)")
    public void getCart_shouldReturn403_whenNoToken() {
        Response response = CartRequest.getCart(""); // Пустой токен

        response.then()
                .statusCode(403); // Или 401, в зависимости от настройки
    }
}