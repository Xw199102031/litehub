package com.litehub;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@QuarkusTest
public class ProjectResourceRoleIntegrationTest {

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
    void memberCannotDeleteProject() {
        String ownerUsername = "owner-role-" + System.nanoTime();
        String ownerPassword = "123456";
        String ownerToken = registerAndLogin(ownerUsername, ownerPassword);

        String memberUsername = "member-role-" + System.nanoTime();
        String memberPassword = "123456";
        String memberToken = registerAndLogin(memberUsername, memberPassword);

        Long projectId = given()
            .header("Authorization", "Bearer " + ownerToken)
            .contentType(ContentType.JSON)
            .body("""
                {
                  "name": "Secured Project",
                  "description": "Only owner can delete"
                }
                """)
            .when()
            .post("/api/projects")
            .then()
            .statusCode(201)
            .extract()
            .jsonPath()
            .getLong("data.id");

        Long memberUserId = given()
            .contentType(ContentType.JSON)
            .body("""
                {
                  "username": "%s",
                  "password": "%s",
                  "email": "%s@example.com"
                }
                """.formatted(memberUsername, memberPassword, memberUsername))
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
                  "role": "MEMBER"
                }
                """.formatted(memberUserId))
            .when()
            .post("/api/projects/" + projectId + "/members")
            .then()
            .statusCode(201);

        given()
            .header("Authorization", "Bearer " + memberToken)
            .when()
            .delete("/api/projects/" + projectId)
            .then()
            .statusCode(403);
    }
}
