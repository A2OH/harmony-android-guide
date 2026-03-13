package android.app.job;

import android.os.Bundle;

/**
 * Android-compatible JobInfo shim.
 * Describes a piece of work to be scheduled by the JobScheduler.
 */
public class JobInfo {

    // Network type constants
    public static final int NETWORK_TYPE_NONE      = 0;
    public static final int NETWORK_TYPE_ANY       = 1;
    public static final int NETWORK_TYPE_UNMETERED = 2;
    public static final int NETWORK_TYPE_NOT_ROAMING = 3;
    public static final int NETWORK_TYPE_CELLULAR  = 4;

    // Backoff policy constants
    public static final int BACKOFF_POLICY_LINEAR      = 0;
    public static final int BACKOFF_POLICY_EXPONENTIAL = 1;

    public static final long DEFAULT_INITIAL_BACKOFF_MILLIS = 30_000L;
    public static final long MAX_BACKOFF_DELAY_MILLIS = 5 * 60 * 60 * 1000L; // 5 hours

    private final int mJobId;
    private final String mService;
    private final long mIntervalMillis;
    private final long mFlexMillis;
    private final long mMinLatencyMillis;
    private final long mMaxExecutionDelayMillis;
    private final int mNetworkType;
    private final boolean mRequiresCharging;
    private final boolean mRequiresDeviceIdle;
    private final boolean mPersisted;
    private final Bundle mExtras;
    private final int mBackoffPolicy;
    private final long mInitialBackoffMillis;

    private JobInfo(Builder builder) {
        mJobId                  = builder.mJobId;
        mService                = builder.mService;
        mIntervalMillis         = builder.mIntervalMillis;
        mFlexMillis             = builder.mFlexMillis;
        mMinLatencyMillis       = builder.mMinLatencyMillis;
        mMaxExecutionDelayMillis = builder.mMaxExecutionDelayMillis;
        mNetworkType            = builder.mNetworkType;
        mRequiresCharging       = builder.mRequiresCharging;
        mRequiresDeviceIdle     = builder.mRequiresDeviceIdle;
        mPersisted              = builder.mPersisted;
        mExtras                 = builder.mExtras != null ? builder.mExtras : new Bundle();
        mBackoffPolicy          = builder.mBackoffPolicy;
        mInitialBackoffMillis   = builder.mInitialBackoffMillis;
    }

    public int getId()                           { return mJobId; }
    public String getService()                   { return mService; }
    public long getIntervalMillis()              { return mIntervalMillis; }
    public long getFlexMillis()                  { return mFlexMillis; }
    public long getMinLatencyMillis()            { return mMinLatencyMillis; }
    public long getMaxExecutionDelayMillis()     { return mMaxExecutionDelayMillis; }
    public int getNetworkType()                  { return mNetworkType; }
    public boolean isRequireCharging()           { return mRequiresCharging; }
    public boolean isRequireDeviceIdle()         { return mRequiresDeviceIdle; }
    public boolean isPersisted()                 { return mPersisted; }
    public Bundle getExtras()                    { return mExtras; }
    public int getBackoffPolicy()                { return mBackoffPolicy; }
    public long getInitialBackoffMillis()        { return mInitialBackoffMillis; }
    public boolean isPeriodic()                  { return mIntervalMillis > 0; }

    @Override
    public String toString() {
        return "JobInfo{id=" + mJobId + ", service=" + mService + "}";
    }

    // -----------------------------------------------------------------------
    // Builder
    // -----------------------------------------------------------------------

    public static final class Builder {
        private final int mJobId;
        private final String mService;
        private long mIntervalMillis = 0;
        private long mFlexMillis = 0;
        private long mMinLatencyMillis = 0;
        private long mMaxExecutionDelayMillis = 0;
        private int  mNetworkType = NETWORK_TYPE_NONE;
        private boolean mRequiresCharging = false;
        private boolean mRequiresDeviceIdle = false;
        private boolean mPersisted = false;
        private Bundle mExtras = null;
        private int  mBackoffPolicy = BACKOFF_POLICY_EXPONENTIAL;
        private long mInitialBackoffMillis = DEFAULT_INITIAL_BACKOFF_MILLIS;

        /**
         * @param jobId   Application-provided ID for this job. Must be unique per app UID.
         * @param jobService Class name of the JobService that will execute the job.
         */
        public Builder(int jobId, String jobService) {
            mJobId   = jobId;
            mService = jobService;
        }

        /**
         * Convenience constructor that accepts a Class instead of a class name.
         */
        public Builder(int jobId, Class<?> jobService) {
            this(jobId, jobService.getName());
        }

        /** Run this job periodically with the given interval. */
        public Builder setPeriodic(long intervalMillis) {
            mIntervalMillis = intervalMillis;
            mFlexMillis     = intervalMillis;
            return this;
        }

        /** Run this job periodically with the given interval and flex window. */
        public Builder setPeriodic(long intervalMillis, long flexMillis) {
            mIntervalMillis = intervalMillis;
            mFlexMillis     = flexMillis;
            return this;
        }

        /** Specify the network type required for this job. */
        public Builder setRequiredNetworkType(int networkType) {
            mNetworkType = networkType;
            return this;
        }

        /** Require the device to be charging before this job runs. */
        public Builder setRequiresCharging(boolean requiresCharging) {
            mRequiresCharging = requiresCharging;
            return this;
        }

        /** Require the device to be in idle mode before this job runs. */
        public Builder setRequiresDeviceIdle(boolean requiresDeviceIdle) {
            mRequiresDeviceIdle = requiresDeviceIdle;
            return this;
        }

        /** Persist this job across device reboots. */
        public Builder setPersisted(boolean isPersisted) {
            mPersisted = isPersisted;
            return this;
        }

        /** Set the minimum delay before this one-shot job is eligible to run. */
        public Builder setMinimumLatency(long minLatencyMillis) {
            mMinLatencyMillis = minLatencyMillis;
            return this;
        }

        /** Set the deadline by which this one-shot job must be run. */
        public Builder setOverrideDeadline(long maxExecutionDelayMillis) {
            mMaxExecutionDelayMillis = maxExecutionDelayMillis;
            return this;
        }

        /** Attach a Bundle of extras to pass to the JobService. */
        public Builder setExtras(Bundle extras) {
            mExtras = extras;
            return this;
        }

        /** Set the back-off/retry policy. */
        public Builder setBackoffCriteria(long initialBackoffMillis, int backoffPolicy) {
            mInitialBackoffMillis = initialBackoffMillis;
            mBackoffPolicy        = backoffPolicy;
            return this;
        }

        /** Build the JobInfo object. */
        public JobInfo build() {
            return new JobInfo(this);
        }
    }
}
