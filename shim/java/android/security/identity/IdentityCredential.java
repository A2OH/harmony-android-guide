package android.security.identity;

import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * Android-compatible IdentityCredential shim. Abstract stub for identity credential operations.
 */
public abstract class IdentityCredential {

    protected IdentityCredential() {}

    /**
     * Returns the credential key certificate chain.
     */
    public Collection<X509Certificate> getCredentialKeyCertificateChain() {
        return Collections.emptyList();
    }

    /**
     * Retrieves credential entries. Returns an empty result map stub.
     *
     * @param requestedEntries map of namespace to entry names
     * @param requestMessage   CBOR-encoded session transcript
     * @param readerEphemeralPublicKey reader ephemeral public key bytes (may be null)
     * @return map of namespace to map of entry name to value bytes
     */
    public Map<String, Map<String, byte[]>> getEntries(
            Map<String, Collection<String>> requestedEntries,
            byte[] requestMessage,
            byte[] readerEphemeralPublicKey) {
        return Collections.emptyMap();
    }

    /**
     * Sets the number of available authentication keys.
     *
     * @param keyCount         maximum number of authentication keys
     * @param maxUsesPerKey    maximum number of uses per key
     */
    public void setAvailableAuthenticationKeys(int keyCount, int maxUsesPerKey) {
        // no-op stub
    }

    /**
     * Returns authentication keys that need certification (their certificate is expired or missing).
     */
    public Collection<X509Certificate> getAuthKeysNeedingCertification() {
        return Collections.emptyList();
    }

    /**
     * Stores static authentication data associated with an authentication key.
     *
     * @param authenticationKey the authentication key certificate
     * @param staticAuthData    the static authentication data to store
     */
    public void storeStaticAuthenticationData(X509Certificate authenticationKey,
                                              byte[] staticAuthData) {
        // no-op stub
    }
}
