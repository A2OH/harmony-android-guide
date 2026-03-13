package android.print;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcel;
import android.os.Parcelable;

public final class PrintJobInfo implements Parcelable {
    public static final int STATE_BLOCKED = 0;
    public static final int STATE_CANCELED = 0;
    public static final int STATE_COMPLETED = 0;
    public static final int STATE_CREATED = 0;
    public static final int STATE_FAILED = 0;
    public static final int STATE_QUEUED = 0;
    public static final int STATE_STARTED = 0;

    public PrintJobInfo() {}

    public int describeContents() { return 0; }
    public int getAdvancedIntOption(String p0) { return 0; }
    public String getAdvancedStringOption(String p0) { return null; }
    public long getCreationTime() { return 0L; }
    public int getState() { return 0; }
    public boolean hasAdvancedOption(String p0) { return false; }
    public void writeToParcel(Parcel p0, int p1) {}
}
