package com.dp.onlinestore.api.requests;

import com.dp.onlinestore.api.base.BaseTest;
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class CategoryRequest extends BaseTest {

    public static Response createCategory(String token, String name, String slug) {
        Map<String, String> body = new HashMap<>();
        body.put("name", name);
        body.put("slug", slug);

        return given()
                .spec(spec)
                .header("Authorization", "Bearer " + token)
                .body(body)
                .when()
                .post("/categories");
    }

    public static Response getCategories() {
        return given().spec(spec).when().get("/categories");
    }

    public static Response deleteCategory(String token, Long id) {
        return given()
                .spec(spec)
                .header("Authorization", "Bearer " + token)
                .when()
                .delete("/categories/" + id);
    }
}