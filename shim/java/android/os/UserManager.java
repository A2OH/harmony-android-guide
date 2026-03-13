package android.os;

/**
 * Android-compatible UserManager shim.
 * Provides user management stubs and DISALLOW_* policy constants.
 * In the shim environment there is always a single system user.
 */
public class UserManager {

    // -------------------------------------------------------------------------
    // DISALLOW_* restriction keys
    // -------------------------------------------------------------------------

    public static final String DISALLOW_ADD_USER            = "no_add_user";
    public static final String DISALLOW_ADD_MANAGED_PROFILE = "no_add_managed_profile";
    public static final String DISALLOW_REMOVE_USER         = "no_remove_user";
    public static final String DISALLOW_REMOVE_MANAGED_PROFILE = "no_remove_managed_profile";
    public static final String DISALLOW_DEBUGGING_FEATURES  = "no_debugging_features";
    public static final String DISALLOW_INSTALL_APPS        = "no_install_apps";
    public static final String DISALLOW_UNINSTALL_APPS      = "no_uninstall_apps";
    public static final String DISALLOW_SHARE_LOCATION      = "no_share_location";
    public static final String DISALLOW_INSTALL_UNKNOWN_SOURCES = "no_install_unknown_sources";
    public static final String DISALLOW_INSTALL_UNKNOWN_SOURCES_GLOBALLY = "no_install_unknown_sources_globally";
    public static final String DISALLOW_CONFIG_BLUETOOTH    = "no_config_bluetooth";
    public static final String DISALLOW_USB_FILE_TRANSFER   = "no_usb_file_transfer";
    public static final String DISALLOW_CONFIG_CREDENTIALS  = "no_config_credentials";
    public static final String DISALLOW_REMOVE_ACCOUNT      = "no_remove_managed_profile";
    public static final String DISALLOW_MODIFY_ACCOUNTS     = "no_modify_accounts";
    public static final String DISALLOW_CONFIG_WIFI         = "no_config_wifi";
    public static final String DISALLOW_CONFIG_CELL_BROADCASTS = "no_config_cell_broadcasts";
    public static final String DISALLOW_CONFIG_MOBILE_NETWORKS = "no_config_mobile_networks";
    public static final String DISALLOW_CONFIG_TETHERING    = "no_config_tethering";
    public static final String DISALLOW_NETWORK_RESET       = "no_network_reset";
    public static final String DISALLOW_FACTORY_RESET       = "no_factory_reset";
    public static final String DISALLOW_ADD_CLONE_PROFILE   = "no_add_clone_profile";
    public static final String DISALLOW_ADD_PRIVATE_PROFILE = "no_add_private_profile";
    public static final String DISALLOW_CONFIG_VPN          = "no_config_vpn";
    public static final String DISALLOW_CONFIG_LOCALE       = "no_config_locale";
    public static final String DISALLOW_MOUNT_PHYSICAL_MEDIA = "no_physical_media";
    public static final String DISALLOW_UNMUTE_MICROPHONE   = "no_unmute_microphone";
    public static final String DISALLOW_ADJUST_VOLUME       = "no_adjust_volume";
    public static final String DISALLOW_TELEPHONY           = "no_telephony";
    public static final String DISALLOW_SMS                 = "no_sms";
    public static final String DISALLOW_FUN                 = "no_fun";
    public static final String DISALLOW_CREATE_WINDOWS      = "no_create_windows";
    public static final String DISALLOW_CROSS_PROFILE_COPY_PASTE = "no_cross_profile_copy_paste";
    public static final String DISALLOW_OUTGOING_BEAM       = "no_outgoing_beam";
    public static final String DISALLOW_KEYGUARD_CUSTOMIZATION = "no_keyguard_customization";
    public static final String DISALLOW_SAFE_BOOT           = "no_safe_boot";
    public static final String DISALLOW_OUTGOING_CALLS      = "no_outgoing_calls";
    public static final String DISALLOW_USER_SWITCH         = "no_user_switch";
    public static final String DISALLOW_SET_WALLPAPER        = "no_set_wallpaper";
    public static final String DISALLOW_SET_USER_ICON       = "no_set_user_icon";
    public static final String DISALLOW_CONFIG_LOCATION     = "disallow_config_location";
    public static final String DISALLOW_AIRPLANE_MODE        = "no_airplane_mode";
    public static final String DISALLOW_AUTOFILL             = "no_autofill";
    public static final String DISALLOW_CONTENT_CAPTURE      = "no_content_capture";
    public static final String DISALLOW_CONTENT_SUGGESTIONS  = "no_content_suggestions";
    public static final String DISALLOW_SHARE_INTO_MANAGED_PROFILE = "no_sharing_into_profile";
    public static final String DISALLOW_UNIFIED_PASSWORD     = "no_unified_challenge";
    public static final String DISALLOW_CONFIG_DEFAULT_APPS  = "no_config_default_apps";
    public static final String DISALLOW_BLUETOOTH            = "no_bluetooth";
    public static final String DISALLOW_BLUETOOTH_SHARING    = "no_bluetooth_sharing";
    public static final String DISALLOW_CONFIG_PRIVATE_DNS   = "disallow_config_private_dns";
    public static final String DISALLOW_MICROPHONE_TOGGLE    = "no_microphone_toggle";
    public static final String DISALLOW_CAMERA_TOGGLE        = "no_camera_toggle";
    public static final String DISALLOW_CHANGE_WIFI_STATE    = "no_change_wifi_state";

