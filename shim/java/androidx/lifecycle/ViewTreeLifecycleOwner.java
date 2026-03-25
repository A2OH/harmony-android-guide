package androidx.lifecycle;

import android.view.View;

/**
 * Sets/gets the LifecycleOwner for a View tree.
 * Compose uses this to find the LifecycleOwner from AndroidComposeView.
 */
public class ViewTreeLifecycleOwner {

    private static final int TAG_KEY = 0x7f0a0001; // arbitrary tag key

    public static void set(View view, LifecycleOwner owner) {
        view.setTag(owner);
    }

    public static LifecycleOwner get(View view) {
        Object tag = view.getTag();
        if (tag instanceof LifecycleOwner) return (LifecycleOwner) tag;
        // Walk up parent chain
        View parent = view;
        while (parent.getParent() instanceof View) {
            parent = (View) parent.getParent();
            tag = parent.getTag();
            if (tag instanceof LifecycleOwner) return (LifecycleOwner) tag;
        }
        return null;
    }
}
