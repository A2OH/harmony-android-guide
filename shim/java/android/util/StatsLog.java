package android.util;

/**
 * Android-compatible StatsLog shim. Stub — logs events to stdout.
 */
public class StatsLog {

    public static boolean logEvent(int atomId) {
        System.out.println("[StatsLog] event: " + atomId);
        return true;
    }

    public static boolean logStart(int atomId) {
        System.out.println("[StatsLog] start: " + atomId);
        return true;
    }

    public static boolean logStop(int atomId) {
        System.out.println("[StatsLog] stop: " + atomId);
        return true;
    }
}
