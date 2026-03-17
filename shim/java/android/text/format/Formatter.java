package android.text.format;

/**
 * Shim for android.text.format.Formatter.
 * Provides file size formatting utilities.
 */
public class Formatter {

    public static String formatFileSize(Object context, long sizeBytes) {
        if (sizeBytes < 1024L) {
            return sizeBytes + " B";
        } else if (sizeBytes < 1024L * 1024L) {
            return formatOneDecimal(sizeBytes / 1024.0) + " KB";
        } else if (sizeBytes < 1024L * 1024L * 1024L) {
            return formatOneDecimal(sizeBytes / (1024.0 * 1024.0)) + " MB";
        } else {
            return formatOneDecimal(sizeBytes / (1024.0 * 1024.0 * 1024.0)) + " GB";
        }
    }

    /** Format a double to one decimal place without String.format. */
    private static String formatOneDecimal(double val) {
        long tenths = Math.round(val * 10);
        return (tenths / 10) + "." + (tenths % 10);
    }

    public static String formatShortFileSize(Object context, long sizeBytes) {
        return formatFileSize(context, sizeBytes);
    }
}
