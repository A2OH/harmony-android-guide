package android.view.animation;

import android.animation.TimeInterpolator;

/**
 * Android-compatible AccelerateDecelerateInterpolator shim. The rate of change
 * starts and ends slowly, accelerating through the middle. Uses a cosine
 * sigmoid: (cos(π + input * π) / 2.0) + 0.5.
 */
public class AccelerateDecelerateInterpolator implements TimeInterpolator {

    public AccelerateDecelerateInterpolator() {}

    @Override
    public float getInterpolation(float input) {
        return (float) (Math.cos((input + 1) * Math.PI) / 2.0f) + 0.5f;
    }
}
