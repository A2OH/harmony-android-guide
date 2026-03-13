package android.widget;

import com.ohos.shim.bridge.OHBridge;

/**
 * Shim: android.widget.Button → ARKUI_NODE_BUTTON
 *
 * ArkUI Button has a label attribute. Text styling from TextView base class
 * applies to the button label.
 */
public class Button extends TextView {
    static final int NODE_TYPE_BUTTON = 13;
    static final int ATTR_BUTTON_LABEL = 13000;

    public Button() {
        super(NODE_TYPE_BUTTON);
    }

    
    public void setText(CharSequence text) {
        super.setText(text);
        // ArkUI Button uses BUTTON_LABEL for its text
        if (nativeHandle != 0 && text != null) {
            OHBridge.nodeSetAttrString(nativeHandle, ATTR_BUTTON_LABEL, text.toString());
        }
    }
}
