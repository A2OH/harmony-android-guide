package android.view;

/**
 * Shim: android.view.ViewOutlineProvider — pure Java stub.
 * Defines the outline (shape) used for shadow casting and clipping.
 */
public abstract class ViewOutlineProvider {

    /**
     * Provider that derives the outline from the view's background drawable.
     */
    public static final ViewOutlineProvider BACKGROUND = new ViewOutlineProvider() {
        @Override
        public void getOutline(View view, Object outline) {
            // no-op stub
        }
    };

    /**
     * Provider that derives the outline from the view's bounds.
     */
    public static final ViewOutlineProvider BOUNDS = new ViewOutlineProvider() {
        @Override
        public void getOutline(View view, Object outline) {
            // no-op stub
        }
    };

    /**
     * Provider that derives the outline from the view's padded bounds.
     */
    public static final ViewOutlineProvider PADDED_BOUNDS = new ViewOutlineProvider() {
        @Override
        public void getOutline(View view, Object outline) {
            // no-op stub
        }
    };

    /**
     * Called to get the provider to populate the Outline.
     * @param view the view building the outline
     * @param outline the Outline to populate (Object to avoid dependency chains)
     */
    public abstract void getOutline(View view, Object outline);
}
