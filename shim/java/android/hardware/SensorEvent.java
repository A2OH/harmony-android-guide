package android.hardware;

/**
 * Android-compatible SensorEvent stub.
 * Maps to the data delivered by @ohos.sensor callbacks.
 */
public class SensorEvent {
    /** The values reported by the sensor. Length depends on the sensor type. */
    public float[] values;

    /** The sensor that generated this event. */
    public Sensor sensor;

    /**
     * The accuracy of this event.
     * One of {@link SensorManager#SENSOR_STATUS_ACCURACY_LOW},
     * {@link SensorManager#SENSOR_STATUS_ACCURACY_MEDIUM},
     * or {@link SensorManager#SENSOR_STATUS_ACCURACY_HIGH}.
     */
    public int accuracy;

    /** The time in nanoseconds at which the event happened. */
    public long timestamp;

    /** Construct an empty SensorEvent; fields are public and set directly. */
    public SensorEvent() {}

    /** Convenience constructor that pre-allocates the values array. */
    public SensorEvent(Sensor sensor, int valueCount) {
        this.sensor = sensor;
        this.values = new float[valueCount];
    }

    @Override
    public String toString() {
        return "SensorEvent{sensor=" + sensor
                + ", accuracy=" + accuracy
                + ", timestamp=" + timestamp
                + ", values.length=" + (values != null ? values.length : 0) + "}";
    }
}
