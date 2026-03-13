package android.location;
import java.util.Set;

/**
 * Android-compatible LocationRequest shim. Stub — stores request parameters only.
 */
public class LocationRequest {

    public static final int PRIORITY_HIGH_ACCURACY             = 100;
    public static final int PRIORITY_BALANCED_POWER_ACCURACY   = 102;
    public static final int PRIORITY_LOW_POWER                 = 104;
    public static final int PRIORITY_NO_POWER                  = 105;

    private int   mPriority             = PRIORITY_BALANCED_POWER_ACCURACY;
    private long  mInterval             = 0;
    private long  mFastestInterval      = 0;
    private float mSmallestDisplacement = 0f;
    private int   mNumUpdates           = Integer.MAX_VALUE;
    private long  mExpirationTime       = Long.MAX_VALUE;

    public LocationRequest() {}

    /** Set the desired interval in milliseconds. */
    public LocationRequest setInterval(long millis) {
        mInterval = millis;
        return this;
    }

    /** Set the fastest permissible interval in milliseconds. */
    public LocationRequest setFastestInterval(long millis) {
        mFastestInterval = millis;
        return this;
    }

    public LocationRequest setPriority(int priority) {
        mPriority = priority;
        return this;
    }

    /** Set the minimum displacement in metres between updates. */
    public LocationRequest setSmallestDisplacement(float smallestDisplacementMeters) {
        mSmallestDisplacement = smallestDisplacementMeters;
        return this;
    }

    public LocationRequest setNumUpdates(int numUpdates) {
        mNumUpdates = numUpdates;
        return this;
    }

    /** Set the request expiration as an absolute elapsed-realtime value in milliseconds. */
    public LocationRequest setExpirationTime(long millis) {
        mExpirationTime = millis;
        return this;
    }

    public int   getPriority()              { return mPriority; }
    public long  getInterval()              { return mInterval; }
    public long  getFastestInterval()       { return mFastestInterval; }
    public float getSmallestDisplacement()  { return mSmallestDisplacement; }
    public int   getNumUpdates()            { return mNumUpdates; }
    public long  getExpirationTime()        { return mExpirationTime; }

    // -----------------------------------------------------------------
    // Builder (API 31+)
    // -----------------------------------------------------------------

    public static final class Builder {

        private final LocationRequest mRequest = new LocationRequest();

        public Builder(long intervalMillis) {
            mRequest.mInterval = intervalMillis;
        }

        public Builder setIntervalMillis(long millis) {
            mRequest.mInterval = millis;
            return this;
        }

        public Builder setMinUpdateIntervalMillis(long millis) {
            mRequest.mFastestInterval = millis;
            return this;
        }

        public Builder setQuality(int priority) {
            mRequest.mPriority = priority;
            return this;
        }

        public Builder setMinUpdateDistanceMeters(float meters) {
            mRequest.mSmallestDisplacement = meters;
            return this;
        }

        public Builder setMaxUpdates(int maxUpdates) {
            mRequest.mNumUpdates = maxUpdates;
            return this;
        }

        public Builder setDurationMillis(long durationMillis) {
            mRequest.mExpirationTime = durationMillis;
            return this;
        }

        public LocationRequest build() {
            return mRequest;
        }
    }
}
