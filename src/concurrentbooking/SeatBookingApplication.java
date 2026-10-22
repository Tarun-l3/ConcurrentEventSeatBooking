package concurrentbooking;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class SeatBookingApplication {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        Event event = new Event(
                "Java Community Meetup", 10
        );

        BookingService service =
                new BookingService(event);

        System.out.println("======================================");
        System.out.println("   CONCURRENT EVENT SEAT BOOKING");
        System.out.println("======================================");
        System.out.println("Event: " + event.getEventName());
        System.out.println("Total seats: " + event.getTotalSeats());
        System.out.println(
                "Available seats: " + service.getAvailableSeats()
        );

        // Step 1: Collect all user input before starting threads.
        List<BookingTask> tasks = new ArrayList<>();

        System.out.print(
                "\nHow many booking requests do you want to enter? "
        );

        int numberOfRequests = scanner.nextInt();
        scanner.nextLine();

        if (numberOfRequests <= 0) {
            System.out.println(
                    "Number of requests must be greater than zero."
            );
            scanner.close();
            return;
        }

        for (int i = 1; i <= numberOfRequests; i++) {

            System.out.println("\nBooking Request " + i);

            System.out.print("Enter customer name: ");
            String customerName = scanner.nextLine();

            System.out.print("Enter seat number (1-10): ");
            int seatNumber = scanner.nextInt();
            scanner.nextLine();

            // Store the request; do not execute it yet.
            tasks.add(
                    new BookingTask(
                            service,
                            customerName,
                            seatNumber
                    )
            );
        }

        // All keyboard input is complete.
        scanner.close();

        // Step 2: Start the monitor after user input.
        StatusMonitor monitor = new StatusMonitor(2000);

        Thread monitorThread =
                new Thread(monitor, "status-monitor");

        monitorThread.start();

        // Step 3: Execute requests concurrently.
        ExecutorService executor =
                Executors.newFixedThreadPool(3);

        boolean allTasksCompleted = false;

        try {
            for (BookingTask task : tasks) {
                executor.submit(task);
            }

        } finally {
            executor.shutdown();
        }

        try {
            // Step 4: Wait for all submitted tasks.
            allTasksCompleted = executor.awaitTermination(
                    30, TimeUnit.SECONDS
            );

            if (!allTasksCompleted) {
                System.out.println(
                        "Booking tasks exceeded the time limit."
                );

                executor.shutdownNow();

                allTasksCompleted = executor.awaitTermination(
                        2, TimeUnit.SECONDS
                );
            }

        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();

        } finally {
            // Step 5: Stop and join the monitor thread.
            monitor.stopMonitoring();
            monitorThread.interrupt();

            try {
                monitorThread.join();

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        // Step 6: Print the final summary only when safe.
        if (allTasksCompleted) {

            System.out.println(
                    "\n========== FINAL SUMMARY =========="
            );

            System.out.println(
                    "Available seats: " +
                            service.getAvailableSeats()
            );

            System.out.println("\nSuccessful bookings:");

            for (Booking booking : service.getBookings()) {
                System.out.println(booking);
            }

            System.out.println(
                    "\n==================================="
            );

        } else {
            System.out.println(
                    "Not all tasks completed. " +
                            "Final summary may be incomplete."
            );
        }
    }
}