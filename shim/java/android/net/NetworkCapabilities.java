package android.net;

import java.util.HashSet;
import java.util.Set;

/**
 * Android-compatible NetworkCapabilities shim. Describes the capabilities of a connected network.
 */
public class NetworkCapabilities {

    private final Set<Integer> capabilities = new HashSet<>();
    private final Set<Integer> transportTypes = new HashSet<>();
    private int downstreamBandwidthKbps = 10000; // mock: 10 Mbps
    private int upstreamBandwidthKbps   = 5000;  // mock: 5 Mbps

    public NetworkCapabilities() {
        // default mock: internet, trusted, wifi
        capabilities.add(NET_CAPABILITY_INTERNET);
        capabilities.add(NET_CAPABILITY_TRUSTED);
        capabilities.add(NET_CAPABILITY_NOT_RESTRICTED);
        capabilities.add(NET_CAPABILITY_VALIDATED);
        transportTypes.add(TRANSPORT_WIFI);
    }

    /** @hide package-accessible constructor for test use */
    NetworkCapabilities(Set<Integer> caps, Set<Integer> transports,
                        int downKbps, int upKbps) {
        capabilities.addAll(caps);
        transportTypes.addAll(transports);
        this.downstreamBandwidthKbps = downKbps;
        this.upstreamBandwidthKbps   = upKbps;
    }

    public boolean hasCapability(int capability) {
        return capabilities.contains(capability);
    }

    public boolean hasTransport(int transport) {
        return transportTypes.contains(transport);
    }

    public int getLinkDownstreamBandwidthKbps() {
        return downstreamBandwidthKbps;
    }

    public int getLinkUpstreamBandwidthKbps() {
        return upstreamBandwidthKbps;
    }

    // NET_CAPABILITY constants
    public static final int NET_CAPABILITY_MMS              = 0;
    public static final int NET_CAPABILITY_SUPL             = 1;
    public static final int NET_CAPABILITY_DUN              = 2;
    public static final int NET_CAPABILITY_FOTA             = 3;
    public static final int NET_CAPABILITY_IMS              = 4;
    public static final int NET_CAPABILITY_CBS              = 5;
    public static final int NET_CAPABILITY_WIFI_P2P         = 6;
    public static final int NET_CAPABILITY_IA               = 7;
    public static final int NET_CAPABILITY_RCS              = 8;
    public static final int NET_CAPABILITY_XCAP             = 9;
    public static final int NET_CAPABILITY_EIMS             = 10;
    public static final int NET_CAPABILITY_NOT_METERED      = 11;
    public static final int NET_CAPABILITY_INTERNET         = 12;
    public static final int NET_CAPABILITY_NOT_RESTRICTED   = 13;
    public static final int NET_CAPABILITY_TRUSTED          = 14;
    public static final int NET_CAPABILITY_NOT_VPN          = 15;
    public static final int NET_CAPABILITY_VALIDATED        = 16;
    public static final int NET_CAPABILITY_CAPTIVE_PORTAL   = 17;

    // TRANSPORT constants
    public static final int TRANSPORT_CELLULAR   = 0;
    public static final int TRANSPORT_WIFI       = 1;
    public static final int TRANSPORT_BLUETOOTH  = 2;
    public static final int TRANSPORT_ETHERNET   = 3;
    public static final int TRANSPORT_VPN        = 4;
    public static final int TRANSPORT_WIFI_AWARE = 5;
    public static final int TRANSPORT_LOWPAN     = 6;

    @Override
    public String toString() {
        return "NetworkCapabilities[caps=" + capabilities + ", transports=" + transportTypes
                + ", downKbps=" + downstreamBandwidthKbps + ", upKbps=" + upstreamBandwidthKbps + "]";
    }
}
