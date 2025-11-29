import javax.swing.*;
import java.awt.*;
import java.time.format.DateTimeFormatter;

/**
 * Read-only dialog to show full customer details.
 */
public class CustomerViewDialog extends JDialog {

    private final DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public CustomerViewDialog(Frame owner, CustomerProfile customer) {
        super(owner, "Customer Details", true);
        buildUi(customer);
        pack();
        setLocationRelativeTo(owner);
    }

    private void buildUi(CustomerProfile c) {
        setLayout(new BorderLayout(10, 10));

        JPanel form = new JPanel(new GridLayout(0, 1, 5, 5));
        form.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        form.add(new JLabel("ID: " + safe(c.getId())));
        form.add(new JLabel("Name: " + safe(c.getFullName())));
        form.add(new JLabel("Email: " + safe(c.getEmail())));
        form.add(new JLabel("Phone: " + safe(c.getPhone())));
        form.add(new JLabel("DOB: " + (c.getDateOfBirth() != null ? df.format(c.getDateOfBirth()) : "")));

        JTextArea addressArea = new JTextArea(safe(c.getAddress()));
        addressArea.setEditable(false);
        addressArea.setLineWrap(true);
        addressArea.setWrapStyleWord(true);
        addressArea.setBorder(BorderFactory.createTitledBorder("Address"));

        add(form, BorderLayout.NORTH);
        add(new JScrollPane(addressArea), BorderLayout.CENTER);

        JButton ok = new JButton("Close");
        ok.addActionListener(e -> dispose());
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttons.add(ok);
        add(buttons, BorderLayout.SOUTH);
    }

    private String safe(Object value) {
        return value == null ? "" : value.toString();
    }
}
