package android.os;
import android.content.Intent;
import java.util.List;

public class UserManager {
    public static final int ALLOW_PARENT_PROFILE_APP_LINKING = 0;
    public static final int DISALLOW_ADD_USER = 0;
    public static final int DISALLOW_ADJUST_VOLUME = 0;
    public static final int DISALLOW_AIRPLANE_MODE = 0;
    public static final int DISALLOW_AMBIENT_DISPLAY = 0;
    public static final int DISALLOW_APPS_CONTROL = 0;
    public static final int DISALLOW_AUTOFILL = 0;
    public static final int DISALLOW_BLUETOOTH = 0;
    public static final int DISALLOW_BLUETOOTH_SHARING = 0;
    public static final int DISALLOW_CONFIG_BLUETOOTH = 0;
    public static final int DISALLOW_CONFIG_BRIGHTNESS = 0;
    public static final int DISALLOW_CONFIG_CELL_BROADCASTS = 0;
    public static final int DISALLOW_CONFIG_CREDENTIALS = 0;
    public static final int DISALLOW_CONFIG_DATE_TIME = 0;
    public static final int DISALLOW_CONFIG_LOCALE = 0;
    public static final int DISALLOW_CONFIG_LOCATION = 0;
    public static final int DISALLOW_CONFIG_MOBILE_NETWORKS = 0;
    public static final int DISALLOW_CONFIG_PRIVATE_DNS = 0;
    public static final int DISALLOW_CONFIG_SCREEN_TIMEOUT = 0;
    public static final int DISALLOW_CONFIG_TETHERING = 0;
    public static final int DISALLOW_CONFIG_VPN = 0;
    public static final int DISALLOW_CONFIG_WIFI = 0;
    public static final int DISALLOW_CONTENT_CAPTURE = 0;
    public static final int DISALLOW_CONTENT_SUGGESTIONS = 0;
    public static final int DISALLOW_CREATE_WINDOWS = 0;
    public static final int DISALLOW_CROSS_PROFILE_COPY_PASTE = 0;
    public static final int DISALLOW_DATA_ROAMING = 0;
    public static final int DISALLOW_DEBUGGING_FEATURES = 0;
    public static final int DISALLOW_FACTORY_RESET = 0;
    public static final int DISALLOW_FUN = 0;
    public static final int DISALLOW_INSTALL_APPS = 0;
    public static final int DISALLOW_INSTALL_UNKNOWN_SOURCES = 0;
    public static final int DISALLOW_INSTALL_UNKNOWN_SOURCES_GLOBALLY = 0;
    public static final int DISALLOW_MODIFY_ACCOUNTS = 0;
    public static final int DISALLOW_MOUNT_PHYSICAL_MEDIA = 0;
    public static final int DISALLOW_NETWORK_RESET = 0;
    public static final int DISALLOW_OUTGOING_BEAM = 0;
    public static final int DISALLOW_OUTGOING_CALLS = 0;
    public static final int DISALLOW_PRINTING = 0;
    public static final int DISALLOW_REMOVE_USER = 0;
    public static final int DISALLOW_SAFE_BOOT = 0;
    public static final int DISALLOW_SET_USER_ICON = 0;
    public static final int DISALLOW_SET_WALLPAPER = 0;
    public static final int DISALLOW_SHARE_INTO_MANAGED_PROFILE = 0;
    public static final int DISALLOW_SHARE_LOCATION = 0;
    public static final int DISALLOW_SMS = 0;
    public static final int DISALLOW_SYSTEM_ERROR_DIALOGS = 0;
    public static final int DISALLOW_UNIFIED_PASSWORD = 0;
    public static final int DISALLOW_UNINSTALL_APPS = 0;
    public static final int DISALLOW_UNMUTE_MICROPHONE = 0;
    public static final int DISALLOW_USB_FILE_TRANSFER = 0;
    public static final int DISALLOW_USER_SWITCH = 0;
    public static final int ENSURE_VERIFY_APPS = 0;
    public static final int KEY_RESTRICTIONS_PENDING = 0;
    public static final int QUIET_MODE_DISABLE_ONLY_IF_CREDENTIAL_NOT_REQUIRED = 0;
    public static final int USER_CREATION_FAILED_NOT_PERMITTED = 0;
    public static final int USER_CREATION_FAILED_NO_MORE_USERS = 0;
    public static final int USER_OPERATION_ERROR_CURRENT_USER = 0;
    public static final int USER_OPERATION_ERROR_LOW_STORAGE = 0;
    public static final int USER_OPERATION_ERROR_MANAGED_PROFILE = 0;
    public static final int USER_OPERATION_ERROR_MAX_RUNNING_USERS = 0;
    public static final int USER_OPERATION_ERROR_MAX_USERS = 0;
    public static final int USER_OPERATION_ERROR_UNKNOWN = 0;
    public static final int USER_OPERATION_SUCCESS = 0;

    public UserManager() {}

    public static Intent createUserCreationIntent(String p0, String p1, String p2, PersistableBundle p3) { return null; }
    public long getSerialNumberForUser(UserHandle p0) { return 0L; }
    public long getUserCreationTime(UserHandle p0) { return 0L; }
    public UserHandle getUserForSerialNumber(long p0) { return null; }
    public List<?> getUserProfiles() { return null; }
    public Bundle getUserRestrictions() { return null; }
    public boolean hasUserRestriction(String p0) { return false; }
    public boolean isDemoUser() { return false; }
    public boolean isManagedProfile() { return false; }
    public boolean isQuietModeEnabled(UserHandle p0) { return false; }
    public boolean isSystemUser() { return false; }
    public boolean isUserAGoat() { return false; }
    public boolean isUserUnlocked() { return false; }
    public boolean requestQuietModeEnabled(boolean p0, UserHandle p1, int p2) { return false; }
    public static boolean supportsMultipleUsers() { return false; }
}
