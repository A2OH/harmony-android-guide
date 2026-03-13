package android.bluetooth;

import java.util.List;

/**
 * Android-compatible BluetoothProfile shim. Stub.
 */
public interface BluetoothProfile {

    int STATE_DISCONNECTED  = 0;
    int STATE_CONNECTING    = 1;
    int STATE_CONNECTED     = 2;
    int STATE_DISCONNECTING = 3;

    int A2DP        = 2;
    int HEADSET     = 1;
    int HEALTH      = 3;
    int GATT        = 7;
    int GATT_SERVER = 8;

    List<BluetoothDevice> getConnectedDevices();

    int getConnectionState(BluetoothDevice device);

    /**
     * Listener for profile proxy connection events.
     */
    interface ServiceListener {
        void onServiceConnected(int profile, BluetoothProfile proxy);
        void onServiceDisconnected(int profile);
    }
}
