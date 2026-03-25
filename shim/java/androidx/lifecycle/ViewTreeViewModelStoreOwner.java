package androidx.lifecycle;

import android.view.View;

public class ViewTreeViewModelStoreOwner {

    public static void set(View view, ViewModelStoreOwner owner) {
        view.setTag(owner);
    }

    public static ViewModelStoreOwner get(View view) {
        Object tag = view.getTag();
        if (tag instanceof ViewModelStoreOwner) return (ViewModelStoreOwner) tag;
        View parent = view;
        while (parent.getParent() instanceof View) {
            parent = (View) parent.getParent();
            tag = parent.getTag();
            if (tag instanceof ViewModelStoreOwner) return (ViewModelStoreOwner) tag;
        }
        return null;
    }
}
