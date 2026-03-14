package android.app.job;
import android.content.ComponentName;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.PersistableBundle;

public class JobInfo implements Parcelable {
    public static final int BACKOFF_POLICY_LINEAR = 0;
    public static final int BACKOFF_POLICY_EXPONENTIAL = 1;
    public static final long DEFAULT_INITIAL_BACKOFF_MILLIS = 30000L;
    public static final long MAX_BACKOFF_DELAY_MILLIS = 18000000L;
    public static final int NETWORK_TYPE_NONE = 0;
    public static final int NETWORK_TYPE_ANY = 1;
    public static final int NETWORK_TYPE_UNMETERED = 2;
    public static final int NETWORK_TYPE_NOT_ROAMING = 3;
    public static final int NETWORK_TYPE_CELLULAR = 4;
    public static final long NETWORK_BYTES_UNKNOWN = -1L;
    public static final long MIN_PERIOD_MILLIS = 900000L;
    public static final long MIN_FLEX_MILLIS = 300000L;

    private final int mJobId;
    private final ComponentName mService;
    private final PersistableBundle mExtras;
    private final int mNetworkType;
    private final long mMinLatencyMillis;
    private final long mMaxExecutionDelayMillis;
    private final boolean mRequireCharging;
    private final boolean mRequireDeviceIdle;
    private final boolean mRequireBatteryNotLow;
    private final boolean mRequireStorageNotLow;
    private final long mIntervalMillis;
    private final long mFlexMillis;
    private final boolean mIsPersisted;
    private final boolean mIsPrefetch;
    private final boolean mImportantWhileForeground;
    private final int mBackoffPolicy;
    private final long mInitialBackoffMillis;
    private final long mTriggerContentUpdateDelay;
    private final long mTriggerContentMaxDelay;
    private final int mClipGrantFlags;
    private final long mEstimatedNetworkDownloadBytes;
    private final long mEstimatedNetworkUploadBytes;

    public JobInfo() {
        mJobId = 0;
        mService = null;
        mExtras = new PersistableBundle();
        mNetworkType = NETWORK_TYPE_NONE;
        mMinLatencyMillis = 0;
        mMaxExecutionDelayMillis = 0;
        mRequireCharging = false;
        mRequireDeviceIdle = false;
        mRequireBatteryNotLow = false;
        mRequireStorageNotLow = false;
        mIntervalMillis = 0;
        mFlexMillis = 0;
        mIsPersisted = false;
        mIsPrefetch = false;
        mImportantWhileForeground = false;
        mBackoffPolicy = BACKOFF_POLICY_EXPONENTIAL;
        mInitialBackoffMillis = DEFAULT_INITIAL_BACKOFF_MILLIS;
        mTriggerContentUpdateDelay = -1;
        mTriggerContentMaxDelay = -1;
        mClipGrantFlags = 0;
        mEstimatedNetworkDownloadBytes = NETWORK_BYTES_UNKNOWN;
        mEstimatedNetworkUploadBytes = NETWORK_BYTES_UNKNOWN;
    }

    private JobInfo(Builder b) {
        mJobId = b.mJobId;
        mService = b.mService;
        mExtras = b.mExtras != null ? new PersistableBundle(b.mExtras) : new PersistableBundle();
        mNetworkType = b.mNetworkType;
        mMinLatencyMillis = b.mMinLatencyMillis;
        mMaxExecutionDelayMillis = b.mMaxExecutionDelayMillis;
        mRequireCharging = b.mRequireCharging;
        mRequireDeviceIdle = b.mRequireDeviceIdle;
        mRequireBatteryNotLow = b.mRequireBatteryNotLow;
        mRequireStorageNotLow = b.mRequireStorageNotLow;
        mIntervalMillis = b.mIntervalMillis;
        mFlexMillis = b.mFlexMillis;
        mIsPersisted = b.mIsPersisted;
        mIsPrefetch = b.mIsPrefetch;
        mImportantWhileForeground = b.mImportantWhileForeground;
        mBackoffPolicy = b.mBackoffPolicy;
        mInitialBackoffMillis = b.mInitialBackoffMillis;
        mTriggerContentUpdateDelay = b.mTriggerContentUpdateDelay;
        mTriggerContentMaxDelay = b.mTriggerContentMaxDelay;
        mClipGrantFlags = b.mClipGrantFlags;
        mEstimatedNetworkDownloadBytes = b.mEstimatedNetworkDownloadBytes;
        mEstimatedNetworkUploadBytes = b.mEstimatedNetworkUploadBytes;
    }

