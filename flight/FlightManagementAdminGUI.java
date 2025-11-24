import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;

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
        new FlightManagementAdminGUI().run();
    }

    private void run() {
        createGUI();
        showGUI();
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
            System.out.println("Database error: " + e.getMessage());
            return;
        }
    }

    private void showGUI() {
        frame.add(label, BorderLayout.NORTH);
        frame.add(field, BorderLayout.CENTER);
        frame.add(button, BorderLayout.SOUTH);

        frame.pack();
        frame.setVisible(true);
    }

    private void showMenu() {
        label.setText("1) List all flights\n2) Add new flight\n3) Update existing flight\n4) Delete flight\n0) Exit");
        button.setText("Enter choice");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    int choice = Integer.parseInt(field.getText());
                    switch (choice) {
                        case 1:
                            listFlights();
                            break;
                        case 2:
                            addFlight();
                            break;
                        case 3:
                            updateFlight();
                            break;
                        case 4:
                            deleteFlight();
                            break;
                        case 0:
                            System.out.println("Goodbye.");
                            frame.dispose();
                            return;
                        default:
                            System.out.println("Invalid choice.");
                    }
                } catch (NumberFormatException ex) {
                    System.out.println("Please enter a valid number.");
                }
            }
        });
    }

    private void listFlights() {
        try {
            List<FlightRecord> flights = flightDAO.getAllFlights();
            String output = "FlightID\tFlightNumber\tAirlineID\tAirlineName\tOrigin\tDestination\tDeparture\tArrival\tPrice\tAircraftID\tAircraftModel\n";
            for (FlightRecord f : flights) {
                output += String.format("%d\t%s\t%d\t%s\t%s\t%s\t%s\t%s\t%.2f\t%d\t%s\n",
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
                        f.getAircraftModel());
            }
            JOptionPane.showMessageDialog(frame, output, "Flights", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Database error: " + e.getMessage(), "Flights", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addFlight() {
        try {
            Airline airline = (Airline) airlineComboBox.getSelectedItem();
            Aircraft aircraft = (Aircraft) aircraftComboBox.getSelectedItem();

            field.setText("");
            label.setText("Enter flight number:");
            button.setText("Add flight");
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    String flightNumber = field.getText();
                    if (flightNumber.isEmpty()) {
                        JOptionPane.showMessageDialog(frame, "Please enter a flight number.", "Add flight", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    FlightRecord newFlight = new FlightRecord(
                            0,
                            flightNumber,
                            airline.getAirlineId(),
                            airline.getAirlineName(),
                            field.getText(),
                            field.getText(),
                            Timestamp.valueOf(field.getText()),
                            Timestamp.valueOf(field.getText()),
                            Double.parseDouble(field.getText()),
                            aircraft.getAircraftId(),
                            aircraft.getModel()
                    );
                    flightDAO.insertFlight(newFlight);
                    JOptionPane.showMessageDialog(frame, "Flight added successfully.", "Add flight", JOptionPane.INFORMATION_MESSAGE);
                }
            });
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Database error: " + e.getMessage(), "Add flight", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateFlight() {
        try {
            List<FlightRecord> flights = flightDAO.getAllFlights();
            String output = "FlightID\tFlightNumber\tAirlineID\tAirlineName\tOrigin\tDestination\tDeparture\tArrival\tPrice\tAircraftID\tAircraftModel\n";
            for (FlightRecord f : flights) {
                output += String.format("%d\t%s\t%d\t%s\t%s\t%s\t%s\t%s\t%.2f\t%d\t%s\n",
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
                        f.getAircraftModel());
            }
            JOptionPane.showMessageDialog(frame, output, "Flights", JOptionPane.INFORMATION_MESSAGE);

            field.setText("");
            label.setText("Enter flight ID to update:");
            button.setText("Update flight");
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    int id = Integer.parseInt(field.getText());
                    FlightRecord existing = null;
                    for (FlightRecord f : flights) {
                        if (f.getFlightId() == id) {
                            existing = f;
                            break;
                        }
                    }
                    if (existing == null) {
                        JOptionPane.showMessageDialog(frame, "Flight ID not found.", "Update flight", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    label.setText("Enter new flight number (leave empty to keep current):");
                    field.setText(existing.getFlightNumber());

                    button.setText("Update flight");
                    button.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            String flightNumber = field.getText();
                            if (flightNumber.isEmpty()) flightNumber = existing.getFlightNumber();

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
                            JOptionPane.showMessageDialog(frame, "Flight updated successfully.", "Update flight", JOptionPane.INFORMATION_MESSAGE);
                        }
                    });
                }
            });
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Database error: " + e.getMessage(), "Update flight", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteFlight() {
        try {
            List<FlightRecord> flights = flightDAO.getAllFlights();
            String output = "FlightID\tFlightNumber\tAirlineID\tAirlineName\tOrigin\tDestination\tDeparture\tArrival\tPrice\tAircraftID\tAircraftModel\n";
            for (FlightRecord f : flights) {
                output += String.format("%d\t%s\t%d\t%s\t%s\t%s\t%s\t%s\t%.2f\t%d\t%s\n",
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
                        f.getAircraftModel());
            }
            JOptionPane.showMessageDialog(frame, output, "Flights", JOptionPane.INFORMATION_MESSAGE);

            field.setText("");
            label.setText("Enter flight ID to delete:");
            button.setText("Delete flight");
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    int id = Integer.parseInt(field.getText());
                    FlightRecord existing = null;
                    for (FlightRecord f : flights) {
                        if (f.getFlightId() == id) {
                            existing = f;
                            break;
                        }
                    }
                    if (existing == null) {
                        JOptionPane.showMessageDialog(frame, "Flight ID not found.", "Delete flight", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    int confirm = JOptionPane.showConfirmDialog(frame, "Are you sure?", "Delete flight", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        flightDAO.deleteFlight(id);
                        JOptionPane.showMessageDialog(frame, "Flight deleted successfully.", "Delete flight", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            });
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Database error: " + e.getMessage(), "Delete flight", JOptionPane.ERROR_MESSAGE);
        }
    }

