package android.security.keystore;

public final class KeyProtection {
    public KeyProtection() {}

    public int getPurposes() { return 0; }
    public int getUserAuthenticationType() { return 0; }
    public int getUserAuthenticationValidityDurationSeconds() { return 0; }
    public boolean isDigestsSpecified() { return false; }
    public boolean isInvalidatedByBiometricEnrollment() { return false; }
    public boolean isRandomizedEncryptionRequired() { return false; }
    public boolean isUnlockedDeviceRequired() { return false; }
    public boolean isUserAuthenticationRequired() { return false; }
    public boolean isUserAuthenticationValidWhileOnBody() { return false; }
    public boolean isUserConfirmationRequired() { return false; }
    public boolean isUserPresenceRequired() { return false; }
}
