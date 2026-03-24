package android.app;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * MiniActivityManager — manages the Activity back stack for a single app.
 *
 * Replaces Android's ActivityManagerService + ActivityTaskManagerService.
 * Handles:
 * - Activity instantiation via reflection
 * - Lifecycle dispatch (onCreate → onStart → onResume → onPause → onStop → onDestroy)
 * - Back stack navigation (push on startActivity, pop on finish)
 * - startActivityForResult / onActivityResult round-trip
 * - Only one Activity is "resumed" at a time
 */
public class MiniActivityManager {
    private static final String TAG = "MiniActivityManager";

    private final MiniServer mServer;
    private final ArrayList<ActivityRecord> mStack = new ArrayList<>();
    private final Map<String, Class<?>> mRegisteredClasses = new HashMap<>();

    /** Register an Activity class loaded from an external DEX/APK */
    public void registerActivityClass(String className, Class<?> cls) {
        mRegisteredClasses.put(className, cls);
    }
    private ActivityRecord mResumed;

    MiniActivityManager(MiniServer server) {
        mServer = server;
    }

    /**
     * Start an Activity.
     * @param caller The calling Activity (null for initial launch)
     * @param intent The intent describing the Activity to start
     * @param requestCode -1 for startActivity, >= 0 for startActivityForResult
     */
    public void startActivity(Activity caller, Intent intent, int requestCode) {
        if (intent == null) {
            Log.w(TAG, "startActivity: null intent");
            return;
        }
        ComponentName component = intent.getComponent();
        if (component == null) {
            // Implicit intent resolution via MiniPackageManager
            android.content.pm.MiniPackageManager pm = mServer.getPackageManager();
            if (pm != null) {
                android.content.pm.ResolveInfo ri = pm.resolveActivity(intent);
                if (ri != null && ri.resolvedComponentName != null) {
                    component = ri.resolvedComponentName;
                    intent.setComponent(component);
                }
            }
            if (component == null) {
                Log.w(TAG, "startActivity: cannot resolve intent, action=" + intent.getAction());
                return;
            }
        }

        String className = component.getClassName();
        Log.d(TAG, "startActivity: " + className);

        // Instantiate the Activity class
        Activity activity;
        try {
            Class<?> cls = mRegisteredClasses.get(className);
            if (cls == null) {
                // Use context classloader (set by ART to app's PathClassLoader)
                // NOT boot classloader (MiniActivityManager is on boot classpath)
                ClassLoader cl = Thread.currentThread().getContextClassLoader();
                if (cl == null) cl = ClassLoader.getSystemClassLoader();
                cls = cl.loadClass(className);
            }
            activity = (Activity) cls.newInstance();
        } catch (ClassNotFoundException e) {
            Log.e(TAG, "Activity class not found: " + className);
            return;
        } catch (Exception e) {
            Log.e(TAG, "Failed to instantiate " + className + ": " + e.getMessage());
            return;
        }

        // Initialize framework state (via reflection — on real Android, Activity fields differ)
        ShimCompat.setActivityField(activity, "mIntent", intent);
        ShimCompat.setActivityField(activity, "mComponent", component);
        ShimCompat.setActivityField(activity, "mApplication", mServer.getApplication());
        ShimCompat.setActivityField(activity, "mFinished", Boolean.FALSE);
        ShimCompat.setActivityField(activity, "mDestroyed", Boolean.FALSE);

        // Create the ActivityRecord
        ActivityRecord record = new ActivityRecord();
        record.activity = activity;
        record.intent = intent;
        record.component = component;
        if (caller != null && requestCode >= 0) {
            record.caller = findRecord(caller);
            record.requestCode = requestCode;
        }

        // Pause the current top activity
        if (mResumed != null) {
            ActivityRecord prev = mResumed;
            performPause(prev);
            performStop(prev);
        }

        // Push and start the new activity
        mStack.add(record);
        performCreate(record, null);
        performStart(record);
        performResume(record);
    }

