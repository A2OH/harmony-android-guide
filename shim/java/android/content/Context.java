package android.content;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Display;
import java.io.File;
import java.util.concurrent.Executor;

public class Context {
    public static final int ACCESSIBILITY_SERVICE = 0;
    public static final int ACCOUNT_SERVICE = 0;
    public static final int ACTIVITY_SERVICE = 0;
    public static final int ALARM_SERVICE = 0;
    public static final int APPWIDGET_SERVICE = 0;
    public static final int APP_OPS_SERVICE = 0;
    public static final int AUDIO_SERVICE = 0;
    public static final int BATTERY_SERVICE = 0;
    public static final int BIND_ABOVE_CLIENT = 0;
    public static final int BIND_ADJUST_WITH_ACTIVITY = 0;
    public static final int BIND_ALLOW_OOM_MANAGEMENT = 0;
    public static final int BIND_AUTO_CREATE = 0;
    public static final int BIND_DEBUG_UNBIND = 0;
    public static final int BIND_EXTERNAL_SERVICE = 0;
    public static final int BIND_IMPORTANT = 0;
    public static final int BIND_INCLUDE_CAPABILITIES = 0;
    public static final int BIND_NOT_FOREGROUND = 0;
    public static final int BIND_NOT_PERCEPTIBLE = 0;
    public static final int BIND_WAIVE_PRIORITY = 0;
    public static final int BIOMETRIC_SERVICE = 0;
    public static final int BLOB_STORE_SERVICE = 0;
    public static final int BLUETOOTH_SERVICE = 0;
    public static final int CAMERA_SERVICE = 0;
    public static final int CAPTIONING_SERVICE = 0;
    public static final int CARRIER_CONFIG_SERVICE = 0;
    public static final int CLIPBOARD_SERVICE = 0;
    public static final int COMPANION_DEVICE_SERVICE = 0;
    public static final int CONNECTIVITY_DIAGNOSTICS_SERVICE = 0;
    public static final int CONNECTIVITY_SERVICE = 0;
    public static final int CONSUMER_IR_SERVICE = 0;
    public static final int CONTEXT_IGNORE_SECURITY = 0;
    public static final int CONTEXT_INCLUDE_CODE = 0;
    public static final int CONTEXT_RESTRICTED = 0;
    public static final int CROSS_PROFILE_APPS_SERVICE = 0;
    public static final int DEVICE_POLICY_SERVICE = 0;
    public static final int DISPLAY_SERVICE = 0;
    public static final int DOWNLOAD_SERVICE = 0;
    public static final int DROPBOX_SERVICE = 0;
    public static final int EUICC_SERVICE = 0;
    public static final int FILE_INTEGRITY_SERVICE = 0;
    public static final int FINGERPRINT_SERVICE = 0;
    public static final int HARDWARE_PROPERTIES_SERVICE = 0;
    public static final int INPUT_METHOD_SERVICE = 0;
    public static final int INPUT_SERVICE = 0;
    public static final int IPSEC_SERVICE = 0;
    public static final int JOB_SCHEDULER_SERVICE = 0;
    public static final int KEYGUARD_SERVICE = 0;
    public static final int LAUNCHER_APPS_SERVICE = 0;
    public static final int LAYOUT_INFLATER_SERVICE = 0;
    public static final int LOCATION_SERVICE = 0;
    public static final int MEDIA_PROJECTION_SERVICE = 0;
    public static final int MEDIA_ROUTER_SERVICE = 0;
    public static final int MEDIA_SESSION_SERVICE = 0;
    public static final int MIDI_SERVICE = 0;
    public static final int MODE_APPEND = 0;
    public static final int MODE_ENABLE_WRITE_AHEAD_LOGGING = 0;
    public static final int MODE_NO_LOCALIZED_COLLATORS = 0;
    public static final int MODE_PRIVATE = 0;
    public static final int NETWORK_STATS_SERVICE = 0;
    public static final int NFC_SERVICE = 0;
    public static final int NOTIFICATION_SERVICE = 0;
    public static final int NSD_SERVICE = 0;
    public static final int POWER_SERVICE = 0;
    public static final int PRINT_SERVICE = 0;
    public static final int RECEIVER_VISIBLE_TO_INSTANT_APPS = 0;
    public static final int RESTRICTIONS_SERVICE = 0;
    public static final int ROLE_SERVICE = 0;
    public static final int SEARCH_SERVICE = 0;
    public static final int SENSOR_SERVICE = 0;
    public static final int SHORTCUT_SERVICE = 0;
    public static final int STORAGE_SERVICE = 0;
    public static final int STORAGE_STATS_SERVICE = 0;
    public static final int SYSTEM_HEALTH_SERVICE = 0;
    public static final int TELECOM_SERVICE = 0;
    public static final int TELEPHONY_IMS_SERVICE = 0;
    public static final int TELEPHONY_SERVICE = 0;
    public static final int TELEPHONY_SUBSCRIPTION_SERVICE = 0;
    public static final int TEXT_CLASSIFICATION_SERVICE = 0;
    public static final int TEXT_SERVICES_MANAGER_SERVICE = 0;
    public static final int TV_INPUT_SERVICE = 0;
    public static final int UI_MODE_SERVICE = 0;
    public static final int USAGE_STATS_SERVICE = 0;
    public static final int USB_SERVICE = 0;
    public static final int USER_SERVICE = 0;
    public static final int VIBRATOR_SERVICE = 0;
    public static final int VPN_MANAGEMENT_SERVICE = 0;
    public static final int WALLPAPER_SERVICE = 0;
    public static final int WIFI_AWARE_SERVICE = 0;
    public static final int WIFI_P2P_SERVICE = 0;
    public static final int WIFI_RTT_RANGING_SERVICE = 0;
    public static final int WIFI_SERVICE = 0;
    public static final int WINDOW_SERVICE = 0;

