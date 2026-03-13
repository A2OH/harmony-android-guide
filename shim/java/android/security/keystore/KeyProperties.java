package android.security.keystore;

/**
 * Android-compatible KeyProperties shim. Pure constants for keystore key attributes.
 */
public abstract class KeyProperties {

    // Purposes
    public static final int PURPOSE_ENCRYPT    = 1;
    public static final int PURPOSE_DECRYPT    = 2;
    public static final int PURPOSE_SIGN       = 4;
    public static final int PURPOSE_VERIFY     = 8;
    public static final int PURPOSE_WRAP_KEY   = 32;

    // Key algorithms
    public static final String KEY_ALGORITHM_RSA          = "RSA";
    public static final String KEY_ALGORITHM_EC           = "EC";
    public static final String KEY_ALGORITHM_AES          = "AES";
    public static final String KEY_ALGORITHM_HMAC_SHA256  = "HmacSHA256";

    // Block modes
    public static final String BLOCK_MODE_ECB = "ECB";
    public static final String BLOCK_MODE_CBC = "CBC";
    public static final String BLOCK_MODE_CTR = "CTR";
    public static final String BLOCK_MODE_GCM = "GCM";

    // Encryption paddings
    public static final String ENCRYPTION_PADDING_NONE      = "NoPadding";
    public static final String ENCRYPTION_PADDING_PKCS7     = "PKCS7Padding";
    public static final String ENCRYPTION_PADDING_RSA_PKCS1 = "PKCS1Padding";
    public static final String ENCRYPTION_PADDING_RSA_OAEP  = "OAEPPadding";

    // Digests
    public static final String DIGEST_NONE   = "NONE";
    public static final String DIGEST_SHA256 = "SHA-256";
    public static final String DIGEST_SHA512 = "SHA-512";

    // Origins
    public static final int ORIGIN_GENERATED = 1;
    public static final int ORIGIN_IMPORTED  = 2;

    private KeyProperties() {}
}
