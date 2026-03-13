package android.content.pm;

import android.content.Intent;
import java.util.List;

public abstract class PackageManager {

    public static final int PERMISSION_GRANTED = 0;
    public static final int PERMISSION_DENIED  = -1;

    public static final int GET_META_DATA  = 128;
    public static final int GET_ACTIVITIES = 1;
    public static final int GET_SERVICES   = 4;
    public static final int GET_RECEIVERS  = 2;
    public static final int GET_PROVIDERS  = 8;

    public static final String FEATURE_BLUETOOTH  = "android.hardware.bluetooth";
    public static final String FEATURE_CAMERA     = "android.hardware.camera";
    public static final String FEATURE_WIFI       = "android.hardware.wifi";
    public static final String FEATURE_LOCATION   = "android.hardware.location";
    public static final String FEATURE_NFC        = "android.hardware.nfc";
    public static final String FEATURE_TELEPHONY  = "android.hardware.telephony";

    public static class NameNotFoundException extends Exception {
        public NameNotFoundException() {
            super();
        }
        public NameNotFoundException(String message) {
            super(message);
        }
    }

    public abstract PackageInfo getPackageInfo(String packageName, int flags)
            throws NameNotFoundException;

    public abstract ApplicationInfo getApplicationInfo(String packageName, int flags)
            throws NameNotFoundException;

    public abstract List<PackageInfo> getInstalledPackages(int flags);

    public abstract int checkPermission(String permName, String pkgName);

    public abstract boolean hasSystemFeature(String name);

    public abstract Intent getLaunchIntentForPackage(String packageName);
}
