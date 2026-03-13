package android.app;

import android.os.Bundle;

/**
 * Android-compatible Presentation shim.
 * A Presentation is a special kind of Dialog that is used for presenting
 * content on a secondary display. Extends Dialog (shim).
 *
 * Abstract — subclasses must override onCreate() to set up the content view.
 * Stub implementation — no secondary display support in shim layer.
 */
public abstract class Presentation extends Dialog {

    private final int mDisplayId;

    /**
     * Create a Presentation on the specified display.
     * @param outerContext ignored in shim (Object to avoid dependency chain)
     * @param display      ignored in shim (Object to avoid dependency chain)
     */
    public Presentation(Object outerContext, Object display) {
        super();
        mDisplayId = 0;
    }

    /**
     * Create a Presentation on the specified display with a theme.
     * @param outerContext ignored in shim (Object to avoid dependency chain)
     * @param display      ignored in shim (Object to avoid dependency chain)
     * @param theme        theme resource id (ignored in shim)
     */
    public Presentation(Object outerContext, Object display, int theme) {
        super();
        mDisplayId = 0;
    }

    /**
     * Returns the display this presentation appears on.
     * @return null in shim — no real display object
     */
    public Object getDisplay() {
        return null;
    }

    /**
     * Called when the presentation is created. Subclasses must override
     * this to set up content views.
     */
    protected void onCreate(Bundle savedInstanceState) {
        // subclasses override this
    }

    /**
     * Called when the presentation is displayed on the secondary screen.
     */
    public void onDisplayRemoved() {
        dismiss();
    }

    /**
     * Called when the properties of the display change.
     */
    public void onDisplayChanged() {
        // stub — no-op in shim layer
    }
}
