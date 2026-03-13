package android.hardware.biometrics;

public class BiometricPrompt {
    public BiometricPrompt() {}

    public static final Object AUTHENTICATION_RESULT_TYPE_BIOMETRIC = null;
    public static final Object AUTHENTICATION_RESULT_TYPE_DEVICE_CREDENTIAL = null;
    public static final Object BIOMETRIC_ACQUIRED_GOOD = null;
    public static final Object BIOMETRIC_ACQUIRED_IMAGER_DIRTY = null;
    public static final Object BIOMETRIC_ACQUIRED_INSUFFICIENT = null;
    public static final Object BIOMETRIC_ACQUIRED_PARTIAL = null;
    public static final Object BIOMETRIC_ACQUIRED_TOO_FAST = null;
    public static final Object BIOMETRIC_ACQUIRED_TOO_SLOW = null;
    public static final Object BIOMETRIC_ERROR_CANCELED = null;
    public static final Object BIOMETRIC_ERROR_HW_NOT_PRESENT = null;
    public static final Object BIOMETRIC_ERROR_HW_UNAVAILABLE = null;
    public static final Object BIOMETRIC_ERROR_LOCKOUT = null;
    public static final Object BIOMETRIC_ERROR_LOCKOUT_PERMANENT = null;
    public static final Object BIOMETRIC_ERROR_NO_BIOMETRICS = null;
    public static final Object BIOMETRIC_ERROR_NO_DEVICE_CREDENTIAL = null;
    public static final Object BIOMETRIC_ERROR_NO_SPACE = null;
    public static final Object BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED = null;
    public static final Object BIOMETRIC_ERROR_TIMEOUT = null;
    public static final Object BIOMETRIC_ERROR_UNABLE_TO_PROCESS = null;
    public static final Object BIOMETRIC_ERROR_USER_CANCELED = null;
    public static final Object BIOMETRIC_ERROR_VENDOR = null;
    public boolean isConfirmationRequired() { return false; }
    public void onAuthenticationError(Object p0, Object p1) {}
    public void onAuthenticationFailed() {}
    public void onAuthenticationHelp(Object p0, Object p1) {}
    public void onAuthenticationSucceeded(Object p0) {}
}
