package android.provider;

/**
 * Android-compatible OpenableColumns shim.
 * Column names for openable URIs (content that can be opened as a stream).
 */
public interface OpenableColumns extends BaseColumns {

    /**
     * The human-readable display name of the item referred to by the URI.
     * Corresponds to {@code OpenableColumns.DISPLAY_NAME} on Android.
     */
    String DISPLAY_NAME = "_display_name";

    /**
     * The size in bytes of the item referred to by the URI, or {@code null}
     * if the size is unknown.
     * Corresponds to {@code OpenableColumns.SIZE} on Android.
     */
    String SIZE = "_size";
}
