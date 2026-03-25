package androidx.savedstate;

import android.view.View;

public class ViewTreeSavedStateRegistryOwner {

    public static void set(View view, SavedStateRegistryOwner owner) {
        view.setTag(owner);
    }

    public static SavedStateRegistryOwner get(View view) {
        Object tag = view.getTag();
        if (tag instanceof SavedStateRegistryOwner) return (SavedStateRegistryOwner) tag;
        View parent = view;
        while (parent.getParent() instanceof View) {
            parent = (View) parent.getParent();
            tag = parent.getTag();
            if (tag instanceof SavedStateRegistryOwner) return (SavedStateRegistryOwner) tag;
        }
        return null;
    }
}
