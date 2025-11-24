import java.time.LocalDate;
import java.util.Objects;

/**
 * Represents a customer profile stored in the database.
 * This is kept separate from the lightweight booking.Customer class to avoid breaking existing flows.
 */
public class CustomerProfile {

    private Integer id; // null until persisted
    private String fullName;
    private String email;
    private String phone;
    private String address;
    private LocalDate dateOfBirth;

    public CustomerProfile(Integer id,
                           String fullName,
                           String email,
                           String phone,
                           String address,
                           LocalDate dateOfBirth) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.dateOfBirth = dateOfBirth;
    }

    public CustomerProfile(String fullName,
                           String email,
                           String phone,
                           String address,
                           LocalDate dateOfBirth) {
        this(null, fullName, email, phone, address, dateOfBirth);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public boolean hasId() {
        return id != null;
    }

    @Override
    public String toString() {
        return "CustomerProfile{" +
                "id=" + id +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CustomerProfile)) return false;
        CustomerProfile that = (CustomerProfile) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(email, that.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email);
    }
}
