package android.app;

/**
 * Shim: android.app.ForegroundServiceStartNotAllowedException (API 31+)
 * Tier 1 — exception thrown when an app tries to call
 * {@link Service#startForeground(int, Notification)} while it is not allowed to
 * start foreground services (e.g. the app is in the background).
 *
 * OH mapping: the equivalent condition on OH is throwing an error from
 * backgroundTaskManager.startBackgroundRunning() when the ability is not in the
 * foreground state.  App-level catch blocks for this exception should be mapped
 * to catching the OH error code 201 (permission denied) or error 9800003 (not in
 * foreground).
 */
public class ForegroundServiceStartNotAllowedException extends IllegalStateException {

    /**
     * Creates the exception with no detail message.
     */
    public ForegroundServiceStartNotAllowedException() {
        super();
    }

    /**
     * Creates the exception with the supplied detail message.
     *
     * @param message detail message
     */
    public ForegroundServiceStartNotAllowedException(String message) {
        super(message);
    }

    /**
     * Creates the exception with a detail message and a cause.
     *
     * @param message detail message
     * @param cause   the cause
     */
    public ForegroundServiceStartNotAllowedException(String message, Throwable cause) {
        super(message, cause);
    }
}
