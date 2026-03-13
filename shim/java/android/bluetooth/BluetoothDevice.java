package android.bluetooth;

import java.util.UUID;

/**
 * Android-compatible BluetoothDevice shim. Stub — no hardware access.
 */
public class BluetoothDevice {
    public static final int BOND_NONE    = 10;
    public static final int BOND_BONDING = 11;
    public static final int BOND_BONDED  = 12;

    public static final int DEVICE_TYPE_CLASSIC = 1;
    public static final int DEVICE_TYPE_LE      = 2;
    public static final int DEVICE_TYPE_DUAL    = 3;

    private final String mAddress;
    private String mName      = "ShimRemote";
    private int mBondState    = BOND_NONE;
    private int mType         = DEVICE_TYPE_CLASSIC;

    /** Package-private constructor; created via BluetoothAdapter.getRemoteDevice(). */
    BluetoothDevice(String address) {
        mAddress = (address != null) ? address : "00:00:00:00:00:00";
    }

    public String getName() {
        return mName;
    }

    public String getAddress() {
        return mAddress;
    }

    public int getBondState() {
        return mBondState;
    }

    public int getType() {
        return mType;
    }

    /** Stub — returns true but does not initiate real pairing. */
    public boolean createBond() {
        mBondState = BOND_BONDING;
        return true;
    }

    /** Returns a stub BluetoothSocket; UUID is ignored in shim. */
    public BluetoothSocket createRfcommSocketToServiceRecord(UUID uuid) {
        return new BluetoothSocket();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BluetoothDevice)) return false;
        return mAddress.equals(((BluetoothDevice) o).mAddress);
    }

    @Override
    public int hashCode() {
        return mAddress.hashCode();
    }

    @Override
    public String toString() {
        return mName + " [" + mAddress + "]";
    }
}
