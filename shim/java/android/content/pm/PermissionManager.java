package android.content.pm;

/**
 * Android-compatible PermissionManager stub (API 31+).
 *
 * Provides permission-checking constants and methods. All permission checks
 * return PERMISSION_DENIED by default; the OpenHarmony bridge layer is
 * responsible for wiring real permission state.
 */
public class PermissionManager {

    /** Permission check result: the permission is granted. */
    public static final int PERMISSION_GRANTED = 0;

    /** Permission check result: the permission is denied. */
    public static final int PERMISSION_DENIED = -1;

    /**
     * Check whether a particular permission has been granted for a given pid.
     *
     * @param permission the name of the permission to check
     * @param pid        the process id to check against
     * @return {@link #PERMISSION_DENIED} (stub always denies)
     */
    public int checkPermission(String permission, int pid) {
        return PERMISSION_DENIED;
    }

    /**
     * Return the flags associated with a permission.
     *
     * @param permission the name of the permission
     * @param packageInfo opaque package context (typed as Object for stub compatibility)
     * @return 0 (stub returns no flags)
     */
    public int getPermissionFlags(String permission, Object packageInfo) {
        return 0;
    }
}
