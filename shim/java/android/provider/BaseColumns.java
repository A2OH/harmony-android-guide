package android.provider;

/**
 * Android-compatible BaseColumns shim.
 * Defines the standard column names shared by all content providers.
 */
public interface BaseColumns {
    /** The unique row ID. */
    String _ID = "_id";

    /** The number of rows in a directory. */
    String _COUNT = "_count";
}
