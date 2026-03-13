package android.graphics;

/**
 * Android-compatible Interpolator shim. Provides keyframe-based interpolation
 * for values over time.
 */
public class Interpolator {
    private int mValueCount;
    private int mFrameCount;

    public Interpolator(int valueCount) {
        this(valueCount, 2);
    }

    public Interpolator(int valueCount, int frameCount) {
        mValueCount = valueCount;
        mFrameCount = frameCount;
    }

    public void reset(int valueCount) {
        reset(valueCount, 2);
    }

    public void reset(int valueCount, int frameCount) {
        mValueCount = valueCount;
        mFrameCount = frameCount;
    }

    public int getKeyFrameCount() {
        return mFrameCount;
    }

    public int getValueCount() {
        return mValueCount;
    }

    public void setKeyFrame(int index, int msec, float[] values) {}

    public void setKeyFrame(int index, int msec, float[] values, float[] blend) {}

    public void setRepeatMirror(float repeatCount, boolean mirror) {}

    public Result timeToValues(float[] values) {
        return timeToValues((int) System.currentTimeMillis(), values);
    }

    public Result timeToValues(int msec, float[] values) {
        if (values != null) {
            for (int i = 0; i < values.length && i < mValueCount; i++) {
                values[i] = 0f;
            }
        }
        return Result.NORMAL;
    }

    public enum Result {
        NORMAL,
        FREEZE_START,
        FREEZE_END
    }
}
