package android.widget;

import android.view.View;

/**
 * Shim: android.widget.ZoomControls
 *
 * A compound widget containing a zoom-in {@link ZoomButton} and a zoom-out
 * {@link ZoomButton} arranged horizontally. On Android this widget is
 * deprecated (API 29) but still widely referenced.
 *
 * OH mapping: ARKUI_NODE_ROW containing two ARKUI_NODE_IMAGE buttons.
 * Click wiring is not yet implemented in the shim.
 */
public class ZoomControls extends LinearLayout {

    private final ZoomButton mZoomIn;
    private final ZoomButton mZoomOut;

    public ZoomControls() {
        super();
        setOrientation(LinearLayout.HORIZONTAL);
        mZoomOut = new ZoomButton();
        mZoomIn  = new ZoomButton();
    }

    public ZoomControls(Object context) {
        this();
    }

    public ZoomControls(Object context, Object attrs) {
        this();
    }

    // ── Listener wiring ───────────────────────────────────────────────────────

    /**
     * Sets the listener that receives callbacks when the zoom-in button is
     * clicked.
     *
     * @param listener  click listener, or null to clear.
     */
    public void setOnZoomInClickListener(View.OnClickListener listener) {
        mZoomIn.setOnClickListener(listener);
    }

    /**
     * Sets the listener that receives callbacks when the zoom-out button is
     * clicked.
     *
     * @param listener  click listener, or null to clear.
     */
    public void setOnZoomOutClickListener(View.OnClickListener listener) {
        mZoomOut.setOnClickListener(listener);
    }

    // ── Visibility helpers ────────────────────────────────────────────────────

    /** Show the controls. No-op in shim beyond state tracking. */
    public void show() {
        setVisibility(View.VISIBLE);
    }

    /** Hide the controls. No-op in shim beyond state tracking. */
    public void hide() {
        setVisibility(View.GONE);
    }

    // ── Enable / disable individual buttons ───────────────────────────────────

    /**
     * Controls whether the zoom-in button is enabled.
     *
     * @param isEnabled  true to enable, false to disable.
     */
    public void setIsZoomInEnabled(boolean isEnabled) {
        mZoomIn.setEnabled(isEnabled);
    }

    /**
     * Controls whether the zoom-out button is enabled.
     *
     * @param isEnabled  true to enable, false to disable.
     */
    public void setIsZoomOutEnabled(boolean isEnabled) {
        mZoomOut.setEnabled(isEnabled);
    }

    // ── Speed ────────────────────────────────────────────────────────────────

    /**
     * Sets the interval between repeated click events for both buttons when
     * held down.
     *
     * @param speed  repeat interval in milliseconds.
     */
    public void setZoomSpeed(long speed) {
        mZoomIn.setZoomSpeed(speed);
        mZoomOut.setZoomSpeed(speed);
    }
}
