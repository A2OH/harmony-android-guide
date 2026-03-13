package android.net.wifi.aware;

/**
 * Android-compatible WifiAwareSession shim. Stub.
 */
public class WifiAwareSession implements AutoCloseable {

    public void publish(PublishConfig publishConfig,
                        WifiAwareManager.DiscoverySessionCallback callback,
                        android.os.Handler handler) {
        // Stub: Wi-Fi Aware not available in shim layer
    }

    public void subscribe(SubscribeConfig subscribeConfig,
                          WifiAwareManager.DiscoverySessionCallback callback,
                          android.os.Handler handler) {
        // Stub: Wi-Fi Aware not available in shim layer
    }

    public Object createNetworkSpecifierOpen(int role, Object peerHandle) {
        return null;
    }

    public Object createNetworkSpecifierPassphrase(int role, Object peerHandle, String passphrase) {
        return null;
    }

    @Override
    public void close() {
        // Stub: no-op
    }
}
