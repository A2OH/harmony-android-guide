package android.hardware.camera2;

import android.os.Handler;

/**
 * Shim stub for android.hardware.camera2.CameraManager.
 *
 * <p>All methods are stubs.  {@link #getCameraIdList()} returns a fixed two-camera
 * list so that callers can enumerate cameras without a real device.  All other
 * methods are no-ops or return placeholder objects.
 */
public class CameraManager {

    // -----------------------------------------------------------------------
    // Camera enumeration
    // -----------------------------------------------------------------------

    /**
     * Returns the list of currently connected camera devices.
     *
     * <p>Stub: always returns {@code {"0", "1"}} representing a rear and a
     * front camera.
     *
     * @return non-null array of camera id strings
     */
    public String[] getCameraIdList() {
        return new String[]{"0", "1"};
    }

    // -----------------------------------------------------------------------
    // Camera open
    // -----------------------------------------------------------------------

    /**
     * Open a connection to a camera with the given id.
     *
     * <p>Stub: the callback's {@link CameraDevice.StateCallback#onOpened} is
     * <em>not</em> invoked here; real routing is left to OHBridge integration.
     *
     * @param cameraId  the identifier of the camera to open
     * @param callback  the callback invoked when the camera is opened (or fails)
     * @param handler   the handler on which the callback should be invoked, or
     *                  {@code null} to use the current thread's looper
     */
    public void openCamera(String cameraId,
                           CameraDevice.StateCallback callback,
                           Handler handler) {
        // Stub: no-op.  A real implementation would post an async open request
        // to the camera service and eventually call callback.onOpened().
    }

    // -----------------------------------------------------------------------
    // Camera characteristics
    // -----------------------------------------------------------------------

    /**
     * Return the capabilities of a given camera device.
     *
     * <p>Stub: returns an empty (all-null) {@link CameraCharacteristics} object.
     *
     * @param cameraId the identifier of the camera device to query
     * @return a stub {@link CameraCharacteristics} instance
     */
    public CameraCharacteristics getCameraCharacteristics(String cameraId) {
        return new CameraCharacteristics();
    }

    // -----------------------------------------------------------------------
    // AvailabilityCallback
    // -----------------------------------------------------------------------

    /**
     * Callback interface for being notified about changes to camera availability.
     */
    public static abstract class AvailabilityCallback {

        /**
         * Called whenever a camera device becomes available for use.
         *
         * @param cameraId the unique identifier of the newly available camera
         */
        public void onCameraAvailable(String cameraId) {
            // Default empty implementation – subclasses override as needed.
        }

        /**
         * Called whenever a camera device becomes unavailable (e.g. opened by
         * another application).
         *
         * @param cameraId the unique identifier of the newly unavailable camera
         */
        public void onCameraUnavailable(String cameraId) {
            // Default empty implementation – subclasses override as needed.
        }
    }
}
