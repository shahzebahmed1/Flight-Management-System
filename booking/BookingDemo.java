import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;

public class BookingDemo {
    public static void main(String[] args) {
        Flight flight = new Flight(
                "FL123",
                "NYC",
                "LAX",
                LocalDateTime.of(2025, 1, 10, 9, 30),
                100,
                new BigDecimal("199.99"));

        Customer customer = new Customer(
                "C001",
                "Alice Smith",
                "alice@example.com");

        BookingService bookingService = new BookingService(Arrays.asList(flight), Arrays.asList(customer));

        // Create a new booking
        Booking booking = bookingService.createBooking("FL123", "C001", 2);
        System.out.println(bookingService.generateConfirmation(booking.getId()));

        // Modify the booking
        bookingService.modifyBooking(booking.getId(), 3);
        System.out.println("After modification:");
        System.out.println(bookingService.generateConfirmation(booking.getId()));

        // Cancel the booking
        bookingService.cancelBooking(booking.getId());
        System.out.println("After cancellation:");
        System.out.println(bookingService.generateConfirmation(booking.getId()));
    }
}
