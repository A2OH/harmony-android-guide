package android.app.usage;

/**
 * Android-compatible NetworkStats stub (API 23+).
 * Provides the Bucket inner class used as a return type by NetworkStatsManager.
 * All data accessors return zero / empty defaults.
 */
public class NetworkStats implements AutoCloseable {

    /** Returns {@code false} — no data rows in this stub. */
    public boolean hasNextBucket() {
        return false;
    }

    /** No-op — returns {@code false} since there are no buckets. */
    public boolean getNextBucket(Bucket bucket) {
        return false;
    }

    @Override
    public void close() {
        // no-op
    }

    /**
     * A single data bucket for network statistics.
     */
    public static class Bucket {

        public static final int STATE_ALL      = -1;
        public static final int STATE_DEFAULT  = 0x0001;
        public static final int STATE_FOREGROUND = 0x0002;

        public static final int METERED_ALL    = -1;
        public static final int METERED_NO     = 0x0001;
        public static final int METERED_YES    = 0x0002;

        public static final int ROAMING_ALL    = -1;
        public static final int ROAMING_NO     = 0x0001;
        public static final int ROAMING_YES    = 0x0002;

        public static final int DEFAULT_NETWORK_ALL = -1;
        public static final int DEFAULT_NETWORK_NO  = 0x0001;
        public static final int DEFAULT_NETWORK_YES = 0x0002;

        public static final int TAG_NONE = 0;

        public int getUid()          { return -1; }
        public int getState()        { return STATE_ALL; }
        public int getMetered()      { return METERED_ALL; }
        public int getRoaming()      { return ROAMING_ALL; }
        public int getDefaultNetworkStatus() { return DEFAULT_NETWORK_ALL; }
        public int getTag()          { return TAG_NONE; }
        public long getStartTimeStamp() { return 0L; }
        public long getEndTimeStamp()   { return 0L; }
        public long getRxBytes()     { return 0L; }
        public long getTxBytes()     { return 0L; }
        public long getRxPackets()   { return 0L; }
        public long getTxPackets()   { return 0L; }
    }
}
