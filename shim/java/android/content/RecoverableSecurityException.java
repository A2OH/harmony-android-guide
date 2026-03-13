package android.content;

/**
 * Shim: android.content.RecoverableSecurityException (API 26+)
 * Tier 1 — a {@link SecurityException} that contains a human-readable error message
 * and a recoverable action that the user can take to resolve it.
 *
 * OH mapping: OH surfaces permission-recovery through the system dialog triggered by
 * @ohos.abilityAccessCtrl requestPermissionsFromUser().  App catch blocks for this
 * exception should launch the equivalent OH permission-request flow.
 */
public class RecoverableSecurityException extends SecurityException {

    private final CharSequence mUserMessage;
    // In the real API this is an android.app.PendingIntent-backed RemoteAction;
    // in the shim we store an opaque Object so that callers compile without the
    // android.app.RemoteAction dependency.
    private final Object mUserAction;

    /**
     * Creates the exception.
     *
     * @param cause       the underlying cause
     * @param userMessage a short, user-readable message describing the error
     * @param userAction  the action the user can take to resolve the issue
     *                    (typed as Object to avoid a hard dependency on RemoteAction
     *                    in the shim layer)
     */
    public RecoverableSecurityException(Throwable cause,
            CharSequence userMessage, Object userAction) {
        super(cause != null ? cause.getMessage() : null);
        initCause(cause);
        mUserMessage = userMessage;
        mUserAction  = userAction;
    }

    /**
     * Simple stub constructor with just a message.  Useful when the recoverable
     * action is not yet wired up.
     *
     * @param message detail message
     */
    public RecoverableSecurityException(String message) {
        super(message);
        mUserMessage = message;
        mUserAction  = null;
    }

    /**
     * Return the human-readable message that should be shown to the user.
     */
    public CharSequence getUserMessage() {
        return mUserMessage;
    }

    /**
     * Return the action the user can take to resolve the issue, or null if none
     * was provided.  The real type is android.app.RemoteAction; in the shim it is
     * returned as Object to avoid a compile-time dependency.
     */
    public Object getUserAction() {
        return mUserAction;
    }
}
