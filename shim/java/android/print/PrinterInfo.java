package android.print;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcel;
import android.os.Parcelable;

public final class PrinterInfo implements Parcelable {
    public static final int STATUS_BUSY = 0;
    public static final int STATUS_IDLE = 0;
    public static final int STATUS_UNAVAILABLE = 0;

    public PrinterInfo() {}

    public int describeContents() { return 0; }
    public int getStatus() { return 0; }
    public void writeToParcel(Parcel p0, int p1) {}
}
