package android.icu.util;

import java.util.Date;

/**
 * Android ICU Calendar shim. Abstract wrapper around java.util.Calendar.
 */
public class Calendar {

    // Field constants (match java.util.Calendar values)
    public static final int ERA            = java.util.Calendar.ERA;
    public static final int YEAR           = java.util.Calendar.YEAR;
    public static final int MONTH          = java.util.Calendar.MONTH;
    public static final int DAY_OF_MONTH   = java.util.Calendar.DAY_OF_MONTH;
    public static final int HOUR           = java.util.Calendar.HOUR;
    public static final int HOUR_OF_DAY    = java.util.Calendar.HOUR_OF_DAY;
    public static final int MINUTE         = java.util.Calendar.MINUTE;
    public static final int SECOND         = java.util.Calendar.SECOND;
    public static final int MILLISECOND    = java.util.Calendar.MILLISECOND;
    public static final int DAY_OF_WEEK    = java.util.Calendar.DAY_OF_WEEK;
    public static final int WEEK_OF_YEAR   = java.util.Calendar.WEEK_OF_YEAR;
    public static final int WEEK_OF_MONTH  = java.util.Calendar.WEEK_OF_MONTH;
    public static final int AM_PM          = java.util.Calendar.AM_PM;

    // Day-of-week constants
    public static final int SUNDAY    = java.util.Calendar.SUNDAY;
    public static final int MONDAY    = java.util.Calendar.MONDAY;
    public static final int TUESDAY   = java.util.Calendar.TUESDAY;
    public static final int WEDNESDAY = java.util.Calendar.WEDNESDAY;
    public static final int THURSDAY  = java.util.Calendar.THURSDAY;
    public static final int FRIDAY    = java.util.Calendar.FRIDAY;
    public static final int SATURDAY  = java.util.Calendar.SATURDAY;

    // Month constants
    public static final int JANUARY   = java.util.Calendar.JANUARY;
    public static final int FEBRUARY  = java.util.Calendar.FEBRUARY;
    public static final int MARCH     = java.util.Calendar.MARCH;
    public static final int APRIL     = java.util.Calendar.APRIL;
    public static final int MAY       = java.util.Calendar.MAY;
    public static final int JUNE      = java.util.Calendar.JUNE;
    public static final int JULY      = java.util.Calendar.JULY;
    public static final int AUGUST    = java.util.Calendar.AUGUST;
    public static final int SEPTEMBER = java.util.Calendar.SEPTEMBER;
    public static final int OCTOBER   = java.util.Calendar.OCTOBER;
    public static final int NOVEMBER  = java.util.Calendar.NOVEMBER;
    public static final int DECEMBER  = java.util.Calendar.DECEMBER;

    protected java.util.Calendar delegate;

    protected Calendar() {
        this.delegate = java.util.Calendar.getInstance();
    }

    protected Calendar(java.util.Calendar cal) {
        this.delegate = cal;
    }

    // ---- Static factory ----

    public static Calendar getInstance() {
        return new GregorianCalendarImpl(java.util.Calendar.getInstance());
    }

    public static Calendar getInstance(TimeZone tz) {
        java.util.Calendar jCal = java.util.Calendar.getInstance(tz.toJavaTimeZone());
        return new GregorianCalendarImpl(jCal);
    }

    public static Calendar getInstance(ULocale locale) {
        java.util.Calendar jCal = java.util.Calendar.getInstance(locale.toLocale());
        return new GregorianCalendarImpl(jCal);
    }

    // ---- Instance methods ----

    public int get(int field) {
        return delegate.get(field);
    }

    public void set(int field, int value) {
        delegate.set(field, value);
    }

    public void add(int field, int amount) {
        delegate.add(field, amount);
    }

    public Date getTime() {
        return delegate.getTime();
    }

    public void setTime(Date date) {
        delegate.setTime(date);
    }

    public long getTimeInMillis() {
        return delegate.getTimeInMillis();
    }

    public void setTimeInMillis(long millis) {
        delegate.setTimeInMillis(millis);
    }

    public TimeZone getTimeZone() {
        return TimeZone.getTimeZone(delegate.getTimeZone().getID());
    }

    public void setTimeZone(TimeZone tz) {
        delegate.setTimeZone(tz.toJavaTimeZone());
    }

    // ---- Concrete subclass used by getInstance() ----

    private static final class GregorianCalendarImpl extends Calendar {
        GregorianCalendarImpl(java.util.Calendar cal) {
            super(cal);
        }
    }
}
