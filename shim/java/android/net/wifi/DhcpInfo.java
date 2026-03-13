package android.net.wifi;
import android.net.Network;
import android.net.Network;

/**
 * Shim: android.net.wifi.DhcpInfo — stub holding DHCP-assigned address info.
 *
 * OpenHarmony does not expose a direct DHCP info API, so all fields default
 * to 0. Apps that only read these fields (e.g. to compute the broadcast
 * address) will get sensible no-op behaviour.
 */
public class DhcpInfo {

    /** IP address. */
    public int ipAddress;

    /** Gateway address. */
    public int gateway;

    /** Network mask. */
    public int netmask;

    /** DNS server 1. */
    public int dns1;

    /** DNS server 2. */
    public int dns2;

    /** Server address. */
    public int serverAddress;

    /** Lease duration in seconds. */
    public int leaseDuration;

    public DhcpInfo() {
        // all fields default to 0
    }

    @Override
    public String toString() {
        return "DhcpInfo{"
                + "ip=" + intToIpString(ipAddress)
                + ", gw=" + intToIpString(gateway)
                + ", mask=" + intToIpString(netmask)
                + ", dns1=" + intToIpString(dns1)
                + ", dns2=" + intToIpString(dns2)
                + ", server=" + intToIpString(serverAddress)
                + ", lease=" + leaseDuration + "s"
                + "}";
    }

    private static String intToIpString(int addr) {
        return ((addr      ) & 0xFF) + "."
             + ((addr >>  8) & 0xFF) + "."
             + ((addr >> 16) & 0xFF) + "."
             + ((addr >> 24) & 0xFF);
    }
}
