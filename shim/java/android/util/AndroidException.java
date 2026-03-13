package android.util;

/**
 * A2OH shim: AndroidException - base checked exception for Android APIs.
 */
public class AndroidException extends Exception {

    public AndroidException() {
        super();
    }

    public AndroidException(String name) {
        super(name);
    }

    public AndroidException(String name, Throwable cause) {
        super(name, cause);
    }

    public AndroidException(Exception cause) {
        super(cause);
    }
}
