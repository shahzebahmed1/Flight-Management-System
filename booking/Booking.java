import java.time.LocalDateTime;
import booking.customer.Customer;

/**
 * Immutable-ish booking aggregate. SeatCount and status can change; id/flight/customer/createdAt do not.
 */
public class Booking {
    private final String id;
    private final Flight flight;
    private final Customer customer;
    private int seatCount;
    private BookingStatus status;
    private final LocalDateTime createdAt;

    public Booking(String id, Flight flight, Customer customer, int seatCount) {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("id is required");
        if (flight == null) throw new IllegalArgumentException("flight is required");
        if (customer == null) throw new IllegalArgumentException("customer is required");
        if (seatCount <= 0) throw new IllegalArgumentException("seatCount must be > 0");
        this.id = id;
        this.flight = flight;
        this.customer = customer;
        this.seatCount = seatCount;
        this.status = BookingStatus.CONFIRMED;
        this.createdAt = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public Flight getFlight() {
        return flight;
    }

    public booking.customer.Customer getCustomer() {
        return customer;
    }

    public int getSeatCount() {
        return seatCount;
    }

    public void setSeatCount(int seatCount) {
        if (seatCount <= 0) throw new IllegalArgumentException("seatCount must be > 0");
        this.seatCount = seatCount;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        if (status == null) throw new IllegalArgumentException("status is required");
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
