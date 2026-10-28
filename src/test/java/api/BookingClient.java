package api;

import io.restassured.response.Response;
import models.Booking;

import static io.restassured.RestAssured.given;

public class BookingClient {

    public static Response create(Booking booking) {
        return given().spec(ApiSpecs.base()).body(booking)
                .when().post("/booking");
    }

    public static Response get(int id) {
        return given().spec(ApiSpecs.base())
                .when().get("/booking/" + id);
    }

    public static Response delete(int id, String token) {
        return given().spec(ApiSpecs.base()).cookie("token", token)
                .when().delete("/booking/" + id);
    }
}