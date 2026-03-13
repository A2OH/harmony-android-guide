package android.hardware.camera2;

/**
 * Shim stub for android.hardware.camera2.CameraDevice.
 * Abstract base; concrete subclasses (or mocks) supply the real implementation.
 */
public abstract class CameraDevice {

    // -----------------------------------------------------------------------
    // Error codes (match AOSP values)
    // -----------------------------------------------------------------------

    /** The camera device is in use already. */
    public static final int ERROR_CAMERA_IN_USE      = 1;
    /** The system-wide limit on concurrent open cameras has been reached. */
    public static final int ERROR_MAX_CAMERAS_IN_USE = 2;
    /** The camera is disabled due to a device policy. */
    public static final int ERROR_CAMERA_DISABLED    = 3;
    /** The camera device has encountered a fatal error. */
    public static final int ERROR_CAMERA_DEVICE      = 4;
    /** The camera service has encountered a fatal error. */
    public static final int ERROR_CAMERA_SERVICE     = 5;

    // -----------------------------------------------------------------------
    // Abstract API
    // -----------------------------------------------------------------------

    /** Returns the unique string identifier for this camera device. */
    public abstract String getId();

    /**
     * Close the connection to this camera device as quickly as possible.
     * Any in-progress captures are abandoned; no further callbacks will fire
     * after this returns.
     */
    public abstract void close();

    // -----------------------------------------------------------------------
    // StateCallback
    // -----------------------------------------------------------------------

    /**
     * Callback interface for receiving notifications about camera device state.
     */
    public static abstract class StateCallback {

        /**
         * Called when the camera device has finished opening.
         *
         * @param camera the camera device that has become opened
         */
        public abstract void onOpened(CameraDevice camera);

        /**
         * Called when the camera device is no longer available for use.
         *
         * @param camera the camera device that has been disconnected
         */
        public abstract void onDisconnected(CameraDevice camera);

        /**
         * Called when the camera device has encountered a serious error.
         *
         * @param camera the camera device that encountered the error
         * @param error  one of the {@code ERROR_*} constants defined above
         */
        public abstract void onError(CameraDevice camera, int error);
    }
}
