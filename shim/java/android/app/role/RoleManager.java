package android.app.role;

/**
 * Android-compatible RoleManager shim. Stub — returns default/no-op values.
 */
public class RoleManager {

    public static final String ROLE_ASSISTANT        = "android.app.role.ASSISTANT";
    public static final String ROLE_BROWSER          = "android.app.role.BROWSER";
    public static final String ROLE_DIALER           = "android.app.role.DIALER";
    public static final String ROLE_SMS              = "android.app.role.SMS";
    public static final String ROLE_HOME             = "android.app.role.HOME";
    public static final String ROLE_EMERGENCY        = "android.app.role.EMERGENCY";
    public static final String ROLE_CALL_SCREENING   = "android.app.role.CALL_SCREENING";
    public static final String ROLE_CALL_REDIRECTION = "android.app.role.CALL_REDIRECTION";

    /**
     * Returns whether the given role is available on this device.
     *
     * @param roleName the name of the role to query
     * @return {@code false} — stub implementation
     */
    public boolean isRoleAvailable(String roleName) {
        return false; // stub
    }

    /**
     * Returns whether the calling application holds the given role.
     *
     * @param roleName the name of the role to query
     * @return {@code false} — stub implementation
     */
    public boolean isRoleHeld(String roleName) {
        return false; // stub
    }

    /**
     * Returns an Intent that can be used to request the given role.
     *
     * @param roleName the name of the role being requested
     * @return {@code null} — stub implementation
     */
    public Object requestRoleIntent(String roleName) {
        return null; // stub
    }
}
