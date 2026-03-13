package android.app;

import android.content.Context;

public class Application extends Context {

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
    public void registerActivityLifecycleCallbacks(Object p0) {}
    public void registerOnProvideAssistDataListener(Object p0) {}
    public void unregisterActivityLifecycleCallbacks(Object p0) {}
    public void unregisterOnProvideAssistDataListener(Object p0) {}
}
