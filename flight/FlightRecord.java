import java.sql.Timestamp;

public class FlightRecord {

    private final int flightId;
    private final String flightNumber;
    private final int airlineId;
    private final String airlineName;
    private final String origin;
    private final String destination;
    private final Timestamp departureTime;
    private final Timestamp arrivalTime;
    private final double price;
    private final int aircraftId;
    private final String aircraftModel;

    public FlightRecord(int flightId, String flightNumber,
                        int airlineId, String airlineName,
                        String origin, String destination,
                        Timestamp departureTime, Timestamp arrivalTime,
                        double price,
                        int aircraftId, String aircraftModel) {
        this.flightId = flightId;
        this.flightNumber = flightNumber;
        this.airlineId = airlineId;
        this.airlineName = airlineName;
        this.origin = origin;
        this.destination = destination;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.price = price;
        this.aircraftId = aircraftId;
        this.aircraftModel = aircraftModel;
    }

    public int getFlightId() {
        return flightId;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public int getAirlineId() {
        return airlineId;
    }

    public String getAirlineName() {
        return airlineName;
    }

    public String getOrigin() {
        return origin;
    }

    public String getDestination() {
        return destination;
    }

    public Timestamp getDepartureTime() {
        return departureTime;
    }

    public Timestamp getArrivalTime() {
        return arrivalTime;
    }

    public double getPrice() {
        return price;
    }

    public int getAircraftId() {
        return aircraftId;
    }

    public String getAircraftModel() {
        return aircraftModel;
    }
}
