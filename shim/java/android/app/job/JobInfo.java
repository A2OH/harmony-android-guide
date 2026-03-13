package android.app.job;
import android.os.Parcel;
import android.os.Parcelable;

public class JobInfo implements Parcelable {
    public static final int BACKOFF_POLICY_EXPONENTIAL = 0;
    public static final int BACKOFF_POLICY_LINEAR = 0;
    public static final int DEFAULT_INITIAL_BACKOFF_MILLIS = 0;
    public static final int MAX_BACKOFF_DELAY_MILLIS = 0;
    public static final int NETWORK_BYTES_UNKNOWN = 0;
    public static final int NETWORK_TYPE_ANY = 0;
    public static final int NETWORK_TYPE_CELLULAR = 0;
    public static final int NETWORK_TYPE_NONE = 0;
    public static final int NETWORK_TYPE_NOT_ROAMING = 0;
    public static final int NETWORK_TYPE_UNMETERED = 0;

    public JobInfo() {}

    public int describeContents() { return 0; }
    public int getBackoffPolicy() { return 0; }
    public int getClipGrantFlags() { return 0; }
    public long getEstimatedNetworkDownloadBytes() { return 0L; }
    public long getEstimatedNetworkUploadBytes() { return 0L; }
    public long getFlexMillis() { return 0L; }
    public int getId() { return 0; }
    public long getInitialBackoffMillis() { return 0L; }
    public long getIntervalMillis() { return 0L; }
    public long getMaxExecutionDelayMillis() { return 0L; }
    public static long getMinFlexMillis() { return 0L; }
    public long getMinLatencyMillis() { return 0L; }
    public static long getMinPeriodMillis() { return 0L; }
    public long getTriggerContentMaxDelay() { return 0L; }
    public long getTriggerContentUpdateDelay() { return 0L; }
    public boolean isImportantWhileForeground() { return false; }
    public boolean isPeriodic() { return false; }
    public boolean isPersisted() { return false; }
    public boolean isPrefetch() { return false; }
    public boolean isRequireBatteryNotLow() { return false; }
    public boolean isRequireCharging() { return false; }
    public boolean isRequireDeviceIdle() { return false; }
    public boolean isRequireStorageNotLow() { return false; }
    public void writeToParcel(Parcel p0, int p1) {}
}
