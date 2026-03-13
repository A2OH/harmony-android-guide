package android.bluetooth;
import java.util.List;
import java.util.concurrent.Executor;

public final class BluetoothHidDevice implements BluetoothProfile {
    public static final int ACTION_CONNECTION_STATE_CHANGED = 0;
    public static final int ERROR_RSP_INVALID_PARAM = 0;
    public static final int ERROR_RSP_INVALID_RPT_ID = 0;
    public static final int ERROR_RSP_NOT_READY = 0;
    public static final int ERROR_RSP_SUCCESS = 0;
    public static final int ERROR_RSP_UNKNOWN = 0;
    public static final int ERROR_RSP_UNSUPPORTED_REQ = 0;
    public static final int PROTOCOL_BOOT_MODE = 0;
    public static final int PROTOCOL_REPORT_MODE = 0;
    public static final int REPORT_TYPE_FEATURE = 0;
    public static final int REPORT_TYPE_INPUT = 0;
    public static final int REPORT_TYPE_OUTPUT = 0;
    public static final int SUBCLASS1_COMBO = 0;
    public static final int SUBCLASS1_KEYBOARD = 0;
    public static final int SUBCLASS1_MOUSE = 0;
    public static final int SUBCLASS1_NONE = 0;
    public static final int SUBCLASS2_CARD_READER = 0;
    public static final int SUBCLASS2_DIGITIZER_TABLET = 0;
    public static final int SUBCLASS2_GAMEPAD = 0;
    public static final int SUBCLASS2_JOYSTICK = 0;
    public static final int SUBCLASS2_REMOTE_CONTROL = 0;
    public static final int SUBCLASS2_SENSING_DEVICE = 0;
    public static final int SUBCLASS2_UNCATEGORIZED = 0;


    public boolean connect(BluetoothDevice p0) { return false; }
    public boolean disconnect(BluetoothDevice p0) { return false; }
    public List<BluetoothDevice> getConnectedDevices() { return null; }
    public int getConnectionState(BluetoothDevice p0) { return 0; }
    public List<?> getDevicesMatchingConnectionStates(int[] p0) { return null; }
    public boolean registerApp(BluetoothHidDeviceAppSdpSettings p0, BluetoothHidDeviceAppQosSettings p1, BluetoothHidDeviceAppQosSettings p2, Executor p3, Object p4) { return false; }
    public boolean replyReport(BluetoothDevice p0, byte p1, byte p2, byte[] p3) { return false; }
    public boolean reportError(BluetoothDevice p0, byte p1) { return false; }
    public boolean sendReport(BluetoothDevice p0, int p1, byte[] p2) { return false; }
    public boolean unregisterApp() { return false; }
    public void onAppStatusChanged(BluetoothDevice p0, boolean p1) {}
    public void onConnectionStateChanged(BluetoothDevice p0, int p1) {}
    public void onGetReport(BluetoothDevice p0, byte p1, byte p2, int p3) {}
    public void onInterruptData(BluetoothDevice p0, byte p1, byte[] p2) {}
    public void onSetProtocol(BluetoothDevice p0, byte p1) {}
    public void onSetReport(BluetoothDevice p0, byte p1, byte p2, byte[] p3) {}
    public void onVirtualCableUnplug(BluetoothDevice p0) {}
}
