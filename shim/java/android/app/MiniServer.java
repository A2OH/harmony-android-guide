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
        // CR59 (2026-05-14): bare `new Application()` has mBase=null
        // (super(null) in shim ctor).  Any caller that walks the
        // ContextWrapper chain through this placeholder (e.g.
        // `app.getApplicationContext()` -> `mBase.getApplicationContext()`)
        // will NPE.  Attach a default-shaped WestlakeContextImpl base
        // context so the placeholder Application is at least
        // chain-walkable.  Generic, no per-app branches.  Uses the same
        // Application.attach(Context) helper the production
        // makeApplication path uses.  Anti-drift compliant: no Unsafe,
        // no setAccessible, no new methods on WestlakeContextImpl, no
        // per-app paths.
        try {
            com.westlake.services.WestlakeContextImpl baseContext =
                    new com.westlake.services.WestlakeContextImpl(
                            packageName != null && !packageName.isEmpty()
                                    ? packageName : "com.example.app",
                            /*apkPath*/ null,
                            /*dataDir*/ null,
                            /*classLoader*/ getClass().getClassLoader(),
                            /*targetSdk*/ 33);
            baseContext.setAttachedApplication(mApplication);
            mApplication.attach(baseContext);
        } catch (Throwable ignored) {
            // Best-effort. If WestlakeContextImpl ctor fails for any
            // reason, leave mApplication with null mBase — matches
            // pre-CR59 behavior so we never regress sandboxed callers.
        }
        ShimCompat.setPackageName(mApplication, packageName);
        mActivityManager = new MiniActivityManager(this);
        mServiceManager = new MiniServiceManager(this);
        mPackageManager = new MiniPackageManager(packageName);
        try {
            SystemServiceRegistry.init();
        } catch (NoSuchMethodError | NoClassDefFoundError e) {
            // On real Android, SystemServiceRegistry is already initialized.
            // Avoid eager logging here; standalone bootstrap still has fragile
            // caller-sensitive paths before the server is fully constructed.
        }
    }

    /** Initialize the MiniServer singleton. Call once at engine startup. */
    public static MiniServer init(String packageName) {
        MiniServer instance = new MiniServer(packageName);
        sInstance = instance;
        instance.mApplication.onCreate();
        return instance;
    }

    /** Get the singleton instance. */
    public static MiniServer get() {
        if (sInstance == null) {
            // Auto-init with default package for testing.
            return init("com.example.app");
        }
        return sInstance;
    }

    /** Return the current singleton without auto-initializing it. */
    public static MiniServer peek() {
        return sInstance;
    }

    public static String currentPackageName() {
        if (sInstance == null) {
            return null;
        }
        return sInstance.mPackageName;
    }

    public static MiniActivityManager currentActivityManager() {
        return sInstance != null ? sInstance.mActivityManager : null;
    }

    public static Application currentApplication() {
        return sInstance != null ? sInstance.mApplication : null;
    }

    public static void currentSetApplication(Application app) {
        if (sInstance == null) {
            return;
        }
        if (app != null) {
            sInstance.mApplication = app;
        } else {
            // CR59 (2026-05-14): match the ctor's CR59 fixup so we
            // never publish a bare-mBase=null Application via this
            // public re-set API.
            Application placeholder = new Application();
            try {
                com.westlake.services.WestlakeContextImpl baseContext =
                        new com.westlake.services.WestlakeContextImpl(
                                sInstance.mPackageName != null
                                        && !sInstance.mPackageName.isEmpty()
                                        ? sInstance.mPackageName
                                        : "com.example.app",
                                /*apkPath*/ null,
                                /*dataDir*/ null,
                                /*classLoader*/ sInstance.getClass().getClassLoader(),
                                /*targetSdk*/ 33);
                baseContext.setAttachedApplication(placeholder);
                placeholder.attach(baseContext);
            } catch (Throwable ignored) {}
            sInstance.mApplication = placeholder;
        }
        ShimCompat.setPackageName(sInstance.mApplication, sInstance.mPackageName);
    }

    public static void currentSetPackageName(String packageName) {
        if (sInstance == null || packageName == null || packageName.isEmpty()) {
            return;
        }
        sInstance.mPackageName = packageName;
        if (sInstance.mApplication != null) {
            ShimCompat.setPackageName(sInstance.mApplication, packageName);
        }
        if (sInstance.mPackageManager != null) {
            sInstance.mPackageManager.setPackageName(packageName);
        }
    }

    public static ApkInfo currentLoadApk(String apkPath) throws IOException {
        MiniServer server = get();
        if (server == null) {
            throw new IllegalStateException("MiniServer unavailable");
        }
        return server.loadApk(apkPath);
    }

    public MiniActivityManager getActivityManager() { return mActivityManager; }
    public MiniServiceManager getServiceManager() { return mServiceManager; }
    public MiniPackageManager getPackageManager() { return mPackageManager; }
    public Application getApplication() { return mApplication; }
    public void setApplication(Application app) {
        if (app != null) {
            mApplication = app;
        } else {
            // CR59 (2026-05-14): see currentSetApplication for rationale.
            Application placeholder = new Application();
            try {
                com.westlake.services.WestlakeContextImpl baseContext =
                        new com.westlake.services.WestlakeContextImpl(
                                mPackageName != null && !mPackageName.isEmpty()
                                        ? mPackageName : "com.example.app",
                                /*apkPath*/ null,
                                /*dataDir*/ null,
                                /*classLoader*/ getClass().getClassLoader(),
                                /*targetSdk*/ 33);
                baseContext.setAttachedApplication(placeholder);
                placeholder.attach(baseContext);
            } catch (Throwable ignored) {}
            mApplication = placeholder;
        }
        ShimCompat.setPackageName(mApplication, mPackageName);
    }
    public String getPackageName() { return mPackageName; }
    public ApkInfo getApkInfo() { return mApkInfo; }

    public void setPackageName(String packageName) {
        if (packageName == null || packageName.isEmpty()) {
            return;
        }
        mPackageName = packageName;
        if (mApplication != null) {
            ShimCompat.setPackageName(mApplication, packageName);
        }
        mPackageManager.setPackageName(packageName);
    }

    /**
     * Start an Activity by class name (convenience for testing).
     * Creates an explicit Intent and delegates to MiniActivityManager.
     */
    public void startActivity(String activityClassName) {
        try {
            Class<?> cls = Class.forName(activityClassName);
            Intent intent = Intent.makeMainActivity(new ComponentName(mPackageName, activityClassName));
            intent.setPackage(mPackageName);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
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
        setPackageName(info.packageName);

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

        // Wire APK path so LayoutInflater can read binary AXML layouts
        // directly from the APK ZIP via ApkResourceLoader
        ShimCompat.setApkPath(mApplication.getResources(), apkPath);
        ShimCompat.setAssetApkPath(mApplication.getAssets(), apkPath);

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
