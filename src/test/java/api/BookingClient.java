package api;

import io.restassured.response.Response;
import models.Booking;

import java.util.Map;

import static io.restassured.RestAssured.given;

/**
 * One method per booking endpoint. Tests call these instead of building requests
 * themselves, so request details live here and the tests stay short and readable.
 * Each method returns the raw Response so the test decides what to assert.
 */
public class BookingClient {

    // POST /booking: creates a booking from a Booking object (Jackson converts it to JSON)
    public static Response create(Booking booking) {
        return given().spec(ApiSpecs.base()).body(booking)
                .when().post("/booking");
    }

    // GET /booking/{id}: no token needed to read a booking
    public static Response get(int id) {
        return given().spec(ApiSpecs.base())
                .when().get("/booking/" + id);
    }

    // PUT /booking/{id}: replaces the whole booking; the API reads the token from a cookie
    public static Response update(int id, Booking booking, String token) {
        return given().spec(ApiSpecs.base()).cookie("token", token).body(booking)
                .when().put("/booking/" + id);
    }

    // PATCH /booking/{id}: changes only the fields in the map
    public static Response partialUpdate(int id, Map<String, Object> fields, String token) {
        return given().spec(ApiSpecs.base()).cookie("token", token).body(fields)
                .when().patch("/booking/" + id);
    }

    // DELETE /booking/{id}: also needs the token cookie
    public static Response delete(int id, String token) {
        return given().spec(ApiSpecs.base()).cookie("token", token)
                .when().delete("/booking/" + id);
    }

    // The two methods below skip the token on purpose, for the security tests
    public static Response updateWithoutToken(int id, Booking booking) {
        return given().spec(ApiSpecs.base()).body(booking)
                .when().put("/booking/" + id);
    }

    public static Response deleteWithoutToken(int id) {
        return given().spec(ApiSpecs.base())
                .when().delete("/booking/" + id);
    }
}