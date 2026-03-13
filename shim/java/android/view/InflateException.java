package android.view;

/**
 * Android-compatible InflateException shim.
 */
public class InflateException extends RuntimeException {
    public InflateException() { super(); }
    public InflateException(String msg) { super(msg); }
    public InflateException(String msg, Throwable cause) { super(msg, cause); }
    public InflateException(Throwable cause) { super(cause); }
}
