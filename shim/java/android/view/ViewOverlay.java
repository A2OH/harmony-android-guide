package android.view;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Drawable;

/**
 * Shim: android.view.ViewOverlay — an overlay layer for a view.
 *
 * In real Android a ViewOverlay lets you draw Drawables on top of a View
 * without affecting the view hierarchy. This no-op shim accepts all calls
 * and silently discards them.
 */
public class ViewOverlay {

    /**
     * Add a Drawable to the overlay. Accepted as {@code Object} to avoid
     * pulling in android.graphics.drawable.Drawable dependency chains.
     *
     * @param drawable the Drawable to add (ignored in this shim)
     */
    public void add(Object drawable) {
        // no-op
    }

    /**
     * Remove a Drawable from the overlay.
     *
     * @param drawable the Drawable to remove (ignored in this shim)
     */
    public void remove(Object drawable) {
        // no-op
    }

    /**
     * Remove all content from the overlay.
     */
    public void clear() {
        // no-op
    }

    /** Returns the overlay view used for drawing. */
    public View getOverlayView() { return null; }

    /** Returns true if the overlay has no content. */
    public boolean isEmpty() { return true; }
}
