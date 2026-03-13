package android.net;

/**
 * Android-compatible TrafficStats shim. Returns mock/stub traffic counters.
 * On OpenHarmony the real counters are not directly accessible via this API;
 * all values are stubs that return UNSUPPORTED (-1) unless overridden.
 */
public class TrafficStats {

    /** Sentinel returned when a counter is unavailable. */
    public static final long UNSUPPORTED = -1L;

    // Mock accumulated byte counters (reset on JVM start, simulate uptime traffic)
    private static final long START_MS = System.currentTimeMillis();

    private static long mockBytes(long bitsPerSec) {
        long elapsedSec = (System.currentTimeMillis() - START_MS) / 1000L;
        return elapsedSec * bitsPerSec / 8L;
    }

    // --- Mobile (cellular) ---

    /** Total bytes transmitted over mobile interfaces. */
    public static long getMobileTxBytes() {
        return mockBytes(500_000L); // mock ~500 Kbps upload
    }

    /** Total bytes received over mobile interfaces. */
    public static long getMobileRxBytes() {
        return mockBytes(2_000_000L); // mock ~2 Mbps download
    }

    /** Total packets transmitted over mobile interfaces. */
    public static long getMobileTxPackets() {
        return getMobileTxBytes() / 1400L;
    }

    /** Total packets received over mobile interfaces. */
    public static long getMobileRxPackets() {
        return getMobileRxBytes() / 1400L;
    }

    // --- Total (all interfaces) ---

    /** Total bytes transmitted across all network interfaces. */
    public static long getTotalTxBytes() {
        return mockBytes(1_000_000L); // mock ~1 Mbps upload
    }

    /** Total bytes received across all network interfaces. */
    public static long getTotalRxBytes() {
        return mockBytes(8_000_000L); // mock ~8 Mbps download
    }

    /** Total packets transmitted across all network interfaces. */
    public static long getTotalTxPackets() {
        return getTotalTxBytes() / 1400L;
    }

    /** Total packets received across all network interfaces. */
    public static long getTotalRxPackets() {
        return getTotalRxBytes() / 1400L;
    }

    // --- Per-UID ---

    /** Bytes transmitted by the given UID. */
    public static long getUidTxBytes(int uid) {
        return UNSUPPORTED; // per-UID stats not available in this environment
    }

    /** Bytes received by the given UID. */
    public static long getUidRxBytes(int uid) {
        return UNSUPPORTED;
    }

    /** Packets transmitted by the given UID. */
    public static long getUidTxPackets(int uid) {
        return UNSUPPORTED;
    }

    /** Packets received by the given UID. */
    public static long getUidRxPackets(int uid) {
        return UNSUPPORTED;
    }

    // --- Thread tagging ---

    private static final ThreadLocal<Integer> threadTag = ThreadLocal.withInitial(() -> 0);

    /**
     * Tags the current thread's socket traffic with the given tag for accounting purposes.
     * This is a no-op stub; the tag is stored per-thread for symmetry.
     */
    public static void setThreadStatsTag(int tag) {
        threadTag.set(tag);
    }

    /** Returns the current thread's traffic tag. */
    public static int getThreadStatsTag() {
        return threadTag.get();
    }

    /** Clears the current thread's traffic tag. */
    public static void clearThreadStatsTag() {
        threadTag.set(0);
    }

    // Prevent instantiation
    private TrafficStats() {}
}
