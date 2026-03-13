package android.animation;

/**
 * Interface for use with the ValueAnimator.setEvaluator(TypeEvaluator) method.
 * Evaluators allow developers to create animations on arbitrary property types,
 * by allowing them to supply custom evaluators for types that are not
 * automatically understood and used by the animation system.
 *
 * @param <Object> The type of value being evaluated.
 */
public interface TypeEvaluator<Object> {
    /**
     * This function returns the result of linearly interpolating the start and end values,
     * with fraction representing the proportion between the start and end values.
     *
     * @param fraction   The fraction from the starting to the ending values.
     * @param startValue The start value.
     * @param endValue   The end value.
     * @return A linear interpolation between the start and end values, given the fraction.
     */
    Object evaluate(float fraction, Object startValue, Object endValue);
}
