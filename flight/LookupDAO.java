import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import database.DatabaseConnectivity; // issue here

public class LookupDAO {

    public List<Airline> getAllAirlines() throws SQLException {
        String sql = "SELECT airlineID, airlineName, iataCode FROM Airlines ORDER BY airlineName";

        try (Connection conn = DatabaseConnectivity.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            List<Airline> list = new ArrayList<>();
            while (rs.next()) {
                list.add(new Airline(
                        rs.getInt("airlineID"),
                        rs.getString("airlineName"),
                        rs.getString("iataCode")
                ));
            }
            return list;
        }
    }

    public List<Aircraft> getAllAircrafts() throws SQLException {
        String sql = "SELECT aircraftID, model, capacity FROM Aircrafts ORDER BY model";

        try (Connection conn = DatabaseConnectivity.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            List<Aircraft> list = new ArrayList<>();
            while (rs.next()) {
                list.add(new Aircraft(
                        rs.getInt("aircraftID"),
                        rs.getString("model"),
                        rs.getInt("capacity")
                ));
            }
            return list;
        }
    }
}
