package android.view;

import android.graphics.Canvas;
import android.graphics.Rect;
import java.util.ArrayList;
import java.util.List;

/**
 * Android-compatible SurfaceView shim.
 * OH mapping: XComponent (surface type) in ArkUI.
 *
 * Provides a dedicated drawing surface embedded inside a view hierarchy.
 * The surface can be rendered by a background thread.
 */
public class SurfaceView extends View {

    // ArkUI node type for XComponent (surface)
    private static final int NODE_TYPE_XCOMPONENT = 9;

    private final SurfaceHolderImpl mHolder = new SurfaceHolderImpl();
    private boolean mZOrderOnTop        = false;
    private boolean mZOrderMediaOverlay = false;
    private boolean mSecure             = false;

    public SurfaceView() {
        super(NODE_TYPE_XCOMPONENT);
    }

    /**
     * Returns the SurfaceHolder associated with this SurfaceView,
     * which controls the underlying Surface.
     */
    public SurfaceHolder getHolder() { return mHolder; }

    /**
     * Places the SurfaceView on top of its window, above all other content
     * (including IME windows).
     *
     * @param onTop  true to draw on top; false to restore normal ordering.
     */
    public void setZOrderOnTop(boolean onTop) {
        mZOrderOnTop = onTop;
    }

    /**
     * Places the SurfaceView above regular surfaces but below media overlays.
     *
     * @param isMediaOverlay  true to place above normal surfaces.
     */
    public void setZOrderMediaOverlay(boolean isMediaOverlay) {
        mZOrderMediaOverlay = isMediaOverlay;
    }

    /**
     * Controls whether the surface content is hidden from screen-capture tools.
     *
     * @param isSecure  true to prevent capture of the surface content.
     */
    public void setSecure(boolean isSecure) {
        mSecure = isSecure;
    }

    // ── SurfaceHolder implementation ──────────────────────────────────────────

    private final class SurfaceHolderImpl implements SurfaceHolder {

        private final List<SurfaceHolder.Callback> callbacks = new ArrayList<>();
        private final Surface surface = new Surface();
        private int format = 0; // PixelFormat.UNKNOWN

        @Override
        public void addCallback(SurfaceHolder.Callback callback) {
            if (callback != null && !callbacks.contains(callback)) {
                callbacks.add(callback);
            }
        }

        @Override
        public void removeCallback(SurfaceHolder.Callback callback) {
            callbacks.remove(callback);
        }

        @Override
        public Surface getSurface() { return surface; }

        @Override
        public void setFormat(int format) { this.format = format; }

        @Override
        public void setFixedSize(int width, int height) {
            // no-op in shim — layout handled by ArkUI
        }

        @Override
        public void setSizeFromLayout() {
            // no-op in shim
        }

        @Override
        public Canvas lockCanvas() { return surface.lockCanvas(null); }

        @Override
        public Canvas lockCanvas(Rect dirty) { return surface.lockCanvas(dirty); }

        @Override
        public void unlockCanvasAndPost(Canvas canvas) {
            surface.unlockCanvasAndPost(canvas);
        }
    }
}
