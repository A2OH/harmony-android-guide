/*
 * OhDisplayProvider.java
 *
 * Snapshot of OH default display info, exposed to OhConfigurationConverter
 * for derivation of densityDpi / orientation / screenSize during
 * bindApplication. Thin wrapper over DisplayManagerAdapter (which already
 * has JNI to OH Rosen DisplayManager via display_manager_adapter_jni.cpp).
 *
 * Authoritative source: doc/ability_manager_ipc_adapter_design.html §1.1.4.4
 *   densityDpi: QUERY OH DisplayManagerProxy
 *   orientation: derived from rotation + width/height
 *   screenWidthDp/screenHeightDp/smallestScreenWidthDp: derived from px/dpi
 */
package adapter.activity;

import adapter.window.DisplayManagerAdapter;
import android.util.Log;
import android.view.DisplayInfo;

public final class OhDisplayProvider {

    private static final String TAG = "OH_DisplayProvider";

    /** rk3568 fallback when DM hasn't booted yet (typical 1280x720 @ xhdpi). */
    private static final int FALLBACK_WIDTH = 1280;
    private static final int FALLBACK_HEIGHT = 720;
    private static final int FALLBACK_DPI = 320;

    public static final class Snapshot {
        public int widthPx;
        public int heightPx;
        public int densityDpi;
        public float density;          // dpi / 160
        public int rotation;            // 0/1/2/3
        public boolean valid;           // false if everything is fallback
    }

    private OhDisplayProvider() {}

    public static Snapshot get() {
        Snapshot s = new Snapshot();
        try {
            DisplayInfo info = DisplayManagerAdapter.getInstance().getDisplayInfo(0);
            if (info != null) {
                s.widthPx = info.appWidth > 0 ? info.appWidth : info.logicalWidth;
                s.heightPx = info.appHeight > 0 ? info.appHeight : info.logicalHeight;
                s.densityDpi = info.logicalDensityDpi > 0
                        ? info.logicalDensityDpi : FALLBACK_DPI;
                s.density = s.densityDpi / 160f;
                s.rotation = info.rotation;
                s.valid = (s.widthPx > 0 && s.heightPx > 0);
            }
        } catch (Throwable t) {
            Log.w(TAG, "DisplayManagerAdapter query failed, using fallback", t);
        }
        if (!s.valid) {
            s.widthPx = FALLBACK_WIDTH;
            s.heightPx = FALLBACK_HEIGHT;
            s.densityDpi = FALLBACK_DPI;
            s.density = s.densityDpi / 160f;
            s.rotation = 0;
        }
        Log.d(TAG, "Snapshot: " + s.widthPx + "x" + s.heightPx
                + " dpi=" + s.densityDpi + " rot=" + s.rotation
                + " valid=" + s.valid);
        return s;
    }
}
