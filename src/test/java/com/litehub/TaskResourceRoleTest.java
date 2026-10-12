package com.litehub;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@QuarkusTest
public class TaskResourceRoleTest {

    private String registerAndLogin(String username, String password) {
        given()
            .contentType(ContentType.JSON)
            .body("""
                {
                  "username": "%s",
                  "password": "%s",
                  "email": "%s@example.com"
                }
                """.formatted(username, password, username))
            .when()
            .post("/api/auth/register")
            .then()
            .statusCode(201);

        Response login = given()
            .contentType(ContentType.JSON)
            .body("""
                {
                  "username": "%s",
                  "password": "%s"
                }
                """.formatted(username, password))
            .when()
            .post("/api/auth/login");

        login.then().statusCode(200);
        return login.jsonPath().getString("data.token");
    }

    @Test
    void maintainerCanCreateTask() {
        String ownerUsername = "owner-task-role-" + System.nanoTime();
        String ownerPassword = "123456";
        String ownerToken = registerAndLogin(ownerUsername, ownerPassword);

        String maintainerUsername = "maintainer-task-role-" + System.nanoTime();
        String maintainerPassword = "123456";
        String maintainerToken = registerAndLogin(maintainerUsername, maintainerPassword);

        Long projectId = given()
            .header("Authorization", "Bearer " + ownerToken)
            .contentType(ContentType.JSON)
            .body("""
                {
                  "name": "Task Project",
                  "description": "Maintainer test"
                }
                """)
            .when()
            .post("/api/projects")
            .then()
            .statusCode(201)
            .extract()
            .jsonPath()
            .getLong("data.id");

        Long maintainerUserId = given()
            .contentType(ContentType.JSON)
            .body("""
                {
                  "username": "%s",
                  "password": "%s",
                  "email": "%s@example.com"
                }
                """.formatted(maintainerUsername, maintainerPassword, maintainerUsername))
            .when()
            .post("/api/auth/register")
            .then()
            .statusCode(201)
            .extract()
            .jsonPath()
            .getLong("id");

        given()
            .header("Authorization", "Bearer " + ownerToken)
            .contentType(ContentType.JSON)
            .body("""
                {
                  "userId": %d,
                  "role": "MAINTAINER"
                }
                """.formatted(maintainerUserId))
            .when()
            .post("/api/projects/" + projectId + "/members")
            .then()
            .statusCode(201);

        given()
            .header("Authorization", "Bearer " + maintainerToken)
            .contentType(ContentType.JSON)
            .body("""
                {
                  "projectId": %d,
                  "title": "Implement RBAC",
                  "description": "Maintainer can create tasks"
                }
                """.formatted(projectId))
            .when()
            .post("/api/tasks")
            .then()
            .statusCode(201)
            .body("data.title", equalTo("Implement RBAC"));
    }
}