    public Context() {}

    public boolean bindIsolatedService(Intent p0, int p1, String p2, Executor p3, ServiceConnection p4) { return false; }
    public boolean bindService(Intent p0, ServiceConnection p1, int p2) { return false; }
    public boolean bindService(Intent p0, int p1, Executor p2, ServiceConnection p3) { return false; }
    public int checkSelfPermission(String p0) { return 0; }
    public Context createConfigurationContext(Configuration p0) { return null; }
    public Context createContextForSplit(String p0) { return null; }
    public Context createDeviceProtectedStorageContext() { return null; }
    public Context createDisplayContext(Display p0) { return null; }
    public Context createPackageContext(String p0, int p1) { return null; }
    public String[] databaseList() { return null; }
    public boolean deleteDatabase(String p0) { return false; }
    public boolean deleteFile(String p0) { return false; }
    public boolean deleteSharedPreferences(String p0) { return false; }
    public void enforceCallingOrSelfPermission(String p0, String p1) {}
    public void enforceCallingOrSelfUriPermission(Uri p0, int p1, String p2) {}
    public void enforceCallingPermission(String p0, String p1) {}
    public void enforceCallingUriPermission(Uri p0, int p1, String p2) {}
    public void enforcePermission(String p0, int p1, int p2, String p3) {}
    public void enforceUriPermission(Uri p0, int p1, int p2, int p3, String p4) {}
    public void enforceUriPermission(Uri p0, String p1, String p2, int p3, int p4, int p5, String p6) {}
    public String[] fileList() { return null; }
    public Context getApplicationContext() { return null; }
    public ApplicationInfo getApplicationInfo() { return null; }
    public AssetManager getAssets() { return null; }
    public File getCacheDir() { return null; }
    public ClassLoader getClassLoader() { return null; }
    public File getCodeCacheDir() { return null; }
    public ContentResolver getContentResolver() { return null; }
    public File getDataDir() { return null; }
    public File getDatabasePath(String p0) { return null; }
    public File getDir(String p0, int p1) { return null; }
    public File[] getExternalCacheDirs() { return null; }
    public File[] getExternalFilesDirs(String p0) { return null; }
    public File getFileStreamPath(String p0) { return null; }
    public File getFilesDir() { return null; }
    public Executor getMainExecutor() { return null; }
    public Looper getMainLooper() { return null; }
    public File getNoBackupFilesDir() { return null; }
    public File getObbDir() { return null; }
    public File[] getObbDirs() { return null; }
    public String getPackageCodePath() { return null; }
    public PackageManager getPackageManager() { return null; }
    public String getPackageName() { return null; }
    public String getPackageResourcePath() { return null; }
    public Resources getResources() { return null; }
    public SharedPreferences getSharedPreferences(String p0, int p1) { return new SharedPreferences(p0); }
    public Object getSystemService(String p0) { return null; }
    public void grantUriPermission(String p0, Uri p1, int p2) {}
    public boolean isDeviceProtectedStorage() { return false; }
    public boolean isRestricted() { return false; }
    public boolean moveDatabaseFrom(Context p0, String p1) { return false; }
    public boolean moveSharedPreferencesFrom(Context p0, String p1) { return false; }
    public java.io.FileInputStream openFileInput(String p0) { return null; }
    public java.io.FileOutputStream openFileOutput(String p0, int p1) { return null; }
    public SQLiteDatabase openOrCreateDatabase(String p0, int p1, Object p2) { return null; }
    public SQLiteDatabase openOrCreateDatabase(String p0, int p1, Object p2, DatabaseErrorHandler p3) { return null; }
    public void registerComponentCallbacks(ComponentCallbacks p0) {}
    public void revokeUriPermission(Uri p0, int p1) {}
    public void revokeUriPermission(String p0, Uri p1, int p2) {}
    public void sendBroadcast(Intent p0) {}
    public void sendBroadcast(Intent p0, String p1) {}
    public void sendBroadcastWithMultiplePermissions(Intent p0, String[] p1) {}
    public void sendOrderedBroadcast(Intent p0, String p1) {}
    public void sendOrderedBroadcast(Intent p0, String p1, BroadcastReceiver p2, Handler p3, int p4, String p5, Bundle p6) {}
    public void sendOrderedBroadcast(Intent p0, String p1, String p2, BroadcastReceiver p3, Handler p4, int p5, String p6, Bundle p7) {}
    public void setTheme(int p0) {}
    public void startActivities(Intent[] p0) {}
    public void startActivities(Intent[] p0, Bundle p1) {}
    public void startActivity(Intent p0) {}
    public void startActivity(Intent p0, Bundle p1) {}
    public boolean startInstrumentation(ComponentName p0, String p1, Bundle p2) { return false; }
    public void startIntentSender(IntentSender p0, Intent p1, int p2, int p3, int p4) {}
    public void startIntentSender(IntentSender p0, Intent p1, int p2, int p3, int p4, Bundle p5) {}
    public boolean stopService(Intent p0) { return false; }
    public void unbindService(ServiceConnection p0) {}
    public void unregisterComponentCallbacks(ComponentCallbacks p0) {}
    public void unregisterReceiver(BroadcastReceiver p0) {}
    public void updateServiceGroup(ServiceConnection p0, int p1, int p2) {}
}
