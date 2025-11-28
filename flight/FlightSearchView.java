import database.DatabaseConnectivity;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
public class FlightSearchView {
    
    private JFrame frame;
    private JTextField originField;
    private JTextField destinationField;
    private JTextField dateField;
    private JComboBox<String> airlineComboBox;
    private JTable resultsTable;
    private DefaultTableModel tableModel;
    private FlightSearchDAO searchDAO;
    
    public FlightSearchView() {
        searchDAO = new FlightSearchDAO();
        initializeGUI();
    }
    
    private void initializeGUI() {
        frame = new JFrame("Flight Search & View");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 600);
        frame.setLocationRelativeTo(null);
        
        JPanel searchPanel = new JPanel(new GridBagLayout());
        searchPanel.setBorder(BorderFactory.createTitledBorder("Search Flights"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        searchPanel.add(new JLabel("Origin (e.g., YYC):"), gbc);
        gbc.gridx = 1;
        originField = new JTextField(10);
        searchPanel.add(originField, gbc);
        
        gbc.gridx = 2;
        gbc.gridy = 0;
        searchPanel.add(new JLabel("Destination (e.g., YVR):"), gbc);
        gbc.gridx = 3;
        destinationField = new JTextField(10);
        searchPanel.add(destinationField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        searchPanel.add(new JLabel("Date (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1;
        dateField = new JTextField(10);
        searchPanel.add(dateField, gbc);
        
        gbc.gridx = 2;
        gbc.gridy = 1;
        searchPanel.add(new JLabel("Airline:"), gbc);
        gbc.gridx = 3;
        airlineComboBox = new JComboBox<>();
        airlineComboBox.addItem("All Airlines");
        loadAirlines();
        searchPanel.add(airlineComboBox, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 4;
        JButton searchButton = new JButton("Search Flights");
        searchButton.addActionListener(e -> searchFlights());
        searchPanel.add(searchButton, gbc);
        
        String[] columnNames = {"Flight #", "Airline", "Origin", "Destination", 
                               "Departure", "Arrival", "Price", "Aircraft", 
                               "Total Seats", "Available Seats"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        resultsTable = new JTable(tableModel);
        resultsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        resultsTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        JScrollPane scrollPane = new JScrollPane(resultsTable);
        
        JPanel buttonPanel = new JPanel();
        JButton bookButton = new JButton("Book Selected Flight");
        bookButton.addActionListener(e -> bookSelectedFlight());
        buttonPanel.add(bookButton);
        
        frame.setLayout(new BorderLayout());
        frame.add(searchPanel, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.SOUTH);
        
        frame.setVisible(true);
    }
    
    private void loadAirlines() {
        try {
            List<String> airlines = searchDAO.getAllAirlineNames();
            for (String airline : airlines) {
                airlineComboBox.addItem(airline);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(frame, "Error loading airlines: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void searchFlights() {
        String origin = originField.getText().trim().toUpperCase();
        String destination = destinationField.getText().trim().toUpperCase();
        String date = dateField.getText().trim();
        String airline = (String) airlineComboBox.getSelectedItem();
        
        if (origin.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please enter origin airport code",
                    "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (destination.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please enter destination airport code",
                    "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (date.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please enter departure date (YYYY-MM-DD)",
                    "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            List<FlightSearchResult> results = searchDAO.searchFlights(origin, destination, date, airline);
            
            tableModel.setRowCount(0);
            
            if (results.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "No flights found matching your criteria",
                        "Search Results", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            for (FlightSearchResult result : results) {
                Object[] row = {
                    result.getFlightNumber(),
                    result.getAirlineName(),
                    result.getOrigin(),
                    result.getDestination(),
                    result.getDepartureTime(),
                    result.getArrivalTime(),
                    "$" + String.format("%.2f", result.getPrice()),
                    result.getAircraftModel(),
                    result.getTotalSeats(),
                    result.getAvailableSeats()
                };
                tableModel.addRow(row);
            }
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(frame, "Database error: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void bookSelectedFlight() {
        int selectedRow = resultsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(frame, "Please select a flight to book",
                    "Selection Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String flightNumber = (String) tableModel.getValueAt(selectedRow, 0);
        int availableSeats = Integer.parseInt(tableModel.getValueAt(selectedRow, 9).toString());
        
        if (availableSeats <= 0) {
            JOptionPane.showMessageDialog(frame, "This flight is fully booked",
                    "Booking Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        JOptionPane.showMessageDialog(frame, "Flight " + flightNumber + " selected for booking.\n" +
                "Available seats: " + availableSeats,
                "Booking", JOptionPane.INFORMATION_MESSAGE);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FlightSearchView());
    }
}

class FlightSearchDAO {
    
    public List<FlightSearchResult> searchFlights(String origin, String destination, 
                                                   String date, String airline) throws SQLException {
        List<FlightSearchResult> results = new ArrayList<>();
        
        String sql = "SELECT f.flightID, f.flightNumber, a.airlineName, f.origin, " +
                    "f.destination, f.departureTime, f.arrivalTime, f.price, " +
                    "ac.model, ac.capacity, " +
                    "(SELECT COUNT(*) FROM Bookings WHERE flightID = f.flightID) AS bookedSeats " +
                    "FROM Flights f " +
                    "JOIN Airlines a ON f.airlineID = a.airlineID " +
                    "JOIN Aircrafts ac ON f.aircraftID = ac.aircraftID " +
                    "WHERE f.origin = ? AND f.destination = ? AND DATE(f.departureTime) = ?";
        
        if (airline != null && !airline.equals("All Airlines")) {
            sql += " AND a.airlineName = ?";
        }
        
        sql += " ORDER BY f.departureTime";
        
        try (Connection conn = DatabaseConnectivity.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, origin);
            ps.setString(2, destination);
            ps.setString(3, date);
            
            if (airline != null && !airline.equals("All Airlines")) {
                ps.setString(4, airline);
            }
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    FlightSearchResult result = new FlightSearchResult(
                            rs.getInt("flightID"),
                            rs.getString("flightNumber"),
                            rs.getString("airlineName"),
                            rs.getString("origin"),
                            rs.getString("destination"),
                            rs.getTimestamp("departureTime"),
                            rs.getTimestamp("arrivalTime"),
                            rs.getDouble("price"),
                            rs.getString("model"),
                            rs.getInt("capacity"),
                            rs.getInt("bookedSeats")
                    );
                    results.add(result);
                }
            }
        }
        return results;
    }
    
    public List<String> getAllAirlineNames() throws SQLException {
        List<String> airlines = new ArrayList<>();
        String sql = "SELECT DISTINCT airlineName FROM Airlines ORDER BY airlineName";
        
        try (Connection conn = DatabaseConnectivity.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                airlines.add(rs.getString("airlineName"));
            }
        }
        return airlines;
    }
}

class FlightSearchResult {
    private int flightId;
    private String flightNumber;
    private String airlineName;
    private String origin;
    private String destination;
    private Timestamp departureTime;
    private Timestamp arrivalTime;
    private double price;
    private String aircraftModel;
    private int totalSeats;
    private int availableSeats;
    
    public FlightSearchResult(int flightId, String flightNumber, String airlineName,
                             String origin, String destination, Timestamp departureTime,
                             Timestamp arrivalTime, double price, String aircraftModel,
                             int totalSeats, int bookedSeats) {
        this.flightId = flightId;
        this.flightNumber = flightNumber;
        this.airlineName = airlineName;
        this.origin = origin;
        this.destination = destination;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.price = price;
        this.aircraftModel = aircraftModel;
        this.totalSeats = totalSeats;
        this.availableSeats = totalSeats - bookedSeats;
    }
    
    public int getFlightId() { return flightId; }
    public String getFlightNumber() { return flightNumber; }
    public String getAirlineName() { return airlineName; }
    public String getOrigin() { return origin; }
    public String getDestination() { return destination; }
    public Timestamp getDepartureTime() { return departureTime; }
    public Timestamp getArrivalTime() { return arrivalTime; }
    public double getPrice() { return price; }
    public String getAircraftModel() { return aircraftModel; }
    public int getTotalSeats() { return totalSeats; }
    public int getAvailableSeats() { return availableSeats; }
}