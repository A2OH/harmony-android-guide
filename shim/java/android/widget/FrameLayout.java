package android.widget;

import android.view.ViewGroup;

/**
 * Shim: android.widget.FrameLayout → ARKUI_NODE_STACK
 *
 * STACK is ArkUI's overlay container — children are stacked on top of each other.
 */
public class FrameLayout extends ViewGroup {
    static final int NODE_TYPE_STACK = 8;

    public FrameLayout() {
        super(NODE_TYPE_STACK);
    }

    public FrameLayout(Object context) {
        this();
    }

    public static class LayoutParams extends ViewGroup.MarginLayoutParams {
        public int gravity = -1;

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(int width, int height, int gravity) {
            super(width, height);
            this.gravity = gravity;
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }
    }
}
