package android.support.v4.app;

/**
 * Android-compatible ActivityCompat shim for support-v4.
 * Provides static helpers for runtime permissions.
 */
public class ActivityCompat {

    /** Stub return value for permission checks — mirrors PackageManager.PERMISSION_DENIED. */
    public static final int PERMISSION_GRANTED = 0;
    public static final int PERMISSION_DENIED  = -1;

    private ActivityCompat() {}

    /**
     * Requests runtime permissions from the given activity.
     * In the shim layer this is a no-op; the host environment handles real permissions.
     *
     * @param activity    the Activity requesting permissions (typed as Object to avoid dependency)
     * @param permissions array of permission strings (e.g. Manifest.permission.CAMERA)
     * @param requestCode caller-defined code passed back in onRequestPermissionsResult
     */
    public static void requestPermissions(Object activity, String[] permissions, int requestCode) {
        // stub — no-op in shim layer
        System.out.println("[ActivityCompat] requestPermissions requestCode=" + requestCode);
    }

    /**
     * Returns whether the user has previously denied a permission and checked
     * "Never ask again".  Always returns false in the shim layer.
     *
     * @param activity   the Activity context
     * @param permission the permission string to check
     * @return false (stub)
     */
    public static boolean shouldShowRequestPermissionRationale(Object activity, String permission) {
        return false;
    }

    /**
     * Checks whether the given permission has been granted to the calling context.
     * Always returns PERMISSION_DENIED in the shim layer so callers are forced
     * through the normal request flow.
     *
     * @param context    the context to check against (typed as Object)
     * @param permission the permission string to check
     * @return {@link #PERMISSION_DENIED} (stub)
     */
    public static int checkSelfPermission(Object context, String permission) {
        return PERMISSION_DENIED;
    }

    // -------------------------------------------------------------------------
    // OnRequestPermissionsResultCallback — implemented by Activities that handle
    // the permission result callback.
    // -------------------------------------------------------------------------

    public interface OnRequestPermissionsResultCallback {
        void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults);
    }
}
