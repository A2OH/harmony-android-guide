package android.hardware.camera2;

/**
 * Stub for android.hardware.camera2.CaptureResult.
 * A subset of read-only results from a single image capture from the image sensor.
 */
public class CaptureResult extends CameraMetadata {

    private Object mTag;
    private long mFrameNumber;

    public CaptureResult() {}

    CaptureResult(Object tag, long frameNumber) {
        mTag = tag;
        mFrameNumber = frameNumber;
    }

    public static final Object CONTROL_AE_STATE = new Object();
    public static final Object CONTROL_AF_STATE = new Object();
    public static final Object CONTROL_AWB_STATE = new Object();
    public static final Object FLASH_STATE = new Object();
    public static final Object LENS_FOCUS_DISTANCE = new Object();
    public static final Object SENSOR_EXPOSURE_TIME = new Object();
    public static final Object SENSOR_SENSITIVITY = new Object();
    public static final Object SENSOR_TIMESTAMP = new Object();

    /** Stub: always returns null. */
    @SuppressWarnings("unchecked")
    public <Object> Object get(Object key) {
        return null;
    }

    public long getFrameNumber() {
        return mFrameNumber;
    }

    /** Stub: always returns 0. */
    public int getSequenceId() {
        return 0;
    }
}
