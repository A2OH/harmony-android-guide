package android.app;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * MiniServer — replaces Android's SystemServer for single-app engine execution.
 *
 * Instead of 80+ system services communicating via Binder IPC, this is a single
 * in-process Java object that manages one app's Activity stack, window, and package info.
 *
 * Usage:
 *   MiniServer.init(packageName);
 *   MiniServer.get().startActivity(launcherIntent);
 */
public class MiniServer {
    private static MiniServer sInstance;

    private final MiniActivityManager mActivityManager;
    private final Application mApplication;
    private String mPackageName;

    private MiniServer(String packageName) {
        mPackageName = packageName;
        mApplication = new Application();
        mApplication.setPackageName(packageName);
        mActivityManager = new MiniActivityManager(this);
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
    public Application getApplication() { return mApplication; }
    public String getPackageName() { return mPackageName; }

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
     */
    public void startActivity(Intent intent) {
        mActivityManager.startActivity(null, intent, -1);
    }

    /** Shut down: destroy all activities, call Application.onTerminate(). */
    public void shutdown() {
        mActivityManager.finishAll();
        mApplication.onTerminate();
    }
}
