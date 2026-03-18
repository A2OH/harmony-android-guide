package android.widget;

/**
 * Shim: android.widget.CompoundButton
 *
 * Abstract base for two-state toggle widgets (CheckBox, RadioButton, Switch,
 * ToggleButton). Maintains checked state and fires OnCheckedChangeListener
 * when the state changes.
 *
 * Subclasses map to specific ArkUI toggle node styles.
 */
public class CompoundButton extends Button {

    private boolean checked;
    private OnCheckedChangeListener onCheckedChangeListener;

    public CompoundButton() {
        super(new android.content.Context());
    }
    public CompoundButton(android.content.Context context) {
        super(context);
    }
    public CompoundButton(android.content.Context context, android.util.AttributeSet attrs) {
        super(context, attrs);
    }
    public CompoundButton(android.content.Context context, android.util.AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    public CompoundButton(android.content.Context context, android.util.AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    // ── Checked state ──

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        if (this.checked != checked) {
            this.checked = checked;
            if (onCheckedChangeListener != null) {
                onCheckedChangeListener.onCheckedChanged(this, checked);
            }
        }
    }

    public void toggle() {
        setChecked(!checked);
    }

    // ── Listener ──

    public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
        this.onCheckedChangeListener = listener;
    }

    /**
     * Object for checked-state changes on a CompoundButton.
     */
    public interface OnCheckedChangeListener {
        void onCheckedChanged(CompoundButton buttonView, boolean isChecked);
    }
}
