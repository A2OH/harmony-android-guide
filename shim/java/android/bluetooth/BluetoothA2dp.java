package android.bluetooth;

import java.util.ArrayList;
import java.util.List;

/**
 * Android-compatible BluetoothA2dp shim. Stub.
 */
public class BluetoothA2dp implements BluetoothProfile {

    public static final String ACTION_CONNECTION_STATE_CHANGED =
            "android.bluetooth.a2dp.profile.action.CONNECTION_STATE_CHANGED";

    public static final String ACTION_PLAYING_STATE_CHANGED =
            "android.bluetooth.a2dp.profile.action.PLAYING_STATE_CHANGED";

    public static final int STATE_PLAYING     = 10;
    public static final int STATE_NOT_PLAYING = 11;

    @Override
    public List<BluetoothDevice> getConnectedDevices() {
        return new ArrayList<>();
    }

    @Override
    public int getConnectionState(BluetoothDevice device) {
        return STATE_DISCONNECTED;
    }

    public boolean isA2dpPlaying(BluetoothDevice device) {
        return false;
    }
}
