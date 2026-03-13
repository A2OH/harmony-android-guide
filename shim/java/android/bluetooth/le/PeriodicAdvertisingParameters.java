package android.bluetooth.le;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcel;
import android.os.Parcelable;

public final class PeriodicAdvertisingParameters implements Parcelable {
    public static final int CREATOR = 0;

    public PeriodicAdvertisingParameters() {}

    public int describeContents() { return 0; }
    public boolean getIncludeTxPower() { return false; }
    public int getInterval() { return 0; }
    public void writeToParcel(Parcel p0, int p1) {}
}
