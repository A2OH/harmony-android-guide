package android.security;

/**
 * Android-compatible NetworkSecurityPolicy shim. Stub for querying network security policy.
 */
public class NetworkSecurityPolicy {

    private static final NetworkSecurityPolicy sInstance = new NetworkSecurityPolicy();

    private NetworkSecurityPolicy() {}

    /**
     * Returns the singleton NetworkSecurityPolicy instance.
     */
    public static NetworkSecurityPolicy getInstance() {
        return sInstance;
    }

    /**
     * Returns whether cleartext network traffic (HTTP, FTP, etc.) is permitted for all hostnames.
     * In the shim this always returns true to avoid blocking test traffic.
     */
    public boolean isCleartextTrafficPermitted() {
        return true;
    }

    /**
     * Returns whether cleartext network traffic is permitted for the given hostname.
     *
     * @param hostname the hostname to check
     */
    public boolean isCleartextTrafficPermitted(String hostname) {
        return true;
    }
}
