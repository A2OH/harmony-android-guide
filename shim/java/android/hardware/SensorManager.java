package android.hardware;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Android-compatible SensorManager stub.
 * Maps to @ohos.sensor on OpenHarmony.
 *
 * This implementation stores listener registrations in-memory.
 * On-device bridge calls (actual @ohos.sensor subscription) would be
 * wired through OHBridge when running on OpenHarmony hardware.
 */
public class SensorManager {

    // ── Sampling-period delay constants ──────────────────────────────────────

    /** Rate suitable for games. */
    public static final int SENSOR_DELAY_FASTEST = 0;
    /** Rate suitable for games. */
    public static final int SENSOR_DELAY_GAME    = 1;
    /** Rate suitable for the user interface. */
    public static final int SENSOR_DELAY_UI      = 2;
    /** Rate (default) suitable for screen orientation changes. */
    public static final int SENSOR_DELAY_NORMAL  = 3;

    // ── Accuracy constants ────────────────────────────────────────────────────

    public static final int SENSOR_STATUS_ACCURACY_LOW    = 1;
    public static final int SENSOR_STATUS_ACCURACY_MEDIUM = 2;
    public static final int SENSOR_STATUS_ACCURACY_HIGH   = 3;
    public static final int SENSOR_STATUS_UNRELIABLE      = 0;

    // ── Gravity / physics constants ───────────────────────────────────────────

    /** Standard acceleration of gravity on Earth (m/s²). */
    public static final float GRAVITY_EARTH = 9.80665f;

    // ── Internal listener registry ────────────────────────────────────────────

    // Maps listener → list of sensors it is registered for
    private final Map<SensorEventListener, List<Sensor>> mListeners = new HashMap<>();

    // ── Sensor catalogue (populated lazily) ──────────────────────────────────

    private static final Sensor ACCEL    = new Sensor("Accelerometer",   Sensor.TYPE_ACCELEROMETER);
    private static final Sensor MAG      = new Sensor("Magnetometer",    Sensor.TYPE_MAGNETIC_FIELD);
    private static final Sensor GYRO     = new Sensor("Gyroscope",       Sensor.TYPE_GYROSCOPE);
    private static final Sensor LIGHT    = new Sensor("Light",           Sensor.TYPE_LIGHT);
    private static final Sensor PROXIMITY= new Sensor("Proximity",       Sensor.TYPE_PROXIMITY);

    // ── Public API ────────────────────────────────────────────────────────────

    /**
     * Returns the default sensor for the given type, or {@code null} if
     * no such sensor is available on this device.
     */
    public Sensor getDefaultSensor(int type) {
        switch (type) {
            case Sensor.TYPE_ACCELEROMETER:  return ACCEL;
            case Sensor.TYPE_MAGNETIC_FIELD: return MAG;
            case Sensor.TYPE_GYROSCOPE:      return GYRO;
            case Sensor.TYPE_LIGHT:          return LIGHT;
            case Sensor.TYPE_PROXIMITY:      return PROXIMITY;
            default:                         return null;
        }
    }

    /**
     * Returns all sensors of the given type, or all sensors if
     * {@code type == Sensor.TYPE_ALL}.
     */
    public List<Sensor> getSensorList(int type) {
        List<Sensor> list = new ArrayList<>();
        Sensor[] all = {ACCEL, MAG, GYRO, LIGHT, PROXIMITY};
        if (type == Sensor.TYPE_ALL) {
            for (Sensor s : all) list.add(s);
        } else {
            for (Sensor s : all) {
                if (s.getType() == type) list.add(s);
            }
        }
        return list;
    }

    /**
     * Registers a {@link SensorEventListener} for the given sensor at the
     * requested sampling rate.
     *
     * @param listener          the listener to receive events
     * @param sensor            the sensor to monitor
     * @param samplingPeriodUs  one of the {@code SENSOR_DELAY_*} constants or
     *                          a rate in microseconds
     * @return {@code true} if the sensor was successfully enabled
     */
    public boolean registerListener(SensorEventListener listener,
                                    Sensor sensor,
                                    int samplingPeriodUs) {
        if (listener == null || sensor == null) return false;

        List<Sensor> sensors = mListeners.get(listener);
        if (sensors == null) {
            sensors = new ArrayList<>();
            mListeners.put(listener, sensors);
        }
        if (!sensors.contains(sensor)) {
            sensors.add(sensor);
        }
        // On-device: OHBridge.sensorSubscribe(sensor.getType(), samplingPeriodUs, listener)
        return true;
    }

    /**
     * Unregisters a listener from all sensors it was registered for.
     */
    public void unregisterListener(SensorEventListener listener) {
        if (listener == null) return;
        mListeners.remove(listener);
        // On-device: OHBridge.sensorUnsubscribeAll(listener)
    }

    /**
     * Unregisters a listener from a specific sensor only.
     */
    public void unregisterListener(SensorEventListener listener, Sensor sensor) {
        if (listener == null || sensor == null) return;
        List<Sensor> sensors = mListeners.get(listener);
        if (sensors != null) {
            sensors.remove(sensor);
            if (sensors.isEmpty()) {
                mListeners.remove(listener);
            }
        }
        // On-device: OHBridge.sensorUnsubscribe(sensor.getType(), listener)
    }

    // ── Test-support helpers (not in Android API) ─────────────────────────────

    /** Returns {@code true} if the given listener is currently registered. */
    public boolean hasListener(SensorEventListener listener) {
        return mListeners.containsKey(listener);
    }

    /** Returns the total number of registered listeners. */
    public int getListenerCount() {
        return mListeners.size();
    }

    /** Returns the sensors registered for the given listener, or an empty list. */
    public List<Sensor> getSensorsForListener(SensorEventListener listener) {
        List<Sensor> result = mListeners.get(listener);
        return result != null ? result : new ArrayList<Sensor>();
    }
}
