package android.net.wifi;

/**
 * Shim: android.net.wifi.WifiInfo — maps to @ohos.wifi (wifi.getLinkedInfo).
 *
 * Holds connection details for the currently associated Wi-Fi network.
 * Populated by WifiManager.getConnectionInfo().
 */
public class WifiInfo {

    private String mSsid;
    private String mBssid;
    private int mRssi;
    private int mLinkSpeed;
    private int mFrequency;
    private String mMacAddress;
    private int mNetworkId;
    private int mIpAddress;

    /** Package-private constructor used by WifiManager. */
    WifiInfo(String ssid, String bssid, int rssi, int linkSpeed,
             int frequency, String macAddress, int networkId, int ipAddress) {
        mSsid = ssid;
        mBssid = bssid;
        mRssi = rssi;
        mLinkSpeed = linkSpeed;
        mFrequency = frequency;
        mMacAddress = macAddress;
        mNetworkId = networkId;
        mIpAddress = ipAddress;
    }

    /**
     * Returns the service set identifier (SSID) of the current 802.11 network.
     * Android traditionally wraps the SSID in double-quotes for UTF-8 names.
     */
    public String getSSID() {
        return mSsid;
    }

    /** Returns the basic service set identifier (BSSID) of the access point. */
    public String getBSSID() {
        return mBssid;
    }

    /** Returns the received signal strength indicator of the current 802.11 network, in dBm. */
    public int getRssi() {
        return mRssi;
    }

    /** Returns the current link speed in {@link #LINK_SPEED_UNITS}. */
    public int getLinkSpeed() {
        return mLinkSpeed;
    }

    /** Returns the frequency (in MHz) of the channel over which the client is communicating. */
    public int getFrequency() {
        return mFrequency;
    }

    /** Returns the MAC address of the client. */
    public String getMacAddress() {
        return mMacAddress;
    }

    /** Returns the ID number that the supplicant uses to identify this network configuration entry. */
    public int getNetworkId() {
        return mNetworkId;
    }

    /**
     * Returns the IP address of the connected device, in big-endian byte order.
     * Each byte of the four bytes of the IP address is encoded in one byte of the integer.
     */
    public int getIpAddress() {
        return mIpAddress;
    }

    /** Units for link speed: Mbps. */
    public static final String LINK_SPEED_UNITS = "Mbps";

    @Override
    public String toString() {
        return "WifiInfo{SSID=" + mSsid
                + ", BSSID=" + mBssid
                + ", RSSI=" + mRssi + "dBm"
                + ", speed=" + mLinkSpeed + "Mbps"
                + ", freq=" + mFrequency + "MHz"
                + ", mac=" + mMacAddress
                + ", netId=" + mNetworkId
                + "}";
    }
}
