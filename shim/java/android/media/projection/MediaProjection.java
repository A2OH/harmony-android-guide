package android.media.projection;

import android.hardware.display.VirtualDisplay;

/**
 * Android-compatible MediaProjection shim. Stub for projected media surfaces.
 */
public class MediaProjection {

    /**
     * Creates a VirtualDisplay that mirrors the projected content.
     *
     * @param name        display name
     * @param width       width in pixels
     * @param height      height in pixels
     * @param dpi         dots per inch
     * @param flags       display flags
     * @param surface     target Surface (Object used – Surface not shimmed)
     * @param callback    VirtualDisplay.Callback or null
     * @param handler     Handler or null
     */
    public VirtualDisplay createVirtualDisplay(String name, int width, int height,
            int dpi, int flags, Object surface,
            VirtualDisplay.Callback callback, Object handler) {
        return new VirtualDisplay();
    }

    public void stop() {
        // no-op
    }

    public void registerCallback(Callback callback, Object handler) {
        // no-op
    }

    public void unregisterCallback(Callback callback) {
        // no-op
    }

    // -----------------------------------------------------------------------
    // Callback abstract inner class
    // -----------------------------------------------------------------------

    public static abstract class Callback {
        public void onStop() {}
    }
}
