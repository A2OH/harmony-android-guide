package android.net;
import java.net.URI;
import java.net.URL;

/**
 * Android-compatible ProxyInfo stub — describes HTTP proxy settings.
 *
 * Note: the real API uses {@code android.net.Uri} for PAC file URLs.  Because
 * {@code android.net.Uri} does not yet exist in this shim tree, PAC-file URLs
 * are represented here as plain {@code String} values internally and exposed
 * via {@link #getPacFileUrl()} returning a {@code String}.  The factory method
 * {@link #buildPacProxy(Object)} accepts the URI as a raw {@code Object} so
 * callers that already have an {@code android.net.Uri} value can pass it
 * without a compile error; {@code toString()} is called on it internally.
 */
public final class ProxyInfo {

    private final String mHost;
    private final int    mPort;
    private final String mPacFileUrl;       // null for direct / host-port proxies
    private final String[] mExclusionList;  // may be empty, never null

    private ProxyInfo(String host, int port, String pacFileUrl, String[] exclusionList) {
        mHost          = host;
        mPort          = port;
        mPacFileUrl    = pacFileUrl;
        mExclusionList = exclusionList != null ? exclusionList : new String[0];
    }

    // ---- Factory methods ----

    /**
     * Constructs a {@code ProxyInfo} for a host:port HTTP proxy with no PAC file.
     *
     * @param host hostname or IP of the proxy server
     * @param port TCP port of the proxy server
     * @return a new {@code ProxyInfo} instance
     */
    public static ProxyInfo buildDirectProxy(String host, int port) {
        return new ProxyInfo(host, port, null, null);
    }

    /**
     * Constructs a {@code ProxyInfo} for a host:port HTTP proxy with an exclusion list.
     *
     * @param host          hostname or IP of the proxy server
     * @param port          TCP port of the proxy server
     * @param exclusionList hosts that should bypass the proxy (comma-separated or as array)
     * @return a new {@code ProxyInfo} instance
     */
    public static ProxyInfo buildDirectProxy(String host, int port, String[] exclusionList) {
        return new ProxyInfo(host, port, null, exclusionList);
    }

    /**
     * Constructs a {@code ProxyInfo} for a PAC-file-based proxy.
     *
     * <p>The {@code pacUri} parameter is typed as {@code Object} because
     * {@code android.net.Uri} is not yet present in this shim tree.  Pass either
     * an {@code android.net.Uri} instance or a plain {@code String}; {@code toString()}
     * is called to obtain the URL string.
     *
     * @param pacUri URI of the PAC file (may be a String or an android.net.Uri)
     * @return a new {@code ProxyInfo} instance
     */
    public static ProxyInfo buildPacProxy(Object pacUri) {
        String url = (pacUri != null) ? pacUri.toString() : null;
        return new ProxyInfo(null, -1, url, null);
    }

    /**
     * Constructs a {@code ProxyInfo} for a PAC-file-based proxy with a port hint.
     *
     * @param pacUri URI of the PAC file
     * @param port   optional port hint (-1 means unset)
     * @return a new {@code ProxyInfo} instance
     */
    public static ProxyInfo buildPacProxy(Object pacUri, int port) {
        String url = (pacUri != null) ? pacUri.toString() : null;
        return new ProxyInfo(null, port, url, null);
    }

    // ---- Accessors ----

    /** Returns the hostname of the proxy, or {@code null} for PAC-based proxies. */
    public String getHost() { return mHost; }

    /** Returns the port of the proxy, or {@code -1} if unset. */
    public int getPort() { return mPort; }

    /**
     * Returns the PAC file URL as a {@code String}, or {@code null} if this is a
     * direct host:port proxy.
     *
     * <p>In the real Android API this returns an {@code android.net.Uri}; the shim
     * returns a {@code String} to avoid a missing-class dependency.
     */
    public String getPacFileUrl() { return mPacFileUrl; }

    /**
     * Returns the array of host patterns that should bypass the proxy.
     * Never {@code null}; may be empty.
     */
    public String[] getExclusionList() { return mExclusionList; }

    /** Returns {@code true} if a PAC URL is configured. */
    public boolean isPacProxy() { return mPacFileUrl != null; }

    @Override
    public String toString() {
        if (mPacFileUrl != null) {
            return "ProxyInfo{pac=" + mPacFileUrl + "}";
        }
        return "ProxyInfo{host=" + mHost + ":" + mPort + "}";
    }
}
