package android.bluetooth.le;

/**
 * A2OH shim: Bluetooth LE advertise callback.
 * Maps to OpenHarmony @ohos.bluetooth.ble advertising callbacks.
 */
public class AdvertiseCallback {

    public static final int ADVERTISE_FAILED_DATA_TOO_LARGE = 1;
    public static final int ADVERTISE_FAILED_TOO_MANY_ADVERTISERS = 2;
    public static final int ADVERTISE_FAILED_ALREADY_STARTED = 3;
    public static final int ADVERTISE_FAILED_INTERNAL_ERROR = 4;
    public static final int ADVERTISE_FAILED_FEATURE_UNSUPPORTED = 5;

    public void onStartSuccess(Object settingsInEffect) {
    }

    public void onStartFailure(int errorCode) {
    }
}
