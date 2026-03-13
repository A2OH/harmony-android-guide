package android.app.admin;

public class DevicePolicyManager {
    public DevicePolicyManager() {}

    public static final int ACTION_ADD_DEVICE_ADMIN = 0;
    public static final int ACTION_ADMIN_POLICY_COMPLIANCE = 0;
    public static final int ACTION_APPLICATION_DELEGATION_SCOPES_CHANGED = 0;
    public static final int ACTION_CHECK_POLICY_COMPLIANCE = 0;
    public static final int ACTION_DEVICE_ADMIN_SERVICE = 0;
    public static final int ACTION_DEVICE_OWNER_CHANGED = 0;
    public static final int ACTION_GET_PROVISIONING_MODE = 0;
    public static final int ACTION_MANAGED_PROFILE_PROVISIONED = 0;
    public static final int ACTION_PROFILE_OWNER_CHANGED = 0;
    public static final int ACTION_PROVISIONING_SUCCESSFUL = 0;
    public static final int ACTION_PROVISION_MANAGED_DEVICE = 0;
    public static final int ACTION_PROVISION_MANAGED_PROFILE = 0;
    public static final int ACTION_SET_NEW_PARENT_PROFILE_PASSWORD = 0;
    public static final int ACTION_SET_NEW_PASSWORD = 0;
    public static final int ACTION_START_ENCRYPTION = 0;
    public static final int ACTION_SYSTEM_UPDATE_POLICY_CHANGED = 0;
    public static final int DELEGATION_APP_RESTRICTIONS = 0;
    public static final int DELEGATION_BLOCK_UNINSTALL = 0;
    public static final int DELEGATION_CERT_INSTALL = 0;
    public static final int DELEGATION_CERT_SELECTION = 0;
    public static final int DELEGATION_ENABLE_SYSTEM_APP = 0;
    public static final int DELEGATION_INSTALL_EXISTING_PACKAGE = 0;
    public static final int DELEGATION_KEEP_UNINSTALLED_PACKAGES = 0;
    public static final int DELEGATION_NETWORK_LOGGING = 0;
    public static final int DELEGATION_PACKAGE_ACCESS = 0;
    public static final int DELEGATION_PERMISSION_GRANT = 0;
    public static final int ENCRYPTION_STATUS_ACTIVATING = 0;
    public static final int ENCRYPTION_STATUS_ACTIVE = 0;
    public static final int ENCRYPTION_STATUS_ACTIVE_DEFAULT_KEY = 0;
    public static final int ENCRYPTION_STATUS_ACTIVE_PER_USER = 0;
    public static final int ENCRYPTION_STATUS_INACTIVE = 0;
    public static final int ENCRYPTION_STATUS_UNSUPPORTED = 0;
    public static final int EXTRA_ADD_EXPLANATION = 0;
    public static final int EXTRA_DELEGATION_SCOPES = 0;
    public static final int EXTRA_DEVICE_ADMIN = 0;
    public static final int EXTRA_PROVISIONING_ACCOUNT_TO_MIGRATE = 0;
    public static final int EXTRA_PROVISIONING_ADMIN_EXTRAS_BUNDLE = 0;
    public static final int EXTRA_PROVISIONING_DEVICE_ADMIN_COMPONENT_NAME = 0;
    public static final int EXTRA_PROVISIONING_DEVICE_ADMIN_MINIMUM_VERSION_CODE = 0;
    public static final int EXTRA_PROVISIONING_DEVICE_ADMIN_PACKAGE_CHECKSUM = 0;
    public static final int EXTRA_PROVISIONING_DEVICE_ADMIN_PACKAGE_DOWNLOAD_COOKIE_HEADER = 0;
    public static final int EXTRA_PROVISIONING_DEVICE_ADMIN_PACKAGE_DOWNLOAD_LOCATION = 0;
    public static final int EXTRA_PROVISIONING_DEVICE_ADMIN_SIGNATURE_CHECKSUM = 0;
    public static final int EXTRA_PROVISIONING_DISCLAIMERS = 0;
    public static final int EXTRA_PROVISIONING_DISCLAIMER_CONTENT = 0;
    public static final int EXTRA_PROVISIONING_DISCLAIMER_HEADER = 0;
    public static final int EXTRA_PROVISIONING_IMEI = 0;
    public static final int EXTRA_PROVISIONING_KEEP_ACCOUNT_ON_MIGRATION = 0;
    public static final int EXTRA_PROVISIONING_LEAVE_ALL_SYSTEM_APPS_ENABLED = 0;
    public static final int EXTRA_PROVISIONING_LOCALE = 0;
    public static final int EXTRA_PROVISIONING_LOCAL_TIME = 0;
    public static final int EXTRA_PROVISIONING_LOGO_URI = 0;
    public static final int EXTRA_PROVISIONING_MAIN_COLOR = 0;
    public static final int EXTRA_PROVISIONING_MODE = 0;
    public static final int EXTRA_PROVISIONING_SERIAL_NUMBER = 0;
    public static final int EXTRA_PROVISIONING_SKIP_EDUCATION_SCREENS = 0;
    public static final int EXTRA_PROVISIONING_SKIP_ENCRYPTION = 0;
    public static final int EXTRA_PROVISIONING_SKIP_USER_CONSENT = 0;
    public static final int EXTRA_PROVISIONING_TIME_ZONE = 0;
    public static final int EXTRA_PROVISIONING_WIFI_ANONYMOUS_IDENTITY = 0;
    public static final int EXTRA_PROVISIONING_WIFI_CA_CERTIFICATE = 0;
    public static final int EXTRA_PROVISIONING_WIFI_DOMAIN = 0;
    public static final int EXTRA_PROVISIONING_WIFI_EAP_METHOD = 0;
    public static final int EXTRA_PROVISIONING_WIFI_HIDDEN = 0;
    public static final int EXTRA_PROVISIONING_WIFI_IDENTITY = 0;
    public static final int EXTRA_PROVISIONING_WIFI_PAC_URL = 0;
    public static final int EXTRA_PROVISIONING_WIFI_PASSWORD = 0;
    public static final int EXTRA_PROVISIONING_WIFI_PHASE2_AUTH = 0;
    public static final int EXTRA_PROVISIONING_WIFI_PROXY_BYPASS = 0;
    public static final int EXTRA_PROVISIONING_WIFI_PROXY_HOST = 0;
    public static final int EXTRA_PROVISIONING_WIFI_PROXY_PORT = 0;
    public static final int EXTRA_PROVISIONING_WIFI_SECURITY_TYPE = 0;
    public static final int EXTRA_PROVISIONING_WIFI_SSID = 0;
    public static final int EXTRA_PROVISIONING_WIFI_USER_CERTIFICATE = 0;
    public static final int FLAG_EVICT_CREDENTIAL_ENCRYPTION_KEY = 0;
    public static final int FLAG_MANAGED_CAN_ACCESS_PARENT = 0;
    public static final int FLAG_PARENT_CAN_ACCESS_MANAGED = 0;
    public static final int ID_TYPE_BASE_INFO = 0;
    public static final int ID_TYPE_IMEI = 0;
    public static final int ID_TYPE_INDIVIDUAL_ATTESTATION = 0;
    public static final int ID_TYPE_MEID = 0;
    public static final int ID_TYPE_SERIAL = 0;
    public static final int INSTALLKEY_REQUEST_CREDENTIALS_ACCESS = 0;
    public static final int INSTALLKEY_SET_USER_SELECTABLE = 0;
    public static final int KEYGUARD_DISABLE_BIOMETRICS = 0;
    public static final int KEYGUARD_DISABLE_FACE = 0;
    public static final int KEYGUARD_DISABLE_FEATURES_ALL = 0;
    public static final int KEYGUARD_DISABLE_FEATURES_NONE = 0;
    public static final int KEYGUARD_DISABLE_FINGERPRINT = 0;
    public static final int KEYGUARD_DISABLE_IRIS = 0;
    public static final int KEYGUARD_DISABLE_REMOTE_INPUT = 0;
    public static final int KEYGUARD_DISABLE_SECURE_CAMERA = 0;
    public static final int KEYGUARD_DISABLE_SECURE_NOTIFICATIONS = 0;
    public static final int KEYGUARD_DISABLE_TRUST_AGENTS = 0;
    public static final int KEYGUARD_DISABLE_UNREDACTED_NOTIFICATIONS = 0;
    public static final int KEYGUARD_DISABLE_WIDGETS_ALL = 0;
    public static final int LEAVE_ALL_SYSTEM_APPS_ENABLED = 0;
    public static final int LOCK_TASK_FEATURE_BLOCK_ACTIVITY_START_IN_TASK = 0;
    public static final int LOCK_TASK_FEATURE_GLOBAL_ACTIONS = 0;
    public static final int LOCK_TASK_FEATURE_HOME = 0;
    public static final int LOCK_TASK_FEATURE_KEYGUARD = 0;
    public static final int LOCK_TASK_FEATURE_NONE = 0;
    public static final int LOCK_TASK_FEATURE_NOTIFICATIONS = 0;
    public static final int LOCK_TASK_FEATURE_OVERVIEW = 0;
    public static final int LOCK_TASK_FEATURE_SYSTEM_INFO = 0;
    public static final int MAKE_USER_EPHEMERAL = 0;
    public static final int MIME_TYPE_PROVISIONING_NFC = 0;
    public static final int PASSWORD_COMPLEXITY_HIGH = 0;
    public static final int PASSWORD_COMPLEXITY_LOW = 0;
    public static final int PASSWORD_COMPLEXITY_MEDIUM = 0;
    public static final int PASSWORD_COMPLEXITY_NONE = 0;
    public static final int PASSWORD_QUALITY_ALPHABETIC = 0;
    public static final int PASSWORD_QUALITY_ALPHANUMERIC = 0;
    public static final int PASSWORD_QUALITY_BIOMETRIC_WEAK = 0;
    public static final int PASSWORD_QUALITY_COMPLEX = 0;
    public static final int PASSWORD_QUALITY_NUMERIC = 0;
    public static final int PASSWORD_QUALITY_NUMERIC_COMPLEX = 0;
    public static final int PASSWORD_QUALITY_SOMETHING = 0;
    public static final int PASSWORD_QUALITY_UNSPECIFIED = 0;
    public static final int PERMISSION_GRANT_STATE_DEFAULT = 0;
    public static final int PERMISSION_GRANT_STATE_DENIED = 0;
    public static final int PERMISSION_GRANT_STATE_GRANTED = 0;
    public static final int PERMISSION_POLICY_AUTO_DENY = 0;
    public static final int PERMISSION_POLICY_AUTO_GRANT = 0;
    public static final int PERMISSION_POLICY_PROMPT = 0;
    public static final int PERSONAL_APPS_NOT_SUSPENDED = 0;
    public static final int PERSONAL_APPS_SUSPENDED_EXPLICITLY = 0;
    public static final int PERSONAL_APPS_SUSPENDED_PROFILE_TIMEOUT = 0;
    public static final int POLICY_DISABLE_CAMERA = 0;
    public static final int POLICY_DISABLE_SCREEN_CAPTURE = 0;
    public static final int PRIVATE_DNS_MODE_OFF = 0;
    public static final int PRIVATE_DNS_MODE_OPPORTUNISTIC = 0;
    public static final int PRIVATE_DNS_MODE_PROVIDER_HOSTNAME = 0;
    public static final int PRIVATE_DNS_MODE_UNKNOWN = 0;
    public static final int PRIVATE_DNS_SET_ERROR_FAILURE_SETTING = 0;
    public static final int PRIVATE_DNS_SET_ERROR_HOST_NOT_SERVING = 0;
    public static final int PRIVATE_DNS_SET_NO_ERROR = 0;
    public static final int PROVISIONING_MODE_FULLY_MANAGED_DEVICE = 0;
    public static final int PROVISIONING_MODE_MANAGED_PROFILE = 0;
    public static final int RESET_PASSWORD_DO_NOT_ASK_CREDENTIALS_ON_BOOT = 0;
    public static final int RESET_PASSWORD_REQUIRE_ENTRY = 0;
    public static final int SKIP_SETUP_WIZARD = 0;
    public static final int WIPE_EUICC = 0;
    public static final int WIPE_EXTERNAL_STORAGE = 0;
    public static final int WIPE_RESET_PROTECTION_DATA = 0;
    public static final int WIPE_SILENTLY = 0;
    public static final int UPDATE_ERROR_BATTERY_LOW = 0;
    public static final int UPDATE_ERROR_FILE_NOT_FOUND = 0;
    public static final int UPDATE_ERROR_INCORRECT_OS_VERSION = 0;
    public static final int UPDATE_ERROR_UNKNOWN = 0;
    public static final int UPDATE_ERROR_UPDATE_FILE_INVALID = 0;
    public void addCrossProfileIntentFilter(Object p0, Object p1, Object p2) {}
    public boolean addCrossProfileWidgetProvider(Object p0, Object p1) { return false; }
    public int addOverrideApn(Object p0, Object p1) { return 0; }
    public void addPersistentPreferredActivity(Object p0, Object p1, Object p2) {}
    public void addUserRestriction(Object p0, Object p1) {}
    public boolean bindDeviceAdminServiceAsUser(Object p0, Object p1, Object p2, Object p3, Object p4) { return false; }
    public void clearApplicationUserData(Object p0, Object p1, Object p2, Object p3) {}
    public void clearCrossProfileIntentFilters(Object p0) {}
    public void clearPackagePersistentPreferredActivities(Object p0, Object p1) {}
    public boolean clearResetPasswordToken(Object p0) { return false; }
    public void clearUserRestriction(Object p0, Object p1) {}
    public Object createAdminSupportIntent(Object p0) { return null; }
    public void enableSystemApp(Object p0, Object p1) {}
    public Object generateKeyPair(Object p0, Object p1, Object p2, Object p3) { return null; }
    public boolean getAutoTimeEnabled(Object p0) { return false; }
    public boolean getAutoTimeZoneEnabled(Object p0) { return false; }
    public boolean getBluetoothContactSharingDisabled(Object p0) { return false; }
    public boolean getCameraDisabled(Object p0) { return false; }
    public boolean getCrossProfileCallerIdDisabled(Object p0) { return false; }
    public boolean getCrossProfileContactsSearchDisabled(Object p0) { return false; }
    public int getCurrentFailedPasswordAttempts() { return 0; }
    public Object getDeviceOwnerLockScreenInfo() { return null; }
    public Object getEndUserSessionMessage(Object p0) { return null; }
    public int getGlobalPrivateDnsMode(Object p0) { return 0; }
    public int getKeyguardDisabledFeatures(Object p0) { return 0; }
    public int getLockTaskFeatures(Object p0) { return 0; }
    public long getManagedProfileMaximumTimeOff(Object p0) { return 0L; }
    public int getMaximumFailedPasswordsForWipe(Object p0) { return 0; }
    public long getMaximumTimeToLock(Object p0) { return 0L; }
    public Object getOverrideApns(Object p0) { return null; }
    public long getPasswordExpiration(Object p0) { return 0L; }
    public long getPasswordExpirationTimeout(Object p0) { return 0L; }
    public int getPasswordHistoryLength(Object p0) { return 0; }
    public int getPasswordMaximumLength(Object p0) { return 0; }
    public int getPasswordMinimumLength(Object p0) { return 0; }
    public int getPasswordMinimumLetters(Object p0) { return 0; }
    public int getPasswordMinimumLowerCase(Object p0) { return 0; }
    public int getPasswordMinimumNonLetter(Object p0) { return 0; }
    public int getPasswordMinimumNumeric(Object p0) { return 0; }
    public int getPasswordMinimumSymbols(Object p0) { return 0; }
    public int getPasswordMinimumUpperCase(Object p0) { return 0; }
    public int getPasswordQuality(Object p0) { return 0; }
    public int getPermissionGrantState(Object p0, Object p1, Object p2) { return 0; }
    public int getPermissionPolicy(Object p0) { return 0; }
    public int getPersonalAppsSuspendedReasons(Object p0) { return 0; }
    public long getRequiredStrongAuthTimeout(Object p0) { return 0L; }
    public boolean getScreenCaptureDisabled(Object p0) { return false; }
    public Object getSecondaryUsers(Object p0) { return null; }
    public Object getShortSupportMessage(Object p0) { return null; }
    public Object getStartUserSessionMessage(Object p0) { return null; }
    public int getStorageEncryptionStatus() { return 0; }
    public boolean grantKeyPairToApp(Object p0, Object p1, Object p2) { return false; }
    public boolean hasCaCertInstalled(Object p0, Object p1) { return false; }
    public boolean hasGrantedPolicy(Object p0, Object p1) { return false; }
    public boolean hasLockdownAdminConfiguredNetworks(Object p0) { return false; }
    public boolean installCaCert(Object p0, Object p1) { return false; }
    public boolean installExistingPackage(Object p0, Object p1) { return false; }
    public boolean installKeyPair(Object p0, Object p1, Object p2, Object p3) { return false; }
    public boolean installKeyPair(Object p0, Object p1, Object p2, Object p3, Object p4) { return false; }
    public void installSystemUpdate(Object p0, Object p1, Object p2, Object p3) {}
    public boolean isActivePasswordSufficient() { return false; }
    public boolean isAdminActive(Object p0) { return false; }
    public boolean isAffiliatedUser() { return false; }
    public boolean isAlwaysOnVpnLockdownEnabled(Object p0) { return false; }
    public boolean isApplicationHidden(Object p0, Object p1) { return false; }
    public boolean isBackupServiceEnabled(Object p0) { return false; }
    public boolean isCommonCriteriaModeEnabled(Object p0) { return false; }
    public boolean isDeviceIdAttestationSupported() { return false; }
    public boolean isDeviceOwnerApp(Object p0) { return false; }
    public boolean isEphemeralUser(Object p0) { return false; }
    public boolean isLockTaskPermitted(Object p0) { return false; }
    public boolean isLogoutEnabled() { return false; }
    public boolean isManagedProfile(Object p0) { return false; }
    public boolean isMasterVolumeMuted(Object p0) { return false; }
    public boolean isNetworkLoggingEnabled(Object p0) { return false; }
    public boolean isOrganizationOwnedDeviceWithManagedProfile() { return false; }
    public boolean isOverrideApnEnabled(Object p0) { return false; }
    public boolean isPackageSuspended(Object p0, Object p1) { return false; }
    public boolean isProfileOwnerApp(Object p0) { return false; }
    public boolean isProvisioningAllowed(Object p0) { return false; }
    public boolean isResetPasswordTokenActive(Object p0) { return false; }
    public boolean isSecurityLoggingEnabled(Object p0) { return false; }
    public boolean isUninstallBlocked(Object p0, Object p1) { return false; }
    public boolean isUniqueDeviceAttestationSupported() { return false; }
    public boolean isUsingUnifiedPassword(Object p0) { return false; }
    public void lockNow() {}
    public void lockNow(Object p0) {}
    public int logoutUser(Object p0) { return 0; }
    public void reboot(Object p0) {}
    public void removeActiveAdmin(Object p0) {}
    public boolean removeCrossProfileWidgetProvider(Object p0, Object p1) { return false; }
    public boolean removeKeyPair(Object p0, Object p1) { return false; }
    public boolean removeOverrideApn(Object p0, Object p1) { return false; }
    public boolean removeUser(Object p0, Object p1) { return false; }
    public boolean requestBugreport(Object p0) { return false; }
    public boolean resetPasswordWithToken(Object p0, Object p1, Object p2, Object p3) { return false; }
    public boolean revokeKeyPairFromApp(Object p0, Object p1, Object p2) { return false; }
    public void setAccountManagementDisabled(Object p0, Object p1, Object p2) {}
    public void setAffiliationIds(Object p0, Object p1) {}
    public void setAlwaysOnVpnPackage(Object p0, Object p1, Object p2) {}
    public void setAlwaysOnVpnPackage(Object p0, Object p1, Object p2, Object p3) {}
    public boolean setApplicationHidden(Object p0, Object p1, Object p2) { return false; }
    public void setAutoTimeEnabled(Object p0, Object p1) {}
    public void setAutoTimeZoneEnabled(Object p0, Object p1) {}
    public void setBackupServiceEnabled(Object p0, Object p1) {}
    public void setBluetoothContactSharingDisabled(Object p0, Object p1) {}
    public void setCameraDisabled(Object p0, Object p1) {}
    public void setCommonCriteriaModeEnabled(Object p0, Object p1) {}
    public void setConfiguredNetworksLockdownState(Object p0, Object p1) {}
    public void setCrossProfileCalendarPackages(Object p0, Object p1) {}
    public void setCrossProfileCallerIdDisabled(Object p0, Object p1) {}
    public void setCrossProfileContactsSearchDisabled(Object p0, Object p1) {}
    public void setCrossProfilePackages(Object p0, Object p1) {}
    public void setDefaultSmsApplication(Object p0, Object p1) {}
    public void setDelegatedScopes(Object p0, Object p1, Object p2) {}
    public void setDeviceOwnerLockScreenInfo(Object p0, Object p1) {}
    public void setEndUserSessionMessage(Object p0, Object p1) {}
    public void setFactoryResetProtectionPolicy(Object p0, Object p1) {}
    public int setGlobalPrivateDnsModeOpportunistic(Object p0) { return 0; }
    public void setGlobalSetting(Object p0, Object p1, Object p2) {}
    public void setKeepUninstalledPackages(Object p0, Object p1) {}
    public boolean setKeyPairCertificate(Object p0, Object p1, Object p2, Object p3) { return false; }
    public boolean setKeyguardDisabled(Object p0, Object p1) { return false; }
    public void setKeyguardDisabledFeatures(Object p0, Object p1) {}
    public void setLocationEnabled(Object p0, Object p1) {}
    public void setLockTaskFeatures(Object p0, Object p1) {}
    public void setLockTaskPackages(Object p0, Object p1) {}
    public void setLogoutEnabled(Object p0, Object p1) {}
    public void setLongSupportMessage(Object p0, Object p1) {}
    public void setManagedProfileMaximumTimeOff(Object p0, Object p1) {}
    public void setMasterVolumeMuted(Object p0, Object p1) {}
    public void setMaximumFailedPasswordsForWipe(Object p0, Object p1) {}
    public void setMaximumTimeToLock(Object p0, Object p1) {}
    public void setNetworkLoggingEnabled(Object p0, Object p1) {}
    public void setOrganizationColor(Object p0, Object p1) {}
    public void setOrganizationName(Object p0, Object p1) {}
    public void setOverrideApnsEnabled(Object p0, Object p1) {}
    public void setPasswordExpirationTimeout(Object p0, Object p1) {}
    public void setPasswordHistoryLength(Object p0, Object p1) {}
    public void setPasswordMinimumLength(Object p0, Object p1) {}
    public void setPasswordMinimumLetters(Object p0, Object p1) {}
    public void setPasswordMinimumLowerCase(Object p0, Object p1) {}
    public void setPasswordMinimumNonLetter(Object p0, Object p1) {}
    public void setPasswordMinimumNumeric(Object p0, Object p1) {}
    public void setPasswordMinimumSymbols(Object p0, Object p1) {}
    public void setPasswordMinimumUpperCase(Object p0, Object p1) {}
    public void setPasswordQuality(Object p0, Object p1) {}
    public boolean setPermissionGrantState(Object p0, Object p1, Object p2, Object p3) { return false; }
    public void setPermissionPolicy(Object p0, Object p1) {}
    public boolean setPermittedAccessibilityServices(Object p0, Object p1) { return false; }
    public boolean setPermittedCrossProfileNotificationListeners(Object p0, Object p1) { return false; }
    public boolean setPermittedInputMethods(Object p0, Object p1) { return false; }
    public void setPersonalAppsSuspended(Object p0, Object p1) {}
    public void setProfileEnabled(Object p0) {}
    public void setProfileName(Object p0, Object p1) {}
    public void setRecommendedGlobalProxy(Object p0, Object p1) {}
    public void setRequiredStrongAuthTimeout(Object p0, Object p1) {}
    public boolean setResetPasswordToken(Object p0, Object p1) { return false; }
    public void setRestrictionsProvider(Object p0, Object p1) {}
    public void setScreenCaptureDisabled(Object p0, Object p1) {}
    public void setSecureSetting(Object p0, Object p1, Object p2) {}
    public void setSecurityLoggingEnabled(Object p0, Object p1) {}
    public void setShortSupportMessage(Object p0, Object p1) {}
    public void setStartUserSessionMessage(Object p0, Object p1) {}
    public boolean setStatusBarDisabled(Object p0, Object p1) { return false; }
    public void setSystemSetting(Object p0, Object p1, Object p2) {}
    public void setSystemUpdatePolicy(Object p0, Object p1) {}
    public boolean setTime(Object p0, Object p1) { return false; }
    public boolean setTimeZone(Object p0, Object p1) { return false; }
    public void setTrustAgentConfiguration(Object p0, Object p1, Object p2) {}
    public void setUninstallBlocked(Object p0, Object p1, Object p2) {}
    public void setUserControlDisabledPackages(Object p0, Object p1) {}
    public void setUserIcon(Object p0, Object p1) {}
    public int startUserInBackground(Object p0, Object p1) { return 0; }
    public int stopUser(Object p0, Object p1) { return 0; }
    public boolean switchUser(Object p0, Object p1) { return false; }
    public void transferOwnership(Object p0, Object p1, Object p2) {}
    public void uninstallAllUserCaCerts(Object p0) {}
    public void uninstallCaCert(Object p0, Object p1) {}
    public boolean updateOverrideApn(Object p0, Object p1, Object p2) { return false; }
    public void wipeData(Object p0) {}
    public void wipeData(Object p0, Object p1) {}
    public void onInstallUpdateError(Object p0, Object p1) {}
}
