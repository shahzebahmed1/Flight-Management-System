import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import auth.UserDAO;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

public class CustomerGUI extends JFrame {
    
    // User role enum
    public enum UserRole {
        CUSTOMER, ADMIN, EMPLOYEE
    }
    
    private JPanel mainPanel;
    private BufferedImage backgroundImage;
    private CardLayout cardLayout;
    private JPanel cardPanel;
    private UserRole currentRole = UserRole.CUSTOMER;
    
    // Different view panels
    private JPanel customerHomePanel;
    private JPanel adminPanel;
    private JPanel employeePanel;
    
    // Admin components
    private FlightManagementDAO flightDAO;
    private LookupDAO lookupDAO;
    
    // Employee components
    private BookingDAO bookingDAO;
    
    public CustomerGUI() {
        super("Flight Management System");
        initializeGUI();
    }
    
    private void initializeGUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setResizable(true);
        
        // Load background image
        try {
            File imageFile = new File("flight/database/ChatGPT Image Nov 25, 2025, 04_16_08 PM.png");
            if (imageFile.exists()) {
                backgroundImage = ImageIO.read(imageFile);
            }
        } catch (Exception e) {
            System.out.println("Could not load background image: " + e.getMessage());
            // Continue without background image
        }
        
        // Create main panel with background
        mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage != null) {
                    // Draw background image scaled to fit
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                } else {
                    // Fallback gradient background
                    Graphics2D g2d = (Graphics2D) g;
                    GradientPaint gradient = new GradientPaint(
                        0, 0, new Color(25, 118, 210),
                        getWidth(), getHeight(), new Color(13, 71, 161)
                    );
                    g2d.setPaint(gradient);
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
        mainPanel.setLayout(new BorderLayout());
        
        // Create card layout for switching between views
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setOpaque(false);
        
        // Create different view panels
        customerHomePanel = createCustomerHomePanel();
        adminPanel = createAdminPanel();
        employeePanel = createEmployeePanel();
        
        // Add panels to card layout
        cardPanel.add(customerHomePanel, "CUSTOMER");
        cardPanel.add(adminPanel, "ADMIN");
        cardPanel.add(employeePanel, "EMPLOYEE");
        
        // Show customer view by default
        cardLayout.show(cardPanel, "CUSTOMER");
        
        mainPanel.add(cardPanel, BorderLayout.CENTER);
        add(mainPanel);
    }
    
    private JPanel createCustomerHomePanel() {
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);
        
        // Top navigation bar
        JPanel topBar = createTopBar();
        contentPanel.add(topBar, BorderLayout.NORTH);
        
        // Center content area
        JPanel centerPanel = createCenterPanel();
        contentPanel.add(centerPanel, BorderLayout.CENTER);
        
        return contentPanel;
    }
    
    private JPanel createAdminPanel() {
        JPanel adminPanel = new JPanel(new BorderLayout());
        adminPanel.setOpaque(false);
        adminPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Initialize DAOs
        flightDAO = new FlightManagementDAO();
        lookupDAO = new LookupDAO();
        
        // Top bar with logout
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);
        
        JLabel title = new JLabel("Admin Dashboard - Flight Management");
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        topBar.add(title, BorderLayout.WEST);
        
        JButton logoutBtn = new JButton("Logout");
        styleButton(logoutBtn, new Color(244, 67, 54), Color.WHITE);
        logoutBtn.addActionListener(e -> switchToView(UserRole.CUSTOMER));
        topBar.add(logoutBtn, BorderLayout.EAST);
        
        adminPanel.add(topBar, BorderLayout.NORTH);
        
        // Admin content with buttons
        JPanel adminContent = new JPanel(new GridBagLayout());
        adminContent.setOpaque(false);
        adminContent.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 30;
        gbc.ipady = 15;
        
        // List Flights Button
        JButton listFlightsBtn = createAdminButton("📋 List All Flights", 
            "View all flights in the system");
        listFlightsBtn.addActionListener(e -> showAllFlights());
        gbc.gridx = 0; gbc.gridy = 0;
        adminContent.add(listFlightsBtn, gbc);
        
        // Add Flight Button
        JButton addFlightBtn = createAdminButton("➕ Add New Flight", 
            "Add a new flight to the system");
        addFlightBtn.addActionListener(e -> showAddFlightDialog());
        gbc.gridx = 1;
        adminContent.add(addFlightBtn, gbc);
        
        // Update Flight Button
        JButton updateFlightBtn = createAdminButton("✏️ Update Flight", 
            "Modify existing flight details");
        updateFlightBtn.addActionListener(e -> showUpdateFlightDialog());
        gbc.gridx = 0; gbc.gridy = 1;
        adminContent.add(updateFlightBtn, gbc);
        
        // Delete Flight Button
        JButton deleteFlightBtn = createAdminButton("🗑️ Delete Flight", 
            "Remove a flight from the system");
        deleteFlightBtn.addActionListener(e -> showDeleteFlightDialog());
        gbc.gridx = 1;
        adminContent.add(deleteFlightBtn, gbc);
        
        adminPanel.add(adminContent, BorderLayout.CENTER);
        
        return adminPanel;
    }
    
    private JButton createAdminButton(String title, String description) {
        JButton button = new JButton("<html><center><b>" + title + "</b><br>" + 
                                     "<small>" + description + "</small></center></html>");
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        styleButton(button, new Color(33, 150, 243), Color.WHITE);
        button.setPreferredSize(new Dimension(250, 100));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }
    
    private void showAllFlights() {
        try {
            List<FlightRecord> flights = flightDAO.getAllFlights();
            
            if (flights.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "No flights found in the system.", 
                    "Flights", 
                    JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            // Create a table to display flights (without aircraft column)
            String[] columnNames = {"ID", "Flight #", "Airline", "Origin", "Destination", 
                                   "Departure", "Arrival", "Price"};
            DefaultTableModel model = new DefaultTableModel(columnNames, 0);
            
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            
            for (FlightRecord f : flights) {
                Object[] row = {
                    f.getFlightId(),
                    f.getFlightNumber(),
                    f.getAirlineName(),
                    f.getOrigin(),
                    f.getDestination(),
                    dateFormat.format(f.getDepartureTime()),
                    dateFormat.format(f.getArrivalTime()),
                    String.format("$%.2f", f.getPrice())
                };
                model.addRow(row);
            }
            
            JTable table = new JTable(model);
            table.setFillsViewportHeight(true);
            table.setRowHeight(25);
            JScrollPane scrollPane = new JScrollPane(table);
            scrollPane.setPreferredSize(new Dimension(1000, 400));
            
            JOptionPane.showMessageDialog(this, scrollPane, "All Flights", 
                JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Database error: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showAddFlightDialog() {
        JDialog dialog = new JDialog(this, "Add New Flight", true);
        dialog.setSize(500, 380);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Flight Number
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Flight Number:"), gbc);
        JTextField flightNumberField = new JTextField(20);
        gbc.gridx = 1;
        formPanel.add(flightNumberField, gbc);
        
        // Origin
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Origin (3-letter code):"), gbc);
        JTextField originField = new JTextField(20);
        gbc.gridx = 1;
        formPanel.add(originField, gbc);
        
        // Destination
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Destination (3-letter code):"), gbc);
        JTextField destField = new JTextField(20);
        gbc.gridx = 1;
        formPanel.add(destField, gbc);
        
        // Departure Date/Time
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Departure (YYYY-MM-DD HH:MM):"), gbc);
        JTextField depField = new JTextField(20);
        depField.setText("2025-12-01 08:00");
        gbc.gridx = 1;
        formPanel.add(depField, gbc);
        
        // Arrival Date/Time
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Arrival (YYYY-MM-DD HH:MM):"), gbc);
        JTextField arrField = new JTextField(20);
        arrField.setText("2025-12-01 10:00");
        gbc.gridx = 1;
        formPanel.add(arrField, gbc);
        
        // Price
        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(new JLabel("Price:"), gbc);
        JTextField priceField = new JTextField(20);
        gbc.gridx = 1;
        formPanel.add(priceField, gbc);
        
        dialog.add(formPanel, BorderLayout.CENTER);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton addBtn = new JButton("Add Flight");
        JButton cancelBtn = new JButton("Cancel");
        
        addBtn.addActionListener(e -> {
            // Validate Flight Number
            String flightNumber = flightNumberField.getText().trim();
            if (flightNumber.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, 
                    "Flight number is required.", 
                    "Validation Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Validate Origin
            String origin = originField.getText().trim().toUpperCase();
            if (origin.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, 
                    "Origin is required.", 
                    "Validation Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (origin.length() != 3 || !origin.matches("[A-Z]{3}")) {
                JOptionPane.showMessageDialog(dialog, 
                    "Origin must be exactly 3 letters (e.g., YYZ, LAX, JFK).", 
                    "Validation Error", 
                    JOptionPane.ERROR_MESSAGE);
                originField.requestFocus();
                return;
            }
            
            // Validate Destination
            String destination = destField.getText().trim().toUpperCase();
            if (destination.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, 
                    "Destination is required.", 
                    "Validation Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (destination.length() != 3 || !destination.matches("[A-Z]{3}")) {
                JOptionPane.showMessageDialog(dialog, 
                    "Destination must be exactly 3 letters (e.g., YYZ, LAX, JFK).", 
                    "Validation Error", 
                    JOptionPane.ERROR_MESSAGE);
                destField.requestFocus();
                return;
            }
            
            // Validate Origin and Destination are different
            if (origin.equals(destination)) {
                JOptionPane.showMessageDialog(dialog, 
                    "Origin and destination cannot be the same.", 
                    "Validation Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Validate Departure Date/Time
            String departureStr = depField.getText().trim();
            if (departureStr.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, 
                    "Departure time is required.", 
                    "Validation Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            Timestamp departure;
            try {
                departure = Timestamp.valueOf(departureStr + ":00");
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(dialog, 
                    "Invalid departure time format. Use YYYY-MM-DD HH:MM (e.g., 2025-12-01 08:00).", 
                    "Validation Error", 
                    JOptionPane.ERROR_MESSAGE);
                depField.requestFocus();
                return;
            }
            
            // Validate Arrival Date/Time
            String arrivalStr = arrField.getText().trim();
            if (arrivalStr.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, 
                    "Arrival time is required.", 
                    "Validation Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            Timestamp arrival;
            try {
                arrival = Timestamp.valueOf(arrivalStr + ":00");
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(dialog, 
                    "Invalid arrival time format. Use YYYY-MM-DD HH:MM (e.g., 2025-12-01 10:00).", 
                    "Validation Error", 
                    JOptionPane.ERROR_MESSAGE);
                arrField.requestFocus();
                return;
            }
            
            // Validate Arrival is after Departure
            if (arrival.before(departure) || arrival.equals(departure)) {
                JOptionPane.showMessageDialog(dialog, 
                    "Arrival time must be after departure time.", 
                    "Validation Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Validate Price
            String priceStr = priceField.getText().trim();
            if (priceStr.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, 
                    "Price is required.", 
                    "Validation Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            double price;
            try {
                price = Double.parseDouble(priceStr);
                if (price <= 0) {
                    JOptionPane.showMessageDialog(dialog, 
                        "Price must be greater than 0.", 
                        "Validation Error", 
                        JOptionPane.ERROR_MESSAGE);
                    priceField.requestFocus();
                    return;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, 
                    "Price must be a valid number.", 
                    "Validation Error", 
                    JOptionPane.ERROR_MESSAGE);
                priceField.requestFocus();
                return;
            }
            
            // All validations passed - create flight
            try {
                FlightRecord newFlight = new FlightRecord(
                    0,
                    flightNumber,
                    8,  // Air Canada ID
                    "Air Canada",
                    origin,
                    destination,
                    departure,
                    arrival,
                    price,
                    1,  // Default aircraft ID (Boeing 737-800)
                    "Boeing 737-800"
                );
                
                flightDAO.insertFlight(newFlight);
                JOptionPane.showMessageDialog(dialog, "Flight added successfully!", 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, 
                    "Database error: " + ex.getMessage(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        
        cancelBtn.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(addBtn);
        buttonPanel.add(cancelBtn);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.setVisible(true);
    }
    
    private void showUpdateFlightDialog() {
        try {
            List<FlightRecord> flights = flightDAO.getAllFlights();
            
            if (flights.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "No flights found to update.", 
                    "Update Flight", 
                    JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            // Show flights and let user select
            String[] flightOptions = new String[flights.size()];
            for (int i = 0; i < flights.size(); i++) {
                FlightRecord f = flights.get(i);
                flightOptions[i] = String.format("ID: %d - %s (%s to %s)", 
                    f.getFlightId(), f.getFlightNumber(), f.getOrigin(), f.getDestination());
            }
            
            String selected = (String) JOptionPane.showInputDialog(this,
                "Select flight to update:",
                "Update Flight",
                JOptionPane.QUESTION_MESSAGE,
                null,
                flightOptions,
                flightOptions[0]);
            
            if (selected == null) return;
            
            // Extract flight ID
            int flightId = Integer.parseInt(selected.substring(4, selected.indexOf(" -")));
            FlightRecord existing = null;
            for (FlightRecord f : flights) {
                if (f.getFlightId() == flightId) {
                    existing = f;
                    break;
                }
            }
            
            if (existing == null) return;
            
            final FlightRecord finalExisting = existing;
            
            // Show update dialog
            JDialog dialog = new JDialog(this, "Update Flight", true);
            dialog.setSize(500, 300);
            dialog.setLocationRelativeTo(this);
            dialog.setLayout(new BorderLayout(10, 10));
            
            JPanel formPanel = new JPanel(new GridBagLayout());
            formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 10, 10, 10);
            gbc.anchor = GridBagConstraints.WEST;
            
            gbc.gridx = 0; gbc.gridy = 0;
            formPanel.add(new JLabel("Flight Number:"), gbc);
            JTextField flightNumberField = new JTextField(finalExisting.getFlightNumber(), 20);
            gbc.gridx = 1;
            formPanel.add(flightNumberField, gbc);
            
            dialog.add(formPanel, BorderLayout.CENTER);
            
            JPanel buttonPanel = new JPanel(new FlowLayout());
            JButton updateBtn = new JButton("Update");
            JButton cancelBtn = new JButton("Cancel");
            
            updateBtn.addActionListener(e -> {
                try {
                    FlightRecord updated = new FlightRecord(
                        finalExisting.getFlightId(),
                        flightNumberField.getText().trim(),
                        finalExisting.getAirlineId(),
                        finalExisting.getAirlineName(),
                        finalExisting.getOrigin(),
                        finalExisting.getDestination(),
                        finalExisting.getDepartureTime(),
                        finalExisting.getArrivalTime(),
                        finalExisting.getPrice(),
                        finalExisting.getAircraftId(),
                        finalExisting.getAircraftModel()
                    );
                    flightDAO.updateFlight(updated);
                    JOptionPane.showMessageDialog(dialog, "Flight updated successfully!", 
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(dialog, 
                        "Database error: " + ex.getMessage(), 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                }
            });
            
            cancelBtn.addActionListener(e -> dialog.dispose());
            buttonPanel.add(updateBtn);
            buttonPanel.add(cancelBtn);
            dialog.add(buttonPanel, BorderLayout.SOUTH);
            dialog.setVisible(true);
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Database error: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showDeleteFlightDialog() {
        try {
            List<FlightRecord> flights = flightDAO.getAllFlights();
            
            if (flights.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "No flights found to delete.", 
                    "Delete Flight", 
                    JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            String[] flightOptions = new String[flights.size()];
            for (int i = 0; i < flights.size(); i++) {
                FlightRecord f = flights.get(i);
                flightOptions[i] = String.format("ID: %d - %s (%s to %s)", 
                    f.getFlightId(), f.getFlightNumber(), f.getOrigin(), f.getDestination());
            }
            
            String selected = (String) JOptionPane.showInputDialog(this,
                "Select flight to delete:",
                "Delete Flight",
                JOptionPane.QUESTION_MESSAGE,
                null,
                flightOptions,
                flightOptions[0]);
            
            if (selected == null) return;
            
            int flightId = Integer.parseInt(selected.substring(4, selected.indexOf(" -")));
            FlightRecord toDelete = null;
            for (FlightRecord f : flights) {
                if (f.getFlightId() == flightId) {
                    toDelete = f;
                    break;
                }
            }
            
            if (toDelete == null) return;
            
            int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete flight " + toDelete.getFlightNumber() + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
            
            if (confirm == JOptionPane.YES_OPTION) {
                flightDAO.deleteFlight(flightId);
                JOptionPane.showMessageDialog(this, 
                    "Flight deleted successfully!", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Database error: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private JPanel createEmployeePanel() {
        JPanel employeePanel = new JPanel(new BorderLayout());
        employeePanel.setOpaque(false);
        employeePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Initialize DAOs
        bookingDAO = new BookingDAO();
        flightDAO = new FlightManagementDAO();
        
        // Top bar with logout
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);
        
        JLabel title = new JLabel("Employee Dashboard - Customer & Booking Management");
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        topBar.add(title, BorderLayout.WEST);
        
        JButton logoutBtn = new JButton("Logout");
        styleButton(logoutBtn, new Color(244, 67, 54), Color.WHITE);
        logoutBtn.addActionListener(e -> switchToView(UserRole.CUSTOMER));
        topBar.add(logoutBtn, BorderLayout.EAST);
        
        employeePanel.add(topBar, BorderLayout.NORTH);
        
        // Employee content with buttons
        JPanel employeeContent = new JPanel(new GridBagLayout());
        employeeContent.setOpaque(false);
        employeeContent.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 30;
        gbc.ipady = 15;
        
        // View All Bookings Button
        JButton viewBookingsBtn = createEmployeeButton("📋 View All Bookings", 
            "View and manage all reservations");
        viewBookingsBtn.addActionListener(e -> showAllBookings());
        gbc.gridx = 0; gbc.gridy = 0;
        employeeContent.add(viewBookingsBtn, gbc);
        
        // Modify Reservation Button
        JButton modifyBookingBtn = createEmployeeButton("✏️ Modify Reservation", 
            "Update booking details and status");
        modifyBookingBtn.addActionListener(e -> showModifyBookingDialog());
        gbc.gridx = 1;
        employeeContent.add(modifyBookingBtn, gbc);
        
        // View Flight Schedule Button
        JButton viewScheduleBtn = createEmployeeButton("📅 View Flight Schedule", 
            "View all available flights");
        viewScheduleBtn.addActionListener(e -> showAllFlights());
        gbc.gridx = 0; gbc.gridy = 1;
        employeeContent.add(viewScheduleBtn, gbc);
        
        // Manage Customer Data Button
        JButton manageCustomersBtn = createEmployeeButton("👥 Manage Customer Data", 
            "View and edit customer information");
        manageCustomersBtn.addActionListener(e -> showManageCustomersDialog());
        gbc.gridx = 1;
        employeeContent.add(manageCustomersBtn, gbc);
        
        employeePanel.add(employeeContent, BorderLayout.CENTER);
        
        return employeePanel;
    }
    
    private JButton createEmployeeButton(String title, String description) {
        JButton button = new JButton("<html><center><b>" + title + "</b><br>" + 
                                     "<small>" + description + "</small></center></html>");
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        styleButton(button, new Color(76, 175, 80), Color.WHITE);
        button.setPreferredSize(new Dimension(250, 100));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }
    
    private void showAllBookings() {
        try {
            List<BookingDAO.BookingRecord> bookings = bookingDAO.getAllBookings();
            
            if (bookings.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "No bookings found.", 
                    "Bookings", 
                    JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            // Create a table to display bookings
            String[] columnNames = {"Booking ID", "Flight ID", "Passenger Name", "Email", 
                                   "Booking Time", "Status"};
            DefaultTableModel model = new DefaultTableModel(columnNames, 0);
            
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            
            for (BookingDAO.BookingRecord b : bookings) {
                Object[] row = {
                    b.getBookingId(),
                    b.getFlightId(),
                    b.getPassengerName(),
                    b.getPassengerEmail(),
                    dateFormat.format(b.getBookingTime()),
                    b.getStatus()
                };
                model.addRow(row);
            }
            
            JTable table = new JTable(model);
            table.setFillsViewportHeight(true);
            table.setRowHeight(25);
            JScrollPane scrollPane = new JScrollPane(table);
            scrollPane.setPreferredSize(new Dimension(900, 400));
            
            JOptionPane.showMessageDialog(this, scrollPane, "All Bookings", 
                JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Database error: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showModifyBookingDialog() {
        try {
            List<BookingDAO.BookingRecord> bookings = bookingDAO.getAllBookings();
            
            if (bookings.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "No bookings found to modify.", 
                    "Modify Booking", 
                    JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            // Show bookings and let user select
            String[] bookingOptions = new String[bookings.size()];
            for (int i = 0; i < bookings.size(); i++) {
                BookingDAO.BookingRecord b = bookings.get(i);
                bookingOptions[i] = String.format("ID: %d - %s (%s) - %s", 
                    b.getBookingId(), b.getPassengerName(), b.getPassengerEmail(), b.getStatus());
            }
            
            String selected = (String) JOptionPane.showInputDialog(this,
                "Select booking to modify:",
                "Modify Booking",
                JOptionPane.QUESTION_MESSAGE,
                null,
                bookingOptions,
                bookingOptions[0]);
            
            if (selected == null) return;
            
            // Extract booking ID
            int bookingId = Integer.parseInt(selected.substring(4, selected.indexOf(" -")));
            BookingDAO.BookingRecord existing = null;
            for (BookingDAO.BookingRecord b : bookings) {
                if (b.getBookingId() == bookingId) {
                    existing = b;
                    break;
                }
            }
            
            if (existing == null) return;
            
            // Show modify dialog
            JDialog dialog = new JDialog(this, "Modify Booking", true);
            dialog.setSize(500, 350);
            dialog.setLocationRelativeTo(this);
            dialog.setLayout(new BorderLayout(10, 10));
            
            JPanel formPanel = new JPanel(new GridBagLayout());
            formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 10, 10, 10);
            gbc.anchor = GridBagConstraints.WEST;
            
            // Passenger Name
            gbc.gridx = 0; gbc.gridy = 0;
            formPanel.add(new JLabel("Passenger Name:"), gbc);
            JTextField nameField = new JTextField(existing.getPassengerName(), 20);
            gbc.gridx = 1;
            formPanel.add(nameField, gbc);
            
            // Passenger Email
            gbc.gridx = 0; gbc.gridy = 1;
            formPanel.add(new JLabel("Passenger Email:"), gbc);
            JTextField emailField = new JTextField(existing.getPassengerEmail(), 20);
            gbc.gridx = 1;
            formPanel.add(emailField, gbc);
            
            // Status
            gbc.gridx = 0; gbc.gridy = 2;
            formPanel.add(new JLabel("Status:"), gbc);
            JComboBox<String> statusCombo = new JComboBox<>(new String[]{"CONFIRMED", "CANCELLED", "MODIFIED"});
            statusCombo.setSelectedItem(existing.getStatus());
            gbc.gridx = 1;
            formPanel.add(statusCombo, gbc);
            
            dialog.add(formPanel, BorderLayout.CENTER);
            
            JPanel buttonPanel = new JPanel(new FlowLayout());
            JButton updateBtn = new JButton("Update");
            JButton cancelBtn = new JButton("Cancel");
            
            final BookingDAO.BookingRecord finalExisting = existing;
            updateBtn.addActionListener(e -> {
                try {
                    String name = nameField.getText().trim();
                    String email = emailField.getText().trim();
                    String status = (String) statusCombo.getSelectedItem();
                    
                    if (name.isEmpty() || email.isEmpty()) {
                        JOptionPane.showMessageDialog(dialog, 
                            "Name and email are required.", 
                            "Validation Error", 
                            JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    
                    if (!email.contains("@")) {
                        JOptionPane.showMessageDialog(dialog, 
                            "Please enter a valid email address.", 
                            "Validation Error", 
                            JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    
                    bookingDAO.updateBookingDetails(finalExisting.getBookingId(), finalExisting.getFlightId(), name, email);
                    bookingDAO.updateBookingStatus(finalExisting.getBookingId(), status);
                    
                    JOptionPane.showMessageDialog(dialog, "Booking updated successfully!", 
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(dialog, 
                        "Database error: " + ex.getMessage(), 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                }
            });
            
            cancelBtn.addActionListener(e -> dialog.dispose());
            buttonPanel.add(updateBtn);
            buttonPanel.add(cancelBtn);
            dialog.add(buttonPanel, BorderLayout.SOUTH);
            dialog.setVisible(true);
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Database error: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showManageCustomersDialog() {
        try {
            List<BookingDAO.BookingRecord> bookings = bookingDAO.getAllBookings();
            
            if (bookings.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "No customer data found.", 
                    "Manage Customers", 
                    JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            // Create a table with customer data
            String[] columnNames = {"Booking ID", "Customer Name", "Email", "Flight ID", "Status"};
            DefaultTableModel model = new DefaultTableModel(columnNames, 0);
            
            for (BookingDAO.BookingRecord b : bookings) {
                Object[] row = {
                    b.getBookingId(),
                    b.getPassengerName(),
                    b.getPassengerEmail(),
                    b.getFlightId(),
                    b.getStatus()
                };
                model.addRow(row);
            }
            
            JTable table = new JTable(model);
            table.setFillsViewportHeight(true);
            table.setRowHeight(25);
            JScrollPane scrollPane = new JScrollPane(table);
            scrollPane.setPreferredSize(new Dimension(800, 400));
            
            JOptionPane.showMessageDialog(this, scrollPane, "Customer Data", 
                JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Database error: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // Customer features
    private void showFlightSearchDialog() {
        JDialog dialog = new JDialog(this, "Search Flights", true);
        dialog.setSize(500, 350);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Origin (optional)
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Origin (3-letter code, optional):"), gbc);
        JTextField originField = new JTextField(20);
        gbc.gridx = 1;
        formPanel.add(originField, gbc);
        
        // Destination (optional)
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Destination (3-letter code, optional):"), gbc);
        JTextField destField = new JTextField(20);
        gbc.gridx = 1;
        formPanel.add(destField, gbc);
        
        // Date (optional)
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Date (YYYY-MM-DD, optional):"), gbc);
        JTextField dateField = new JTextField(20);
        gbc.gridx = 1;
        formPanel.add(dateField, gbc);
        
        // Airline (optional)
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Airline (optional):"), gbc);
        JTextField airlineField = new JTextField(20);
        gbc.gridx = 1;
        formPanel.add(airlineField, gbc);
        
        dialog.add(formPanel, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton searchBtn = new JButton("Search");
        JButton cancelBtn = new JButton("Cancel");
        
        searchBtn.addActionListener(e -> {
            String origin = originField.getText().trim().toUpperCase();
            String destination = destField.getText().trim().toUpperCase();
            String date = dateField.getText().trim();
            String airline = airlineField.getText().trim();
            
            // Validate format if provided, but all fields are optional
            if (!origin.isEmpty() && (origin.length() != 3 || !origin.matches("[A-Z]{3}"))) {
                JOptionPane.showMessageDialog(dialog, 
                    "Origin must be exactly 3 letters if provided.", 
                    "Validation Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (!destination.isEmpty() && (destination.length() != 3 || !destination.matches("[A-Z]{3}"))) {
                JOptionPane.showMessageDialog(dialog, 
                    "Destination must be exactly 3 letters if provided.", 
                    "Validation Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (!origin.isEmpty() && !destination.isEmpty() && origin.equals(destination)) {
                JOptionPane.showMessageDialog(dialog, 
                    "Origin and destination cannot be the same.", 
                    "Validation Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            dialog.dispose();
            searchFlights(origin, destination, date, airline);
        });
        
        cancelBtn.addActionListener(e -> dialog.dispose());
        buttonPanel.add(searchBtn);
        buttonPanel.add(cancelBtn);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
    
    private void searchFlights(String origin, String destination, String date, String airline) {
        try {
            List<FlightRecord> allFlights = flightDAO.getAllFlights();
            List<FlightRecord> matchingFlights = new ArrayList<>();
            
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            
            for (FlightRecord f : allFlights) {
                boolean matches = true;
                
                // Filter by origin if provided
                if (!origin.isEmpty() && !f.getOrigin().equals(origin)) {
                    matches = false;
                }
                
                // Filter by destination if provided
                if (!destination.isEmpty() && !f.getDestination().equals(destination)) {
                    matches = false;
                }
                
                // Filter by date if provided
                if (!date.isEmpty()) {
                    String flightDate = dateFormat.format(f.getDepartureTime());
                    if (!flightDate.equals(date)) {
                        matches = false;
                    }
                }
                
                // Filter by airline if provided
                if (!airline.isEmpty() && !f.getAirlineName().toLowerCase().contains(airline.toLowerCase())) {
                    matches = false;
                }
                
                if (matches) {
                    matchingFlights.add(f);
                }
            }
            
            if (matchingFlights.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "No flights found for " + origin + " to " + destination + 
                    (date.isEmpty() ? "" : " on " + date) + ".", 
                    "Search Results", 
                    JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            // Display results with booking option
            String[] columnNames = {"ID", "Flight #", "Airline", "Origin", "Destination", 
                                   "Departure", "Arrival", "Price", "Action"};
            DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return column == 8; // Only action column is editable
                }
            };
            
            SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            
            for (FlightRecord f : matchingFlights) {
                Object[] row = {
                    f.getFlightId(),
                    f.getFlightNumber(),
                    f.getAirlineName(),
                    f.getOrigin(),
                    f.getDestination(),
                    dateTimeFormat.format(f.getDepartureTime()),
                    dateTimeFormat.format(f.getArrivalTime()),
                    String.format("$%.2f", f.getPrice()),
                    "Book"
                };
                model.addRow(row);
            }
            
            JTable table = new JTable(model);
            table.setRowHeight(25);
            table.getColumn("Action").setCellRenderer(new ButtonRenderer());
            table.getColumn("Action").setCellEditor(new ButtonEditor(new JCheckBox(), matchingFlights));
            
            JScrollPane scrollPane = new JScrollPane(table);
            scrollPane.setPreferredSize(new Dimension(1000, 400));
            
            String title = "Search Results";
            if (!origin.isEmpty() || !destination.isEmpty()) {
                title += ": " + (origin.isEmpty() ? "Any" : origin) + " to " + (destination.isEmpty() ? "Any" : destination);
            }
            JOptionPane.showMessageDialog(this, scrollPane, title, 
                JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Database error: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showCustomerBookings() {
        JDialog emailDialog = new JDialog(this, "View My Bookings", true);
        emailDialog.setSize(550, 180);
        emailDialog.setLocationRelativeTo(this);
        emailDialog.setLayout(new BorderLayout(10, 10));
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.anchor = GridBagConstraints.WEST;
        
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel emailLabel = new JLabel("Enter your email:");
        emailLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(emailLabel, gbc);
        
        JTextField emailField = new JTextField(35);
        emailField.setFont(new Font("Arial", Font.PLAIN, 14));
        emailField.setPreferredSize(new Dimension(350, 30));
        emailField.setMinimumSize(new Dimension(350, 30));
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        formPanel.add(emailField, gbc);
        
        emailDialog.add(formPanel, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton viewBtn = new JButton("View Bookings");
        JButton cancelBtn = new JButton("Cancel");
        
        viewBtn.addActionListener(e -> {
            String email = emailField.getText().trim();
            if (email.isEmpty() || !email.contains("@")) {
                JOptionPane.showMessageDialog(emailDialog, 
                    "Please enter a valid email address.", 
                    "Validation Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            emailDialog.dispose();
            displayCustomerBookings(email);
        });
        
        cancelBtn.addActionListener(e -> emailDialog.dispose());
        buttonPanel.add(viewBtn);
        buttonPanel.add(cancelBtn);
        emailDialog.add(buttonPanel, BorderLayout.SOUTH);
        emailDialog.setVisible(true);
    }
    
    private void displayCustomerBookings(String email) {
        try {
            List<BookingDAO.BookingRecord> allBookings = bookingDAO.getAllBookings();
            List<BookingDAO.BookingRecord> customerBookings = new ArrayList<>();
            
            for (BookingDAO.BookingRecord b : allBookings) {
                if (b.getPassengerEmail().equalsIgnoreCase(email)) {
                    customerBookings.add(b);
                }
            }
            
            if (customerBookings.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "No bookings found for " + email + ".", 
                    "My Bookings", 
                    JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            // Create table with action buttons
            String[] columnNames = {"Booking ID", "Flight ID", "Passenger Name", 
                                   "Booking Time", "Status", "Action"};
            DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return column == 5; // Only action column
                }
            };
            
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            
            for (BookingDAO.BookingRecord b : customerBookings) {
                Object[] row = {
                    b.getBookingId(),
                    b.getFlightId(),
                    b.getPassengerName(),
                    dateFormat.format(b.getBookingTime()),
                    b.getStatus(),
                    "Manage"
                };
                model.addRow(row);
            }
            
            JTable table = new JTable(model);
            table.setRowHeight(25);
            table.getColumn("Action").setCellRenderer(new ButtonRenderer());
            table.getColumn("Action").setCellEditor(new BookingButtonEditor(new JCheckBox(), customerBookings));
            
            JScrollPane scrollPane = new JScrollPane(table);
            scrollPane.setPreferredSize(new Dimension(900, 400));
            
            JOptionPane.showMessageDialog(this, scrollPane, "My Bookings", 
                JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Database error: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // Helper classes for table buttons
    class ButtonRenderer extends JButton implements javax.swing.table.TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }
        
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "" : value.toString());
            return this;
        }
    }
    
    class ButtonEditor extends DefaultCellEditor {
        protected JButton button;
        private String label;
        private boolean isPushed;
        private List<FlightRecord> flights;
        private int currentRow;
        
        public ButtonEditor(JCheckBox checkBox, List<FlightRecord> flights) {
            super(checkBox);
            this.flights = flights;
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> {
                if (isPushed) {
                    if (currentRow >= 0 && currentRow < flights.size()) {
                        FlightRecord flight = flights.get(currentRow);
                        showBookingDialog(flight);
                    }
                }
                fireEditingStopped();
            });
        }
        
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            label = (value == null) ? "" : value.toString();
            button.setText(label);
            isPushed = true;
            currentRow = row;
            return button;
        }
        
        public Object getCellEditorValue() {
            isPushed = false;
            return label;
        }
        
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }
    }
    
    class BookingButtonEditor extends DefaultCellEditor {
        protected JButton button;
        private String label;
        private boolean isPushed;
        private List<BookingDAO.BookingRecord> bookings;
        private int currentRow;
        
        public BookingButtonEditor(JCheckBox checkBox, List<BookingDAO.BookingRecord> bookings) {
            super(checkBox);
            this.bookings = bookings;
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> {
                if (isPushed) {
                    if (currentRow >= 0 && currentRow < bookings.size()) {
                        BookingDAO.BookingRecord booking = bookings.get(currentRow);
                        showManageCustomerBookingDialog(booking);
                    }
                }
                fireEditingStopped();
            });
        }
        
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            label = (value == null) ? "" : value.toString();
            button.setText(label);
            isPushed = true;
            currentRow = row;
            return button;
        }
        
        public Object getCellEditorValue() {
            isPushed = false;
            return label;
        }
        
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }
    }
    
    private void showBookingDialog(FlightRecord flight) {
        JDialog dialog = new JDialog(this, "Book Flight", true);
        dialog.setSize(650, 750);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        
        // Top panel with flight info and passenger details
        JPanel topPanel = new JPanel(new GridBagLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Display flight info and price
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        topPanel.add(new JLabel("<html><b>Flight: " + flight.getFlightNumber() + 
            " (" + flight.getOrigin() + " to " + flight.getDestination() + ")</b><br>" +
            "Price: <b>$" + String.format("%.2f", flight.getPrice()) + "</b></html>"), gbc);
        gbc.gridwidth = 1;
        
        // Passenger Name
        gbc.gridx = 0; gbc.gridy = 1;
        topPanel.add(new JLabel("Passenger Name:"), gbc);
        JTextField nameField = new JTextField(20);
        gbc.gridx = 1;
        topPanel.add(nameField, gbc);
        
        // Passenger Email
        gbc.gridx = 0; gbc.gridy = 2;
        topPanel.add(new JLabel("Email:"), gbc);
        JTextField emailField = new JTextField(20);
        gbc.gridx = 1;
        topPanel.add(emailField, gbc);
        
        mainPanel.add(topPanel, BorderLayout.NORTH);
        
        // Seat selection panel
        JPanel seatPanel = new JPanel(new BorderLayout());
        seatPanel.setBorder(BorderFactory.createTitledBorder("Select Seat (Green = Available, Red = Booked)"));
        
        JPanel seatGrid = new JPanel(new GridLayout(4, 5, 5, 5));
        seatGrid.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Get booked seats for this flight
        java.util.Set<Integer> bookedSeats = new java.util.HashSet<>();
        try {
            String sql = "SELECT seatNumber FROM Bookings WHERE flightID = ? AND status != 'CANCELLED' AND seatNumber IS NOT NULL";
            try (Connection conn = database.DatabaseConnectivity.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, flight.getFlightId());
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        bookedSeats.add(rs.getInt("seatNumber"));
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error loading booked seats: " + e.getMessage());
        }
        
        JButton[] seatButtons = new JButton[20];
        final int[] selectedSeat = {0}; // Use array to make it effectively final
        
        for (int i = 1; i <= 20; i++) {
            final int seatNum = i;
            JButton seatBtn = new JButton(String.valueOf(seatNum));
            seatBtn.setPreferredSize(new Dimension(50, 50));
            
            if (bookedSeats.contains(seatNum)) {
                seatBtn.setBackground(Color.RED);
                seatBtn.setEnabled(false);
                seatBtn.setToolTipText("Seat " + seatNum + " - Booked");
            } else {
                seatBtn.setBackground(Color.GREEN);
                seatBtn.setEnabled(true);
                seatBtn.setToolTipText("Seat " + seatNum + " - Available");
                
                seatBtn.addActionListener(e -> {
                    // Reset previous selection
                    if (selectedSeat[0] > 0 && !bookedSeats.contains(selectedSeat[0])) {
                        seatButtons[selectedSeat[0] - 1].setBackground(Color.GREEN);
                    }
                    // Highlight selected seat
                    selectedSeat[0] = seatNum;
                    seatBtn.setBackground(new Color(0, 150, 0)); // Darker green for selected
                });
            }
            
            seatButtons[i - 1] = seatBtn;
            seatGrid.add(seatBtn);
        }
        
        seatPanel.add(seatGrid, BorderLayout.CENTER);
        mainPanel.add(seatPanel, BorderLayout.CENTER);
        
        // Payment panel
        JPanel paymentPanel = new JPanel(new GridBagLayout());
        paymentPanel.setBorder(BorderFactory.createTitledBorder("Payment Information"));
        paymentPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Payment Information"),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        GridBagConstraints pgbc = new GridBagConstraints();
        pgbc.insets = new Insets(8, 8, 8, 8);
        pgbc.anchor = GridBagConstraints.WEST;
        
        // Payment Method
        pgbc.gridx = 0; pgbc.gridy = 0;
        paymentPanel.add(new JLabel("Payment Method:"), pgbc);
        JComboBox<String> paymentMethodCombo = new JComboBox<>(new String[]{"CREDIT_CARD", "DEBIT_CARD"});
        pgbc.gridx = 1;
        paymentPanel.add(paymentMethodCombo, pgbc);
        
        // Card Number
        pgbc.gridx = 0; pgbc.gridy = 1;
        paymentPanel.add(new JLabel("Card Number:"), pgbc);
        JTextField cardNumberField = new JTextField(20);
        pgbc.gridx = 1;
        paymentPanel.add(cardNumberField, pgbc);
        
        // Expiry
        pgbc.gridx = 0; pgbc.gridy = 2;
        paymentPanel.add(new JLabel("Expiry (MM/YY):"), pgbc);
        JTextField expiryField = new JTextField(7);
        pgbc.gridx = 1;
        paymentPanel.add(expiryField, pgbc);
        
        // CVV
        pgbc.gridx = 0; pgbc.gridy = 3;
        paymentPanel.add(new JLabel("CVV:"), pgbc);
        JPasswordField cvvField = new JPasswordField(4);
        pgbc.gridx = 1;
        paymentPanel.add(cvvField, pgbc);
        
        mainPanel.add(paymentPanel, BorderLayout.SOUTH);
        
        dialog.add(mainPanel, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton bookBtn = new JButton("Confirm Booking & Pay");
        JButton cancelBtn = new JButton("Cancel");
        
        bookBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String cardNumber = cardNumberField.getText().trim();
            String expiry = expiryField.getText().trim();
            String cvv = new String(cvvField.getPassword());
            String paymentMethod = (String) paymentMethodCombo.getSelectedItem();
            
            // Validate passenger info
            if (name.isEmpty() || email.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, 
                    "Please enter passenger name and email.", 
                    "Validation Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (!email.contains("@")) {
                JOptionPane.showMessageDialog(dialog, 
                    "Please enter a valid email address.", 
                    "Validation Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (selectedSeat[0] == 0) {
                JOptionPane.showMessageDialog(dialog, 
                    "Please select a seat.", 
                    "Validation Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Validate payment info
            if (cardNumber.isEmpty() || expiry.isEmpty() || cvv.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, 
                    "Please enter all payment information.", 
                    "Validation Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (cardNumber.length() < 13 || cardNumber.length() > 19) {
                JOptionPane.showMessageDialog(dialog, 
                    "Card number must be between 13 and 19 digits.", 
                    "Validation Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (!cardNumber.matches("\\d+")) {
                JOptionPane.showMessageDialog(dialog, 
                    "Card number must contain only numbers.", 
                    "Validation Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            try {
                Connection conn = database.DatabaseConnectivity.getConnection();
                
                // Create booking first
                String bookingSql = "INSERT INTO Bookings (flightID, passengerName, passengerEmail, seatNumber, status) VALUES (?, ?, ?, ?, ?)";
                int bookingId = 0;
                try (PreparedStatement ps = conn.prepareStatement(bookingSql, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setInt(1, flight.getFlightId());
                    ps.setString(2, name);
                    ps.setString(3, email);
                    ps.setInt(4, selectedSeat[0]);
                    ps.setString(5, "CONFIRMED");
                    ps.executeUpdate();
                    
                    // Get the generated booking ID
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (rs.next()) {
                            bookingId = rs.getInt(1);
                        }
                    }
                }
                
                // Process payment and link to booking
                String cardLast4 = cardNumber.length() >= 4 ? 
                    cardNumber.substring(cardNumber.length() - 4) : cardNumber;
                
                String paymentSql = "INSERT INTO Payments (bookingID, amount, method, status, cardLast4) VALUES (?, ?, ?, ?, ?)";
                try (PreparedStatement ps = conn.prepareStatement(paymentSql)) {
                    ps.setInt(1, bookingId);
                    ps.setDouble(2, flight.getPrice());
                    ps.setString(3, paymentMethod);
                    ps.setString(4, "PAID");
                    ps.setString(5, cardLast4);
                    ps.executeUpdate();
                }
                
                conn.close();
                
                // Generate confirmation
                String confirmation = "=== Booking Confirmation ===\n" +
                    "Booking ID: " + bookingId + "\n" +
                    "Flight: " + flight.getFlightNumber() + "\n" +
                    "Route: " + flight.getOrigin() + " to " + flight.getDestination() + "\n" +
                    "Passenger: " + name + "\n" +
                    "Email: " + email + "\n" +
                    "Seat: " + selectedSeat[0] + "\n" +
                    "Amount Paid: $" + String.format("%.2f", flight.getPrice()) + "\n" +
                    "Payment Method: " + paymentMethod + "\n" +
                    "Card (last 4): " + cardLast4 + "\n" +
                    "Status: CONFIRMED\n" +
                    "============================";
                
                JOptionPane.showMessageDialog(dialog, confirmation, 
                    "Booking & Payment Confirmed!", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, 
                    "Database error: " + ex.getMessage(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        
        cancelBtn.addActionListener(e -> dialog.dispose());
        buttonPanel.add(bookBtn);
        buttonPanel.add(cancelBtn);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
    
    private void showManageCustomerBookingDialog(BookingDAO.BookingRecord booking) {
        JDialog dialog = new JDialog(this, "Manage Booking", true);
        dialog.setSize(400, 250);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));
        
        JPanel infoPanel = new JPanel(new GridBagLayout());
        infoPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        gbc.gridx = 0; gbc.gridy = 0;
        infoPanel.add(new JLabel("Booking ID: " + booking.getBookingId()), gbc);
        gbc.gridy = 1;
        infoPanel.add(new JLabel("Flight ID: " + booking.getFlightId()), gbc);
        gbc.gridy = 2;
        infoPanel.add(new JLabel("Status: " + booking.getStatus()), gbc);
        
        dialog.add(infoPanel, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton cancelBtn = new JButton("Cancel Booking");
        JButton closeBtn = new JButton("Close");
        
        cancelBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(dialog,
                "Are you sure you want to cancel this booking?",
                "Confirm Cancellation",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
            
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    bookingDAO.updateBookingStatus(booking.getBookingId(), "CANCELLED");
                    JOptionPane.showMessageDialog(dialog, 
                        "Booking cancelled successfully!", 
                        "Success", 
                        JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(dialog, 
                        "Database error: " + ex.getMessage(), 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        closeBtn.addActionListener(e -> dialog.dispose());
        buttonPanel.add(cancelBtn);
        buttonPanel.add(closeBtn);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
    
    private void switchToView(UserRole role) {
        currentRole = role;
        switch(role) {
            case CUSTOMER:
                cardLayout.show(cardPanel, "CUSTOMER");
                setTitle("Flight Management System - Book Your Flight");
                break;
            case ADMIN:
                cardLayout.show(cardPanel, "ADMIN");
                setTitle("Flight Management System - Admin Dashboard");
                break;
            case EMPLOYEE:
                cardLayout.show(cardPanel, "EMPLOYEE");
                setTitle("Flight Management System - Employee Dashboard");
                break;
        }
    }
    
    private JPanel createTopBar() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);
        topBar.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        // Left side - Logo/Title
        JLabel titleLabel = new JLabel("✈ FLIGHT MANAGEMENT SYSTEM");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        topBar.add(titleLabel, BorderLayout.WEST);
        
        // Right side - Separate Admin and Employee Login buttons
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setOpaque(false);
        
        JButton adminLoginBtn = new JButton("Admin Login");
        styleButton(adminLoginBtn, new Color(255, 87, 34), Color.WHITE);
        adminLoginBtn.addActionListener(e -> showLoginDialog(UserRole.ADMIN));
        
        JButton employeeLoginBtn = new JButton("Employee Login");
        styleButton(employeeLoginBtn, new Color(76, 175, 80), Color.WHITE);
        employeeLoginBtn.addActionListener(e -> showLoginDialog(UserRole.EMPLOYEE));
        
        rightPanel.add(adminLoginBtn);
        rightPanel.add(employeeLoginBtn);
        topBar.add(rightPanel, BorderLayout.EAST);
        
        return topBar;
    }
    
    private JPanel createCenterPanel() {
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);
        gbc.anchor = GridBagConstraints.CENTER;
        
        // Welcome message
        JLabel welcomeLabel = new JLabel("Welcome to Flight Management System");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 32));
        welcomeLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        centerPanel.add(welcomeLabel, gbc);
        
        // Subtitle
        JLabel subtitleLabel = new JLabel("Book your flight with ease");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        subtitleLabel.setForeground(new Color(255, 255, 255, 220));
        gbc.gridy = 1;
        gbc.insets = new Insets(10, 20, 40, 20);
        centerPanel.add(subtitleLabel, gbc);
        
        // Main action buttons
        gbc.gridwidth = 1;
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 40;
        gbc.ipady = 20;
        
        // Search Flights Button
        JButton searchFlightsBtn = createFeatureButton("🔍 Search Flights", 
            "Find and book available flights");
        searchFlightsBtn.addActionListener(e -> showFlightSearchDialog());
        gbc.gridx = 0;
        gbc.gridy = 2;
        centerPanel.add(searchFlightsBtn, gbc);
        
        // View My Bookings Button
        JButton viewBookingsBtn = createFeatureButton("📋 My Bookings", 
            "View and manage your reservations");
        viewBookingsBtn.addActionListener(e -> showCustomerBookings());
        gbc.gridx = 1;
        centerPanel.add(viewBookingsBtn, gbc);
        
        // Flight Schedule Button
        JButton scheduleBtn = createFeatureButton("📅 Flight Schedule", 
            "View all available flights");
        scheduleBtn.addActionListener(e -> showAllFlights());
        gbc.gridx = 0;
        gbc.gridy = 3;
        centerPanel.add(scheduleBtn, gbc);
        
        // Promotions Button
        JButton promotionsBtn = createFeatureButton("🎁 Monthly Promotions", 
            "Check out our special offers");
        promotionsBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, 
                "🎉 Special Promotion!\n\n" +
                "Book any flight in December 2025 and get 15% off!\n" +
                "Use code: DEC2025 at checkout.\n\n" +
                "Limited time offer - Book now!", 
                "Monthly Promotions", 
                JOptionPane.INFORMATION_MESSAGE);
        });
        gbc.gridx = 1;
        centerPanel.add(promotionsBtn, gbc);
        
        return centerPanel;
    }
    
    private JButton createFeatureButton(String title, String description) {
        JButton button = new JButton("<html><center><b>" + title + "</b><br>" + 
                                     "<small>" + description + "</small></center></html>");
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        styleButton(button, new Color(33, 150, 243), Color.WHITE);
        button.setPreferredSize(new Dimension(250, 100));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }
    
    private void styleButton(JButton button, Color bgColor, Color textColor) {
        button.setBackground(bgColor);
        button.setForeground(textColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        
        // Add hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bgColor.brighter());
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });
    }
    
    private void showLoginDialog(UserRole role) {
        String roleName = role == UserRole.ADMIN ? "Admin" : "Employee";
        JDialog loginDialog = new JDialog(this, roleName + " Login", true);
        loginDialog.setSize(400, 200);
        loginDialog.setLocationRelativeTo(this);
        loginDialog.setLayout(new BorderLayout(10, 10));
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Username field
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Username:"), gbc);
        JTextField usernameField = new JTextField(15);
        gbc.gridx = 1;
        formPanel.add(usernameField, gbc);
        
        // Password field
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Password:"), gbc);
        JPasswordField passwordField = new JPasswordField(15);
        gbc.gridx = 1;
        formPanel.add(passwordField, gbc);
        
        loginDialog.add(formPanel, BorderLayout.CENTER);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton loginBtn = new JButton("Login");
        JButton cancelBtn = new JButton("Cancel");
        
        loginBtn.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            
            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(loginDialog, 
                    "Please enter username and password", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Authenticate with database
            UserDAO userDAO = new UserDAO();
            UserDAO.UserRole dbRole = role == UserRole.ADMIN ? UserDAO.UserRole.ADMIN : UserDAO.UserRole.EMPLOYEE;
            
            if (userDAO.authenticate(username, password, dbRole)) {
                // Authentication successful
                switchToView(role);
                loginDialog.dispose();
            } else {
                JOptionPane.showMessageDialog(loginDialog, 
                    "Invalid username or password. Please try again.", 
                    "Login Failed", 
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        
        cancelBtn.addActionListener(e -> loginDialog.dispose());
        
        buttonPanel.add(loginBtn);
        buttonPanel.add(cancelBtn);
        loginDialog.add(buttonPanel, BorderLayout.SOUTH);
        
        loginDialog.setVisible(true);
    }
    
    public static void main(String[] args) {
        // Set look and feel for better appearance
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Use default look and feel if system L&F fails
        }
        
        SwingUtilities.invokeLater(() -> {
            new CustomerGUI().setVisible(true);
        });
    }
}
