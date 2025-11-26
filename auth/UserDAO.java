package auth;
import database.DatabaseConnectivity;
import java.sql.*;

public class UserDAO {
    
    public enum UserRole {
        ADMIN, EMPLOYEE
    }
    
    /**
     * Authenticates a user with username and password
     * @param username The username
     * @param password The password
     * @param role The expected role (ADMIN or EMPLOYEE)
     * @return true if authentication successful, false otherwise
     */
    public boolean authenticate(String username, String password, UserRole role) {
        String sql = "SELECT * FROM Users WHERE username = ? AND password = ? AND role = ?";
        
        try (Connection conn = DatabaseConnectivity.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, role.toString());
            
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next(); // Returns true if user found
            }
        } catch (SQLException e) {
            System.err.println("Authentication error: " + e.getMessage());
            return false;
        }
    }
}

