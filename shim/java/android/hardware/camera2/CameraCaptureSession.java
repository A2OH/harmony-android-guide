package android.hardware.camera2;

/**
 * Android-compatible CameraCaptureSession stub.
 * A configured capture session for a CameraDevice.
 * Methods throw UnsupportedOperationException unless overridden.
 */
public abstract class CameraCaptureSession {

    /**
     * Submit a request for image capture.
     *
     * @param request  the capture request to submit
     * @param listener callback invoked when the capture completes
     * @param handler  handler on which the listener is invoked (Object to avoid dependency)
     * @return a unique sequence ID for this capture, or -1 in the shim
     */
    public int capture(Object request, CaptureCallback listener, Object handler)
            throws Exception {
        throw new UnsupportedOperationException("CameraCaptureSession: not available in shim");
    }

    /**
     * Request endlessly repeating capture of images.
     *
     * @param request  the capture request to repeat
     * @param listener callback invoked for each captured frame
     * @param handler  handler on which the listener is invoked
     * @return a unique sequence ID, or -1 in the shim
     */
    public int setRepeatingRequest(Object request, CaptureCallback listener, Object handler)
            throws Exception {
        throw new UnsupportedOperationException("CameraCaptureSession: not available in shim");
    }

    /**
     * Cancel any ongoing repeating capture request previously set via
     * {@link #setRepeatingRequest}.
     */
    public void stopRepeating() throws Exception {
        // No-op in shim
    }

    /**
     * Close this capture session asynchronously.
     */
    public abstract void close();

    /**
     * Returns the CameraDevice that created this session, or null in the shim.
     */
    public Object getDevice() {
        return null;
    }

    /**
     * Returns true if the session is in an active (not closed) state.
     */
    public boolean isReprocessable() {
        return false;
    }

    // -----------------------------------------------------------------------
    // StateCallback
    // -----------------------------------------------------------------------

    public abstract static class StateCallback {
        /**
         * Called when the camera device has finished configuring itself, and the session can
         * start processing capture requests.
         *
         * @param session the configured CameraCaptureSession
         */
        public abstract void onConfigured(CameraCaptureSession session);

        /**
         * Called if the session cannot be configured as requested.
         *
         * @param session the CameraCaptureSession that failed
         */
        public abstract void onConfigureFailed(CameraCaptureSession session);

        /**
         * Called when the session is closed.
         *
         * @param session the closed CameraCaptureSession
         */
        public void onClosed(CameraCaptureSession session) {}

        /**
         * Called when the session is ready for use (active), after surfaces are prepared.
         *
         * @param session the ready CameraCaptureSession
         */
        public void onReady(CameraCaptureSession session) {}

        /**
         * Called when the session starts actively processing capture requests.
         *
         * @param session the active CameraCaptureSession
         */
        public void onActive(CameraCaptureSession session) {}
    }

    // -----------------------------------------------------------------------
    // CaptureCallback
    // -----------------------------------------------------------------------

    public abstract static class CaptureCallback {
        /**
         * Called when a capture request has been submitted to the camera device.
         *
         * @param session the active CameraCaptureSession
         * @param request the CaptureRequest that was submitted (Object to avoid dependency)
         * @param timestamp the timestamp at the start of capture, in nanoseconds
         * @param frameNumber the frame number for this capture
         */
        public void onCaptureStarted(CameraCaptureSession session, Object request,
                                     long timestamp, long frameNumber) {}

        /**
         * Called when an image capture has completed and the result metadata is available.
         *
         * @param session the active CameraCaptureSession
         * @param request the CaptureRequest submitted
         * @param result  the CaptureResult with result metadata
         */
        public void onCaptureCompleted(CameraCaptureSession session, Object request,
                                       CaptureResult result) {}

        /**
         * Called instead of {@link #onCaptureCompleted} when the camera device failed to
         * produce a CaptureResult for the request.
         *
         * @param session  the active CameraCaptureSession
         * @param request  the failed CaptureRequest
         * @param failure  the CaptureFailure describing the reason for failure (Object in shim)
         */
        public void onCaptureFailed(CameraCaptureSession session, Object request,
                                    Object failure) {}

        /**
         * Called when a capture sequence aborts before any CaptureResult is returned.
         *
         * @param session        the active CameraCaptureSession
         * @param sequenceId     the sequence ID returned by capture/setRepeatingRequest
         */
        public void onCaptureSequenceAborted(CameraCaptureSession session, int sequenceId) {}

        /**
         * Called when a capture sequence completes.
         *
         * @param session        the active CameraCaptureSession
         * @param sequenceId     the sequence ID returned by capture/setRepeatingRequest
         * @param frameNumber    the last frame number of the sequence
         */
        public void onCaptureSequenceCompleted(CameraCaptureSession session, int sequenceId,
                                               long frameNumber) {}
    }
}
