package android.bluetooth;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcel;
import android.os.Parcelable;

public final class BluetoothHidDeviceAppQosSettings implements Parcelable {
    public static final int MAX = 0;
    public static final int SERVICE_BEST_EFFORT = 0;
    public static final int SERVICE_GUARANTEED = 0;
    public static final int SERVICE_NO_TRAFFIC = 0;

    public BluetoothHidDeviceAppQosSettings(int p0, int p1, int p2, int p3, int p4, int p5) {}

    public int describeContents() { return 0; }
    public int getDelayVariation() { return 0; }
    public int getLatency() { return 0; }
    public int getPeakBandwidth() { return 0; }
    public int getServiceType() { return 0; }
    public int getTokenBucketSize() { return 0; }
    public int getTokenRate() { return 0; }
    public void writeToParcel(Parcel p0, int p1) {}
}
