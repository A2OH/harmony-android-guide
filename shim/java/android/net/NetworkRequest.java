package android.net;

import java.util.HashSet;
import java.util.Set;

/**
 * Android-compatible NetworkRequest shim. Describes a desired network configuration.
 */
public class NetworkRequest {

    private final Set<Integer> capabilities;
    private final Set<Integer> transportTypes;

    private NetworkRequest(Builder builder) {
        this.capabilities = new HashSet<>(builder.capabilities);
        this.transportTypes = new HashSet<>(builder.transportTypes);
    }

    public boolean hasCapability(int capability) {
        return capabilities.contains(capability);
    }

    public boolean hasTransport(int transport) {
        return transportTypes.contains(transport);
    }

    public static final class Builder {
        private final Set<Integer> capabilities = new HashSet<>();
        private final Set<Integer> transportTypes = new HashSet<>();

        public Builder() {}

        public Builder addCapability(int capability) {
            capabilities.add(capability);
            return this;
        }

        public Builder removeCapability(int capability) {
            capabilities.remove(capability);
            return this;
        }

        public Builder addTransportType(int transportType) {
            transportTypes.add(transportType);
            return this;
        }

        public Builder removeTransportType(int transportType) {
            transportTypes.remove(transportType);
            return this;
        }

        public NetworkRequest build() {
            return new NetworkRequest(this);
        }
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
}
