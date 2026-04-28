package androidx.savedstate;

import android.view.View;
import android.view.ViewParent;

public final class ViewTreeSavedStateRegistryOwner {
    private static final int KEY =
            androidx.savedstate.R.id.view_tree_saved_state_registry_owner;

    private ViewTreeSavedStateRegistryOwner() {}

    public static void set(View view, SavedStateRegistryOwner owner) {
        if (view != null) {
            view.setTag(KEY, owner);
        }
    }

    public static SavedStateRegistryOwner get(View view) {
        View current = view;
        while (current != null) {
            Object owner = current.getTag(KEY);
            if (owner instanceof SavedStateRegistryOwner) {
                return (SavedStateRegistryOwner) owner;
            }
            ViewParent parent = current.getParent();
            current = parent instanceof View ? (View) parent : null;
        }
        return null;
    }
}
