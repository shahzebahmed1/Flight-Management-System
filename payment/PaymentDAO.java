package payment;
import database.DatabaseConnectivity;
import java.sql.*;
public class PaymentDAO {

    /**
     * Total revenue for a given flight (summing successful payments).
     * We join Payments -> Bookings -> Flights using bookingID and flightID.
     */
    public double getTotalRevenueForFlight(int flightId) throws SQLException {
        String sql = "SELECT COALESCE(SUM(p.amount), 0) AS total " +
                     "FROM Payments p " +
                     "JOIN Bookings b ON p.bookingID = b.bookingID " +
                     "WHERE b.flightID = ? AND p.status = 'SUCCESS'";

        try (Connection conn = DatabaseConnectivity.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, flightId);

            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getDouble("total");
            }
        }
    }
}
