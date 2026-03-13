package android.net.wifi.aware;
import android.net.NetworkSpecifier;
import android.os.Handler;

public class WifiAwareSession implements AutoCloseable {
    public WifiAwareSession() {}

    public void close() {}
    public NetworkSpecifier createNetworkSpecifierOpen(int p0, byte[] p1) { return null; }
    public NetworkSpecifier createNetworkSpecifierPassphrase(int p0, byte[] p1, String p2) { return null; }
    public void publish(PublishConfig p0, DiscoverySessionCallback p1, Handler p2) {}
    public void subscribe(SubscribeConfig p0, DiscoverySessionCallback p1, Handler p2) {}
}
