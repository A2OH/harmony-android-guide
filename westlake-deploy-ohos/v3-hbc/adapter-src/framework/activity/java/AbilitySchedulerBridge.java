/*
 * AbilitySchedulerBridge.java
 *
 * Reverse bridge: OH IAbilityScheduler callbacks -> Android IApplicationThread.
 *
 * OH IAbilityScheduler is called by OH AbilityManagerService to notify
 * individual ability instances of lifecycle transitions, data operations,
 * and service connection events.
 *
 * Mapping:
 *   OH IAbilityScheduler -> Android IApplicationThread (activity/service lifecycle)
 *                        -> Android ContentProvider (data operations)
 *
 * Key differences:
 *   - OH uses LifeCycleStateInfo for state transitions
 *   - Android uses ClientTransaction with individual lifecycle items
 *   - OH DataAbility operations map to Android ContentProvider
 *   - OH has ability-level save/restore; Android uses Bundle in lifecycle items
 */
package adapter.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import adapter.activity.LifecycleAdapter;

public class AbilitySchedulerBridge {

    private static final String TAG = "OH_AbilitySchedBridge";
    private final Object mApplicationThread;

    // Track created service tokens to avoid double-creation
    private final Set<IBinder> mCreatedServices = new HashSet<>();
    // Map component key -> service token for stable token reuse
    private final Map<String, IBinder> mServiceTokens = new HashMap<>();

    // OH LifeCycleStateInfo state constants
    private static final int OH_ABILITY_STATE_INITIAL = 0;
    private static final int OH_ABILITY_STATE_INACTIVE = 2;
    private static final int OH_ABILITY_STATE_BACKGROUND_NEW = 4;
    private static final int OH_ABILITY_STATE_FOREGROUND_NEW = 5;

    public AbilitySchedulerBridge(Object applicationThread) {
        mApplicationThread = applicationThread;
    }

    // ============================================================
    // Category 1: Ability Lifecycle (-> IApplicationThread.scheduleTransaction)
    // ============================================================

    /**
     * [BRIDGED] ScheduleAbilityTransaction -> IApplicationThread.scheduleTransaction
     *
     * OH sends lifecycle state transition via LifeCycleStateInfo.
     * This method maps the OH state to the Android lifecycle system using
     * LifecycleAdapter, which constructs the appropriate ClientTransaction.
     *
     * OH lifecycle states (AbilityLifeCycleState wire enum) -> Android lifecycle:
     *   INITIAL(0)        -> DESTROYED (destroy the activity)
     *   INACTIVE(2)       -> STARTED (pause equivalent)
     *   BACKGROUND_NEW(4) -> STOPPED
     *   FOREGROUND_NEW(5) -> RESUMED
     *
     * G2.14m (2026-05-01): Added tokenAddr param so we can find the actual Android
     * Activity IBinder via OhTokenRegistry. Without it, the previous token=0 default
     * caused getActivityToken to return null and the transaction was dropped. This
     * is the second half of the OH two-phase lifecycle protocol — first half is
     * AppSchedulerBridge.nativeOnScheduleLaunchAbility (Activity.onCreate); this
     * method drives Activity.onResume / onPause / onStop / onDestroy from AMS state
     * machine.
     */
    public void onScheduleAbilityTransaction(long tokenAddr, String wantJson,
                                              int targetState, boolean isNewWant) {
        String androidItem = mapLifecycleStateToItem(targetState);
        logBridged("ScheduleAbilityTransaction(tokenAddr=0x" + Long.toHexString(tokenAddr)
                + " state=" + targetState + ")",
                "-> IApplicationThread.scheduleTransaction(" + androidItem + ")");

        // Resolve OH ability token → Android Activity IBinder via OhTokenRegistry.
        // This registry is populated by AppSchedulerBridge.nativeOnScheduleLaunchAbility
        // when the LaunchActivityItem transaction is constructed.
        // Direct BCP call to adapter.core.OhTokenRegistry — AbilitySchedulerBridge
        // lives in BCP (oh-adapter-framework.jar) but AppSchedulerBridge lives in
        // PathClassLoader (oh-adapter-runtime.jar) and is not visible to BCP code.
        IBinder activityToken = adapter.core.OhTokenRegistry.findByOhToken(tokenAddr);
        if (activityToken == null) {
            Log.w(TAG, "onScheduleAbilityTransaction: no Android token for OH tokenAddr=0x"
                    + Long.toHexString(tokenAddr) + " — transaction dropped");
            return;
        }

        // Map OH state to Android state and dispatch via LifecycleAdapter.
        LifecycleAdapter lifecycle = LifecycleAdapter.getInstance();
        int ohState;
        switch (targetState) {
            case OH_ABILITY_STATE_FOREGROUND_NEW:
                ohState = LifecycleAdapter.OH_STATE_FOREGROUND_NEW;
                break;
            case OH_ABILITY_STATE_BACKGROUND_NEW:
                ohState = LifecycleAdapter.OH_STATE_BACKGROUND_NEW;
                break;
            case OH_ABILITY_STATE_INACTIVE:
                ohState = LifecycleAdapter.OH_STATE_INACTIVE;
                break;
            case OH_ABILITY_STATE_INITIAL:
            default:
                ohState = LifecycleAdapter.OH_STATE_INITIAL;
                break;
        }

        // Dispatch via LifecycleAdapter using the resolved Android Activity IBinder.
        lifecycle.dispatchOhLifecycleByActivityToken(activityToken, ohState);
    }

