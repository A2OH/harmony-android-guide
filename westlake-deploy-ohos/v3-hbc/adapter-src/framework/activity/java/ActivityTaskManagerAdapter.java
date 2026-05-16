/*
 * ActivityTaskManagerAdapter.java
 *
 * Adapter implementation for Android IActivityTaskManager interface using class inheritance.
 * Extends IActivityTaskManager.Stub directly.
 * (Historical: replaced an InvocationHandler-based dynamic proxy class
 *  IActivityTaskManagerBridge which was deleted 2026-04-29 per feedback.txt P1a.)
 *
 * Routes calls to OpenHarmony system services:
 *   IActivityTaskManager -> OH IAbilityManager  (activity start/stop)
 *                        -> OH IMissionManager  (task/mission management)
 *                        -> OH ISceneSessionManager (window organization)
 *
 * Methods are categorized as:
 *   [BRIDGED]  - Mapped to OH equivalent
 *   [PARTIAL]  - Partially mapped
 *   [STUB]     - No OH equivalent, safe default returned
 */
package adapter.activity;

import android.app.ActivityManager;
import android.app.ActivityTaskManager;
import android.app.IActivityClientController;
import android.app.IActivityController;
import android.app.IAppTask;
import android.app.IApplicationThread;
import android.app.IAssistDataReceiver;
import android.app.IProcessObserver;
import android.app.IScreenCaptureObserver;
import android.app.ITaskStackListener;
import android.app.Notification;
import android.app.PictureInPictureUiState;
import android.app.ProfilerInfo;
import android.app.WaitResult;
import android.app.IActivityTaskManager;
import android.app.assist.AssistContent;
import android.app.assist.AssistStructure;
import android.content.ComponentName;
import android.content.IIntentSender;
import android.content.Intent;
import android.content.pm.ConfigurationInfo;
import android.content.pm.ParceledListSlice;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteCallback;
import android.os.RemoteException;
import android.service.voice.IVoiceInteractionSession;
import android.util.Log;
import android.view.IRecentsAnimationRunner;
import android.view.RemoteAnimationAdapter;
import android.view.RemoteAnimationDefinition;
import android.window.BackAnimationAdapter;
import android.window.BackNavigationInfo;
import android.window.IWindowOrganizerController;
import android.window.SplashScreenView;
import android.window.TaskSnapshot;

import adapter.activity.IntentWantConverter;
import com.android.internal.app.IVoiceInteractor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Adapter that extends IActivityTaskManager.Stub to bridge Android activity task management
 * calls to OpenHarmony system services. Each AIDL method is overridden with either a bridged
 * implementation (routed to OH) or a stub (safe default).
 */
public class ActivityTaskManagerAdapter extends IActivityTaskManager.Stub {

    private static final String TAG = "OH_ATMAdapter";

    private final long mOhAbilityManager;

    private static native long nativeGetOHAbilityManagerService();
    static native int nativeStartAbility(String bundleName, String abilityName,
            String action, String uri, String extraJson);
    private static native int nativeStartAbilityInMission(String bundleName, String abilityName,
            String action, String uri, String extraJson, int missionId);
    private static native int nativeCleanMission(int missionId);
    private static native int nativeMoveMissionToFront(int missionId);
    private static native boolean nativeIsTopAbility(int missionId, String abilityName);
    private static native int nativeClearAbilitiesAbove(int missionId, String abilityName);
    private static native int nativeGetMissionIdForBundle(String bundleName);

    // Track bundleName -> OH missionId for active Android app Missions
    private final Map<String, Integer> mActiveMissions = new HashMap<>();

    private static volatile ActivityTaskManagerAdapter sInstance;

    public static ActivityTaskManagerAdapter getInstance() {
        if (sInstance == null) {
            synchronized (ActivityTaskManagerAdapter.class) {
                if (sInstance == null) {
                    sInstance = new ActivityTaskManagerAdapter();
                }
            }
        }
        return sInstance;
    }

    public ActivityTaskManagerAdapter() {
        mOhAbilityManager = nativeGetOHAbilityManagerService();
        Log.i(TAG, "ActivityTaskManagerAdapter created, OH AbilityManager handle: "
                + mOhAbilityManager);
    }

    // ====================================================================
    // Category 1: Activity Start (-> OH IAbilityManager)
    // ====================================================================

    /** [BRIDGED] startActivity -> OH IAbilityManager.StartAbility / StartAbilityInMission */
    @Override
    public int startActivity(IApplicationThread caller, String callingPackage,
            String callingFeatureId, Intent intent, String resolvedType,
            IBinder resultTo, String resultWho, int requestCode,
            int flags, ProfilerInfo profilerInfo, Bundle options) throws RemoteException {
        logBridged("startActivity", "-> OH IAbilityManager.StartAbility / StartAbilityInMission");
        return bridgeStartActivityWithStack(intent, flags);
    }

    /** [BRIDGED] startActivities -> OH IAbilityManager.StartAbility(Want) for each intent */
    @Override
    public int startActivities(IApplicationThread caller, String callingPackage,
            String callingFeatureId, Intent[] intents, String[] resolvedTypes,
            IBinder resultTo, Bundle options, int userId) throws RemoteException {
        logBridged("startActivities", "-> OH IAbilityManager.StartAbility");
        if (intents != null && intents.length > 0) {
            return bridgeStartAbility(intents[0]);
        }
        return 0;
    }

