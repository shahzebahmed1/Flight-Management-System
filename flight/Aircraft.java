public class Aircraft {
    private final int aircraftId;
    private final String model;
    private final int capacity;

    public Aircraft(int aircraftId, String model, int capacity) {
        this.aircraftId = aircraftId;
        this.model = model;
        this.capacity = capacity;
    }

    public int getAircraftId() {
        return aircraftId;
    }

    public String getModel() {
        return model;
    }

    public int getCapacity() {
        return capacity;
    }

    @Override
    public String toString() {
        return model + " (" + capacity + " seats)";
    }
}
