package android.bluetooth;

import java.util.Collections;
import java.util.List;

/**
 * Android-compatible BluetoothGatt shim. Stub — no BLE hardware access.
 */
public class BluetoothGatt {
    public static final int STATE_DISCONNECTED  = 0;
    public static final int STATE_CONNECTING    = 1;
    public static final int STATE_CONNECTED     = 2;
    public static final int STATE_DISCONNECTING = 3;

    public static final int GATT_SUCCESS = 0;
    public static final int GATT_FAILURE = 257;

    private final BluetoothDevice      mDevice;
    private final BluetoothGattCallback mCallback;
    private int mConnectionState = STATE_DISCONNECTED;

    /** Package-private; instances created via BluetoothDevice.connectGatt() on real devices. */
    BluetoothGatt(BluetoothDevice device, BluetoothGattCallback callback) {
        mDevice   = device;
        mCallback = callback;
    }

    public BluetoothDevice getDevice() {
        return mDevice;
    }

    /** Stub — always returns false (no real BLE stack). */
    public boolean connect() {
        return false;
    }

    /** No-op in shim. */
    public void disconnect() {
        mConnectionState = STATE_DISCONNECTED;
    }

    /** No-op in shim. */
    public void close() {
        mConnectionState = STATE_DISCONNECTED;
    }

    /** Stub — always returns false (no real BLE stack). */
    public boolean discoverServices() {
        return false;
    }

    /** Returns an empty list — no services discovered in stub. */
    public List<BluetoothGattService> getServices() {
        return Collections.emptyList();
    }
}
