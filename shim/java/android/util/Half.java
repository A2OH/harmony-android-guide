package android.util;
import java.lang.Comparable;

/**
 * Stub for android.util.Half — half-precision 16-bit IEEE 754 floating-point.
 */
public final class Half extends Number implements Comparable<Half> {
    public static final short POSITIVE_INFINITY = (short) 0x7c00;
    public static final short NEGATIVE_INFINITY = (short) 0xfc00;
    public static final short NaN = (short) 0x7e00;
    public static final short POSITIVE_ZERO = (short) 0x0000;
    public static final short NEGATIVE_ZERO = (short) 0x8000;

    public static final short MAX_VALUE = (short) 0x7bff;
    public static final short MIN_VALUE = (short) 0x0001;
    public static final short LOWEST_VALUE = (short) 0xfbff;
    public static final short MIN_NORMAL = (short) 0x0400;

    public static final int MAX_EXPONENT = 15;
    public static final int MIN_EXPONENT = -14;

    public static final int SIZE = 16;
    public static final int EPSILON = 0x1400;

    private final short mValue;

    public Half(short value) {
        mValue = value;
    }

    public Half(float value) {
        mValue = toHalf(value);
    }

    public Half(double value) {
        mValue = toHalf((float) value);
    }

    public Half(String value) {
        mValue = toHalf(Float.parseFloat(value));
    }

    public short halfValue() {
        return mValue;
    }

    @Override
    public float floatValue() {
        return toFloat(mValue);
    }

    @Override
    public double doubleValue() {
        return (double) toFloat(mValue);
    }

    @Override
    public int intValue() {
        return (int) toFloat(mValue);
    }

    @Override
    public long longValue() {
        return (long) toFloat(mValue);
    }

    @Override
    public short shortValue() {
        return (short) toFloat(mValue);
    }

    @Override
    public byte byteValue() {
        return (byte) toFloat(mValue);
    }

    public boolean isNaN() {
        return isNaN(mValue);
    }

    public static boolean isNaN(short h) {
        return (h & 0x7fff) > 0x7c00;
    }

    public static boolean isInfinite(short h) {
        return (h & 0x7fff) == 0x7c00;
    }

    public static boolean isNormalized(short h) {
        int exponent = (h & 0x7c00) >>> 10;
        return exponent != 0 && exponent != 0x1f;
    }

    public static float toFloat(short h) {
        // Simplified stub conversion
        int sign = (h >>> 15) & 0x1;
        int exponent = (h >>> 10) & 0x1f;
        int mantissa = h & 0x3ff;

        if (exponent == 0x1f) {
            if (mantissa != 0) return Float.NaN;
            return sign == 0 ? Float.POSITIVE_INFINITY : Float.NEGATIVE_INFINITY;
        }
        if (exponent == 0) {
            if (mantissa == 0) return sign == 0 ? 0.0f : -0.0f;
            float val = (float) (mantissa / 1024.0 * Math.pow(2, -14));
            return sign == 0 ? val : -val;
        }
        float val = (float) ((1.0 + mantissa / 1024.0) * Math.pow(2, exponent - 15));
        return sign == 0 ? val : -val;
    }

    public static short toHalf(float f) {
        // Simplified stub conversion
        int bits = Float.floatToIntBits(f);
        int sign = (bits >>> 16) & 0x8000;
        int val = (bits & 0x7fffffff) + 0x1000;

        if (val >= 0x47800000) {
            if ((bits & 0x7fffffff) >= 0x47800000) {
                if (val < 0x7f800000) return (short) (sign | 0x7c00);
                return (short) (sign | 0x7c00 | ((bits & 0x007fffff) >>> 13));
            }
            return (short) (sign | 0x7bff);
        }
        if (val >= 0x38800000) {
            return (short) (sign | ((val - 0x38000000) >>> 13));
        }
        if (val < 0x33000000) {
            return (short) sign;
        }
        val = (bits & 0x7fffffff) >>> 23;
        return (short) (sign | (((bits & 0x7fffff) | 0x800000) + (0x800000 >>> (val - 102)) >>> (126 - val)));
    }

    @Override
    public int compareTo(Half another) {
        return Float.compare(toFloat(mValue), toFloat(another.mValue));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Half)) return false;
        return mValue == ((Half) o).mValue;
    }

    @Override
    public int hashCode() {
        return Short.hashCode(mValue);
    }

    @Override
    public String toString() {
        return Float.toString(toFloat(mValue));
    }

    public static Half valueOf(short h) {
        return new Half(h);
    }

    public static Half valueOf(float f) {
        return new Half(f);
    }

    public static Half valueOf(String s) {
        return new Half(s);
    }
}
