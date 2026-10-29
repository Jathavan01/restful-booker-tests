package db;

import models.Booking;
import models.BookingDates;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * A small staging table in an in-memory H2 database, accessed with plain JDBC.
 * In-memory means nothing to install, and the data disappears when the connection closes.
 */
public class BookingRepository implements AutoCloseable {

    private final Connection connection;

    public BookingRepository(String dbName) throws SQLException {
        connection = DriverManager.getConnection("jdbc:h2:mem:" + dbName);
        try (Statement statement = connection.createStatement()) {
            // Columns are deliberately nullable (like a staging table loaded from another system),
            // so the validation queries below have something real to check for.
            statement.execute("""
                    CREATE TABLE bookings (
                        id INT PRIMARY KEY,
                        firstname VARCHAR(100),
                        lastname VARCHAR(100),
                        totalprice INT,
                        depositpaid BOOLEAN,
                        checkin DATE,
                        checkout DATE,
                        additionalneeds VARCHAR(255)
                    )
                    """);
        }
    }

    // Load step: writes one booking into the table. PreparedStatement keeps values out of the SQL text.
    public void insert(int id, Booking booking) throws SQLException {
        String sql = "INSERT INTO bookings VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setString(2, booking.getFirstname());
            ps.setString(3, booking.getLastname());
            ps.setInt(4, booking.getTotalprice());
            ps.setBoolean(5, booking.isDepositpaid());
            ps.setDate(6, Date.valueOf(booking.getBookingdates().getCheckin()));
            ps.setDate(7, Date.valueOf(booking.getBookingdates().getCheckout()));
            ps.setString(8, booking.getAdditionalneeds());
            ps.executeUpdate();
        }
    }

    // Reads one row back as a Booking so it can be compared with the source field by field
    public Booking findById(int id) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement("SELECT * FROM bookings WHERE id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                return new Booking(
                        rs.getString("firstname"),
                        rs.getString("lastname"),
                        rs.getInt("totalprice"),
                        rs.getBoolean("depositpaid"),
                        new BookingDates(rs.getDate("checkin").toString(), rs.getDate("checkout").toString()),
                        rs.getString("additionalneeds"));
            }
        }
    }

    // Runs a query that returns one number, such as COUNT(*) or SUM(...)
    public long scalar(String sql) throws SQLException {
        try (Statement st = connection.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            rs.next();
            return rs.getLong(1);
        }
    }

    // Runs a query and returns how many rows came back (used for "should return nothing" checks)
    public int countRows(String sql) throws SQLException {
        try (Statement st = connection.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            int count = 0;
            while (rs.next()) {
                count++;
            }
            return count;
        }
    }

    @Override
    public void close() throws SQLException {
        connection.close();
    }
}