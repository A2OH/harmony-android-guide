package android.security.identity;

/**
 * Android-compatible IdentityCredentialStore shim. Abstract stub for the identity credential store.
 */
public abstract class IdentityCredentialStore {

    public static final String CIPHERSUITE_ECDHE_HKDF_ECDSA_WITH_AES_256_GCM_SHA256 =
            "CIPHERSUITE_ECDHE_HKDF_ECDSA_WITH_AES_256_GCM_SHA256";

    protected IdentityCredentialStore() {}

    /**
     * Returns the default {@link IdentityCredentialStore} for the given context, or null if
     * not available on this device.
     */
    public static IdentityCredentialStore getInstance(Object context) {
        return null; // not available in shim environment
    }

    /**
     * Returns a direct-access {@link IdentityCredentialStore} for the given context, or null if
     * direct access is not supported on this device.
     */
    public static IdentityCredentialStore getDirectAccessInstance(Object context) {
        return null; // not available in shim environment
    }

    /**
     * Creates a new credential with the given name and cipher suite.
     *
     * @param credentialName name of the credential
     * @param cipherSuite    cipher suite to use
     * @return a {@link WritableIdentityCredential} for the new credential
     */
    public abstract WritableIdentityCredential createCredential(
            String credentialName, String cipherSuite) throws Exception;

    /**
     * Retrieves a credential by name.
     *
     * @param credentialName name of the credential
     * @param cipherSuite    cipher suite the credential was created with
     * @return the {@link IdentityCredential}, or null if not found
     */
    public abstract IdentityCredential getCredentialByName(
            String credentialName, String cipherSuite) throws Exception;

    /**
     * Deletes a credential by name.
     *
     * @param credentialName name of the credential to delete
     * @return proof-of-deletion bytes, or null if not found
     */
    public abstract byte[] deleteCredentialByName(String credentialName) throws Exception;

    // ---------------------------------------------------------------------------
    // Inner stub class so callers that reference WritableIdentityCredential compile
    // ---------------------------------------------------------------------------

    /**
     * Minimal stub for WritableIdentityCredential returned by createCredential().
     */
    public static abstract class WritableIdentityCredential {
        protected WritableIdentityCredential() {}

        public abstract byte[] getCredentialKeyCertificateChain(byte[] challenge) throws Exception;

        public abstract void addNamespace(String namespace,
                java.util.Collection<NamespaceEntry> entries) throws Exception;

        public abstract byte[] finishAdding() throws Exception;
    }

    /** Represents a single data entry within a namespace. */
    public static class NamespaceEntry {
        private final String mName;
        private final byte[] mValue;
        private final int[] mAccessControlProfileIds;

        public NamespaceEntry(String name, byte[] value, int[] accessControlProfileIds) {
            mName = name;
            mValue = value != null ? value.clone() : new byte[0];
            mAccessControlProfileIds = accessControlProfileIds != null
                    ? accessControlProfileIds.clone() : new int[0];
        }

        public String getName() { return mName; }
        public byte[] getValue() { return mValue.clone(); }
        public int[] getAccessControlProfileIds() { return mAccessControlProfileIds.clone(); }
    }
}
