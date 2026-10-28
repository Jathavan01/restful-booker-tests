package tests;

import api.AuthClient;
import api.BookingClient;
import api.TestData;
import models.Booking;
import models.CreateBookingResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BookingUpdateDeleteTest {

    private static String token;
    private Integer bookingId;
    private Booking original;

    @BeforeAll
    static void authenticate() {
        token = AuthClient.getToken();
    }

    @BeforeEach
    void createBooking() {
        original = TestData.newBooking();
        bookingId = BookingClient.create(original).as(CreateBookingResponse.class).getBookingid();
    }

    @AfterEach
    void cleanUp() {
        if (bookingId != null) {
            BookingClient.delete(bookingId, token);
        }
    }

    @Test
    void fullUpdateReplacesBookingDetails() {
        Booking updated = TestData.newBooking();
        updated.setFirstname("Updated");
        updated.setTotalprice(999);

        Booking result = BookingClient.update(bookingId, updated, token)
                .then().statusCode(200)
                .extract().as(Booking.class);

        assertEquals("Updated", result.getFirstname());
        assertEquals(999, result.getTotalprice());
    }

    @Test
    void partialUpdateChangesOnlySentFields() {
        Booking result = BookingClient.partialUpdate(bookingId, Map.of("firstname", "Patched"), token)
                .then().statusCode(200)
                .extract().as(Booking.class);

        assertEquals("Patched", result.getFirstname());
        assertEquals(original.getLastname(), result.getLastname());
        assertEquals(original.getTotalprice(), result.getTotalprice());
    }

    @Test
    void deleteRemovesBooking() {
        BookingClient.delete(bookingId, token).then().statusCode(201);
        BookingClient.get(bookingId).then().statusCode(404);
        bookingId = null;
    }
}