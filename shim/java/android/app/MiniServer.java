package android.app;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.MiniPackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import java.io.IOException;

/**
 * MiniServer — replaces Android's SystemServer for single-app engine execution.
 *
 * Instead of 80+ system services communicating via Binder IPC, this is a single
 * in-process Java object that manages one app's Activity stack, window, package info,
 * and service lifecycle.
 *
 * Usage:
 *   MiniServer.init(packageName);
 *   MiniServer.get().startActivity(launcherIntent);
 */
public class MiniServer {
    private static MiniServer sInstance;

    private final MiniActivityManager mActivityManager;
    private final MiniServiceManager mServiceManager;
    private final MiniPackageManager mPackageManager;
    private Application mApplication;
    private String mPackageName;
    private ApkInfo mApkInfo;

    private MiniServer(String packageName) {
        mPackageName = packageName;
        mApplication = new Application();
        ShimCompat.setPackageName(mApplication, packageName);
        mActivityManager = new MiniActivityManager(this);
        mServiceManager = new MiniServiceManager(this);
        mPackageManager = new MiniPackageManager(packageName);
        try {
            SystemServiceRegistry.init();
            // Register LayoutInflater as a system service
            SystemServiceRegistry.registerService(Context.LAYOUT_INFLATER_SERVICE,
                    new LayoutInflater(mApplication));
        } catch (NoSuchMethodError | NoClassDefFoundError e) {
            // On real Android, SystemServiceRegistry is already initialized
            android.util.Log.w("MiniServer", "SystemServiceRegistry.init() skipped: " + e.getClass().getSimpleName());
        }
    }

    /** Initialize the MiniServer singleton. Call once at engine startup. */
    public static void init(String packageName) {
        sInstance = new MiniServer(packageName);
        sInstance.mApplication.onCreate();
    }

    /** Get the singleton instance. */
    public static MiniServer get() {
        if (sInstance == null) {
            // Auto-init with default package for testing
            init("com.example.app");
        }
        return sInstance;
    }

    public MiniActivityManager getActivityManager() { return mActivityManager; }
    public MiniServiceManager getServiceManager() { return mServiceManager; }
    public MiniPackageManager getPackageManager() { return mPackageManager; }
    public Application getApplication() { return mApplication; }
    public void setApplication(Application app) { mApplication = app; }
    public String getPackageName() { return mPackageName; }
    public ApkInfo getApkInfo() { return mApkInfo; }

    /**
     * Start an Activity by class name (convenience for testing).
     * Creates an explicit Intent and delegates to MiniActivityManager.
     */
    public void startActivity(String activityClassName) {
        try {
            Class<?> cls = Class.forName(activityClassName);
            Intent intent = new Intent();
            intent.setComponent(new ComponentName(mPackageName, activityClassName));
            mActivityManager.startActivity(null, intent, -1);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Activity class not found: " + activityClassName, e);
        }
    }

    /**
     * Start an Activity from an Intent.
     * Handles implicit intent resolution via MiniPackageManager.
     */
    public void startActivity(Intent intent) {
        // If no component set, try implicit resolution
        if (intent.getComponent() == null) {
            android.content.pm.ResolveInfo ri = mPackageManager.resolveActivity(intent);
            if (ri != null && ri.resolvedComponentName != null) {
                intent.setComponent(ri.resolvedComponentName);
            }
        }
        mActivityManager.startActivity(null, intent, -1);
    }

    /**
     * Load an APK: extract DEX files, parse manifest, register activities/services.
     * After this, startActivity() with the launcher intent will work.
     */
    public ApkInfo loadApk(String apkPath) throws IOException {
        ApkInfo info = ApkLoader.load(apkPath);
        mApkInfo = info;

        // Update package info
        mPackageName = info.packageName;

        // If manifest declares a custom Application class, instantiate it
        if (info.applicationClassName != null) {
            try {
                Class<?> appCls = Class.forName(info.applicationClassName);
                mApplication = (Application) appCls.newInstance();
            } catch (Exception e) {
                // fallback to default Application
            }
        }
        ShimCompat.setPackageName(mApplication, info.packageName);

        // Wire resources from parsed resources.arsc
        if (info.resourceTable instanceof android.content.res.ResourceTable) {
            ShimCompat.loadResourceTable(mApplication.getResources(),
                    (android.content.res.ResourceTable) info.resourceTable);
        }

        // Wire assets from extracted assets/ directory
        if (info.assetDir != null) {
            ShimCompat.setAssetDir(mApplication.getAssets(), info.assetDir);
        }

        // Set native lib path for System.loadLibrary()
        if (info.nativeLibDir != null) {
            System.setProperty("app.native.lib.dir", info.nativeLibDir);
        }

        // Register all activities from manifest
        for (String activityName : info.activities) {
            mPackageManager.addActivity(activityName);
        }

        // Register launcher activity with MAIN/LAUNCHER filter
        if (info.launcherActivity != null) {
            IntentFilter launcherFilter = new IntentFilter(Intent.ACTION_MAIN);
            launcherFilter.addCategory(Intent.CATEGORY_LAUNCHER);
            mPackageManager.addActivity(info.launcherActivity, launcherFilter);
        }

        // Register services
        for (String serviceName : info.services) {
            mPackageManager.addService(serviceName);
        }

        // Call Application.onCreate after all wiring
        mApplication.onCreate();

        return info;
    }

    /**
     * Load APK and launch the main activity.
     */
    public void loadAndLaunch(String apkPath) throws IOException {
        ApkInfo info = loadApk(apkPath);
        if (info.launcherActivity != null) {
            startActivity(info.launcherActivity);
        } else if (!info.activities.isEmpty()) {
            startActivity(info.activities.get(0));
        }
    }

    /** Shut down: destroy all services and activities, call Application.onTerminate(). */
    public void shutdown() {
        mServiceManager.stopAll();
        mActivityManager.finishAll();
        mApplication.onTerminate();
    }
}
