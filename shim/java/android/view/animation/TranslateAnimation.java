package android.view.animation;
import android.renderscript.Type;
import android.renderscript.Type;

/**
 * Android-compatible TranslateAnimation shim. Animates the position of a view
 * along X and/or Y axes.
 */
public class TranslateAnimation extends Animation {

    public static final int ABSOLUTE = 0;
    public static final int RELATIVE_TO_SELF = 1;
    public static final int RELATIVE_TO_PARENT = 2;

    private final int mFromXType;
    private final float mFromXValue;
    private final int mToXType;
    private final float mToXValue;
    private final int mFromYType;
    private final float mFromYValue;
    private final int mToYType;
    private final float mToYValue;

    /**
     * Absolute pixel translation constructor.
     *
     * @param fromXDelta starting X offset in pixels
     * @param toXDelta   ending X offset in pixels
     * @param fromYDelta starting Y offset in pixels
     * @param toYDelta   ending Y offset in pixels
     */
    public TranslateAnimation(float fromXDelta, float toXDelta,
                              float fromYDelta, float toYDelta) {
        this(ABSOLUTE, fromXDelta, ABSOLUTE, toXDelta,
             ABSOLUTE, fromYDelta, ABSOLUTE, toYDelta);
    }

    /**
     * Type-qualified translation constructor.
     *
     * @param fromXType  one of {@link #ABSOLUTE}, {@link #RELATIVE_TO_SELF}, or
     *                   {@link #RELATIVE_TO_PARENT}
     * @param fromXValue X start value (interpretation depends on fromXType)
     * @param toXType    type for X end value
     * @param toXValue   X end value
     * @param fromYType  type for Y start value
     * @param fromYValue Y start value
     * @param toYType    type for Y end value
     * @param toYValue   Y end value
     */
    public TranslateAnimation(int fromXType, float fromXValue, int toXType, float toXValue,
                              int fromYType, float fromYValue, int toYType, float toYValue) {
        mFromXType  = fromXType;
        mFromXValue = fromXValue;
        mToXType    = toXType;
        mToXValue   = toXValue;
        mFromYType  = fromYType;
        mFromYValue = fromYValue;
        mToYType    = toYType;
        mToYValue   = toYValue;
    }

    public int getFromXType()    { return mFromXType; }
    public float getFromXValue() { return mFromXValue; }
    public int getToXType()      { return mToXType; }
    public float getToXValue()   { return mToXValue; }
    public int getFromYType()    { return mFromYType; }
    public float getFromYValue() { return mFromYValue; }
    public int getToYType()      { return mToYType; }
    public float getToYValue()   { return mToYValue; }
}
