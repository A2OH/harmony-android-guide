package android.location;

/**
 * Android-compatible GnssClock shim. Stub — returns default clock values.
 */
public class GnssClock {

    private long    mTimeNanos;
    private double  mTimeUncertaintyNanos;
    private boolean mHasTimeUncertaintyNanos;
    private double  mBiasNanos;
    private boolean mHasBiasNanos;
    private long    mFullBiasNanos;
    private boolean mHasFullBiasNanos;

    public GnssClock() {}

    public long    getTimeNanos()                { return mTimeNanos; }
    public void    setTimeNanos(long v)          { mTimeNanos = v; }

    public boolean hasTimeUncertaintyNanos()     { return mHasTimeUncertaintyNanos; }
    public double  getTimeUncertaintyNanos()     { return mTimeUncertaintyNanos; }
    public void    setTimeUncertaintyNanos(double v) { mTimeUncertaintyNanos = v; mHasTimeUncertaintyNanos = true; }

    public boolean hasBiasNanos()               { return mHasBiasNanos; }
    public double  getBiasNanos()               { return mBiasNanos; }
    public void    setBiasNanos(double v)       { mBiasNanos = v; mHasBiasNanos = true; }

    public boolean hasFullBiasNanos()           { return mHasFullBiasNanos; }
    public long    getFullBiasNanos()           { return mFullBiasNanos; }
    public void    setFullBiasNanos(long v)     { mFullBiasNanos = v; mHasFullBiasNanos = true; }
}
