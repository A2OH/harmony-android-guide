package android.net.wifi.p2p;

import java.net.InetAddress;

/**
 * Android-compatible WifiP2pInfo shim. Stub — default connection state.
 */
public class WifiP2pInfo {

    public boolean     groupFormed      = false;
    public boolean     isGroupOwner     = false;
    public InetAddress groupOwnerAddress = null;

    public WifiP2pInfo() {}

    @Override
    public String toString() {
        return "WifiP2pInfo{groupFormed=" + groupFormed
                + ", isGroupOwner=" + isGroupOwner
                + ", groupOwnerAddress=" + groupOwnerAddress + "}";
    }
}
