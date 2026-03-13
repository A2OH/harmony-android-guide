package android.view.animation;

/**
 * Android shim: android.view.animation.Interpolator
 *
 * An interpolator defines the rate of change of an animation. This allows the
 * basic animation effects (alpha, scale, translate, rotate) to be accelerated,
 * decelerated, repeated, etc.
 *
 * Because android.animation.TimeInterpolator is not available in this shim set,
 * this interface is declared standalone but provides the same contract.
 */
public interface Interpolator {

    /**
     * Maps a value representing the elapsed fraction of an animation's duration
     * to a value that represents the interpolated fraction. This interpolated
     * value is then multiplied by the change in value of an animation to derive
     * the animated value at the current elapsed animation time.
     *
     * @param input a value between 0 and 1.0 indicating our current point in
     *              the animation where 0 represents the start and 1.0 represents
     *              the end
     * @return the interpolation value. This value can be more than 1.0 for
     *         interpolators which overshoot their targets, or less than 0 for
     *         interpolators that undershoot their targets.
     */
    float getInterpolation(float input);
}
