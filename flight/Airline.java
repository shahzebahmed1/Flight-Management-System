public class Airline {
    private final int airlineId;
    private final String airlineName;
    private final String iataCode;

    public Airline(int airlineId, String airlineName, String iataCode) {
        this.airlineId = airlineId;
        this.airlineName = airlineName;
        this.iataCode = iataCode;
    }

    public int getAirlineId() {
        return airlineId;
    }

    public String getAirlineName() {
        return airlineName;
    }

    public String getIataCode() {
        return iataCode;
    }

    @Override
    public String toString() {
        return airlineName + " (" + iataCode + ")";
    }
}
