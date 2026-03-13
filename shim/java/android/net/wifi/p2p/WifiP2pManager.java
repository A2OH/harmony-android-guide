package android.net.wifi.p2p;

import java.util.Collection;

/**
 * Android-compatible WifiP2pManager shim. Stub — no-op Wi-Fi Direct operations.
 * Real implementation would use OH Wi-Fi P2P service.
 */
public class WifiP2pManager {

    public static final String WIFI_P2P_STATE_CHANGED_ACTION =
            "android.net.wifi.p2p.STATE_CHANGED";
    public static final String WIFI_P2P_PEERS_CHANGED_ACTION =
            "android.net.wifi.p2p.PEERS_CHANGED";
    public static final String WIFI_P2P_CONNECTION_CHANGED_ACTION =
            "android.net.wifi.p2p.CONNECTION_STATE_CHANGE";

    // -------------------------------------------------------------------------
    // Listener interfaces
    // -------------------------------------------------------------------------

    public interface ActionListener {
        void onSuccess();
        void onFailure(int reason);
    }

    public interface PeerListListener {
        void onPeersAvailable(WifiP2pDeviceList peers);
    }

    public interface ConnectionInfoListener {
        void onConnectionInfoAvailable(WifiP2pInfo info);
    }

    public interface GroupInfoListener {
        void onGroupInfoAvailable(WifiP2pGroup group);
    }

    // -------------------------------------------------------------------------
    // Channel inner class
    // -------------------------------------------------------------------------

    public static class Channel {
        private Channel() {}

        public void close() {}
    }

    // -------------------------------------------------------------------------
    // Manager methods
    // -------------------------------------------------------------------------

    /**
     * Creates and returns a Channel bound to the supplied Looper.
     * @param context application context (typed as Object for shim compatibility)
     * @param looper  may be null in stub
     * @return a new Channel stub
     */
    public Channel initialize(Object context, Object looper, Object listener) {
        return new Channel();
    }

    public void discoverPeers(Channel channel, ActionListener listener) {
        if (listener != null) listener.onSuccess();
    }

    public void connect(Channel channel, Object config, ActionListener listener) {
        if (listener != null) listener.onSuccess();
    }

    public void cancelConnect(Channel channel, ActionListener listener) {
        if (listener != null) listener.onSuccess();
    }

    public void requestConnectionInfo(Channel channel, ConnectionInfoListener listener) {
        if (listener != null) listener.onConnectionInfoAvailable(new WifiP2pInfo());
    }

    public void requestPeers(Channel channel, PeerListListener listener) {
        if (listener != null) listener.onPeersAvailable(new WifiP2pDeviceList());
    }

    public void createGroup(Channel channel, ActionListener listener) {
        if (listener != null) listener.onSuccess();
    }

    public void removeGroup(Channel channel, ActionListener listener) {
        if (listener != null) listener.onSuccess();
    }
}