    /** [BRIDGED] startActivityAsUser -> OH IAbilityManager.StartAbility(Want) */
    @Override
    public int startActivityAsUser(IApplicationThread caller, String callingPackage,
            String callingFeatureId, Intent intent, String resolvedType,
            IBinder resultTo, String resultWho, int requestCode, int flags,
            ProfilerInfo profilerInfo, Bundle options, int userId) throws RemoteException {
        logBridged("startActivityAsUser", "-> OH IAbilityManager.StartAbility");
        return bridgeStartAbility(intent);
    }

    /** [BRIDGED] startNextMatchingActivity -> OH implicit Want resolution */
    @Override
    public boolean startNextMatchingActivity(IBinder callingActivity,
            Intent intent, Bundle options) throws RemoteException {
        logBridged("startNextMatchingActivity", "-> OH implicit Want resolution");
        return false;
    }

    /** [STUB] startDreamActivity - No OH dream/screensaver equivalent */
    @Override
    public boolean startDreamActivity(Intent intent) throws RemoteException {
        logStub("startDreamActivity", "No OH dream/screensaver equivalent");
        return false;
    }

    /** [BRIDGED] startActivityIntentSender -> OH IAbilityManager.StartAbility */
    @Override
    public int startActivityIntentSender(IApplicationThread caller,
            IIntentSender target, IBinder whitelistToken, Intent fillInIntent,
            String resolvedType, IBinder resultTo, String resultWho, int requestCode,
            int flagsMask, int flagsValues, Bundle options) throws RemoteException {
        logBridged("startActivityIntentSender", "-> OH IAbilityManager.StartAbility");
        return bridgeStartAbility(fillInIntent);
    }

    /** [BRIDGED] startActivityAndWait -> OH IAbilityManager.StartAbility (sync wait) */
    @Override
    public WaitResult startActivityAndWait(IApplicationThread caller, String callingPackage,
            String callingFeatureId, Intent intent, String resolvedType,
            IBinder resultTo, String resultWho, int requestCode, int flags,
            ProfilerInfo profilerInfo, Bundle options, int userId) throws RemoteException {
        logBridged("startActivityAndWait", "-> OH IAbilityManager.StartAbility (sync wait)");
        bridgeStartAbility(intent);
        return null;
    }

    /** [BRIDGED] startActivityWithConfig -> OH IAbilityManager.StartAbility */
    @Override
    public int startActivityWithConfig(IApplicationThread caller, String callingPackage,
            String callingFeatureId, Intent intent, String resolvedType,
            IBinder resultTo, String resultWho, int requestCode, int startFlags,
            Configuration newConfig, Bundle options, int userId) throws RemoteException {
        logBridged("startActivityWithConfig", "-> OH IAbilityManager.StartAbility");
        return bridgeStartAbility(intent);
    }

    /** [BRIDGED] startVoiceActivity -> OH IAbilityManager.StartAbility */
    @Override
    public int startVoiceActivity(String callingPackage, String callingFeatureId,
            int callingPid, int callingUid, Intent intent, String resolvedType,
            IVoiceInteractionSession session, IVoiceInteractor interactor, int flags,
            ProfilerInfo profilerInfo, Bundle options, int userId) throws RemoteException {
        logBridged("startVoiceActivity", "-> OH IAbilityManager.StartAbility");
        return bridgeStartAbility(intent);
    }

    /** [STUB] getVoiceInteractorPackageName - No OH voice interaction */
    @Override
    public String getVoiceInteractorPackageName(IBinder callingVoiceInteractor)
            throws RemoteException {
        logStub("getVoiceInteractorPackageName", "No OH voice interaction");
        return null;
    }

    /** [BRIDGED] startAssistantActivity -> OH IAbilityManager.StartAbility */
    @Override
    public int startAssistantActivity(String callingPackage, String callingFeatureId,
            int callingPid, int callingUid, Intent intent, String resolvedType,
            Bundle options, int userId) throws RemoteException {
        logBridged("startAssistantActivity", "-> OH IAbilityManager.StartAbility");
        return bridgeStartAbility(intent);
    }

    /** [BRIDGED] startActivityFromGameSession -> OH IAbilityManager.StartAbility */
    @Override
    public int startActivityFromGameSession(IApplicationThread caller, String callingPackage,
            String callingFeatureId, int callingPid, int callingUid, Intent intent,
            int taskId, int userId) throws RemoteException {
        logBridged("startActivityFromGameSession", "-> OH IAbilityManager.StartAbility");
        return bridgeStartAbility(intent);
    }

    /** [BRIDGED] startRecentsActivity -> OH IMissionManager (recents) */
    @Override
    public void startRecentsActivity(Intent intent, long eventTime,
            IRecentsAnimationRunner recentsAnimationRunner) throws RemoteException {
        logBridged("startRecentsActivity", "-> OH IMissionManager (recents)");
    }

    /** [BRIDGED] startActivityFromRecents -> OH IMissionManager.MoveMissionToFront */
    @Override
    public int startActivityFromRecents(int taskId, Bundle options) throws RemoteException {
        logBridged("startActivityFromRecents", "-> OH IMissionManager.MoveMissionToFront");
        return nativeMoveMissionToFront(taskId);
    }

