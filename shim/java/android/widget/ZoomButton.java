package android.widget;

/**
 * Shim: android.widget.ZoomButton
 *
 * An ImageButton that fires repeated click events while held down, used as
 * a zoom-in or zoom-out control inside {@link ZoomControls}.
 *
 * OH mapping: ARKUI_NODE_IMAGE (via ImageButton → ImageView). The long-press
 * repeat behaviour is not yet wired to an OH timer.
 */
public class ZoomButton extends ImageButton {

    /** Delay between repeated clicks while the button is held, in ms. */
    private long mZoomSpeed = 1000L;

    public ZoomButton() {
        super();
    }

    public ZoomButton(Object context) {
        super(context);
    }

    public ZoomButton(Object context, Object attrs) {
        super(context, attrs);
    }

    public ZoomButton(Object context, Object attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * Sets the interval between repeated click events when the button is
     * held down.
     *
     * @param speed  repeat interval in milliseconds (default 1000 ms)
     */
    public void setZoomSpeed(long speed) {
        this.mZoomSpeed = speed;
    }

    /** @return the current repeat interval in milliseconds. */
    public long getZoomSpeed() {
        return mZoomSpeed;
    }

    /**
     * Enables or disables the button.  When disabled the button does not
     * deliver click events.  No-op in the shim beyond storing the state.
     */
    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
    }
}
