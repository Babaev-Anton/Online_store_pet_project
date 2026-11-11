package com.dp.onlinestore.api.base;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
public class BaseTest {

    protected static RequestSpecification spec;

    @BeforeAll
    public static void setup() {
        // Настройка базового URI и порта нашего API
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8080;
        RestAssured.basePath = "/api";

        // Базовая спецификация для всех запросов (заголовки по умолчанию)
        spec = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .build();
    }
}