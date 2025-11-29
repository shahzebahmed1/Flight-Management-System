import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Modal dialog for adding or editing a customer.
 */
public class CustomerFormDialog extends JDialog {

    private final JTextField txtName = new JTextField(20);
    private final JTextField txtEmail = new JTextField(20);
    private final JTextField txtPhone = new JTextField(15);
    private final JTextField txtDob = new JTextField(10); // yyyy-MM-dd
    private final JTextArea txtAddress = new JTextArea(3, 20);

    private CustomerProfile result;
    private final DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public CustomerFormDialog(Frame owner, String title, CustomerProfile existing) {
        super(owner, title, true);
        buildUi();
        if (existing != null) {
            populate(existing);
        }
        pack();
        setLocationRelativeTo(owner);
    }

    private void buildUi() {
        setLayout(new BorderLayout(10, 10));
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        int row = 0;
        addField(form, gbc, row++, "Name:", txtName);
        addField(form, gbc, row++, "Email:", txtEmail);
        addField(form, gbc, row++, "Phone:", txtPhone);
        addField(form, gbc, row++, "DOB (yyyy-MM-dd):", txtDob);

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        form.add(new JLabel("Address:"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        txtAddress.setLineWrap(true);
        txtAddress.setWrapStyleWord(true);
        JScrollPane scroll = new JScrollPane(txtAddress);
        form.add(scroll, gbc);

        add(form, BorderLayout.CENTER);

        JButton btnSave = new JButton("Save");
        JButton btnCancel = new JButton("Cancel");

        btnSave.addActionListener(e -> onSave());
        btnCancel.addActionListener(e -> onCancel());

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttons.add(btnSave);
        buttons.add(btnCancel);
        add(buttons, BorderLayout.SOUTH);
    }

    private void addField(JPanel form, GridBagConstraints gbc, int row, String label, JComponent field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        form.add(new JLabel(label), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        form.add(field, gbc);
    }

    private void populate(CustomerProfile existing) {
        if (existing == null) return;
        if (existing.getFullName() != null) txtName.setText(existing.getFullName());
        if (existing.getEmail() != null) txtEmail.setText(existing.getEmail());
        if (existing.getPhone() != null) txtPhone.setText(existing.getPhone());
        if (existing.getAddress() != null) txtAddress.setText(existing.getAddress());
        if (existing.getDateOfBirth() != null) txtDob.setText(df.format(existing.getDateOfBirth()));
    }

    private void onSave() {
        String name = txtName.getText().trim();
        String email = txtEmail.getText().trim();
        String phone = txtPhone.getText().trim();
        String address = txtAddress.getText().trim();
        String dobText = txtDob.getText().trim();

        LocalDate dob = null;
        if (!dobText.isEmpty()) {
            try {
                dob = LocalDate.parse(dobText, df);
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(
                        this,
                        "Date of birth must be in yyyy-MM-dd format.",
                        "Invalid Date",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        result = new CustomerProfile(name, email, phone, address, dob);
        dispose();
    }

    private void onCancel() {
        result = null;
        dispose();
    }

    public CustomerProfile getResult() {
        return result;
    }
}
