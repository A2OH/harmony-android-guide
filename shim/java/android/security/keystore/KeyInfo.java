package android.security.keystore;

/**
 * Android-compatible KeyInfo shim. Describes properties of a key stored in AndroidKeyStore.
 */
public class KeyInfo {

    private final String mKeystoreAlias;
    private final int mKeySize;
    private final int mOrigin;
    private final int mPurposes;
    private final String[] mBlockModes;
    private final String[] mEncryptionPaddings;
    private final String[] mDigests;
    private final boolean mInsideSecureHardware;
    private final boolean mUserAuthenticationRequired;
    private final int mUserAuthenticationValidityDurationSeconds;

    public KeyInfo(String keystoreAlias, int keySize, int origin, int purposes,
                   String[] blockModes, String[] encryptionPaddings, String[] digests,
                   boolean insideSecureHardware, boolean userAuthenticationRequired,
                   int userAuthenticationValidityDurationSeconds) {
        mKeystoreAlias = keystoreAlias;
        mKeySize = keySize;
        mOrigin = origin;
        mPurposes = purposes;
        mBlockModes = blockModes != null ? blockModes : new String[0];
        mEncryptionPaddings = encryptionPaddings != null ? encryptionPaddings : new String[0];
        mDigests = digests != null ? digests : new String[0];
        mInsideSecureHardware = insideSecureHardware;
        mUserAuthenticationRequired = userAuthenticationRequired;
        mUserAuthenticationValidityDurationSeconds = userAuthenticationValidityDurationSeconds;
    }

    /** Default stub constructor. */
    public KeyInfo() {
        this("stub", 0, KeyProperties.ORIGIN_GENERATED, 0,
             new String[0], new String[0], new String[0],
             false, false, -1);
    }

    public String getKeystoreAlias() {
        return mKeystoreAlias;
    }

    public int getKeySize() {
        return mKeySize;
    }

    public int getOrigin() {
        return mOrigin;
    }

    public int getPurposes() {
        return mPurposes;
    }

    public String[] getBlockModes() {
        return mBlockModes.clone();
    }

    public String[] getEncryptionPaddings() {
        return mEncryptionPaddings.clone();
    }

    public String[] getDigests() {
        return mDigests.clone();
    }

    public boolean isInsideSecureHardware() {
        return mInsideSecureHardware;
    }

    public boolean isUserAuthenticationRequired() {
        return mUserAuthenticationRequired;
    }

    public int getUserAuthenticationValidityDurationSeconds() {
        return mUserAuthenticationValidityDurationSeconds;
    }
}
