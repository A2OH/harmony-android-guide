package android.view;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

/**
 * Android-compatible TextureView shim.
 * OH mapping: XComponent (texture type) in ArkUI.
 *
 * A View that can display a content stream such as Camera or MediaPlayer output
 * via a SurfaceTexture.
 */
public class TextureView extends View {

    // ArkUI node type for XComponent (texture)
    private static final int NODE_TYPE_XCOMPONENT_TEXTURE = 10;

    /**
     * Callback interface for SurfaceTexture lifecycle events.
     */
    public interface SurfaceTextureListener {

        /**
         * Invoked when a SurfaceTexture is ready for use.
         *
         * @param surface  the SurfaceTexture.
         * @param width    the width of the surface.
         * @param height   the height of the surface.
         */
        void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height);

        /**
         * Invoked when the SurfaceTexture's buffers size changed.
         *
         * @param surface  the SurfaceTexture.
         * @param width    the new width.
         * @param height   the new height.
         */
        void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height);

        /**
         * Invoked when the SurfaceTexture is about to be destroyed.
         *
         * @param surface  the SurfaceTexture.
         * @return true to release the SurfaceTexture automatically; false to keep it alive.
         */
        boolean onSurfaceTextureDestroyed(SurfaceTexture surface);

        /**
         * Invoked when the specified SurfaceTexture is updated through
         * {@link SurfaceTexture#updateTexImage()}.
         *
         * @param surface  the SurfaceTexture.
         */
        void onSurfaceTextureUpdated(SurfaceTexture surface);
    }

    private SurfaceTextureListener mListener;
    private SurfaceTexture         mSurfaceTexture;
    private boolean                mAvailable = false;

    public TextureView() {
        super(NODE_TYPE_XCOMPONENT_TEXTURE);
    }

    /**
     * Sets the listener used to receive SurfaceTexture lifecycle events.
     *
     * @param listener  the listener; may be null to clear.
     */
    public void setSurfaceTextureListener(SurfaceTextureListener listener) {
        this.mListener = listener;
    }

    /**
     * Returns the SurfaceTexture used by this TextureView, or null if not yet available.
     */
    public SurfaceTexture getSurfaceTexture() { return mSurfaceTexture; }

    /**
     * Returns true if the SurfaceTexture associated with this view is available
     * for rendering.
     */
    public boolean isAvailable() { return mAvailable; }

    /**
     * Returns the Bitmap the TextureView is rendering to (snapshot), or null
     * if not available.
     * In this shim a 1×1 stub Bitmap is returned when available.
     */
    public Bitmap getBitmap() {
        if (!mAvailable) return null;
        return Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
    }

    /**
     * Returns a Bitmap snapshot of the TextureView, scaled to the given dimensions.
     *
     * @param width   target width.
     * @param height  target height.
     */
    public Bitmap getBitmap(int width, int height) {
        if (!mAvailable || width <= 0 || height <= 0) return null;
        return Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
    }

    /**
     * Locks the TextureView for drawing and returns a Canvas.
     * The caller must call {@link #unlockCanvasAndPost(Canvas)} when done.
     *
     * @return a Canvas for drawing.
     */
    public Canvas lockCanvas() {
        return new Canvas();
    }

    /**
     * Locks a sub-region of the TextureView for drawing.
     *
     * @param dirty  the dirty rectangle; may be null.
     * @return a Canvas for drawing.
     */
    public Canvas lockCanvas(Rect dirty) {
        return new Canvas();
    }

    /**
     * Finishes drawing and posts the Canvas.
     *
     * @param canvas  the Canvas obtained from {@link #lockCanvas()}.
     */
    public void unlockCanvasAndPost(Canvas canvas) {
        // no-op in shim
    }
}
