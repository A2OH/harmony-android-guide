package android.animation;

/**
 * This evaluator can be used to perform type interpolation between int values.
 */
public class IntEvaluator implements TypeEvaluator<Integer> {

    /**
     * This function returns the result of linearly interpolating the start and end values,
     * with fraction representing the proportion between the start and end values.
     *
     * @param fraction   The fraction from the starting to the ending values.
     * @param startValue The start value; should not be null.
     * @param endValue   The end value; should not be null.
     * @return A linear interpolation between the start and end values, given the fraction parameter.
     */
    @Override
    public Integer evaluate(float fraction, Integer startValue, Integer endValue) {
        return (int) (startValue + fraction * (endValue - startValue));
    }
}
