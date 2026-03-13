package android.net.wifi;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcel;
import android.os.Parcelable;

public class WpsInfo implements Parcelable {
    public int BSSID = 0;
    public static final int DISPLAY = 0;
    public static final int INVALID = 0;
    public static final int KEYPAD = 0;
    public static final int LABEL = 0;
    public static final int PBC = 0;
    public int pin = 0;
    public int setup = 0;

    public WpsInfo() {}
    public WpsInfo(WpsInfo p0) {}

    public int describeContents() { return 0; }
    public void writeToParcel(Parcel p0, int p1) {}
}
