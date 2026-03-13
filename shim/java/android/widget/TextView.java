package android.widget;

import android.view.View;
import com.ohos.shim.bridge.OHBridge;

/**
 * Shim: android.widget.TextView → ARKUI_NODE_TEXT
 *
 * Maps setText/setTextColor/setTextSize to ArkUI text node attributes.
 */
public class TextView extends View {
    // ArkUI text attribute constants (must match oh_ffi::attr)
    static final int ATTR_TEXT_CONTENT = 1000;
    static final int ATTR_FONT_COLOR = 1001;
    static final int ATTR_FONT_SIZE = 1002;
    static final int ATTR_FONT_STYLE = 1003;
    static final int ATTR_FONT_WEIGHT = 1004;
    static final int ATTR_TEXT_LINE_HEIGHT = 1005;
    static final int ATTR_TEXT_MAX_LINES = 1009;
    static final int ATTR_TEXT_ALIGN = 1010;

    // ArkUI node type for Text
    static final int NODE_TYPE_TEXT = 1;

    private CharSequence text = "";
    private int textColor = 0xFF000000; // default black
    private float textSize = 16.0f;     // default 16sp → vp
    private int maxLines = Integer.MAX_VALUE;
    private int gravity = 0;

    public TextView() {
        super(NODE_TYPE_TEXT);
    }

    /** Used by subclasses (Button) that create a different node type */
    protected TextView(int arkuiNodeType) {
        super(arkuiNodeType);
    }

    // ── Text content ──

    public CharSequence getText() { return text; }

    public void setText(CharSequence text) {
        this.text = text != null ? text : "";
        if (nativeHandle != 0) {
            OHBridge.nodeSetAttrString(nativeHandle, ATTR_TEXT_CONTENT, this.text.toString());
        }
    }

    public void setText(int resId) {
        // Resource lookup not yet implemented — store the ID
        setText("@" + resId);
    }

    // ── Text color ──

    public void setTextColor(int color) {
        this.textColor = color;
        if (nativeHandle != 0) {
            OHBridge.nodeSetAttrColor(nativeHandle, ATTR_FONT_COLOR, color);
        }
    }

    public int getCurrentTextColor() { return textColor; }

    // ── Text size ──

    public void setTextSize(float size) {
        this.textSize = size;
        if (nativeHandle != 0) {
            OHBridge.nodeSetAttrFloat(nativeHandle, ATTR_FONT_SIZE, size, 0, 0, 0, 1);
        }
    }

    /** setTextSize with unit — we treat all units as vp for now */
    public void setTextSize(int unit, float size) {
        setTextSize(size);
    }

    public float getTextSize() { return textSize; }

    // ── Max lines ──

    public void setMaxLines(int maxLines) {
        this.maxLines = maxLines;
        if (nativeHandle != 0) {
            OHBridge.nodeSetAttrInt(nativeHandle, ATTR_TEXT_MAX_LINES, maxLines);
        }
    }

    public int getMaxLines() { return maxLines; }

    public void setSingleLine(boolean singleLine) {
        setMaxLines(singleLine ? 1 : Integer.MAX_VALUE);
    }

    // ── Gravity / text alignment ──

    public void setGravity(int gravity) {
        this.gravity = gravity;
        if (nativeHandle != 0) {
            // Map Android Gravity to ArkUI TextAlign
            // CENTER_HORIZONTAL=1, LEFT=3(START), RIGHT=5(END)
            int arkAlign;
            if ((gravity & 0x01) != 0) {       // CENTER_HORIZONTAL
                arkAlign = 1; // ARKUI_TEXT_ALIGN_CENTER
            } else if ((gravity & 0x05) != 0) { // RIGHT/END
                arkAlign = 2; // ARKUI_TEXT_ALIGN_END
            } else {
                arkAlign = 0; // ARKUI_TEXT_ALIGN_START
            }
            OHBridge.nodeSetAttrInt(nativeHandle, ATTR_TEXT_ALIGN, arkAlign);
        }
    }

    // ── Font weight ──

    public void setTypeface(Object typeface, int style) {
        if (nativeHandle == 0) return;
        // style: 0=NORMAL, 1=BOLD, 2=ITALIC, 3=BOLD_ITALIC
        if ((style & 1) != 0) { // BOLD
            OHBridge.nodeSetAttrInt(nativeHandle, ATTR_FONT_WEIGHT, 9); // ARKUI_FONT_WEIGHT_BOLD
        }
        if ((style & 2) != 0) { // ITALIC
            OHBridge.nodeSetAttrInt(nativeHandle, ATTR_FONT_STYLE, 1); // ARKUI_FONT_STYLE_ITALIC
        }
    }

    public void setTypeface(Object typeface) {
        setTypeface(typeface, 0);
    }

    // ── Line height ──

    public void setLineSpacing(float add, float mult) {
        if (nativeHandle != 0 && add > 0) {
            OHBridge.nodeSetAttrFloat(nativeHandle, ATTR_TEXT_LINE_HEIGHT, add, 0, 0, 0, 1);
        }
    }

    // ── Text event handling ──

    @Override
    public void onNativeEvent(int eventId, int eventKind, String stringData) {
        super.onNativeEvent(eventId, eventKind, stringData);
    }
}
