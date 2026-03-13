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
            return String.format("%.1f KB", sizeBytes / 1024.0);
        } else if (sizeBytes < 1024L * 1024L * 1024L) {
            return String.format("%.1f MB", sizeBytes / (1024.0 * 1024.0));
        } else {
            return String.format("%.1f GB", sizeBytes / (1024.0 * 1024.0 * 1024.0));
        }
    }

    public static String formatShortFileSize(Object context, long sizeBytes) {
        return formatFileSize(context, sizeBytes);
    }
}
