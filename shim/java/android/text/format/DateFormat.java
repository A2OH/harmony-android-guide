package android.text.format;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Android-compatible DateFormat shim. Provides static factory methods returning
 * standard java.text.DateFormat instances.
 */
public class DateFormat {

    private DateFormat() {}

    /**
     * Returns a {@link java.text.DateFormat} suitable for displaying the date
     * in short form for the default locale.
     */
    public static java.text.DateFormat getDateFormat(android.content.Context context) {
        return java.text.DateFormat.getDateInstance(java.text.DateFormat.SHORT);
    }

    /**
     * Returns a {@link java.text.DateFormat} suitable for displaying the time
     * for the default locale, respecting 12/24-hour preference.
     */
    public static java.text.DateFormat getTimeFormat(android.content.Context context) {
        return java.text.DateFormat.getTimeInstance(java.text.DateFormat.SHORT);
    }

    /**
     * Returns a {@link java.text.DateFormat} suitable for displaying date and
     * time together in medium form.
     */
    public static java.text.DateFormat getDateTimeFormat(android.content.Context context) {
        return java.text.DateFormat.getDateTimeInstance(
            java.text.DateFormat.SHORT, java.text.DateFormat.SHORT);
    }

    /**
     * Formats a {@link java.util.Date} using the given format string.
     *
     * <p>Android format uses different tokens from {@link SimpleDateFormat}:
     * {@code d} → day-of-month, {@code M} → month, {@code y} → year,
     * {@code h} → hour (12), {@code H} → hour (24), {@code m} → minute,
     * {@code s} → second.</p>
     */
    public static CharSequence format(CharSequence inFormat, java.util.Date date) {
        // Convert Android-style single-char tokens to SimpleDateFormat patterns.
        String pattern = convertAndroidFormat(inFormat.toString());
        return new SimpleDateFormat(pattern, Locale.getDefault()).format(date);
    }

    /**
     * Formats the given millisecond timestamp using an Android-style format string.
     */
    public static CharSequence format(CharSequence inFormat, long timeInMillis) {
        return format(inFormat, new java.util.Date(timeInMillis));
    }

    /**
     * Formats the given {@link java.util.Calendar} using an Android-style format string.
     */
    public static CharSequence format(CharSequence inFormat, java.util.Calendar cal) {
        return format(inFormat, cal.getTime());
    }

    /**
     * Returns {@code true} if the current user prefers 24-hour time.
     * Stub always returns {@code false}.
     */
    public static boolean is24HourFormat(android.content.Context context) {
        return false;
    }

    // -----------------------------------------------------------------------
    // Helpers
    // -----------------------------------------------------------------------

    /**
     * Converts Android single-char format tokens to {@link SimpleDateFormat} patterns.
     * Quoted literals ('text') are preserved unchanged.
     */
    private static String convertAndroidFormat(String fmt) {
        StringBuilder out = new StringBuilder();
        boolean inQuote = false;
        for (int i = 0; i < fmt.length(); i++) {
            char c = fmt.charAt(i);
            if (c == '\'') {
                inQuote = !inQuote;
                out.append(c);
                continue;
            }
            if (inQuote) {
                out.append(c);
                continue;
            }
            // Android single-char tokens that differ from SimpleDateFormat
            switch (c) {
                case 'A': out.append("a");   break; // AM/PM
                case 'k': out.append("H");   break; // 24-hour without padding
                default:  out.append(c);     break;
            }
        }
        return out.toString();
    }
}
