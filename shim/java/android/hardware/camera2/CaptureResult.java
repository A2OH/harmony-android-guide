package android.hardware.camera2;

import java.util.HashMap;
import java.util.Map;

/**
 * Android-compatible CaptureResult stub.
 * Contains the result of a single image capture from the camera device.
 */
public class CaptureResult extends CameraMetadata {

    // -----------------------------------------------------------------------
    // Key constants (string identifiers matching Android camera2 API)
    // -----------------------------------------------------------------------
    public static final String KEY_CONTROL_AE_MODE            = "android.control.aeMode";
    public static final String KEY_CONTROL_AF_MODE            = "android.control.afMode";
    public static final String KEY_CONTROL_AWB_MODE           = "android.control.awbMode";
    public static final String KEY_CONTROL_MODE               = "android.control.mode";
    public static final String KEY_CONTROL_AE_EXPOSURE_COMPENSATION
                                                              = "android.control.aeExposureCompensation";
    public static final String KEY_CONTROL_AE_PRECAPTURE_TRIGGER
                                                              = "android.control.aePrecaptureTrigger";
    public static final String KEY_CONTROL_AF_TRIGGER         = "android.control.afTrigger";
    public static final String KEY_CONTROL_AE_STATE           = "android.control.aeState";
    public static final String KEY_CONTROL_AF_STATE           = "android.control.afState";
    public static final String KEY_CONTROL_AWB_STATE          = "android.control.awbState";
    public static final String KEY_FLASH_MODE                 = "android.flash.mode";
    public static final String KEY_FLASH_STATE                = "android.flash.state";
    public static final String KEY_LENS_FOCAL_LENGTH          = "android.lens.focalLength";
    public static final String KEY_LENS_FOCUS_DISTANCE        = "android.lens.focusDistance";
    public static final String KEY_LENS_APERTURE              = "android.lens.aperture";
    public static final String KEY_SENSOR_EXPOSURE_TIME       = "android.sensor.exposureTime";
    public static final String KEY_SENSOR_SENSITIVITY         = "android.sensor.sensitivity";
    public static final String KEY_SENSOR_FRAME_DURATION      = "android.sensor.frameDuration";
    public static final String KEY_JPEG_QUALITY               = "android.jpeg.quality";
    public static final String KEY_JPEG_ORIENTATION           = "android.jpeg.orientation";
    public static final String KEY_STATISTICS_FACE_DETECT_MODE
                                                              = "android.statistics.faceDetectMode";

    // AE state values
    public static final int CONTROL_AE_STATE_INACTIVE         = 0;
    public static final int CONTROL_AE_STATE_SEARCHING        = 1;
    public static final int CONTROL_AE_STATE_CONVERGED        = 2;
    public static final int CONTROL_AE_STATE_LOCKED           = 3;
    public static final int CONTROL_AE_STATE_FLASH_REQUIRED   = 4;
    public static final int CONTROL_AE_STATE_PRECAPTURE       = 5;

    // AF state values
    public static final int CONTROL_AF_STATE_INACTIVE            = 0;
    public static final int CONTROL_AF_STATE_PASSIVE_SCAN        = 1;
    public static final int CONTROL_AF_STATE_PASSIVE_FOCUSED     = 2;
    public static final int CONTROL_AF_STATE_ACTIVE_SCAN         = 3;
    public static final int CONTROL_AF_STATE_FOCUSED_LOCKED      = 4;
    public static final int CONTROL_AF_STATE_NOT_FOCUSED_LOCKED  = 5;
    public static final int CONTROL_AF_STATE_PASSIVE_UNFOCUSED   = 6;

    private final Map<String, Object> mEntries = new HashMap<>();
    private final long mFrameNumber;

    public CaptureResult() {
        mFrameNumber = 0L;
    }

    public CaptureResult(long frameNumber) {
        mFrameNumber = frameNumber;
    }

    public CaptureResult(Object tag, long frameNumber) {
        mFrameNumber = frameNumber;
    }

    /**
     * Returns the value associated with the given key, or null if not present.
     *
     * @param key  one of the KEY_* string constants
     * @param <T>  expected type of the value
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        return (T) mEntries.get(key);
    }

    /**
     * Stores a key-value pair in this result (used by shim producers).
     */
    public void put(String key, Object value) {
        mEntries.put(key, value);
    }

    /** Returns the frame number for this result. */
    public long getFrameNumber() {
        return mFrameNumber;
    }

    @Override
    public String toString() {
        return "CaptureResult{frameNumber=" + mFrameNumber
             + ", entries=" + mEntries.size() + "}";
    }
}
