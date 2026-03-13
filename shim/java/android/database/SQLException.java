package android.database;

/**
 * Shim: android.database.SQLException — same as AOSP, pure Java.
 */
public class SQLException extends RuntimeException {
    public SQLException() {}
    public SQLException(String error) { super(error); }
    public SQLException(String error, Throwable cause) { super(error, cause); }
}
