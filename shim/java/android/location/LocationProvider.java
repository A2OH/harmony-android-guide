package android.location;

/**
 * Android-compatible LocationProvider shim. Stub — stores provider capability metadata.
 */
public class LocationProvider {

    public static final int OUT_OF_SERVICE        = 0;
    public static final int TEMPORARILY_UNAVAILABLE = 1;
    public static final int AVAILABLE             = 2;

    private final String mName;
    private boolean mRequiresNetwork;
    private boolean mRequiresSatellite;
    private boolean mRequiresCell;
    private boolean mHasMonetaryCost;
    private boolean mSupportsAltitude;
    private boolean mSupportsBearing;
    private boolean mSupportsSpeed;
    private int     mPowerRequirement;
    private int     mAccuracy;

    public LocationProvider(String name) {
        mName = name;
    }

    public String  getName()              { return mName; }
    public boolean requiresNetwork()      { return mRequiresNetwork; }
    public boolean requiresSatellite()    { return mRequiresSatellite; }
    public boolean requiresCell()         { return mRequiresCell; }
    public boolean hasMonetaryCost()      { return mHasMonetaryCost; }
    public boolean supportsAltitude()     { return mSupportsAltitude; }
    public boolean supportsBearing()      { return mSupportsBearing; }
    public boolean supportsSpeed()        { return mSupportsSpeed; }
    public int     getPowerRequirement()  { return mPowerRequirement; }
    public int     getAccuracy()          { return mAccuracy; }

    public void setRequiresNetwork(boolean v)   { mRequiresNetwork = v; }
    public void setRequiresSatellite(boolean v) { mRequiresSatellite = v; }
    public void setRequiresCell(boolean v)      { mRequiresCell = v; }
    public void setHasMonetaryCost(boolean v)   { mHasMonetaryCost = v; }
    public void setSupportsAltitude(boolean v)  { mSupportsAltitude = v; }
    public void setSupportsBearing(boolean v)   { mSupportsBearing = v; }
    public void setSupportsSpeed(boolean v)     { mSupportsSpeed = v; }
    public void setPowerRequirement(int v)      { mPowerRequirement = v; }
    public void setAccuracy(int v)              { mAccuracy = v; }
}
