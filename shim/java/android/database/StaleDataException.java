package android.database;

/**
 * Android-compatible StaleDataException shim.
 * Thrown when an application attempts to use a Cursor that has been
 * deactivated or re-queried, making the cached data stale.
 * Stub — no-op implementation for A2OH migration.
 */
public class StaleDataException extends RuntimeException {

    public StaleDataException() {
        super();
    }

    public StaleDataException(String message) {
        super(message);
    }
}
