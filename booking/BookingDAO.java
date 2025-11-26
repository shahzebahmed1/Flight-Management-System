import database.DatabaseConnectivity;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookingDAO {
    
    public static class BookingRecord {
        private int bookingId;
        private int flightId;
        private String passengerName;
        private String passengerEmail;
        private Timestamp bookingTime;
        private String status;
        
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
        List<BookingRecord> bookings = new ArrayList<>();
        String sql = "SELECT * FROM Bookings ORDER BY bookingTime DESC";
        
        try (Connection conn = DatabaseConnectivity.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                bookings.add(new BookingRecord(
                    rs.getInt("bookingID"),
                    rs.getInt("flightID"),
                    rs.getString("passengerName"),
                    rs.getString("passengerEmail"),
                    rs.getTimestamp("bookingTime"),
                    rs.getString("status")
                ));
            }
        }
        return bookings;
    }
    
    public BookingRecord getBookingById(int bookingId) throws SQLException {
        String sql = "SELECT * FROM Bookings WHERE bookingID = ?";
        
        try (Connection conn = DatabaseConnectivity.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, bookingId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
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
        }
        return null;
    }
    
    public void updateBookingStatus(int bookingId, String status) throws SQLException {
        String sql = "UPDATE Bookings SET status = ? WHERE bookingID = ?";
        
        try (Connection conn = DatabaseConnectivity.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, status);
            ps.setInt(2, bookingId);
            ps.executeUpdate();
        }
    }
    
    public void updateBookingDetails(int bookingId, String passengerName, String passengerEmail) throws SQLException {
        String sql = "UPDATE Bookings SET passengerName = ?, passengerEmail = ? WHERE bookingID = ?";
        
        try (Connection conn = DatabaseConnectivity.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, passengerName);
            ps.setString(2, passengerEmail);
            ps.setInt(3, bookingId);
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
}

