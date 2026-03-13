package android.widget;

import android.view.View;
import com.ohos.shim.bridge.OHBridge;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Shim: android.widget.EditText → ARKUI_NODE_TEXT_INPUT (single line)
 *                                   or ARKUI_NODE_TEXT_AREA (multi line)
 *
 * Default is single-line (TEXT_INPUT). Call setInputType or setSingleLine(false)
 * to switch to multi-line.
 */
public class EditText extends TextView {
    static final int NODE_TYPE_TEXT_INPUT = 7;
    static final int NODE_TYPE_TEXT_AREA = 12;

    // TextInput-specific attrs
    static final int ATTR_TEXT_INPUT_PLACEHOLDER = 7000;
    static final int ATTR_TEXT_INPUT_TEXT = 7001;
    static final int ATTR_TEXT_INPUT_TYPE = 7005;

    // TextArea-specific attrs
    static final int ATTR_TEXT_AREA_PLACEHOLDER = 12000;
    static final int ATTR_TEXT_AREA_TEXT = 12001;

    // Event types
    static final int EVENT_TEXT_INPUT_ON_CHANGE = 7000;
    static final int EVENT_TEXT_AREA_ON_CHANGE = 12000;

    private boolean multiLine = false;
    private String hint = "";
    private TextWatcher textWatcher;
    private int textChangeEventId;
    private static final AtomicInteger sNextEventId = new AtomicInteger(10000);

    public EditText() {
        super(NODE_TYPE_TEXT_INPUT);
        registerTextChangeEvent();
    }

    private void registerTextChangeEvent() {
        if (nativeHandle != 0) {
            textChangeEventId = sNextEventId.getAndIncrement();
            int eventType = multiLine ? EVENT_TEXT_AREA_ON_CHANGE : EVENT_TEXT_INPUT_ON_CHANGE;
            OHBridge.nodeRegisterEvent(nativeHandle, eventType, textChangeEventId);
        }
    }

    // ── Text ──

    @Override
    public void setText(CharSequence text) {
        super.setText(text);
        if (nativeHandle != 0 && text != null) {
            int attr = multiLine ? ATTR_TEXT_AREA_TEXT : ATTR_TEXT_INPUT_TEXT;
            OHBridge.nodeSetAttrString(nativeHandle, attr, text.toString());
        }
    }

    // ── Hint ──

    public void setHint(CharSequence hint) {
        this.hint = hint != null ? hint.toString() : "";
        if (nativeHandle != 0) {
            int attr = multiLine ? ATTR_TEXT_AREA_PLACEHOLDER : ATTR_TEXT_INPUT_PLACEHOLDER;
            OHBridge.nodeSetAttrString(nativeHandle, attr, this.hint);
        }
    }

    public void setHint(int resId) {
        setHint("@" + resId);
    }

    public CharSequence getHint() { return hint; }

    // ── Input type ──

    /** Android input types → ArkUI text input types */
    public void setInputType(int type) {
        if (nativeHandle == 0) return;
        // TYPE_CLASS_TEXT=1, TYPE_CLASS_NUMBER=2, TYPE_CLASS_PHONE=3
        // TYPE_TEXT_VARIATION_PASSWORD=0x80, TYPE_NUMBER_VARIATION_PASSWORD=0x10
        int arkType;
        if ((type & 0x80) != 0 || (type & 0x10) != 0) {
            arkType = 7; // ARKUI_TEXTINPUT_TYPE_PASSWORD
        } else if ((type & 0x02) != 0) {
            arkType = 2; // ARKUI_TEXTINPUT_TYPE_NUMBER
        } else if ((type & 0x03) != 0) {
            arkType = 3; // ARKUI_TEXTINPUT_TYPE_PHONE_NUMBER
        } else {
            arkType = 0; // ARKUI_TEXTINPUT_TYPE_NORMAL
        }
        OHBridge.nodeSetAttrInt(nativeHandle, ATTR_TEXT_INPUT_TYPE, arkType);
    }

    public void setSingleLine(boolean singleLine) {
        // Note: switching between TEXT_INPUT and TEXT_AREA would require
        // recreating the node. For now, we set this at construction time.
        this.multiLine = !singleLine;
    }

    // ── TextWatcher ──

    public void addTextChangedListener(TextWatcher watcher) {
        this.textWatcher = watcher;
    }

    public void removeTextChangedListener(TextWatcher watcher) {
        if (this.textWatcher == watcher) {
            this.textWatcher = null;
        }
    }

    @Override
    public void onNativeEvent(int eventId, int eventKind, String stringData) {
        if ((eventKind == EVENT_TEXT_INPUT_ON_CHANGE || eventKind == EVENT_TEXT_AREA_ON_CHANGE)
                && stringData != null && textWatcher != null) {
            textWatcher.onTextChanged(stringData, 0, 0, stringData.length());
        }
        super.onNativeEvent(eventId, eventKind, stringData);
    }

    // ── TextWatcher interface ──

    public interface TextWatcher {
        default void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        void onTextChanged(CharSequence s, int start, int before, int count);
        default void afterTextChanged(Object editable) {}
    }
}
