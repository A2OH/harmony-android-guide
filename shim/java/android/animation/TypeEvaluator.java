package android.animation;

/**
 * Android-compatible TypeEvaluator shim.
 * Calculates an animated value given start and end values and a fraction.
 * The generic parameter T is erased to Object for shim compatibility.
 */
public interface TypeEvaluator {
    /**
     * Returns the interpolated value for a given fraction between start and end.
     *
     * @param fraction   The fraction from the starting to the ending values.
     * @param startValue The start value.
     * @param endValue   The end value.
     * @return A linear interpolation between the start and end values, given the fraction parameter.
     */
    Object evaluate(float fraction, Object startValue, Object endValue);
}
