package android.hardware.camera2;

import java.util.List;

/**
 * Android CameraDevice shim for OpenHarmony migration.
 *
 * Maps to OpenHarmony's camera NDK (@ohos.multimedia.camera).
 */
public class CameraDevice {

    public static final int TEMPLATE_PREVIEW = 1;
    public static final int TEMPLATE_STILL_CAPTURE = 2;
    public static final int TEMPLATE_RECORD = 3;
    public static final int TEMPLATE_VIDEO_SNAPSHOT = 4;
    public static final int TEMPLATE_ZERO_SHUTTER_LAG = 5;
    public static final int TEMPLATE_MANUAL = 6;

    /**
     * Returns the identifier for this camera device.
     */
    public String getId() { return null; }

    /**
     * Create a capture request builder for the given template type.
     * Stub: returns null.
     */
    public Object createCaptureRequest(int templateType) {
        return null;
    }

    /**
     * Create a capture session.
     * Stub: no-op.
     */
    public void createCaptureSession(java.util.List<Object> outputs, Object stateCallback, Object handler) {
        // no-op
    }

    /**
     * Close the camera device.
     */
    public void close() {}

    /**
     * Object for receiving updates about the state of a camera device.
     */
    public static abstract class StateCallback {

        public static final int ERROR_CAMERA_IN_USE = 1;
        public static final int ERROR_MAX_CAMERAS_IN_USE = 2;
        public static final int ERROR_CAMERA_DISABLED = 3;
        public static final int ERROR_CAMERA_DEVICE = 4;
        public static final int ERROR_CAMERA_SERVICE = 5;

        public void onOpened(CameraDevice camera) {}

        public void onDisconnected(CameraDevice camera) {}

        public void onError(CameraDevice camera, int error) {}
    }
}
