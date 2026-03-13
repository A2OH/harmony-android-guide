package android.util;

/**
 * Shim: android.util.TypedValue
 * Carries typed resource values and provides dimension-conversion utilities.
 * Pure Java — no Android or OHBridge dependencies.
 */
public class TypedValue {

    // -----------------------------------------------------------------------
    // Type constants
    // -----------------------------------------------------------------------

    public static final int TYPE_NULL        =  0;
    public static final int TYPE_REFERENCE   =  1;
    public static final int TYPE_ATTRIBUTE   =  2;
    public static final int TYPE_STRING      =  3;
    public static final int TYPE_FLOAT       =  4;
    public static final int TYPE_DIMENSION   =  5;
    public static final int TYPE_FRACTION    =  6;
    public static final int TYPE_INT_DEC     = 16;
    public static final int TYPE_INT_HEX     = 17;
    public static final int TYPE_INT_BOOLEAN = 18;
    public static final int TYPE_FIRST_COLOR_INT = 28;
    public static final int TYPE_LAST_COLOR_INT  = 31;
    public static final int TYPE_FIRST_INT   = 16;
    public static final int TYPE_LAST_INT    = 31;

    // -----------------------------------------------------------------------
    // Complex unit constants (used with TYPE_DIMENSION)
    // -----------------------------------------------------------------------

    public static final int COMPLEX_UNIT_PX  = 0;
    public static final int COMPLEX_UNIT_DIP = 1;
    public static final int COMPLEX_UNIT_SP  = 2;
    public static final int COMPLEX_UNIT_PT  = 3;
    public static final int COMPLEX_UNIT_IN  = 4;
    public static final int COMPLEX_UNIT_MM  = 5;

    /** Alias used by Android source: dp == dip. */
    public static final int COMPLEX_UNIT_DP  = COMPLEX_UNIT_DIP;

    // -----------------------------------------------------------------------
    // Fraction unit constants
    // -----------------------------------------------------------------------

    public static final int COMPLEX_UNIT_FRACTION      = 0;
    public static final int COMPLEX_UNIT_FRACTION_PARENT = 1;

    // -----------------------------------------------------------------------
    // Fields
    // -----------------------------------------------------------------------

    /** The type held by this value; one of the TYPE_* constants above. */
    public int type;

    /**
     * Basic data in the value. Interpretation depends on {@link #type}:
     * integer types hold the raw int, float types hold the bit pattern of
     * a float (use {@link Float#intBitsToFloat(int)}), dimension/fraction
     * types encode unit and mantissa via the Android complex-data format.
     */
    public int data;

    /** Resource ID that backed this value, or 0. */
    public int resourceId;

    /** Screen density associated with this value, or 0. */
    public int density;

    /** String data for TYPE_STRING values (may be null). */
    public CharSequence string;

    // -----------------------------------------------------------------------
    // Constructors
    // -----------------------------------------------------------------------

    public TypedValue() {}

    // -----------------------------------------------------------------------
    // Static conversion utilities
    // -----------------------------------------------------------------------

    /**
     * Converts a dimension value of the given unit to raw pixels.
     *
     * @param unit    one of the COMPLEX_UNIT_* constants
     * @param value   the dimension amount in the given unit
     * @param metrics the target screen's {@link DisplayMetrics}
     * @return the pixel value, or {@code value} unchanged for COMPLEX_UNIT_PX
     */
    public static float applyDimension(int unit, float value, DisplayMetrics metrics) {
        switch (unit) {
            case COMPLEX_UNIT_PX:  return value;
            case COMPLEX_UNIT_DIP: return value * metrics.density;
            case COMPLEX_UNIT_SP:  return value * metrics.scaledDensity;
            case COMPLEX_UNIT_PT:  return value * metrics.xdpi * (1.0f / 72.0f);
            case COMPLEX_UNIT_IN:  return value * metrics.xdpi;
            case COMPLEX_UNIT_MM:  return value * metrics.xdpi * (1.0f / 25.4f);
            default:               return 0f;
        }
    }

    // -----------------------------------------------------------------------
    // Instance helpers
    // -----------------------------------------------------------------------

    /**
     * Returns the float value stored in {@link #data} for TYPE_FLOAT values.
     */
    public float getFloat() {
        return Float.intBitsToFloat(data);
    }

    /**
     * Returns the dimension value in pixels.  Only valid when {@link #type}
     * is {@link #TYPE_DIMENSION}.
     *
     * @param metrics the target screen's {@link DisplayMetrics}
     */
    public float getDimension(DisplayMetrics metrics) {
        int unit   = data & 0xF;
        float fval = complexToFloat(data);
        return applyDimension(unit, fval, metrics);
    }

    /**
     * Returns the fraction value.  Only valid when {@link #type} is
     * {@link #TYPE_FRACTION}.
     *
     * @param base   base value to multiply a base-relative fraction by
     * @param pbase  parent-base value to multiply a parent-relative fraction by
     */
    public float getFraction(float base, float pbase) {
        int unit   = data & 0xF;
        float fval = complexToFloat(data);
        switch (unit) {
            case COMPLEX_UNIT_FRACTION:        return fval * base;
            case COMPLEX_UNIT_FRACTION_PARENT: return fval * pbase;
            default:                           return fval;
        }
    }

    /**
     * Converts an Android complex integer (mantissa + radix encoding) to a
     * float value.  Mirrors {@code android.util.TypedValue.complexToFloat()}.
     */
    public static float complexToFloat(int complex) {
        // Bits 23-31: mantissa sign + high bits; bits 4-22: mantissa low bits
        // Bits 0-3: unit; bits 4-5: radix
        //
        // Android's real encoding is non-trivial; we reproduce a faithful
        // approximation sufficient for shim usage.
        final int MANTISSA_MULT = 1 << 8;
        int mantissa = (complex >> 8);
        int radix    = (complex >> 4) & 0x3;
        float result;
        switch (radix) {
            case 0: result = mantissa         / (float)(1 << 23); break; // 0 fraction bits
            case 1: result = mantissa * 256f  / (float)(1 << 23); break; // 8 fraction bits
            case 2: result = mantissa * 65536f/ (float)(1 << 23); break; // 16 fraction bits
            case 3: result = mantissa;                             break; // integer
            default: result = 0f; break;
        }
        return result;
    }

    // -----------------------------------------------------------------------
    // Object overrides
    // -----------------------------------------------------------------------

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("TypedValue{t=0x");
        sb.append(Integer.toHexString(type));
        sb.append("/d=0x");
        sb.append(Integer.toHexString(data));
        if (string != null) sb.append(" \"").append(string).append('"');
        sb.append('}');
        return sb.toString();
    }
}
