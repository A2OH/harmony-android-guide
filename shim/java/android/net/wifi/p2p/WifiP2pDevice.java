package android.net.wifi.p2p;

/**
 * Android-compatible WifiP2pDevice shim. Stub — field-only representation.
 */
public class WifiP2pDevice {

    public static final int CONNECTED   = 0;
    public static final int INVITED     = 1;
    public static final int FAILED      = 2;
    public static final int AVAILABLE   = 3;
    public static final int UNAVAILABLE = 4;

    public String deviceName        = "";
    public String deviceAddress     = "";
    public String primaryDeviceType = "";
    public String secondaryDeviceType = "";
    public int    status            = UNAVAILABLE;

    public WifiP2pDevice() {}

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof WifiP2pDevice)) return false;
        WifiP2pDevice other = (WifiP2pDevice) obj;
        if (deviceAddress == null) return other.deviceAddress == null;
        return deviceAddress.equals(other.deviceAddress);
    }

    @Override
    public int hashCode() {
        return deviceAddress == null ? 0 : deviceAddress.hashCode();
    }

    @Override
    public String toString() {
        return "WifiP2pDevice{name=" + deviceName
                + ", address=" + deviceAddress
                + ", status=" + status + "}";
    }
}
