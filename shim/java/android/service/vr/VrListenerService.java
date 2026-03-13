package android.service.vr;

import android.app.Service;

/**
 * Android-compatible VrListenerService shim. Stub for VR mode listener service.
 */
public abstract class VrListenerService extends Service {

    /**
     * Called when the current VR-mode activity changes.
     *
     * @param component the ComponentName of the new VR activity, or null if VR mode ended.
     *                  (ComponentName in real API; Object used here to avoid extra stubs.)
     */
    public abstract void onCurrentVrActivityChanged(Object component);

    /**
     * Returns whether the given package has a valid VrListenerService that is enabled.
     * Stub always returns false.
     *
     * @param context     application context
     * @param packageName the package to query
     * @return false (stub)
     */
    public static boolean isVrModePackageEnabled(Object context, Object packageName) {
        return false;
    }
}
