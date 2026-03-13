package android.hardware.camera2;

/**
 * Android-compatible CameraCaptureSession stub.
 * Maps to @ohos.multimedia.camera capture session on OpenHarmony.
 *
 * Abstract base class for camera capture sessions.  Concrete instances
 * would be created by CameraDevice.createCaptureSession().
 */
public class CameraCaptureSession {

    /**
     * Get the camera device that this session is created for.
     */
    public CameraDevice getDevice() {
        return null;
    }

    /**
     * Submit a request for an image to be captured.
     *
     * @param request  the capture request
     * @param listener the callback (may be null)
     * @param handler  the handler for listener callbacks (may be null)
     * @return the unique sequence ID for this capture
     */
    public int capture(Object request, Object listener, Object handler) {
        return 0;
    }

    /**
     * Object endlessly repeating capture of images.
     *
     * @param request  the capture request to repeat
     * @param listener the callback (may be null)
     * @param handler  the handler for listener callbacks (may be null)
     * @return the unique sequence ID for this repeating request
     */
    public int setRepeatingRequest(Object request, Object listener, Object handler) {
        return 0;
    }

    /**
     * Cancel any ongoing repeating capture request.
     */
    public void stopRepeating() {}

    /**
     * Discard all captures currently pending and in-progress.
     */
    public void abortCaptures() {}

    /**
     * Close this capture session.
     */
    public void close() {}

    // ── Inner class: StateCallback ──────────────────────────────────────────

    /**
     * Object object for receiving updates about the state of a camera
     * capture session.
     */
    public static abstract class StateCallback {

        /**
         * Called when the camera device has finished configuring itself
         * and the session can start processing capture requests.
         */
        public void onConfigured(CameraCaptureSession session) {}

        /**
         * Called if the session cannot be configured as requested.
         */
        public void onConfigureFailed(CameraCaptureSession session) {}

        /**
         * Called every time the session has no more capture requests to process.
         */
        public void onReady(CameraCaptureSession session) {
        }

        /**
         * Called when the session starts actively processing capture requests.
         */
        public void onActive(CameraCaptureSession session) {
        }

        /**
         * Called when the session is closed.
         */
        public void onClosed(CameraCaptureSession session) {
        }
    }

    // ── Inner class: CaptureCallback ────────────────────────────────────────

    /**
     * Object object for tracking the progress of a capture request
     * submitted to the camera device.
     */
    public static abstract class CaptureCallback {

        /**
         * Called when an image capture has started.
         */
        public void onCaptureStarted(CameraCaptureSession session,
                Object request, long timestamp, long frameNumber) {
        }

        /**
         * Called when an image capture makes partial forward progress.
         */
        public void onCaptureProgressed(CameraCaptureSession session,
                Object request, Object partialResult) {
        }

        /**
         * Called when an image capture has fully completed.
         */
        public void onCaptureCompleted(CameraCaptureSession session,
                Object request, Object result) {
        }

        /**
         * Called when the camera device failed to produce a result for the request.
         */
        public void onCaptureFailed(CameraCaptureSession session,
                Object request, Object failure) {
        }
    }
}
