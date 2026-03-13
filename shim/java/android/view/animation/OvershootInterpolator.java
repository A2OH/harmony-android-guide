package android.view.animation;

import android.animation.TimeInterpolator;

/**
 * Android-compatible OvershootInterpolator shim. The animation flings past its
 * end value, then returns. Higher tension means a larger overshoot.
 * Formula (Hermite overshoot): t * t * ((tension + 1) * t - tension).
 */
public class OvershootInterpolator implements TimeInterpolator {

    private static final float DEFAULT_TENSION = 2.0f;

    private final float mTension;

    /** Creates an OvershootInterpolator with tension 2.0. */
    public OvershootInterpolator() {
        this(DEFAULT_TENSION);
    }

    /**
     * @param tension how much the animation overshoots. When tension is 0,
     *                there is no overshoot. Higher values increase overshoot.
     */
    public OvershootInterpolator(float tension) {
        mTension = tension;
    }

    public float getTension() { return mTension; }

    @Override
    public float getInterpolation(float input) {
        // Shift input to [0..1]: apply cubic overshoot formula.
        input -= 1.0f;
        return input * input * ((mTension + 1) * input + mTension) + 1.0f;
    }
}
