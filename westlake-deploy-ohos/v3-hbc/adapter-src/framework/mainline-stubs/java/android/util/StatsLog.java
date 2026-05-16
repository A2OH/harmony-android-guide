// B.18 (2026-04-28) — hand-augmented mainline stub for StatsLog.
// FrameworkStatsLog.write() calls StatsLog.write(StatsEvent) — no-op safe.
package android.util;

public class StatsLog {
    public StatsLog() {}

    /** No-op stub: HelloWorld doesn't need stats logging. */
    public static void write(StatsEvent statsEvent) {}

    /** No-op stub. */
    public static void writeRaw(byte[] buffer, int size) {}

    /** No-op stub. */
    public static int logEvent(int tag) { return 0; }

    /** No-op stub. */
    public static int logStart(int tag) { return 0; }

    /** No-op stub. */
    public static int logStop(int tag) { return 0; }
}
