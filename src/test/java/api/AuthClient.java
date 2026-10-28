package api;

import java.util.Map;

import static io.restassured.RestAssured.given;

public class AuthClient {
    public static String getToken() {
        return given()
                .spec(ApiSpecs.base())
                .body(Map.of("username", "admin", "password", "password123"))
                .when()
                .post("/auth")
                .then()
                .statusCode(200)
                .extract().path("token");
    }
}