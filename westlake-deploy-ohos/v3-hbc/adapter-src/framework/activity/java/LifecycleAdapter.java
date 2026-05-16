/*
 * LifecycleAdapter.java
 *
 * Bidirectional mapping between Android Activity lifecycle and
 * OpenHarmony Ability lifecycle.
 *
 * Android:  onCreate -> onStart -> onResume -> onPause -> onStop -> onDestroy
 * OH:       onCreate -> onWindowStageCreate -> onForeground -> onBackground
 *                    -> onWindowStageDestroy -> onDestroy
 *
 * Mapping strategy:
 *   Android onCreate + onStart   <->  OH onCreate + onWindowStageCreate
 *   Android onResume             <->  OH onForeground
 *   Android onPause              <->  (intermediate state, no direct OH equivalent)
 *   Android onStop               <->  OH onBackground
 *   Android onDestroy            <->  OH onWindowStageDestroy + onDestroy
 */
package adapter.activity;

import adapter.core.OHEnvironment;

import android.app.ActivityThread;
import android.app.IApplicationThread;
import android.app.servertransaction.ClientTransaction;
import android.app.servertransaction.LaunchActivityItem;
import android.app.servertransaction.ResumeActivityItem;
import android.app.servertransaction.PauseActivityItem;
import android.app.servertransaction.StopActivityItem;
import android.app.servertransaction.DestroyActivityItem;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LifecycleAdapter {

    private static final String TAG = "OH_LifecycleAdapter";

    // OH Ability lifecycle state constants (maps to OH AbilityLifecycleExecutor::LifecycleState)
    public static final int OH_STATE_INITIAL = 0;
    public static final int OH_STATE_INACTIVE = 1;       // onCreate completed
    public static final int OH_STATE_FOREGROUND_NEW = 2;  // onForeground
    public static final int OH_STATE_BACKGROUND_NEW = 4;  // onBackground

    // Android Activity lifecycle state constants
    public static final int ANDROID_STATE_CREATED = 1;
    public static final int ANDROID_STATE_STARTED = 2;
    public static final int ANDROID_STATE_RESUMED = 3;
    public static final int ANDROID_STATE_PAUSED = 4;
    public static final int ANDROID_STATE_STOPPED = 5;
    public static final int ANDROID_STATE_DESTROYED = 6;

    private static volatile LifecycleAdapter sInstance;

    // Track current lifecycle state of each Activity/Ability
    // key: Android Activity token (hashCode), value: current OH state
    private final Map<Integer, Integer> mStateMap = new ConcurrentHashMap<>();

    public static LifecycleAdapter getInstance() {
        if (sInstance == null) {
            synchronized (LifecycleAdapter.class) {
                if (sInstance == null) {
                    sInstance = new LifecycleAdapter();
                }
            }
        }
        return sInstance;
    }

    private LifecycleAdapter() {
    }

    /**
     * Convert Android Activity lifecycle state changes to OH Ability state changes.
     * Called when ActivityThread processes lifecycle events.
     *
     * @param activityToken Activity identifier
     * @param androidState  Target Android lifecycle state
     */
    public void onAndroidLifecycleChanged(int activityToken, int androidState) {
        int currentOHState = mStateMap.getOrDefault(activityToken, OH_STATE_INITIAL);
        int targetOHState = mapAndroidToOH(androidState);

        Log.d(TAG, "Android state change: " + androidStateName(androidState)
                + " -> OH state: " + ohStateName(targetOHState)
                + " (token=" + activityToken + ")");

        if (targetOHState != currentOHState) {
            mStateMap.put(activityToken, targetOHState);
            notifyOHStateChange(activityToken, targetOHState);
        }

        // Clean up state when Activity is destroyed
        if (androidState == ANDROID_STATE_DESTROYED) {
            mStateMap.remove(activityToken);
        }
    }

    /**
     * OH system service callback: Ability state change -> convert to Android Activity
     * lifecycle event. Called by the JNI layer's AbilitySchedulerStub.
     *
     * @param abilityToken OH Ability identifier
     * @param ohState      OH target lifecycle state
     */
    public void onOHLifecycleCallback(int abilityToken, int ohState) {
        int androidState = mapOHToAndroid(ohState);

        Log.d(TAG, "OH callback: state=" + ohStateName(ohState)
                + " -> Android state: " + androidStateName(androidState)
                + " (token=" + abilityToken + ")");

        // Trigger the corresponding Android lifecycle change via internal mechanism
        dispatchAndroidLifecycle(abilityToken, androidState);
    }

    /**
     * G2.14m (2026-05-01): direct dispatch by Android Activity IBinder.
     * Used by AbilitySchedulerBridge.onScheduleAbilityTransaction when the
     * OH ability tokenAddr has already been resolved via OhTokenRegistry to
     * the Android Activity IBinder. Bypasses the abilityRecordId-based
     * getActivityToken() lookup which doesn't have the right key for the
     * IAbilityScheduler IPC path.
     */
    public void dispatchOhLifecycleByActivityToken(android.os.IBinder activityToken,
                                                     int ohState) {
        int androidState = mapOHToAndroid(ohState);
        Log.i(TAG, "OH callback (direct token): state=" + ohStateName(ohState)
                + " -> " + androidStateName(androidState)
                + " activityToken=" + activityToken);
        dispatchByActivityToken(activityToken, androidState);
    }

    /**
     * Internal: dispatch directly with a known Android Activity IBinder.
     * Mirrors dispatchAndroidLifecycle but skips the int → IBinder lookup.
     *
     * 2026-05-08 G2.14x: this method is invoked from the OH binder thread
     * (e.g. OH_AbilitySched-XXXX). hitrace evidence showed every lifecycle
     * Choreographer / doFrame / activityResume mark was emitted on the binder
     * thread, while main thread (Looper.loop()) sat idle in epoll_wait. Reason:
     * appThread.scheduleTransaction() local-call goes through ActivityThread.H
     * Handler which is bound to main Looper, but ClientTransaction.preExecute()
     * and any synchronous follow-up runs on the calling thread first; subsequent
     * callbacks from the lifecycle pipeline (vsync, doFrame stub) then fired in
     * the binder thread context, never giving control back to main. Solution:
     * post the entire transaction-build + schedule onto main Looper so the
     * whole lifecycle handling runs in the proper thread context that
     * ViewRootImpl / ThreadedRenderer / hwui RenderThread expects.
     *
     * Same pattern already proved correct in AppSchedulerBridge.notifyForegroundDeferred
     * (Handler(Looper.getMainLooper()).post(...)).
     */
    private void dispatchByActivityToken(final android.os.IBinder activityToken,
                                          final int androidState) {
        Handler mainHandler = new Handler(Looper.getMainLooper());
        mainHandler.post(new Runnable() {
            @Override public void run() {
                dispatchByActivityTokenOnMainLooper(activityToken, androidState);
            }
        });
        Log.i(TAG, "dispatchByActivityToken posted to main Looper: "
                + androidStateName(androidState));
    }

    private void dispatchByActivityTokenOnMainLooper(android.os.IBinder activityToken,
                                                      int androidState) {
        try {
            android.app.ActivityThread activityThread =
                    android.app.ActivityThread.currentActivityThread();
            if (activityThread == null) {
                Log.e(TAG, "ActivityThread not available");
                return;
            }
            android.app.IApplicationThread appThread = activityThread.getApplicationThread();
            if (appThread == null) {
                Log.e(TAG, "ApplicationThread not available");
                return;
            }
            android.app.servertransaction.ClientTransaction transaction =
                    android.app.servertransaction.ClientTransaction.obtain(appThread, activityToken);
            switch (androidState) {
                case ANDROID_STATE_RESUMED:
                    transaction.setLifecycleStateRequest(
                            android.app.servertransaction.ResumeActivityItem.obtain(true, false));
                    break;
                case ANDROID_STATE_PAUSED:
                    transaction.setLifecycleStateRequest(
                            android.app.servertransaction.PauseActivityItem.obtain());
                    break;
                case ANDROID_STATE_STOPPED:
                    transaction.setLifecycleStateRequest(
                            android.app.servertransaction.StopActivityItem.obtain(0));
                    break;
                case ANDROID_STATE_DESTROYED:
                    transaction.setLifecycleStateRequest(
                            android.app.servertransaction.DestroyActivityItem.obtain(false, 0));
                    break;
                default:
                    Log.w(TAG, "dispatchByActivityToken: unsupported androidState="
                            + androidState);
                    return;
            }
            appThread.scheduleTransaction(transaction);
            Log.i(TAG, "ClientTransaction scheduled on main Looper (direct token): "
                    + androidStateName(androidState));

            // 2026-05-11 G2.14as — visibility-chain probe (post-RESUMED reflection dump).
            // See dumpActivityVisibilityState() docstring for full rationale.
            if (androidState == ANDROID_STATE_RESUMED) {
                // G2.14as r9 — multi-timepoint stability check.
                // r7/r8 produced contradictory hasDL results (false vs true) — likely state
                // drift or invalidate-loop.  Five-point sampling at 500/1500/3000/5000/8000ms
                // distinguishes:
                //   - stable false: record never fires
                //   - stable true:  record fires but RT can't replay
                //   - oscillating:  invalidate/discard loop
                final android.os.IBinder fToken = activityToken;
                android.os.Handler handler = new android.os.Handler(android.os.Looper.getMainLooper());
                for (int delay : new int[]{500, 1500, 3000, 5000, 8000}) {
                    final int t = delay;
                    handler.postDelayed(new Runnable() {
                        @Override public void run() {
                            dumpActivityVisibilityState(fToken, t);
                        }
                    }, delay);
                }
            }
        } catch (Throwable t) {
            Log.e(TAG, "dispatchByActivityTokenOnMainLooper failed", t);
        }
    }

    // ==================== State Mapping ====================

    private int mapAndroidToOH(int androidState) {
        switch (androidState) {
            case ANDROID_STATE_CREATED:
            case ANDROID_STATE_STARTED:
                return OH_STATE_INACTIVE;
            case ANDROID_STATE_RESUMED:
                return OH_STATE_FOREGROUND_NEW;
            case ANDROID_STATE_PAUSED:
                // OH has no pause equivalent, stay in foreground
                return OH_STATE_FOREGROUND_NEW;
            case ANDROID_STATE_STOPPED:
                return OH_STATE_BACKGROUND_NEW;
            case ANDROID_STATE_DESTROYED:
                return OH_STATE_INITIAL;
            default:
                return OH_STATE_INITIAL;
        }
    }

    private int mapOHToAndroid(int ohState) {
        switch (ohState) {
            case OH_STATE_INACTIVE:
                return ANDROID_STATE_STARTED;
            case OH_STATE_FOREGROUND_NEW:
                return ANDROID_STATE_RESUMED;
            case OH_STATE_BACKGROUND_NEW:
                return ANDROID_STATE_STOPPED;
            case OH_STATE_INITIAL:
                return ANDROID_STATE_DESTROYED;
            default:
                return ANDROID_STATE_DESTROYED;
        }
    }

    // ==================== Notification Methods ====================

    /**
     * Notify OH system service of Ability state change.
     * Calls OH AbilityScheduler via JNI.
     */
    private void notifyOHStateChange(int token, int ohState) {
        OHEnvironment.nativeNotifyAppState(ohState);
    }

    /**
     * Dispatch Android lifecycle event to ActivityThread.
     * Constructs ClientTransaction with the appropriate lifecycle item
     * and schedules it via ApplicationThread.
     *
     * This is the key bridge method: when OH AbilityScheduler notifies us of
     * a lifecycle state change, we construct the corresponding Android
     * ClientTransaction and deliver it to ActivityThread for execution.
     */
    private void dispatchAndroidLifecycle(int token, int androidState) {
        Log.i(TAG, "Dispatching Android lifecycle: token=" + token
                + ", state=" + androidStateName(androidState));

        try {
            ActivityThread activityThread = ActivityThread.currentActivityThread();
            if (activityThread == null) {
                Log.e(TAG, "ActivityThread not available");
                return;
            }

            IApplicationThread appThread = activityThread.getApplicationThread();
            if (appThread == null) {
                Log.e(TAG, "ApplicationThread not available");
                return;
            }

            // Find the activity token for the given ability token.
            // In the adapter, we map OH ability tokens to Android activity tokens.
            IBinder activityToken = getActivityToken(token);
            if (activityToken == null && androidState != ANDROID_STATE_CREATED) {
                Log.w(TAG, "No activity token found for OH token " + token
                        + ", cannot dispatch " + androidStateName(androidState));
                return;
            }

            // Construct ClientTransaction with the appropriate lifecycle item
            ClientTransaction transaction = ClientTransaction.obtain(appThread, activityToken);

            switch (androidState) {
                case ANDROID_STATE_CREATED:
                    // LaunchActivityItem triggers handleLaunchActivity -> Activity.onCreate
                    // This is handled separately via AppSchedulerBridge.nativeOnScheduleLaunchAbility
                    Log.d(TAG, "CREATED state dispatched via LaunchActivityItem (separate path)");
                    return;

                case ANDROID_STATE_STARTED:
                    // No separate StartActivityItem in Android; handled via resume path
                    Log.d(TAG, "STARTED state handled as part of resume sequence");
                    return;

                case ANDROID_STATE_RESUMED:
                    // ResumeActivityItem triggers handleResumeActivity -> Activity.onResume
                    transaction.setLifecycleStateRequest(
                            ResumeActivityItem.obtain(true, false));
                    break;

                case ANDROID_STATE_PAUSED:
                    // PauseActivityItem triggers handlePauseActivity -> Activity.onPause
                    transaction.setLifecycleStateRequest(
                            PauseActivityItem.obtain());
                    break;

                case ANDROID_STATE_STOPPED:
                    // StopActivityItem triggers handleStopActivity -> Activity.onStop
                    transaction.setLifecycleStateRequest(
                            StopActivityItem.obtain(0 /* configChanges */));
                    break;

                case ANDROID_STATE_DESTROYED:
                    // DestroyActivityItem triggers handleDestroyActivity -> Activity.onDestroy
                    transaction.setLifecycleStateRequest(
                            DestroyActivityItem.obtain(false /* finished */,
                                    0 /* configChanges */));
                    break;

                default:
                    Log.w(TAG, "Unknown Android state: " + androidState);
                    return;
            }

            // Schedule the transaction on the ActivityThread.
            //
            // 2026-04-29 (post-B.29, feedback.txt P1b): replaced reflection on
            // ActivityThread$ApplicationThread.scheduleTransaction with a
            // direct call through the public IApplicationThread interface.
            //
            // ApplicationThread is a private inner class of ActivityThread but
            // it `extends IApplicationThread.Stub`, so it IS-A IApplicationThread
            // (public interface from AIDL).  Assigning the return value to
            // IApplicationThread typed variable lets the call go through the
            // public interface contract — no reflection needed, no AOSP patch
            // needed.
            // appThread already resolved above (line 186) as IApplicationThread.
            appThread.scheduleTransaction(transaction);
            Log.i(TAG, "ClientTransaction scheduled: " + androidStateName(androidState));

            // 2026-05-11 G2.14as — visibility-chain probe.
            // hitrace实证 handleResumeActivity 真被调（activityResume marker fire）
            // 但 makeVisible / mVisibleFromClient / mDecor.setVisibility 在 AOSP
            // 都没 atrace marker，hitrace 看不到决策走向。relayoutWindow 两次
            // 都 vis=false 暗示 makeVisible 未生效（或时序错位）。
            // 此 probe 在 RESUMED dispatch 后 300ms（让 handleResumeActivity 完成
            // 所有 main-thread 同步工作 + 第一个 vsync 之后），通过反射读
            // Activity 内部 visibility 状态 + DecorView 实际 visibility，输出到
            // hilog 直接证实是哪条 if 分支让 makeVisible 没生效。
            if (androidState == ANDROID_STATE_RESUMED) {
                final IBinder fToken = activityToken;
                android.os.Handler handler = new android.os.Handler(android.os.Looper.getMainLooper());
                for (int delay : new int[]{500, 1500, 3000, 5000, 8000}) {
                    final int t = delay;
                    handler.postDelayed(new Runnable() {
                        @Override public void run() {
                            dumpActivityVisibilityState(fToken, t);
                        }
                    }, delay);
                }
            }

        } catch (Exception e) {
            Log.e(TAG, "Failed to dispatch Android lifecycle", e);
        }
    }

    /**
     * G2.14as visibility-chain probe — reflection dump of Activity + DecorView
     * visibility state.  Triggered 300ms after ANDROID_STATE_RESUMED dispatch.
     * Outputs to hilog + stderr.
     */
    private static void dumpActivityVisibilityState(IBinder activityToken, int timepoint_ms) {
        // G2.14as r9: 'timepoint_ms' is the delay (in ms) after RESUMED dispatch
        // when this dump fires. Prefix log lines so multi-timepoint runs are
        // distinguishable in hilog (T=500 / T=1500 / T=3000 / T=5000 / T=8000).
        final String TP = "[T=" + timepoint_ms + "ms]";
        try {
            Class<?> atClass = Class.forName("android.app.ActivityThread");
            Object at = atClass.getMethod("currentActivityThread").invoke(null);
            if (at == null) {
                Log.e(TAG, TP + "[G2.14as-VIS] currentActivityThread() == null");
                return;
            }
            java.lang.reflect.Field mActivitiesField = atClass.getDeclaredField("mActivities");
            mActivitiesField.setAccessible(true);
            Object mActivities = mActivitiesField.get(at);
            // mActivities is ArrayMap<IBinder, ActivityClientRecord>
            java.lang.reflect.Method getMethod = mActivities.getClass().getMethod("get", Object.class);
            Object r = getMethod.invoke(mActivities, activityToken);
            if (r == null) {
                Log.e(TAG, TP + "[G2.14as-VIS] mActivities.get(token) == null; token=" + activityToken);
                System.err.println(TP + "[G2.14as-VIS] mActivities.get(token)==null token=" + activityToken);
                return;
            }
            // ActivityClientRecord r
            Class<?> rClass = r.getClass();
            // r.activity
            java.lang.reflect.Field activityField = rClass.getDeclaredField("activity");
            activityField.setAccessible(true);
            Object activity = activityField.get(r);
            // r.hideForNow
            java.lang.reflect.Field hideForNowField = rClass.getDeclaredField("hideForNow");
            hideForNowField.setAccessible(true);
            boolean hideForNow = hideForNowField.getBoolean(r);
            // r.getLifecycleState()
            int lifecycleState = -1;
            try {
                java.lang.reflect.Method getLifecycle = rClass.getDeclaredMethod("getLifecycleState");
                getLifecycle.setAccessible(true);
                Object ls = getLifecycle.invoke(r);
                lifecycleState = (ls instanceof Integer) ? (Integer) ls : -1;
            } catch (NoSuchMethodException ignore) { }

            if (activity == null) {
                Log.e(TAG, TP + "[G2.14as-VIS] r.activity == null; hideForNow=" + hideForNow
                        + " lifecycleState=" + lifecycleState);
                System.err.println(TP + "[G2.14as-VIS] r.activity==null hideForNow=" + hideForNow
                        + " lifecycleState=" + lifecycleState);
                return;
            }
            Class<?> aClass = Class.forName("android.app.Activity");
            java.lang.reflect.Field mVFC = aClass.getDeclaredField("mVisibleFromClient");
            mVFC.setAccessible(true);
            boolean mVisibleFromClient = mVFC.getBoolean(activity);
            java.lang.reflect.Field mVFS = aClass.getDeclaredField("mVisibleFromServer");
            mVFS.setAccessible(true);
            boolean mVisibleFromServer = mVFS.getBoolean(activity);
            java.lang.reflect.Field mFinishedF = aClass.getDeclaredField("mFinished");
            mFinishedF.setAccessible(true);
            boolean mFinished = mFinishedF.getBoolean(activity);
            java.lang.reflect.Field mWindowAddedF = aClass.getDeclaredField("mWindowAdded");
            mWindowAddedF.setAccessible(true);
            boolean mWindowAdded = mWindowAddedF.getBoolean(activity);
            java.lang.reflect.Field mDecorF = aClass.getDeclaredField("mDecor");
            mDecorF.setAccessible(true);
            Object mDecor = mDecorF.get(activity);
            int decorVisibility = -1;
            int decorPrivateFlags = -1;
            int decorWidth = -1, decorHeight = -1;
            String decorClassName = "null";
            if (mDecor != null) {
                decorClassName = mDecor.getClass().getName();
                Class<?> viewClass = Class.forName("android.view.View");
                java.lang.reflect.Method getVis = viewClass.getMethod("getVisibility");
                decorVisibility = (Integer) getVis.invoke(mDecor);
                java.lang.reflect.Field pfFld = viewClass.getDeclaredField("mPrivateFlags");
                pfFld.setAccessible(true);
                decorPrivateFlags = pfFld.getInt(mDecor);
                decorWidth = (Integer) viewClass.getMethod("getWidth").invoke(mDecor);
                decorHeight = (Integer) viewClass.getMethod("getHeight").invoke(mDecor);
            }

            String msg = TP + "[G2.14as-VIS] dump after RESUMED+300ms: "
                    + "mVisibleFromClient=" + mVisibleFromClient
                    + " mVisibleFromServer=" + mVisibleFromServer
                    + " mFinished=" + mFinished
                    + " mWindowAdded=" + mWindowAdded
                    + " hideForNow=" + hideForNow
                    + " lifecycleState=" + lifecycleState
                    + " decorClass=" + decorClassName
                    + " decorVisibility=" + decorVisibility
                    + "(0=VISIBLE,4=INVISIBLE,8=GONE)"
                    + " decorPrivateFlags=0x" + Integer.toHexString(decorPrivateFlags)
                    + " decorSize=" + decorWidth + "x" + decorHeight;
            Log.i(TAG, msg);
            System.err.println(msg);

            // G2.14as r2 — ViewRootImpl + decor children size dump
            // (decorSize=0x0 root cause investigation: distinguish IPC frame vs
            // mWidth/mHeight vs measured size vs child layout)
            if (mDecor != null) {
                try {
                    Class<?> viewClass = Class.forName("android.view.View");
                    java.lang.reflect.Method getViewRootImplMethod =
                            viewClass.getMethod("getViewRootImpl");
                    Object viewRoot = getViewRootImplMethod.invoke(mDecor);
                    String vri = TP + "[G2.14as-VRI] viewRoot=null";
                    if (viewRoot != null) {
                        Class<?> vriClass = viewRoot.getClass();
                        // mWinFrame (Rect) — frame set by IPC addToDisplay/relayout
                        Object winFrame = null;
                        try {
                            java.lang.reflect.Field f = vriClass.getDeclaredField("mWinFrame");
                            f.setAccessible(true);
                            winFrame = f.get(viewRoot);
                        } catch (NoSuchFieldException ignore) { }
                        // mWidth / mHeight — what ViewRoot uses for measure
                        int vriW = -1, vriH = -1;
                        try {
                            java.lang.reflect.Field wf = vriClass.getDeclaredField("mWidth");
                            wf.setAccessible(true);
                            vriW = wf.getInt(viewRoot);
                            java.lang.reflect.Field hf = vriClass.getDeclaredField("mHeight");
                            hf.setAccessible(true);
                            vriH = hf.getInt(viewRoot);
                        } catch (NoSuchFieldException ignore) { }
                        // Measured size of decor
                        int decorMeasW = (Integer) viewClass.getMethod("getMeasuredWidth").invoke(mDecor);
                        int decorMeasH = (Integer) viewClass.getMethod("getMeasuredHeight").invoke(mDecor);
                        // decor.mLeft/Top/Right/Bottom (layout output)
                        java.lang.reflect.Field mLeft = viewClass.getDeclaredField("mLeft");
                        mLeft.setAccessible(true);
                        java.lang.reflect.Field mTop = viewClass.getDeclaredField("mTop");
                        mTop.setAccessible(true);
                        java.lang.reflect.Field mRight = viewClass.getDeclaredField("mRight");
                        mRight.setAccessible(true);
                        java.lang.reflect.Field mBottom = viewClass.getDeclaredField("mBottom");
                        mBottom.setAccessible(true);
                        int dL = mLeft.getInt(mDecor), dT = mTop.getInt(mDecor),
                            dR = mRight.getInt(mDecor), dB = mBottom.getInt(mDecor);
                        vri = TP + "[G2.14as-VRI] viewRoot=" + vriClass.getSimpleName()
                                + " mWinFrame=" + (winFrame == null ? "null" : winFrame.toString())
                                + " mWidth=" + vriW + " mHeight=" + vriH
                                + " decorMeasured=" + decorMeasW + "x" + decorMeasH
                                + " decorLayout=[" + dL + "," + dT + "," + dR + "," + dB + "]";
                    }
                    Log.i(TAG, vri);
                    System.err.println(vri);

                    // G2.14as r3 — dump mTmpFrames (ClientWindowFrames returned
                    // by IWindowSession.relayout) to confirm adapter filled it
                    if (viewRoot != null) {
                        try {
                            java.lang.reflect.Field tfF = viewRoot.getClass().getDeclaredField("mTmpFrames");
                            tfF.setAccessible(true);
                            Object cwf = tfF.get(viewRoot);
                            if (cwf != null) {
                                Class<?> cwfClass = cwf.getClass();
                                Object frameObj = null, displayFrameObj = null, attachedFrameObj = null, parentFrameObj = null;
                                try { java.lang.reflect.Field f = cwfClass.getField("frame"); frameObj = f.get(cwf); } catch (Throwable ignore) { }
                                try { java.lang.reflect.Field f = cwfClass.getField("displayFrame"); displayFrameObj = f.get(cwf); } catch (Throwable ignore) { }
                                try { java.lang.reflect.Field f = cwfClass.getField("attachedFrame"); attachedFrameObj = f.get(cwf); } catch (Throwable ignore) { }
                                try { java.lang.reflect.Field f = cwfClass.getField("parentFrame"); parentFrameObj = f.get(cwf); } catch (Throwable ignore) { }
                                String tfmsg = TP + "[G2.14as-CWF] mTmpFrames="
                                        + cwfClass.getSimpleName()
                                        + " frame=" + (frameObj == null ? "null" : frameObj.toString())
                                        + " displayFrame=" + (displayFrameObj == null ? "null" : displayFrameObj.toString())
                                        + " attachedFrame=" + (attachedFrameObj == null ? "null" : attachedFrameObj.toString())
                                        + " parentFrame=" + (parentFrameObj == null ? "null" : parentFrameObj.toString());
                                Log.i(TAG, tfmsg);
                                System.err.println(tfmsg);
                            }
                        } catch (Throwable t3) {
                            Log.e(TAG, TP + "[G2.14as-CWF] mTmpFrames dump failed", t3);
                        }

                        // G2.14as r5 — option C — probe ViewRoot's other frame
                        // history / lifecycle fields to distinguish:
                        //   (1) timing race (resized fired but probe ran first) —
                        //       eliminated by 2000ms delay
                        //   (2) handleResized fired but setFrame skipped
                        //       (mForceNextWindowRelayout, mLastLayoutFrame)
                        //   (3) ViewRoot reset mWinFrame again after handleResized
                        //       (mFirst, mAdded, mPendingDragResizing flags)
                        // G2.14as r8 — add performDraw skip reason fields:
                        //   mLastPerformDrawSkippedReason, mLastPerformTraversalsSkipDrawReason
                        //   These are populated by ViewRoot when performDraw returns
                        //   false or performTraversals decides not to draw.
                        try {
                            Class<?> vc = viewRoot.getClass();
                            StringBuilder pb = new StringBuilder(TP + "[G2.14as-VR2]");
                            for (String fn : new String[]{
                                    "mFirst", "mAdded", "mForceNextWindowRelayout",
                                    "mPendingDragResizing", "mLastLayoutFrame",
                                    "mWinFrameInScreen", "mLastReportedMergedConfiguration",
                                    "mMeasuredWidth", "mMeasuredHeight",
                                    "mRelayoutSeq", "mSyncSeqId", "mLastSyncSeqId",
                                    "mPendingMergedConfiguration",
                                    "mLastPerformDrawSkippedReason",
                                    "mLastPerformTraversalsSkipDrawReason",
                                    "mLastReportNextDrawReason",
                                    "mWillDrawSoon", "mIsInTraversal",
                                    "mFullRedrawNeeded", "mNewSurfaceNeeded",
                                    "mViewVisibility", "mAppVisible",
                                    "mDirty", "mDrewOnceForSync", "mReportNextDraw"}) {
                                try {
                                    // Try declared field; if not on ViewRootImpl, try super or AttachInfo
                                    java.lang.reflect.Field f = null;
                                    try {
                                        f = vc.getDeclaredField(fn);
                                    } catch (NoSuchFieldException nsf2) {
                                        // also try AttachInfo for mDisplayState etc.
                                    }
                                    if (f != null) {
                                        f.setAccessible(true);
                                        Object v = f.get(viewRoot);
                                        pb.append(" ").append(fn).append("=").append(v);
                                    } else {
                                        pb.append(" ").append(fn).append("=<no-field>");
                                    }
                                } catch (Throwable ignore) { }
                            }
                            Log.i(TAG, pb.toString());
                            System.err.println(pb.toString());
                        } catch (Throwable t4) {
                            Log.e(TAG, TP + "[G2.14as-VR2] sub-dump failed", t4);
                        }
                    }

                    // Dump decor child sizes (1 level deep)
                    Class<?> vgClass = Class.forName("android.view.ViewGroup");
                    int childCount = (Integer) vgClass.getMethod("getChildCount").invoke(mDecor);
                    StringBuilder sb = new StringBuilder(TP + "[G2.14as-CHD] decor childCount=" + childCount);
                    int n = Math.min(childCount, 4);
                    for (int i = 0; i < n; i++) {
                        Object child = vgClass.getMethod("getChildAt", int.class).invoke(mDecor, i);
                        if (child == null) continue;
                        int cw = (Integer) viewClass.getMethod("getMeasuredWidth").invoke(child);
                        int ch = (Integer) viewClass.getMethod("getMeasuredHeight").invoke(child);
                        int cv = (Integer) viewClass.getMethod("getVisibility").invoke(child);
                        int gw = (Integer) viewClass.getMethod("getWidth").invoke(child);
                        int gh = (Integer) viewClass.getMethod("getHeight").invoke(child);
                        sb.append(" | child[").append(i).append("]=")
                                .append(child.getClass().getSimpleName())
                                .append(" meas=").append(cw).append("x").append(ch)
                                .append(" size=").append(gw).append("x").append(gh)
                                .append(" vis=").append(cv);
                    }
                    Log.i(TAG, sb.toString());
                    System.err.println(sb.toString());

                    // G2.14as r10 — deep-recurse VRT (View RenderNode Tree) dump.
                    // r7/r8/r9 only probed up to lvl2 (FrameLayout, ActionBarContainer).
                    // memBytes ~344 for those is consistent with drawRenderNode refs
                    // (container ops), NOT actual drawText/drawRect ops which live in
                    // leaf View RenderNodes (TextView, Button, etc.).  This probe
                    // recurses to depth 8 to find leaves with real draw ops.
                    if (timepoint_ms == 1500 || timepoint_ms == 5000) {
                        try {
                            Class<?> rnClass = Class.forName("android.graphics.RenderNode");
                            java.lang.reflect.Method hasDlM = rnClass.getMethod("hasDisplayList");
                            java.lang.reflect.Method memM = rnClass.getMethod("computeApproximateMemoryUsage");
                            System.err.println(TP + "[G2.14as-VRT] tree (depth-first):");
                            Log.i(TAG, TP + "[G2.14as-VRT] tree (depth-first):");
                            dumpRenderNodeTreeOneLinePerView(mDecor, 0, 8, viewClass, rnClass, hasDlM, memM, TP);
                        } catch (Throwable t6) {
                            Log.e(TAG, TP + "[G2.14as-VRT] deep recurse failed", t6);
                            System.err.println(TP + "[G2.14as-VRT] deep recurse failed: " + t6);
                        }
                    }

                    // G2.14as r7 — VRN (View RenderNode) data 1 probe:
                    // UI thread → RT DisplayList content size.  AOSP RenderNode
                    // has @hide public method computeApproximateMemoryUsage() that
                    // returns the byte size of the recorded DisplayList ops.
                    // 0 or near-0 = nothing recorded; > 1KB = ops are recorded.
                    try {
                        Class<?> rnClass = Class.forName("android.graphics.RenderNode");
                        // hasDisplayList() and computeApproximateMemoryUsage() are
                        // public hidden API; both safe to reflect
                        java.lang.reflect.Method hasDl =
                                rnClass.getMethod("hasDisplayList");
                        java.lang.reflect.Method memUsage =
                                rnClass.getMethod("computeApproximateMemoryUsage");

                        java.util.function.BiFunction<Object, String, String> dumpRn = (view, label) -> {
                            try {
                                java.lang.reflect.Field rnf = viewClass.getDeclaredField("mRenderNode");
                                rnf.setAccessible(true);
                                Object rn = rnf.get(view);
                                if (rn == null) return label + "=mRenderNode:null";
                                Boolean has = (Boolean) hasDl.invoke(rn);
                                Long mem = (Long) memUsage.invoke(rn);
                                return label + "=" + view.getClass().getSimpleName()
                                        + "{hasDL=" + has + " memBytes=" + mem + "}";
                            } catch (Throwable t) {
                                return label + "=ERR:" + t.getClass().getSimpleName();
                            }
                        };

                        StringBuilder vrn = new StringBuilder(TP + "[G2.14as-VRN] ");
                        vrn.append(dumpRn.apply(mDecor, "decor"));
                        // recurse 2 levels deep — DecorView → 1st child → 1st grandchild
                        if (childCount > 0) {
                            Object lvl1 = vgClass.getMethod("getChildAt", int.class).invoke(mDecor, 0);
                            if (lvl1 != null) {
                                vrn.append(" | ").append(dumpRn.apply(lvl1, "lvl1"));
                                if (lvl1.getClass().getSuperclass() != null
                                        && vgClass.isInstance(lvl1)) {
                                    int gcc = (Integer) vgClass.getMethod("getChildCount").invoke(lvl1);
                                    int gN = Math.min(gcc, 3);
                                    for (int gi = 0; gi < gN; gi++) {
                                        Object lvl2 = vgClass.getMethod("getChildAt", int.class).invoke(lvl1, gi);
                                        if (lvl2 != null) {
                                            vrn.append(" | ").append(dumpRn.apply(lvl2, "lvl2[" + gi + "]"));
                                        }
                                    }
                                }
                            }
                        }
                        Log.i(TAG, vrn.toString());
                        System.err.println(vrn.toString());
                    } catch (Throwable t5) {
                        Log.e(TAG, TP + "[G2.14as-VRN] RenderNode probe failed", t5);
                        System.err.println(TP + "[G2.14as-VRN] RenderNode probe failed: " + t5);
                    }
                } catch (Throwable t2) {
                    Log.e(TAG, TP + "[G2.14as-VRI] sub-dump failed", t2);
                    System.err.println(TP + "[G2.14as-VRI] sub-dump failed: " + t2);
                }
            }
        } catch (Throwable t) {
            Log.e(TAG, TP + "[G2.14as-VIS] reflection dump failed", t);
            System.err.println(TP + "[G2.14as-VIS] reflection dump failed: " + t);
        }
    }

    /**
     * G2.14as r10 — recursive RenderNode tree dump.  Walks the View hierarchy
     * depth-first to maxDepth, printing each View's class + hasDL + memBytes.
     * For TextView, also dumps a 60-char prefix of its text.  This finds the
     * leaves where actual draw ops (drawText/drawRect) are recorded — DecorView
     * etc. only contain drawRenderNode-ref ops.
     */
    /**
     * G2.14as r11 — one-line-per-View variant.  Each View emits a single
     * Log.i/System.err.println line tagged with timepoint TP prefix.  Avoids
     * hilog single-line truncation (~4KB) and surfaces partial failures
     * (one bad View doesn't kill the whole dump).
     */
    private static void dumpRenderNodeTreeOneLinePerView(Object view, int depth, int maxDepth,
            Class<?> viewClass, Class<?> rnClass,
            java.lang.reflect.Method hasDlM, java.lang.reflect.Method memM,
            String tpPrefix) {
        if (view == null || depth > maxDepth) return;
        try {
            StringBuilder line = new StringBuilder(tpPrefix);
            line.append("[G2.14as-VRT] ");
            for (int i = 0; i < depth; i++) line.append("..");

            long memBytes = -1L;
            boolean hasDl = false;
            try {
                java.lang.reflect.Field rnf = viewClass.getDeclaredField("mRenderNode");
                rnf.setAccessible(true);
                Object rn = rnf.get(view);
                if (rn != null) {
                    hasDl = (Boolean) hasDlM.invoke(rn);
                    memBytes = (Long) memM.invoke(rn);
                }
            } catch (Throwable ignore) { }

            int w = -1, h = -1, vis = -1;
            try {
                w = (Integer) viewClass.getMethod("getWidth").invoke(view);
                h = (Integer) viewClass.getMethod("getHeight").invoke(view);
                vis = (Integer) viewClass.getMethod("getVisibility").invoke(view);
            } catch (Throwable ignore) { }

            line.append(view.getClass().getSimpleName())
                    .append("@").append(System.identityHashCode(view))
                    .append("{DL=").append(hasDl ? "T" : "F")
                    .append(" mem=").append(memBytes)
                    .append(" ").append(w).append("x").append(h)
                    .append(" v=").append(vis);

            try {
                Class<?> tvClass = Class.forName("android.widget.TextView");
                if (tvClass.isInstance(view)) {
                    java.lang.reflect.Method getText = tvClass.getMethod("getText");
                    CharSequence text = (CharSequence) getText.invoke(view);
                    String s = text == null ? "<null>" : text.toString();
                    if (s.length() > 50) s = s.substring(0, 50) + "...";
                    line.append(" text='").append(s).append("'");
                }
            } catch (Throwable ignore) { }
            line.append("}");

            Log.i(TAG, line.toString());
            System.err.println(line.toString());

            // recurse
            Class<?> vgClass = Class.forName("android.view.ViewGroup");
            if (vgClass.isInstance(view)) {
                int childCount = (Integer) vgClass.getMethod("getChildCount").invoke(view);
                int n = Math.min(childCount, 12);
                for (int i = 0; i < n; i++) {
                    Object child = vgClass.getMethod("getChildAt", int.class).invoke(view, i);
                    if (child != null) {
                        dumpRenderNodeTreeOneLinePerView(child, depth + 1, maxDepth,
                                viewClass, rnClass, hasDlM, memM, tpPrefix);
                    }
                }
            }
        } catch (Throwable t) {
            String msg = tpPrefix + "[G2.14as-VRT] depth=" + depth + " ERR: " + t;
            Log.e(TAG, msg);
            System.err.println(msg);
        }
    }

    private static void dumpRenderNodeTree(Object view, int depth, int maxDepth,
            Class<?> viewClass, Class<?> rnClass,
            java.lang.reflect.Method hasDlM, java.lang.reflect.Method memM,
            StringBuilder sb) throws Exception {
        if (view == null || depth > maxDepth) return;
        // indent
        for (int i = 0; i < depth; i++) sb.append("  ");
        // read RenderNode
        long memBytes = -1L;
        boolean hasDl = false;
        try {
            java.lang.reflect.Field rnf = viewClass.getDeclaredField("mRenderNode");
            rnf.setAccessible(true);
            Object rn = rnf.get(view);
            if (rn != null) {
                hasDl = (Boolean) hasDlM.invoke(rn);
                memBytes = (Long) memM.invoke(rn);
            }
        } catch (Throwable ignore) { }

        int w = (Integer) viewClass.getMethod("getWidth").invoke(view);
        int h = (Integer) viewClass.getMethod("getHeight").invoke(view);
        int vis = (Integer) viewClass.getMethod("getVisibility").invoke(view);

        sb.append(view.getClass().getSimpleName())
                .append("{hasDL=").append(hasDl)
                .append(" mem=").append(memBytes)
                .append(" size=").append(w).append("x").append(h)
                .append(" vis=").append(vis);

        // TextView text snippet
        try {
            Class<?> tvClass = Class.forName("android.widget.TextView");
            if (tvClass.isInstance(view)) {
                java.lang.reflect.Method getText = tvClass.getMethod("getText");
                CharSequence text = (CharSequence) getText.invoke(view);
                String s = text == null ? "<null>" : text.toString();
                if (s.length() > 60) s = s.substring(0, 60) + "...";
                sb.append(" text='").append(s).append("'");
            }
        } catch (Throwable ignore) { }

        sb.append("}\n");

        // recurse into children if ViewGroup
        try {
            Class<?> vgClass = Class.forName("android.view.ViewGroup");
            if (vgClass.isInstance(view)) {
                int childCount = (Integer) vgClass.getMethod("getChildCount").invoke(view);
                int n = Math.min(childCount, 12);
                for (int i = 0; i < n; i++) {
                    Object child = vgClass.getMethod("getChildAt", int.class).invoke(view, i);
                    if (child != null) {
                        dumpRenderNodeTree(child, depth + 1, maxDepth,
                                viewClass, rnClass, hasDlM, memM, sb);
                    }
                }
            }
        } catch (Throwable ignore) { }
    }

    // ==================== Activity Token Management ====================

    // Maps OH ability tokens to Android Activity IBinder tokens
    private final Map<Integer, IBinder> mTokenMap = new ConcurrentHashMap<>();

    /**
     * Register an Android Activity token for an OH ability token.
     * Called when a new Activity is launched via the adapter.
     */
    public void registerActivityToken(int ohToken, IBinder activityToken) {
        mTokenMap.put(ohToken, activityToken);
        Log.d(TAG, "Registered activity token for OH token " + ohToken);
    }

    /**
     * Remove a token mapping.
     */
    public void unregisterActivityToken(int ohToken) {
        mTokenMap.remove(ohToken);
    }

    /**
     * Get the Android Activity IBinder token for an OH ability token.
     */
    private IBinder getActivityToken(int ohToken) {
        return mTokenMap.get(ohToken);
    }

    // ==================== Utility Methods ====================

    private static String androidStateName(int state) {
        switch (state) {
            case ANDROID_STATE_CREATED:   return "CREATED";
            case ANDROID_STATE_STARTED:   return "STARTED";
            case ANDROID_STATE_RESUMED:   return "RESUMED";
            case ANDROID_STATE_PAUSED:    return "PAUSED";
            case ANDROID_STATE_STOPPED:   return "STOPPED";
            case ANDROID_STATE_DESTROYED: return "DESTROYED";
            default: return "UNKNOWN(" + state + ")";
        }
    }

    private static String ohStateName(int state) {
        switch (state) {
            case OH_STATE_INITIAL:        return "INITIAL";
            case OH_STATE_INACTIVE:       return "INACTIVE";
            case OH_STATE_FOREGROUND_NEW: return "FOREGROUND";
            case OH_STATE_BACKGROUND_NEW: return "BACKGROUND";
            default: return "UNKNOWN(" + state + ")";
        }
    }
}
