package android.net.wifi.p2p;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Android-compatible WifiP2pGroup shim. Stub — empty group info.
 */
public class WifiP2pGroup {

    private String networkName  = "";
    private boolean groupOwner  = false;
    private WifiP2pDevice owner = null;
    private final java.util.List<WifiP2pDevice> clients = new ArrayList<>();
    private String networkInterface = "";
    private String passphrase       = "";

    public WifiP2pGroup() {}

    public String getNetworkName() {
        return networkName;
    }

    public boolean isGroupOwner() {
        return groupOwner;
    }

    public WifiP2pDevice getOwner() {
        return owner;
    }

    public Collection<WifiP2pDevice> getClientList() {
        return Collections.unmodifiableList(clients);
    }

    public String getInterface() {
        return networkInterface;
    }

    public String getPassphrase() {
        return passphrase;
    }
}
