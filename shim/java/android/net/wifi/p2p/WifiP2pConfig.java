package android.net.wifi.p2p;

/**
 * Android-compatible WifiP2pConfig shim.
 * Stub providing field and Builder API surface for compilation.
 */
public class WifiP2pConfig {
    public String deviceAddress;
    public int groupOwnerIntent = -1;

    public WifiP2pConfig() {}

    public static class Builder {
        private String deviceAddress;
        private int groupOperatingBand;
        private int groupOperatingFrequency;
        private String networkName;
        private String passphrase;

        public Builder() {}

        public Builder setDeviceAddress(Object deviceAddress) {
            if (deviceAddress != null) {
                this.deviceAddress = deviceAddress.toString();
            }
            return this;
        }

        public Builder setGroupOperatingBand(int band) {
            this.groupOperatingBand = band;
            return this;
        }

        public Builder setGroupOperatingFrequency(int frequency) {
            this.groupOperatingFrequency = frequency;
            return this;
        }

        public Builder setNetworkName(String networkName) {
            this.networkName = networkName;
            return this;
        }

        public Builder setPassphrase(String passphrase) {
            this.passphrase = passphrase;
            return this;
        }

        public WifiP2pConfig build() {
            WifiP2pConfig config = new WifiP2pConfig();
            config.deviceAddress = this.deviceAddress;
            return config;
        }
    }
}
