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
    // Real Android context for resource loading (set when running on app_process64)
    private static Object sRealContext;
    protected Context mBase;
    public static void setRealContext(Object ctx) { sRealContext = ctx; }
    public static Object getRealContext() { return sRealContext; }

    // Service name constants (AOSP values)
    public static final String ACCESSIBILITY_SERVICE = "accessibility";
    public static final String ACCOUNT_SERVICE = "account";
    public static final String ACTIVITY_SERVICE = "activity";
    public static final String ALARM_SERVICE = "alarm";
    public static final String APPWIDGET_SERVICE = "appwidget";
    public static final String APP_OPS_SERVICE = "appops";
    public static final String AUDIO_SERVICE = "audio";
    public static final String BATTERY_SERVICE = "batterymanager";
    public static final String BIOMETRIC_SERVICE = "biometric";
    public static final String BLUETOOTH_SERVICE = "bluetooth";
    public static final String CAMERA_SERVICE = "camera";
    public static final String CLIPBOARD_SERVICE = "clipboard";
    public static final String CONNECTIVITY_SERVICE = "connectivity";
    public static final String CONSUMER_IR_SERVICE = "consumer_ir";
    public static final String DEVICE_POLICY_SERVICE = "device_policy";
    public static final String DISPLAY_SERVICE = "display";
    public static final String DOWNLOAD_SERVICE = "download";
    public static final String DROPBOX_SERVICE = "dropbox";
    public static final String INPUT_METHOD_SERVICE = "input_method";
    public static final String INPUT_SERVICE = "input";
    public static final String JOB_SCHEDULER_SERVICE = "jobscheduler";
    public static final String KEYGUARD_SERVICE = "keyguard";
    public static final String LAUNCHER_APPS_SERVICE = "launcherapps";
    public static final String LAYOUT_INFLATER_SERVICE = "layout_inflater";
    public static final String LOCATION_SERVICE = "location";
    public static final String MEDIA_PROJECTION_SERVICE = "media_projection";
    public static final String MEDIA_ROUTER_SERVICE = "media_router";
    public static final String MEDIA_SESSION_SERVICE = "media_session";
    public static final String NETWORK_STATS_SERVICE = "netstats";
    public static final String NFC_SERVICE = "nfc";
    public static final String NOTIFICATION_SERVICE = "notification";
    public static final String NSD_SERVICE = "servicediscovery";
    public static final String POWER_SERVICE = "power";
    public static final String PRINT_SERVICE = "print";
    public static final String SEARCH_SERVICE = "search";
    public static final String SENSOR_SERVICE = "sensor";
    public static final String SHORTCUT_SERVICE = "shortcut";
    public static final String STORAGE_SERVICE = "storage";
    public static final String STORAGE_STATS_SERVICE = "storagestats";
    public static final String TELECOM_SERVICE = "telecom";
    public static final String TELEPHONY_SERVICE = "phone";
    public static final String TELEPHONY_SUBSCRIPTION_SERVICE = "isub";
    public static final String TEXT_CLASSIFICATION_SERVICE = "textclassification";
    public static final String TEXT_SERVICES_MANAGER_SERVICE = "textservices";
    public static final String TV_INPUT_SERVICE = "tv_input";
    public static final String UI_MODE_SERVICE = "uimode";
    public static final String USAGE_STATS_SERVICE = "usagestats";
    public static final String USB_SERVICE = "usb";
    public static final String USER_SERVICE = "user";
    public static final String VIBRATOR_SERVICE = "vibrator";
    public static final String WALLPAPER_SERVICE = "wallpaper";
    public static final String WIFI_AWARE_SERVICE = "wifiaware";
    public static final String WIFI_P2P_SERVICE = "wifip2p";
    public static final String WIFI_RTT_RANGING_SERVICE = "wifirtt";
    public static final String WIFI_SERVICE = "wifi";
    public static final String WINDOW_SERVICE = "window";

    // Bind flags (remain int)
    public static final int BIND_ABOVE_CLIENT = 0x8;
    public static final int BIND_ADJUST_WITH_ACTIVITY = 0x80;
    public static final int BIND_ALLOW_OOM_MANAGEMENT = 0x10;
    public static final int BIND_AUTO_CREATE = 0x1;
    public static final int BIND_DEBUG_UNBIND = 0x2;
    public static final int BIND_EXTERNAL_SERVICE = 0x80000000;
    public static final int BIND_IMPORTANT = 0x40;
    public static final int BIND_INCLUDE_CAPABILITIES = 0x1000;
    public static final int BIND_NOT_FOREGROUND = 0x4;
    public static final int BIND_NOT_PERCEPTIBLE = 0x100;
    public static final int BIND_WAIVE_PRIORITY = 0x20;

    // Context flags (remain int)
    public static final int CONTEXT_IGNORE_SECURITY = 0x2;
    public static final int CONTEXT_INCLUDE_CODE = 0x1;
    public static final int CONTEXT_RESTRICTED = 0x4;
    public static final int RECEIVER_VISIBLE_TO_INSTANT_APPS = 0x1;

    // File mode constants (remain int)
    public static final int MODE_PRIVATE = 0;
    public static final int MODE_APPEND = 0x8000;
    public static final int MODE_ENABLE_WRITE_AHEAD_LOGGING = 0x8;
    public static final int MODE_NO_LOCALIZED_COLLATORS = 0x10;

    public Context() {}

    public Context getBaseContext() { return mBase != null ? mBase : this; }
    public void attachBaseContext(Context base) { mBase = base; }

    public boolean bindIsolatedService(Intent p0, int p1, String p2, Executor p3, ServiceConnection p4) { return false; }
    public boolean bindService(Intent p0, ServiceConnection p1, int p2) {
        return android.app.MiniServer.get().getServiceManager().bindService(p0, p1);
    }
    public boolean bindService(Intent p0, int p1, Executor p2, ServiceConnection p3) {
        return bindService(p0, p3, p1);
    }
    public int checkSelfPermission(String p0) { return PackageManager.PERMISSION_GRANTED; }
    public int checkCallingPermission(String p0) { return PackageManager.PERMISSION_GRANTED; }
    public int checkPermission(String p0, int p1, int p2) { return PackageManager.PERMISSION_GRANTED; }
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
    public Context getApplicationContext() {
        // Return the Application from MiniServer if available
        try {
            android.app.MiniServer server = android.app.MiniServer.get();
            if (server != null) {
                android.app.Application app = server.getApplication();
                if (app != null) return app;
            }
        } catch (Exception e) { /* ignore */ }
        return this;
    }
    public ApplicationInfo getApplicationInfo() {
        if (android.app.HostBridge.hasHost()) {
            String pkg = getPackageName();
            if (pkg != null && !pkg.isEmpty()) {
                try {
                    return getPackageManager().getApplicationInfo(pkg, 0);
                } catch (PackageManager.NameNotFoundException e) {
                    // fall through
                }
            }
        }
        return new ApplicationInfo();
    }
    private AssetManager mAssets;
    public AssetManager getAssets() {
        if (mAssets == null) mAssets = new AssetManager();
        return mAssets;
    }
    private static File sBaseDir;
    private static File ensureBaseDir() {
        if (sBaseDir == null) {
            // Use the westlake data dir or fall back to /data/local/tmp/westlake/files
            String base = System.getProperty("westlake.data.dir", "/data/local/tmp/westlake");
            sBaseDir = new File(base);
            new File(sBaseDir, "files").mkdirs();
            new File(sBaseDir, "cache").mkdirs();
            new File(sBaseDir, "databases").mkdirs();
        }
        return sBaseDir;
    }
    public File getCacheDir() { return new File(ensureBaseDir(), "cache"); }
    public ClassLoader getClassLoader() { return getClass().getClassLoader(); }
    public File getCodeCacheDir() { return new File(ensureBaseDir(), "code_cache"); }
    public ContentResolver getContentResolver() {
        return new ContentResolver(this);
    }
    public File getDataDir() { return ensureBaseDir(); }
    public File getDatabasePath(String p0) { return new File(new File(ensureBaseDir(), "databases"), p0); }
    public File getDir(String p0, int p1) { File d = new File(ensureBaseDir(), "app_" + p0); d.mkdirs(); return d; }
    public File[] getExternalCacheDirs() { return new File[]{ getCacheDir() }; }
    public File[] getExternalFilesDirs(String p0) { return new File[]{ getFilesDir() }; }
    public File getFileStreamPath(String p0) { return new File(getFilesDir(), p0); }
    public File getFilesDir() { return new File(ensureBaseDir(), "files"); }
    public Executor getMainExecutor() {
        return new Executor() {
            @Override public void execute(Runnable r) { r.run(); }
        };
    }
    public Looper getMainLooper() { return Looper.getMainLooper(); }
    public File getNoBackupFilesDir() { return null; }
    public File getObbDir() { return null; }
    public File[] getObbDirs() { return null; }
    public String getPackageCodePath() { return null; }
    private PackageManager mPackageManager;
    public PackageManager getPackageManager() {
        if (mPackageManager == null) mPackageManager = new PackageManager();
        return mPackageManager;
    }
    public String getPackageName() {
        try {
            android.app.MiniServer server = android.app.MiniServer.peek();
            if (server != null) {
                String pkg = server.getPackageName();
                if (pkg != null && !pkg.isEmpty()) return pkg;
            }
        } catch (Throwable ignored) {}
        try {
            String pkg = android.app.MiniServer.currentPackageName();
            if (pkg != null && !pkg.isEmpty()) return pkg;
        } catch (Throwable ignored) {}
        try {
            String pkg = System.getProperty("westlake.apk.package");
            if (pkg != null && !pkg.isEmpty()) return pkg;
        } catch (Throwable ignored) {}
        try {
            if (android.app.HostBridge.hasHost()) {
                String hostPkg = android.app.HostBridge.getHostPackageName();
                if (hostPkg != null && !hostPkg.isEmpty()) return hostPkg;
            }
        } catch (Throwable ignored) {
        }
        return "";
    }
    public String getPackageResourcePath() { return null; }
    private Resources mResources;
    public Resources getResources() {
        if (mResources == null) mResources = new Resources();
        return mResources;
    }
    public SharedPreferences getSharedPreferences(String p0, int p1) {
        // Delegate to host's real SharedPreferences via HostBridge
        if (android.app.HostBridge.hasHost()) {
            Object sp = android.app.HostBridge.call("getSharedPreferences",
                new Class[]{String.class, int.class}, p0, p1);
            if (sp != null) {
                // Wrap the host's real SP in our shim SP (copies data)
                SharedPreferences shimSp = SharedPreferencesImpl.getInstance(p0);
                // Copy values from host SP to shim SP via reflection
                try {
                    java.util.Map<String, ?> all = (java.util.Map<String, ?>)
                        sp.getClass().getMethod("getAll").invoke(sp);
                    if (all != null) {
                        SharedPreferences.Editor editor = shimSp.edit();
                        for (java.util.Map.Entry<String, ?> entry : all.entrySet()) {
                            Object v = entry.getValue();
                            if (v instanceof String) editor.putString(entry.getKey(), (String) v);
                            else if (v instanceof Integer) editor.putInt(entry.getKey(), (Integer) v);
                            else if (v instanceof Boolean) editor.putBoolean(entry.getKey(), (Boolean) v);
                            else if (v instanceof Long) editor.putLong(entry.getKey(), (Long) v);
                            else if (v instanceof Float) editor.putFloat(entry.getKey(), (Float) v);
                        }
                        editor.apply();
                    }
                } catch (Exception e) {}
                return shimSp;
            }
        }
        return SharedPreferencesImpl.getInstance(p0);
    }
    public Object getSystemService(String p0) {
        if (LAYOUT_INFLATER_SERVICE.equals(p0)) {
            return new android.view.LayoutInflater(this);
        }
        // Try host's real system services first (provides real WindowManager, LayoutInflater, etc.)
        if (android.app.HostBridge.hasHost()) {
            Object hostService = android.app.HostBridge.getHostSystemService(p0);
            if (hostService != null) return hostService;
        }
        Object svc = android.app.SystemServiceRegistry.getService(p0);
        return svc;
    }
    @SuppressWarnings("unchecked")
    public <T> T getSystemService(Class<T> serviceClass) {
        return (T) getSystemService(serviceClass.getSimpleName());
    }
    public void grantUriPermission(String p0, Uri p1, int p2) {}
    public boolean isDeviceProtectedStorage() { return false; }
    public boolean isRestricted() { return false; }
    public boolean moveDatabaseFrom(Context p0, String p1) { return false; }
    public boolean moveSharedPreferencesFrom(Context p0, String p1) { return false; }
    public java.io.FileInputStream openFileInput(String p0) throws java.io.FileNotFoundException {
        return new java.io.FileInputStream(new java.io.File(getFilesDir(), p0));
    }
    public java.io.FileOutputStream openFileOutput(String p0, int p1) throws java.io.FileNotFoundException {
        return new java.io.FileOutputStream(new java.io.File(getFilesDir(), p0), (p1 & 0x8000) != 0);
    }
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
    public void startActivities(Intent[] p0) {
        if (p0 != null) {
            for (Intent intent : p0) startActivity(intent);
        }
    }
    public void startActivities(Intent[] p0, Bundle p1) { startActivities(p0); }
    public void startActivity(Intent p0) {
        android.app.MiniServer.get().startActivity(p0);
    }
    public void startActivity(Intent p0, Bundle p1) { startActivity(p0); }
    public boolean startInstrumentation(ComponentName p0, String p1, Bundle p2) { return false; }
    public void startIntentSender(IntentSender p0, Intent p1, int p2, int p3, int p4) {}
    public void startIntentSender(IntentSender p0, Intent p1, int p2, int p3, int p4, Bundle p5) {}
    public ComponentName startService(Intent p0) {
        return android.app.MiniServer.get().getServiceManager().startService(p0);
    }
    public boolean stopService(Intent p0) {
        return android.app.MiniServer.get().getServiceManager().stopService(p0);
    }
    public void unbindService(ServiceConnection p0) {
        android.app.MiniServer.get().getServiceManager().unbindService(p0);
    }
    public void unregisterComponentCallbacks(ComponentCallbacks p0) {}
    public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter) {
        return null;
    }
    public void unregisterReceiver(BroadcastReceiver p0) {}
    public void updateServiceGroup(ServiceConnection p0, int p1, int p2) {}

    public android.content.res.TypedArray obtainStyledAttributes(android.util.AttributeSet set, int[] attrs) {
        if (android.app.HostBridge.hasHost()) {
            return android.app.HostBridge.host_obtainStyledAttributes(attrs);
        }
        return new android.content.res.TypedArray();
    }
    public android.content.res.TypedArray obtainStyledAttributes(android.util.AttributeSet set, int[] attrs, int defStyleAttr, int defStyleRes) {
        if (android.app.HostBridge.hasHost()) {
            return android.app.HostBridge.host_obtainStyledAttributes(attrs);
        }
        return new android.content.res.TypedArray();
    }
    public android.content.res.TypedArray obtainStyledAttributes(int resId, int[] attrs) {
        if (android.app.HostBridge.hasHost()) {
            return android.app.HostBridge.host_obtainStyledAttributes(resId, attrs);
        }
        return new android.content.res.TypedArray();
    }
    public android.content.res.TypedArray obtainStyledAttributes(int[] attrs) {
        if (android.app.HostBridge.hasHost()) {
            return android.app.HostBridge.host_obtainStyledAttributes(attrs);
        }
        return new android.content.res.TypedArray();
    }

    public android.graphics.drawable.Drawable getDrawable(int id) {
        // Try real Android context first (app_process64 mode)
        Object realCtx = com.westlake.engine.WestlakeLauncher.sRealContext;
        if (com.westlake.engine.WestlakeLauncher.isRealFrameworkFallbackAllowed() && realCtx != null) {
            try {
                java.lang.reflect.Method m = realCtx.getClass().getMethod("getDrawable", int.class);
                Object d = m.invoke(realCtx, id);
                if (d instanceof android.graphics.drawable.Drawable) return (android.graphics.drawable.Drawable) d;
            } catch (Throwable t) { /* fall through to shim */ }
        }
        // Shim fallback: load from resource table + file
        // Check BOTH activity's and application's resource tables
        android.content.res.Resources res = getResources();
        android.content.res.ResourceTable table = null;
        if (res != null) table = res.getResourceTable();
        // Also try MiniServer's application Resources (has merged split tables)
        if (table == null || table.getString(id) == null) {
            try {
                android.content.res.Resources appRes = android.app.MiniServer.get().getApplication().getResources();
                if (appRes != null && appRes.getResourceTable() != null) {
                    android.content.res.ResourceTable appTable = appRes.getResourceTable();
                    if (appTable.getString(id) != null || appTable.getEntryFilePath(id) != null) {
                        table = appTable;
                    }
                }
            } catch (Throwable t) {}
        }
        if (table != null) {
            if (table != null) {
                String filePath = table.getString(id);
                if (filePath == null) filePath = table.getEntryFilePath(id);
                if (filePath != null) {
                    String resDir = System.getProperty("westlake.apk.resdir");
                    if (resDir != null) {
                        java.io.File f = new java.io.File(resDir, filePath);
                        if (f.exists()) {
                            if (filePath.endsWith(".png") || filePath.endsWith(".webp") || filePath.endsWith(".jpg") || filePath.endsWith(".9.png")) {
                                try {
                                    android.graphics.Bitmap bmp = android.graphics.BitmapFactory.decodeFile(f.getAbsolutePath());
                                    if (bmp != null) {
                                        return new android.graphics.drawable.BitmapDrawable(res, bmp);
                                    }
                                } catch (Throwable t) { /* fall through */ }
                            }
                        }
                    }
                }
            }
        }
        return new android.graphics.drawable.ColorDrawable(0);
    }
    public android.content.res.Resources.Theme getTheme() { return new android.content.res.Resources.Theme(); }
    public boolean isAutofillCompatibilityEnabled() { return false; }
    public AutofillOptions getAutofillOptions() { return null; }
    public Object getContentCaptureOptions() { return null; }
    public int getNextAutofillId() { return 0; }
    public void startActivityForResult(String who, android.content.Intent intent, int requestCode, android.os.Bundle options) {}
    public boolean canStartActivityForResult() { return false; }
    public Context createPackageContextAsUser(String packageName, int flags, android.os.UserHandle user) throws android.content.pm.PackageManager.NameNotFoundException { return this; }
    public void startActivityAsUser(android.content.Intent intent, android.os.UserHandle user) {}
    public boolean canLoadUnsafeResources() { return false; }

    public final CharSequence getText(int resId) { return ""; }
    public final String getString(int resId) { return ""; }
    public final String getString(int resId, Object... formatArgs) { return ""; }
    public final int getColor(int id) { return 0; }
    public android.view.Display getDisplay() { return null; }
    public int getUserId() { return 0; }
    public int getDisplayId() { return 0; }
    public String getOpPackageName() { return getPackageName(); }
    public boolean isUiContext() { return true; }
    public android.content.Intent registerReceiverAsUser(
            android.content.BroadcastReceiver receiver, android.os.UserHandle user,
            android.content.IntentFilter filter, String broadcastPermission,
            android.os.Handler scheduler) { return null; }
    public android.content.res.ColorStateList getColorStateList(int id) {
        return android.content.res.ColorStateList.valueOf(0);
    }
    public int getThemeResId() { return 0; }
}
