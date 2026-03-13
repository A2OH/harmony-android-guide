package android.net.nsd;

public final class NsdManager {
    public static final int ACTION_NSD_STATE_CHANGED = 0;
    public static final int EXTRA_NSD_STATE = 0;
    public static final int FAILURE_ALREADY_ACTIVE = 0;
    public static final int FAILURE_INTERNAL_ERROR = 0;
    public static final int FAILURE_MAX_LIMIT = 0;
    public static final int NSD_STATE_DISABLED = 0;
    public static final int NSD_STATE_ENABLED = 0;
    public static final int PROTOCOL_DNS_SD = 0;

    public NsdManager() {}

    public void discoverServices(String p0, int p1, Object p2) {}
    public void registerService(NsdServiceInfo p0, int p1, Object p2) {}
    public void resolveService(NsdServiceInfo p0, Object p1) {}
    public void stopServiceDiscovery(Object p0) {}
    public void unregisterService(Object p0) {}
}
