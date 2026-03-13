package android.app.admin;

/**
 * Android-compatible DevicePolicyManager shim.
 * Stub — device policy operations are all no-ops; status queries return safe defaults.
 */
public class DevicePolicyManager {

    // --- Intent actions ---
    public static final String ACTION_ADD_DEVICE_ADMIN =
            "android.app.action.ADD_DEVICE_ADMIN";

    // --- Password quality constants ---
    public static final int PASSWORD_QUALITY_UNSPECIFIED        = 0x00000000;
    public static final int PASSWORD_QUALITY_BIOMETRIC_WEAK     = 0x00008000;
    public static final int PASSWORD_QUALITY_SOMETHING          = 0x00010000;
    public static final int PASSWORD_QUALITY_NUMERIC            = 0x00020000;
    public static final int PASSWORD_QUALITY_NUMERIC_COMPLEX    = 0x00030000;
    public static final int PASSWORD_QUALITY_ALPHABETIC         = 0x00040000;
    public static final int PASSWORD_QUALITY_ALPHANUMERIC       = 0x00050000;
    public static final int PASSWORD_QUALITY_COMPLEX            = 0x00060000;
    public static final int PASSWORD_QUALITY_MANAGED            = 0x00080000;

    // --- Storage encryption status constants ---
    public static final int ENCRYPTION_STATUS_UNSUPPORTED          = 0;
    public static final int ENCRYPTION_STATUS_INACTIVE             = 1;
    public static final int ENCRYPTION_STATUS_ACTIVATING           = 2;
    public static final int ENCRYPTION_STATUS_ACTIVE               = 3;
    public static final int ENCRYPTION_STATUS_ACTIVE_INCOMPATIBLE  = 4;
    public static final int ENCRYPTION_STATUS_ACTIVE_PER_USER      = 5;

    // --- wipeData flags ---
    public static final int WIPE_EXTERNAL_STORAGE = 0x0001;
    public static final int WIPE_RESET_PROTECTION_DATA = 0x0002;

    // -------------------------------------------------------------------------
    // Admin / owner queries
    // -------------------------------------------------------------------------

    /** Returns false — no active admin in shim environment. */
    public boolean isAdminActive(Object admin) { return false; }

    /** Returns false — not a device owner in shim environment. */
    public boolean isDeviceOwnerApp(String packageName) { return false; }

    /** Returns false — not a profile owner in shim environment. */
    public boolean isProfileOwnerApp(String packageName) { return false; }

    // -------------------------------------------------------------------------
    // Device control — all no-ops
    // -------------------------------------------------------------------------

    /** Immediately locks the device. No-op in shim. */
    public void lockNow() {}

    /**
     * Wipes the device. No-op in shim.
     *
     * @param flags combination of WIPE_* flags
     */
    public void wipeData(int flags) {}

    /**
     * Resets the device password.
     *
     * @param password the new password string
     * @param flags    optional flags (0 for none)
     * @return false — always fails in shim
     */
    public boolean resetPassword(String password, int flags) { return false; }

    /** Disables or re-enables the camera. No-op in shim. */
    public void setCameraDisabled(Object admin, boolean disabled) {}

    // -------------------------------------------------------------------------
    // Policy setters — all no-ops
    // -------------------------------------------------------------------------

    /** Sets the minimum password quality requirement. No-op in shim. */
    public void setPasswordQuality(Object admin, int quality) {}

    /**
     * Sets the maximum time-to-lock (screen timeout).
     *
     * @param admin   active admin component
     * @param timeMs  timeout in milliseconds
     */
    public void setMaximumTimeToLock(Object admin, long timeMs) {}

    // -------------------------------------------------------------------------
    // Status queries
    // -------------------------------------------------------------------------

    /**
     * Returns the storage encryption status.
     * Always returns ENCRYPTION_STATUS_UNSUPPORTED in shim.
     */
    public int getStorageEncryptionStatus() {
        return ENCRYPTION_STATUS_UNSUPPORTED;
    }
}
