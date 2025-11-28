package payment;
import database.DatabaseConnectivity;
import java.sql.*;
import java.sql.Timestamp;

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

    /**
     * Records a payment in the database.
     * @param bookingId The booking ID
     * @param amount The payment amount
     * @param method The payment method
     * @param cardNumber The card number (will extract last 4 digits)
     * @return The created Payment object
     * @throws SQLException if database error occurs
     */
    public Payment recordPayment(int bookingId, double amount, PaymentMethod method, String cardNumber) throws SQLException {
        // Extract last 4 digits of card number
        String cardLast4 = cardNumber != null && cardNumber.length() >= 4 
            ? cardNumber.substring(cardNumber.length() - 4) 
            : null;

        // Determine payment status (simplified: always PAID for now)
        PaymentStatus status = PaymentStatus.PAID;
        String statusStr = "PAID";

        String sql = "INSERT INTO Payments (bookingID, amount, method, status, cardLast4) " +
                     "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnectivity.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, bookingId);
            ps.setDouble(2, amount);
            ps.setString(3, method.toString());
            ps.setString(4, statusStr);
            ps.setString(5, cardLast4);

            ps.executeUpdate();

            // Get the generated payment ID
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int paymentId = rs.getInt(1);

                    // Retrieve the full payment record with timestamp
                    String selectSql = "SELECT transactionTime FROM Payments WHERE paymentID = ?";
                    try (PreparedStatement selectPs = conn.prepareStatement(selectSql)) {
                        selectPs.setInt(1, paymentId);
                        try (ResultSet selectRs = selectPs.executeQuery()) {
                            if (selectRs.next()) {
                                Timestamp transactionTime = selectRs.getTimestamp("transactionTime");
                                return new Payment(paymentId, bookingId, amount, method, status, cardLast4, transactionTime);
                            }
                        }
                    }
                }
            }
        }

        throw new SQLException("Failed to create payment record");
    }
}
