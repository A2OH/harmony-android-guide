package android.bluetooth.le;

import android.bluetooth.BluetoothDevice;

/**
 * Android-compatible ScanFilter shim. Stub.
 */
public class ScanFilter {

    private final String mDeviceName;
    private final String mDeviceAddress;
    private final String mServiceUuid;

    private ScanFilter(Builder builder) {
        mDeviceName    = builder.mDeviceName;
        mDeviceAddress = builder.mDeviceAddress;
        mServiceUuid   = builder.mServiceUuid;
    }

    public String getDeviceName() { return mDeviceName; }

    public String getDeviceAddress() { return mDeviceAddress; }

    public String getServiceUuid() { return mServiceUuid; }

    public boolean matches(ScanResult scanResult) {
        if (scanResult == null) return false;
        if (mDeviceAddress != null) {
            BluetoothDevice device = scanResult.getDevice();
            if (device == null || !mDeviceAddress.equals(device.getAddress())) return false;
        }
        if (mDeviceName != null) {
            ScanRecord record = scanResult.getScanRecord();
            if (record == null || !mDeviceName.equals(record.getDeviceName())) return false;
        }
        return true;
    }

    public static class Builder {
        private String mDeviceName;
        private String mDeviceAddress;
        private String mServiceUuid;

        public Builder setDeviceName(String deviceName) {
            mDeviceName = deviceName;
            return this;
        }

        public Builder setDeviceAddress(String deviceAddress) {
            mDeviceAddress = deviceAddress;
            return this;
        }

        public Builder setServiceUuid(String serviceUuid) {
            mServiceUuid = serviceUuid;
            return this;
        }

        public ScanFilter build() {
            return new ScanFilter(this);
        }
    }
}
