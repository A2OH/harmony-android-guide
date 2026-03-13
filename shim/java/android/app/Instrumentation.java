package android.app;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;

/**
 * Instrumentation — manages Activity lifecycle.
 * In real Android this lives in android.app.Instrumentation.
 * We implement the subset needed to launch and drive activities.
 */
public class Instrumentation {

    public Instrumentation() {}

    /**
     * Create a new Activity instance by class name (reflection).
     */
    public Activity newActivity(ClassLoader cl, String className, Intent intent)
            throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        Class<?> clazz = cl.loadClass(className);
        Activity activity = (Activity) clazz.newInstance();
        activity.mIntent = intent;
        return activity;
    }

    /**
     * Attach application context to the activity.
     */
    public void callActivityOnCreate(Activity activity, Bundle icicle) {
        activity.onCreate(icicle);
    }

    public void callActivityOnCreate(Activity activity, Bundle icicle, PersistableBundle persistentState) {
        activity.onCreate(icicle, persistentState);
    }

    public void callActivityOnStart(Activity activity) {
        activity.mStarted = true;
        activity.onStart();
    }

    public void callActivityOnResume(Activity activity) {
        activity.mResumed = true;
        activity.onResume();
        activity.onPostResume();
    }

    public void callActivityOnPause(Activity activity) {
        activity.mResumed = false;
        activity.onPause();
    }

    public void callActivityOnStop(Activity activity) {
        activity.mStarted = false;
        activity.onStop();
    }

    public void callActivityOnDestroy(Activity activity) {
        activity.mDestroyed = true;
        activity.onDestroy();
    }

    public void callActivityOnRestart(Activity activity) {
        activity.onRestart();
    }

    public void callActivityOnNewIntent(Activity activity, Intent intent) {
        activity.onNewIntent(intent);
    }

    public void callActivityOnSaveInstanceState(Activity activity, Bundle outState) {
        activity.onSaveInstanceState(outState);
    }

    public void callActivityOnRestoreInstanceState(Activity activity, Bundle savedInstanceState) {
        activity.onRestoreInstanceState(savedInstanceState);
    }

    public void callApplicationOnCreate(Application app) {
        app.onCreate();
    }

    /* ── Remaining stubs ── */
    public static final int REPORT_KEY_IDENTIFIER = 0;
    public static final int REPORT_KEY_STREAMRESULT = 0;
    public void start() {}
    public void onStart() {}
    public void onCreate(Bundle arguments) {}
    public void onDestroy() {}
    public void finish(int resultCode, Bundle results) {}
    public boolean onException(Object obj, Throwable e) { return false; }
    public Context getContext() { return null; }
    public Context getTargetContext() { return null; }
    public ComponentName getComponentName() { return null; }
    public boolean isProfiling() { return false; }
    public void startProfiling() {}
    public void stopProfiling() {}
    public void setInTouchMode(boolean inTouch) {}
    public void waitForIdleSync() {}
    public void runOnMainSync(Runnable runner) { if (runner != null) runner.run(); }
    public Activity startActivitySync(Intent intent) { return null; }
    public void addMonitor(Object monitor) {}
    public void removeMonitor(Object monitor) {}
    public void sendStatus(int resultCode, Bundle results) {}
    public void addResults(Bundle results) {}
    public void setAutomaticPerformanceSnapshots() {}
    public void startPerformanceSnapshot() {}
    public void endPerformanceSnapshot() {}
}
