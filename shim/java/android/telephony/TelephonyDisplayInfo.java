package android.telephony;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcel;
import android.os.Parcelable;

public final class TelephonyDisplayInfo implements Parcelable {
    public static final int OVERRIDE_NETWORK_TYPE_LTE_ADVANCED_PRO = 0;
    public static final int OVERRIDE_NETWORK_TYPE_LTE_CA = 0;
    public static final int OVERRIDE_NETWORK_TYPE_NONE = 0;
    public static final int OVERRIDE_NETWORK_TYPE_NR_NSA = 0;
    public static final int OVERRIDE_NETWORK_TYPE_NR_NSA_MMWAVE = 0;

    public TelephonyDisplayInfo() {}

    public int describeContents() { return 0; }
    public int getNetworkType() { return 0; }
    public int getOverrideNetworkType() { return 0; }
    public void writeToParcel(Parcel p0, int p1) {}
}
