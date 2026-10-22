package concurrentbooking;

public class StatusMonitor implements Runnable {

    private volatile boolean running = true;
    private final long intervalMillis;

    public StatusMonitor(long intervalMillis) {
        if (intervalMillis <= 0) {
            throw new IllegalArgumentException(
                    "Monitoring interval must be positive."
            );
        }

        this.intervalMillis = intervalMillis;
    }

    @Override
    public void run() {
        while (running) {

            System.out.println(
                    "[Monitor] Booking service is running."
            );

            try {
                Thread.sleep(intervalMillis);

            } catch (InterruptedException e) {

                Thread.currentThread().interrupt();
                break;
            }
        }

        System.out.println(
                "[Monitor] Monitoring stopped."
        );
    }

    public void stopMonitoring() {
        running = false;
    }
}
