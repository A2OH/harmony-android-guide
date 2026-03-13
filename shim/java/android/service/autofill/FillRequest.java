package android.service.autofill;
import android.os.Parcel;
import android.os.Parcelable;

public final class FillRequest implements Parcelable {
    public static final int FLAG_COMPATIBILITY_MODE_REQUEST = 0;
    public static final int FLAG_MANUAL_REQUEST = 0;

    public FillRequest() {}

    public int describeContents() { return 0; }
    public int getFlags() { return 0; }
    public int getId() { return 0; }
    public void writeToParcel(Parcel p0, int p1) {}
}
