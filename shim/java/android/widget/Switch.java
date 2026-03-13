package android.widget;
import android.view.View;
import android.view.View;

import android.view.View;
import com.ohos.shim.bridge.OHBridge;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Shim: android.widget.Switch → ARKUI_NODE_TOGGLE (switch type)
 */
public class Switch extends TextView {
    static final int NODE_TYPE_TOGGLE = 5;
    static final int ATTR_TOGGLE_STATE = 5001;
    static final int EVENT_TOGGLE_ON_CHANGE = 5000;

    private boolean checked = false;
    private OnCheckedChangeListener onCheckedChangeListener;
    private int toggleEventId;
    private static final AtomicInteger sNextEventId = new AtomicInteger(30000);

    public Switch() {
        super(NODE_TYPE_TOGGLE);
        registerToggleEvent();
    }

    private void registerToggleEvent() {
        if (nativeHandle != 0) {
            toggleEventId = sNextEventId.getAndIncrement();
            OHBridge.nodeRegisterEvent(nativeHandle, EVENT_TOGGLE_ON_CHANGE, toggleEventId);
        }
    }

    public boolean isChecked() { return checked; }

    public void setChecked(boolean checked) {
        this.checked = checked;
        if (nativeHandle != 0) {
            OHBridge.nodeSetAttrInt(nativeHandle, ATTR_TOGGLE_STATE, checked ? 1 : 0);
        }
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
        this.onCheckedChangeListener = listener;
    }

    
    public void onNativeEvent(int eventId, int eventKind, String stringData) {
        if (eventKind == EVENT_TOGGLE_ON_CHANGE) {
            checked = !checked;
            if (onCheckedChangeListener != null) {
                onCheckedChangeListener.onCheckedChanged(this, checked);
            }
        }
        super.onNativeEvent(eventId, eventKind, stringData);
    }

    public interface OnCheckedChangeListener {
        void onCheckedChanged(Switch buttonView, boolean isChecked);
    }
}
