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
import android.os.UserHandle;
import android.view.Display;
import java.io.File;

public class ContextWrapper extends Context {
    public ContextWrapper(Context p0) {}

    public void attachBaseContext(Context p0) {}
    public boolean bindService(Intent p0, ServiceConnection p1, int p2) { return super.bindService(p0, p1, p2); }
    public int checkCallingOrSelfPermission(String p0) { return 0; }
    public int checkCallingOrSelfUriPermission(Uri p0, int p1) { return 0; }
    public int checkCallingPermission(String p0) { return 0; }
    public int checkCallingUriPermission(Uri p0, int p1) { return 0; }
    public int checkPermission(String p0, int p1, int p2) { return 0; }
    public int checkSelfPermission(String p0) { return 0; }
    public int checkUriPermission(Uri p0, int p1, int p2, int p3) { return 0; }
    public int checkUriPermission(Uri p0, String p1, String p2, int p3, int p4, int p5) { return 0; }
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
    public Context getBaseContext() { return null; }
    public File getCacheDir() { return null; }
    public ClassLoader getClassLoader() { return null; }
    public File getCodeCacheDir() { return null; }
    public ContentResolver getContentResolver() { return null; }
    public File getDataDir() { return null; }
    public File getDatabasePath(String p0) { return null; }
    public File getDir(String p0, int p1) { return null; }
    public File getExternalCacheDir() { return null; }
    public File[] getExternalCacheDirs() { return null; }
    public File getExternalFilesDir(String p0) { return null; }
    public File[] getExternalFilesDirs(String p0) { return null; }
    public File[] getExternalMediaDirs() { return null; }
    public File getFileStreamPath(String p0) { return null; }
    public File getFilesDir() { return null; }
    public Looper getMainLooper() { return null; }
    public File getNoBackupFilesDir() { return null; }
    public File getObbDir() { return null; }
    public File[] getObbDirs() { return null; }
    public String getPackageCodePath() { return null; }
    public PackageManager getPackageManager() { return null; }
    public String getPackageName() { return null; }
    public String getPackageResourcePath() { return null; }
    public Resources getResources() { return super.getResources(); }
    public SharedPreferences getSharedPreferences(String p0, int p1) { return null; }
    public Object getSystemService(String p0) { return super.getSystemService(p0); }
    public String getSystemServiceName(Object p0) { return null; }
    public android.content.res.Resources.Theme getTheme() { return super.getTheme(); }
    public void grantUriPermission(String p0, Uri p1, int p2) {}
    public boolean isDeviceProtectedStorage() { return false; }
    public boolean moveDatabaseFrom(Context p0, String p1) { return false; }
    public boolean moveSharedPreferencesFrom(Context p0, String p1) { return false; }
    public java.io.FileInputStream openFileInput(String p0) { return null; }
    public java.io.FileOutputStream openFileOutput(String p0, int p1) { return null; }
    public SQLiteDatabase openOrCreateDatabase(String p0, int p1, Object p2) { return null; }
    public SQLiteDatabase openOrCreateDatabase(String p0, int p1, Object p2, DatabaseErrorHandler p3) { return null; }
    public Intent registerReceiver(BroadcastReceiver p0, IntentFilter p1) { return null; }
    public Intent registerReceiver(BroadcastReceiver p0, IntentFilter p1, int p2) { return null; }
    public Intent registerReceiver(BroadcastReceiver p0, IntentFilter p1, String p2, Handler p3) { return null; }
    public Intent registerReceiver(BroadcastReceiver p0, IntentFilter p1, String p2, Handler p3, int p4) { return null; }
    public void revokeUriPermission(Uri p0, int p1) {}
    public void revokeUriPermission(String p0, Uri p1, int p2) {}
    public void sendBroadcast(Intent p0) {}
    public void sendBroadcast(Intent p0, String p1) {}
    public void sendBroadcastAsUser(Intent p0, UserHandle p1) {}
    public void sendBroadcastAsUser(Intent p0, UserHandle p1, String p2) {}
    public void sendOrderedBroadcast(Intent p0, String p1) {}
    public void sendOrderedBroadcast(Intent p0, String p1, BroadcastReceiver p2, Handler p3, int p4, String p5, Bundle p6) {}
    public void sendOrderedBroadcast(Intent p0, int p1, String p2, String p3, BroadcastReceiver p4, Handler p5, String p6, Bundle p7, Bundle p8) {}
    public void sendOrderedBroadcastAsUser(Intent p0, UserHandle p1, String p2, BroadcastReceiver p3, Handler p4, int p5, String p6, Bundle p7) {}
    public void setTheme(int p0) {}
    public void startActivities(Intent[] p0) {}
    public void startActivities(Intent[] p0, Bundle p1) {}
    public void startActivity(Intent p0) { super.startActivity(p0); }
    public void startActivity(Intent p0, Bundle p1) { super.startActivity(p0, p1); }
    public ComponentName startForegroundService(Intent p0) { return null; }
    public boolean startInstrumentation(ComponentName p0, String p1, Bundle p2) { return false; }
    public void startIntentSender(IntentSender p0, Intent p1, int p2, int p3, int p4) {}
    public void startIntentSender(IntentSender p0, Intent p1, int p2, int p3, int p4, Bundle p5) {}
    public ComponentName startService(Intent p0) { return super.startService(p0); }
    public boolean stopService(Intent p0) { return super.stopService(p0); }
    public void unbindService(ServiceConnection p0) { super.unbindService(p0); }
    public void unregisterReceiver(BroadcastReceiver p0) {}
}
