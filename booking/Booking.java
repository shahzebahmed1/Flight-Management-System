import java.time.LocalDateTime;

public class Booking {
    private final String id;
    private final Flight flight;
    private final Customer customer;
    private int seatCount;
    private BookingStatus status;
    private final LocalDateTime createdAt;

    public Booking(String id, Flight flight, Customer customer, int seatCount) {
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

    public Customer getCustomer() {
        return customer;
    }

    public int getSeatCount() {
        return seatCount;
    }

    public void setSeatCount(int seatCount) {
        this.seatCount = seatCount;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
