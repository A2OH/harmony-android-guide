package android.bluetooth;

import java.util.Collections;
import java.util.Set;

/**
 * Android-compatible BluetoothAdapter shim. Stub — no hardware access.
 */
public class BluetoothAdapter {
    public static final int STATE_OFF           = 10;
    public static final int STATE_TURNING_ON    = 11;
    public static final int STATE_ON            = 12;
    public static final int STATE_TURNING_OFF   = 13;

    public static final int SCAN_MODE_NONE                       = 20;
    public static final int SCAN_MODE_CONNECTABLE                = 21;
    public static final int SCAN_MODE_CONNECTABLE_DISCOVERABLE   = 23;

    private static final BluetoothAdapter sInstance = new BluetoothAdapter();

    private int mState    = STATE_OFF;
    private int mScanMode = SCAN_MODE_NONE;
    private boolean mDiscovering = false;
    private String mName    = "ShimDevice";
    private String mAddress = "00:00:00:00:00:00";

    private BluetoothAdapter() {}

    /** Returns the singleton adapter instance (may be null on real hardware without BT). */
    public static BluetoothAdapter getDefaultAdapter() {
        return sInstance;
    }

    public boolean isEnabled() {
        return mState == STATE_ON;
    }

    public boolean enable() {
        mState = STATE_ON;
        return true;
    }

    public boolean disable() {
        mState = STATE_OFF;
        return true;
    }

    public String getName() {
        return mName;
    }

    public boolean setName(String name) {
        mName = name;
        return true;
    }

    public String getAddress() {
        return mAddress;
    }

    public int getState() {
        return mState;
    }

    public int getScanMode() {
        return mScanMode;
    }

    /** Returns an empty set — no bonded devices in stub. */
    public Set<BluetoothDevice> getBondedDevices() {
        return Collections.emptySet();
    }

    public boolean startDiscovery() {
        mDiscovering = true;
        return true;
    }

    public boolean cancelDiscovery() {
        mDiscovering = false;
        return true;
    }

    public boolean isDiscovering() {
        return mDiscovering;
    }

    /** Returns a stub BluetoothDevice for the given MAC address. */
    public BluetoothDevice getRemoteDevice(String address) {
        return new BluetoothDevice(address);
    }
}
