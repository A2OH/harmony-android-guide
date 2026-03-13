package android.view.animation;

/**
 * Android-compatible RotateAnimation shim. Animates the rotation of a view
 * around an optional pivot point.
 */
public class RotateAnimation extends Animation {

    private final float mFromDegrees;
    private final float mToDegrees;
    private final int mPivotXType;
    private final float mPivotXValue;
    private final int mPivotYType;
    private final float mPivotYValue;

    /**
     * Rotates around the top-left corner (0,0).
     *
     * @param fromDegrees starting rotation in degrees
     * @param toDegrees   ending rotation in degrees
     */
    public RotateAnimation(float fromDegrees, float toDegrees) {
        this(fromDegrees, toDegrees, 0f, 0f);
    }

    /**
     * Rotates around an absolute pixel pivot point.
     *
     * @param fromDegrees starting rotation in degrees
     * @param toDegrees   ending rotation in degrees
     * @param pivotX      X coordinate of the pivot (absolute pixels)
     * @param pivotY      Y coordinate of the pivot (absolute pixels)
     */
    public RotateAnimation(float fromDegrees, float toDegrees,
                           float pivotX, float pivotY) {
        this(fromDegrees, toDegrees,
             ABSOLUTE, pivotX, ABSOLUTE, pivotY);
    }

    /**
     * Rotates around a type-qualified pivot point.
     *
     * @param fromDegrees starting rotation in degrees
     * @param toDegrees   ending rotation in degrees
     * @param pivotXType  one of {@link #ABSOLUTE}, {@link #RELATIVE_TO_SELF},
     *                    or {@link #RELATIVE_TO_PARENT}
     * @param pivotXValue X pivot value (interpretation depends on type)
     * @param pivotYType  type for Y pivot
     * @param pivotYValue Y pivot value
     */
    public RotateAnimation(float fromDegrees, float toDegrees,
                           int pivotXType, float pivotXValue,
                           int pivotYType, float pivotYValue) {
        mFromDegrees = fromDegrees;
        mToDegrees   = toDegrees;
        mPivotXType  = pivotXType;
        mPivotXValue = pivotXValue;
        mPivotYType  = pivotYType;
        mPivotYValue = pivotYValue;
    }

    public float getFromDegrees()  { return mFromDegrees; }
    public float getToDegrees()    { return mToDegrees; }
    public int getPivotXType()     { return mPivotXType; }
    public float getPivotXValue()  { return mPivotXValue; }
    public int getPivotYType()     { return mPivotYType; }
    public float getPivotYValue()  { return mPivotYValue; }
}
