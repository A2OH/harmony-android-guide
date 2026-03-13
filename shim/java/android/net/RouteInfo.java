package android.net;

import java.net.InetAddress;

/**
 * Android-compatible RouteInfo shim for OpenHarmony migration.
 * Represents a network route: a destination prefix, an optional gateway,
 * an optional network interface name, and a route type.
 */
public class RouteInfo {

    /** Route type: unicast — packets are forwarded to a next-hop or delivered locally. */
    public static final int RTN_UNICAST    = 1;
    /** Route type: unreachable — destination is unreachable; ICMP unreachable is sent. */
    public static final int RTN_UNREACHABLE = 7;
    /** Route type: throw — route lookup in the current table should be aborted. */
    public static final int RTN_THROW      = 9;

    private final IpPrefix mDestination;
    private final InetAddress mGateway;
    private final String mInterface;
    private final int mType;

    /**
     * Constructs a RouteInfo with the given destination, gateway, interface, and type.
     *
     * @param destination the destination prefix (may be null for a default route)
     * @param gateway     the next-hop gateway address (may be null for directly-connected routes)
     * @param iface       the network interface name (may be null)
     * @param type        one of {@link #RTN_UNICAST}, {@link #RTN_UNREACHABLE}, {@link #RTN_THROW}
     */
    public RouteInfo(IpPrefix destination, InetAddress gateway, String iface, int type) {
        mDestination = destination;
        mGateway = gateway;
        mInterface = iface;
        mType = type;
    }

    /**
     * Constructs a unicast RouteInfo with the given destination, gateway, and interface.
     */
    public RouteInfo(IpPrefix destination, InetAddress gateway, String iface) {
        this(destination, gateway, iface, RTN_UNICAST);
    }

    /**
     * Constructs a unicast RouteInfo with the given destination and gateway.
     */
    public RouteInfo(IpPrefix destination, InetAddress gateway) {
        this(destination, gateway, null, RTN_UNICAST);
    }

    /**
     * Returns the destination prefix of this route, or null if this is a default route.
     */
    public IpPrefix getDestination() {
        return mDestination;
    }

    /**
     * Returns the gateway address, or null for directly-connected routes.
     */
    public InetAddress getGateway() {
        return mGateway;
    }

    /**
     * Returns the network interface name, or null if not set.
     */
    public String getInterface() {
        return mInterface;
    }

    /**
     * Returns the route type: one of {@link #RTN_UNICAST}, {@link #RTN_UNREACHABLE},
     * or {@link #RTN_THROW}.
     */
    public int getType() {
        return mType;
    }

    /**
     * Returns true if this route is a default route (i.e., destination is null or
     * has a prefix length of 0).
     */
    public boolean isDefaultRoute() {
        return mDestination == null || mDestination.getPrefixLength() == 0;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("RouteInfo{");
        sb.append("dest=").append(mDestination != null ? mDestination : "default");
        sb.append(", gw=").append(mGateway);
        sb.append(", iface=").append(mInterface);
        sb.append(", type=").append(mType);
        sb.append("}");
        return sb.toString();
    }
}
