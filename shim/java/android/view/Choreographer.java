package android.view;

/**
 * Shim: android.view.Choreographer — pure Java stub.
 * Coordinates the timing of animations, input, and drawing.
 */
public class Choreographer {

    private static final Choreographer sInstance = new Choreographer();

    /** Private constructor — use getInstance(). */
    private Choreographer() {}

    /** Return the singleton Choreographer instance for the current thread. */
    public static Choreographer getInstance() {
        return sInstance;
    }

    /**
     * Post a frame callback to run on the next frame.
     * The callback receives the frame time in nanoseconds.
     */
    public void postFrameCallback(FrameCallback callback) {
        // no-op stub
    }

    /**
     * Post a frame callback to run on the next frame after the specified delay.
     */
    public void postFrameCallbackDelayed(FrameCallback callback, long delayMillis) {
        // no-op stub
    }

    /** Remove a previously posted frame callback. */
    public void removeFrameCallback(FrameCallback callback) {
        // no-op stub
    }

    /**
     * Object invoked on each display frame.
     */
    public interface FrameCallback {
        /**
         * Called when a new display frame is being rendered.
         * @param frameTimeNanos the frame time in nanoseconds
         */
        void doFrame(long frameTimeNanos);
    }
}
