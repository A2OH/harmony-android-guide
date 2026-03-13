package android.hardware.biometrics;

import java.util.concurrent.Executor;

/**
 * Android-compatible BiometricPrompt stub.
 */
public class BiometricPrompt {

    // Error codes
    public static final int BIOMETRIC_ERROR_HW_UNAVAILABLE      = 1;
    public static final int BIOMETRIC_ERROR_UNABLE_TO_PROCESS   = 2;
    public static final int BIOMETRIC_ERROR_TIMEOUT              = 3;
    public static final int BIOMETRIC_ERROR_NO_SPACE             = 4;
    public static final int BIOMETRIC_ERROR_CANCELED             = 5;
    public static final int BIOMETRIC_ERROR_LOCKOUT              = 7;
    public static final int BIOMETRIC_ERROR_USER_CANCELED        = 10;
    public static final int BIOMETRIC_ERROR_NO_BIOMETRICS        = 11;
    public static final int BIOMETRIC_ERROR_HW_NOT_PRESENT       = 12;
    public static final int BIOMETRIC_ERROR_NEGATIVE_BUTTON      = 13;
    public static final int BIOMETRIC_ERROR_NO_DEVICE_CREDENTIAL = 14;

    // Acquisition codes
    public static final int BIOMETRIC_ACQUIRED_GOOD         = 0;
    public static final int BIOMETRIC_ACQUIRED_IMAGER_DIRTY = 3;
    public static final int BIOMETRIC_ACQUIRED_TOO_SLOW     = 4;
    public static final int BIOMETRIC_ACQUIRED_TOO_FAST     = 5;

    private final String mTitle;
    private final String mSubtitle;
    private final String mDescription;
    private final String mNegativeButtonText;

    private BiometricPrompt(Builder builder) {
        mTitle              = builder.mTitle;
        mSubtitle           = builder.mSubtitle;
        mDescription        = builder.mDescription;
        mNegativeButtonText = builder.mNegativeButtonText;
    }

    public void authenticate(CancellationSignal cancel, Executor executor,
                             AuthenticationCallback callback) {
        executor.execute(() ->
            callback.onAuthenticationError(BIOMETRIC_ERROR_HW_NOT_PRESENT,
                "BiometricPrompt: hardware not present (shim)"));
    }

    public void authenticate(CryptoObject crypto, CancellationSignal cancel,
                             Executor executor, AuthenticationCallback callback) {
        authenticate(cancel, executor, callback);
    }

    // -----------------------------------------------------------------------
    // Builder
    // -----------------------------------------------------------------------

    public static final class Builder {
        private String mTitle            = "";
        private String mSubtitle         = "";
        private String mDescription      = "";
        private String mNegativeButtonText = "";

        /** @param context unused in shim (accepts Object to avoid dependency) */
        public Builder(Object context) {}

        public Builder setTitle(CharSequence title) {
            mTitle = title != null ? title.toString() : "";
            return this;
        }

        public Builder setSubtitle(CharSequence subtitle) {
            mSubtitle = subtitle != null ? subtitle.toString() : "";
            return this;
        }

        public Builder setDescription(CharSequence description) {
            mDescription = description != null ? description.toString() : "";
            return this;
        }

        /**
         * @param text     label for the negative button
         * @param executor executor on which the listener runs
         * @param listener click listener (NegativeButtonListener)
         */
        public Builder setNegativeButton(CharSequence text, Executor executor,
                                         NegativeButtonListener listener) {
            mNegativeButtonText = text != null ? text.toString() : "";
            return this;
        }

        public BiometricPrompt build() {
            return new BiometricPrompt(this);
        }
    }

    /** Listener for the negative/cancel button on the biometric dialog. */
    public interface NegativeButtonListener {
        void onClick(Object dialogInterface, int which);
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
        private final int          mAuthenticationType;

        public AuthenticationResult(CryptoObject crypto, int authenticationType) {
            mCryptoObject       = crypto;
            mAuthenticationType = authenticationType;
        }

        public CryptoObject getCryptoObject()  { return mCryptoObject; }
        public int getAuthenticationType()     { return mAuthenticationType; }
    }

    // -----------------------------------------------------------------------
    // CryptoObject
    // -----------------------------------------------------------------------

    public static final class CryptoObject {
        private final javax.crypto.Cipher      mCipher;
        private final javax.crypto.Mac         mMac;
        private final java.security.Signature  mSignature;

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
