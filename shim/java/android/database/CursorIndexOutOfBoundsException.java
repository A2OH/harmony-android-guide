package android.database;

/**
 * Android-compatible CursorIndexOutOfBoundsException shim. Pure Java stub.
 */
public class CursorIndexOutOfBoundsException extends IndexOutOfBoundsException {
    public CursorIndexOutOfBoundsException(String message) {
        super(message);
    }

    public CursorIndexOutOfBoundsException(int index, int size) {
        super("Index " + index + " requested, with size " + size);
    }
}
