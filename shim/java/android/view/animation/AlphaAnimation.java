package android.view.animation;

/**
 * Android-compatible AlphaAnimation shim. Animates the alpha (opacity) of a
 * view from {@code fromAlpha} to {@code toAlpha}.
 */
public class AlphaAnimation extends Animation {

    private final float mFromAlpha;
    private final float mToAlpha;

    /**
     * @param fromAlpha starting alpha (0.0 = fully transparent, 1.0 = fully opaque)
     * @param toAlpha   ending alpha
     */
    public AlphaAnimation(float fromAlpha, float toAlpha) {
        mFromAlpha = fromAlpha;
        mToAlpha = toAlpha;
    }

    public float getFromAlpha() { return mFromAlpha; }

    public float getToAlpha() { return mToAlpha; }

    @Override
    protected void applyTransformation(float interpolatedTime, Object t) {
        // Interpolated alpha = fromAlpha + (toAlpha - fromAlpha) * interpolatedTime
        float alpha = mFromAlpha + (mToAlpha - mFromAlpha) * interpolatedTime;
        // Stub: in a real runtime, alpha would be applied to the transformation.
    }
}
