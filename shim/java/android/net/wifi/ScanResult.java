package android.net.wifi;

public final class ScanResult {
    public ScanResult() {}

    public int BSSID = 0;
    public static final int CHANNEL_WIDTH_160MHZ = 0;
    public static final int CHANNEL_WIDTH_20MHZ = 0;
    public static final int CHANNEL_WIDTH_40MHZ = 0;
    public static final int CHANNEL_WIDTH_80MHZ = 0;
    public static final int CHANNEL_WIDTH_80MHZ_PLUS_MHZ = 0;
    public int SSID = 0;
    public static final int WIFI_STANDARD_11AC = 0;
    public static final int WIFI_STANDARD_11AX = 0;
    public static final int WIFI_STANDARD_11N = 0;
    public static final int WIFI_STANDARD_LEGACY = 0;
    public static final int WIFI_STANDARD_UNKNOWN = 0;
    public int capabilities = 0;
    public int centerFreq0 = 0;
    public int centerFreq1 = 0;
    public int channelWidth = 0;
    public int frequency = 0;
    public int level = 0;
    public int operatorFriendlyName = 0;
    public int timestamp = 0;
    public int venueName = 0;
    public int describeContents() { return 0; }
    public int getWifiStandard() { return 0; }
    public boolean is80211mcResponder() { return false; }
    public boolean isPasspointNetwork() { return false; }
    public void writeToParcel(Object p0, Object p1) {}
}
