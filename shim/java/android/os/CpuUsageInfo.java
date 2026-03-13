package android.os;

/**
 * A2OH shim: CpuUsageInfo - per-CPU active and total time information.
 */
public final class CpuUsageInfo {

    private final long mActive;
    private final long mTotal;

    public CpuUsageInfo(long active, long total) {
        mActive = active;
        mTotal = total;
    }

    /** Returns the time (in milliseconds) that the CPU was active (non-idle). */
    public long getActive() {
        return mActive;
    }

    /** Returns the total elapsed time (in milliseconds) for the CPU. */
    public long getTotal() {
        return mTotal;
    }
}
