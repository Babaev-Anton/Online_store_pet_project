package com.dp.onlinestore.api.requests;

import com.dp.onlinestore.api.base.BaseTest;
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class ProductRequest extends BaseTest {

    public static Response createProduct(String token, String sku, String name, double price, int stock, Long categoryId) {
        Map<String, Object> body = new HashMap<>();
        body.put("sku", sku);
        body.put("name", name);
        body.put("description", "Test Description");
        body.put("price", price);
        body.put("stockQuantity", stock);
        body.put("categoryId", categoryId);

        return given()
                .spec(spec)
                .header("Authorization", "Bearer " + token)
                .body(body)
                .when()
                .post("/products");
    }

    public static Response getProducts() {
        return given().spec(spec).when().get("/products");
    }

    public static Response getProductById(Long id) {
        return given().spec(spec).when().get("/products/" + id);
    }
}