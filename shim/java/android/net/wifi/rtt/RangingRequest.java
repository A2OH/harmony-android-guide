package android.net.wifi.rtt;

import java.util.ArrayList;
import java.util.List;

/**
 * Android-compatible RangingRequest shim. Stub.
 */
public class RangingRequest {

    private static final int MAX_PEERS = 10;

    private final List<Object> peers;

    private RangingRequest(Builder b) {
        this.peers = new ArrayList<>(b.peers);
    }

    public List<Object> getPeers() {
        return peers;
    }

    public static int getMaxPeers() {
        return MAX_PEERS;
    }

    public static class Builder {
        private final List<Object> peers = new ArrayList<>();

        /** Add a single access point (ScanResult) to range against. */
        public Builder addAccessPoint(Object scanResult) {
            peers.add(scanResult);
            return this;
        }

        /** Add a list of access points (ScanResult) to range against. */
        public Builder addAccessPoints(List<?> scanResults) {
            peers.addAll(scanResults);
            return this;
        }

        /** Add a Wi-Fi Aware peer (PeerHandle or MacAddress) to range against. */
        public Builder addWifiAwarePeer(Object peerHandle) {
            peers.add(peerHandle);
            return this;
        }

        public RangingRequest build() {
            return new RangingRequest(this);
        }
    }
}
