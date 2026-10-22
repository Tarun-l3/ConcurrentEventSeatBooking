package concurrentbooking;

public class Event {

    private final String eventName;
    private final int totalSeats;

    public Event(String eventName, int totalSeats) {
        if (eventName == null || eventName.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Event name cannot be empty."
            );
        }

        if (totalSeats <= 0) {
            throw new IllegalArgumentException(
                    "Total seats must be greater than zero."
            );
        }

        this.eventName = eventName;
        this.totalSeats = totalSeats;
    }

    public String getEventName() {
        return eventName;
    }

    public int getTotalSeats() {
        return totalSeats;
    }
}
