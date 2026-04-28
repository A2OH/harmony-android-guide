package androidx.lifecycle;

import android.view.View;
import android.view.ViewParent;

public final class ViewTreeLifecycleOwner {
    private static final int KEY = androidx.lifecycle.runtime.R.id.view_tree_lifecycle_owner;

    private ViewTreeLifecycleOwner() {}

    public static void set(View view, LifecycleOwner owner) {
        if (view != null) {
            view.setTag(KEY, owner);
        }
    }

    public static LifecycleOwner get(View view) {
        View current = view;
        while (current != null) {
            Object owner = current.getTag(KEY);
            if (owner instanceof LifecycleOwner) {
                return (LifecycleOwner) owner;
            }
            ViewParent parent = current.getParent();
            current = parent instanceof View ? (View) parent : null;
        }
        return null;
    }
}
