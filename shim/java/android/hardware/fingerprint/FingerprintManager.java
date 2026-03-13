package android.hardware.fingerprint;

import java.util.concurrent.Executor;

/**
 * Android-compatible FingerprintManager stub.
 * Deprecated in Android API 28+ (use BiometricPrompt instead) but kept for shim coverage.
 */
public class FingerprintManager {

    // Error codes
    public static final int FINGERPRINT_ERROR_HW_UNAVAILABLE   = 1;
    public static final int FINGERPRINT_ERROR_UNABLE_TO_PROCESS = 2;
    public static final int FINGERPRINT_ERROR_TIMEOUT           = 3;
    public static final int FINGERPRINT_ERROR_NO_SPACE          = 4;
    public static final int FINGERPRINT_ERROR_CANCELED          = 5;
    public static final int FINGERPRINT_ERROR_LOCKOUT           = 7;

    // Acquisition codes
    public static final int FINGERPRINT_ACQUIRED_GOOD           = 0;
    public static final int FINGERPRINT_ACQUIRED_PARTIAL        = 1;
    public static final int FINGERPRINT_ACQUIRED_INSUFFICIENT   = 2;
    public static final int FINGERPRINT_ACQUIRED_IMAGER_DIRTY   = 3;
    public static final int FINGERPRINT_ACQUIRED_TOO_SLOW       = 4;
    public static final int FINGERPRINT_ACQUIRED_TOO_FAST       = 5;

    /** @param context unused in shim */
    public FingerprintManager(Object context) {}

    /**
     * Returns whether the device has fingerprint hardware.
     * Always false in the shim.
     */
    public boolean isHardwareDetected() {
        return false;
    }

    /**
     * Returns whether the user has enrolled at least one fingerprint.
     * Always false in the shim.
     */
    public boolean hasEnrolledFingerprints() {
        return false;
    }

    /**
     * Start fingerprint authentication. The shim immediately calls
     * {@link AuthenticationCallback#onAuthenticationError} with
     * {@link #FINGERPRINT_ERROR_HW_UNAVAILABLE}.
     */
    public void authenticate(CryptoObject crypto,
                             CancellationSignal cancel,
                             int flags,
                             AuthenticationCallback callback,
                             Object handler) {
        if (callback != null) {
            callback.onAuthenticationError(FINGERPRINT_ERROR_HW_UNAVAILABLE,
                "FingerprintManager: hardware not available (shim)");
        }
    }

    // -----------------------------------------------------------------------
    // AuthenticationCallback
    // -----------------------------------------------------------------------

    public abstract static class AuthenticationCallback {
        public void onAuthenticationError(int errorCode, CharSequence errString) {}
        public void onAuthenticationHelp(int helpCode, CharSequence helpString) {}
        public void onAuthenticationSucceeded(AuthenticationResult result) {}
        public void onAuthenticationFailed() {}
    }

    // -----------------------------------------------------------------------
    // AuthenticationResult
    // -----------------------------------------------------------------------

    public static final class AuthenticationResult {
        private final CryptoObject mCryptoObject;

        public AuthenticationResult(CryptoObject crypto) {
            mCryptoObject = crypto;
        }

        public CryptoObject getCryptoObject() { return mCryptoObject; }
    }

    // -----------------------------------------------------------------------
    // CryptoObject
    // -----------------------------------------------------------------------

    public static final class CryptoObject {
        private final javax.crypto.Cipher     mCipher;
        private final javax.crypto.Mac        mMac;
        private final java.security.Signature mSignature;

        public CryptoObject(javax.crypto.Cipher cipher) {
            mCipher = cipher; mMac = null; mSignature = null;
        }
        public CryptoObject(javax.crypto.Mac mac) {
            mMac = mac; mCipher = null; mSignature = null;
        }
        public CryptoObject(java.security.Signature sig) {
            mSignature = sig; mCipher = null; mMac = null;
        }

        public javax.crypto.Cipher     getCipher()    { return mCipher; }
        public javax.crypto.Mac        getMac()       { return mMac; }
        public java.security.Signature getSignature() { return mSignature; }
    }

    // -----------------------------------------------------------------------
    // CancellationSignal
    // -----------------------------------------------------------------------

    public static final class CancellationSignal {
        private boolean          mCanceled = false;
        private OnCancelListener mListener;

        public boolean isCanceled() { return mCanceled; }

        public void cancel() {
            mCanceled = true;
            if (mListener != null) mListener.onCancel();
        }

        public void setOnCancelListener(OnCancelListener listener) {
            mListener = listener;
        }

        public interface OnCancelListener {
            void onCancel();
        }
    }
}
