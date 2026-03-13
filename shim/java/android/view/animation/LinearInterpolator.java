package android.view.animation;
import android.animation.TimeInterpolator;
import android.animation.TimeInterpolator;

import android.animation.TimeInterpolator;

/**
 * Android-compatible LinearInterpolator shim. The rate of change is constant
 * throughout the animation. Returns the input value unchanged.
 */
public class LinearInterpolator implements TimeInterpolator {

    public LinearInterpolator() {}

    @Override
    public float getInterpolation(float input) {
        return input;
    }
}
