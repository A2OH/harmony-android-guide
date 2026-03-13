package android.net;

/**
 * Android-compatible DhcpInfo stub — describes a DHCP lease obtained by the Wi-Fi interface.
 * All fields are public ints, matching the real Android API.
 * IP/gateway/netmask values are 32-bit integers in little-endian byte order (same as
 * {@code WifiInfo.getIpAddress()}).
 */
public class DhcpInfo {

    /** IPv4 address assigned to the interface. */
    public int ipAddress;

    /** IPv4 gateway address. */
    public int gateway;

    /** Network mask. */
    public int netmask;

    /** Primary DNS server address. */
    public int dns1;

    /** Secondary DNS server address. */
    public int dns2;

    /** DHCP server address. */
    public int serverAddress;

    /** Lease duration in seconds. */
    public int leaseDuration;

    public DhcpInfo() {}

    /**
     * Converts a packed IPv4 int (little-endian) to dotted-decimal notation.
     * Utility method for debugging.
     */
    public static String intToInetAddress(int addr) {
        return  (addr & 0xFF)         + "."
              + ((addr >>  8) & 0xFF) + "."
              + ((addr >> 16) & 0xFF) + "."
              + ((addr >> 24) & 0xFF);
    }

    @Override
    public String toString() {
        return "DhcpInfo{"
                + "ip=" + intToInetAddress(ipAddress)
                + ", gw=" + intToInetAddress(gateway)
                + ", mask=" + intToInetAddress(netmask)
                + ", dns1=" + intToInetAddress(dns1)
                + ", dns2=" + intToInetAddress(dns2)
                + ", server=" + intToInetAddress(serverAddress)
                + ", lease=" + leaseDuration + "s}";
    }
}
