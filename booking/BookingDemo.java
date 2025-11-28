// src/main/java/BookingDemo.java
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new BookingDemo().createGUI();
        });
    }

    private void createGUI() {
        frame = new JFrame("Booking Demo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JPanel panelFields = new JPanel();
        panelFields.setLayout(new GridLayout(3, 2, 5, 5));

        JLabel lblFlightId = new JLabel("Flight ID:");
        JLabel lblCustomerId = new JLabel("Customer ID:");
        JLabel lblSeatCount = new JLabel("Seat count:");

        txtFlightId = new JTextField();
        txtCustomerId = new JTextField();
        txtSeatCount = new JTextField();

        panelFields.add(lblFlightId);
        panelFields.add(txtFlightId);
        panelFields.add(lblCustomerId);
        panelFields.add(txtCustomerId);
        panelFields.add(lblSeatCount);
        panelFields.add(txtSeatCount);

        frame.add(panelFields, BorderLayout.NORTH);

        JPanel panelButtons = new JPanel();
        panelButtons.setLayout(new FlowLayout());

        btnCreateBooking = new JButton("Create booking");
        btnModifyBooking = new JButton("Modify booking");
        btnCancelBooking = new JButton("Cancel booking");

        panelButtons.add(btnCreateBooking);
        panelButtons.add(btnModifyBooking);
        panelButtons.add(btnCancelBooking);

        frame.add(panelButtons, BorderLayout.CENTER);

        txtConfirmation = new JTextArea(10, 20);
        txtConfirmation.setEditable(false);

        frame.add(new JScrollPane(txtConfirmation), BorderLayout.SOUTH);

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        btnCreateBooking.addActionListener(e -> {
            String flightId = txtFlightId.getText();
            String customerId = txtCustomerId.getText();
            int seatCount;

            try {
                seatCount = Integer.parseInt(txtSeatCount.getText());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(
                        frame,
                        "Invalid seat count!",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            BookingService bookingService = createBookingService(flightId, customerId);

            try {
                Booking booking = bookingService.createBooking(flightId, customerId, seatCount);
                txtConfirmation.setText(bookingService.generateConfirmation(booking.getId()));
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(
                        frame,
                        ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        btnModifyBooking.addActionListener(e -> {
            String flightId = txtFlightId.getText();
            String customerId = txtCustomerId.getText();
            int seatCount;

            try {
                seatCount = Integer.parseInt(txtSeatCount.getText());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(
                        frame,
                        "Invalid seat count!",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            BookingService bookingService = createBookingService(flightId, customerId);

            try {
                Booking booking = bookingService.getBookingOrThrow(flightId + "_" + customerId);
                bookingService.modifyBooking(booking.getId(), seatCount);
                txtConfirmation.setText(bookingService.generateConfirmation(booking.getId()));
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(
                        frame,
                        ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    /**
     * Keeps the way bookingService is constructed in one place so it's easy to
     * change.
     */
    private BookingService createBookingService(String flightId, String customerId) {
        return new BookingService(
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

