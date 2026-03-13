package android.widget;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Drawable;

/**
 * Shim: android.widget.CheckedTextView → ArkUI text node with check state
 *
 * A TextView that also implements the Checkable interface.
 * Used in ListView with choiceMode to display checked/unchecked items.
 */
public class CheckedTextView extends TextView implements Checkable {

    private boolean checked;
    private int checkMarkResource;

    public CheckedTextView() {
        super();
    }

    // ── Checkable implementation ──

    @Override
    public void setChecked(boolean checked) {
        if (this.checked != checked) {
            this.checked = checked;
            refreshCheckedState();
        }
    }

    @Override
    public boolean isChecked() {
        return checked;
    }

    @Override
    public void toggle() {
        setChecked(!checked);
    }

    // ── Check-mark drawable ──

    /**
     * Sets the check-mark drawable by resource ID.
     *
     * @param resId drawable resource identifier, or 0 to remove
     */
    public void setCheckMarkDrawable(int resId) {
        this.checkMarkResource = resId;
        // Actual drawable lookup not yet wired to ArkUI
    }

    /**
     * Sets the check-mark drawable directly.
     * Accepts Object to avoid hard dependency on android.graphics.drawable.Drawable.
     *
     * @param drawable the drawable to use, or null to remove
     */
    public void setCheckMarkDrawable(Object drawable) {
        // Stub — will forward to ArkUI image attribute when implemented
    }

    /**
     * Returns the resource ID currently set for the check-mark, or 0 if none.
     */
    public int getCheckMarkDrawableResId() {
        return checkMarkResource;
    }

    // ── Drawable-state integration ──

    private static final int[] CHECKED_STATE_SET = { /* android.R.attr.state_checked */ };

    /**
     * Stub for refreshing drawable state after a check change.
     */
    private void refreshCheckedState() {
        // Will push checked state to ArkUI when wired up
    }
}
