// src/main/java/BookingDemo.java
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import javax.swing.*;
import customer.*;

public class BookingDemo {
    private JFrame frame;
    private JTextField txtFlightId;
    private JTextField txtCustomerId;
    private JTextField txtSeatCount;
    private JButton btnCreateBooking;
    private JButton btnModifyBooking;
    private JButton btnCancelBooking;
    private JTextArea txtConfirmation;

    private BookingService service;
    private String currentBookingId;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BookingDemo().createAndShowGUI());
    }

    private void createAndShowGUI() {
        frame = new JFrame("Booking Demo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout(10, 10));

        JPanel form = new JPanel(new GridLayout(0, 2, 10, 8));
        form.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        form.add(new JLabel("Flight ID:"));
        txtFlightId = new JTextField("F100");
        form.add(txtFlightId);

        form.add(new JLabel("Customer ID:"));
        txtCustomerId = new JTextField("C001");
        form.add(txtCustomerId);

        form.add(new JLabel("Seats:"));
        txtSeatCount = new JTextField("2");
        form.add(txtSeatCount);

        btnCreateBooking = new JButton("Create Booking");
        btnModifyBooking = new JButton("Modify Booking");
        btnCancelBooking = new JButton("Cancel Booking");

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttons.add(btnCreateBooking);
        buttons.add(btnModifyBooking);
        buttons.add(btnCancelBooking);

        txtConfirmation = new JTextArea(12, 40);
        txtConfirmation.setEditable(false);
        JScrollPane scroll = new JScrollPane(txtConfirmation);

        frame.add(form, BorderLayout.NORTH);
        frame.add(buttons, BorderLayout.CENTER);
        frame.add(scroll, BorderLayout.SOUTH);

        wiring();

        // seed demo data
        seedData();

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void wiring() {
        btnCreateBooking.addActionListener(e -> {
            try {
                String flightId = txtFlightId.getText().trim();
                String customerId = txtCustomerId.getText().trim();
                int seats = Integer.parseInt(txtSeatCount.getText().trim());

                Booking booking = service.createBooking(flightId, customerId, seats);
                currentBookingId = booking.getId();
                txtConfirmation.setText(service.generateConfirmation(currentBookingId));
            } catch (Exception ex) {
                showError(ex);
            }
        });

        btnModifyBooking.addActionListener(e -> {
            if (currentBookingId == null) {
                JOptionPane.showMessageDialog(frame, "Create a booking first.");
                return;
            }
            try {
                int seats = Integer.parseInt(txtSeatCount.getText().trim());
                service.modifyBooking(currentBookingId, seats);
                txtConfirmation.setText(service.generateConfirmation(currentBookingId));
            } catch (Exception ex) {
                showError(ex);
            }
        });

        btnCancelBooking.addActionListener(e -> {
            if (currentBookingId == null) {
                JOptionPane.showMessageDialog(frame, "Create a booking first.");
                return;
            }
            try {
                service.cancelBooking(currentBookingId);
                txtConfirmation.setText(service.generateConfirmation(currentBookingId));
            } catch (Exception ex) {
                showError(ex);
            }
        });
    }

    private void showError(Exception ex) {
        // Only why: avoid leaking stack traces in UI
        JOptionPane.showMessageDialog(frame, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void seedData() {
        String flightId = txtFlightId.getText().trim();
        String customerId = txtCustomerId.getText().trim();

        service = new BookingService(
                Arrays.asList(
                        new Flight(
                                flightId,
                                "NYC",
                                "LAX",
                                LocalDateTime.of(2025, 1, 10, 9, 30),
                                100,
                                new BigDecimal("199.99"))),
                Arrays.asList(
                        new Customer(
                                customerId,
                                "Alice Smith",
                                "alice@example.com")));
    }
}
