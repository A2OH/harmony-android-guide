package android.widget;

import com.ohos.shim.bridge.OHBridge;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Shim: android.widget.ToggleButton → ARKUI_NODE_TOGGLE (switch type).
 *
 * ToggleButton extends Button and adds checked state with separate
 * on/off text labels. The ArkUI TOGGLE node handles the visual switch.
 */
public class ToggleButton extends Button {

    // Reuse the TOGGLE node type from the Switch shim
    static final int NODE_TYPE_TOGGLE = 5;
    static final int ATTR_TOGGLE_STATE = 5001;
    static final int EVENT_TOGGLE_ON_CHANGE = 5000;

    private boolean checked = false;
    private CharSequence textOn = "ON";
    private CharSequence textOff = "OFF";
    private OnCheckedChangeListener onCheckedChangeListener;
    private int toggleEventId;
    private static final AtomicInteger sNextEventId = new AtomicInteger(40000);

    public ToggleButton() {
        // Button creates a BUTTON node; for ToggleButton we ideally want TOGGLE.
        // The shim layer uses the Button node and overlays checked state on top.
        super(new android.content.Context());
        registerToggleEvent();
    }

    /** Constructor accepting a context-like object (ignored in shim). */
    public ToggleButton(Object context) {
        this();
    }

    /** Constructor accepting context and attribute set (both ignored). */
    public ToggleButton(Object context, Object attrs) {
        this();
    }

    /** Constructor accepting context, attribute set and default style (all ignored). */
    public ToggleButton(Object context, Object attrs, int defStyleAttr) {
        this();
    }

    private void registerToggleEvent() {
        if (nativeHandle != 0) {
            toggleEventId = sNextEventId.getAndIncrement();
            OHBridge.nodeRegisterEvent(nativeHandle, EVENT_TOGGLE_ON_CHANGE, toggleEventId);
        }
    }

    // ── Checked state ──

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
        if (nativeHandle != 0) {
            OHBridge.nodeSetAttrInt(nativeHandle, ATTR_TOGGLE_STATE, checked ? 1 : 0);
        }
        // Update the displayed label to match the checked state
        setText(checked ? textOn : textOff);
    }

    public void toggle() {
        setChecked(!checked);
    }

    // ── Text labels ──

    public CharSequence getTextOn() {
        return textOn;
    }

    public void setTextOn(CharSequence textOn) {
        this.textOn = textOn != null ? textOn : "ON";
        if (checked) setText(this.textOn);
    }

    public CharSequence getTextOff() {
        return textOff;
    }

    public void setTextOff(CharSequence textOff) {
        this.textOff = textOff != null ? textOff : "OFF";
        if (!checked) setText(this.textOff);
    }

    // ── Listener ──

    public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
        this.onCheckedChangeListener = listener;
    }

    
    public void onNativeEvent(int eventId, int eventKind, String stringData) {
        if (eventKind == EVENT_TOGGLE_ON_CHANGE) {
            checked = !checked;
            setText(checked ? textOn : textOff);
            if (onCheckedChangeListener != null) {
                onCheckedChangeListener.onCheckedChanged(this, checked);
            }
        }
        super.onNativeEvent(eventId, eventKind, stringData);
    }

    public interface OnCheckedChangeListener {
        void onCheckedChanged(ToggleButton buttonView, boolean isChecked);
    }
}
