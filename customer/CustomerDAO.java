import database.DatabaseConnectivity;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Data access object for persisting customers.
 * TODO: Confirm table/column names with the actual schema.
 */
public class CustomerDAO {

    private static final String TABLE_NAME = "Customers"; // TODO adjust if the schema differs

    public CustomerProfile addCustomer(CustomerProfile customer) throws SQLException {
        String sql = "INSERT INTO " + TABLE_NAME +
                " (fullName, email, phone, address, dateOfBirth) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnectivity.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            fillStatement(ps, customer);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    customer.setId(rs.getInt(1));
                }
            }
            return customer;
        }
    }

    public void updateCustomer(CustomerProfile customer) throws SQLException {
        if (customer.getId() == null) {
            throw new IllegalArgumentException("Customer ID is required for update.");
        }

        String sql = "UPDATE " + TABLE_NAME + " SET " +
                "fullName=?, email=?, phone=?, address=?, dateOfBirth=? WHERE customerID=?";

        try (Connection conn = DatabaseConnectivity.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            fillStatement(ps, customer);
            ps.setInt(6, customer.getId());
            ps.executeUpdate();
        }
    }

    public CustomerProfile getCustomerById(int id) throws SQLException {
        String sql = "SELECT customerID, fullName, email, phone, address, dateOfBirth " +
                "FROM " + TABLE_NAME + " WHERE customerID=?";

        try (Connection conn = DatabaseConnectivity.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    public List<CustomerProfile> getAllCustomers() throws SQLException {
        String sql = "SELECT customerID, fullName, email, phone, address, dateOfBirth " +
                "FROM " + TABLE_NAME + " ORDER BY fullName";

        try (Connection conn = DatabaseConnectivity.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            List<CustomerProfile> list = new ArrayList<>();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
            return list;
        }
    }

    private void fillStatement(PreparedStatement ps, CustomerProfile c) throws SQLException {
        ps.setString(1, c.getFullName());
        ps.setString(2, c.getEmail());
        ps.setString(3, c.getPhone());
        ps.setString(4, c.getAddress());
        if (c.getDateOfBirth() != null) {
            ps.setDate(5, Date.valueOf(c.getDateOfBirth()));
        } else {
            ps.setNull(5, java.sql.Types.DATE);
        }
    }

    private CustomerProfile mapRow(ResultSet rs) throws SQLException {
        int id = rs.getInt("customerID");
        String fullName = rs.getString("fullName");
        String email = rs.getString("email");
        String phone = rs.getString("phone");
        String address = rs.getString("address");
        Date dob = rs.getDate("dateOfBirth");
        LocalDate dobLocal = dob != null ? dob.toLocalDate() : null;

        return new CustomerProfile(id, fullName, email, phone, address, dobLocal);
    }
}
