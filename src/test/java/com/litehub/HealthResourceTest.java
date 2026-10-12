package com.litehub;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.equalTo;

@QuarkusTest
class HealthResourceTest {

    @Test
    void healthShouldReturnUp() {
        RestAssured.given()
            .when()
            .get("/api/health")
            .then()
            .statusCode(200)
            .body("status", equalTo("UP"));
    }
}
