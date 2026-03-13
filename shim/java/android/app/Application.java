package android.app;

import android.content.ContextWrapper;

/**
 * Android-compatible Application shim. Stub — lifecycle hooks are no-ops.
 */
public class Application extends ContextWrapper {
    public Application() { super(null); }

    public void onCreate() {}
    public void onTerminate() {}
    public void onConfigurationChanged(Object newConfig) {}
    public void onLowMemory() {}
    public void onTrimMemory(int level) {}

    public void registerActivityLifecycleCallbacks(ActivityLifecycleCallbacks callback) {}
    public void unregisterActivityLifecycleCallbacks(ActivityLifecycleCallbacks callback) {}

    public interface ActivityLifecycleCallbacks {
        void onActivityCreated(Object activity, Object savedInstanceState);
        void onActivityStarted(Object activity);
        void onActivityResumed(Object activity);
        void onActivityPaused(Object activity);
        void onActivityStopped(Object activity);
        void onActivitySaveInstanceState(Object activity, Object outState);
        void onActivityDestroyed(Object activity);
    }

    public interface OnProvideAssistDataListener {
        void onProvideAssistData(Object activity, Object data);
    }
}
