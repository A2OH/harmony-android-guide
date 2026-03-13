package android.bluetooth;

import java.util.ArrayList;
import java.util.List;

/**
 * Android-compatible BluetoothHeadset shim. Stub.
 */
public class BluetoothHeadset implements BluetoothProfile {

    public static final String ACTION_CONNECTION_STATE_CHANGED =
            "android.bluetooth.headset.profile.action.CONNECTION_STATE_CHANGED";

    @Override
    public List<BluetoothDevice> getConnectedDevices() {
        return new ArrayList<>();
    }

    @Override
    public int getConnectionState(BluetoothDevice device) {
        return STATE_DISCONNECTED;
    }

    public boolean startVoiceRecognition(BluetoothDevice device) {
        return false;
    }

    public boolean stopVoiceRecognition(BluetoothDevice device) {
        return false;
    }
}
