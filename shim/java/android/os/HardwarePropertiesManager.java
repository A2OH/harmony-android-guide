package android.os;

/**
 * Android-compatible HardwarePropertiesManager shim.
 * Provides stub implementations of thermal and CPU usage queries.
 */
public class HardwarePropertiesManager {

    /** Device temperature type: CPU cores. */
    public static final int DEVICE_TEMPERATURE_CPU = 0;
    /** Device temperature type: GPU. */
    public static final int DEVICE_TEMPERATURE_GPU = 1;
    /** Device temperature type: battery. */
    public static final int DEVICE_TEMPERATURE_BATTERY = 2;
    /** Device temperature type: skin (device surface). */
    public static final int DEVICE_TEMPERATURE_SKIN = 3;

    /** Temperature source: current reading. */
    public static final int TEMPERATURE_CURRENT = 0;
    /** Temperature source: throttling threshold. */
    public static final int TEMPERATURE_THROTTLING = 1;
    /** Temperature source: shutdown threshold. */
    public static final int TEMPERATURE_SHUTDOWN = 2;
    /** Temperature source: throttling threshold below VR minimum. */
    public static final int TEMPERATURE_THROTTLING_BELOW_VR_MIN = 3;

    /** Sentinel value returned when a temperature is not available. */
    public static final float UNDEFINED_TEMPERATURE = Float.NaN;

    /**
     * Returns device temperatures for the given type and source.
     *
     * @param type   one of {@code DEVICE_TEMPERATURE_*}
     * @param source one of {@code TEMPERATURE_*}
     * @return array of temperatures in degrees Celsius; empty on this shim
     */
    public float[] getDeviceTemperatures(int type, int source) {
        return new float[0];
    }

    /**
     * Returns current fan speeds in RPM.
     *
     * @return array of fan speeds; empty on this shim
     */
    public float[] getFanSpeeds() {
        return new float[0];
    }

    /**
     * Returns CPU usage information per core.
     *
     * @return array of {@link android.os.CpuUsageInfo}; empty on this shim
     */
    public CpuUsageInfo[] getCpuUsages() {
        return new CpuUsageInfo[0];
    }
}
