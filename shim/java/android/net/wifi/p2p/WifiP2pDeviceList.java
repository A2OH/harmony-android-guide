package android.net.wifi.p2p;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * Android-compatible WifiP2pDeviceList shim. Stub — empty peer list.
 */
public class WifiP2pDeviceList {

    private final java.util.List<WifiP2pDevice> mDevices = new ArrayList<>();

    public WifiP2pDeviceList() {}

    public Collection<WifiP2pDevice> getDeviceList() {
        return Collections.unmodifiableList(mDevices);
    }

    public WifiP2pDevice get(String deviceAddress) {
        for (WifiP2pDevice d : mDevices) {
            if (d.deviceAddress != null && d.deviceAddress.equals(deviceAddress)) {
                return d;
            }
        }
        return null;
    }
}
