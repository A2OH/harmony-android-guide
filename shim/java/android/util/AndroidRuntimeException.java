package android.util;

/**
 * A2OH shim: AndroidRuntimeException - base unchecked exception for Android APIs.
 */
public class AndroidRuntimeException extends RuntimeException {

    public AndroidRuntimeException() {
        super();
    }

    public AndroidRuntimeException(String msg) {
        super(msg);
    }

    public AndroidRuntimeException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public AndroidRuntimeException(Exception cause) {
        super(cause);
    }
}
