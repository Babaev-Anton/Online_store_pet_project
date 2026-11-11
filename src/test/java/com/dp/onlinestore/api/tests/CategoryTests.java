package com.dp.onlinestore.api.tests;

import com.dp.onlinestore.api.base.BaseTest;
import com.dp.onlinestore.api.requests.CategoryRequest;
import com.dp.onlinestore.api.utils.AuthHelper;
import com.dp.onlinestore.api.utils.TestDataGenerator;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.notNullValue;

@DisplayName("Тесты модуля Категорий")
public class CategoryTests extends BaseTest {

    @Test
    @DisplayName("CAT-01: Создание категории админом (Позитив)")
    public void createCategory_shouldReturn201_whenAdmin() {
        String adminToken = AuthHelper.getAdminToken();
        String name = "Category " + TestDataGenerator.randomSku();
        String slug = "cat-" + TestDataGenerator.randomSku().toLowerCase();

        Response response = CategoryRequest.createCategory(adminToken, name, slug);

        response.then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("name", org.hamcrest.Matchers.equalTo(name));
    }

    @Test
    @DisplayName("CAT-02: Создание категории обычным юзером (Негатив)")
    public void createCategory_shouldReturn403_whenUser() {
        String userToken = AuthHelper.getUserToken();

        Response response = CategoryRequest.createCategory(userToken, "Test", "test-" + TestDataGenerator.randomSku());

        response.then()
                .statusCode(403);
    }
}