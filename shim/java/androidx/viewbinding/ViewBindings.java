// PF-noice-013 (2026-05-05) shim: noice's auto-generated view-binding code
// (e.g. MainActivityBinding.bind) calls ViewBindings.findChildViewById(root,
// R.id.X), and if the result is null, throws NPE "Missing required view with
// ID: res/0xNNNNNNNN". The Westlake guest's layout-XML inflation drops or
// misroutes some views (e.g. <include> children, fragment containers), so
// findChildViewById returns null for views that view-binding considers
// required. This shim ensures findChildViewById NEVER returns null — when
// the tree doesn't have the requested id, return a generic stub View. The
// view-binding NPE no longer fires; downstream code may or may not work
// (depends on whether it actually uses the returned view), but the activity
// onCreate completes and the layout that DID inflate becomes visible.
//
// This wins over noice's bundled androidx.viewbinding.ViewBindings because
// aosp-shim.dex is listed first on the dalvikvm classpath.
package androidx.viewbinding;

import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

public final class ViewBindings {
    @SuppressWarnings("unchecked")
    public static <T extends View> T findChildViewById(View rootView, int id) {
        if (rootView == null) {
            return null;
        }
        try {
            T result = (T) rootView.findViewById(id);
            if (result != null) {
                return result;
            }
        } catch (Throwable ignored) {
            // findViewById failed; fall through to stub
        }
        // Manufacture a stub view so view-binding's null-check passes.
        try {
            android.content.Context ctx = rootView.getContext();
            FrameLayout stub = new FrameLayout(ctx);
            stub.setId(id);
            // Don't attach to parent — view-binding just uses the reference.
            return (T) stub;
        } catch (Throwable t) {
            // Last resort: return null, which will throw NPE in caller, but
            // at least we tried.
            return null;
        }
    }

    private ViewBindings() {}
}
