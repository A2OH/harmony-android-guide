package android.app;
import android.graphics.drawable.Icon;
import android.graphics.drawable.Icon;

/**
 * Android-compatible RecoverableSecurityException shim.
 * Wraps a security violation with a user-presentable message and a
 * recoverable action the caller may invoke.
 */
public class RecoverableSecurityException extends SecurityException {

    // -----------------------------------------------------------------------
    // RemoteAction inner class
    // -----------------------------------------------------------------------
    /**
     * Lightweight stub for {@code android.app.RemoteAction}.
     * Avoids pulling in the full platform class while still letting callers
     * inspect the action's metadata.
     */
    public static final class RemoteAction {
        private final CharSequence mTitle;
        private final Object       mIcon;           // android.graphics.drawable.Icon — typed as Object
        private final CharSequence mContentDescription;
        private final Object       mActionIntent;   // android.app.PendingIntent — typed as Object

        public RemoteAction(CharSequence title, Object icon,
                            CharSequence contentDescription, Object actionIntent) {
            mTitle              = title;
            mIcon               = icon;
            mContentDescription = contentDescription;
            mActionIntent       = actionIntent;
        }

        /** Returns the title of this action. */
        public CharSequence getTitle() {
            return mTitle;
        }

        /** Returns the icon associated with this action, or {@code null}. */
        public Object getIcon() {
            return mIcon;
        }

        /** Returns the content description of this action. */
        public CharSequence getContentDescription() {
            return mContentDescription;
        }

        /**
         * Returns the {@code PendingIntent} (typed as {@code Object}) that will
         * be sent when the user accepts the action.
         */
        public Object getActionIntent() {
            return mActionIntent;
        }
    }

    // -----------------------------------------------------------------------
    // RecoverableSecurityException fields
    // -----------------------------------------------------------------------
    private final CharSequence mUserMessage;
    private final RemoteAction mUserAction;

    /**
     * Creates a new RecoverableSecurityException.
     *
     * @param cause       the underlying cause of the security violation
     * @param userMessage a user-presentable description of the problem
     * @param userAction  a {@link RemoteAction} the user can invoke to recover,
     *                    typed as {@code Object} for compatibility — pass a
     *                    {@link RemoteAction} instance
     */
    public RecoverableSecurityException(Throwable cause,
                                        CharSequence userMessage,
                                        Object userAction) {
        super(userMessage != null ? userMessage.toString() : null);
        if (cause != null) {
            initCause(cause);
        }
        mUserMessage = userMessage;
        mUserAction  = (userAction instanceof RemoteAction) ? (RemoteAction) userAction : null;
    }

    /**
     * Returns the user-presentable message describing the security violation.
     */
    public CharSequence getUserMessage() {
        return mUserMessage;
    }

    /**
     * Returns the {@link RemoteAction} the user can perform to recover from
     * this exception, or {@code null} if none was provided.
     */
    public RemoteAction getUserAction() {
        return mUserAction;
    }
}
