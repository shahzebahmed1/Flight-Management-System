import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Simple in-memory flight entity with seat tracking.
 */
public class Flight {
    private final String id;
    private final String origin;
    private final String destination;
    private final LocalDateTime departureTime;
    private final int capacity;
    private int availableSeats;
    private final BigDecimal price;

    public Flight(String id,
                  String origin,
                  String destination,
                  LocalDateTime departureTime,
                  int capacity,
                  BigDecimal price) {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("id is required");
        if (origin == null || origin.isBlank()) throw new IllegalArgumentException("origin is required");
        if (destination == null || destination.isBlank()) throw new IllegalArgumentException("destination is required");
        if (departureTime == null) throw new IllegalArgumentException("departureTime is required");
        if (capacity <= 0) throw new IllegalArgumentException("capacity must be positive");
        if (price == null) throw new IllegalArgumentException("price is required");
        this.id = id;
        this.origin = origin;
        this.destination = destination;
        this.departureTime = departureTime;
        this.capacity = capacity;
        this.availableSeats = capacity;
        this.price = price;
    }

    public String getId() {
        return id;
    }

    public String getOrigin() {
        return origin;
    }

    public String getDestination() {
        return destination;
    }

    public LocalDateTime getDepartureTime() {
        return departureTime;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getAvailableSeats() {
        return availableSeats;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void bookSeats(int count) {
        if (count <= 0) {
            throw new IllegalArgumentException("Seat count must be positive.");
        }
        if (availableSeats < count) {
            throw new IllegalStateException("Not enough seats available for flight " + id);
        }
        availableSeats -= count;
    }

    public void releaseSeats(int count) {
        if (count <= 0) {
            throw new IllegalArgumentException("Seat count must be positive.");
        }
        if (availableSeats + count > capacity) {
            throw new IllegalStateException("Releasing more seats than flight capacity.");
        }
        availableSeats += count;
    }
}
