package android.view.animation;
import android.animation.TimeInterpolator;
import android.animation.TimeInterpolator;

import android.animation.TimeInterpolator;

/**
 * Android-compatible DecelerateInterpolator shim. The rate of change starts
 * quickly and then decelerates. Formula: 1 - (1 - t)^(2 * factor).
 */
public class DecelerateInterpolator implements TimeInterpolator {

    private final float mFactor;

    /** Creates a DecelerateInterpolator with factor 1.0 (standard decelerate). */
    public DecelerateInterpolator() {
        this(1.0f);
    }

    /**
     * @param factor degree to which the animation should be eased. Setting
     *               factor to 1.0f produces a y=1-(1-x)^2 curve; increasing
     *               factor makes the initial acceleration more abrupt.
     */
    public DecelerateInterpolator(float factor) {
        mFactor = factor;
    }

    public float getFactor() { return mFactor; }

    @Override
    public float getInterpolation(float input) {
        if (mFactor == 1.0f) {
            return 1.0f - (1.0f - input) * (1.0f - input);
        }
        return (float) (1.0f - Math.pow(1.0f - input, 2.0 * mFactor));
    }
}
