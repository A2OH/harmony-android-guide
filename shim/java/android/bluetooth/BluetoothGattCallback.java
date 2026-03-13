package android.bluetooth;

/**
 * Android-compatible BluetoothGattCallback shim. Abstract class with no-op defaults.
 */
public abstract class BluetoothGattCallback {
    /**
     * Called when the connection state of a remote GATT server changes.
     *
     * @param gatt     the GATT client
     * @param status   {@link BluetoothGatt#GATT_SUCCESS} or {@link BluetoothGatt#GATT_FAILURE}
     * @param newState {@link BluetoothGatt#STATE_CONNECTED} / {@link BluetoothGatt#STATE_DISCONNECTED}
     */
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {}

    /**
     * Called when services have been discovered on the remote device.
     *
     * @param gatt   the GATT client
     * @param status {@link BluetoothGatt#GATT_SUCCESS} if the discovery was successful
     */
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {}
}
