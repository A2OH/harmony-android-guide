package android.view;

public class FrameStats {
    public static final int UNDEFINED_TIME_NANO = 0;

    public FrameStats() {}

    public long getEndTimeNano() { return 0L; }
    public int getFrameCount() { return 0; }
    public long getFramePresentedTimeNano(int p0) { return 0L; }
    public long getRefreshPeriodNano() { return 0L; }
    public long getStartTimeNano() { return 0L; }
}
