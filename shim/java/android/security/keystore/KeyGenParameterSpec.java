package android.security.keystore;

import java.util.Arrays;

/**
 * Android-compatible KeyGenParameterSpec shim.
 * Describes the parameters for a key-pair or secret-key generation operation
 * destined for the Android Keystore system.
 */
public final class KeyGenParameterSpec {

    private final String keystoreAlias;
    private final int purposes;
    private final int keySize;
    private final String[] blockModes;
    private final String[] encryptionPaddings;
    private final String[] digests;
    private final String[] signaturePaddings;
    private final boolean userAuthenticationRequired;
    private final int userAuthenticationValidityDurationSeconds;
    private final boolean randomizedEncryptionRequired;
    private final boolean invalidatedByBiometricEnrollment;

    private KeyGenParameterSpec(Builder builder) {
        this.keystoreAlias                           = builder.keystoreAlias;
        this.purposes                                = builder.purposes;
        this.keySize                                 = builder.keySize;
        this.blockModes                              = builder.blockModes;
        this.encryptionPaddings                      = builder.encryptionPaddings;
        this.digests                                 = builder.digests;
        this.signaturePaddings                       = builder.signaturePaddings;
        this.userAuthenticationRequired              = builder.userAuthenticationRequired;
        this.userAuthenticationValidityDurationSeconds = builder.userAuthenticationValidityDurationSeconds;
        this.randomizedEncryptionRequired            = builder.randomizedEncryptionRequired;
        this.invalidatedByBiometricEnrollment        = builder.invalidatedByBiometricEnrollment;
    }

    public String getKeystoreAlias() { return keystoreAlias; }
    public int getPurposes()         { return purposes; }
    public int getKeySize()          { return keySize; }

    public String[] getBlockModes() {
        return blockModes == null ? null : blockModes.clone();
    }

    public String[] getEncryptionPaddings() {
        return encryptionPaddings == null ? null : encryptionPaddings.clone();
    }

    public String[] getDigests() {
        return digests == null ? null : digests.clone();
    }

    public String[] getSignaturePaddings() {
        return signaturePaddings == null ? null : signaturePaddings.clone();
    }

    public boolean isUserAuthenticationRequired() {
        return userAuthenticationRequired;
    }

    public int getUserAuthenticationValidityDurationSeconds() {
        return userAuthenticationValidityDurationSeconds;
    }

    public boolean isRandomizedEncryptionRequired() {
        return randomizedEncryptionRequired;
    }

    public boolean isInvalidatedByBiometricEnrollment() {
        return invalidatedByBiometricEnrollment;
    }

    @Override
    public String toString() {
        return "KeyGenParameterSpec{alias=" + keystoreAlias
                + ", purposes=" + purposes
                + ", keySize=" + keySize
                + ", blockModes=" + Arrays.toString(blockModes)
                + ", encryptionPaddings=" + Arrays.toString(encryptionPaddings)
                + ", userAuthRequired=" + userAuthenticationRequired + "}";
    }

    // -----------------------------------------------------------------------
    // Builder
    // -----------------------------------------------------------------------

    public static final class Builder {

        private final String keystoreAlias;
        private final int purposes;
        private int keySize = -1; // -1 means "use algorithm default"
        private String[] blockModes;
        private String[] encryptionPaddings;
        private String[] digests;
        private String[] signaturePaddings;
        private boolean userAuthenticationRequired = false;
        private int userAuthenticationValidityDurationSeconds = -1;
        private boolean randomizedEncryptionRequired = true;
        private boolean invalidatedByBiometricEnrollment = true;

        /**
         * @param keystoreAlias alias under which the key will be stored
         * @param purposes      bitmask of {@link KeyProperties#PURPOSE_ENCRYPT} etc.
         */
        public Builder(String keystoreAlias, int purposes) {
            if (keystoreAlias == null || keystoreAlias.isEmpty()) {
                throw new IllegalArgumentException("keystoreAlias must not be null or empty");
            }
            this.keystoreAlias = keystoreAlias;
            this.purposes      = purposes;
        }

        /** Sets the key size in bits. */
        public Builder setKeySize(int keySize) {
            if (keySize <= 0) throw new IllegalArgumentException("keySize must be positive");
            this.keySize = keySize;
            return this;
        }

        /** Sets the block modes (e.g. "CBC", "GCM"). */
        public Builder setBlockModes(String... blockModes) {
            this.blockModes = blockModes == null ? null : blockModes.clone();
            return this;
        }

        /** Sets the encryption paddings (e.g. "PKCS7Padding", "NoPadding"). */
        public Builder setEncryptionPaddings(String... encryptionPaddings) {
            this.encryptionPaddings = encryptionPaddings == null ? null : encryptionPaddings.clone();
            return this;
        }

        /** Sets the digest algorithms (e.g. "SHA-256"). */
        public Builder setDigests(String... digests) {
            this.digests = digests == null ? null : digests.clone();
            return this;
        }

        /** Sets the signature paddings (e.g. "PKCS1"). */
        public Builder setSignaturePaddings(String... signaturePaddings) {
            this.signaturePaddings = signaturePaddings == null ? null : signaturePaddings.clone();
            return this;
        }

        /**
         * Sets whether user authentication is required before the key can be used.
         *
         * @param required if true, user must authenticate
         * @return this builder
         */
        public Builder setUserAuthenticationRequired(boolean required) {
            this.userAuthenticationRequired = required;
            return this;
        }

        /**
         * Sets how long (in seconds) the key is authorised after authentication.
         * Use -1 for "every use requires authentication".
         */
        public Builder setUserAuthenticationValidityDurationSeconds(int seconds) {
            this.userAuthenticationValidityDurationSeconds = seconds;
            return this;
        }

        /** Controls whether the system must ensure ciphertext randomness (default true). */
        public Builder setRandomizedEncryptionRequired(boolean required) {
            this.randomizedEncryptionRequired = required;
            return this;
        }

        /** Sets whether the key is invalidated on new biometric enrolment (default true). */
        public Builder setInvalidatedByBiometricEnrollment(boolean invalidated) {
            this.invalidatedByBiometricEnrollment = invalidated;
            return this;
        }

        /** Builds the {@link KeyGenParameterSpec}. */
        public KeyGenParameterSpec build() {
            return new KeyGenParameterSpec(this);
        }
    }
}
