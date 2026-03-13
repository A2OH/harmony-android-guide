package android.net;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Android-compatible LinkProperties shim. Describes the properties of a network link.
 * Returns plausible mock values for use on OpenHarmony / desktop.
 */
public class LinkProperties {

    private String interfaceName = "wlan0";
    private String domains = null;
    private final List<LinkAddress> linkAddresses = new ArrayList<>();
    private final List<InetAddress> dnsServers = new ArrayList<>();

    public LinkProperties() {
        // Mock: a typical Wi-Fi link address + common DNS servers
        try {
            linkAddresses.add(new LinkAddress(InetAddress.getByName("192.168.1.100")));
            dnsServers.add(InetAddress.getByName("8.8.8.8"));
            dnsServers.add(InetAddress.getByName("8.8.4.4"));
        } catch (UnknownHostException e) {
            // should not happen with literal addresses
        }
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String iface) {
        this.interfaceName = iface;
    }

    public List<LinkAddress> getLinkAddresses() {
        return Collections.unmodifiableList(linkAddresses);
    }

    public void addLinkAddress(LinkAddress address) {
        linkAddresses.add(address);
    }

    public List<InetAddress> getDnsServers() {
        return Collections.unmodifiableList(dnsServers);
    }

    public void addDnsServer(InetAddress dns) {
        dnsServers.add(dns);
    }

    public String getDomains() {
        return domains;
    }

    public void setDomains(String domains) {
        this.domains = domains;
    }

    @Override
    public String toString() {
        return "LinkProperties{iface=" + interfaceName
                + ", addresses=" + linkAddresses
                + ", dns=" + dnsServers
                + ", domains=" + domains + "}";
    }

    // -----------------------------------------------------------------------
    // Nested helper: LinkAddress
    // -----------------------------------------------------------------------

    /**
     * Represents a network link address (IP address + prefix length).
     */
    public static class LinkAddress {
        private final InetAddress address;
        private final int prefixLength;

        public LinkAddress() {
            this.address = null;
            this.prefixLength = 0;
        }

        public LinkAddress(InetAddress address) {
            this(address, 24);
        }

        public LinkAddress(InetAddress address, int prefixLength) {
            this.address = address;
            this.prefixLength = prefixLength;
        }

        public InetAddress getAddress() {
            return address;
        }

        public int getPrefixLength() {
            return prefixLength;
        }

        @Override
        public String toString() {
            return "LinkAddress{" + address + "/" + prefixLength + "}";
        }
    }
}
