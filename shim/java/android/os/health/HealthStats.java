package android.os.health;

import java.util.Collections;
import java.util.Map;

/**
 * A2OH shim: HealthStats - snapshot of health data for a UID.
 * All query methods return 0 / empty stubs; no real data is collected.
 */
public class HealthStats {

    private final String mDataType;

    /** Package-private constructor; created by SystemHealthManager. */
    HealthStats(String dataType) {
        mDataType = dataType;
    }

    /** Returns the data-type tag for this snapshot (e.g. "uid"). */
    public String getDataType() {
        return mDataType;
    }

    // ---- Timer queries -------------------------------------------------------

    public boolean hasTimer(int key) {
        return false;
    }

    public TimerStat getTimer(int key) {
        return new TimerStat(0);
    }

    public int getTimerCount(int key) {
        return 0;
    }

    public long getTimerTime(int key) {
        return 0L;
    }

    public Map<Integer, TimerStat> getTimers() {
        return Collections.emptyMap();
    }

    // ---- Measurement queries -------------------------------------------------

    public boolean hasMeasurement(int key) {
        return false;
    }

    public long getMeasurement(int key) {
        return 0L;
    }

    public int getMeasurementCount() {
        return 0;
    }

    public Map<Integer, Long> getMeasurements() {
        return Collections.emptyMap();
    }

    // ---- Stats sub-map queries -----------------------------------------------

    public boolean hasStats(int key) {
        return false;
    }

    public Map<String, HealthStats> getStats(int key) {
        return Collections.emptyMap();
    }
}
