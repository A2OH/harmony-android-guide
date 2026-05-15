// SPDX-License-Identifier: Apache-2.0
//
// Westlake V2-Step5 WindowManagerImpl stub (decision 12-A from
// BINDER_PIVOT_DESIGN_V2.md §3.5).
//
// Westlake-owned classpath-shadowed WindowManagerImpl: no real surface
// daemon hookup yet — `addView`, `updateViewLayout`, `removeView`,
// `removeViewImmediate` are no-ops until M6 (westlake-surface-daemon)
// lands. `getDefaultDisplay()` routes through the DisplayManager system
// service (backed by M4d WestlakeDisplayManagerService).

package android.view;

import android.content.Context;

public final class WindowManagerImpl implements WindowManager {

    private final Context mContext;

    public WindowManagerImpl(Context context) {
        mContext = context;
    }

    @Override
    public Display getDefaultDisplay() {
        // V2-Step5 stub: real Display construction needs DisplayManagerGlobal
        // + DisplayInfo, both of which require system_server cold-init we
        // don't run. Reflectively try to ask DisplayManager (backed by
        // M4d WestlakeDisplayManagerService) — it may return its own
        // Display shape. Callers that compare against null behave correctly
        // when we fall through.
        try {
            Context c = mContext != null ? mContext.getApplicationContext() : null;
            if (c == null) {
                c = mContext;
            }
            if (c == null) {
                return null;
            }
            Object svc = c.getSystemService(Context.DISPLAY_SERVICE);
            if (svc != null) {
                java.lang.reflect.Method m = svc.getClass().getMethod("getDisplay", int.class);
                Object disp = m.invoke(svc, Display.DEFAULT_DISPLAY);
                if (disp instanceof Display) {
                    return (Display) disp;
                }
            }
        } catch (Throwable t) {
            // M4d service may not be ready — fall through to null.
        }
        return null;
    }

    @Override
    public WindowMetrics getCurrentWindowMetrics() {
        return null;
    }

    @Override
    public WindowMetrics getMaximumWindowMetrics() {
        return null;
    }

    @Override
    public void addView(View view, ViewGroup.LayoutParams params) {
        // No-op for V2; M6 surface daemon will route here.
    }

    @Override
    public void updateViewLayout(View view, ViewGroup.LayoutParams params) {
        // No-op.
    }

    @Override
    public void removeView(View view) {
        // No-op.
    }

    @Override
    public void removeViewImmediate(View view) {
        // No-op.
    }

    public boolean isHardwareAccelerated() {
        return false;
    }
}