    // -------------------------------------------------------------------------
    // USER_OPERATION_* result codes
    // -------------------------------------------------------------------------

    public static final int USER_OPERATION_SUCCESS             = 0;
    public static final int USER_OPERATION_ERROR_UNKNOWN       = 1;
    public static final int USER_OPERATION_ERROR_MANAGED_PROFILE = 2;
    public static final int USER_OPERATION_ERROR_MAX_RUNNING_USERS = 3;
    public static final int USER_OPERATION_ERROR_MAX_USERS     = 4;
    public static final int USER_OPERATION_ERROR_LOW_STORAGE   = 5;

    // -------------------------------------------------------------------------
    // Constructor (typically obtained via Context.getSystemService)
    // -------------------------------------------------------------------------

    public UserManager() {}

    // -------------------------------------------------------------------------
    // Core API stubs
    // -------------------------------------------------------------------------

    /**
     * Returns whether the calling user is a goat.
     * Always false in the shim.
     */
    public boolean isUserAGoat() {
        return false;
    }

    /**
     * Returns the display name for the current user.
     */
    public String getUserName() {
        return "User";
    }

    /**
     * Returns true if the calling user is the system (owner) user.
     */
    public boolean isSystemUser() {
        return true;
    }

    /**
     * Returns true if the calling user is an admin user.
     */
    public boolean isAdminUser() {
        return true;
    }

    /**
     * Returns the total number of users on the device.
     */
    public int getUserCount() {
        return 1;
    }

    /**
     * Returns a serial number uniquely identifying the given UserHandle.
     * Stub returns -1 for any null/unknown handle.
     */
    public long getSerialNumberForUser(Object userHandle) {
        return 0L;
    }

    /**
     * Returns whether the supplied user has a restriction on the given key.
     */
    public boolean hasUserRestriction(String restrictionKey) {
        return false;
    }

    /**
     * Returns true if this device is managed by an enterprise Device Policy Controller.
     */
    public boolean isDeviceInDemoMode() {
        return false;
    }

    /**
     * Returns true if the device supports multiple users.
     */
    public static boolean supportsMultipleUsers() {
        return false;
    }

    /**
     * Returns true if the calling user is running in a managed profile.
     */
    public boolean isManagedProfile() {
        return false;
    }

    /**
     * Returns true if the calling user is a demo user.
     */
    public boolean isDemoUser() {
        return false;
    }

    /**
     * Returns true if the calling user is a guest user.
     */
    public boolean isGuestUser() {
        return false;
    }

    /**
     * Returns true if the user is a clone profile.
     */
    public boolean isCloneProfile() {
        return false;
    }

    /**
     * Returns true if the device is in quiet mode.
     */
    public boolean isQuietModeEnabled(Object userHandle) {
        return false;
    }

    /**
     * Returns the user type string for the calling user.
     */
    public String getUserType() {
        return "android.os.usertype.full.SYSTEM";
    }
}
