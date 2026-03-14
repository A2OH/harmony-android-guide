package android.app;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcel;
import android.os.Parcelable;

public final class ApplicationExitInfo implements Parcelable {
    public static final int REASON_UNKNOWN = 0;
    public static final int REASON_EXIT_SELF = 1;
    public static final int REASON_SIGNALED = 2;
    public static final int REASON_LOW_MEMORY = 3;
    public static final int REASON_CRASH = 4;
    public static final int REASON_CRASH_NATIVE = 5;
    public static final int REASON_ANR = 6;
    public static final int REASON_INITIALIZATION_FAILURE = 7;
    public static final int REASON_PERMISSION_CHANGE = 8;
    public static final int REASON_EXCESSIVE_RESOURCE_USAGE = 9;
    public static final int REASON_USER_REQUESTED = 10;
    public static final int REASON_USER_STOPPED = 11;
    public static final int REASON_DEPENDENCY_DIED = 12;
    public static final int REASON_OTHER = 13;

    private int mReason;
    private int mStatus;
    private int mPid;
    private int mRealUid;
    private int mPackageUid;
    private int mDefiningUid;
    private int mImportance;
    private long mPss;
    private long mRss;
    private long mTimestamp;
    private String mProcessName;
    private String mDescription;

    public ApplicationExitInfo() {}

    public int describeContents() { return 0; }
    public int getDefiningUid() { return mDefiningUid; }
    public int getImportance() { return mImportance; }
    public int getPackageUid() { return mPackageUid; }
    public int getPid() { return mPid; }
    public long getPss() { return mPss; }
    public int getRealUid() { return mRealUid; }
    public int getReason() { return mReason; }
    public long getRss() { return mRss; }
    public int getStatus() { return mStatus; }
    public long getTimestamp() { return mTimestamp; }
    public String getProcessName() { return mProcessName; }
    public String getDescription() { return mDescription; }

    public void setReason(int reason) { mReason = reason; }
    public void setStatus(int status) { mStatus = status; }
    public void setPid(int pid) { mPid = pid; }
    public void setRealUid(int uid) { mRealUid = uid; }
    public void setPackageUid(int uid) { mPackageUid = uid; }
    public void setDefiningUid(int uid) { mDefiningUid = uid; }
    public void setImportance(int importance) { mImportance = importance; }
    public void setPss(long pss) { mPss = pss; }
    public void setRss(long rss) { mRss = rss; }
    public void setTimestamp(long timestamp) { mTimestamp = timestamp; }
    public void setProcessName(String name) { mProcessName = name; }
    public void setDescription(String description) { mDescription = description; }

    public void writeToParcel(Parcel p0, int p1) {}
}