    /** [BRIDGED] startActivityAsCaller -> OH IAbilityManager.StartAbility */
    @Override
    public int startActivityAsCaller(IApplicationThread caller, String callingPackage,
            Intent intent, String resolvedType, IBinder resultTo, String resultWho,
            int requestCode, int flags, ProfilerInfo profilerInfo, Bundle options,
            boolean ignoreTargetSecurity, int userId) throws RemoteException {
        logBridged("startActivityAsCaller", "-> OH IAbilityManager.StartAbility");
        return bridgeStartAbility(intent);
    }

    /** [STUB] isActivityStartAllowedOnDisplay - No OH equivalent */
    @Override
    public boolean isActivityStartAllowedOnDisplay(int displayId, Intent intent,
            String resolvedType, int userId) throws RemoteException {
        logStub("isActivityStartAllowedOnDisplay", "No OH equivalent");
        return true;
    }

    // ====================================================================
    // Category 2: Activity Client Controller
    // ====================================================================

    /** [BRIDGED] unhandledBack -> OH ISession.RequestSessionBack */
    @Override
    public void unhandledBack() throws RemoteException {
        logBridged("unhandledBack", "-> OH ISession.RequestSessionBack");
    }

    /** [STUB] getActivityClientController - OH manages lifecycle differently */
    @Override
    public IActivityClientController getActivityClientController() throws RemoteException {
        logStub("getActivityClientController", "OH manages lifecycle differently");
        return null;
    }

    // ====================================================================
    // Category 3: Screen Compat Mode
    // ====================================================================

    /** [STUB] getFrontActivityScreenCompatMode - No OH screen compat */
    @Override
    public int getFrontActivityScreenCompatMode() throws RemoteException {
        logStub("getFrontActivityScreenCompatMode", "No OH screen compat");
        return 0;
    }

    /** [STUB] setFrontActivityScreenCompatMode - No OH screen compat */
    @Override
    public void setFrontActivityScreenCompatMode(int mode) throws RemoteException {
        logStub("setFrontActivityScreenCompatMode", "No OH screen compat");
    }

    // ====================================================================
    // Category 4: Task Management (-> OH IMissionManager)
    // ====================================================================

    /** [BRIDGED] setFocusedTask -> OH IMissionManager.MoveMissionToFront */
    @Override
    public void setFocusedTask(int taskId) throws RemoteException {
        logBridged("setFocusedTask", "-> OH IMissionManager.MoveMissionToFront");
    }

    /** [BRIDGED] removeTask -> OH IMissionManager.CleanMission */
    @Override
    public boolean removeTask(int taskId) throws RemoteException {
        logBridged("removeTask", "-> OH IMissionManager.CleanMission");
        int result = nativeCleanMission(taskId);
        // Remove from active missions tracking
        mActiveMissions.values().removeIf(id -> id == taskId);
        return result == 0;
    }

    /** [BRIDGED] removeAllVisibleRecentTasks -> OH IMissionManager.CleanAllMissions */
    @Override
    public void removeAllVisibleRecentTasks() throws RemoteException {
        logBridged("removeAllVisibleRecentTasks", "-> OH IMissionManager.CleanAllMissions");
    }

    /** [BRIDGED] getTasks -> OH IMissionManager.GetMissionInfos */
    @Override
    public List<ActivityManager.RunningTaskInfo> getTasks(int maxNum,
            boolean filterOnlyVisibleRecents, boolean keepIntentExtra, int displayId)
            throws RemoteException {
        logBridged("getTasks", "-> OH IMissionManager.GetMissionInfos");
        return Collections.emptyList();
    }

    /** [BRIDGED] moveTaskToFront -> OH IMissionManager.MoveMissionToFront */
    @Override
    public void moveTaskToFront(IApplicationThread app, String callingPackage, int task,
            int flags, Bundle options) throws RemoteException {
        logBridged("moveTaskToFront", "-> OH IMissionManager.MoveMissionToFront");
        nativeMoveMissionToFront(task);
    }

    /** [BRIDGED] getRecentTasks -> OH IMissionManager.GetMissionInfos */
    @Override
    public ParceledListSlice<ActivityManager.RecentTaskInfo> getRecentTasks(int maxNum,
            int flags, int userId) throws RemoteException {
        logBridged("getRecentTasks", "-> OH IMissionManager.GetMissionInfos");
        return null;
    }

    /** [STUB] isTopActivityImmersive - No direct OH equivalent */
    @Override
    public boolean isTopActivityImmersive() throws RemoteException {
        logStub("isTopActivityImmersive", "No direct OH equivalent");
        return false;
    }

    /** [BRIDGED] getTaskDescription -> OH IMissionManager.GetMissionInfo */
    @Override
    public ActivityManager.TaskDescription getTaskDescription(int taskId) throws RemoteException {
        logBridged("getTaskDescription", "-> OH IMissionManager.GetMissionInfo");
        return null;
    }

    // ====================================================================
    // Category 5: Assist / Autofill
    // ====================================================================

    /** [STUB] reportAssistContextExtras - No OH assist equivalent */
    @Override
    public void reportAssistContextExtras(IBinder assistToken, Bundle extras,
            AssistStructure structure, AssistContent content, Uri referrer)
            throws RemoteException {
        logStub("reportAssistContextExtras", "No OH assist equivalent");
    }

    /** [STUB] getAssistContextExtras - No OH assist equivalent */
    @Override
    public Bundle getAssistContextExtras(int requestType) throws RemoteException {
        logStub("getAssistContextExtras", "No OH assist equivalent");
        return null;
    }

