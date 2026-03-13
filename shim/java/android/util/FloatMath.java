package android.util;

/**
 * Android-compatible FloatMath shim.
 * Math routines similar to those found in java.lang.Math.
 *
 * @deprecated Use {@link java.lang.Math} directly. Accuracy may differ.
 */
@Deprecated
public final class FloatMath {

    private FloatMath() {}

    /**
     * Returns the float conversion of the most positive (closest to positive
     * infinity) integer value which is less than the argument.
     */
    public static float floor(float value) {
        return (float) Math.floor(value);
    }

    /**
     * Returns the float conversion of the most negative (closest to negative
     * infinity) integer value which is greater than the argument.
     */
    public static float ceil(float value) {
        return (float) Math.ceil(value);
    }

    /**
     * Returns the closest float approximation of the sine of the argument.
     *
     * @param angle the angle in radians
     */
    public static float sin(float angle) {
        return (float) Math.sin(angle);
    }

    /**
     * Returns the closest float approximation of the cosine of the argument.
     *
     * @param angle the angle in radians
     */
    public static float cos(float angle) {
        return (float) Math.cos(angle);
    }

    /**
     * Returns the closest float approximation of the square root of the argument.
     */
    public static float sqrt(float value) {
        return (float) Math.sqrt(value);
    }

    /**
     * Returns the closest float approximation of the hypot of the arguments.
     */
    public static float hypot(float x, float y) {
        return (float) Math.hypot(x, y);
    }

    /**
     * Returns the closest float approximation of the power of the first argument
     * to the second argument.
     */
    public static float pow(float x, float y) {
        return (float) Math.pow(x, y);
    }
}
