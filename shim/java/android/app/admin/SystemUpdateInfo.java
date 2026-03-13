package android.app.admin;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcel;
import android.os.Parcelable;

public final class SystemUpdateInfo implements Parcelable {
    public static final int SECURITY_PATCH_STATE_FALSE = 0;
    public static final int SECURITY_PATCH_STATE_TRUE = 0;
    public static final int SECURITY_PATCH_STATE_UNKNOWN = 0;

    public SystemUpdateInfo() {}

    public int describeContents() { return 0; }
    public long getReceivedTime() { return 0L; }
    public int getSecurityPatchState() { return 0; }
    public void writeToParcel(Parcel p0, int p1) {}
}
