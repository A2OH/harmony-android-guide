package android.net.wifi;

import java.util.BitSet;

/**
 * Android-compatible WifiConfiguration shim. Stub.
 */
public class WifiConfiguration {

    /** SSID of the network, e.g., {@code "\"MyNetwork\""}. */
    public String SSID;

    /** BSSID of the access point. */
    public String BSSID;

    /** Pre-shared key for WPA/WPA2-Personal networks. */
    public String preSharedKey;

    /** Network ID assigned by the Wi-Fi framework. */
    public int networkId = -1;

    /** Current status of this network configuration. */
    public int status = Status.DISABLED;

    /** Whether this is a hidden SSID network. */
    public boolean hiddenSSID;

    /** Priority hint (deprecated in Android 26+, kept for compat). */
    public int priority;

    /** Allowed key management schemes. */
    public BitSet allowedKeyManagement = new BitSet();

    /** Allowed security protocols. */
    public BitSet allowedProtocols = new BitSet();

    /** Allowed authentication algorithms. */
    public BitSet allowedAuthAlgorithms = new BitSet();

    /** Allowed pairwise ciphers for WPA. */
    public BitSet allowedPairwiseCiphers = new BitSet();

    /** Allowed group ciphers. */
    public BitSet allowedGroupCiphers = new BitSet();

    // -----------------------------------------------------------------------
    // Inner constant classes
    // -----------------------------------------------------------------------

    public static class KeyMgmt {
        public static final int NONE    = 0;
        public static final int WPA_PSK = 1;
        public static final int WPA_EAP = 2;
        public static final int IEEE8021X = 3;
        public static final int WPA2_PSK  = 4;
        public static final int OSEN      = 5;
        public static final int FT_PSK    = 11;
        public static final int FT_EAP    = 12;

        private KeyMgmt() {}
    }

    public static class Protocol {
        public static final int WPA  = 0;
        public static final int RSN  = 1;
        public static final int OSEN = 2;

        private Protocol() {}
    }

    public static class AuthAlgorithm {
        public static final int OPEN   = 0;
        public static final int SHARED = 1;
        public static final int LEAP   = 2;

        private AuthAlgorithm() {}
    }

    public static class PairwiseCipher {
        public static final int NONE = 0;
        public static final int TKIP = 1;
        public static final int CCMP = 2;

        private PairwiseCipher() {}
    }

    public static class GroupCipher {
        public static final int WEP40  = 0;
        public static final int WEP104 = 1;
        public static final int TKIP   = 2;
        public static final int CCMP   = 3;
        public static final int GTK_NOT_USED = 4;

        private GroupCipher() {}
    }

    public static class Status {
        public static final int CURRENT  = 0;
        public static final int DISABLED = 1;
        public static final int ENABLED  = 2;

        private Status() {}
    }

    public WifiConfiguration() {
        // defaults set at field declarations
    }

    @Override
    public String toString() {
        return "WifiConfiguration{SSID=" + SSID + ", networkId=" + networkId + "}";
    }
}
