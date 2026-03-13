package android.hardware.camera2;

/**
 * Shim stub for android.hardware.camera2.CameraCharacteristics.
 * All methods are no-ops or return null; real implementations route through OHBridge.
 */
public class CameraCharacteristics {

    /** Typed key used to query a camera characteristic. */
    public static final class Key<Object> {
        private final String name;

        public Key(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    // -----------------------------------------------------------------------
    // Well-known keys
    // -----------------------------------------------------------------------

    /** Which direction the camera lens faces (front / back / external). */
    public static final Key<Integer> LENS_FACING =
            new Key<>("android.lens.facing");

    /** Clockwise angle (degrees) through which the output image must be rotated
     *  to be upright on the device screen in its native orientation. */
    public static final Key<Integer> SENSOR_ORIENTATION =
            new Key<>("android.sensor.orientation");

    // -----------------------------------------------------------------------
    // API
    // -----------------------------------------------------------------------

    /**
     * Returns the value for {@code key}, or {@code null} if this stub does not
     * populate characteristics data.
     */
    @SuppressWarnings("unchecked")
    public <Object> Object get(Key<Object> key) {
        // Stub: real behaviour would delegate to native camera metadata.
        return null;
    }
}
