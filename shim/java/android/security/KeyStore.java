package android.security;

import java.io.InputStream;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Android-compatible KeyStore shim. Wraps a thin in-memory key store.
 * <p>
 * This shim is intentionally separate from {@link java.security.KeyStore} to
 * allow Android code that references {@code android.security.KeyStore} to
 * compile and run without modification.  For real cryptographic operations,
 * delegate to the JDK's {@link java.security.KeyStore}.
 */
public class KeyStore {

    /** Provider name used in real Android for the hardware-backed key store. */
    public static final String PROVIDER_ANDROID_KEYSTORE = "AndroidKeyStore";

    private final String provider;
    private boolean loaded = false;

    // Simple alias → entry map
    private final Map<String, Entry> entries = new LinkedHashMap<>();

    private KeyStore(String provider) {
        this.provider = provider;
    }

    /**
     * Returns a KeyStore instance for the given provider.
     *
     * @param provider e.g. {@link #PROVIDER_ANDROID_KEYSTORE}
     * @return a new KeyStore instance
     * @throws KeyStoreException if the provider is unrecognised
     */
    public static KeyStore getInstance(String provider) throws KeyStoreException {
        return new KeyStore(provider);
    }

    /**
     * Initialises (loads) the key store. The stream and password are ignored in
     * this shim; pass {@code null} for both.
     */
    public void load(InputStream stream, char[] password)
            throws java.io.IOException, NoSuchAlgorithmException, CertificateException {
        loaded = true;
    }

    private void checkLoaded() throws KeyStoreException {
        if (!loaded) throw new KeyStoreException("KeyStore has not been loaded (call load() first)");
    }

    /**
     * Returns the entry associated with the given alias, or {@code null}.
     */
    public Entry getEntry(String alias, ProtectionParameter param) throws KeyStoreException {
        checkLoaded();
        return entries.get(alias);
    }

    /**
     * Stores an entry under the given alias.
     */
    public void setEntry(String alias, Entry entry, ProtectionParameter param) throws KeyStoreException {
        checkLoaded();
        if (alias == null) throw new KeyStoreException("alias cannot be null");
        entries.put(alias, entry);
    }

    /**
     * Deletes the entry for the given alias. No-op if not present.
     */
    public void deleteEntry(String alias) throws KeyStoreException {
        checkLoaded();
        entries.remove(alias);
    }

    /**
     * Returns {@code true} if an entry exists for the given alias.
     */
    public boolean containsAlias(String alias) throws KeyStoreException {
        checkLoaded();
        return entries.containsKey(alias);
    }

    /**
     * Returns an enumeration of all alias names.
     */
    public Enumeration<String> aliases() throws KeyStoreException {
        checkLoaded();
        return Collections.enumeration(entries.keySet());
    }

    /**
     * Returns the number of entries.
     */
    public int size() throws KeyStoreException {
        checkLoaded();
        return entries.size();
    }

    public String getProvider() {
        return provider;
    }

    // -----------------------------------------------------------------------
    // Nested marker interfaces mirroring java.security.KeyStore types
    // -----------------------------------------------------------------------

    /** Marker interface for key store entries. */
    public interface Entry {}

    /** Marker interface for protection parameters. */
    public interface ProtectionParameter {}

    /** A simple secret-key entry. */
    public static class SecretKeyEntry implements Entry {
        private final javax.crypto.SecretKey secretKey;
        public SecretKeyEntry(javax.crypto.SecretKey secretKey) {
            this.secretKey = secretKey;
        }
        public javax.crypto.SecretKey getSecretKey() { return secretKey; }
    }

    /** A password-based protection parameter. */
    public static class PasswordProtection implements ProtectionParameter {
        private final char[] password;
        public PasswordProtection(char[] password) {
            this.password = password == null ? null : password.clone();
        }
        public char[] getPassword() {
            return password == null ? null : password.clone();
        }
    }
}
