package concurrentbooking;

public class BookingTask implements Runnable {

    private final BookingService bookingService;
    private final String customerName;
    private final int seatNumber;

    public BookingTask(
            BookingService bookingService,
            String customerName,
            int seatNumber
    ) {
        this.bookingService = bookingService;
        this.customerName = customerName;
        this.seatNumber = seatNumber;
    }

    @Override
    public void run() {
        try {
            Booking booking = bookingService.bookSeat(
                    customerName,
                    seatNumber
            );

            System.out.println(
                    "SUCCESS: " + booking
            );

        } catch (InvalidBookingException e) {

            System.out.println(
                    "BOOKING FAILED for " +
                            customerName + ": " +
                            e.getMessage()
            );

        } finally {
            System.out.println(
                    "Booking attempt completed for " +
                            customerName
            );
        }
    }
}

