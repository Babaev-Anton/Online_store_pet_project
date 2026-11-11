package com.dp.onlinestore.api.requests;

import com.dp.onlinestore.api.base.BaseTest;
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class CartRequest extends BaseTest {

    public static Response addItem(String token, Long productId, int quantity) {
        Map<String, Object> body = new HashMap<>();
        body.put("productId", productId);
        body.put("quantity", quantity);

        return given()
                .spec(spec)
                .header("Authorization", "Bearer " + token)
                .body(body)
                .when()
                .post("/cart/items");
    }

    public static Response getCart(String token) {
        return given()
                .spec(spec)
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/cart");
    }
}