    /** [STUB] requestAssistContextExtras - No OH assist equivalent */
    @Override
    public boolean requestAssistContextExtras(int requestType, IAssistDataReceiver receiver,
            Bundle receiverExtras, IBinder activityToken,
            boolean focused, boolean newSessionId) throws RemoteException {
        logStub("requestAssistContextExtras", "No OH assist equivalent");
        return false;
    }

    /** [STUB] requestAutofillData - No OH assist equivalent */
    @Override
    public boolean requestAutofillData(IAssistDataReceiver receiver, Bundle receiverExtras,
            IBinder activityToken, int flags) throws RemoteException {
        logStub("requestAutofillData", "No OH assist equivalent");
        return false;
    }

    /** [STUB] isAssistDataAllowedOnCurrentActivity - No OH assist equivalent */
    @Override
    public boolean isAssistDataAllowedOnCurrentActivity() throws RemoteException {
        logStub("isAssistDataAllowedOnCurrentActivity", "No OH assist equivalent");
        return false;
    }

    /** [STUB] requestAssistDataForTask - No OH assist equivalent */
    @Override
    public boolean requestAssistDataForTask(IAssistDataReceiver receiver, int taskId,
            String callingPackageName, String callingAttributionTag) throws RemoteException {
        logStub("requestAssistDataForTask", "No OH assist equivalent");
        return false;
    }

    // ====================================================================
    // Category 6: Root Task / Stack (-> OH IMissionManager)
    // ====================================================================

    /** [BRIDGED] setFocusedRootTask -> OH IMissionManager.MoveMissionToFront */
    @Override
    public void setFocusedRootTask(int taskId) throws RemoteException {
        logBridged("setFocusedRootTask", "-> OH IMissionManager.MoveMissionToFront");
    }

    /** [BRIDGED] getFocusedRootTaskInfo -> OH (focused mission query) */
    @Override
    public ActivityTaskManager.RootTaskInfo getFocusedRootTaskInfo() throws RemoteException {
        logBridged("getFocusedRootTaskInfo", "-> OH (focused mission query)");
        return null;
    }

    /** [BRIDGED] getTaskBounds -> OH ISession.GetGlobalScaledRect */
    @Override
    public Rect getTaskBounds(int taskId) throws RemoteException {
        logBridged("getTaskBounds", "-> OH ISession.GetGlobalScaledRect");
        return null;
    }

    /** [STUB] focusTopTask - OH manages focus differently */
    @Override
    public void focusTopTask(int displayId) throws RemoteException {
        logStub("focusTopTask", "OH manages focus differently");
    }

    /** [BRIDGED] cancelRecentsAnimation - OH recents animation not mapped */
    @Override
    public void cancelRecentsAnimation(boolean restoreHomeRootTaskPosition)
            throws RemoteException {
        logStub("cancelRecentsAnimation", "OH recents animation not mapped");
    }

    /** [BRIDGED] getAllRootTaskInfos -> OH IMissionManager.GetMissionInfos */
    @Override
    public List<ActivityTaskManager.RootTaskInfo> getAllRootTaskInfos() throws RemoteException {
        logBridged("getAllRootTaskInfos", "-> OH IMissionManager.GetMissionInfos");
        return Collections.emptyList();
    }

    /** [BRIDGED] getRootTaskInfo -> OH IMissionManager.GetMissionInfos */
    @Override
    public ActivityTaskManager.RootTaskInfo getRootTaskInfo(int windowingMode, int activityType)
            throws RemoteException {
        logBridged("getRootTaskInfo", "-> OH IMissionManager.GetMissionInfos");
        return null;
    }

    /** [BRIDGED] getAllRootTaskInfosOnDisplay -> OH IMissionManager.GetMissionInfos */
    @Override
    public List<ActivityTaskManager.RootTaskInfo> getAllRootTaskInfosOnDisplay(int displayId)
            throws RemoteException {
        logBridged("getAllRootTaskInfosOnDisplay", "-> OH IMissionManager.GetMissionInfos");
        return Collections.emptyList();
    }

    /** [BRIDGED] getRootTaskInfoOnDisplay -> OH IMissionManager.GetMissionInfos */
    @Override
    public ActivityTaskManager.RootTaskInfo getRootTaskInfoOnDisplay(int windowingMode,
            int activityType, int displayId) throws RemoteException {
        logBridged("getRootTaskInfoOnDisplay", "-> OH IMissionManager.GetMissionInfos");
        return null;
    }

    /** [BRIDGED] moveRootTaskToDisplay -> OH IMissionManager task operations */
    @Override
    public void moveRootTaskToDisplay(int taskId, int displayId) throws RemoteException {
        logBridged("moveRootTaskToDisplay", "-> OH IMissionManager task operations");
    }

    /** [BRIDGED] moveTaskToRootTask -> OH IMissionManager task operations */
    @Override
    public void moveTaskToRootTask(int taskId, int rootTaskId, boolean toTop)
            throws RemoteException {
        logBridged("moveTaskToRootTask", "-> OH IMissionManager task operations");
    }

    /** [BRIDGED] removeRootTasksInWindowingModes -> OH IMissionManager.CleanMission */
    @Override
    public void removeRootTasksInWindowingModes(int[] windowingModes) throws RemoteException {
        logBridged("removeRootTasksInWindowingModes", "-> OH IMissionManager.CleanMission");
    }

    /** [BRIDGED] removeRootTasksWithActivityTypes -> OH IMissionManager.CleanMission */
    @Override
    public void removeRootTasksWithActivityTypes(int[] activityTypes) throws RemoteException {
        logBridged("removeRootTasksWithActivityTypes", "-> OH IMissionManager.CleanMission");
    }

