package android.icu.util;

/**
 * Android ICU TimeZone shim. Wraps java.util.TimeZone.
 */
public class TimeZone {

    public static final TimeZone GMT_ZONE     = getTimeZone("GMT");
    public static final TimeZone UNKNOWN_ZONE = getTimeZone("GMT");

    private final java.util.TimeZone delegate;

    private TimeZone(java.util.TimeZone tz) {
        this.delegate = tz;
    }

    // ---- Static factories ----

    public static TimeZone getDefault() {
        return new TimeZone(java.util.TimeZone.getDefault());
    }

    public static TimeZone getTimeZone(String id) {
        return new TimeZone(java.util.TimeZone.getTimeZone(id));
    }

    public static String[] getAvailableIDs() {
        return java.util.TimeZone.getAvailableIDs();
    }

    // ---- Instance methods ----

    public String getID() {
        return delegate.getID();
    }

    public String getDisplayName() {
        return delegate.getDisplayName();
    }

    public int getRawOffset() {
        return delegate.getRawOffset();
    }

    public boolean useDaylightTime() {
        return delegate.useDaylightTime();
    }

    /** Returns the underlying java.util.TimeZone for interop. */
    public java.util.TimeZone toJavaTimeZone() {
        return delegate;
    }

    @Override
    public String toString() {
        return getID();
    }
}
