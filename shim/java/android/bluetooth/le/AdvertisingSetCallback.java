package android.bluetooth.le;

public class AdvertisingSetCallback {
    public static final int ADVERTISE_FAILED_ALREADY_STARTED = 0;
    public static final int ADVERTISE_FAILED_DATA_TOO_LARGE = 0;
    public static final int ADVERTISE_FAILED_FEATURE_UNSUPPORTED = 0;
    public static final int ADVERTISE_FAILED_INTERNAL_ERROR = 0;
    public static final int ADVERTISE_FAILED_TOO_MANY_ADVERTISERS = 0;
    public static final int ADVERTISE_SUCCESS = 0;

    public AdvertisingSetCallback() {}

    public void onAdvertisingDataSet(AdvertisingSet p0, int p1) {}
    public void onAdvertisingEnabled(AdvertisingSet p0, boolean p1, int p2) {}
    public void onAdvertisingParametersUpdated(AdvertisingSet p0, int p1, int p2) {}
    public void onAdvertisingSetStarted(AdvertisingSet p0, int p1, int p2) {}
    public void onAdvertisingSetStopped(AdvertisingSet p0) {}
    public void onPeriodicAdvertisingDataSet(AdvertisingSet p0, int p1) {}
    public void onPeriodicAdvertisingEnabled(AdvertisingSet p0, boolean p1, int p2) {}
    public void onPeriodicAdvertisingParametersUpdated(AdvertisingSet p0, int p1) {}
    public void onScanResponseDataSet(AdvertisingSet p0, int p1) {}
}
