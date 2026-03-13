package android.net;

import java.net.InetAddress;
import java.util.Arrays;

/**
 * Android-compatible LinkAddress shim for OpenHarmony migration.
 * Identifies an IP address on a network interface, including prefix length,
 * address flags, and scope.
 */
public class LinkAddress {
    /** Flag: address is deprecated (RFC 4862). */
    public static final int FLAG_DEPRECATED = 0x20;
    /** Flag: address is tentative (RFC 4862). */
    public static final int FLAG_TENTATIVE  = 0x40;

    private final InetAddress mAddress;
    private final int mPrefixLength;
    private final int mFlags;
    private final int mScope;

    /**
     * Constructs a LinkAddress with address and prefix length; flags and scope default to 0.
     */
    public LinkAddress(InetAddress address, int prefixLength) {
        this(address, prefixLength, 0, 0);
    }

    /**
     * Constructs a LinkAddress with all fields.
     *
     * @param address      the IP address
     * @param prefixLength the network prefix length
     * @param flags        address flags (e.g. deprecated, tentative)
     * @param scope        address scope (RT_SCOPE_* constants)
     */
    public LinkAddress(InetAddress address, int prefixLength, int flags, int scope) {
        if (address == null) {
            throw new NullPointerException("address must not be null");
        }
        int maxPrefix = address.getAddress().length * 8;
        if (prefixLength < 0 || prefixLength > maxPrefix) {
            throw new IllegalArgumentException("Invalid prefix length: " + prefixLength);
        }
        mAddress = address;
        mPrefixLength = prefixLength;
        mFlags = flags;
        mScope = scope;
    }

    /** Returns the IP address of this link address. */
    public InetAddress getAddress() {
        return mAddress;
    }

    /** Returns the prefix length of this link address. */
    public int getPrefixLength() {
        return mPrefixLength;
    }

    /** Returns the address flags. */
    public int getFlags() {
        return mFlags;
    }

    /** Returns the address scope. */
    public int getScope() {
        return mScope;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof LinkAddress)) return false;
        LinkAddress other = (LinkAddress) obj;
        return mPrefixLength == other.mPrefixLength
                && mFlags == other.mFlags
                && mScope == other.mScope
                && Arrays.equals(mAddress.getAddress(), other.mAddress.getAddress());
    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(mAddress.getAddress());
        result = 31 * result + mPrefixLength;
        result = 31 * result + mFlags;
        result = 31 * result + mScope;
        return result;
    }

    @Override
    public String toString() {
        return mAddress.getHostAddress() + "/" + mPrefixLength
                + " flags=" + mFlags + " scope=" + mScope;
    }
}