    public int getId() { return mJobId; }
    public ComponentName getService() { return mService; }
    public PersistableBundle getExtras() { return mExtras; }
    public int getBackoffPolicy() { return mBackoffPolicy; }
    public int getClipGrantFlags() { return mClipGrantFlags; }
    public long getEstimatedNetworkDownloadBytes() { return mEstimatedNetworkDownloadBytes; }
    public long getEstimatedNetworkUploadBytes() { return mEstimatedNetworkUploadBytes; }
    public long getFlexMillis() { return mFlexMillis; }
    public long getInitialBackoffMillis() { return mInitialBackoffMillis; }
    public long getIntervalMillis() { return mIntervalMillis; }
    public long getMaxExecutionDelayMillis() { return mMaxExecutionDelayMillis; }
    public long getMinLatencyMillis() { return mMinLatencyMillis; }
    public static long getMinFlexMillis() { return MIN_FLEX_MILLIS; }
    public static long getMinPeriodMillis() { return MIN_PERIOD_MILLIS; }
    public long getTriggerContentMaxDelay() { return mTriggerContentMaxDelay; }
    public long getTriggerContentUpdateDelay() { return mTriggerContentUpdateDelay; }
    public boolean isImportantWhileForeground() { return mImportantWhileForeground; }
    public boolean isPeriodic() { return mIntervalMillis > 0; }
    public boolean isPersisted() { return mIsPersisted; }
    public boolean isPrefetch() { return mIsPrefetch; }
    public boolean isRequireBatteryNotLow() { return mRequireBatteryNotLow; }
    public boolean isRequireCharging() { return mRequireCharging; }
    public boolean isRequireDeviceIdle() { return mRequireDeviceIdle; }
    public boolean isRequireStorageNotLow() { return mRequireStorageNotLow; }

    public int describeContents() { return 0; }
    public void writeToParcel(Parcel p0, int p1) {}

    @Override
    public String toString() {
        return "JobInfo{id=" + mJobId + ", service=" + mService
            + ", periodic=" + isPeriodic()
            + ", interval=" + mIntervalMillis
            + ", net=" + mNetworkType + "}";
    }

    // ── Builder ─────────────────────────────────────────────────────────────

    public static class Builder {
        int mJobId;
        ComponentName mService;
        PersistableBundle mExtras;
        int mNetworkType = NETWORK_TYPE_NONE;
        long mMinLatencyMillis;
        long mMaxExecutionDelayMillis;
        boolean mRequireCharging;
        boolean mRequireDeviceIdle;
        boolean mRequireBatteryNotLow;
        boolean mRequireStorageNotLow;
        long mIntervalMillis;
        long mFlexMillis;
        boolean mIsPersisted;
        boolean mIsPrefetch;
        boolean mImportantWhileForeground;
        int mBackoffPolicy = BACKOFF_POLICY_EXPONENTIAL;
        long mInitialBackoffMillis = DEFAULT_INITIAL_BACKOFF_MILLIS;
        long mTriggerContentUpdateDelay = -1;
        long mTriggerContentMaxDelay = -1;
        int mClipGrantFlags;
        long mEstimatedNetworkDownloadBytes = NETWORK_BYTES_UNKNOWN;
        long mEstimatedNetworkUploadBytes = NETWORK_BYTES_UNKNOWN;

        public Builder(int jobId, ComponentName jobService) {
            mJobId = jobId;
            mService = jobService;
        }

        public Builder setExtras(PersistableBundle extras) { mExtras = extras; return this; }
        public Builder setRequiredNetworkType(int type) { mNetworkType = type; return this; }
        public Builder setMinimumLatency(long millis) { mMinLatencyMillis = millis; return this; }
        public Builder setOverrideDeadline(long millis) { mMaxExecutionDelayMillis = millis; return this; }
        public Builder setRequiresCharging(boolean v) { mRequireCharging = v; return this; }
        public Builder setRequiresDeviceIdle(boolean v) { mRequireDeviceIdle = v; return this; }
        public Builder setRequiresBatteryNotLow(boolean v) { mRequireBatteryNotLow = v; return this; }
        public Builder setRequiresStorageNotLow(boolean v) { mRequireStorageNotLow = v; return this; }
        public Builder setPersisted(boolean v) { mIsPersisted = v; return this; }
        public Builder setPrefetch(boolean v) { mIsPrefetch = v; return this; }
        public Builder setImportantWhileForeground(boolean v) { mImportantWhileForeground = v; return this; }
        public Builder setTriggerContentUpdateDelay(long millis) { mTriggerContentUpdateDelay = millis; return this; }
        public Builder setTriggerContentMaxDelay(long millis) { mTriggerContentMaxDelay = millis; return this; }
        public Builder setEstimatedNetworkBytes(long down, long up) {
            mEstimatedNetworkDownloadBytes = down;
            mEstimatedNetworkUploadBytes = up;
            return this;
        }

        public Builder setPeriodic(long intervalMillis) {
            return setPeriodic(intervalMillis, intervalMillis);
        }
        public Builder setPeriodic(long intervalMillis, long flexMillis) {
            mIntervalMillis = Math.max(intervalMillis, MIN_PERIOD_MILLIS);
            mFlexMillis = Math.max(flexMillis, MIN_FLEX_MILLIS);
            mFlexMillis = Math.min(mFlexMillis, mIntervalMillis);
            return this;
        }

        public Builder setBackoffCriteria(long initialBackoffMillis, int backoffPolicy) {
            mInitialBackoffMillis = Math.max(initialBackoffMillis, MIN_FLEX_MILLIS);
            mBackoffPolicy = backoffPolicy;
            return this;
        }

        public JobInfo build() {
            if (mIntervalMillis > 0) {
                if (mMinLatencyMillis > 0) {
                    throw new IllegalArgumentException("Can't set minimum latency on periodic job");
                }
                if (mMaxExecutionDelayMillis > 0) {
                    throw new IllegalArgumentException("Can't set override deadline on periodic job");
                }
            }
            return new JobInfo(this);
        }
    }
}
