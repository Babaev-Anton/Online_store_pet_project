package com.dp.onlinestore.api.requests;

import com.dp.onlinestore.api.base.BaseTest;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class OrderRequest extends BaseTest {

    public static Response createOrder(String token) {
        return given()
                .spec(spec)
                .header("Authorization", "Bearer " + token)
                .when()
                .post("/orders");
    }

    public static Response getOrderById(String token, Long orderId) {
        return given()
                .spec(spec)
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/orders/" + orderId);
    }
}