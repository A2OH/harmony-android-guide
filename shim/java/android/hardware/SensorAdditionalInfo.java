package android.hardware;
import android.renderscript.Type;
import android.renderscript.Type;

/**
 * Android-compatible SensorAdditionalInfo shim.
 * Carries additional information associated with a sensor event frame.
 */
public class SensorAdditionalInfo {

    // --- Type constants ---

    /** Marks the beginning of a frame of sensor events. */
    public static final int TYPE_FRAME_BEGIN          = 0;
    /** Marks the end of a frame of sensor events. */
    public static final int TYPE_FRAME_END            = 1;
    /** Reports untracked delay between events. */
    public static final int TYPE_UNTRACKED_DELAY      = 0x10000;
    /** Reports the sensor's internal temperature. */
    public static final int TYPE_INTERNAL_TEMPERATURE = 0x10001;

    // --- Fields (public, matching Android API) ---

    /** The type of this additional info record; one of the TYPE_* constants. */
    public final int type;

    /** Serial number of this additional info record within a frame. */
    public final int serial;

    /** The sensor that generated this additional info. May be null. */
    public final Sensor sensor;

    /** Float-valued payload, or null if not applicable. */
    public final float[] floatValues;

    /** Integer-valued payload, or null if not applicable. */
    public final int[] intValues;

    /**
     * Construct a SensorAdditionalInfo with float payload.
     *
     * @param sensor      sensor that generated this info
     * @param type        one of the TYPE_* constants
     * @param serial      serial number within the frame
     * @param floatValues float payload (may be null)
     */
    public SensorAdditionalInfo(Sensor sensor, int type, int serial, float[] floatValues) {
        this.sensor      = sensor;
        this.type        = type;
        this.serial      = serial;
        this.floatValues = floatValues;
        this.intValues   = null;
    }

    /**
     * Construct a SensorAdditionalInfo with integer payload.
     *
     * @param sensor    sensor that generated this info
     * @param type      one of the TYPE_* constants
     * @param serial    serial number within the frame
     * @param intValues integer payload (may be null)
     */
    public SensorAdditionalInfo(Sensor sensor, int type, int serial, int[] intValues) {
        this.sensor      = sensor;
        this.type        = type;
        this.serial      = serial;
        this.floatValues = null;
        this.intValues   = intValues;
    }

    @Override
    public String toString() {
        return "SensorAdditionalInfo{type=0x" + Integer.toHexString(type)
                + ", serial=" + serial + ", sensor=" + sensor + "}";
    }
}
