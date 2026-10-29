package tests;

import api.AuthClient;
import api.BookingClient;
import api.TestData;
import db.BookingRepository;
import models.Booking;
import models.BookingDates;
import models.CreateBookingResponse;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Source-to-target data validation.
 * Source = bookings created in and read back from the API.
 * Target = the same bookings loaded into a SQL table.
 * The tests use SQL to check the loaded data is complete, clean, and identical to the source.
 */
class BookingDataValidationTest {

    // A booking is invalid if it checks out before it checks in
    private static final String DATE_RULE_QUERY = "SELECT COUNT(*) FROM bookings WHERE checkout < checkin";

    private static String token;
    private static final Map<Integer, Booking> source = new LinkedHashMap<>();  // id -> booking as the API returned it
    private static BookingRepository target;

    // Extract and load: create 3 bookings, read each back from the API, and insert them into the table
    @BeforeAll
    static void extractAndLoad() throws SQLException {
        token = AuthClient.getToken();
        target = new BookingRepository("validation");

        for (int i = 1; i <= 3; i++) {
            Booking booking = TestData.newBooking();
            booking.setFirstname("Source" + i);
            booking.setLastname("Tester" + i);
            booking.setTotalprice(100 * i);

            int id = BookingClient.create(booking).as(CreateBookingResponse.class).getBookingid();
            Booking fromApi = BookingClient.get(id).then().statusCode(200).extract().as(Booking.class);

            source.put(id, fromApi);
            target.insert(id, fromApi);
        }
    }

    // Delete the test bookings from the shared API and close the database
    @AfterAll
    static void cleanUp() throws SQLException {
        for (int id : source.keySet()) {
            BookingClient.delete(id, token);
        }
        target.close();
    }

    @Test
    void rowCountMatchesSource() throws SQLException {
        assertEquals(source.size(), target.scalar("SELECT COUNT(*) FROM bookings"));
    }

    @Test
    void noNullsInRequiredColumns() throws SQLException {
        String sql = "SELECT COUNT(*) FROM bookings WHERE firstname IS NULL OR lastname IS NULL "
                + "OR totalprice IS NULL OR checkin IS NULL OR checkout IS NULL";
        assertEquals(0, target.scalar(sql));
    }

    @Test
    void noDuplicateBookings() throws SQLException {
        // The id is unique by design, so duplicates are checked on the business details instead
        String sql = "SELECT firstname, lastname, checkin, checkout FROM bookings "
                + "GROUP BY firstname, lastname, checkin, checkout HAVING COUNT(*) > 1";
        assertEquals(0, target.countRows(sql));
    }

    @Test
    void totalPriceMatchesSource() throws SQLException {
        long expected = source.values().stream().mapToLong(Booking::getTotalprice).sum();
        assertEquals(expected, target.scalar("SELECT SUM(totalprice) FROM bookings"));
    }

    @Test
    void everyRowMatchesSourceFieldByField() throws SQLException {
        for (Map.Entry<Integer, Booking> entry : source.entrySet()) {
            Booking expected = entry.getValue();
            Booking actual = target.findById(entry.getKey());

            assertNotNull(actual, "Row missing in target for id " + entry.getKey());
            assertEquals(expected.getFirstname(), actual.getFirstname());
            assertEquals(expected.getLastname(), actual.getLastname());
            assertEquals(expected.getTotalprice(), actual.getTotalprice());
            assertEquals(expected.isDepositpaid(), actual.isDepositpaid());
            assertEquals(expected.getBookingdates().getCheckin(), actual.getBookingdates().getCheckin());
            assertEquals(expected.getBookingdates().getCheckout(), actual.getBookingdates().getCheckout());
            assertEquals(expected.getAdditionalneeds(), actual.getAdditionalneeds());
        }
    }

    @Test
    void noCheckoutBeforeCheckin() throws SQLException {
        assertEquals(0, target.scalar(DATE_RULE_QUERY));
    }

    // Proves the date rule can actually fail: a bad row in a separate scratch database must be caught
    @Test
    void validationQueryCatchesBadData() throws SQLException {
        try (BookingRepository scratch = new BookingRepository("scratch")) {
            Booking bad = TestData.newBooking();
            bad.setBookingdates(new BookingDates("2026-12-10", "2026-12-01"));
            scratch.insert(1, bad);

            assertEquals(1, scratch.scalar(DATE_RULE_QUERY));
        }
    }
}