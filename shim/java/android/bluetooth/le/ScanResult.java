package android.bluetooth.le;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothDevice;

import android.bluetooth.BluetoothDevice;

/**
 * Android-compatible BLE ScanResult shim. Stub.
 * Note: This is the LE scan result (android.bluetooth.le), not android.net.wifi.ScanResult.
 */
public class ScanResult {

    private final BluetoothDevice mDevice;
    private final int mRssi;
    private final ScanRecord mScanRecord;
    private final long mTimestampNanos;

    public ScanResult(BluetoothDevice device, ScanRecord scanRecord, int rssi,
            long timestampNanos) {
        mDevice = device;
        mScanRecord = scanRecord;
        mRssi = rssi;
        mTimestampNanos = timestampNanos;
    }

    public BluetoothDevice getDevice() { return mDevice; }

    public int getRssi() { return mRssi; }

    public ScanRecord getScanRecord() { return mScanRecord; }

    public long getTimestampNanos() { return mTimestampNanos; }

    @Override
    public String toString() {
        return "ScanResult{device=" + mDevice + ", rssi=" + mRssi + "}";
    }
}
