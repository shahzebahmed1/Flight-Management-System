import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BookingService {

    private final Map<String, Flight> flights = new HashMap<>();
    private final Map<String, Customer> customers = new HashMap<>();
    private final Map<String, Booking> bookings = new HashMap<>();

    public BookingService(Collection<Flight> flights, Collection<Customer> customers) {
        for (Flight f : flights) {
            this.flights.put(f.getId(), f);
        }
        for (Customer c : customers) {
            this.customers.put(c.getId(), c);
        }
    }

    // --- Make a new booking ---
    public Booking createBooking(String flightId, String customerId, int seatCount) {
        if (seatCount <= 0) {
            throw new IllegalArgumentException("Seat count must be positive.");
        }

        Flight flight = flights.get(flightId);
        if (flight == null) {
            throw new IllegalArgumentException("Flight not found: " + flightId);
        }

        Customer customer = customers.get(customerId);
        if (customer == null) {
            throw new IllegalArgumentException("Customer not found: " + customerId);
        }

        // Check and reserve seats
        flight.bookSeats(seatCount);

        String bookingId = UUID.randomUUID().toString();
        Booking booking = new Booking(bookingId, flight, customer, seatCount);
        bookings.put(bookingId, booking);

        return booking;
    }

    // --- Modify an existing booking (change number of seats) ---
    public Booking modifyBooking(String bookingId, int newSeatCount) {
        if (newSeatCount <= 0) {
            throw new IllegalArgumentException("Seat count must be positive.");
        }

        Booking booking = getBookingOrThrow(bookingId);

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new IllegalStateException("Cannot modify a cancelled booking.");
        }

        Flight flight = booking.getFlight();
        int oldSeatCount = booking.getSeatCount();
        int delta = newSeatCount - oldSeatCount;

        if (delta > 0) {
            // Need more seats
            flight.bookSeats(delta);
        } else if (delta < 0) {
            // Releasing seats
            flight.releaseSeats(-delta);
        }

        booking.setSeatCount(newSeatCount);
        booking.setStatus(BookingStatus.MODIFIED);
        return booking;
    }

    // --- Cancel an existing booking ---
    public Booking cancelBooking(String bookingId) {
        Booking booking = getBookingOrThrow(bookingId);

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            return booking; // already cancelled
        }

        Flight flight = booking.getFlight();
        flight.releaseSeats(booking.getSeatCount());
        booking.setStatus(BookingStatus.CANCELLED);
        return booking;
    }

    // --- Generate a booking confirmation string ---
    public String generateConfirmation(String bookingId) {
        Booking booking = getBookingOrThrow(bookingId);
        Flight flight = booking.getFlight();
        Customer customer = booking.getCustomer();

        StringBuilder sb = new StringBuilder();
        sb.append("=== Booking Confirmation ===\n");
        sb.append("Booking ID: ").append(booking.getId()).append("\n");
        sb.append("Status: ").append(booking.getStatus()).append("\n\n");
        sb.append("Customer: ").append(customer.getName())
          .append(" (").append(customer.getEmail()).append(")\n");
        sb.append("Flight: ").append(flight.getId()).append("\n");
        sb.append("Route: ").append(flight.getOrigin())
          .append(" -> ").append(flight.getDestination()).append("\n");
        sb.append("Departure: ").append(flight.getDepartureTime()).append("\n");
        sb.append("Seats: ").append(booking.getSeatCount()).append("\n");
        sb.append("============================\n");

        return sb.toString();
    }

    public Booking getBookingOrThrow(String bookingId) {
        Booking booking = bookings.get(bookingId);
        if (booking == null) {
            throw new IllegalArgumentException("Booking not found: " + bookingId);
        }
        return booking;
    }
}
