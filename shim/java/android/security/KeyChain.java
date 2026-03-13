package android.security;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;

/**
 * Android-compatible KeyChain stub for OpenHarmony migration.
 * Provides no-op implementations of keychain credential access APIs.
 */
public class KeyChain {

    /** Callback interface for private key alias selection. */
    public interface KeyChainAliasCallback {
        void alias(String alias);
    }

    private KeyChain() {}

    /**
     * Returns the private key for the given alias, or null if unavailable.
     * Stub: always returns null.
     */
    public static PrivateKey getPrivateKey(Object context, String alias)
            throws Exception {
        return null;
    }

    /**
     * Returns the certificate chain for the given alias, or null if unavailable.
     * Stub: always returns null.
     */
    public static X509Certificate[] getCertificateChain(Object context, String alias)
            throws Exception {
        return null;
    }

    /**
     * Launches the system's key chooser activity.
     * Stub: immediately delivers null alias to callback if non-null.
     *
     * @param activity  the calling Activity (typed as Object to avoid Activity dependency)
     * @param callback  receives the chosen alias or null on cancellation
     * @param keyTypes  acceptable key algorithm types, or null for any
     * @param issuers   acceptable CA issuers, or null for any
     * @param uri       the URI being accessed (may be null)
     * @param alias     a pre-selected alias (may be null)
     */
    public static void choosePrivateKeyAlias(Object activity,
                                             KeyChainAliasCallback callback,
                                             String[] keyTypes,
                                             Object[] issuers,
                                             Object uri,
                                             String alias) {
        if (callback != null) {
            callback.alias(null);
        }
    }
}
