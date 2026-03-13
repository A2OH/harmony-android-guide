package android.net.wifi.aware;

import java.util.List;

/**
 * Android-compatible SubscribeConfig shim. Stub.
 */
public class SubscribeConfig {

    public static final int SUBSCRIBE_TYPE_PASSIVE = 0;
    public static final int SUBSCRIBE_TYPE_ACTIVE  = 1;

    private final String serviceName;
    private final byte[] serviceSpecificInfo;
    private final List<byte[]> matchFilter;
    private final int subscribeType;

    private SubscribeConfig(Builder b) {
        this.serviceName       = b.serviceName;
        this.serviceSpecificInfo = b.serviceSpecificInfo;
        this.matchFilter       = b.matchFilter;
        this.subscribeType     = b.subscribeType;
    }

    public String getServiceName()           { return serviceName; }
    public byte[] getServiceSpecificInfo()   { return serviceSpecificInfo; }
    public List<byte[]> getMatchFilter()     { return matchFilter; }
    public int getSubscribeType()            { return subscribeType; }

    public static class Builder {
        private String serviceName;
        private byte[] serviceSpecificInfo;
        private List<byte[]> matchFilter;
        private int subscribeType = SUBSCRIBE_TYPE_PASSIVE;

        public Builder setServiceName(String serviceName) {
            this.serviceName = serviceName;
            return this;
        }

        public Builder setServiceSpecificInfo(byte[] serviceSpecificInfo) {
            this.serviceSpecificInfo = serviceSpecificInfo;
            return this;
        }

        public Builder setMatchFilter(List<byte[]> matchFilter) {
            this.matchFilter = matchFilter;
            return this;
        }

        public Builder setSubscribeType(int subscribeType) {
            this.subscribeType = subscribeType;
            return this;
        }

        public SubscribeConfig build() {
            return new SubscribeConfig(this);
        }
    }
}
