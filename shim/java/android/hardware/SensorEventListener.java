package android.hardware;

/**
 * Android-compatible SensorEventListener interface stub.
 * Maps to @ohos.sensor callback registration.
 */
public interface SensorEventListener {
    /**
     * Called when there is a new sensor event.
     */
    void onSensorChanged(SensorEvent event);

    /**
     * Called when the accuracy of the registered sensor has changed.
     */
    void onAccuracyChanged(Sensor sensor, int accuracy);
}
