package android.location;

/**
 * Android-compatible GnssMeasurement shim. Stub — returns default sensor values.
 */
public class GnssMeasurement {

    public static final int STATE_CODE_LOCK = 1;
    public static final int STATE_BIT_SYNC  = 2;
    public static final int STATE_TOW_DECODED = 8;

    private int mConstellationType;
    private int mSvid;
    private double mTimeOffsetNanos;
    private long mReceivedSvTimeNanos;
    private long mReceivedSvTimeUncertaintyNanos;
    private double mCn0DbHz;
    private double mPseudorangeRateMetersPerSecond;
    private int mState;
    private int mAccumulatedDeltaRangeState;
    private float mCarrierFrequencyHz;
    private boolean mHasCarrierFrequencyHz;

    public GnssMeasurement() {}

    public int    getConstellationType()                    { return mConstellationType; }
    public void   setConstellationType(int v)               { mConstellationType = v; }

    public int    getSvid()                                 { return mSvid; }
    public void   setSvid(int v)                            { mSvid = v; }

    public double getTimeOffsetNanos()                      { return mTimeOffsetNanos; }
    public void   setTimeOffsetNanos(double v)              { mTimeOffsetNanos = v; }

    public long   getReceivedSvTimeNanos()                  { return mReceivedSvTimeNanos; }
    public void   setReceivedSvTimeNanos(long v)            { mReceivedSvTimeNanos = v; }

    public long   getReceivedSvTimeUncertaintyNanos()       { return mReceivedSvTimeUncertaintyNanos; }
    public void   setReceivedSvTimeUncertaintyNanos(long v) { mReceivedSvTimeUncertaintyNanos = v; }

    public double getCn0DbHz()                              { return mCn0DbHz; }
    public void   setCn0DbHz(double v)                      { mCn0DbHz = v; }

    public double getPseudorangeRateMetersPerSecond()       { return mPseudorangeRateMetersPerSecond; }
    public void   setPseudorangeRateMetersPerSecond(double v) { mPseudorangeRateMetersPerSecond = v; }

    public int    getState()                                { return mState; }
    public void   setState(int v)                           { mState = v; }

    public int    getAccumulatedDeltaRangeState()           { return mAccumulatedDeltaRangeState; }
    public void   setAccumulatedDeltaRangeState(int v)      { mAccumulatedDeltaRangeState = v; }

    public boolean hasCarrierFrequencyHz()                  { return mHasCarrierFrequencyHz; }
    public float   getCarrierFrequencyHz()                  { return mCarrierFrequencyHz; }
    public void    setCarrierFrequencyHz(float v)           { mCarrierFrequencyHz = v; mHasCarrierFrequencyHz = true; }
}
