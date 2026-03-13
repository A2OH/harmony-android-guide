package android.view.animation;

/**
 * Shim: android.view.animation.TimeInterpolator
 *
 * Maps a linear 0–1 animation fraction to an arbitrary easing curve.
 * Implementations include AccelerateInterpolator, DecelerateInterpolator, etc.
 */
public interface TimeInterpolator {
    /**
     * Maps a value representing elapsed fraction of an animation to a fraction
     * applied to the change in value being animated.
     *
     * @param input  elapsed fraction [0.0, 1.0]
     * @return       interpolated fraction (may go outside [0,1] for overshoot)
     */
    float getInterpolation(float input);
}