    /**
     * Finish an Activity. Pops it from the stack and resumes the previous one.
     */
    public void finishActivity(Activity activity) {
        if (activity == null) {
            Log.w(TAG, "finishActivity: null activity");
            return;
        }
        ActivityRecord record = findRecord(activity);
        if (record == null) {
            // Already finished or never in stack — idempotent
            return;
        }

        Log.d(TAG, "finishActivity: " + record.component.getClassName());

        // If this is the resumed activity, pause it first
        if (record == mResumed) {
            performPause(record);
        }
        performStop(record);
        performDestroy(record);

        // Deliver result to caller if startActivityForResult was used
        if (record.caller != null && record.requestCode >= 0) {
            Activity callerActivity = record.caller.activity;
            boolean callerDestroyed = ShimCompat.getActivityBooleanField(callerActivity, "mDestroyed", false);
            if (callerActivity != null && !callerDestroyed) {
                int resultCode = ShimCompat.getActivityIntField(activity, "mResultCode", 0);
                android.content.Intent resultData = ShimCompat.getActivityField(activity, "mResultData", (android.content.Intent) null);
                callerActivity.onActivityResult(
                    record.requestCode,
                    resultCode,
                    resultData
                );
            }
        }

        // Remove from stack
        mStack.remove(record);

        // Resume the new top activity
        if (!mStack.isEmpty()) {
            ActivityRecord top = mStack.get(mStack.size() - 1);
            performRestart(top);
            performStart(top);
            performResume(top);
        }
    }

    /**
     * Handle back button press. Finishes the top activity.
     */
    public void onBackPressed() {
        if (mResumed != null && mResumed.activity != null) {
            Activity top = mResumed.activity;
            top.onBackPressed();
            // finish() is now idempotent and calls finishActivity internally
        }
    }

    /**
     * Finish all activities (shutdown).
     */
    public void finishAll() {
        // Destroy from top to bottom
        while (!mStack.isEmpty()) {
            ActivityRecord top = mStack.get(mStack.size() - 1);
            if (top == mResumed) {
                performPause(top);
            }
            performStop(top);
            performDestroy(top);
            mStack.remove(mStack.size() - 1);
        }
        mResumed = null;
    }

    /** Get the currently resumed Activity, or null. */
    public Activity getResumedActivity() {
        return mResumed != null ? mResumed.activity : null;
    }

    /** Get the Activity stack size. */
    public int getStackSize() {
        return mStack.size();
    }

    /** Get an Activity by index (0 = bottom). */
    public Activity getActivity(int index) {
        if (index < 0 || index >= mStack.size()) return null;
        return mStack.get(index).activity;
    }

    // ── Lifecycle dispatch ──────────────────────────────────────────────────

    private void performCreate(ActivityRecord r, Bundle savedInstanceState) {
        Log.d(TAG, "  performCreate: " + r.component.getClassName());
        r.activity.onCreate(savedInstanceState);
    }

    private void performStart(ActivityRecord r) {
        Log.d(TAG, "  performStart: " + r.component.getClassName());
        ShimCompat.setActivityField(r.activity, "mStarted", Boolean.TRUE);
        r.activity.onStart();
    }

    private void performResume(ActivityRecord r) {
        Log.d(TAG, "  performResume: " + r.component.getClassName());
        ShimCompat.setActivityField(r.activity, "mResumed", Boolean.TRUE);
        mResumed = r;
        r.activity.onResume();
        r.activity.onPostResume();
    }

    private void performPause(ActivityRecord r) {
        Log.d(TAG, "  performPause: " + r.component.getClassName());
        ShimCompat.setActivityField(r.activity, "mResumed", Boolean.FALSE);
        r.activity.onPause();
        if (r == mResumed) {
            mResumed = null;
        }
    }

    private void performStop(ActivityRecord r) {
        if (ShimCompat.getActivityBooleanField(r.activity, "mStarted", false) == false) return;
        Log.d(TAG, "  performStop: " + r.component.getClassName());
        ShimCompat.setActivityField(r.activity, "mStarted", Boolean.FALSE);
        r.activity.onStop();
    }

    private void performDestroy(ActivityRecord r) {
        Log.d(TAG, "  performDestroy: " + r.component.getClassName());
        ShimCompat.setActivityField(r.activity, "mDestroyed", Boolean.TRUE);
        r.activity.onDestroy();
    }

    private void performRestart(ActivityRecord r) {
        Log.d(TAG, "  performRestart: " + r.component.getClassName());
        r.activity.onRestart();
    }

    // ── Internal ────────────────────────────────────────────────────────────

    private ActivityRecord findRecord(Activity activity) {
        for (int i = mStack.size() - 1; i >= 0; i--) {
            if (mStack.get(i).activity == activity) {
                return mStack.get(i);
            }
        }
        return null;
    }

    /** Internal record tracking an Activity's state. */
    static class ActivityRecord {
        Activity activity;
        Intent intent;
        ComponentName component;
        ActivityRecord caller;
        int requestCode = -1;
    }
}
