import java.awt.*;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import javax.swing.*;

public class FlightManagementAdminGUI {

    private final FlightManagementDAO flightDAO = new FlightManagementDAO();
    private final LookupDAO lookupDAO = new LookupDAO();

    private JFrame frame;
    private JLabel label;
    private JTextField field;
    private JButton button;
    private JComboBox<Airline> airlineComboBox;
    private JComboBox<Aircraft> aircraftComboBox;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FlightManagementAdminGUI().run());
    }

    private void run() {
        createGUI();
        showGUI();
        showMenu();
    }

    private void createGUI() {
        frame = new JFrame("Flight Management (Admin Only)");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        label = new JLabel();
        field = new JTextField();
        button = new JButton();
        airlineComboBox = new JComboBox<>();
        aircraftComboBox = new JComboBox<>();

        try {
            List<Airline> airlines = lookupDAO.getAllAirlines();
            for (Airline a : airlines) {
                airlineComboBox.addItem(a);
            }

            List<Aircraft> aircrafts = lookupDAO.getAllAircrafts();
            for (Aircraft ac : aircrafts) {
                aircraftComboBox.addItem(ac);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    frame,
                    "Database error: " + e.getMessage(),
                    "Lookup",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void showGUI() {
        // Top panel: menu label + airline/aircraft selectors
        JPanel topPanel = new JPanel(new BorderLayout());

        JPanel menuLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        menuLabelPanel.add(label);
        topPanel.add(menuLabelPanel, BorderLayout.NORTH);

        JPanel selectorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        selectorPanel.add(new JLabel("Airline:"));
        selectorPanel.add(airlineComboBox);
        selectorPanel.add(new JLabel("Aircraft:"));
        selectorPanel.add(aircraftComboBox);
        topPanel.add(selectorPanel, BorderLayout.SOUTH);

        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(field, BorderLayout.CENTER);
        frame.add(button, BorderLayout.SOUTH);

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void setButtonAction(ActionListener listener) {
        // Avoid stacked listeners
        for (ActionListener l : button.getActionListeners()) {
            button.removeActionListener(l);
        }
        button.addActionListener(listener);
    }

    private void showMenu() {
        label.setText("<html>"
                + "1) List all flights<br>"
                + "2) Add new flight<br>"
                + "3) Update existing flight<br>"
                + "4) Delete flight<br>"
                + "0) Exit"
                + "</html>");
        field.setText("");
        button.setText("Enter choice");

        setButtonAction(e -> {
            String text = field.getText().trim();
            int choice;
            try {
                choice = Integer.parseInt(text);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Please enter a valid number.", "Menu",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            switch (choice) {
                case 1 -> listFlights();
                case 2 -> addFlight();
                case 3 -> updateFlight();
                case 4 -> deleteFlight();
                case 0 -> {
                    JOptionPane.showMessageDialog(frame, "Goodbye.");
                    frame.dispose();
                    break;
                }
                default -> JOptionPane.showMessageDialog(frame, "Invalid choice.", "Menu",
                        JOptionPane.WARNING_MESSAGE);
            }
        });
    }

    private void listFlights() {
        try {
            List<FlightRecord> flights = flightDAO.getAllFlights();
            StringBuilder output = new StringBuilder(
                    "FlightID\tFlightNumber\tAirlineID\tAirlineName\tOrigin\tDestination\tDeparture\tArrival\tPrice\tAircraftID\tAircraftModel\n"
            );
            for (FlightRecord f : flights) {
                output.append(String.format("%d\t%s\t%d\t%s\t%s\t%s\t%s\t%s\t%.2f\t%d\t%s%n",
                        f.getFlightId(),
                        f.getFlightNumber(),
                        f.getAirlineId(),
                        f.getAirlineName(),
                        f.getOrigin(),
                        f.getDestination(),
                        f.getDepartureTime(),
                        f.getArrivalTime(),
                        f.getPrice(),
                        f.getAircraftId(),
                        f.getAircraftModel()));
            }
            JOptionPane.showMessageDialog(frame, output.toString(), "Flights", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Database error: " + e.getMessage(), "Flights",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addFlight() {
        final Airline airline = (Airline) airlineComboBox.getSelectedItem();
        final Aircraft aircraft = (Aircraft) aircraftComboBox.getSelectedItem();

        if (airline == null || aircraft == null) {
            JOptionPane.showMessageDialog(frame, "Please select an airline and an aircraft first.",
                    "Add flight", JOptionPane.ERROR_MESSAGE);
            return;
        }

        label.setText("Enter flight number:");
        field.setText("");
        button.setText("Add flight");

        setButtonAction(e -> {
            String flightNumber = field.getText().trim();
            if (flightNumber.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please enter a flight number.", "Add flight",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                String origin = JOptionPane.showInputDialog(frame, "Enter origin (e.g., JFK):");
                if (origin == null || origin.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Origin is required.", "Add flight",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String destination = JOptionPane.showInputDialog(frame, "Enter destination (e.g., LAX):");
                if (destination == null || destination.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Destination is required.", "Add flight",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String departureStr = JOptionPane.showInputDialog(frame,
                        "Enter departure timestamp (yyyy-MM-dd HH:mm:ss):");
                if (departureStr == null || departureStr.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Departure time is required.", "Add flight",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String arrivalStr = JOptionPane.showInputDialog(frame,
                        "Enter arrival timestamp (yyyy-MM-dd HH:mm:ss):");
                if (arrivalStr == null || arrivalStr.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Arrival time is required.", "Add flight",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String priceStr = JOptionPane.showInputDialog(frame, "Enter price:");
                if (priceStr == null || priceStr.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Price is required.", "Add flight",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Timestamp departure = Timestamp.valueOf(departureStr.trim());
                Timestamp arrival = Timestamp.valueOf(arrivalStr.trim());
                double price = Double.parseDouble(priceStr.trim());

                FlightRecord newFlight = new FlightRecord(
                        0,
                        flightNumber,
                        airline.getAirlineId(),
                        airline.getAirlineName(),
                        origin.trim(),
                        destination.trim(),
                        departure,
                        arrival,
                        price,
                        aircraft.getAircraftId(),
                        aircraft.getModel()
                );

                flightDAO.insertFlight(newFlight);
                JOptionPane.showMessageDialog(frame, "Flight added successfully.", "Add flight",
                        JOptionPane.INFORMATION_MESSAGE);
                showMenu();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(frame, "Database error: " + ex.getMessage(), "Add flight",
                        JOptionPane.ERROR_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Invalid number: " + ex.getMessage(), "Add flight",
                        JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(frame, "Invalid date/time: " + ex.getMessage(), "Add flight",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private void updateFlight() {
        try {
            final List<FlightRecord> flights = flightDAO.getAllFlights();
            StringBuilder output = new StringBuilder(
                    "FlightID\tFlightNumber\tAirlineID\tAirlineName\tOrigin\tDestination\tDeparture\tArrival\tPrice\tAircraftID\tAircraftModel\n"
            );
            for (FlightRecord f : flights) {
                output.append(String.format("%d\t%s\t%d\t%s\t%s\t%s\t%s\t%s\t%.2f\t%d\t%s%n",
                        f.getFlightId(),
                        f.getFlightNumber(),
                        f.getAirlineId(),
                        f.getAirlineName(),
                        f.getOrigin(),
                        f.getDestination(),
                        f.getDepartureTime(),
                        f.getArrivalTime(),
                        f.getPrice(),
                        f.getAircraftId(),
                        f.getAircraftModel()));
            }
            JOptionPane.showMessageDialog(frame, output.toString(), "Flights", JOptionPane.INFORMATION_MESSAGE);

            label.setText("Enter flight ID to update:");
            field.setText("");
            button.setText("Update flight");

            setButtonAction(e -> {
                int id;
                try {
                    id = Integer.parseInt(field.getText().trim());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Please enter a valid flight ID.", "Update flight",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                FlightRecord existing = flights.stream()
                        .filter(f -> f.getFlightId() == id)
                        .findFirst()
                        .orElse(null);

                if (existing == null) {
                    JOptionPane.showMessageDialog(frame, "Flight ID not found.", "Update flight",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                label.setText("Enter new flight number (leave empty to keep current):");
                field.setText(existing.getFlightNumber());
                button.setText("Save");

                setButtonAction(event -> {
                    String flightNumber = field.getText().trim();
                    if (flightNumber.isEmpty()) {
                        flightNumber = existing.getFlightNumber();
                    }

                    try {
                        FlightRecord updated = new FlightRecord(
                                existing.getFlightId(),
                                flightNumber,
                                existing.getAirlineId(),
                                existing.getAirlineName(),
                                existing.getOrigin(),
                                existing.getDestination(),
                                existing.getDepartureTime(),
                                existing.getArrivalTime(),
                                existing.getPrice(),
                                existing.getAircraftId(),
                                existing.getAircraftModel()
                        );
                        flightDAO.updateFlight(updated);
                        JOptionPane.showMessageDialog(frame, "Flight updated successfully.", "Update flight",
                                JOptionPane.INFORMATION_MESSAGE);
                        showMenu();
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(frame, "Database error: " + ex.getMessage(),
                                "Update flight", JOptionPane.ERROR_MESSAGE);
                    }
                });
            });
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Database error: " + e.getMessage(), "Update flight",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteFlight() {
        try {
            final List<FlightRecord> flights = flightDAO.getAllFlights();
            StringBuilder output = new StringBuilder(
                    "FlightID\tFlightNumber\tAirlineID\tAirlineName\tOrigin\tDestination\tDeparture\tArrival\tPrice\tAircraftID\tAircraftModel\n"
            );
            for (FlightRecord f : flights) {
                output.append(String.format("%d\t%s\t%d\t%s\t%s\t%s\t%s\t%s\t%.2f\t%d\t%s%n",
                        f.getFlightId(),
                        f.getFlightNumber(),
                        f.getAirlineId(),
                        f.getAirlineName(),
                        f.getOrigin(),
                        f.getDestination(),
                        f.getDepartureTime(),
                        f.getArrivalTime(),
                        f.getPrice(),
                        f.getAircraftId(),
                        f.getAircraftModel()));
            }
            JOptionPane.showMessageDialog(frame, output.toString(), "Flights", JOptionPane.INFORMATION_MESSAGE);

            label.setText("Enter flight ID to delete:");
            field.setText("");
            button.setText("Delete flight");

            setButtonAction(e -> {
                int id;
                try {
                    id = Integer.parseInt(field.getText().trim());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Please enter a valid flight ID.", "Delete flight",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                FlightRecord existing = null;
                for (FlightRecord f : flights) {
                    if (f.getFlightId() == id) {
                        existing = f;
                        break;
                    }
                }

                if (existing == null) {
                    JOptionPane.showMessageDialog(frame, "Flight ID not found.", "Delete flight",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int confirm = JOptionPane.showConfirmDialog(frame, "Are you sure?", "Delete flight",
                        JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        flightDAO.deleteFlight(id);
                        JOptionPane.showMessageDialog(frame, "Flight deleted successfully.", "Delete flight",
                                JOptionPane.INFORMATION_MESSAGE);
                        showMenu();
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(frame, "Database error: " + ex.getMessage(), "Delete flight",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Database error: " + e.getMessage(), "Delete flight",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
