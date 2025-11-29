import javax.swing.table.AbstractTableModel;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CustomerTableModel extends AbstractTableModel {

    private final List<CustomerProfile> data = new ArrayList<>();
    private final String[] columns = {"ID", "Name", "Email", "Phone", "DOB"};
    private final DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public String getColumnName(int column) {
        return columns[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        CustomerProfile c = data.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> c.getId();
            case 1 -> c.getFullName();
            case 2 -> c.getEmail();
            case 3 -> c.getPhone();
            case 4 -> c.getDateOfBirth() != null ? df.format(c.getDateOfBirth()) : "";
            default -> "";
        };
    }

    public CustomerProfile getCustomerAt(int row) {
        return data.get(row);
    }

    public void setCustomers(List<CustomerProfile> customers) {
        data.clear();
        if (customers != null) {
            data.addAll(customers);
        }
        fireTableDataChanged();
    }
}
