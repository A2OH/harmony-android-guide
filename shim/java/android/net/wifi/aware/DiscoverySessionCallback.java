package android.net.wifi.aware;
import java.util.List;

public class DiscoverySessionCallback {
    public DiscoverySessionCallback() {}

    public void onMessageReceived(PeerHandle p0, byte[] p1) {}
    public void onMessageSendFailed(int p0) {}
    public void onMessageSendSucceeded(int p0) {}
    public void onPublishStarted(PublishDiscoverySession p0) {}
    public void onServiceDiscovered(PeerHandle p0, byte[] p1, java.util.List<Object> p2) {}
    public void onServiceDiscoveredWithinRange(PeerHandle p0, byte[] p1, java.util.List<Object> p2, int p3) {}
    public void onSessionConfigFailed() {}
    public void onSessionConfigUpdated() {}
    public void onSessionTerminated() {}
    public void onSubscribeStarted(SubscribeDiscoverySession p0) {}
}
