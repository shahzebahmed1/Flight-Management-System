package payment;
import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class PaymentSimulationFrame extends JFrame {

    private JTextField txtBookingId;
    private JTextField txtAmount;
    private JTextField txtCardNumber;
    private JTextField txtExpiry;
    private JPasswordField txtCvv;
    private JComboBox<PaymentMethod> cmbMethod;

    private final PaymentDAO paymentDAO = new PaymentDAO();

    public PaymentSimulationFrame() {
        super("Payment Simulation");
        initComponents();
    }

    private void initComponents() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        setResizable(false);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        txtBookingId = new JTextField(10);
        txtAmount = new JTextField(10);
        txtCardNumber = new JTextField(16);
        txtExpiry = new JTextField(7); // MM/YY
        txtCvv = new JPasswordField(4);
        cmbMethod = new JComboBox<>(PaymentMethod.values());

        int row = 0;

        gbc.gridx = 0; gbc.gridy = row;
        form.add(new JLabel("Booking ID:"), gbc);
        gbc.gridx = 1;
        form.add(txtBookingId, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        form.add(new JLabel("Amount:"), gbc);
        gbc.gridx = 1;
        form.add(txtAmount, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        form.add(new JLabel("Payment Method:"), gbc);
        gbc.gridx = 1;
        form.add(cmbMethod, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        form.add(new JLabel("Card Number:"), gbc);
        gbc.gridx = 1;
        form.add(txtCardNumber, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        form.add(new JLabel("Expiry (MM/YY):"), gbc);
        gbc.gridx = 1;
        form.add(txtExpiry, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        form.add(new JLabel("CVV:"), gbc);
        gbc.gridx = 1;
        form.add(txtCvv, gbc);

        add(form, BorderLayout.CENTER);

        JButton btnPay = new JButton("Simulate Payment");
        JButton btnClear = new JButton("Clear");

        btnPay.addActionListener(e -> onPayClicked());
        btnClear.addActionListener(e -> clearForm());

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttons.add(btnPay);
        buttons.add(btnClear);

        add(buttons, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
    }

    private void onPayClicked() {
        String bookingIdText = txtBookingId.getText().trim();
        String amountText = txtAmount.getText().trim();
        String cardNumber = txtCardNumber.getText().trim();
        PaymentMethod method = (PaymentMethod) cmbMethod.getSelectedItem();

        if (bookingIdText.isEmpty() || amountText.isEmpty()) {
            showError("Booking ID and amount are required.");
            return;
        }

        int bookingId;
        double amount;
        try {
            bookingId = Integer.parseInt(bookingIdText);
        } catch (NumberFormatException e) {
            showError("Booking ID must be a number.");
            return;
        }
        try {
            amount = Double.parseDouble(amountText);
        } catch (NumberFormatException e) {
            showError("Amount must be a valid number.");
            return;
        }

        try {
            Payment payment = paymentDAO.recordPayment(bookingId, amount, method, cardNumber);

            JOptionPane.showMessageDialog(
                    this,
                    "Payment " + payment.getStatus() +
                            "\nPayment ID: " + payment.getPaymentId() +
                            "\nBooking ID: " + payment.getBookingId() +
                            "\nAmount: $" + payment.getAmount() +
                            "\nMethod: " + payment.getMethod() +
                            "\nCard (last 4): " + payment.getCardLast4(),
                    "Payment Result",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (SQLException ex) {
            ex.printStackTrace();
            showError("Database error: " + ex.getMessage());
        }
    }

    private void clearForm() {
        txtBookingId.setText("");
        txtAmount.setText("");
        txtCardNumber.setText("");
        txtExpiry.setText("");
        txtCvv.setText("");
        cmbMethod.setSelectedIndex(0);
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(
                this,
                msg,
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PaymentSimulationFrame().setVisible(true));
    }
}
