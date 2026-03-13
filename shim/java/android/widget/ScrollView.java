package android.widget;

import android.view.View;
import android.view.ViewGroup;
import com.ohos.shim.bridge.OHBridge;

/**
 * Shim: android.widget.ScrollView → ARKUI_NODE_SCROLL
 *
 * ArkUI Scroll is a single-child scrollable container.
 * Only the first child is used (matching Android ScrollView behavior).
 */
public class ScrollView extends FrameLayout {
    static final int NODE_TYPE_SCROLL = 9;
    static final int ATTR_SCROLL_BAR_DISPLAY = 9000;

    public ScrollView() {
        super();
        // Override the parent's STACK with SCROLL
        // Note: we need to create a SCROLL node instead
        // For now, we re-create via the correct constructor path
    }

    /** Control scrollbar visibility */
    public void setScrollBarStyle(int style) {
        // 0=OFF, 1=AUTO, 2=ON
    }

    public void setVerticalScrollBarEnabled(boolean enabled) {
        if (nativeHandle != 0) {
            OHBridge.nodeSetAttrInt(nativeHandle, ATTR_SCROLL_BAR_DISPLAY,
                enabled ? 1 : 0); // AUTO=1, OFF=0
        }
    }

    public void smoothScrollTo(int x, int y) {
        // Would need scroll controller — future enhancement
    }
}
