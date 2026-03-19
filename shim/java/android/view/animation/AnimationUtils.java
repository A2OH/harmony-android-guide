package android.view.animation;
import android.animation.TimeInterpolator;
import android.os.SystemClock;
import android.animation.TimeInterpolator;
import android.os.SystemClock;

import android.animation.TimeInterpolator;

/**
 * Android-compatible AnimationUtils shim. Provides static factory helpers
 * for loading animations and interpolators from resource ids.
 */
public class AnimationUtils {

    private AnimationUtils() {}

    /**
     * Loads an animation from a resource id. Returns a no-op Animation stub.
     *
     * @param context ignored in this shim
     * @param id      resource id (ignored)
     * @return a stub Animation instance
     */
    public static Animation loadAnimation(Object context, int id) {
        return new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Object t) {}
        };
    }

    /**
     * Loads a layout animation controller from a resource id.
     */
    public static LayoutAnimationController loadLayoutAnimation(Object context, int id) {
        return new LayoutAnimationController();
    }

    /**
     * Loads an interpolator from a resource id. Returns a linear interpolator stub.
     *
     * @param context ignored in this shim
     * @param id      resource id (ignored)
     * @return a stub TimeInterpolator that returns input unchanged
     */
    public static Interpolator loadInterpolator(Object context, int id) {
        return new LinearInterpolator();
    }

    /**
     * Returns the current animation time in milliseconds. Equivalent to
     * {@link android.os.SystemClock#uptimeMillis()}.
     */
    public static long currentAnimationTimeMillis() {
        return System.nanoTime() / 1_000_000L;
    }
}
