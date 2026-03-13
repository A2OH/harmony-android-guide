package android.view.animation;

/**
 * Android-compatible ScaleAnimation shim. Animates the scale of a view from a
 * starting size to an ending size, optionally around a pivot point.
 */
public class ScaleAnimation extends Animation {

    private final float mFromX;
    private final float mToX;
    private final float mFromY;
    private final float mToY;
    private final int mPivotXType;
    private final float mPivotXValue;
    private final int mPivotYType;
    private final float mPivotYValue;

    /**
     * Scales around the top-left corner (0, 0).
     *
     * @param fromX starting X scale factor (1.0 = normal)
     * @param toX   ending X scale factor
     * @param fromY starting Y scale factor
     * @param toY   ending Y scale factor
     */
    public ScaleAnimation(float fromX, float toX, float fromY, float toY) {
        this(fromX, toX, fromY, toY, 0f, 0f);
    }

    /**
     * Scales around an absolute pixel pivot point.
     *
     * @param fromX  starting X scale factor
     * @param toX    ending X scale factor
     * @param fromY  starting Y scale factor
     * @param toY    ending Y scale factor
     * @param pivotX X coordinate of the pivot (absolute pixels)
     * @param pivotY Y coordinate of the pivot (absolute pixels)
     */
    public ScaleAnimation(float fromX, float toX, float fromY, float toY,
                          float pivotX, float pivotY) {
        this(fromX, toX, fromY, toY, ABSOLUTE, pivotX, ABSOLUTE, pivotY);
    }

    /**
     * Scales around a type-qualified pivot point.
     *
     * @param fromX      starting X scale factor
     * @param toX        ending X scale factor
     * @param fromY      starting Y scale factor
     * @param toY        ending Y scale factor
     * @param pivotXType one of {@link #ABSOLUTE}, {@link #RELATIVE_TO_SELF},
     *                   or {@link #RELATIVE_TO_PARENT}
     * @param pivotXValue X pivot value
     * @param pivotYType  type for Y pivot
     * @param pivotYValue Y pivot value
     */
    public ScaleAnimation(float fromX, float toX, float fromY, float toY,
                          int pivotXType, float pivotXValue,
                          int pivotYType, float pivotYValue) {
        mFromX      = fromX;
        mToX        = toX;
        mFromY      = fromY;
        mToY        = toY;
        mPivotXType  = pivotXType;
        mPivotXValue = pivotXValue;
        mPivotYType  = pivotYType;
        mPivotYValue = pivotYValue;
    }

    public float getFromX()       { return mFromX; }
    public float getToX()         { return mToX; }
    public float getFromY()       { return mFromY; }
    public float getToY()         { return mToY; }
    public int getPivotXType()    { return mPivotXType; }
    public float getPivotXValue() { return mPivotXValue; }
    public int getPivotYType()    { return mPivotYType; }
    public float getPivotYValue() { return mPivotYValue; }
}