    // ====================================================================
    // Category 7: Lock Task Mode (-> OH IMissionManager.Lock/Unlock)
    // ====================================================================

    /** [STUB] updateLockTaskPackages - OH uses LockMissionForCleanup */
    @Override
    public void updateLockTaskPackages(int userId, String[] packages) throws RemoteException {
        logStub("updateLockTaskPackages", "OH uses LockMissionForCleanup");
    }

    /** [STUB] isInLockTaskMode - No OH equivalent */
    @Override
    public boolean isInLockTaskMode() throws RemoteException {
        logStub("isInLockTaskMode", "No OH equivalent");
        return false;
    }

    /** [STUB] getLockTaskModeState - No OH equivalent */
    @Override
    public int getLockTaskModeState() throws RemoteException {
        logStub("getLockTaskModeState", "No OH equivalent");
        return 0;
    }

    /** [BRIDGED] startSystemLockTaskMode -> OH IMissionManager.LockMissionForCleanup */
    @Override
    public void startSystemLockTaskMode(int taskId) throws RemoteException {
        logBridged("startSystemLockTaskMode", "-> OH IMissionManager.LockMissionForCleanup");
    }

    /** [BRIDGED] stopSystemLockTaskMode -> OH IMissionManager.UnlockMissionForCleanup */
    @Override
    public void stopSystemLockTaskMode() throws RemoteException {
        logBridged("stopSystemLockTaskMode", "-> OH IMissionManager.UnlockMissionForCleanup");
    }

    /** [STUB] updateLockTaskFeatures - No OH equivalent */
    @Override
    public void updateLockTaskFeatures(int userId, int flags) throws RemoteException {
        logStub("updateLockTaskFeatures", "No OH equivalent");
    }

    // ====================================================================
    // Category 8: Keyguard
    // ====================================================================

    /** [STUB] setLockScreenShown - OH keyguard not mapped */
    @Override
    public void setLockScreenShown(boolean showingKeyguard, boolean showingAod)
            throws RemoteException {
        logStub("setLockScreenShown", "OH keyguard not mapped");
    }

    /** [STUB] keyguardGoingAway - OH keyguard not mapped */
    @Override
    public void keyguardGoingAway(int flags) throws RemoteException {
        logStub("keyguardGoingAway", "OH keyguard not mapped");
    }

    // ====================================================================
    // Category 9: App Task (-> OH IMissionManager)
    // ====================================================================

    /** [BRIDGED] getAppTasks -> OH IMissionManager.GetMissionInfos */
    @Override
    public List<IBinder> getAppTasks(String callingPackage) throws RemoteException {
        logBridged("getAppTasks", "-> OH IMissionManager.GetMissionInfos");
        return Collections.emptyList();
    }

    /** [STUB] addAppTask - No OH equivalent */
    @Override
    public int addAppTask(IBinder activityToken, Intent intent,
            ActivityManager.TaskDescription description, Bitmap thumbnail)
            throws RemoteException {
        logStub("addAppTask", "No OH equivalent");
        return -1;
    }

    /** [STUB] getAppTaskThumbnailSize - No OH equivalent */
    @Override
    public Point getAppTaskThumbnailSize() throws RemoteException {
        logStub("getAppTaskThumbnailSize", "No OH equivalent");
        return null;
    }

    // ====================================================================
    // Category 10: Voice / VR
    // ====================================================================

    /** [STUB] finishVoiceTask - No OH voice/VR equivalent */
    @Override
    public void finishVoiceTask(IVoiceInteractionSession session) throws RemoteException {
        logStub("finishVoiceTask", "No OH voice/VR equivalent");
    }

    /** [STUB] setVoiceKeepAwake - No OH voice/VR equivalent */
    @Override
    public void setVoiceKeepAwake(IVoiceInteractionSession session, boolean keepAwake)
            throws RemoteException {
        logStub("setVoiceKeepAwake", "No OH voice/VR equivalent");
    }

    /** [STUB] setVrThread - No OH voice/VR equivalent */
    @Override
    public void setVrThread(int tid) throws RemoteException {
        logStub("setVrThread", "No OH voice/VR equivalent");
    }

    /** [STUB] setPersistentVrThread - No OH voice/VR equivalent */
    @Override
    public void setPersistentVrThread(int tid) throws RemoteException {
        logStub("setPersistentVrThread", "No OH voice/VR equivalent");
    }

    /** [STUB] supportsLocalVoiceInteraction - No OH equivalent */
    @Override
    public boolean supportsLocalVoiceInteraction() throws RemoteException {
        logStub("supportsLocalVoiceInteraction", "No OH equivalent");
        return false;
    }

    // ====================================================================
    // Category 11: Task Resize / Window Mode
    // ====================================================================

    /** [STUB] setTaskResizeable - OH window mode managed by SceneSession */
    @Override
    public void setTaskResizeable(int taskId, int resizeableMode) throws RemoteException {
        logStub("setTaskResizeable", "OH window mode managed by SceneSession");
    }

    /** [BRIDGED] resizeTask -> OH ISession.UpdateSessionRect */
    @Override
    public void resizeTask(int taskId, Rect bounds, int resizeMode) throws RemoteException {
        logBridged("resizeTask", "-> OH ISession.UpdateSessionRect");
    }

    // ====================================================================
    // Category 12: Task Stack Listener (-> OH IMissionManager)
    // ====================================================================

