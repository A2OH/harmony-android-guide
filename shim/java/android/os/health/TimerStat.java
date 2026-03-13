package android.os.health;

/**
 * A2OH shim: TimerStat - holds a count and total time for a timer.
 */
public final class TimerStat {

    private final int mCount;
    private final long mTime;

    public TimerStat(int count, long time) {
        mCount = count;
        mTime = time;
    }

    /** Returns the number of times the timer was acquired. */
    public int getCount() {
        return mCount;
    }

    /** Returns the total time held in milliseconds. */
    public long getTime() {
        return mTime;
    }
}
