package android.mtp;

/**
 * Android-compatible MtpDeviceInfo shim for A2OH migration.
 * Stub: all accessors return null.
 */
public final class MtpDeviceInfo {

    private final String mManufacturer;
    private final String mModel;
    private final String mSerialNumber;
    private final String mVersion;

    MtpDeviceInfo(String manufacturer, String model, String serialNumber, String version) {
        mManufacturer = manufacturer;
        mModel = model;
        mSerialNumber = serialNumber;
        mVersion = version;
    }

    public String getManufacturer() {
        return mManufacturer;
    }

    public String getModel() {
        return mModel;
    }

    public String getSerialNumber() {
        return mSerialNumber;
    }

    public String getVersion() {
        return mVersion;
    }
}
