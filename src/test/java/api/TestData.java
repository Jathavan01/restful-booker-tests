package api;

import models.Booking;
import models.BookingDates;

/**
 * Builds test data in one place so every test starts from the same valid booking.
 */
public class TestData {

    public static Booking newBooking() {
        // The first name gets a number from the clock so bookings are easy to tell apart.
        // This is a shared public API, so tests never depend on data that already exists.
        return new Booking("Jim" + System.currentTimeMillis() % 1000, "Brown", 150, true,
                new BookingDates("2026-12-01", "2026-12-05"), "Breakfast");
    }
}