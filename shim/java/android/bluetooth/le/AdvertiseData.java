package android.bluetooth.le;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A2OH shim: Bluetooth LE advertise data.
 * Maps to OpenHarmony @ohos.bluetooth.ble AdvertisingData.
 */
public final class AdvertiseData {

    private final List<Object> serviceUuids;
    private final Map<Object, byte[]> serviceData;
    private final Map<Integer, byte[]> manufacturerData;
    private final boolean includeDeviceName;
    private final boolean includeTxPowerLevel;

    private AdvertiseData(List<Object> serviceUuids, Map<Object, byte[]> serviceData,
            Map<Integer, byte[]> manufacturerData, boolean includeDeviceName,
            boolean includeTxPowerLevel) {
        this.serviceUuids = serviceUuids;
        this.serviceData = serviceData;
        this.manufacturerData = manufacturerData;
        this.includeDeviceName = includeDeviceName;
        this.includeTxPowerLevel = includeTxPowerLevel;
    }

    public List<Object> getServiceUuids() {
        return serviceUuids;
    }

    public Map<Object, byte[]> getServiceData() {
        return serviceData;
    }

    public Map<Integer, byte[]> getManufacturerData() {
        return manufacturerData;
    }

    public boolean getIncludeDeviceName() {
        return includeDeviceName;
    }

    public boolean getIncludeTxPowerLevel() {
        return includeTxPowerLevel;
    }

    public static final class Builder {
        private final List<Object> serviceUuids = new ArrayList<>();
        private final Map<Object, byte[]> serviceData = new HashMap<>();
        private final Map<Integer, byte[]> manufacturerData = new HashMap<>();
        private boolean includeDeviceName = false;
        private boolean includeTxPowerLevel = false;

        public Builder addServiceUuid(Object serviceUuid) {
            serviceUuids.add(serviceUuid);
            return this;
        }

        public Builder addServiceData(Object serviceDataUuid, byte[] data) {
            serviceData.put(serviceDataUuid, data);
            return this;
        }

        public Builder addManufacturerData(int manufacturerId, byte[] data) {
            manufacturerData.put(manufacturerId, data);
            return this;
        }

        public Builder setIncludeDeviceName(boolean includeDeviceName) {
            this.includeDeviceName = includeDeviceName;
            return this;
        }

        public Builder setIncludeTxPowerLevel(boolean includeTxPowerLevel) {
            this.includeTxPowerLevel = includeTxPowerLevel;
            return this;
        }

        public AdvertiseData build() {
            return new AdvertiseData(
                    new ArrayList<>(serviceUuids),
                    new HashMap<>(serviceData),
                    new HashMap<>(manufacturerData),
                    includeDeviceName,
                    includeTxPowerLevel);
        }
    }
}
