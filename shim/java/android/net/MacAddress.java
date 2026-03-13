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
        mAddr = Arrays.copyOf(addr, 6);
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
        String[] parts = addr.split(":");
        if (parts.length != 6) {
            throw new IllegalArgumentException("Invalid MAC address: " + addr);
        }
        byte[] bytes = new byte[6];
        for (int i = 0; i < 6; i++) {
            int val = Integer.parseInt(parts[i], 16);
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
        return Arrays.copyOf(mAddr, 6);
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
        return String.format("%02X:%02X:%02X:%02X:%02X:%02X",
                mAddr[0] & 0xFF, mAddr[1] & 0xFF, mAddr[2] & 0xFF,
                mAddr[3] & 0xFF, mAddr[4] & 0xFF, mAddr[5] & 0xFF);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MacAddress)) return false;
        return Arrays.equals(mAddr, ((MacAddress) o).mAddr);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(mAddr);
    }
}
