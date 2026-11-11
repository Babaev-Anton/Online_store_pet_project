package com.dp.onlinestore.api.requests;

import com.dp.onlinestore.api.base.BaseTest;
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class AuthRequest extends BaseTest {

    public static Response register(String email, String password) {
        Map<String, String> body = new HashMap<>();
        body.put("email", email);
        body.put("password", password);

        return given()
                .spec(spec)
                .body(body)
                .when()
                .post("/auth/register");
    }

    public static Response login(String email, String password) {
        Map<String, String> body = new HashMap<>();
        body.put("email", email);
        body.put("password", password);

        return given()
                .spec(spec)
                .body(body)
                .when()
                .post("/auth/login");
    }
}