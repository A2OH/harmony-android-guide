package android.bluetooth;

/**
 * Android-compatible BluetoothGattServer shim. Stub.
 */
public class BluetoothGattServer {

    public boolean addService(BluetoothGattService service) {
        return false;
    }

    public boolean removeService(BluetoothGattService service) {
        return false;
    }

    public void clearServices() {}

    public boolean connect(BluetoothDevice device, boolean autoConnect) {
        return false;
    }

    public void cancelConnection(BluetoothDevice device) {}

    public void close() {}

    public boolean notifyCharacteristicChanged(BluetoothDevice device,
            BluetoothGattCharacteristic characteristic, boolean confirm) {
        return false;
    }
}
