package android.net.nsd;

/**
 * Android-compatible NsdManager shim. Stub — no-op network service discovery.
 * Real implementation would use OH mDNS / DNS-SD service.
 */
public class NsdManager {

    public static final int PROTOCOL_DNS_SD = 1;

    // -------------------------------------------------------------------------
    // Listener interfaces
    // -------------------------------------------------------------------------

    public interface RegistrationListener {
        void onRegistrationFailed(NsdServiceInfo serviceInfo, int errorCode);
        void onUnregistrationFailed(NsdServiceInfo serviceInfo, int errorCode);
        void onServiceRegistered(NsdServiceInfo serviceInfo);
        void onServiceUnregistered(NsdServiceInfo serviceInfo);
    }

    public interface DiscoveryListener {
        void onStartDiscoveryFailed(String serviceType, int errorCode);
        void onStopDiscoveryFailed(String serviceType, int errorCode);
        void onDiscoveryStarted(String serviceType);
        void onDiscoveryStopped(String serviceType);
        void onServiceFound(NsdServiceInfo serviceInfo);
        void onServiceLost(NsdServiceInfo serviceInfo);
    }

    public interface ResolveListener {
        void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode);
        void onServiceResolved(NsdServiceInfo serviceInfo);
    }

    // -------------------------------------------------------------------------
    // Manager methods
    // -------------------------------------------------------------------------

    public NsdManager() {}

    public void registerService(NsdServiceInfo serviceInfo, int protocolType,
            RegistrationListener listener) {
        if (listener != null) listener.onServiceRegistered(serviceInfo);
    }

    public void unregisterService(RegistrationListener listener) {
        if (listener != null) listener.onServiceUnregistered(new NsdServiceInfo());
    }

    public void discoverServices(String serviceType, int protocolType,
            DiscoveryListener listener) {
        if (listener != null) listener.onDiscoveryStarted(serviceType);
    }

    public void stopServiceDiscovery(DiscoveryListener listener) {
        if (listener != null) listener.onDiscoveryStopped("");
    }

    public void resolveService(NsdServiceInfo serviceInfo, ResolveListener listener) {
        if (listener != null) listener.onServiceResolved(serviceInfo);
    }
}
