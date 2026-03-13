package android.animation;

/**
 * Android-compatible ArgbEvaluator shim.
 * Evaluates between two ARGB color integers by linearly interpolating each
 * channel (alpha, red, green, blue) independently.
 */
public class ArgbEvaluator implements TypeEvaluator {

    private static final ArgbEvaluator sInstance = new ArgbEvaluator();

    /**
     * Returns a shared ArgbEvaluator instance.
     */
    public static ArgbEvaluator getInstance() {
        return sInstance;
    }

    /**
     * Interpolates between two ARGB color values.
     *
     * @param fraction   The fraction from the starting to the ending color.
     * @param startValue The start color as an Integer ARGB value.
     * @param endValue   The end color as an Integer ARGB value.
     * @return An Integer holding the interpolated ARGB color.
     */
    @Override
    public Object evaluate(float fraction, Object startValue, Object endValue) {
        int startInt = (Integer) startValue;
        int endInt   = (Integer) endValue;

        float startA = ((startInt >> 24) & 0xff) / 255f;
        float startR = ((startInt >> 16) & 0xff) / 255f;
        float startG = ((startInt >>  8) & 0xff) / 255f;
        float startB = ( startInt        & 0xff) / 255f;

        float endA = ((endInt >> 24) & 0xff) / 255f;
        float endR = ((endInt >> 16) & 0xff) / 255f;
        float endG = ((endInt >>  8) & 0xff) / 255f;
        float endB = ( endInt        & 0xff) / 255f;

        // Interpolate in linear light (gamma approximation skipped for shim simplicity)
        int a = Math.round((startA + fraction * (endA - startA)) * 255f);
        int r = Math.round((startR + fraction * (endR - startR)) * 255f);
        int g = Math.round((startG + fraction * (endG - startG)) * 255f);
        int b = Math.round((startB + fraction * (endB - startB)) * 255f);

        return (a << 24) | (r << 16) | (g << 8) | b;
    }
}
