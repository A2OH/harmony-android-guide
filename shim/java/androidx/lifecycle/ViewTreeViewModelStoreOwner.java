package androidx.lifecycle;

import android.view.View;
import android.view.ViewParent;

public final class ViewTreeViewModelStoreOwner {
    private static final int KEY =
            androidx.lifecycle.viewmodel.R.id.view_tree_view_model_store_owner;

    private ViewTreeViewModelStoreOwner() {}

    public static void set(View view, ViewModelStoreOwner owner) {
        if (view != null) {
            view.setTag(KEY, owner);
        }
    }

    public static ViewModelStoreOwner get(View view) {
        View current = view;
        while (current != null) {
            Object owner = current.getTag(KEY);
            if (owner instanceof ViewModelStoreOwner) {
                return (ViewModelStoreOwner) owner;
            }
            ViewParent parent = current.getParent();
            current = parent instanceof View ? (View) parent : null;
        }
        return null;
    }
}
