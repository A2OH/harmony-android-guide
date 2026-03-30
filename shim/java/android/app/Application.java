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
    public String getPackageName() { return mPackageName; }

    @Override
    public Context getApplicationContext() { return this; }

    /* Framework-internal */
    void setPackageName(String pkg) { mPackageName = pkg; }

    public static String getProcessName() { return null; }
    public void registerActivityLifecycleCallbacks(ActivityLifecycleCallbacks cb) {
        if (cb != null) mCallbacks.add(cb);
    }
    public void unregisterActivityLifecycleCallbacks(ActivityLifecycleCallbacks cb) {
        mCallbacks.remove(cb);
    }
    public void registerActivityLifecycleCallbacks(Object p0) {}
    public void registerOnProvideAssistDataListener(Object p0) {}
    public void unregisterActivityLifecycleCallbacks(Object p0) {}
    public void unregisterOnProvideAssistDataListener(Object p0) {}
}
