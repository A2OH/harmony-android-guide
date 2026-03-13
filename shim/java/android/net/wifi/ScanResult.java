package android.net.wifi;

/**
 * Shim: android.net.wifi.ScanResult — represents a single Wi-Fi scan result.
 * Populated by WifiManager.getScanResults().
 */
public class ScanResult {

    /** The network name (SSID). */
    public String SSID;

    /** The address of the access point. */
    public String BSSID;

    /** The capabilities of the access point (security type, etc.). */
    public String capabilities;

    /** The detected signal level in dBm, also known as the RSSI. */
    public int level;

    /** The primary 20 MHz frequency (in MHz) of the channel over which the client is communicating. */
    public int frequency;

    /** Timestamp in microseconds (since boot) when this result was last seen. */
    public long timestamp;

    /** Package-private constructor. */
    ScanResult(String ssid, String bssid, String capabilities, int level, int frequency, long timestamp) {
        this.SSID = ssid;
        this.BSSID = bssid;
        this.capabilities = capabilities;
        this.level = level;
        this.frequency = frequency;
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "ScanResult{SSID=" + SSID
                + ", BSSID=" + BSSID
                + ", level=" + level + "dBm"
                + ", freq=" + frequency + "MHz"
                + ", caps=" + capabilities
                + "}";
    }
}
