package android.app;

import android.content.Intent;
import android.os.Bundle;

/**
 * Android-compatible Instrumentation shim.
 * Provides the Activity lifecycle orchestration stubs used in testing and
 * migration tooling. All methods are no-ops or return sensible defaults;
 * no OH-bridge calls are needed.
 */
public class Instrumentation {

    /** Opaque token used to identify a running instrumentation. */
    public static class ActivityResult {
        private final int mResultCode;
        private final Intent mResultData;

        public ActivityResult(int resultCode, Intent resultData) {
            mResultCode = resultCode;
            mResultData = resultData;
        }

        public int getResultCode() { return mResultCode; }
        public Intent getResultData() { return mResultData; }
    }

    // -------------------------------------------------------------------------
    // Construction
    // -------------------------------------------------------------------------

    public Instrumentation() {}

    // -------------------------------------------------------------------------
    // Activity instantiation
    // -------------------------------------------------------------------------

    /**
     * Create a new Activity instance of the given class.
     * In the shim, this simply reflectively instantiates the class.
     */
    public Activity newActivity(Class<?> clazz, Object context,
            Object token, Object application, Intent intent,
            Object info, String title, Activity parent,
            String id, Object lastNonConfigurationInstances) {
        try {
            return (Activity) clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Cannot instantiate activity " + clazz, e);
        }
    }

    /**
     * Simplified variant: instantiate an Activity by its class name.
     */
    public Activity newActivity(ClassLoader cl, String className, Intent intent)
            throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        Class<?> clazz = cl.loadClass(className);
        try {
            return (Activity) clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new InstantiationException("Cannot instantiate " + className);
        }
    }

    // -------------------------------------------------------------------------
    // Activity lifecycle callbacks
    // -------------------------------------------------------------------------

    /**
     * Calls {@code Activity.onCreate(savedInstanceState)}.
     */
    public void callActivityOnCreate(Activity activity, Bundle savedInstanceState) {
        if (activity != null) {
            activity.onCreate(savedInstanceState);
        }
    }

    /**
     * Calls {@code Activity.onCreate(savedInstanceState, persistentState)}.
     */
    public void callActivityOnCreate(Activity activity, Bundle savedInstanceState,
            Object persistentState) {
        if (activity != null) {
            activity.onCreate(savedInstanceState);
        }
    }

    /**
     * Calls {@code Activity.onStart()}.
     */
    public void callActivityOnStart(Activity activity) {
        if (activity != null) {
            activity.onStart();
        }
    }

    /**
     * Calls {@code Activity.onRestart()}.
     */
    public void callActivityOnRestart(Activity activity) {
        if (activity != null) {
            activity.onRestart();
        }
    }

    /**
     * Calls {@code Activity.onResume()}.
     */
    public void callActivityOnResume(Activity activity) {
        if (activity != null) {
            activity.onResume();
        }
    }

    /**
     * Calls {@code Activity.onPause()}.
     */
    public void callActivityOnPause(Activity activity) {
        if (activity != null) {
            activity.onPause();
        }
    }

    /**
     * Calls {@code Activity.onStop()}.
     */
    public void callActivityOnStop(Activity activity) {
        if (activity != null) {
            activity.onStop();
        }
    }

    /**
     * Calls {@code Activity.onDestroy()}.
     */
    public void callActivityOnDestroy(Activity activity) {
        if (activity != null) {
            activity.onDestroy();
        }
    }

    /**
     * Calls {@code Activity.onRestoreInstanceState(savedInstanceState)}.
     */
    public void callActivityOnRestoreInstanceState(Activity activity, Bundle savedInstanceState) {
        if (activity != null && savedInstanceState != null) {
            activity.onRestoreInstanceState(savedInstanceState);
        }
    }

    /**
     * Calls {@code Activity.onSaveInstanceState(outState)}.
     */
    public void callActivityOnSaveInstanceState(Activity activity, Bundle outState) {
        if (activity != null && outState != null) {
            activity.onSaveInstanceState(outState);
        }
    }

    /**
     * Calls {@code Activity.onPostCreate(savedInstanceState)}.
     */
    public void callActivityOnPostCreate(Activity activity, Bundle savedInstanceState) {
        // stub — Activity.onPostCreate is an internal framework hook
    }

    /**
     * Calls {@code Activity.onNewIntent(intent)}.
     */
    public void callActivityOnNewIntent(Activity activity, Intent intent) {
        if (activity != null) {
            activity.onNewIntent(intent);
        }
    }

    /**
     * Calls {@code Activity.onUserLeaveHint()}.
     */
    public void callActivityOnUserLeaving(Activity activity) {
        // stub — no-op in shim
    }

    /**
     * Calls {@code Activity.onWindowFocusChanged(hasFocus)}.
     */
    public void callActivityOnWindowFocusChanged(Activity activity, boolean hasFocus) {
        // stub — no-op in shim
    }

    // -------------------------------------------------------------------------
    // Intent / result helpers
    // -------------------------------------------------------------------------

    /**
     * Execute a startActivity call made by the given activity.
     * Stub — no-op in shim layer.
     */
    public void execStartActivity(Object context, Object ibinder,
            Object ibinder2, Activity activity, Intent intent, int requestCode) {
        // no-op stub
    }

    /**
     * Execute a startActivityForResult call.
     * Stub — returns a default RESULT_CANCELED ActivityResult.
     */
    public ActivityResult execStartActivity(Object context, Object ibinder,
            Object ibinder2, Object fragment, Intent intent, int requestCode,
            Bundle options) {
        return new ActivityResult(Activity.RESULT_CANCELED, null);
    }

    // -------------------------------------------------------------------------
    // Miscellaneous stubs
    // -------------------------------------------------------------------------

    /**
     * Called when instrumentation is about to start. No-op stub.
     */
    public void onCreate(Bundle arguments) {}

    /**
     * Called when instrumentation is finishing. No-op stub.
     */
    public void finish(int resultCode, Bundle results) {}

    /**
     * Returns the context of the instrumentation's package. Returns null in shim.
     */
    public Object getContext() { return null; }

    /**
     * Returns the target context (the app under test). Returns null in shim.
     */
    public Object getTargetContext() { return null; }

    /**
     * Returns the component name of the instrumentation. Returns null in shim.
     */
    public Object getComponentName() { return null; }

    /**
     * Run on the main thread (stub — just invokes run() directly in shim).
     */
    public void runOnMainSync(Runnable runner) {
        if (runner != null) runner.run();
    }

    /**
     * Waits for the Activity idle state. No-op stub.
     */
    public void waitForIdleSync() {}

    /**
     * Sends a key down/up pair. No-op stub.
     */
    public void sendKeyDownUpSync(int key) {}

    /**
     * Sends a string as a series of key events. No-op stub.
     */
    public void sendStringSync(String text) {}

    /**
     * Sends a pointer event. No-op stub.
     */
    public void sendPointerSync(Object event) {}

    /**
     * Sends a trackball event. No-op stub.
     */
    public void sendTrackballEventSync(Object event) {}
}
