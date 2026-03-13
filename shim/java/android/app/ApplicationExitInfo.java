package android.app;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcel;
import android.os.Parcelable;

public final class ApplicationExitInfo implements Parcelable {
    public static final int REASON_ANR = 0;
    public static final int REASON_CRASH = 0;
    public static final int REASON_CRASH_NATIVE = 0;
    public static final int REASON_DEPENDENCY_DIED = 0;
    public static final int REASON_EXCESSIVE_RESOURCE_USAGE = 0;
    public static final int REASON_EXIT_SELF = 0;
    public static final int REASON_INITIALIZATION_FAILURE = 0;
    public static final int REASON_LOW_MEMORY = 0;
    public static final int REASON_OTHER = 0;
    public static final int REASON_PERMISSION_CHANGE = 0;
    public static final int REASON_SIGNALED = 0;
    public static final int REASON_UNKNOWN = 0;
    public static final int REASON_USER_REQUESTED = 0;
    public static final int REASON_USER_STOPPED = 0;

    public ApplicationExitInfo() {}

    public int describeContents() { return 0; }
    public int getDefiningUid() { return 0; }
    public int getImportance() { return 0; }
    public int getPackageUid() { return 0; }
    public int getPid() { return 0; }
    public long getPss() { return 0L; }
    public int getRealUid() { return 0; }
    public int getReason() { return 0; }
    public long getRss() { return 0L; }
    public int getStatus() { return 0; }
    public long getTimestamp() { return 0L; }
    public void writeToParcel(Parcel p0, int p1) {}
}
