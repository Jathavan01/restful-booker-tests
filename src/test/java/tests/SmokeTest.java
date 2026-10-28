package tests;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

class SmokeTest {

    @BeforeAll
    static void setup() {
        RestAssured.baseURI = "https://restful-booker.herokuapp.com";
    }

    @Test
    void pingReturnsCreated() {
        given()
                .when()
                .get("/ping")
                .then()
                .statusCode(201);
    }

    @Test
    void authReturnsToken() {
        given()
                .contentType("application/json")
                .body("{\"username\":\"admin\",\"password\":\"password123\"}")
                .when()
                .post("/auth")
                .then()
                .statusCode(200)
                .body("token", notNullValue());
    }
}