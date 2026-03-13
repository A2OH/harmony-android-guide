package android.view;

/**
 * Shim: android.view.ViewGroupOverlay — overlay layer for a ViewGroup.
 *
 * Extends {@link ViewOverlay} to additionally allow adding/removing child
 * {@link View} instances into the overlay layer. All operations are no-ops in
 * this shim implementation.
 */
public class ViewGroupOverlay extends ViewOverlay {

    /**
     * Add a View to the overlay. The view will be drawn on top of the
     * ViewGroup's content but is not actually added to the view hierarchy.
     *
     * @param view the View to add to the overlay (ignored in this shim)
     */
    public void add(View view) {
        // no-op
    }

    /**
     * Remove a View that was previously added to the overlay.
     *
     * @param view the View to remove from the overlay (ignored in this shim)
     */
    public void remove(View view) {
        // no-op
    }
}