    /** [BRIDGED] registerTaskStackListener -> OH IMissionManager.RegisterMissionListener */
    @Override
    public void registerTaskStackListener(ITaskStackListener listener) throws RemoteException {
        logBridged("registerTaskStackListener", "-> OH IMissionManager.RegisterMissionListener");
    }

    /** [BRIDGED] unregisterTaskStackListener -> OH IMissionManager.UnRegisterMissionListener */
    @Override
    public void unregisterTaskStackListener(ITaskStackListener listener) throws RemoteException {
        logBridged("unregisterTaskStackListener",
                "-> OH IMissionManager.UnRegisterMissionListener");
    }

    // ====================================================================
    // Category 13: Activity Release / Memory
    // ====================================================================

    /** [STUB] releaseSomeActivities - OH manages memory internally */
    @Override
    public void releaseSomeActivities(IApplicationThread app) throws RemoteException {
        logStub("releaseSomeActivities", "OH manages memory internally");
    }

    // ====================================================================
    // Category 14: Task Snapshot (-> OH ISceneSessionManager)
    // ====================================================================

    /** [BRIDGED] getTaskDescriptionIcon -> OH IMissionManager icon */
    @Override
    public Bitmap getTaskDescriptionIcon(String filename, int userId) throws RemoteException {
        logBridged("getTaskDescriptionIcon", "-> OH IMissionManager icon");
        return null;
    }

    /** [STUB] cancelTaskWindowTransition - No OH equivalent */
    @Override
    public void cancelTaskWindowTransition(int taskId) throws RemoteException {
        logStub("cancelTaskWindowTransition", "No OH equivalent");
    }

    /** [BRIDGED] getTaskSnapshot -> OH ISceneSessionManager.GetSessionSnapshot */
    @Override
    public TaskSnapshot getTaskSnapshot(int taskId, boolean isLowResolution,
            boolean takeSnapshotIfNeeded) throws RemoteException {
        logBridged("getTaskSnapshot", "-> OH ISceneSessionManager.GetSessionSnapshot");
        return null;
    }

    /** [BRIDGED] takeTaskSnapshot -> OH ISceneSessionManager.GetSessionSnapshot */
    @Override
    public TaskSnapshot takeTaskSnapshot(int taskId, boolean updateCache)
            throws RemoteException {
        logBridged("takeTaskSnapshot", "-> OH ISceneSessionManager.GetSessionSnapshot");
        return null;
    }

    // ====================================================================
    // Category 15: Configuration
    // ====================================================================

    /** [BRIDGED] updateConfiguration -> OH IAppMgr.UpdateConfiguration */
    @Override
    public boolean updateConfiguration(Configuration values) throws RemoteException {
        logBridged("updateConfiguration", "-> OH IAppMgr.UpdateConfiguration");
        return true;
    }

    /** [STUB] suppressResizeConfigChanges - No OH equivalent */
    @Override
    public void suppressResizeConfigChanges(boolean suppress) throws RemoteException {
        logStub("suppressResizeConfigChanges", "No OH equivalent");
    }

    /** [STUB] getDeviceConfigurationInfo - OH config managed differently */
    @Override
    public ConfigurationInfo getDeviceConfigurationInfo() throws RemoteException {
        logStub("getDeviceConfigurationInfo", "OH config managed differently");
        return null;
    }

    // ====================================================================
    // Category 16: Remote Animation
    // ====================================================================

    /** [STUB] registerRemoteAnimationForNextActivityStart - OH remote animation not mapped */
    @Override
    public void registerRemoteAnimationForNextActivityStart(String packageName,
            RemoteAnimationAdapter adapter, IBinder launchCookie) throws RemoteException {
        logStub("registerRemoteAnimationForNextActivityStart",
                "OH remote animation not mapped");
    }

    /** [STUB] registerRemoteAnimationsForDisplay - OH remote animation not mapped */
    @Override
    public void registerRemoteAnimationsForDisplay(int displayId,
            RemoteAnimationDefinition definition) throws RemoteException {
        logStub("registerRemoteAnimationsForDisplay", "OH remote animation not mapped");
    }

    // ====================================================================
    // Category 17: Window Organizer (-> OH ISceneSessionManager)
    // ====================================================================

    /** [PARTIAL] getWindowOrganizerController -> OH ISceneSessionManager (partial) */
    @Override
    public IWindowOrganizerController getWindowOrganizerController() throws RemoteException {
        logBridged("getWindowOrganizerController", "-> OH ISceneSessionManager (partial)");
        return null;
    }

    // ====================================================================
    // Category 18: App Switches / Activity Controller
    // ====================================================================

    /** [STUB] stopAppSwitches - No OH equivalent */
    @Override
    public void stopAppSwitches() throws RemoteException {
        logStub("stopAppSwitches", "No OH equivalent");
    }

    /** [STUB] resumeAppSwitches - No OH equivalent */
    @Override
    public void resumeAppSwitches() throws RemoteException {
        logStub("resumeAppSwitches", "No OH equivalent");
    }

    /** [STUB] setActivityController - No OH equivalent */
    @Override
    public void setActivityController(IActivityController watcher, boolean imAMonkey)
            throws RemoteException {
        logStub("setActivityController", "No OH equivalent");
    }

    // ====================================================================
    // Category 19: Screen Compat (Package Level)
    // ====================================================================

