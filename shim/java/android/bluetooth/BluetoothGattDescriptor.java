package android.bluetooth;

import java.util.UUID;

/**
 * Android-compatible BluetoothGattDescriptor shim. Stub.
 */
public class BluetoothGattDescriptor {

    public static final byte[] ENABLE_NOTIFICATION_VALUE  = {0x01, 0x00};
    public static final byte[] ENABLE_INDICATION_VALUE    = {0x02, 0x00};
    public static final byte[] DISABLE_NOTIFICATION_VALUE = {0x00, 0x00};

    public static final int PERMISSION_READ  = 0x01;
    public static final int PERMISSION_WRITE = 0x10;

    private final UUID mUuid;
    private final int mPermissions;
    private byte[] mValue;
    private BluetoothGattCharacteristic mCharacteristic;

    public BluetoothGattDescriptor(UUID uuid, int permissions) {
        mUuid = uuid;
        mPermissions = permissions;
    }

    public UUID getUuid() { return mUuid; }

    public int getPermissions() { return mPermissions; }

    public byte[] getValue() { return mValue; }

    public boolean setValue(byte[] value) {
        mValue = value;
        return true;
    }

    public BluetoothGattCharacteristic getCharacteristic() { return mCharacteristic; }

    /** Package-private setter used by BluetoothGattCharacteristic. */
    void setCharacteristic(BluetoothGattCharacteristic characteristic) {
        mCharacteristic = characteristic;
    }
}
