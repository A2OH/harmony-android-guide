package android.media;

/**
 * Android-compatible MediaTimestamp shim. Stub for media clock timestamps.
 */
public class MediaTimestamp {

    public static final MediaTimestamp TIMESTAMP_UNKNOWN = new MediaTimestamp(-1, -1, 0.0f);

    private final long mAnchorMediaTimeUs;
    private final long mAnchorSystemNanoTime;
    private final float mMediaClockRate;

    public MediaTimestamp(long anchorMediaTimeUs, long anchorSystemNanoTime, float mediaClockRate) {
        mAnchorMediaTimeUs = anchorMediaTimeUs;
        mAnchorSystemNanoTime = anchorSystemNanoTime;
        mMediaClockRate = mediaClockRate;
    }

    public MediaTimestamp() {
        this(0, 0, 1.0f);
    }

    public long getAnchorMediaTimeUs() { return mAnchorMediaTimeUs; }

    public long getAnchorSytemNanoTime() { return mAnchorSystemNanoTime; }

    public float getMediaClockRate() { return mMediaClockRate; }
}