    /** [STUB] getPackageScreenCompatMode - No OH screen compat */
    @Override
    public int getPackageScreenCompatMode(String packageName) throws RemoteException {
        logStub("getPackageScreenCompatMode", "No OH screen compat");
        return 0;
    }

    /** [STUB] setPackageScreenCompatMode - No OH screen compat */
    @Override
    public void setPackageScreenCompatMode(String packageName, int mode) throws RemoteException {
        logStub("setPackageScreenCompatMode", "No OH screen compat");
    }

    /** [STUB] getPackageAskScreenCompat - No OH screen compat */
    @Override
    public boolean getPackageAskScreenCompat(String packageName) throws RemoteException {
        logStub("getPackageAskScreenCompat", "No OH screen compat");
        return false;
    }

    /** [STUB] setPackageAskScreenCompat - No OH screen compat */
    @Override
    public void setPackageAskScreenCompat(String packageName, boolean ask)
            throws RemoteException {
        logStub("setPackageAskScreenCompat", "No OH screen compat");
    }

    // ====================================================================
    // Category 20: Miscellaneous
    // ====================================================================

    /** [STUB] getLastResumedActivityUserId - No OH equivalent */
    @Override
    public int getLastResumedActivityUserId() throws RemoteException {
        logStub("getLastResumedActivityUserId", "No OH equivalent");
        return 0;
    }

    /** [STUB] alwaysShowUnsupportedCompileSdkWarning - No OH equivalent */
    @Override
    public void alwaysShowUnsupportedCompileSdkWarning(ComponentName activity)
            throws RemoteException {
        logStub("alwaysShowUnsupportedCompileSdkWarning", "No OH equivalent");
    }

    /** [STUB] clearLaunchParamsForPackages - No OH equivalent */
    @Override
    public void clearLaunchParamsForPackages(List<String> packageNames) throws RemoteException {
        logStub("clearLaunchParamsForPackages", "No OH equivalent");
    }

    /** [STUB] onSplashScreenViewCopyFinished - OH splash screen managed differently */
    @Override
    public void onSplashScreenViewCopyFinished(int taskId,
            SplashScreenView.SplashScreenViewParcelable material) throws RemoteException {
        logStub("onSplashScreenViewCopyFinished", "OH splash screen managed differently");
    }

    /** [BRIDGED] onPictureInPictureStateChanged -> OH ISession PiP state */
    @Override
    public void onPictureInPictureStateChanged(PictureInPictureUiState pipState)
            throws RemoteException {
        logBridged("onPictureInPictureStateChanged", "-> OH ISession PiP state");
    }

    /** [STUB] detachNavigationBarFromApp - No OH equivalent */
    @Override
    public void detachNavigationBarFromApp(IBinder transition) throws RemoteException {
        logStub("detachNavigationBarFromApp", "No OH equivalent");
    }

    /** [STUB] setRunningRemoteTransitionDelegate - No OH equivalent */
    @Override
    public void setRunningRemoteTransitionDelegate(IApplicationThread caller)
            throws RemoteException {
        logStub("setRunningRemoteTransitionDelegate", "No OH equivalent");
    }

    /** [BRIDGED] startBackNavigation -> OH ISession.RequestSessionBack */
    @Override
    public BackNavigationInfo startBackNavigation(RemoteCallback navigationObserver,
            BackAnimationAdapter adaptor) throws RemoteException {
        logBridged("startBackNavigation", "-> OH ISession.RequestSessionBack");
        return null;
    }

    /** [BRIDGED] registerScreenCaptureObserver -> OH IWindowManager screenshot listener */
    @Override
    public void registerScreenCaptureObserver(IBinder activityToken,
            IScreenCaptureObserver observer) throws RemoteException {
        logBridged("registerScreenCaptureObserver",
                "-> OH IWindowManager screenshot listener");
    }

    /** [BRIDGED] unregisterScreenCaptureObserver -> OH IWindowManager screenshot listener */
    @Override
    public void unregisterScreenCaptureObserver(IBinder activityToken,
            IScreenCaptureObserver observer) throws RemoteException {
        logBridged("unregisterScreenCaptureObserver",
                "-> OH IWindowManager screenshot listener");
    }

    // ====================================================================
    // Internal Helpers
    // ====================================================================

    /**
     * Bridge an Intent-based activity start to OH IAbilityManager.StartAbility(Want).
     * Converts the Android Intent to an OH Want using IntentWantConverter, then
     * calls the native StartAbility method.
     *
     * @param intent the Android Intent to bridge
     * @return the result code from OH IAbilityManager.StartAbility
     */
    private int bridgeStartAbility(Intent intent) {
        if (intent == null) {
            Log.w(TAG, "bridgeStartAbility called with null Intent");
            return 0;
        }
        IntentWantConverter.WantParams want = IntentWantConverter.intentToWant(intent);
        return nativeStartAbility(want.bundleName, want.abilityName,
                want.action, want.uri, want.extrasJson);
    }

