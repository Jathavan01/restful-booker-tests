package tests;

import api.AuthClient;
import api.BookingClient;
import io.restassured.response.Response;
import models.Booking;
import models.BookingDates;
import models.CreateBookingResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BookingCrudTest {

    private static String token;
    private Integer createdId;

    @BeforeAll
    static void authenticate() {
        token = AuthClient.getToken();
    }

    @AfterEach
    void cleanUp() {
        if (createdId != null) {
            BookingClient.delete(createdId, token);
            createdId = null;
        }
    }

    private Booking newBooking() {
        return new Booking("Jim" + System.currentTimeMillis() % 1000, "Brown", 150, true,
                new BookingDates("2026-12-01", "2026-12-05"), "Breakfast");
    }

    @Test
    void createBookingReturnsSavedDetails() {
        Booking booking = newBooking();

        Response response = BookingClient.create(booking);
        response.then().statusCode(200);

        CreateBookingResponse body = response.as(CreateBookingResponse.class);
        createdId = body.getBookingid();

        assertEquals(booking.getFirstname(), body.getBooking().getFirstname());
        assertEquals(booking.getLastname(), body.getBooking().getLastname());
        assertEquals(booking.getTotalprice(), body.getBooking().getTotalprice());
        assertEquals(booking.getBookingdates().getCheckin(), body.getBooking().getBookingdates().getCheckin());
    }

    @Test
    void getBookingReturnsCreatedBooking() {
        Booking booking = newBooking();
        createdId = BookingClient.create(booking).as(CreateBookingResponse.class).getBookingid();

        Booking fetched = BookingClient.get(createdId)
                .then().statusCode(200)
                .extract().as(Booking.class);

        assertEquals(booking.getFirstname(), fetched.getFirstname());
        assertEquals(booking.getAdditionalneeds(), fetched.getAdditionalneeds());
    }
}