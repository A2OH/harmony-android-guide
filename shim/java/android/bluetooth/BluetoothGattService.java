package android.bluetooth;

import java.util.UUID;

/**
 * Android-compatible BluetoothGattService shim. Stub — minimal implementation.
 */
public class BluetoothGattService {
    public static final int SERVICE_TYPE_PRIMARY   = 0;
    public static final int SERVICE_TYPE_SECONDARY = 1;

    private final UUID mUuid;
    private final int  mType;

    public BluetoothGattService(UUID uuid, int serviceType) {
        mUuid = uuid;
        mType = serviceType;
    }

    public UUID getUuid() {
        return mUuid;
    }

    public int getType() {
        return mType;
    }
}