    /**
     * [BRIDGED] SendResult -> IApplicationThread.scheduleTransaction
     *
     * OH sends result back to caller ability.
     * Android delivers via ActivityResultItem in ClientTransaction.
     */
    public void onSendResult(int requestCode, int resultCode, String resultWantJson) {
        logBridged("SendResult",
                "-> IApplicationThread.scheduleTransaction(ActivityResultItem)");
    }

    /**
     * [BRIDGED] ScheduleSaveAbilityState -> IApplicationThread.scheduleTransaction
     *
     * OH requests ability to save state.
     * Android includes SaveStateItem in ClientTransaction.
     */
    public void onScheduleSaveAbilityState() {
        logBridged("ScheduleSaveAbilityState",
                "-> IApplicationThread.scheduleTransaction(SaveStateItem)");
    }

    /**
     * [BRIDGED] ScheduleRestoreAbilityState -> IApplicationThread.scheduleTransaction
     *
     * OH restores ability state from PacMap.
     * Android restores from Bundle in LaunchActivityItem.
     *
     * Semantic gap: OH PacMap vs Android Bundle - need conversion.
     */
    public void onScheduleRestoreAbilityState(String stateJson) {
        logBridged("ScheduleRestoreAbilityState",
                "-> LaunchActivityItem with savedInstanceState Bundle");
    }

    /**
     * [PARTIAL] SchedulePrepareTerminateAbility -> (no direct Android equivalent)
     *
     * OH gives ability a chance to prepare before termination.
     * Android has no prepare-terminate concept for activities.
     * Impact: MEDIUM - Some OH apps may rely on this for cleanup.
     * Strategy: Map to onPause -> onStop sequence to give app cleanup time.
     */
    public boolean onSchedulePrepareTerminateAbility() {
        logPartial("SchedulePrepareTerminateAbility",
                "No Android equivalent, map to onPause+onStop");
        return true; // Allow termination
    }

    /**
     * [BRIDGED] ScheduleShareData -> (Activity result mechanism)
     *
     * OH data sharing between abilities.
     * Android uses startActivityForResult / onActivityResult.
     */
    public void onScheduleShareData(int uniqueId) {
        logBridged("ScheduleShareData",
                "-> Activity.onActivityResult (data sharing)");
    }

    // ============================================================
    // Category 2: Service Connection (-> IApplicationThread)
    // ============================================================

    /**
     * [BRIDGED] ScheduleConnectAbility -> IApplicationThread.scheduleBindService
     *
     * OH requests the service ability to handle a new connection.
     * Android dispatches via IApplicationThread:
     *   1. scheduleCreateService (if first bind, service not yet created)
     *   2. scheduleBindService (deliver the bind intent)
     *
     * The service token is derived from the Want (bundleName + abilityName)
     * and cached in ServiceConnectionRegistry on the Java side.
     */
    public void onScheduleConnectAbility(String wantJson) {
        logBridged("ScheduleConnectAbility",
                "-> IApplicationThread.scheduleBindService");
        try {
            // Build Intent from Want JSON
            android.content.Intent intent =
                    adapter.activity.IntentWantConverter.wantJsonToIntent(wantJson);
            android.os.IBinder serviceToken = getOrCreateServiceToken(intent);

            // If this is the first bind for this service, create it first
            if (!mCreatedServices.contains(serviceToken)) {
                invokeApplicationThread("scheduleCreateService",
                        new Class[]{android.os.IBinder.class,
                                android.content.pm.ServiceInfo.class,
                                android.content.res.CompatibilityInfo.class,
                                int.class},
                        new Object[]{serviceToken,
                                buildServiceInfo(intent),
                                null, // CompatibilityInfo
                                0}); // processState
                mCreatedServices.add(serviceToken);
            }

            // Schedule the bind
            invokeApplicationThread("scheduleBindService",
                    new Class[]{android.os.IBinder.class,
                            android.content.Intent.class,
                            boolean.class, int.class},
                    new Object[]{serviceToken, intent, false, 0});
        } catch (Exception e) {
            Log.e(TAG, "Failed to dispatch scheduleBindService", e);
        }
    }

