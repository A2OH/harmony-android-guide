package android.view;

import com.ohos.shim.bridge.OHBridge;
import java.util.ArrayList;
import java.util.List;

/**
 * Shim: android.view.ViewGroup → ArkUI container node.
 *
 * Default container type is STACK (FrameLayout equivalent).
 * Subclasses override with COLUMN (LinearLayout vertical), ROW (horizontal), etc.
 */
public abstract class ViewGroup extends View {

    private final List<View> children = new ArrayList<>();

    protected ViewGroup() {
        super(NODE_TYPE_STACK);
    }

    protected ViewGroup(int arkuiNodeType) {
        super(arkuiNodeType);
    }

    public void addView(View child) {
        addView(child, -1);
    }

    public void addView(View child, int index) {
        if (child == null) return;
        children.add(index >= 0 ? index : children.size(), child);
        if (nativeHandle != 0 && child.getNativeHandle() != 0) {
            if (index >= 0) {
                OHBridge.nodeInsertChildAt(nativeHandle, child.getNativeHandle(), index);
            } else {
                OHBridge.nodeAddChild(nativeHandle, child.getNativeHandle());
            }
        }
    }

    public void addView(View child, LayoutParams params) {
        child.setLayoutParams(params);
        addView(child);
    }

    public void addView(View child, int index, LayoutParams params) {
        child.setLayoutParams(params);
        addView(child, index);
    }

    public void removeView(View child) {
        if (child == null) return;
        children.remove(child);
        if (nativeHandle != 0 && child.getNativeHandle() != 0) {
            OHBridge.nodeRemoveChild(nativeHandle, child.getNativeHandle());
        }
    }

    public void removeViewAt(int index) {
        if (index >= 0 && index < children.size()) {
            removeView(children.get(index));
        }
    }

    public void removeAllViews() {
        for (int i = children.size() - 1; i >= 0; i--) {
            removeView(children.get(i));
        }
    }

    public int getChildCount() { return children.size(); }

    public View getChildAt(int index) {
        return (index >= 0 && index < children.size()) ? children.get(index) : null;
    }

    public int indexOfChild(View child) {
        return children.indexOf(child);
    }

    /** Find a view by ID in this subtree */
    public View findViewById(int id) {
        if (getId() == id) return this;
        for (View child : children) {
            if (child.getId() == id) return child;
            if (child instanceof ViewGroup) {
                View found = ((ViewGroup) child).findViewById(id);
                if (found != null) return found;
            }
        }
        return null;
    }

    @Override
    public void destroy() {
        for (View child : children) {
            child.destroy();
        }
        children.clear();
        super.destroy();
    }

    // ── LayoutParams ──

    public static class LayoutParams {
        public static final int FILL_PARENT  = -1;
        public static final int MATCH_PARENT = -1;
        public static final int WRAP_CONTENT = -2;

        public int width;
        public int height;

        public LayoutParams(int width, int height) {
            this.width = width;
            this.height = height;
        }

        public LayoutParams(LayoutParams source) {
            this.width = source.width;
            this.height = source.height;
        }
    }

    public static class MarginLayoutParams extends LayoutParams {
        public int leftMargin;
        public int topMargin;
        public int rightMargin;
        public int bottomMargin;

        public MarginLayoutParams(int width, int height) {
            super(width, height);
        }

        public MarginLayoutParams(LayoutParams source) {
            super(source);
        }

        public void setMargins(int left, int top, int right, int bottom) {
            leftMargin = left;
            topMargin = top;
            rightMargin = right;
            bottomMargin = bottom;
        }
    }
}
