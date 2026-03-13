package android.media;

public final class AudioTimestamp {
    public static final int TIMEBASE_BOOTTIME = 0;
    public static final int TIMEBASE_MONOTONIC = 0;
    public int framePosition = 0;
    public int nanoTime = 0;

    public AudioTimestamp() {}

}
