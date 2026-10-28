package api;

import io.restassured.response.Response;
import models.Booking;

import java.util.Optional;

import static io.restassured.RestAssured.given;

/**
 * Looks up a booking by ID. Takes the base URL as a parameter so tests can point it
 * at a WireMock server instead of the real API.
 */
public class BookingLookupService {

    private final String baseUrl;

    public BookingLookupService(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    // Returns the booking, or empty if it doesn't exist (404).
    // Retries once on a 5xx error; any other unexpected status throws.
    public Optional<Booking> findById(int id) {
        Response response = fetch(id);
        if (response.statusCode() >= 500) {
            response = fetch(id);   // one retry
        }
        if (response.statusCode() == 404) {
            return Optional.empty();
        }
        if (response.statusCode() != 200) {
            throw new IllegalStateException("Booking service returned " + response.statusCode());
        }
        return Optional.of(response.as(Booking.class));
    }

    private Response fetch(int id) {
        return given().baseUri(baseUrl).header("Accept", "application/json")
                .when().get("/booking/" + id);
    }
}