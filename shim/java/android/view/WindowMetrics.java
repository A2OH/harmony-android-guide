package android.view;
import android.graphics.Rect;
import android.graphics.Rect;

import android.graphics.Rect;

/**
 * Shim: android.view.WindowMetrics (API 30+)
 * Reports the current metrics of a window, including bounds and insets.
 */
public class WindowMetrics {

    private final Rect bounds;
    private final Object windowInsets;

    // ── Constructor ─────────────────────────────────────────────────────────

    public WindowMetrics(Rect bounds, Object windowInsets) {
        this.bounds = bounds;
        this.windowInsets = windowInsets;
    }

    // ── Accessors ───────────────────────────────────────────────────────────

    /** Returns the bounds of the window. */
    public Rect getBounds() {
        return bounds;
    }

    /**
     * Returns the window insets.
     * Stub: always returns null (typed as Object to avoid pulling in
     * the full WindowInsets dependency chain).
     */
    public Object getWindowInsets() {
        return null;
    }
}
