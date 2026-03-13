package android.bluetooth.le;

/**
 * Android-compatible ScanSettings shim. Stub.
 */
public class ScanSettings {

    public static final int SCAN_MODE_LOW_POWER   = 0;
    public static final int SCAN_MODE_BALANCED     = 1;
    public static final int SCAN_MODE_LOW_LATENCY  = 2;

    public static final int CALLBACK_TYPE_ALL_MATCHES = 1;
    public static final int CALLBACK_TYPE_FIRST_MATCH = 2;
    public static final int CALLBACK_TYPE_MATCH_LOST  = 4;

    private final int mScanMode;
    private final long mReportDelay;
    private final int mCallbackType;

    private ScanSettings(Builder builder) {
        mScanMode     = builder.mScanMode;
        mReportDelay  = builder.mReportDelay;
        mCallbackType = builder.mCallbackType;
    }

    public int getScanMode() { return mScanMode; }

    public long getReportDelayMillis() { return mReportDelay; }

    public int getCallbackType() { return mCallbackType; }

    public static class Builder {
        private int  mScanMode     = SCAN_MODE_LOW_POWER;
        private long mReportDelay  = 0;
        private int  mCallbackType = CALLBACK_TYPE_ALL_MATCHES;

        public Builder setScanMode(int scanMode) {
            mScanMode = scanMode;
            return this;
        }

        public Builder setReportDelay(long reportDelayMillis) {
            mReportDelay = reportDelayMillis;
            return this;
        }

        public Builder setCallbackType(int callbackType) {
            mCallbackType = callbackType;
            return this;
        }

        public ScanSettings build() {
            return new ScanSettings(this);
        }
    }
}
