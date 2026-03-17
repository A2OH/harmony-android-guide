package android.net;

import java.util.Arrays;

/**
 * Android-compatible MacAddress stub — an immutable representation of a MAC address.
 * Supports the factory methods and accessors used by the real Android API.
 */
public final class MacAddress {

    /** IEEE broadcast MAC address (FF:FF:FF:FF:FF:FF). */
    public static final MacAddress BROADCAST_ADDRESS =
            new MacAddress(new byte[]{(byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF,
                                      (byte)0xFF, (byte)0xFF});

    /** All-zeros placeholder used for locally administered addresses. */
    public static final MacAddress ALL_ZEROS_ADDRESS =
            new MacAddress(new byte[]{0, 0, 0, 0, 0, 0});

    private final byte[] mAddr;

    private MacAddress(byte[] addr) {
        if (addr == null || addr.length != 6) {
            throw new IllegalArgumentException("MAC address must be exactly 6 bytes");
        }
        mAddr = Arrays.copyOf(addr, addr.length);
    }

    // ---- Factory methods ----

    /**
     * Creates a {@code MacAddress} from a colon-separated string such as
     * {@code "AA:BB:CC:DD:EE:FF"} (case-insensitive).
     *
     * @throws IllegalArgumentException if the string is not a valid MAC address.
     */
    public static MacAddress fromString(String addr) {
        if (addr == null) {
            throw new IllegalArgumentException("MAC address string must not be null");
        }
        String[] parts = splitByChar(addr, ':');
        if (parts.length != 6) {
            throw new IllegalArgumentException("Invalid MAC address: " + addr);
        }
        byte[] bytes = new byte[6];
        for (int i = 0; i < 6; i++) {
            int val = Integer.parseInt(parts[i]);
            if (val < 0 || val > 255) {
                throw new IllegalArgumentException("Octet out of range in: " + addr);
            }
            bytes[i] = (byte) val;
        }
        return new MacAddress(bytes);
    }

    /**
     * Creates a {@code MacAddress} from a 6-element byte array.
     *
     * @throws IllegalArgumentException if the array is null or not exactly 6 bytes.
     */
    public static MacAddress fromBytes(byte[] addr) {
        return new MacAddress(addr);
    }

    // ---- Instance methods ----

    /** Returns a copy of the 6-byte representation of this MAC address. */
    public byte[] toByteArray() {
        return Arrays.copyOf(mAddr, mAddr.length);
    }

    /** Returns {@code true} if this is the all-ones broadcast address. */
    public boolean isBroadcast() {
        for (byte b : mAddr) {
            if (b != (byte) 0xFF) return false;
        }
        return true;
    }

    /** Returns {@code true} if the locally-administered bit is set. */
    public boolean isLocallyAssigned() {
        return (mAddr[0] & 0x02) != 0;
    }

    /** Returns {@code true} if the multicast bit is set. */
    public boolean isMulticastAddress() {
        return (mAddr[0] & 0x01) != 0;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(17);
        for (int i = 0; i < 6; i++) {
            if (i > 0) sb.append(':');
            sb.append(toHex((mAddr[i] & 0xFF), 2));
        }
        return sb.toString();
    }

    private static String toHex(int val, int digits) {
        String hex = Integer.toHexString(val).toUpperCase();
        while (hex.length() < digits) hex = "0" + hex;
        return hex;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MacAddress)) return false;
        return Arrays.equals(mAddr, ((MacAddress) o).mAddr);
    }

    private static String[] splitByChar(String s, char delim) {
        java.util.ArrayList<String> parts = new java.util.ArrayList<>();
        int start = 0;
        for (int i = 0; i <= s.length(); i++) {
            if (i == s.length() || s.charAt(i) == delim) {
                parts.add(s.substring(start, i));
                start = i + 1;
            }
        }
        return parts.toArray(new String[0]);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(mAddr);
    }
}
