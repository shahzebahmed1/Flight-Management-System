import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import customer.Customer;

/**
 * In-memory booking orchestration service.
 */
public class BookingService {

    private final Map<String, Flight> flights = new HashMap<>();
    private final Map<String, Customer> customers = new HashMap<>();
    private final Map<String, Booking> bookings = new HashMap<>();

    public BookingService(Collection<Flight> flights, Collection<Customer> customers) {
        if (flights != null) {
            for (Flight f : flights) {
                this.flights.put(f.getId(), f);
            }
        }
        if (customers != null) {
            for (Customer c : customers) {
                this.customers.put(c.getId(), c);
            }
        }
    }

    public Booking createBooking(String flightId, String customerId, int seatCount) {
        Flight flight = requireFlight(flightId);
        Customer customer = requireCustomer(customerId);

        flight.bookSeats(seatCount);

        String id = UUID.randomUUID().toString();
        Booking booking = new Booking(id, flight, customer, seatCount);
        bookings.put(id, booking);
        return booking;
    }

    public Booking modifyBooking(String bookingId, int newSeatCount) {
        if (newSeatCount <= 0) throw new IllegalArgumentException("newSeatCount must be > 0");
        Booking booking = getBookingOrThrow(bookingId);
        int delta = newSeatCount - booking.getSeatCount();

        if (delta > 0) {
            booking.getFlight().bookSeats(delta);
        } else if (delta < 0) {
            booking.getFlight().releaseSeats(-delta);
        }

        booking.setSeatCount(newSeatCount);
        booking.setStatus(BookingStatus.MODIFIED);
        return booking;
    }

    public void cancelBooking(String bookingId) {
        Booking booking = getBookingOrThrow(bookingId);
        booking.getFlight().releaseSeats(booking.getSeatCount());
        booking.setStatus(BookingStatus.CANCELLED);
    }

    public Booking getBookingOrThrow(String bookingId) {
        Booking booking = bookings.get(bookingId);
        if (booking == null) {
            throw new IllegalArgumentException("Booking not found: " + bookingId);
        }
        return booking;
    }

    public String generateConfirmation(String bookingId) {
        Booking booking = getBookingOrThrow(bookingId);

        StringBuilder sb = new StringBuilder();
        sb.append("===== BOOKING CONFIRMATION =====
");
        sb.append("Booking ID: ").append(booking.getId()).append("\n");
        sb.append("Status: ").append(booking.getStatus()).append("\n");
        sb.append("Customer: ").append(booking.getCustomer().getName())
          .append(" <").append(booking.getCustomer().getEmail()).append(">").append("\n");
        sb.append("Flight: ").append(booking.getFlight().getId())
          .append(" ").append(booking.getFlight().getOrigin())
          .append(" → ").append(booking.getFlight().getDestination()).append("\n");
        sb.append("Departure: ").append(booking.getFlight().getDepartureTime()).append("\n");
        sb.append("Seats: ").append(booking.getSeatCount()).append("\n");
        sb.append("============================\n");

        return sb.toString();
    }

    private Flight requireFlight(String id) {
        Flight f = flights.get(id);
        if (f == null) throw new IllegalArgumentException("Unknown flight: " + id);
        return f;
    }

    private Customer requireCustomer(String id) {
        Customer c = customers.get(id);
        if (c == null) throw new IllegalArgumentException("Unknown customer: " + id);
        return c;
    }
}
