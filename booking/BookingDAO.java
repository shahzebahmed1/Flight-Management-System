import database.DatabaseConnectivity;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * JDBC DAO for CRUD operations on Bookings table.
 * The table is expected to be:
 *   Bookings(bookingID INT PK AI, flightID INT, passengerName VARCHAR, passengerEmail VARCHAR, bookingTime DATETIME, status VARCHAR)
 */
public class BookingDAO {

    public static class BookingRecord {
        private final int bookingId;
        private final int flightId;
        private final String passengerName;
        private final String passengerEmail;
        private final Timestamp bookingTime;
        private final String status;

        public BookingRecord(int bookingId, int flightId, String passengerName, 
                             String passengerEmail, Timestamp bookingTime, String status) {
            this.bookingId = bookingId;
            this.flightId = flightId;
            this.passengerName = passengerName;
            this.passengerEmail = passengerEmail;
            this.bookingTime = bookingTime;
            this.status = status;
        }

        public int getBookingId() { return bookingId; }
        public int getFlightId() { return flightId; }
        public String getPassengerName() { return passengerName; }
        public String getPassengerEmail() { return passengerEmail; }
        public Timestamp getBookingTime() { return bookingTime; }
        public String getStatus() { return status; }
    }

    public List<BookingRecord> getAllBookings() throws SQLException {
        String sql = "SELECT bookingID, flightID, passengerName, passengerEmail, bookingTime, status FROM Bookings";
        try (Connection conn = DatabaseConnectivity.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<BookingRecord> list = new ArrayList<>();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
            return list;
        }
    }

    public BookingRecord getBookingById(int bookingId) throws SQLException {
        String sql = "SELECT bookingID, flightID, passengerName, passengerEmail, bookingTime, status FROM Bookings WHERE bookingID=?";
        try (Connection conn = DatabaseConnectivity.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookingId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    public void updateBookingStatus(int bookingId, String status) throws SQLException {
        String sql = "UPDATE Bookings SET status=? WHERE bookingID=?";
        try (Connection conn = DatabaseConnectivity.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, bookingId);
            ps.executeUpdate();
        }
    }

    public void updateBookingDetails(int bookingId, int flightId, String passengerName, String passengerEmail) throws SQLException {
        String sql = "UPDATE Bookings SET flightID=?, passengerName=?, passengerEmail=? WHERE bookingID=?";
        try (Connection conn = DatabaseConnectivity.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, flightId);
            ps.setString(2, passengerName);
            ps.setString(3, passengerEmail);
            ps.setInt(4, bookingId);
            ps.executeUpdate();
        }
    }

    public void deleteBooking(int bookingId) throws SQLException {
        String sql = "DELETE FROM Bookings WHERE bookingID = ?";
        try (Connection conn = DatabaseConnectivity.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookingId);
            ps.executeUpdate();
        }
    }

    private BookingRecord mapRow(ResultSet rs) throws SQLException {
        return new BookingRecord(
                rs.getInt("bookingID"),
                rs.getInt("flightID"),
                rs.getString("passengerName"),
                rs.getString("passengerEmail"),
                rs.getTimestamp("bookingTime"),
                rs.getString("status")
        );
    }
}
