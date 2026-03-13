package android.os;

/**
 * Android-compatible SystemClock shim. Pure Java using System time APIs.
 */
public class SystemClock {

    public static long uptimeMillis() {
        return System.nanoTime() / 1_000_000;
    }

    public static long elapsedRealtime() {
        return System.nanoTime() / 1_000_000;
    }

    public static long elapsedRealtimeNanos() {
        return System.nanoTime();
    }

    public static long currentThreadTimeMillis() {
        return java.lang.management.ManagementFactory.getThreadMXBean()
            .getCurrentThreadCpuTime() / 1_000_000;
    }

    public static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            // restore interrupt flag
            Thread.currentThread().interrupt();
        }
    }

    public static boolean setCurrentTimeMillis(long millis) {
        // Stub — setting system time requires privileges
        return false;
    }
}
