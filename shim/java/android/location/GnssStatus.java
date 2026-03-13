package android.location;

/**
 * Android-compatible GnssStatus shim. Stub — returns zero satellites.
 */
public class GnssStatus {

    public static final int CONSTELLATION_GPS     = 1;
    public static final int CONSTELLATION_SBAS    = 2;
    public static final int CONSTELLATION_GLONASS = 3;
    public static final int CONSTELLATION_QZSS    = 4;
    public static final int CONSTELLATION_BEIDOU  = 5;
    public static final int CONSTELLATION_GALILEO = 6;

    private final int     mSatelliteCount;
    private final int[]   mConstellationTypes;
    private final int[]   mSvids;
    private final float[] mCn0DbHz;
    private final float[] mElevations;
    private final float[] mAzimuths;
    private final boolean[] mHasAlmanac;
    private final boolean[] mHasEphemeris;
    private final boolean[] mUsedInFix;

    /** Construct an empty (zero-satellite) status. */
    public GnssStatus() {
        mSatelliteCount     = 0;
        mConstellationTypes = new int[0];
        mSvids              = new int[0];
        mCn0DbHz            = new float[0];
        mElevations         = new float[0];
        mAzimuths           = new float[0];
        mHasAlmanac         = new boolean[0];
        mHasEphemeris       = new boolean[0];
        mUsedInFix          = new boolean[0];
    }

    public int     getSatelliteCount()                     { return mSatelliteCount; }
    public int     getConstellationType(int satelliteIndex){ return mConstellationTypes[satelliteIndex]; }
    public int     getSvid(int satelliteIndex)             { return mSvids[satelliteIndex]; }
    public float   getCn0DbHz(int satelliteIndex)          { return mCn0DbHz[satelliteIndex]; }
    public float   getElevationDegrees(int satelliteIndex) { return mElevations[satelliteIndex]; }
    public float   getAzimuthDegrees(int satelliteIndex)   { return mAzimuths[satelliteIndex]; }
    public boolean hasAlmanacData(int satelliteIndex)      { return mHasAlmanac[satelliteIndex]; }
    public boolean hasEphemerisData(int satelliteIndex)    { return mHasEphemeris[satelliteIndex]; }
    public boolean usedInFix(int satelliteIndex)           { return mUsedInFix[satelliteIndex]; }

    /** Object registered with LocationManager for GNSS status updates. */
    public static abstract class Object {
        public void onStarted()  {}
        public void onStopped() {}
        public void onFirstFix(int ttffMillis) {}
        public void onSatelliteStatusChanged(GnssStatus status) {}
    }
}
