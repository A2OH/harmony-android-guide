package android.accounts;

public class AccountManager {
    public AccountManager() {}

    public static final int ACTION_ACCOUNT_REMOVED = 0;
    public static final int ACTION_AUTHENTICATOR_INTENT = 0;
    public static final int AUTHENTICATOR_ATTRIBUTES_NAME = 0;
    public static final int AUTHENTICATOR_META_DATA_NAME = 0;
    public static final int ERROR_CODE_BAD_ARGUMENTS = 0;
    public static final int ERROR_CODE_BAD_AUTHENTICATION = 0;
    public static final int ERROR_CODE_BAD_REQUEST = 0;
    public static final int ERROR_CODE_CANCELED = 0;
    public static final int ERROR_CODE_INVALID_RESPONSE = 0;
    public static final int ERROR_CODE_NETWORK_ERROR = 0;
    public static final int ERROR_CODE_REMOTE_EXCEPTION = 0;
    public static final int ERROR_CODE_UNSUPPORTED_OPERATION = 0;
    public static final int KEY_ACCOUNTS = 0;
    public static final int KEY_ACCOUNT_AUTHENTICATOR_RESPONSE = 0;
    public static final int KEY_ACCOUNT_MANAGER_RESPONSE = 0;
    public static final int KEY_ACCOUNT_NAME = 0;
    public static final int KEY_ACCOUNT_SESSION_BUNDLE = 0;
    public static final int KEY_ACCOUNT_STATUS_TOKEN = 0;
    public static final int KEY_ACCOUNT_TYPE = 0;
    public static final int KEY_ANDROID_PACKAGE_NAME = 0;
    public static final int KEY_AUTHENTICATOR_TYPES = 0;
    public static final int KEY_AUTHTOKEN = 0;
    public static final int KEY_AUTH_FAILED_MESSAGE = 0;
    public static final int KEY_AUTH_TOKEN_LABEL = 0;
    public static final int KEY_BOOLEAN_RESULT = 0;
    public static final int KEY_CALLER_PID = 0;
    public static final int KEY_CALLER_UID = 0;
    public static final int KEY_ERROR_CODE = 0;
    public static final int KEY_ERROR_MESSAGE = 0;
    public static final int KEY_INTENT = 0;
    public static final int KEY_LAST_AUTHENTICATED_TIME = 0;
    public static final int KEY_PASSWORD = 0;
    public static final int KEY_USERDATA = 0;
    public static final int PACKAGE_NAME_KEY_LEGACY_NOT_VISIBLE = 0;
    public static final int PACKAGE_NAME_KEY_LEGACY_VISIBLE = 0;
    public static final int VISIBILITY_NOT_VISIBLE = 0;
    public static final int VISIBILITY_UNDEFINED = 0;
    public static final int VISIBILITY_USER_MANAGED_NOT_VISIBLE = 0;
    public static final int VISIBILITY_USER_MANAGED_VISIBLE = 0;
    public static final int VISIBILITY_VISIBLE = 0;
    public boolean addAccountExplicitly(Object p0, Object p1, Object p2, Object p3) { return false; }
    public void addOnAccountsUpdatedListener(Object p0, Object p1, Object p2, Object p3) {}
    public Object finishSession(Object p0, Object p1, Object p2, Object p3) { return null; }
    public static Object get(Object p0) { return null; }
    public int getAccountVisibility(Object p0, Object p1) { return 0; }
    public Object getAccountsAndVisibilityForPackage(Object p0, Object p1) { return null; }
    public Object getAccountsByTypeAndFeatures(Object p0, Object p1, Object p2, Object p3) { return null; }
    public Object getAuthenticatorTypes() { return null; }
    public Object getPackagesAndVisibilityForAccount(Object p0) { return null; }
    public Object getPreviousName(Object p0) { return null; }
    public Object hasFeatures(Object p0, Object p1, Object p2, Object p3) { return null; }
    public Object isCredentialsUpdateSuggested(Object p0, Object p1, Object p2, Object p3) { return null; }
    public static Object newChooseAccountIntent(Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6) { return null; }
    public void removeOnAccountsUpdatedListener(Object p0) {}
    public boolean setAccountVisibility(Object p0, Object p1, Object p2) { return false; }
    public Object startAddAccountSession(Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6) { return null; }
    public Object startUpdateCredentialsSession(Object p0, Object p1, Object p2, Object p3, Object p4, Object p5) { return null; }
}
