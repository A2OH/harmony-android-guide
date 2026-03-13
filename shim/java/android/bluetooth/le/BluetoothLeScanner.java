package android.bluetooth.le;

import java.util.List;

/**
 * Android-compatible BluetoothLeScanner shim. Stub.
 */
public class BluetoothLeScanner {

    public void startScan(ScanCallback callback) {
        // no-op in shim
    }

    public void startScan(List<ScanFilter> filters, ScanSettings settings, ScanCallback callback) {
        // no-op in shim
    }

    public void stopScan(ScanCallback callback) {
        // no-op in shim
    }

    public void flushPendingScanResults(ScanCallback callback) {
        // no-op in shim
    }
}
