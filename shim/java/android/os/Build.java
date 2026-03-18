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
        public static final int BASE = 1;
        public static final int ECLAIR = 7;
        public static final int FROYO = 8;
        public static final int GINGERBREAD = 9;
        public static final int HONEYCOMB = 11;
        public static final int ICE_CREAM_SANDWICH = 14;
        public static final int JELLY_BEAN = 16;
        public static final int JELLY_BEAN_MR1 = 17;
        public static final int JELLY_BEAN_MR2 = 18;
        public static final int KITKAT = 19;
        public static final int LOLLIPOP = 21;
        public static final int LOLLIPOP_MR1 = 22;
        public static final int M = 23;
        public static final int N = 24;
        public static final int N_MR1 = 25;
        public static final int O = 26;
        public static final int O_MR1 = 27;
        public static final int P = 28;
        public static final int Q = 29;
        public static final int R = 30;
        public static final int S = 31;
        public static final int TIRAMISU = 33;
        public static final int CUR_DEVELOPMENT = 10000;
    }
}
