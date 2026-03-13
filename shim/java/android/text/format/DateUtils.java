package android.text.format;

import java.util.Calendar;
import java.util.Formatter;

/**
 * Android-compatible DateUtils shim. Provides time constants and human-readable
 * date/time formatting. Pure Java, no Android framework dependencies.
 */
public class DateUtils {

    // Time constants (milliseconds) -----------------------------------------
    public static final long SECOND_IN_MILLIS = 1_000L;
    public static final long MINUTE_IN_MILLIS = 60_000L;
    public static final long HOUR_IN_MILLIS   = 3_600_000L;
    public static final long DAY_IN_MILLIS    = 86_400_000L;
    public static final long WEEK_IN_MILLIS   = 7 * DAY_IN_MILLIS;
    public static final long YEAR_IN_MILLIS   = 52 * WEEK_IN_MILLIS;

    // Format flags ----------------------------------------------------------
    public static final int FORMAT_SHOW_TIME    = 0x00001;
    public static final int FORMAT_SHOW_DATE    = 0x00004;
    public static final int FORMAT_ABBREV_ALL   = 0x00080;
    public static final int FORMAT_SHOW_YEAR    = 0x00010;
    public static final int FORMAT_NO_YEAR      = 0x00020;
    public static final int FORMAT_SHOW_WEEKDAY = 0x00002;
    public static final int FORMAT_ABBREV_WEEKDAY  = 0x00200;
    public static final int FORMAT_ABBREV_MONTH    = 0x00100;
    public static final int FORMAT_12HOUR          = 0x00400;
    public static final int FORMAT_24HOUR          = 0x00800;
    public static final int FORMAT_UTC             = 0x02000;

    private DateUtils() {}

    /**
     * Returns a human-readable date/time string for the given timestamp,
     * honoring the FORMAT_SHOW_TIME / FORMAT_SHOW_DATE flags.
     */
    public static String formatDateTime(android.content.Context context,
                                        long millis, int flags) {
        java.util.Date date = new java.util.Date(millis);
        StringBuilder sb = new StringBuilder();
        boolean showDate = (flags & FORMAT_SHOW_DATE) != 0;
        boolean showTime = (flags & FORMAT_SHOW_TIME) != 0;
        if (!showDate && !showTime) { showDate = true; }

        java.text.SimpleDateFormat dateFmt =
            new java.text.SimpleDateFormat((flags & FORMAT_ABBREV_ALL) != 0 ? "MMM d, yyyy" : "MMMM d, yyyy");
        java.text.SimpleDateFormat timeFmt = new java.text.SimpleDateFormat("h:mm a");

        if (showDate) sb.append(dateFmt.format(date));
        if (showDate && showTime) sb.append(", ");
        if (showTime) sb.append(timeFmt.format(date));
        return sb.toString();
    }

    /**
     * Returns a relative time span string such as "3 minutes ago" or "in 2 hours".
     */
    public static CharSequence getRelativeTimeSpanString(long time) {
        return getRelativeTimeSpanString(time, System.currentTimeMillis(), MINUTE_IN_MILLIS);
    }

    public static CharSequence getRelativeTimeSpanString(long time, long now, long minResolution) {
        long delta = Math.abs(now - time);
        boolean past = time <= now;

        String amount;
        if (delta < MINUTE_IN_MILLIS && minResolution < MINUTE_IN_MILLIS) {
            amount = "seconds";
        } else if (delta < HOUR_IN_MILLIS) {
            long mins = delta / MINUTE_IN_MILLIS;
            amount = mins + " " + (mins == 1 ? "minute" : "minutes");
        } else if (delta < DAY_IN_MILLIS) {
            long hours = delta / HOUR_IN_MILLIS;
            amount = hours + " " + (hours == 1 ? "hour" : "hours");
        } else if (delta < WEEK_IN_MILLIS) {
            long days = delta / DAY_IN_MILLIS;
            amount = days + " " + (days == 1 ? "day" : "days");
        } else if (delta < YEAR_IN_MILLIS) {
            long weeks = delta / WEEK_IN_MILLIS;
            amount = weeks + " " + (weeks == 1 ? "week" : "weeks");
        } else {
            long years = delta / YEAR_IN_MILLIS;
            amount = years + " " + (years == 1 ? "year" : "years");
        }

        return past ? amount + " ago" : "in " + amount;
    }

    public static CharSequence getRelativeTimeSpanString(long time, long now,
                                                          long minResolution, int flags) {
        return getRelativeTimeSpanString(time, now, minResolution);
    }

    /**
     * Returns true if the given timestamp falls on today's date.
     */
    public static boolean isToday(long when) {
        Calendar now  = Calendar.getInstance();
        Calendar then = Calendar.getInstance();
        then.setTimeInMillis(when);
        return now.get(Calendar.YEAR)         == then.get(Calendar.YEAR)
            && now.get(Calendar.DAY_OF_YEAR)  == then.get(Calendar.DAY_OF_YEAR);
    }
}
