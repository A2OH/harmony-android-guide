package android.icu.util;

/**
 * Android ICU MeasureUnit shim. Static constant units with type/subtype metadata.
 */
public class MeasureUnit {

    // Length
    public static final MeasureUnit METER     = new MeasureUnit("length",   "meter");
    public static final MeasureUnit KILOMETER = new MeasureUnit("length",   "kilometer");
    public static final MeasureUnit MILE      = new MeasureUnit("length",   "mile");
    public static final MeasureUnit FOOT      = new MeasureUnit("length",   "foot");
    public static final MeasureUnit INCH      = new MeasureUnit("length",   "inch");
    public static final MeasureUnit YARD      = new MeasureUnit("length",   "yard");

    // Mass
    public static final MeasureUnit GRAM      = new MeasureUnit("mass",     "gram");
    public static final MeasureUnit KILOGRAM  = new MeasureUnit("mass",     "kilogram");
    public static final MeasureUnit POUND     = new MeasureUnit("mass",     "pound");
    public static final MeasureUnit OUNCE     = new MeasureUnit("mass",     "ounce");

    // Volume
    public static final MeasureUnit LITER     = new MeasureUnit("volume",   "liter");
    public static final MeasureUnit GALLON    = new MeasureUnit("volume",   "gallon");

    // Temperature
    public static final MeasureUnit CELSIUS    = new MeasureUnit("temperature", "celsius");
    public static final MeasureUnit FAHRENHEIT = new MeasureUnit("temperature", "fahrenheit");

    // Duration
    public static final MeasureUnit SECOND    = new MeasureUnit("duration", "second");
    public static final MeasureUnit MINUTE    = new MeasureUnit("duration", "minute");
    public static final MeasureUnit HOUR      = new MeasureUnit("duration", "hour");
    public static final MeasureUnit DAY       = new MeasureUnit("duration", "day");
    public static final MeasureUnit YEAR      = new MeasureUnit("duration", "year");

    // Digital storage
    public static final MeasureUnit BYTE      = new MeasureUnit("digital",  "byte");
    public static final MeasureUnit KILOBYTE  = new MeasureUnit("digital",  "kilobyte");
    public static final MeasureUnit MEGABYTE  = new MeasureUnit("digital",  "megabyte");
    public static final MeasureUnit GIGABYTE  = new MeasureUnit("digital",  "gigabyte");
    public static final MeasureUnit TERABYTE  = new MeasureUnit("digital",  "terabyte");

    // Misc
    public static final MeasureUnit PERCENT   = new MeasureUnit("concentr", "percent");
    public static final MeasureUnit KNOT      = new MeasureUnit("speed",    "knot");

    private final String type;
    private final String subtype;

    protected MeasureUnit(String type, String subtype) {
        this.type    = type;
        this.subtype = subtype;
    }

    public String getType() {
        return type;
    }

    public String getSubtype() {
        return subtype;
    }

    @Override
    public String toString() {
        return type + "-" + subtype;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof MeasureUnit)) return false;
        MeasureUnit o = (MeasureUnit) obj;
        return type.equals(o.type) && subtype.equals(o.subtype);
    }

    @Override
    public int hashCode() {
        return 31 * type.hashCode() + subtype.hashCode();
    }
}
