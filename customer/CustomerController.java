import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Control layer between UI and persistence for customer management.
 */
public class CustomerController {

    private final CustomerDAO customerDAO;
    private final Pattern emailPattern = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    private final Pattern phonePattern = Pattern.compile("^[+]?[-0-9()\\s]{7,20}$");

    public CustomerController() {
        this(new CustomerDAO());
    }

    public CustomerController(CustomerDAO customerDAO) {
        this.customerDAO = customerDAO;
    }

    public CustomerProfile addCustomer(CustomerProfile customer) throws SQLException {
        validateOrThrow(customer);
        return customerDAO.addCustomer(customer);
    }

    public void updateCustomer(CustomerProfile customer) throws SQLException {
        validateOrThrow(customer);
        customerDAO.updateCustomer(customer);
    }

    public CustomerProfile getCustomerById(int id) throws SQLException {
        return customerDAO.getCustomerById(id);
    }

    public List<CustomerProfile> getAllCustomers() throws SQLException {
        return customerDAO.getAllCustomers();
    }

    public List<String> validate(CustomerProfile customer) {
        if (customer == null) {
            return Collections.singletonList("Customer data is required.");
        }

        List<String> errors = new ArrayList<>();

        String name = emptyToNull(customer.getFullName());
        if (name == null) {
            errors.add("Name is required.");
        }

        String email = emptyToNull(customer.getEmail());
        if (email == null) {
            errors.add("Email is required.");
        } else if (!emailPattern.matcher(email).matches()) {
            errors.add("Email format is invalid.");
        }

        String phone = emptyToNull(customer.getPhone());
        if (phone != null && !phonePattern.matcher(phone).matches()) {
            errors.add("Phone number format is invalid.");
        }

        LocalDate dob = customer.getDateOfBirth();
        if (dob != null && dob.isAfter(LocalDate.now())) {
            errors.add("Date of birth cannot be in the future.");
        }

        return errors;
    }

    private void validateOrThrow(CustomerProfile customer) {
        List<String> errors = validate(customer);
        if (!errors.isEmpty()) {
            throw new IllegalArgumentException(String.join(" ", errors));
        }
    }

    private String emptyToNull(String value) {
        if (value == null) return null;
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
