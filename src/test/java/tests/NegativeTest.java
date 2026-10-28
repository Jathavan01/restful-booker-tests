package tests;

import api.ApiSpecs;
import api.AuthClient;
import api.BookingClient;
import api.TestData;
import models.CreateBookingResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;

class NegativeTests {

    private static String token;
    private int bookingId;

    @BeforeAll
    static void authenticate() {
        token = AuthClient.getToken();
    }

    @BeforeEach
    void createBooking() {
        bookingId = BookingClient.create(TestData.newBooking())
                .as(CreateBookingResponse.class).getBookingid();
    }

    @AfterEach
    void cleanUp() {
        BookingClient.delete(bookingId, token);
    }

    @Test
    void updateWithoutTokenReturns403() {
        BookingClient.updateWithoutToken(bookingId, TestData.newBooking())
                .then().statusCode(403);
    }

    @Test
    void deleteWithoutTokenReturns403() {
        BookingClient.deleteWithoutToken(bookingId)
                .then().statusCode(403);
    }

    @Test
    void updateWithInvalidTokenReturns403() {
        BookingClient.update(bookingId, TestData.newBooking(), "invalidtoken123")
                .then().statusCode(403);
    }

    @Test
    void getNonExistentBookingReturns404() {
        BookingClient.get(999999999).then().statusCode(404);
    }

    @Test
    void invalidLoginDoesNotReturnToken() {
        given()
                .spec(ApiSpecs.base())
                .body(Map.of("username", "admin", "password", "wrongpassword"))
                .when()
                .post("/auth")
                .then()
                .body("token", nullValue())
                .body("reason", equalTo("Bad credentials"));
    }
}