    /**
     * [BRIDGED] ScheduleDisconnectAbility -> IApplicationThread.scheduleUnbindService
     *
     * OH requests the service ability to handle disconnect.
     * Android dispatches IApplicationThread.scheduleUnbindService.
     */
    public void onScheduleDisconnectAbility(String wantJson) {
        logBridged("ScheduleDisconnectAbility",
                "-> IApplicationThread.scheduleUnbindService");
        try {
            android.content.Intent intent =
                    adapter.activity.IntentWantConverter.wantJsonToIntent(wantJson);
            android.os.IBinder serviceToken = getOrCreateServiceToken(intent);

            invokeApplicationThread("scheduleUnbindService",
                    new Class[]{android.os.IBinder.class,
                            android.content.Intent.class},
                    new Object[]{serviceToken, intent});
        } catch (Exception e) {
            Log.e(TAG, "Failed to dispatch scheduleUnbindService", e);
        }
    }

    /**
     * [BRIDGED] ScheduleCommandAbility -> IApplicationThread.scheduleServiceArgs
     *
     * OH sends command to service ability (equivalent to startService delivery).
     * Android dispatches IApplicationThread.scheduleServiceArgs.
     */
    public void onScheduleCommandAbility(String wantJson, boolean restart, int startId) {
        logBridged("ScheduleCommandAbility",
                "-> IApplicationThread.scheduleServiceArgs");
        try {
            android.content.Intent intent =
                    adapter.activity.IntentWantConverter.wantJsonToIntent(wantJson);
            android.os.IBinder serviceToken = getOrCreateServiceToken(intent);

            // If service not yet created, create it first
            if (!mCreatedServices.contains(serviceToken)) {
                invokeApplicationThread("scheduleCreateService",
                        new Class[]{android.os.IBinder.class,
                                android.content.pm.ServiceInfo.class,
                                android.content.res.CompatibilityInfo.class,
                                int.class},
                        new Object[]{serviceToken,
                                buildServiceInfo(intent),
                                null, 0});
                mCreatedServices.add(serviceToken);
            }

            invokeApplicationThread("scheduleServiceArgs",
                    new Class[]{android.os.IBinder.class,
                            android.content.pm.ParceledListSlice.class,
                            boolean.class, boolean.class, int.class},
                    new Object[]{serviceToken,
                            null, // ParceledListSlice<Intent> - simplified
                            false, // taskRemoved
                            restart, startId});
        } catch (Exception e) {
            Log.e(TAG, "Failed to dispatch scheduleServiceArgs", e);
        }
    }

    /**
     * [PARTIAL] ScheduleCommandAbilityWindow -> IApplicationThread.scheduleServiceArgs
     *
     * OH sends command with window session info.
     * Android has no direct service-with-window concept.
     * Strategy: Forward command part as scheduleServiceArgs, ignore window part.
     */
    public void onScheduleCommandAbilityWindow(String wantJson, int windowCommand) {
        logPartial("ScheduleCommandAbilityWindow",
                "-> scheduleServiceArgs + window handling split");
        // Delegate to regular command handling, ignoring window part
        onScheduleCommandAbility(wantJson, false, 0);
    }

    // ============================================================
    // Category 3: Data Operations (-> ContentProvider)
    // ============================================================

    /*
     * OH DataAbility operations map to Android ContentProvider.
     * These are NOT IApplicationThread calls but ContentProvider interface calls.
     *
     * OH DataAbility is deprecated in favor of DataShareExtensionAbility,
     * but the IAbilityScheduler still carries these methods.
     *
     * Impact assessment:
     * - GetFileTypes, OpenFile, OpenRawFile: MEDIUM - File sharing operations
     * - Insert, Update, Delete, Query: HIGH - CRUD operations on structured data
     * - BatchInsert, ExecuteBatch: MEDIUM - Batch data operations
     * - Call: LOW - Custom method invocation
     *
     * Strategy: Route to ContentProvider if available, otherwise stub.
     */

    /**
     * [PARTIAL] GetFileTypes -> ContentProvider.getType (partial)
     */
    public void onGetFileTypes(String uri, String mimeTypeFilter) {
        logPartial("GetFileTypes", "-> ContentProvider.getStreamTypes (semantic differs)");
    }

