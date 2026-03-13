package android.net.wifi.aware;

import java.util.List;

/**
 * Android-compatible PublishConfig shim. Stub.
 */
public class PublishConfig {

    public static final int PUBLISH_TYPE_UNSOLICITED = 0;
    public static final int PUBLISH_TYPE_SOLICITED    = 1;

    private final String serviceName;
    private final byte[] serviceSpecificInfo;
    private final List<byte[]> matchFilter;
    private final int publishType;

    private PublishConfig(Builder b) {
        this.serviceName       = b.serviceName;
        this.serviceSpecificInfo = b.serviceSpecificInfo;
        this.matchFilter       = b.matchFilter;
        this.publishType       = b.publishType;
    }

    public String getServiceName()           { return serviceName; }
    public byte[] getServiceSpecificInfo()   { return serviceSpecificInfo; }
    public List<byte[]> getMatchFilter()     { return matchFilter; }
    public int getPublishType()              { return publishType; }

    public static class Builder {
        private String serviceName;
        private byte[] serviceSpecificInfo;
        private List<byte[]> matchFilter;
        private int publishType = PUBLISH_TYPE_UNSOLICITED;

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

        public Builder setPublishType(int publishType) {
            this.publishType = publishType;
            return this;
        }

        public PublishConfig build() {
            return new PublishConfig(this);
        }
    }
}
