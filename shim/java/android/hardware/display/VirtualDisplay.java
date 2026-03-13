package android.hardware.display;

/**
 * Android-compatible VirtualDisplay shim. Stub for virtual display management.
 */
public class VirtualDisplay {

    public VirtualDisplay() {}

    /** Returns a stub Display object (Display not yet shimmed). */
    public Object getDisplay() {
        return null;
    }

    /** Returns the Surface backing this virtual display (Surface not yet shimmed). */
    public Object getSurface() {
        return null;
    }

    public void setSurface(Object surface) {
        // no-op
    }

    public void resize(int width, int height, int densityDpi) {
        // no-op
    }

    public void release() {
        // no-op
    }

    // -----------------------------------------------------------------------
    // Callback abstract inner class
    // -----------------------------------------------------------------------

    public static abstract class Callback {
        public void onPaused() {}
        public void onResumed() {}
        public void onStopped() {}
    }
}
