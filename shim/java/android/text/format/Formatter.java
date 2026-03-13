package android.text.format;

/**
 * Android-compatible Formatter shim. Provides human-readable byte-size strings.
 */
public final class Formatter {

    private Formatter() {}

    private static final long KB = 1_024L;
    private static final long MB = 1_024L * KB;
    private static final long GB = 1_024L * MB;
    private static final long TB = 1_024L * GB;
    private static final long PB = 1_024L * TB;

    /**
     * Formats the given number of bytes into a short human-readable string,
     * e.g. "1.2 MB". Uses binary (1024-based) prefixes, matching Android behaviour.
     */
    public static String formatFileSize(android.content.Context context, long sizeBytes) {
        return formatSize(sizeBytes, false);
    }

    /**
     * Like {@link #formatFileSize} but limits the precision to one significant
     * digit, e.g. "1 MB" instead of "1.2 MB".
     */
    public static String formatShortFileSize(android.content.Context context, long sizeBytes) {
        return formatSize(sizeBytes, true);
    }

    // -----------------------------------------------------------------------
    // Helpers
    // -----------------------------------------------------------------------

    private static String formatSize(long bytes, boolean shortForm) {
        if (bytes < 0) bytes = 0;
        if (bytes < KB) return bytes + " B";

        double value;
        String suffix;
        if (bytes < MB)      { value = (double) bytes / KB; suffix = "KB"; }
        else if (bytes < GB) { value = (double) bytes / MB; suffix = "MB"; }
        else if (bytes < TB) { value = (double) bytes / GB; suffix = "GB"; }
        else if (bytes < PB) { value = (double) bytes / TB; suffix = "TB"; }
        else                 { value = (double) bytes / PB; suffix = "PB"; }

        String formatted;
        if (shortForm || value >= 100) {
            formatted = String.valueOf(Math.round(value));
        } else if (value >= 10) {
            formatted = String.format("%.1f", value);
        } else {
            formatted = String.format("%.2f", value);
        }
        return formatted + " " + suffix;
    }
}
