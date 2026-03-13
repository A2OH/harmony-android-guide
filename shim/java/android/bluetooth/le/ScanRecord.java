package android.bluetooth.le;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Android-compatible ScanRecord shim. Stub.
 */
public class ScanRecord {

    private final byte[] mBytes;
    private final String mDeviceName;

    public ScanRecord(byte[] bytes) {
        mBytes = bytes;
        mDeviceName = null;
    }

    public ScanRecord(String deviceName, byte[] bytes) {
        mDeviceName = deviceName;
        mBytes = bytes;
    }

    public byte[] getBytes() { return mBytes; }

    public String getDeviceName() { return mDeviceName; }

    public int getAdvertiseFlags() { return -1; }

    public List<UUID> getServiceUuids() { return null; }

    public Map<Integer, byte[]> getManufacturerSpecificData() { return null; }

    public byte[] getManufacturerSpecificData(int manufacturerId) { return null; }

    public Map<UUID, byte[]> getServiceData() { return null; }

    public byte[] getServiceData(UUID serviceDataUuid) { return null; }

    public int getTxPowerLevel() { return Integer.MIN_VALUE; }

    @Override
    public String toString() {
        return "ScanRecord{deviceName=" + mDeviceName + "}";
    }
}
