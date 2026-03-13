package android.content;

/**
 * Android-compatible SyncRequest shim. Stub.
 */
public class SyncRequest {
    private Object mBundle;
    private String mAuthority;
    private String mAccountType;
    private boolean mIsPeriodic;
    private long mSyncFlexTimeSecs;
    private long mSyncRunTimeSecs;

    private SyncRequest(Builder builder) {
        this.mBundle = builder.mBundle;
        this.mAuthority = builder.mAuthority;
        this.mAccountType = builder.mAccountType;
        this.mIsPeriodic = builder.mIsPeriodic;
        this.mSyncFlexTimeSecs = builder.mSyncFlexTimeSecs;
        this.mSyncRunTimeSecs = builder.mSyncRunTimeSecs;
    }

    public Object getBundle() {
        return mBundle;
    }

    public static class Builder {
        private Object mBundle;
        private String mAuthority;
        private String mAccountType;
        private boolean mIsPeriodic;
        private long mSyncFlexTimeSecs;
        private long mSyncRunTimeSecs;

        public Builder setSyncAdapter(Object account, String authority) {
            this.mAuthority = authority;
            return this;
        }

        public Builder syncOnce() {
            this.mIsPeriodic = false;
            return this;
        }

        public Builder syncPeriodic(long pollFrequency, long beforeSeconds) {
            this.mIsPeriodic = true;
            this.mSyncRunTimeSecs = pollFrequency;
            this.mSyncFlexTimeSecs = beforeSeconds;
            return this;
        }

        public Builder setExtras(Object extras) {
            this.mBundle = extras;
            return this;
        }

        public SyncRequest build() {
            return new SyncRequest(this);
        }
    }
}
