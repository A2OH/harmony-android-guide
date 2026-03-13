package android.widget;
import android.view.View;
import android.view.View;

import android.view.View;
import com.ohos.shim.bridge.OHBridge;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Shim: android.widget.CheckBox → ARKUI_NODE_CHECKBOX
 */
public class CheckBox extends Button {
    static final int NODE_TYPE_CHECKBOX = 15;
    static final int ATTR_CHECKBOX_SELECT = 15000;
    static final int ATTR_CHECKBOX_SELECT_COLOR = 15001;
    static final int EVENT_CHECKBOX_ON_CHANGE = 15000;

    private boolean checked = false;
    private OnCheckedChangeListener onCheckedChangeListener;
    private int checkEventId;
    private static final AtomicInteger sNextEventId = new AtomicInteger(20000);

    public CheckBox() {
        super();
        // Override button node type — need to create CHECKBOX node
        // This is a limitation of the single-constructor chain.
        // In practice, the shim factory would create the right type.
    }

    public boolean isChecked() { return checked; }

    public void setChecked(boolean checked) {
        this.checked = checked;
        if (nativeHandle != 0) {
            OHBridge.nodeSetAttrInt(nativeHandle, ATTR_CHECKBOX_SELECT, checked ? 1 : 0);
        }
    }

    public void toggle() {
        setChecked(!checked);
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
        this.onCheckedChangeListener = listener;
        if (nativeHandle != 0 && listener != null && checkEventId == 0) {
            checkEventId = sNextEventId.getAndIncrement();
            OHBridge.nodeRegisterEvent(nativeHandle, EVENT_CHECKBOX_ON_CHANGE, checkEventId);
        }
    }

    
    public void onNativeEvent(int eventId, int eventKind, String stringData) {
        if (eventKind == EVENT_CHECKBOX_ON_CHANGE) {
            // data[0].i32: 1=checked, 0=unchecked
            checked = !checked; // toggle state on event
            if (onCheckedChangeListener != null) {
                onCheckedChangeListener.onCheckedChanged(this, checked);
            }
        }
        super.onNativeEvent(eventId, eventKind, stringData);
    }

    public interface OnCheckedChangeListener {
        void onCheckedChanged(CheckBox buttonView, boolean isChecked);
    }
}
