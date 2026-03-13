package android.view;

import android.graphics.Canvas;
import android.graphics.Rect;

/**
 * Android-compatible SurfaceHolder shim.
 * OH mapping: OH_NativeWindow lifecycle callbacks
 *
 * Interface used to control and monitor a Surface owned by a SurfaceView.
 */
public interface SurfaceHolder {

    /**
     * Lifecycle callbacks for a Surface managed by this holder.
     */
    interface Callback {
        /**
         * Called immediately after the surface is first created.
         *
         * @param holder  the SurfaceHolder whose surface was created.
         */
        void surfaceCreated(SurfaceHolder holder);

        /**
         * Called after any structural changes (format or size) to the surface.
         *
         * @param holder  the SurfaceHolder whose surface changed.
         * @param format  the new pixel format of the surface.
         * @param width   the new width of the surface.
         * @param height  the new height of the surface.
         */
        void surfaceChanged(SurfaceHolder holder, int format, int width, int height);

        /**
         * Called immediately before the surface is destroyed.
         *
         * @param holder  the SurfaceHolder whose surface is being destroyed.
         */
        void surfaceDestroyed(SurfaceHolder holder);
    }

    // ── Callback registration ──

    /** Registers a Callback to receive surface lifecycle events. */
    void addCallback(Callback callback);

    /** Unregisters a previously added Callback. */
    void removeCallback(Callback callback);

    // ── Surface access ──

    /** Returns the Surface associated with this holder. Never null. */
    Surface getSurface();

    // ── Format and size configuration ──

    /**
     * Sets the pixel format of the surface.
     *
     * @param format  a constant from {@link android.graphics.PixelFormat}.
     */
    void setFormat(int format);

    /**
     * Makes the surface a fixed size, ignoring the view's layout size.
     *
     * @param width   desired width in pixels.
     * @param height  desired height in pixels.
     */
    void setFixedSize(int width, int height);

    /**
     * Allows the surface to resize with its containing view (default behaviour).
     */
    void setSizeFromLayout();

    // ── Canvas drawing ──

    /**
     * Locks the surface for CPU drawing.
     *
     * @return a Canvas on which to draw.
     */
    Canvas lockCanvas();

    /**
     * Locks the surface for CPU drawing, restricting drawing to {@code dirty}.
     *
     * @param dirty  dirty rectangle; may be null for the whole surface.
     * @return a Canvas on which to draw.
     */
    Canvas lockCanvas(Rect dirty);

    /**
     * Posts the Canvas and unlocks the surface.
     *
     * @param canvas  the Canvas obtained from {@link #lockCanvas()}.
     */
    void unlockCanvasAndPost(Canvas canvas);
}
