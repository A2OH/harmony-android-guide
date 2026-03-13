package android.content;

import android.os.Bundle;

/**
 * Android-compatible IntentSender shim. Stub implementation.
 */
public class IntentSender {

    private final String mPackage;

    public IntentSender() {
        mPackage = null;
    }

    public IntentSender(String packageName) {
        mPackage = packageName;
    }

    /**
     * Send an intent. Stub -- does nothing.
     */
    public void sendIntent(Context context, int code, Intent intent,
                           OnFinished onFinished, android.os.Handler handler)
            throws SendIntentException {
        // stub -- no-op
    }

    /**
     * Send an intent with extras. Stub -- does nothing.
     */
    public void sendIntent(Context context, int code, Intent intent,
                           OnFinished onFinished, android.os.Handler handler,
                           String requiredPermission)
            throws SendIntentException {
        // stub -- no-op
    }

    public String getTargetPackage() {
        return mPackage;
    }

    public String getCreatorPackage() {
        return mPackage;
    }

    public int getCreatorUid() {
        return 0; // stub
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IntentSender)) return false;
        return true; // stub
    }

    @Override
    public int hashCode() {
        return mPackage != null ? mPackage.hashCode() : 0;
    }

    /**
     * Exception thrown when sendIntent fails.
     */
    public static class SendIntentException extends Exception {
        public SendIntentException() {
            super();
        }

        public SendIntentException(String message) {
            super(message);
        }

        public SendIntentException(Throwable cause) {
            super(cause);
        }

        public SendIntentException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    /**
     * Callback for when a sent intent completes.
     */
    public interface OnFinished {
        void onSendFinished(IntentSender intentSender, Intent intent,
                            int resultCode, String resultData, Bundle resultExtras);
    }
}