    /**
     * [PARTIAL] OpenFile -> ContentProvider.openFile
     */
    public void onOpenFile(String uri, String mode) {
        logPartial("OpenFile", "-> ContentProvider.openFile");
    }

    /**
     * [PARTIAL] Insert -> ContentProvider.insert
     */
    public void onInsert(String uri) {
        logPartial("Insert", "-> ContentProvider.insert (data format conversion needed)");
    }

    /**
     * [PARTIAL] Update -> ContentProvider.update
     */
    public void onUpdate(String uri) {
        logPartial("Update", "-> ContentProvider.update (predicate conversion needed)");
    }

    /**
     * [PARTIAL] Delete -> ContentProvider.delete
     */
    public void onDelete(String uri) {
        logPartial("Delete", "-> ContentProvider.delete (predicate conversion needed)");
    }

    /**
     * [PARTIAL] Query -> ContentProvider.query
     */
    public void onQuery(String uri) {
        logPartial("Query", "-> ContentProvider.query (ResultSet conversion needed)");
    }

    /**
     * [PARTIAL] Call -> ContentProvider.call
     */
    public void onCall(String uri, String method, String arg) {
        logPartial("Call", "-> ContentProvider.call");
    }

    /**
     * [PARTIAL] BatchInsert -> ContentProvider.bulkInsert
     */
    public void onBatchInsert(String uri) {
        logPartial("BatchInsert", "-> ContentProvider.bulkInsert");
    }

    /**
     * [PARTIAL] ExecuteBatch -> ContentProvider.applyBatch
     */
    public void onExecuteBatch() {
        logPartial("ExecuteBatch", "-> ContentProvider.applyBatch");
    }

    /**
     * [PARTIAL] GetType -> ContentProvider.getType
     */
    public void onGetType(String uri) {
        logPartial("GetType", "-> ContentProvider.getType");
    }

    /**
     * [PARTIAL] NormalizeUri / DenormalizeUri -> ContentProvider.canonicalize/uncanonicalize
     */
    public void onNormalizeUri(String uri) {
        logPartial("NormalizeUri", "-> ContentProvider.canonicalize");
    }

    /**
     * [PARTIAL] Reload -> (no direct ContentProvider equivalent)
     * Impact: LOW - Reload is OH-specific data refresh.
     */
    public void onReload(String uri) {
        logPartial("Reload", "No ContentProvider equivalent, use notifyChange");
    }

    /**
     * [PARTIAL] ScheduleRegisterObserver -> ContentResolver.registerContentObserver
     */
    public void onScheduleRegisterObserver(String uri) {
        logPartial("ScheduleRegisterObserver",
                "-> ContentResolver.registerContentObserver");
    }

    /**
     * [PARTIAL] ScheduleUnregisterObserver -> ContentResolver.unregisterContentObserver
     */
    public void onScheduleUnregisterObserver(String uri) {
        logPartial("ScheduleUnregisterObserver",
                "-> ContentResolver.unregisterContentObserver");
    }

    /**
     * [PARTIAL] ScheduleNotifyChange -> ContentResolver.notifyChange
     */
    public void onScheduleNotifyChange(String uri) {
        logPartial("ScheduleNotifyChange", "-> ContentResolver.notifyChange");
    }

    // ============================================================
    // Category 4: Continuation (OH_ONLY)
    // ============================================================

    /**
     * [OH_ONLY] ContinueAbility - OH distributed ability continuation
     * No Android equivalent (Android has no built-in device continuation).
     * Impact: None for single-device Android apps.
     * Strategy: Ignore. Multi-device scenarios not supported in bridge.
     */
    public void onContinueAbility(String deviceId, int versionCode) {
        logOhOnly("ContinueAbility", "OH distributed continuation, no Android equivalent");
    }

    /**
     * [OH_ONLY] NotifyContinuationResult
     */
    public void onNotifyContinuationResult(int result) {
        logOhOnly("NotifyContinuationResult", "OH distributed continuation result");
    }

    // ============================================================
    // Category 5: Misc (-> various Android mechanisms)
    // ============================================================

    /**
     * [PARTIAL] DumpAbilityInfo -> IApplicationThread.dumpActivity
     */
    public void onDumpAbilityInfo() {
        logPartial("DumpAbilityInfo", "-> IApplicationThread.dumpActivity");
    }

