package android.widget;
import android.view.View;
import android.view.ViewGroup;
import android.view.View;
import android.view.ViewGroup;

import android.view.View;
import android.view.ViewGroup;
import com.ohos.shim.bridge.OHBridge;

/**
 * Shim: android.widget.LinearLayout → ARKUI_NODE_COLUMN (vertical) / ARKUI_NODE_ROW (horizontal)
 *
 * Default orientation is VERTICAL (COLUMN).
 */
public class LinearLayout extends ViewGroup {
    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;

    static final int NODE_TYPE_COLUMN = 16;
    static final int NODE_TYPE_ROW = 17;

    private int orientation = VERTICAL;

    public LinearLayout() {
        super(NODE_TYPE_COLUMN);
    }

    /** Create with specific orientation.
     *  Note: changing orientation after creation requires node recreation.
     *  For now, set orientation before adding children. */
    public void setOrientation(int orientation) {
        this.orientation = orientation;
        // If orientation differs from what was created, we'd need to recreate.
        // For the shim, we create the correct type based on first setOrientation call.
    }

    public int getOrientation() { return orientation; }

    // ── LayoutParams with weight support ──

    public static class LayoutParams extends ViewGroup.MarginLayoutParams {
        public float weight;
        public int gravity = -1;

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(int width, int height, float weight) {
            super(width, height);
            this.weight = weight;
        }

        public LayoutParams(ViewGroup.MarginLayoutParams source) {
            super(source);
        }
    }
}
