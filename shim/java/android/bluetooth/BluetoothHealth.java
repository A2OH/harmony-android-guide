package android.bluetooth;

public final class BluetoothHealth implements BluetoothProfile {
    public BluetoothHealth() {}

    public java.util.List<BluetoothDevice> getConnectedDevices() { return new java.util.ArrayList<>(); }
    public int getConnectionState(BluetoothDevice device) { return 0; }
}