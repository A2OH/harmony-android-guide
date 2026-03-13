package android.bluetooth;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.BluetoothLeScanner;

import android.bluetooth.le.BluetoothLeScanner;
import java.util.ArrayList;
import java.util.List;

/**
 * Android-compatible BluetoothManager shim. Stub.
 */
public class BluetoothManager {

    private static final BluetoothAdapter sAdapter = BluetoothAdapter.getDefaultAdapter();

    /** @param context ignored in shim — pass null or any Object */
    public BluetoothManager(Object context) {}

    public BluetoothAdapter getAdapter() {
        return sAdapter;
    }

    public List<BluetoothDevice> getConnectedDevices(int profile) {
        return new ArrayList<>();
    }

    public int getConnectionState(BluetoothDevice device, int profile) {
        return BluetoothProfile.STATE_DISCONNECTED;
    }

    /**
     * @param context  ignored in shim
     * @param callback callback for GATT server events
     * @return a no-op BluetoothGattServer stub
     */
    public BluetoothGattServer openGattServer(Object context,
            BluetoothGattServerCallback callback) {
        return new BluetoothGattServer();
    }
}
