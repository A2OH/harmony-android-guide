package android.net.wifi.aware;

/**
 * Android-compatible WifiAwareManager shim. Stub.
 */
public class WifiAwareManager {

    /** Abstract callback for attach operations. */
    public static abstract class AttachCallback {
        public void onAttached(WifiAwareSession session) {}
        public void onAttachFailed() {}
    }

    /** Abstract callback for discovery session operations. */
    public static abstract class DiscoverySessionCallback {
        public void onPublishStarted(Object publishDiscoverySession) {}
        public void onSubscribeStarted(Object subscribeDiscoverySession) {}
        public void onSessionConfigUpdated() {}
        public void onSessionConfigFailed() {}
        public void onSessionTerminated() {}
        public void onServiceDiscovered(Object peerHandle, byte[] serviceSpecificInfo, java.util.List<byte[]> matchFilter) {}
        public void onMessageSendSucceeded(int messageId) {}
        public void onMessageSendFailed(int messageId) {}
        public void onMessageReceived(Object peerHandle, byte[] message) {}
    }

    public boolean isAvailable() {
        return false;
    }

    public void attach(AttachCallback callback, android.os.Handler handler) {
        // Stub: Wi-Fi Aware not available in shim layer
    }

    public void attach(AttachCallback callback, Object identityChangedListener, android.os.Handler handler) {
        // Stub: Wi-Fi Aware not available in shim layer
    }
}
