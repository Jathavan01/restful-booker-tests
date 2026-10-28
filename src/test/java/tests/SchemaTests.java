package tests;

import api.AuthClient;
import api.BookingClient;
import api.TestData;
import io.restassured.response.Response;
import models.CreateBookingResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

/**
 * Contract tests: check the shape of API responses (required fields and data types),
 * not just individual values. If the API changes a field name or type, these fail.
 * The schemas live in src/test/resources/schemas.
 */
class SchemaTests {

    private static String token;
    private Integer bookingId;

    @BeforeAll
    static void authenticate() {
        token = AuthClient.getToken();
    }

    // Delete the booking each test created so the shared API isn't left full of test data
    @AfterEach
    void cleanUp() {
        if (bookingId != null) {
            BookingClient.delete(bookingId, token);
            bookingId = null;
        }
    }

    @Test
    void createBookingResponseMatchesSchema() {
        Response response = BookingClient.create(TestData.newBooking());
        bookingId = response.as(CreateBookingResponse.class).getBookingid();

        response.then()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/create-booking-response-schema.json"));
    }

    @Test
    void getBookingResponseMatchesSchema() {
        bookingId = BookingClient.create(TestData.newBooking())
                .as(CreateBookingResponse.class).getBookingid();

        BookingClient.get(bookingId)
                .then()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/booking-schema.json"));
    }
}