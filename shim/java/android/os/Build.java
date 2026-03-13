package android.os;

import com.ohos.shim.bridge.OHBridge;

/**
 * Shim: android.os.Build → @ohos.deviceInfo
 * Tier 1 — direct mapping of device info fields.
 */
public class Build {
    public static final String BRAND = OHBridge.getDeviceBrand();
    public static final String MODEL = OHBridge.getDeviceModel();
    public static final String MANUFACTURER = OHBridge.getDeviceBrand(); // OH doesn't separate manufacturer
    public static final String DEVICE = OHBridge.getDeviceModel();
    public static final String PRODUCT = OHBridge.getDeviceModel();
    public static final String DISPLAY = OHBridge.getOSVersion();

    public static class VERSION {
        public static final String RELEASE = OHBridge.getOSVersion();
        public static final int SDK_INT = OHBridge.getSDKVersion();
    }

    public static class VERSION_CODES {
        public static final int O = 26;       // for version checks in app code
        public static final int P = 28;
        public static final int Q = 29;
        public static final int R = 30;
        public static final int S = 31;
        public static final int TIRAMISU = 33;
    }
}