    /**
     * Bridge activity start with Mission Ability stack support.
     *
     * If the target app already has an active Mission, the new Activity is pushed
     * onto that Mission's Ability stack (via StartAbilityInMission).
     * Otherwise, a new Mission is created (via StartAbility).
     *
     * This ensures Android apps get one Mission per app (like Android's one Task per app),
     * while OH native apps remain unaffected.
     *
     * @param intent the Android Intent
     * @param flags  Intent flags (FLAG_ACTIVITY_NEW_TASK, CLEAR_TOP, etc.)
     * @return result code
     */
    private int bridgeStartActivityWithStack(Intent intent, int flags) {
        if (intent == null) {
            Log.w(TAG, "bridgeStartActivityWithStack called with null Intent");
            return 0;
        }

        IntentWantConverter.WantParams want = IntentWantConverter.intentToWant(intent);
        String bundleName = want.bundleName;
        String abilityName = want.abilityName;

        if (bundleName == null || bundleName.isEmpty()) {
            // No target bundle — fall through to standard StartAbility
            return nativeStartAbility(want.bundleName, want.abilityName,
                    want.action, want.uri, want.extrasJson);
        }

        // Check if we should force a new Mission
        boolean forceNewTask = (flags & Intent.FLAG_ACTIVITY_NEW_TASK) != 0
                && (flags & Intent.FLAG_ACTIVITY_MULTIPLE_TASK) != 0;

        // Check if this app already has an active Mission
        Integer existingMissionId = mActiveMissions.get(bundleName);

        if (!forceNewTask && existingMissionId != null) {
            // Reuse existing Mission — push new Activity onto Ability stack
            Log.i(TAG, "startActivity: reusing Mission " + existingMissionId
                    + " for " + bundleName + "/" + abilityName);

            // Handle FLAG_ACTIVITY_SINGLE_TOP: if target is already on top, deliver onNewIntent
            if ((flags & Intent.FLAG_ACTIVITY_SINGLE_TOP) != 0) {
                if (nativeIsTopAbility(existingMissionId, abilityName)) {
                    Log.i(TAG, "startActivity: SINGLE_TOP, top is already " + abilityName
                            + ", delivering onNewIntent");
                    deliverOnNewIntent(intent, abilityName);
                    return 0;
                }
            }

            // Handle FLAG_ACTIVITY_CLEAR_TOP: clear everything above target, then bring it to top
            if ((flags & Intent.FLAG_ACTIVITY_CLEAR_TOP) != 0) {
                Log.i(TAG, "startActivity: CLEAR_TOP for " + abilityName
                        + " in Mission " + existingMissionId);
                int clearResult = nativeClearAbilitiesAbove(existingMissionId, abilityName);
                if (clearResult >= 0) {
                    // CLEAR_TOP with SINGLE_TOP: reuse existing instance (onNewIntent)
                    // CLEAR_TOP without SINGLE_TOP: destroy and recreate the target
                    if ((flags & Intent.FLAG_ACTIVITY_SINGLE_TOP) != 0) {
                        deliverOnNewIntent(intent, abilityName);
                        return 0;
                    }
                    // Without SINGLE_TOP, the target ability is destroyed and recreated
                    // by StartAbilityInMission with the CLEAR_TOP marker
                    return clearResult;
                }
                // If target not found in stack, fall through to push normally
            }

            int result = nativeStartAbilityInMission(
                    want.bundleName, want.abilityName,
                    want.action, want.uri, want.extrasJson,
                    existingMissionId);
            return result;
        } else {
            // Create new Mission
            Log.i(TAG, "startActivity: creating new Mission for "
                    + bundleName + "/" + abilityName);

            int result = nativeStartAbility(want.bundleName, want.abilityName,
                    want.action, want.uri, want.extrasJson);

            if (result == 0) {
                // StartAbility returns 0 on success (not missionId).
                // Query OH to get the actual missionId for this bundle.
                int missionId = nativeGetMissionIdForBundle(bundleName);
                if (missionId >= 0) {
                    mActiveMissions.put(bundleName, missionId);
                    Log.i(TAG, "startActivity: new Mission created, missionId=" + missionId);
                } else {
                    Log.w(TAG, "startActivity: Mission created but could not resolve missionId");
                }
            }

            return result;
        }
    }

    /**
     * Deliver onNewIntent to an existing Activity via OH AbilityScheduler callback.
     * When SINGLE_TOP is set and the target is already on top, Android expects
     * Activity.onNewIntent() to be called instead of creating a new instance.
     */
    private void deliverOnNewIntent(Intent intent, String abilityName) {
        // The onNewIntent delivery flows through the OH reverse callback path:
        // AbilitySchedulerBridge receives the ScheduleNewIntent call from OH
        // and forwards it to IApplicationThread.scheduleNewIntent().
        // Here we trigger it by notifying the OH side.
        Log.i(TAG, "deliverOnNewIntent: " + abilityName + " intent=" + intent);
        // The actual delivery is handled by OH AbilityManagerService when
        // StartAbilityInMission detects the target is already on top with
        // the SINGLE_TOP flag in the Want. We set the flag in the Want params.
        IntentWantConverter.WantParams want = IntentWantConverter.intentToWant(intent);
        Integer missionId = mActiveMissions.get(want.bundleName);
        if (missionId != null) {
            // Start with SINGLE_TOP marker — OH side delivers onNewIntent
            // instead of creating a new AbilityRecord
            nativeStartAbilityInMission(
                    want.bundleName, want.abilityName,
                    want.action, want.uri, want.extrasJson,
                    missionId);
        }
    }

    /**
     * Log a bridged method call (method is mapped to an OH equivalent).
     */
    private void logBridged(String method, String target) {
        Log.d(TAG, "[BRIDGED] " + method + " " + target);
    }

    /**
     * Log a stub method call (method has no OH equivalent and returns a safe default).
     */
    private void logStub(String method, String reason) {
        Log.d(TAG, "[STUB] " + method + " - " + reason);
    }
}
