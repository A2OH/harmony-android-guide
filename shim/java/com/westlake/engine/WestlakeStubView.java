/*
 * PF-arch-025: Context-free stub View/ViewGroup for Westlake's view-root.
 *
 * Standard android.view.View constructors call mContext.getResources()
 * which NPEs in our standalone dalvikvm — Activity.attach() can't run
 * without real Context. We need a View tree that doesn't depend on
 * Context at construction time so the render loop has something to
 * walk. WestlakeStubView extends ViewGroup with no-op overrides; it's
 * intended to be instantiated via Unsafe.allocateInstance to skip
 * Android's failing View ctor.
 */
package com.westlake.engine;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

public class WestlakeStubView extends ViewGroup {
    /* Normal ctor — used if Unsafe-allocate isn't available. Will likely
     * fail on Resources lookup, hence the Unsafe path is preferred. */
    public WestlakeStubView(Context context) {
        super(context);
    }

    /* Required by ViewGroup. No-op layout — render loop doesn't use this. */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int n = getChildCount();
        for (int i = 0; i < n; i++) {
            View child = getChildAt(i);
            if (child == null) continue;
            try {
                child.layout(l, t, r, b);
            } catch (Throwable ignored) {}
        }
    }
}
