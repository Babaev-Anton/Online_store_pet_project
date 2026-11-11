package com.dp.onlinestore.api.requests;

import com.dp.onlinestore.api.base.BaseTest;
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class PaymentRequest extends BaseTest {

    public static Response initiatePayment(String token, Long orderId) {
        return given()
                .spec(spec)
                .header("Authorization", "Bearer " + token)
                .when()
                .post("/payments/" + orderId + "/initiate");
    }

    public static Response sendWebhook(String paymentId, String event) {
        Map<String, Object> paymentObject = new HashMap<>();
        paymentObject.put("id", paymentId);
        paymentObject.put("status", event.equals("payment.succeeded") ? "succeeded" : "canceled");
        paymentObject.put("paid", event.equals("payment.succeeded"));

        Map<String, Object> body = new HashMap<>();
        body.put("type", "notification");
        body.put("event", event);
        body.put("object", paymentObject);

        return given()
                .spec(spec)
                .body(body)
                .when()
                .post("/payments/webhook");
    }
}