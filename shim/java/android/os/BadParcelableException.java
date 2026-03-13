package android.os;

/**
 * Shim: android.os.BadParcelableException — thrown when a Parcelable
 * is malformed or could not be instantiated.
 */
public class BadParcelableException extends RuntimeException {

    public BadParcelableException(String msg) {
        super(msg);
    }

    public BadParcelableException(Exception cause) {
        super(cause);
    }
}
