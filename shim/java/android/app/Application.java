package android.app;

import android.content.Context;

public class Application extends Context {

    public interface ActivityLifecycleCallbacks {
        default void onActivityPreCreated(Activity activity, android.os.Bundle savedInstanceState) {}
        void onActivityCreated(Activity activity, android.os.Bundle savedInstanceState);
        void onActivityStarted(Activity activity);
        void onActivityResumed(Activity activity);
        void onActivityPaused(Activity activity);
        void onActivityStopped(Activity activity);
        void onActivitySaveInstanceState(Activity activity, android.os.Bundle outState);
        void onActivityDestroyed(Activity activity);
        default void onActivityPostCreated(Activity activity, android.os.Bundle savedInstanceState) {}
        default void onActivityPreStarted(Activity activity) {}
        default void onActivityPostStarted(Activity activity) {}
        default void onActivityPreResumed(Activity activity) {}
        default void onActivityPostResumed(Activity activity) {}
        default void onActivityPrePaused(Activity activity) {}
        default void onActivityPostPaused(Activity activity) {}
        default void onActivityPreStopped(Activity activity) {}
        default void onActivityPostStopped(Activity activity) {}
        default void onActivityPreSaveInstanceState(Activity activity, android.os.Bundle outState) {}
        default void onActivityPostSaveInstanceState(Activity activity, android.os.Bundle outState) {}
        default void onActivityPreDestroyed(Activity activity) {}
        default void onActivityPostDestroyed(Activity activity) {}
    }

    private java.util.List<ActivityLifecycleCallbacks> mCallbacks = new java.util.ArrayList<>();

    private String mPackageName;

    public Application() {}

    public void onCreate() {}

    public void onTerminate() {}

    public void onLowMemory() {}

    public void onTrimMemory(int level) {}

    @Override
    public String getPackageName() {
        if (mPackageName != null && !mPackageName.isEmpty()) {
            return mPackageName;
        }
        String currentPkg = android.app.MiniServer.currentPackageName();
        if (currentPkg != null && !currentPkg.isEmpty()) {
            return currentPkg;
        }
        String propPkg = System.getProperty("westlake.apk.package");
        if (propPkg != null && !propPkg.isEmpty()) {
            return propPkg;
        }
        try {
            return super.getPackageName();
        } catch (Throwable ignored) {
            return "";
        }
    }

    @Override
    public Context getApplicationContext() { return this; }

    /* Framework-internal */
    void setPackageName(String pkg) { mPackageName = pkg; }

    public static String getProcessName() { return null; }

    // Hilt/Dagger: obfuscated componentManager() — returns ApplicationComponentManager
    // McD app calls Application.b() to get the GeneratedComponentManager
    private dagger.hilt.android.internal.managers.ApplicationComponentManager mComponentManager;
    public dagger.hilt.android.internal.managers.ApplicationComponentManager b() {
        if (mComponentManager == null) {
            mComponentManager = new dagger.hilt.android.internal.managers.ApplicationComponentManager(null);
        }
        return mComponentManager;
    }
    public Object componentManager() { return b(); }
    private java.util.List<ActivityLifecycleCallbacks> ensureCallbacks() {
        if (mCallbacks == null) {
            mCallbacks = new java.util.ArrayList<>();
        }
        return mCallbacks;
    }

    public void registerActivityLifecycleCallbacks(ActivityLifecycleCallbacks cb) {
        if (cb != null) ensureCallbacks().add(cb);
    }
    public void unregisterActivityLifecycleCallbacks(ActivityLifecycleCallbacks cb) {
        if (mCallbacks != null) mCallbacks.remove(cb);
    }
    public void registerActivityLifecycleCallbacks(Object p0) {}
    public void registerOnProvideAssistDataListener(Object p0) {}
    public void unregisterActivityLifecycleCallbacks(Object p0) {}
    public void unregisterOnProvideAssistDataListener(Object p0) {}
}
