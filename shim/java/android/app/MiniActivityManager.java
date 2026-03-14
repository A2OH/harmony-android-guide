package android.app;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import java.util.ArrayList;

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
        ComponentName component = intent.getComponent();
        if (component == null) {
            Log.w(TAG, "startActivity: no component in intent, action=" + intent.getAction());
            // TODO: implicit intent resolution via MiniPackageManager
            return;
        }

        String className = component.getClassName();
        Log.d(TAG, "startActivity: " + className);

        // Instantiate the Activity class
        Activity activity;
        try {
            Class<?> cls = Class.forName(className);
            activity = (Activity) cls.newInstance();
        } catch (ClassNotFoundException e) {
            Log.e(TAG, "Activity class not found: " + className);
            return;
        } catch (Exception e) {
            Log.e(TAG, "Failed to instantiate " + className + ": " + e.getMessage());
            return;
        }

        // Initialize framework state
        activity.mIntent = intent;
        activity.mComponent = component;
        activity.mApplication = mServer.getApplication();
        activity.mFinished = false;
        activity.mDestroyed = false;

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
        ActivityRecord record = findRecord(activity);
        if (record == null) {
            Log.w(TAG, "finishActivity: activity not in stack");
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
            callerActivity.onActivityResult(
                record.requestCode,
                activity.mResultCode,
                activity.mResultData
            );
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
        if (mResumed != null) {
            Activity top = mResumed.activity;
            top.onBackPressed();
            if (top.mFinished) {
                finishActivity(top);
            }
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
        return mStack.get(index).activity;
    }

    // ── Lifecycle dispatch ──────────────────────────────────────────────────

    private void performCreate(ActivityRecord r, Bundle savedInstanceState) {
        Log.d(TAG, "  performCreate: " + r.component.getClassName());
        r.activity.onCreate(savedInstanceState);
    }

    private void performStart(ActivityRecord r) {
        Log.d(TAG, "  performStart: " + r.component.getClassName());
        r.activity.mStarted = true;
        r.activity.onStart();
    }

    private void performResume(ActivityRecord r) {
        Log.d(TAG, "  performResume: " + r.component.getClassName());
        r.activity.mResumed = true;
        mResumed = r;
        r.activity.onResume();
        r.activity.onPostResume();
    }

    private void performPause(ActivityRecord r) {
        Log.d(TAG, "  performPause: " + r.component.getClassName());
        r.activity.mResumed = false;
        r.activity.onPause();
        if (r == mResumed) {
            mResumed = null;
        }
    }

    private void performStop(ActivityRecord r) {
        if (!r.activity.mStarted) return;
        Log.d(TAG, "  performStop: " + r.component.getClassName());
        r.activity.mStarted = false;
        r.activity.onStop();
    }

    private void performDestroy(ActivityRecord r) {
        Log.d(TAG, "  performDestroy: " + r.component.getClassName());
        r.activity.mDestroyed = true;
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