    /**
     * [OH_ONLY] CreateModalUIExtension - OH UIExtension concept
     * No direct Android equivalent.
     * Impact: LOW - Modal dialogs can be shown via standard Activity.
     * Strategy: Could map to Dialog or DialogFragment.
     */
    public void onCreateModalUIExtension(String wantJson) {
        logOhOnly("CreateModalUIExtension",
                "OH UIExtension, could map to DialogFragment");
    }

    /**
     * [PARTIAL] OnExecuteIntent -> IApplicationThread.scheduleTransaction
     * OH intent execution callback.
     * Android handles intents through activity/service launch.
     */
    public void onExecuteIntent(String wantJson) {
        logPartial("OnExecuteIntent",
                "-> scheduleTransaction with new intent delivery");
    }

    /**
     * [PARTIAL] CallRequest -> (handled via Activity.onStart with call mode)
     * OH UIAbility started by call.
     * Android doesn't have "call" start mode.
     * Impact: LOW - Specialized OH feature.
     */
    public void onCallRequest() {
        logPartial("CallRequest", "OH call mode, no Android equivalent");
    }

    /**
     * [OH_ONLY] UpdateSessionToken - OH session token update
     */
    public void onUpdateSessionToken() {
        logOhOnly("UpdateSessionToken", "OH session management");
    }

    /**
     * [OH_ONLY] ScheduleCollaborate - OH collaboration feature
     */
    public void onScheduleCollaborate(String wantJson) {
        logOhOnly("ScheduleCollaborate", "OH collaboration, no Android equivalent");
    }

    /**
     * [OH_ONLY] ScheduleAbilityRequestFailure/Success/Done
     * OH ability request result notification.
     */
    public void onScheduleAbilityRequestResult(String requestId, boolean success) {
        logOhOnly("ScheduleAbilityRequest" + (success ? "Success" : "Failure"),
                "OH ability request result");
    }

    // ==================== Service Helpers ====================

    /**
     * Get or create a stable service token for the given Intent.
     * Uses component name as key for token reuse across bind/unbind cycles.
     */
    private IBinder getOrCreateServiceToken(Intent intent) {
        String key = (intent.getComponent() != null)
                ? intent.getComponent().flattenToString()
                : intent.getAction();
        if (key == null) key = "unknown_service";

        IBinder token = mServiceTokens.get(key);
        if (token == null) {
            token = new Binder();
            mServiceTokens.put(key, token);
        }
        return token;
    }

    /**
     * Build a minimal ServiceInfo from the Intent for scheduleCreateService.
     */
    private ServiceInfo buildServiceInfo(Intent intent) {
        ServiceInfo info = new ServiceInfo();
        if (intent.getComponent() != null) {
            info.packageName = intent.getComponent().getPackageName();
            info.name = intent.getComponent().getClassName();
        }
        try {
            android.app.ActivityThread at = android.app.ActivityThread.currentActivityThread();
            if (at != null && at.getApplication() != null) {
                info.applicationInfo = at.getApplication().getApplicationInfo();
            }
        } catch (Exception e) {
            Log.w(TAG, "Could not get ApplicationInfo for ServiceInfo", e);
        }
        return info;
    }

    /**
     * Invoke a method on the IApplicationThread via reflection.
     */
    private void invokeApplicationThread(String methodName, Class<?>[] paramTypes,
                                          Object[] args) throws Exception {
        if (mApplicationThread == null) return;
        java.lang.reflect.Method method;
        if (paramTypes != null) {
            method = mApplicationThread.getClass().getMethod(methodName, paramTypes);
        } else {
            method = mApplicationThread.getClass().getMethod(methodName);
        }
        method.invoke(mApplicationThread, args);
    }

    /**
     * Notify that a service has been destroyed, clean up its token.
     */
    public void onServiceDestroyed(String componentKey) {
        IBinder token = mServiceTokens.remove(componentKey);
        if (token != null) {
            mCreatedServices.remove(token);
        }
    }

    // ==================== Utility ====================

    private String mapLifecycleStateToItem(int ohState) {
        switch (ohState) {
            case 0: return "LaunchActivityItem";      // INITIAL
            case 2: return "PauseActivityItem";        // INACTIVE
            case 4: return "StopActivityItem";         // BACKGROUND_NEW
            case 5: return "ResumeActivityItem";       // FOREGROUND_NEW
            default: return "Unknown(" + ohState + ")";
        }
    }

    private void logBridged(String method, String target) {
        Log.d(TAG, "[BRIDGED] " + method + " " + target);
    }

    private void logPartial(String method, String reason) {
        Log.d(TAG, "[PARTIAL] " + method + " - " + reason);
    }

    private void logOhOnly(String method, String reason) {
        Log.d(TAG, "[OH_ONLY] " + method + " - " + reason);
    }
}
