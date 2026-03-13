package android.view.animation;
import android.animation.TimeInterpolator;
import android.animation.TimeInterpolator;

import android.animation.TimeInterpolator;

/**
 * Android-compatible AccelerateInterpolator shim. The rate of change starts
 * slowly and then accelerates. Formula: t^(2 * factor).
 */
public class AccelerateInterpolator implements TimeInterpolator {

    private final float mFactor;
    private final double mDoubleFactor;

    /** Creates an AccelerateInterpolator with factor 1.0 (standard accelerate). */
    public AccelerateInterpolator() {
        this(1.0f);
    }

    /**
     * @param factor degree to which the animation should be eased. Setting
     *               factor to 1.0f produces a y=x^2 curve; increasing factor
     *               makes the initial animation more sudden.
     */
    public AccelerateInterpolator(float factor) {
        mFactor = factor;
        mDoubleFactor = 2.0 * factor;
    }

    public float getFactor() { return mFactor; }

    @Override
    public float getInterpolation(float input) {
        if (mFactor == 1.0f) {
            return input * input;
        }
        return (float) Math.pow(input, mDoubleFactor);
    }
}
