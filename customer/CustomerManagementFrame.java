import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

/**
 * Customer management window (list + add/edit/view).
 * GUI talks only to CustomerController; persistence is abstracted away.
 */
public class CustomerManagementFrame extends JFrame {

    private final CustomerController controller;
    private final CustomerTableModel tableModel = new CustomerTableModel();
    private JTable table;

    public CustomerManagementFrame() {
        this(new CustomerController());
    }

    public CustomerManagementFrame(CustomerController controller) {
        super("Customer Management");
        this.controller = controller;
        buildUi();
        refreshCustomers();
    }

    private void buildUi() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scroll = new JScrollPane(table);
        add(scroll, BorderLayout.CENTER);

        JButton btnAdd = new JButton("Add");
        JButton btnEdit = new JButton("Edit");
        JButton btnView = new JButton("View");

        btnAdd.addActionListener(e -> onAdd());
        btnEdit.addActionListener(e -> onEdit());
        btnView.addActionListener(e -> onView());

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        buttons.add(btnAdd);
        buttons.add(btnEdit);
        buttons.add(btnView);
        add(buttons, BorderLayout.NORTH);

        setSize(700, 400);
        setLocationRelativeTo(null);
    }

    private void refreshCustomers() {
        try {
            List<CustomerProfile> customers = controller.getAllCustomers();
            tableModel.setCustomers(customers);
        } catch (SQLException ex) {
            showError("Failed to load customers: " + ex.getMessage());
        }
    }

    private void onAdd() {
        CustomerFormDialog dialog = new CustomerFormDialog(this, "Add Customer", null);
        dialog.setVisible(true);
        CustomerProfile result = dialog.getResult();
        if (result == null) return;

        List<String> errors = controller.validate(result);
        if (!errors.isEmpty()) {
            showValidationErrors(errors);
            return;
        }

        try {
            controller.addCustomer(result);
            refreshCustomers();
        } catch (SQLException ex) {
            showError("Unable to add customer: " + ex.getMessage());
        }
    }

    private void onEdit() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a customer to edit.");
            return;
        }

        CustomerProfile existing = tableModel.getCustomerAt(row);
        CustomerFormDialog dialog = new CustomerFormDialog(this, "Edit Customer", existing);
        dialog.setVisible(true);
        CustomerProfile updated = dialog.getResult();
        if (updated == null) return;

        updated.setId(existing.getId());

        List<String> errors = controller.validate(updated);
        if (!errors.isEmpty()) {
            showValidationErrors(errors);
            return;
        }

        try {
            controller.updateCustomer(updated);
            refreshCustomers();
        } catch (SQLException ex) {
            showError("Unable to update customer: " + ex.getMessage());
        }
    }

    private void onView() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a customer to view.");
            return;
        }
        CustomerProfile existing = tableModel.getCustomerAt(row);
        CustomerViewDialog dialog = new CustomerViewDialog(this, existing);
        dialog.setVisible(true);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showValidationErrors(List<String> errors) {
        StringBuilder sb = new StringBuilder("Please correct the following:\n");
        for (String err : errors) {
            sb.append("- ").append(err).append("\n");
        }
        JOptionPane.showMessageDialog(this, sb.toString(), "Validation", JOptionPane.WARNING_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CustomerManagementFrame().setVisible(true));
    }
}
