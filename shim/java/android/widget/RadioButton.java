package android.widget;

import android.view.View;
import com.ohos.shim.bridge.OHBridge;

/**
 * Shim: android.widget.RadioButton → ArkUI Radio node concept.
 *
 * ArkUI provides a Radio component; we map checked state through a generic
 * toggle attribute on a STACK node (the bridge can specialise the node type).
 */
public class RadioButton extends View {

    // ArkUI attribute for checked state (approximate)
    private static final int ATTR_CHECKED = 1100;

    private boolean checked = false;
    private OnCheckedChangeListener onCheckedChangeListener;

    public RadioButton() {
        super(View.NODE_TYPE_STACK); // Bridge maps to Radio component
    }

    // ── Checked state ──

    public boolean isChecked() { return checked; }

    public void setChecked(boolean checked) {
        if (this.checked != checked) {
            this.checked = checked;
            if (nativeHandle != 0) {
                OHBridge.nodeSetAttrInt(nativeHandle, ATTR_CHECKED, checked ? 1 : 0);
            }
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

    // ── Interface ──

    public interface OnCheckedChangeListener {
        void onCheckedChanged(RadioButton button, boolean isChecked);
    }
}
