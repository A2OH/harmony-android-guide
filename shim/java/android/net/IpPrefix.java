package android.net;

import java.net.InetAddress;
import java.util.Arrays;

/**
 * Android-compatible IpPrefix shim for OpenHarmony migration.
 * Represents a network prefix defined by an InetAddress and a prefix length.
 */
public class IpPrefix {
    private final InetAddress mAddress;
    private final int mPrefixLength;

    public IpPrefix(InetAddress address, int prefixLength) {
        if (address == null) {
            throw new NullPointerException("address must not be null");
        }
        if (prefixLength < 0 || prefixLength > address.getAddress().length * 8) {
            throw new IllegalArgumentException("Invalid prefix length: " + prefixLength);
        }
        // Store the masked (network) address
        byte[] raw = address.getAddress();
        maskAddress(raw, prefixLength);
        try {
            mAddress = InetAddress.getByAddress(raw);
        } catch (java.net.UnknownHostException e) {
            throw new IllegalArgumentException("Invalid address", e);
        }
        mPrefixLength = prefixLength;
    }

    private static void maskAddress(byte[] addr, int prefixLength) {
        int fullBytes = prefixLength / 8;
        int remainBits = prefixLength % 8;
        for (int i = fullBytes; i < addr.length; i++) {
            if (i == fullBytes && remainBits != 0) {
                addr[i] = (byte) (addr[i] & (0xFF << (8 - remainBits)));
            } else {
                addr[i] = 0;
            }
        }
    }

    /** Returns the masked network address of this prefix. */
    public InetAddress getAddress() {
        return mAddress;
    }

    /** Returns the raw byte array of the masked network address. */
    public byte[] getRawAddress() {
        return mAddress.getAddress();
    }

    /** Returns the prefix length. */
    public int getPrefixLength() {
        return mPrefixLength;
    }

    /**
     * Returns true if the given address is within this prefix.
     */
    public boolean contains(InetAddress address) {
        if (address == null) {
            return false;
        }
        byte[] raw = address.getAddress();
        byte[] net = mAddress.getAddress();
        if (raw.length != net.length) {
            return false;
        }
        byte[] masked = raw.clone();
        maskAddress(masked, mPrefixLength);
        return Arrays.equals(masked, net);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof IpPrefix)) return false;
        IpPrefix other = (IpPrefix) obj;
        return mPrefixLength == other.mPrefixLength
                && Arrays.equals(mAddress.getAddress(), other.mAddress.getAddress());
    }

    @Override
    public int hashCode() {
        return 31 * Arrays.hashCode(mAddress.getAddress()) + mPrefixLength;
    }

    @Override
    public String toString() {
        return mAddress.getHostAddress() + "/" + mPrefixLength;
    }
}
