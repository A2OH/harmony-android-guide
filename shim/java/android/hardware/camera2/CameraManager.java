package android.hardware.camera2;

/**
 * Android-compatible CameraManager stub.
 * Maps to @ohos.multimedia.camera on OpenHarmony.
 *
 * All methods are no-op stubs; actual camera access would be wired
 * through OHBridge when running on OpenHarmony hardware.
 */
public class CameraManager {

    /**
     * Returns the list of currently connected camera devices.
     * Stub always returns an empty array.
     */
    public String[] getCameraIdList() {
        return new String[0];
    }

    /**
     * Returns the characteristics of the requested camera.
     * Stub always returns null.
     */
    public Object getCameraCharacteristics(String cameraId) {
        return null;
    }

    /**
     * Opens a connection to the given camera. No-op stub.
     */
    public void openCamera(String cameraId, Object callback, Object handler) {
        // no-op
    }

    /**
     * Registers a callback to be notified about camera availability. No-op stub.
     */
    public void registerAvailabilityCallback(Object callback, Object handler) {
        // no-op
    }

    /**
     * Removes a previously-registered availability callback. No-op stub.
     */
    public void unregisterAvailabilityCallback(Object callback) {
        // no-op
    }

    // ── Inner class ────────────────────────────────────────────────────────

    /**
     * Object for camera device availability changes.
     */
    public abstract static class AvailabilityCallback {
        public void onCameraAvailable(String cameraId) {}
        public void onCameraUnavailable(String cameraId) {}
    }
}
