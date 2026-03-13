package android.net.wifi;
import android.net.wifi.hotspot2.PasspointConfiguration;
import java.net.InetAddress;
import java.util.List;

public class WifiManager {
    public static final int ACTION_PICK_WIFI_NETWORK = 0;
    public static final int ACTION_REQUEST_SCAN_ALWAYS_AVAILABLE = 0;
    public static final int ACTION_WIFI_NETWORK_SUGGESTION_POST_CONNECTION = 0;
    public static final int ACTION_WIFI_SCAN_AVAILABILITY_CHANGED = 0;
    public static final int EXTRA_NETWORK_INFO = 0;
    public static final int EXTRA_NETWORK_SUGGESTION = 0;
    public static final int EXTRA_NEW_RSSI = 0;
    public static final int EXTRA_PREVIOUS_WIFI_STATE = 0;
    public static final int EXTRA_RESULTS_UPDATED = 0;
    public static final int EXTRA_SCAN_AVAILABLE = 0;
    public static final int EXTRA_WIFI_STATE = 0;
    public static final int NETWORK_IDS_CHANGED_ACTION = 0;
    public static final int NETWORK_STATE_CHANGED_ACTION = 0;
    public static final int RSSI_CHANGED_ACTION = 0;
    public static final int SCAN_RESULTS_AVAILABLE_ACTION = 0;
    public static final int STATUS_NETWORK_SUGGESTIONS_ERROR_ADD_DUPLICATE = 0;
    public static final int STATUS_NETWORK_SUGGESTIONS_ERROR_ADD_EXCEEDS_MAX_PER_APP = 0;
    public static final int STATUS_NETWORK_SUGGESTIONS_ERROR_ADD_INVALID = 0;
    public static final int STATUS_NETWORK_SUGGESTIONS_ERROR_ADD_NOT_ALLOWED = 0;
    public static final int STATUS_NETWORK_SUGGESTIONS_ERROR_APP_DISALLOWED = 0;
    public static final int STATUS_NETWORK_SUGGESTIONS_ERROR_INTERNAL = 0;
    public static final int STATUS_NETWORK_SUGGESTIONS_ERROR_REMOVE_INVALID = 0;
    public static final int STATUS_NETWORK_SUGGESTIONS_SUCCESS = 0;
    public static final int STATUS_SUGGESTION_CONNECTION_FAILURE_ASSOCIATION = 0;
    public static final int STATUS_SUGGESTION_CONNECTION_FAILURE_AUTHENTICATION = 0;
    public static final int STATUS_SUGGESTION_CONNECTION_FAILURE_IP_PROVISIONING = 0;
    public static final int STATUS_SUGGESTION_CONNECTION_FAILURE_UNKNOWN = 0;
    public static final int UNKNOWN_SSID = 0;
    public static final int WIFI_MODE_FULL_HIGH_PERF = 0;
    public static final int WIFI_MODE_FULL_LOW_LATENCY = 0;
    public static final int WIFI_STATE_CHANGED_ACTION = 0;
    public static final int WIFI_STATE_DISABLED = 0;
    public static final int WIFI_STATE_DISABLING = 0;
    public static final int WIFI_STATE_ENABLED = 0;
    public static final int WIFI_STATE_ENABLING = 0;
    public static final int WIFI_STATE_UNKNOWN = 0;

    public WifiManager() {}

    public void addOrUpdatePasspointConfiguration(PasspointConfiguration p0) {}
    public static int compareSignalLevel(int p0, int p1) { return 0; }
    public Object createMulticastLock(String p0) { return null; }
    public Object createWifiLock(int p0, String p1) { return null; }
    public WifiInfo getConnectionInfo() { return null; }
    public DhcpInfo getDhcpInfo() { return null; }
    public int getMaxNumberOfNetworkSuggestionsPerApp() { return 0; }
    public List<?> getScanResults() { return null; }
    public int getWifiState() { return 0; }
    public boolean is5GHzBandSupported() { return false; }
    public boolean is6GHzBandSupported() { return false; }
    public boolean isEasyConnectSupported() { return false; }
    public boolean isEnhancedOpenSupported() { return false; }
    public boolean isEnhancedPowerReportingSupported() { return false; }
    public boolean isP2pSupported() { return false; }
    public boolean isPreferredNetworkOffloadSupported() { return false; }
    public boolean isStaApConcurrencySupported() { return false; }
    public boolean isTdlsSupported() { return false; }
    public boolean isWapiSupported() { return false; }
    public boolean isWifiEnabled() { return false; }
    public boolean isWifiStandardSupported(int p0) { return false; }
    public boolean isWpa3SaeSupported() { return false; }
    public boolean isWpa3SuiteBSupported() { return false; }
    public void setTdlsEnabled(InetAddress p0, boolean p1) {}
    public void setTdlsEnabledWithMacAddress(String p0, boolean p1) {}
}
