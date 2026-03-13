package android.location;

/**
 * Android-compatible Criteria stub for selecting a location provider.
 * Maps to @ohos.geoLocationManager provider selection.
 */
public class Criteria {

    public static final int ACCURACY_FINE   = 1;
    public static final int ACCURACY_COARSE = 2;

    public static final int POWER_LOW    = 1;
    public static final int POWER_MEDIUM = 2;
    public static final int POWER_HIGH   = 3;

    private int mAccuracy        = ACCURACY_COARSE;
    private int mPowerRequirement = POWER_LOW;

    public Criteria() {}

    public int  getAccuracy()              { return mAccuracy; }
    public void setAccuracy(int accuracy)  { mAccuracy = accuracy; }

    public int  getPowerRequirement()                  { return mPowerRequirement; }
    public void setPowerRequirement(int requirement)   { mPowerRequirement = requirement; }

    @Override
    public String toString() {
        return "Criteria[accuracy=" + mAccuracy + " power=" + mPowerRequirement + "]";
    }
}
