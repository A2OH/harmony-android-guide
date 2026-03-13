package android.location;

import java.util.Collection;
import java.util.Collections;

/**
 * Android-compatible GnssMeasurementsEvent shim. Stub — returns provided measurements.
 */
public class GnssMeasurementsEvent {

    public static final int STATUS_NOT_SUPPORTED = 0;
    public static final int STATUS_READY         = 1;

    private final GnssClock mClock;
    private final Collection<GnssMeasurement> mMeasurements;

    public GnssMeasurementsEvent(GnssClock clock, Collection<GnssMeasurement> measurements) {
        mClock        = clock;
        mMeasurements = measurements != null
                ? Collections.unmodifiableCollection(measurements)
                : Collections.<GnssMeasurement>emptyList();
    }

    public GnssClock getClock() { return mClock; }

    public Collection<GnssMeasurement> getMeasurements() { return mMeasurements; }

    /** Callback registered with LocationManager to receive GNSS raw measurements. */
    public static abstract class Callback {
        public abstract void onGnssMeasurementsReceived(GnssMeasurementsEvent event);
        public abstract void onStatusChanged(int status);
    }
}
