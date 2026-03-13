package android.os;

/**
 * Android-compatible ParcelFormatException shim.
 * Thrown when a Parcel encounters malformed data.
 */
public class ParcelFormatException extends RuntimeException {

    public ParcelFormatException() {
        super();
    }

    public ParcelFormatException(String message) {
        super(message);
    }
}
