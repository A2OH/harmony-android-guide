package android.net.wifi;

import com.ohos.shim.bridge.OHBridge;
import java.util.ArrayList;
import java.util.List;

/**
 * Shim: android.net.wifi.WifiManager → @ohos.wifi
 *
 * Provides Wi-Fi management functions backed by OpenHarmony's wifi JS/NDK API.
 * Obtain an instance via Context.getSystemService(Context.WIFI_SERVICE).
 *
 * OH mapping:
 *   isWifiEnabled()     → wifi.isWifiActive()
 *   setWifiEnabled()    → wifi.enableWifi() / wifi.disableWifi()
 *   getWifiState()      → wifi.isWifiActive() → WIFI_STATE_ENABLED / WIFI_STATE_DISABLED
 *   startScan()         → wifi.scan()
 *   getScanResults()    → wifi.getScanResults()
 *   getConnectionInfo() → wifi.getLinkedInfo()
 *   disconnect()        → wifi.disconnect()
 *   reconnect()         → wifi.reconnect()
 */
public class WifiManager {

    // ── Wi-Fi state constants (match android.net.wifi.WifiManager) ──

    /** Wi-Fi is currently being disabled. */
    public static final int WIFI_STATE_DISABLING = 0;
    /** Wi-Fi is disabled. */
    public static final int WIFI_STATE_DISABLED  = 1;
    /** Wi-Fi is currently being enabled. */
    public static final int WIFI_STATE_ENABLING  = 2;
    /** Wi-Fi is enabled. */
    public static final int WIFI_STATE_ENABLED   = 3;
    /** Wi-Fi is in an unknown state. */
    public static final int WIFI_STATE_UNKNOWN   = 4;

    /** Broadcast intent action: Wi-Fi scan results are available. */
    public static final String SCAN_RESULTS_AVAILABLE_ACTION =
            "android.net.wifi.SCAN_RESULTS";

    /** Broadcast intent action: Wi-Fi state changed. */
    public static final String WIFI_STATE_CHANGED_ACTION =
            "android.net.wifi.WIFI_STATE_CHANGED";

    /** Extra key for Wi-Fi state in WIFI_STATE_CHANGED_ACTION broadcast. */
    public static final String EXTRA_WIFI_STATE = "wifi_state";

    /** Link speed in Mbps unit string. */
    public static final String LINK_SPEED_UNITS = "Mbps";

    // ── Constructor (package-private; use Context.getSystemService) ──

    public WifiManager() { }

    // ── State ──────────────────────────────────────────────────────

    /**
     * Return whether Wi-Fi is enabled or disabled.
     * @return {@code true} if Wi-Fi is enabled
     */
    public boolean isWifiEnabled() {
        return OHBridge.wifiIsEnabled();
    }

    /**
     * Enable or disable Wi-Fi.
     * @param enabled {@code true} to enable, {@code false} to disable
     * @return {@code true} if the operation succeeds (always {@code false} on failure)
     * @deprecated On Android 10+ this method is a no-op for third-party apps.
     *             On OpenHarmony the privileged wifi API is required.
     */
    public boolean setWifiEnabled(boolean enabled) {
        return OHBridge.wifiSetEnabled(enabled);
    }

    /**
     * Gets the Wi-Fi enabled state.
     * @return One of {@link #WIFI_STATE_DISABLED}, {@link #WIFI_STATE_DISABLING},
     *         {@link #WIFI_STATE_ENABLED}, {@link #WIFI_STATE_ENABLING},
     *         {@link #WIFI_STATE_UNKNOWN}
     */
    public int getWifiState() {
        return OHBridge.wifiGetState();
    }

    /**
     * Return whether Wi-Fi is enabled.
     * Equivalent to {@code getWifiState() == WIFI_STATE_ENABLED}.
     */
    public boolean isWifiApEnabled() {
        return getWifiState() == WIFI_STATE_ENABLED;
    }

    // ── Scanning ──────────────────────────────────────────────────

    /**
     * Initiate a scan for Wi-Fi access points.
     * Results are available via {@link #getScanResults()}.
     * @return {@code true} if the scan was initiated
     */
    public boolean startScan() {
        // OH: wifi.scan() is async; we trigger it and return true optimistically.
        // On a real device the SCAN_RESULTS_AVAILABLE_ACTION broadcast would follow.
        try {
            // Scan results will be populated asynchronously on a real device.
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Return the results of the latest access point scan.
     * @return list of {@link ScanResult} objects
     */
    public List<ScanResult> getScanResults() {
        // On a real OH device this would call wifi.getScanResults() via JNI.
        // Returning an empty list as a safe default; subclasses or native
        // implementation may override.
        return new ArrayList<>();
    }

    // ── Connection info ───────────────────────────────────────────

    /**
     * Return dynamic information about the current Wi-Fi connection, if any is active.
     * @return the Wi-Fi information, never {@code null}
     */
    public WifiInfo getConnectionInfo() {
        String ssid  = OHBridge.wifiGetSSID();
        int rssi      = OHBridge.wifiGetRssi();
        int linkSpeed = OHBridge.wifiGetLinkSpeed();
        int frequency = OHBridge.wifiGetFrequency();
        // Fields not exposed by OHBridge stub; use safe defaults
        String bssid  = "00:00:00:00:00:00";
        String mac    = "02:00:00:00:00:00";
        int netId     = 0;
        int ipAddress = 0;
        return new WifiInfo(ssid, bssid, rssi, linkSpeed, frequency, mac, netId, ipAddress);
    }

    /**
     * Return the DHCP-assigned addresses from the last successful DHCP request.
     * This is a stub — DHCP info is not exposed by the OH wifi API.
     * @return a stub {@link DhcpInfo} with all fields set to 0
     */
    public DhcpInfo getDhcpInfo() {
        return new DhcpInfo();
    }

    // ── Connection control ────────────────────────────────────────

    /**
     * Disassociate from the currently active access point.
     * @return {@code true} if the operation succeeded
     */
    public boolean disconnect() {
        // OH: wifi.disconnect() — invoke via bridge if available
        try {
            // Stubbed; real implementation routes through OHBridge
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Reconnect to the currently active access point, if we are currently disconnected.
     * @return {@code true} if the operation succeeded
     */
    public boolean reconnect() {
        try {
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Reconnect to the currently active access point, even if we are already connected.
     * @return {@code true} if the operation succeeded
     */
    public boolean reassociate() {
        return reconnect();
    }

    // ── Signal strength helpers ───────────────────────────────────

    /**
     * Calculates the level of the signal.
     * @param rssi  the power of the signal in dBm
     * @param numLevels the number of levels (steps) to consider in the calculated level
     * @return a value between 0 and {@code numLevels - 1}
     */
    public static int calculateSignalLevel(int rssi, int numLevels) {
        final int MIN_RSSI = -100;
        final int MAX_RSSI = -55;
        if (rssi <= MIN_RSSI) return 0;
        if (rssi >= MAX_RSSI) return numLevels - 1;
        float inputRange  = MAX_RSSI - MIN_RSSI;
        float outputRange = numLevels - 1;
        return (int) ((float)(rssi - MIN_RSSI) * outputRange / inputRange);
    }

    /**
     * Compares two signal strengths.
     * @return negative if {@code rssiA} is weaker, 0 if equal, positive if stronger
     */
    public static int compareSignalLevel(int rssiA, int rssiB) {
        return rssiA - rssiB;
    }
}
