package android.view.animation;
import android.animation.TimeInterpolator;
import android.animation.TimeInterpolator;

import android.animation.TimeInterpolator;

/**
 * Android-compatible BounceInterpolator shim. The animation bounces at the
 * end, like a ball dropped on the floor. Implements the standard Android
 * piecewise bounce formula.
 */
public class BounceInterpolator implements TimeInterpolator {

    public BounceInterpolator() {}

    private static float bounce(float t) {
        return t * t * 8.0f;
    }

    @Override
    public float getInterpolation(float input) {
        // Adapted from Android AOSP BounceInterpolator.
        input *= 1.1226f;
        if (input < 0.3535f) {
            return bounce(input);
        } else if (input < 0.7408f) {
            input -= 0.54719f;
            return bounce(input) + 0.7f;
        } else if (input < 0.9f) {
            input -= 0.8526f;
            return bounce(input) + 0.9f;
        } else {
            input -= 0.9643f;
            return bounce(input) + 0.95f;
        }
    }
}
