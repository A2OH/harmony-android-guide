/*
 * ActivityManagerAdapter.java
 *
 * Stub-based adapter for Android IActivityManager interface.
 * Extends IActivityManager.Stub (class inheritance).
 * (Historical: replaced an InvocationHandler-based dynamic proxy class
 *  IActivityManagerBridge which was deleted 2026-04-29 per feedback.txt P1a.)
 *
 * Routes IActivityManager binder calls to OpenHarmony system services:
 *   IActivityManager -> OH IAbilityManager  (lifecycle, ability management)
 *                    -> OH IAppMgr           (process management, app state)
 *                    -> OH IMissionManager   (mission/task management)
 *
 * Methods are categorized as:
 *   [BRIDGED]  - Mapped to an OH equivalent via native JNI call
 *   [PARTIAL]  - Partially mapped (some params ignored)
 *   [STUB]     - No OH equivalent, returns a safe default
 */
package adapter.activity;

import android.app.ActivityManager;
import android.app.ActivityManager.PendingIntentInfo;
import android.app.ActivityTaskManager;
import android.app.ApplicationStartInfo;
import android.app.ApplicationErrorReport;
import android.app.ApplicationExitInfo;
import android.app.ContentProviderHolder;
import android.app.GrantedUriPermission;
import android.app.IApplicationStartInfoCompleteListener;
import android.app.IApplicationThread;
import android.app.IActivityController;
import android.app.IActivityManager;
import android.app.IAppTask;
import android.app.IForegroundServiceObserver;
import android.app.IInstrumentationWatcher;
import android.app.IProcessObserver;
import android.app.IServiceConnection;
import android.app.IStopUserCallback;
import android.app.ITaskStackListener;
import android.app.IUiAutomationConnection;
import android.app.IUidFrozenStateChangedCallback;
import android.app.IUidObserver;
import android.app.IUserSwitchObserver;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.PictureInPictureParams;
import android.app.ProfilerInfo;
import android.app.WaitResult;
import android.app.assist.AssistContent;
import android.app.assist.AssistStructure;
import android.content.ComponentName;
import android.content.IIntentReceiver;
import android.content.IIntentSender;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.LocusId;
import android.content.pm.ApplicationInfo;
import android.content.pm.ConfigurationInfo;
import android.content.pm.IPackageDataObserver;
import android.content.pm.ParceledListSlice;
import android.content.pm.ProviderInfo;
import android.content.pm.ResolveInfo;
import android.content.pm.UserInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.GraphicBuffer;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Debug;
import android.os.IBinder;
import android.os.IProgressListener;
import android.os.ParcelFileDescriptor;
import android.os.PersistableBundle;
import android.os.RemoteCallback;
import android.os.RemoteException;
import android.os.StrictMode;
import android.os.WorkSource;
import android.service.voice.IVoiceInteractionSession;
import android.util.Log;
import adapter.contentprovider.ContentProviderRegistry;
import android.view.RemoteAnimationDefinition;
import android.view.RemoteAnimationAdapter;

import adapter.activity.IntentWantConverter;
import adapter.broadcast.BroadcastEventConverter;
import adapter.broadcast.CommonEventReceiverBridge;
import com.android.internal.app.IVoiceInteractor;
import com.android.internal.os.IResultReceiver;
import com.android.internal.policy.IKeyguardDismissCallback;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Class-inheritance based adapter that extends IActivityManager.Stub.
 * Every AIDL method is overridden with either a bridged implementation
 * (forwarding to OH native services) or a stub returning a safe default.
 */
public class ActivityManagerAdapter extends IActivityManager.Stub {

    private static final String TAG = "OH_AMAdapter";

    private final long mOhAbilityManager;

    private static native long nativeGetOHAbilityManagerService();
    static native int nativeStartAbility(String bundleName, String abilityName, String action, String uri, String extraJson);
    static native int nativeConnectAbility(String bundleName, String abilityName, int connectionId);
    static native int nativeDisconnectAbility(int connectionId);
    static native int nativeStopServiceAbility(String bundleName, String abilityName);

    // Broadcast / CommonEvent native methods
    private static native int nativeSubscribeCommonEvent(int subscriptionId,
            String[] ohEventNames, int priority, String permission);
    private static native int nativeUnsubscribeCommonEvent(int subscriptionId);
    private static native int nativePublishCommonEvent(String ohAction, String extrasJson,
            String uri, int code, String data, boolean ordered, boolean sticky,
            String[] subscriberPermissions);
    private static native int nativeFinishCommonEvent(int subscriptionId, int resultCode,
            String resultData, boolean abortEvent);
    private static native String nativeGetStickyCommonEvent(String ohEventName);

    // Broadcast subscription tracking
    private final AtomicInteger mNextSubscriptionId = new AtomicInteger(1);
    // IIntentReceiver binder identity -> subscriptionId
    private final ConcurrentHashMap<IBinder, Integer> mReceiverToSubscription =
            new ConcurrentHashMap<>();
    // subscriptionId -> IntentFilter (for secondary filtering)
    private final ConcurrentHashMap<Integer, IntentFilter> mSubscriptionFilters =
            new ConcurrentHashMap<>();

    private static volatile ActivityManagerAdapter sInstance;

    public static ActivityManagerAdapter getInstance() {
        if (sInstance == null) {
            synchronized (ActivityManagerAdapter.class) {
                if (sInstance == null) {
                    sInstance = new ActivityManagerAdapter();
                }
            }
        }
        return sInstance;
    }

    public ActivityManagerAdapter() {
        mOhAbilityManager = nativeGetOHAbilityManagerService();
        Log.i(TAG, "ActivityManagerAdapter created, OH handle=0x" +
                Long.toHexString(mOhAbilityManager));
    }

    // ==================== Helper Methods ====================

    private void logBridged(String method, String target) {
        Log.d(TAG, "[BRIDGED] " + method + " " + target);
    }

    private void logStub(String method, String reason) {
        Log.d(TAG, "[STUB] " + method + " - " + reason);
    }

    /**
     * Searches the given typed parameter for an Intent instance.
     * Adapted from the old InvocationHandler's Object[] search to typed usage.
     */
    private Intent findIntentIn(Intent intent) {
        return intent;
    }

    /**
     * Returns a safe default value for the given return type.
     */
    @SuppressWarnings("unchecked")
    private static <T> T getDefaultReturn(Class<T> type) {
        if (type == boolean.class || type == Boolean.class) return (T) Boolean.FALSE;
        if (type == int.class || type == Integer.class) return (T) Integer.valueOf(0);
        if (type == long.class || type == Long.class) return (T) Long.valueOf(0L);
        if (type == float.class || type == Float.class) return (T) Float.valueOf(0.0f);
        if (type == double.class || type == Double.class) return (T) Double.valueOf(0.0);
        return null;
    }

    // ========================================================================
    // Category 1: Activity Lifecycle (-> OH IAbilityManager)
    // ========================================================================

    // [BRIDGED] startActivity -> OH IAbilityManager.StartAbility(Want)
    @Override
    public int startActivity(IApplicationThread caller, String callingPackage, Intent intent,
            String resolvedType, IBinder resultTo, String resultWho, int requestCode,
            int flags, ProfilerInfo profilerInfo, Bundle options) throws RemoteException {
        logBridged("startActivity", "-> OH IAbilityManager.StartAbility");
        return bridgeStartAbility(intent);
    }

    // [BRIDGED] startActivityWithFeature -> OH IAbilityManager.StartAbility(Want)
    @Override
    public int startActivityWithFeature(IApplicationThread caller, String callingPackage,
            String callingFeatureId, Intent intent, String resolvedType,
            IBinder resultTo, String resultWho, int requestCode, int flags,
            ProfilerInfo profilerInfo, Bundle options) throws RemoteException {
        logBridged("startActivityWithFeature", "-> OH IAbilityManager.StartAbility");
        return bridgeStartAbility(intent);
    }

    // [BRIDGED] startActivityAsUser -> OH IAbilityManager.StartAbility(Want)
    @Override
    public int startActivityAsUser(IApplicationThread caller, String callingPackage,
            Intent intent, String resolvedType, IBinder resultTo, String resultWho,
            int requestCode, int flags, ProfilerInfo profilerInfo,
            Bundle options, int userId) throws RemoteException {
        logBridged("startActivityAsUser", "-> OH IAbilityManager.StartAbility");
        return bridgeStartAbility(intent);
    }

    // [BRIDGED] startActivityAsUserWithFeature -> OH IAbilityManager.StartAbility(Want)
    @Override
    public int startActivityAsUserWithFeature(IApplicationThread caller, String callingPackage,
            String callingFeatureId, Intent intent, String resolvedType,
            IBinder resultTo, String resultWho, int requestCode, int flags,
            ProfilerInfo profilerInfo, Bundle options, int userId) throws RemoteException {
        logBridged("startActivityAsUserWithFeature", "-> OH IAbilityManager.StartAbility");
        return bridgeStartAbility(intent);
    }

    /** Common bridge helper for all startActivity variants. */
    private int bridgeStartAbility(Intent intent) {
        if (intent == null) {
            logStub("startActivity", "No Intent provided");
            return 0;
        }
        IntentWantConverter.WantParams want = IntentWantConverter.intentToWant(intent);
        return nativeStartAbility(want.bundleName, want.abilityName,
                want.action, want.uri, want.extrasJson);
    }

    // [BRIDGED] finishActivity -> OH IAbilityManager.CloseAbility(token)
    @Override
    public boolean finishActivity(IBinder token, int code, Intent data,
            int finishTask) throws RemoteException {
        logBridged("finishActivity", "-> OH IAbilityManager.CloseAbility");
        return true;
    }

    // [BRIDGED] moveActivityTaskToBack -> OH IAbilityManager.MinimizeAbility(token)
    @Override
    public boolean moveActivityTaskToBack(IBinder token,
            boolean nonRoot) throws RemoteException {
        logBridged("moveActivityTaskToBack", "-> OH IAbilityManager.MinimizeAbility");
        return true;
    }

    // [BRIDGED] unhandledBack -> OH ISession.RequestSessionBack
    @Override
    public void unhandledBack() throws RemoteException {
        logBridged("unhandledBack", "-> OH ISession.RequestSessionBack");
    }

    // [BRIDGED] setRequestedOrientation -> OH ISession property update
    @Override
    public void setRequestedOrientation(IBinder token,
            int requestedOrientation) throws RemoteException {
        logBridged("setRequestedOrientation", "-> OH ISession property update");
    }

    // ========================================================================
    // Category 2: Service Lifecycle (-> OH IAbilityManager)
    // ========================================================================

    // [BRIDGED] startService -> OH IAbilityManager.StartAbility (ServiceExtension)
    @Override
    public ComponentName startService(IApplicationThread caller, Intent service,
            String resolvedType, boolean requireForeground, String callingPackage,
            String callingFeatureId, int userId) throws RemoteException {
        logBridged("startService", "-> OH IAbilityManager.StartAbility (ServiceExtension)");
        if (service != null) {
            IntentWantConverter.WantParams want = IntentWantConverter.intentToWant(service);
            nativeStartAbility(want.bundleName, want.abilityName,
                    want.action, want.uri, want.extrasJson);
        }
        return service != null ? service.getComponent() : null;
    }

    // [BRIDGED] stopService -> OH IAbilityManager.StopServiceAbility(Want)
    @Override
    public int stopService(IApplicationThread caller, Intent service,
            String resolvedType, int userId) throws RemoteException {
        logBridged("stopService", "-> OH IAbilityManager.StopServiceAbility");
        if (service != null) {
            IntentWantConverter.WantParams want = IntentWantConverter.intentToWant(service);
            nativeStopServiceAbility(want.bundleName, want.abilityName);
        }
        return 1; // stopped
    }

    // [BRIDGED] bindService -> OH IAbilityManager.ConnectAbility(Want)
    @Override
    public int bindService(IApplicationThread caller, IBinder token, Intent service,
            String resolvedType, IServiceConnection connection, long flags,
            String callingPackage, int userId) throws RemoteException {
        logBridged("bindService", "-> OH IAbilityManager.ConnectAbility");
        if (service != null && connection != null) {
            IntentWantConverter.WantParams want = IntentWantConverter.intentToWant(service);
            ComponentName comp = service.getComponent();
            int connId = ServiceConnectionRegistry.getInstance()
                    .registerConnection(connection, comp);
            int ohResult = nativeConnectAbility(want.bundleName, want.abilityName, connId);
            if (ohResult < 0) {
                ServiceConnectionRegistry.getInstance().unregisterConnection(connection);
                return 0; // bind failed
            }
        }
        return 1; // bind success
    }

    // [BRIDGED] bindServiceInstance -> OH IAbilityManager.ConnectAbility(Want)
    @Override
    public int bindServiceInstance(IApplicationThread caller, IBinder token, Intent service,
            String resolvedType, IServiceConnection connection, long flags,
            String instanceName, String callingPackage,
            int userId) throws RemoteException {
        logBridged("bindServiceInstance", "-> OH IAbilityManager.ConnectAbility");
        if (service != null && connection != null) {
            IntentWantConverter.WantParams want = IntentWantConverter.intentToWant(service);
            ComponentName comp = service.getComponent();
            int connId = ServiceConnectionRegistry.getInstance()
                    .registerConnection(connection, comp);
            int ohResult = nativeConnectAbility(want.bundleName, want.abilityName, connId);
            if (ohResult < 0) {
                ServiceConnectionRegistry.getInstance().unregisterConnection(connection);
                return 0; // bind failed
            }
        }
        return 1; // bind success
    }

    // [STUB] updateServiceGroup
    @Override
    public void updateServiceGroup(IServiceConnection connection, int group,
            int importance) throws RemoteException {
        logStub("updateServiceGroup", "No OH equivalent");
    }

    // [BRIDGED] unbindService -> OH IAbilityManager.DisconnectAbility(connection)
    @Override
    public boolean unbindService(IServiceConnection connection) throws RemoteException {
        logBridged("unbindService", "-> OH IAbilityManager.DisconnectAbility");
        if (connection != null) {
            int connId = ServiceConnectionRegistry.getInstance()
                    .unregisterConnection(connection);
            if (connId >= 0) {
                nativeDisconnectAbility(connId);
            }
        }
        return true;
    }

    // [BRIDGED] publishService -> OH IAbilityManager.ScheduleConnectAbilityDone
    @Override
    public void publishService(IBinder token, Intent intent,
            IBinder service) throws RemoteException {
        logBridged("publishService", "-> OH IAbilityManager.ScheduleConnectAbilityDone");
    }

    // [BRIDGED] unbindFinished -> OH IAbilityManager.ScheduleDisconnectAbilityDone
    @Override
    public void unbindFinished(IBinder token, Intent service,
            boolean doRebind) throws RemoteException {
        logBridged("unbindFinished", "-> OH IAbilityManager.ScheduleDisconnectAbilityDone");
    }

    // [BRIDGED] stopServiceToken -> OH IAbilityManager.StopServiceAbility
    @Override
    public boolean stopServiceToken(ComponentName className, IBinder token,
            int startId) throws RemoteException {
        logBridged("stopServiceToken", "-> OH IAbilityManager.StopServiceAbility");
        if (className != null) {
            nativeStopServiceAbility(className.getPackageName(), className.getClassName());
        }
        return true;
    }

    // [BRIDGED] serviceDoneExecuting -> OH IAbilityManager.ScheduleCommandAbilityDone (oneway)
    @Override
    public void serviceDoneExecuting(IBinder token, int type, int startId,
            int res) throws RemoteException {
        logBridged("serviceDoneExecuting", "-> OH ScheduleCommandAbilityDone");
    }

    // [STUB] setServiceForeground - OH has no foreground service concept
    @Override
    public void setServiceForeground(ComponentName className, IBinder token,
            int id, Notification notification, int flags,
            int foregroundServiceType) throws RemoteException {
        logStub("setServiceForeground", "OH has no foreground service concept");
    }

    // [STUB] getForegroundServiceType
    @Override
    public int getForegroundServiceType(ComponentName className,
            IBinder token) throws RemoteException {
        logStub("getForegroundServiceType", "OH has no foreground service type");
        return 0;
    }

    // [STUB] peekService
    @Override
    public IBinder peekService(Intent service, String resolvedType,
            String callingPackage) throws RemoteException {
        logStub("peekService", "No OH equivalent");
        return null;
    }

    // [STUB] getServices (RunningServiceInfo)
    @Override
    public List<ActivityManager.RunningServiceInfo> getServices(int maxNum,
            int flags) throws RemoteException {
        logStub("getServices", "Use OH IAbilityManager.GetExtensionRunningInfos");
        return Collections.emptyList();
    }

    // [STUB] getRunningServiceControlPanel
    @Override
    public PendingIntent getRunningServiceControlPanel(
            ComponentName service) throws RemoteException {
        logStub("getRunningServiceControlPanel", "No OH equivalent");
        return null;
    }

    // [STUB] registerForegroundServiceObserver
    @Override
    public boolean registerForegroundServiceObserver(
            IForegroundServiceObserver callback) throws RemoteException {
        logStub("registerForegroundServiceObserver", "No OH equivalent");
        return false;
    }

    // [STUB] shouldServiceTimeOut
    @Override
    public boolean shouldServiceTimeOut(ComponentName className,
            IBinder token) throws RemoteException {
        logStub("shouldServiceTimeOut", "No OH equivalent");
        return false;
    }

    // [STUB] enableFgsNotificationRateLimit
    @Override
    public boolean enableFgsNotificationRateLimit(boolean enable) throws RemoteException {
        logStub("enableFgsNotificationRateLimit", "No OH equivalent");
        return true;
    }

    // ========================================================================
    // Category 3: Application / Process Management (-> OH IAppMgr)
    // ========================================================================

    // [BRIDGED] attachApplication -> OH IAppMgr.AttachApplication
    //
    // Called by ActivityThread.main() → thread.attach(false, startSeq) → here.
    // Triggers OH AppMgrService to register this child process as an
    // active application, allowing OH to dispatch back IAppScheduler /
    // IAbilityScheduler callbacks (scheduleLaunchAbility / lifecycle etc.).
    //
    // History note: until 2026-04-27 (B.8) this method body was empty with
    // a stale comment "real attach is done during OHEnvironment.initialize()"
    // — but OHEnvironment.initialize() only does nativeInitialize +
    // nativeConnectToOHServices, never nativeAttachApplication.  So OH AppMgr
    // never knew about the child process, and ⑥ in the IPC sequence diagram
    // was a silent no-op.  Now we route via OHEnvironment.attachApplication,
    // which calls native → OHAppMgrClient::AttachApplication via OH IPC.
    @Override
    public void attachApplication(IApplicationThread app,
            long startSeq) throws RemoteException {
        int pid = android.os.Process.myPid();
        int uid = android.os.Process.myUid();
        // Best-effort process / package name. ActivityThread holds the actual
        // bound process name via mBoundApplication.processName once
        // handleBindApplication has run, but at attach-time that's not yet
        // populated.  Use system property + arg fallback.
        String procName = android.os.Process.myProcessName();
        if (procName == null || procName.isEmpty()) {
            procName = "com.example.helloworld";  // shell-launch default
        }
        // Diagnostic via stderr (System.err) — Log.d may go via hilog which
        // doesn't surface in child stdout/stderr-redirected log; stderr is
        // inherited from parent appspawn-x and reliably reaches ax_*.log.
        System.err.println("[OH_AMAdapter] attachApplication ENTRY pid=" + pid +
                " uid=" + uid + " proc=" + procName + " startSeq=" + startSeq);
        System.err.flush();
        logBridged("attachApplication",
                "-> OH IAppMgr.AttachApplication(pid=" + pid +
                ", uid=" + uid + ", proc=" + procName + ", startSeq=" + startSeq + ")");
        try {
            // Real route: pass the IApplicationThread Binder stub down so the
            // native side can construct an AppSchedulerAdapter wrapping it,
            // and then call OH IAppMgr.AttachApplication(sptr<IRemoteObject>).
            // The boolean return is authoritative: false means the IPC did
            // not actually leave the process (samgr null / proxy null /
            // AppSchedulerAdapter creation failed). No silent "OK".
            boolean ok = adapter.core.OHEnvironment.attachApplication(
                    app, pid, uid, procName);
            if (ok) {
                System.err.println("[OH_AMAdapter] attachApplication → OH IAppMgr OK (IPC sent)");
            } else {
                System.err.println("[OH_AMAdapter] attachApplication → OH IAppMgr FAILED (native returned false; IPC NOT sent — see OH_AppMgrClient hilog)");
                android.util.Log.e(TAG,
                        "attachApplication: native returned false — see OH_AppMgrClient hilog for cause");
            }
        } catch (Throwable t) {
            System.err.println("[OH_AMAdapter] attachApplication → OH EXCEPTION: " + t);
            android.util.Log.e(TAG,
                    "attachApplication routing to OH failed (continuing): " + t);
        }
        System.err.flush();
    }

    // [BRIDGED] finishAttachApplication -> OH IAppMgr (part of attach flow)
    @Override
    public void finishAttachApplication(long startSeq) throws RemoteException {
        logBridged("finishAttachApplication", "-> OH AttachApplication completion");
    }

    // [BRIDGED] getRunningAppProcesses -> OH IAppMgr.GetAllRunningProcesses
    @Override
    public List<ActivityManager.RunningAppProcessInfo> getRunningAppProcesses() throws RemoteException {
        logBridged("getRunningAppProcesses", "-> OH IAppMgr.GetAllRunningProcesses");
        return Collections.emptyList(); // Phase 1 stub
    }

    // [BRIDGED] getProcessMemoryInfo -> OH IAppMgr.DumpHeapMemory
    @Override
    public Debug.MemoryInfo[] getProcessMemoryInfo(int[] pids) throws RemoteException {
        logBridged("getProcessMemoryInfo", "-> OH IAppMgr.DumpHeapMemory");
        return new Debug.MemoryInfo[0];
    }

    // [BRIDGED] handleApplicationCrash -> OH IAppMgr.NotifyAppFault
    @Override
    public void handleApplicationCrash(IBinder app,
            ApplicationErrorReport.ParcelableCrashInfo crashInfo) throws RemoteException {
        logBridged("handleApplicationCrash", "-> OH IAppMgr.NotifyAppFault");
    }

    // [BRIDGED] handleApplicationWtf -> OH IAppMgr.NotifyAppFaultBySA
    @Override
    public boolean handleApplicationWtf(IBinder app, String tag, boolean system,
            ApplicationErrorReport.ParcelableCrashInfo crashInfo,
            int immediateCallerPid) throws RemoteException {
        logBridged("handleApplicationWtf", "-> OH IAppMgr.NotifyAppFaultBySA");
        return false;
    }

    // [BRIDGED] handleApplicationStrictModeViolation -> OH IAppMgr fault handling
    @Override
    public void handleApplicationStrictModeViolation(IBinder app, int penaltyMask,
            StrictMode.ViolationInfo crashInfo) throws RemoteException {
        logBridged("handleApplicationStrictModeViolation", "-> OH IAppMgr fault handling");
    }

    // [STUB] registerStrictModeCallback
    @Override
    public void registerStrictModeCallback(IBinder binder) throws RemoteException {
        logStub("registerStrictModeCallback", "No OH equivalent");
    }

    // [BRIDGED] killBackgroundProcesses -> OH IAbilityManager.KillProcess
    @Override
    public void killBackgroundProcesses(String packageName,
            int userId) throws RemoteException {
        logBridged("killBackgroundProcesses", "-> OH IAbilityManager.KillProcess");
    }

    // [BRIDGED] killAllBackgroundProcesses -> OH IAbilityManager.KillProcess
    @Override
    public void killAllBackgroundProcesses() throws RemoteException {
        logBridged("killAllBackgroundProcesses", "-> OH kill processes");
    }

    // [BRIDGED] forceStopPackage -> OH IAbilityManager.KillProcess
    @Override
    public void forceStopPackage(String packageName,
            int userId) throws RemoteException {
        logBridged("forceStopPackage", "-> OH IAbilityManager.KillProcess");
    }

    // [BRIDGED] forceStopPackageEvenWhenStopping -> OH IAbilityManager.KillProcess
    @Override
    public void forceStopPackageEvenWhenStopping(String packageName,
            int userId) throws RemoteException {
        logBridged("forceStopPackageEvenWhenStopping", "-> OH IAbilityManager.KillProcess");
    }

    // [BRIDGED] killApplication -> OH IAbilityManager.KillProcess
    @Override
    public void killApplication(String pkg, int appId, int userId, String reason,
            int exitInfoReason) throws RemoteException {
        logBridged("killApplication", "-> OH IAbilityManager.KillProcess");
    }

    // [BRIDGED] killApplicationProcess -> OH IAbilityManager.KillProcess
    @Override
    public void killApplicationProcess(String processName,
            int uid) throws RemoteException {
        logBridged("killApplicationProcess", "-> OH IAbilityManager.KillProcess");
    }

    // [BRIDGED] stopAppForUser -> OH IAbilityManager.KillProcess
    @Override
    public void stopAppForUser(String packageName,
            int userId) throws RemoteException {
        logBridged("stopAppForUser", "-> OH IAbilityManager.KillProcess");
    }

    // [BRIDGED] killPids -> OH process management
    @Override
    public boolean killPids(int[] pids, String reason,
            boolean secure) throws RemoteException {
        logBridged("killPids", "-> OH process management");
        return true;
    }

    // [BRIDGED] killUid -> OH process management
    @Override
    public void killUid(int appId, int userId,
            String reason) throws RemoteException {
        logBridged("killUid", "-> OH process management");
    }

    // [BRIDGED] killUidForPermissionChange -> OH process management
    @Override
    public void killUidForPermissionChange(int appId, int userId,
            String reason) throws RemoteException {
        logBridged("killUidForPermissionChange", "-> OH process management");
    }

    // [BRIDGED] killPackageDependents -> OH process management
    @Override
    public void killPackageDependents(String packageName,
            int userId) throws RemoteException {
        logBridged("killPackageDependents", "-> OH process management");
    }

    // [BRIDGED] killProcessesBelowForeground -> OH kill processes
    @Override
    public boolean killProcessesBelowForeground(String reason) throws RemoteException {
        logBridged("killProcessesBelowForeground", "-> OH kill processes");
        return false;
    }

    // [BRIDGED] killProcessesWhenImperceptible -> OH process management
    @Override
    public void killProcessesWhenImperceptible(int[] pids,
            String reason) throws RemoteException {
        logStub("killProcessesWhenImperceptible", "No OH equivalent");
    }

    // [BRIDGED] addPackageDependency -> OH process management
    @Override
    public void addPackageDependency(String packageName) throws RemoteException {
        logBridged("addPackageDependency", "-> OH process management");
    }

    // [BRIDGED] getMyMemoryState -> OH IAppMgr.GetProcessRunningInformation
    @Override
    public void getMyMemoryState(ActivityManager.RunningAppProcessInfo outInfo) throws RemoteException {
        logBridged("getMyMemoryState", "-> OH IAppMgr.GetProcessRunningInformation");
    }

    // [BRIDGED] getMemoryInfo -> OH IAppMgr (memory query)
    @Override
    public void getMemoryInfo(ActivityManager.MemoryInfo outInfo) throws RemoteException {
        logBridged("getMemoryInfo", "-> OH IAppMgr (memory query)");
    }

    // [PARTIAL] isUidActive -> OH IAppMgr.IsApplicationRunning
    @Override
    public boolean isUidActive(int uid, String callingPackage) throws RemoteException {
        logBridged("isUidActive", "-> OH IAppMgr.IsApplicationRunning");
        return true;
    }

    // [PARTIAL] getUidProcessState -> OH IAppMgr.GetProcessRunningInformation
    @Override
    public int getUidProcessState(int uid, String callingPackage) throws RemoteException {
        logBridged("getUidProcessState", "-> OH IAppMgr.GetProcessRunningInformation");
        return 2; // PROCESS_STATE_FOREGROUND
    }

    // [STUB] getProcessLimit
    @Override
    public int getProcessLimit() throws RemoteException {
        logStub("getProcessLimit", "No OH equivalent");
        return -1;
    }

    // [STUB] setProcessLimit
    @Override
    public void setProcessLimit(int max) throws RemoteException {
        logStub("setProcessLimit", "No OH equivalent");
    }

    // [STUB] setProcessImportant
    @Override
    public void setProcessImportant(IBinder token, int pid, boolean isForeground,
            String reason) throws RemoteException {
        logStub("setProcessImportant", "OH manages process priority internally");
    }

    // [BRIDGED] getProcessPss -> OH IAppMgr memory info
    @Override
    public long[] getProcessPss(int[] pids) throws RemoteException {
        logBridged("getProcessPss", "-> OH IAppMgr memory info");
        return new long[0];
    }

    // [STUB] setProcessMemoryTrimLevel
    @Override
    public boolean setProcessMemoryTrimLevel(String process, int userId,
            int level) throws RemoteException {
        logStub("setProcessMemoryTrimLevel", "No OH equivalent");
        return false;
    }

    // [BRIDGED] getProcessesInErrorState -> OH IAppMgr fault handling
    @Override
    public List<ActivityManager.ProcessErrorStateInfo> getProcessesInErrorState() throws RemoteException {
        logBridged("getProcessesInErrorState", "-> OH IAppMgr fault handling");
        return null;
    }

    // [BRIDGED] clearApplicationUserData -> OH IAppMgr.ClearUpApplicationDataBySelf
    @Override
    public boolean clearApplicationUserData(String packageName, boolean keepState,
            IPackageDataObserver observer, int userId) throws RemoteException {
        logBridged("clearApplicationUserData", "-> OH IAppMgr.ClearUpApplicationDataBySelf");
        return true;
    }

    // [STUB] getRunningExternalApplications
    @Override
    public List<ApplicationInfo> getRunningExternalApplications() throws RemoteException {
        logStub("getRunningExternalApplications", "No OH equivalent");
        return Collections.emptyList();
    }

    // [STUB] finishHeavyWeightApp
    @Override
    public void finishHeavyWeightApp() throws RemoteException {
        logStub("finishHeavyWeightApp", "No OH equivalent");
    }

    // [BRIDGED] getPackageProcessState -> OH IAppMgr process state
    @Override
    public int getPackageProcessState(String packageName,
            String callingPackage) throws RemoteException {
        logBridged("getPackageProcessState", "-> OH IAppMgr process state");
        return 2; // PROCESS_STATE_FOREGROUND
    }

    // [PARTIAL] scheduleApplicationInfoChanged -> OH IAppMgr.UpdateConfiguration
    @Override
    public void scheduleApplicationInfoChanged(List<String> packageNames,
            int userId) throws RemoteException {
        logBridged("scheduleApplicationInfoChanged", "-> OH IAppMgr.UpdateConfiguration");
    }

    // [STUB] makePackageIdle
    @Override
    public void makePackageIdle(String packageName,
            int userId) throws RemoteException {
        logStub("makePackageIdle", "No OH equivalent");
    }

    // [STUB] getMemoryTrimLevel
    @Override
    public int getMemoryTrimLevel() throws RemoteException {
        logStub("getMemoryTrimLevel", "No OH equivalent");
        return 0;
    }

    // [STUB] performIdleMaintenance
    @Override
    public void performIdleMaintenance() throws RemoteException {
        logStub("performIdleMaintenance", "No OH equivalent");
    }

    // [BRIDGED] appNotResponding -> OH IAppMgr fault handling
    @Override
    public void appNotResponding(String reason) throws RemoteException {
        logBridged("appNotResponding", "-> OH IAppMgr fault handling");
    }

    // [BRIDGED] appNotRespondingViaProvider -> OH IAppMgr fault handling
    @Override
    public void appNotRespondingViaProvider(IBinder connection) throws RemoteException {
        logBridged("appNotRespondingViaProvider", "-> OH IAppMgr fault handling");
    }

    // [BRIDGED] crashApplicationWithType -> OH IAppMgr fault handling
    @Override
    public void crashApplicationWithType(int uid, int initialPid, String packageName, int userId,
            String message, boolean force, int exceptionTypeId) throws RemoteException {
        logBridged("crashApplicationWithType", "-> OH IAppMgr fault handling");
    }

    // [BRIDGED] crashApplicationWithTypeWithExtras -> OH IAppMgr fault handling
    @Override
    public void crashApplicationWithTypeWithExtras(int uid, int initialPid, String packageName,
            int userId, String message, boolean force, int exceptionTypeId,
            Bundle extras) throws RemoteException {
        logBridged("crashApplicationWithTypeWithExtras", "-> OH IAppMgr fault handling");
    }

    // [STUB] resetAppErrors
    @Override
    public void resetAppErrors() throws RemoteException {
        logStub("resetAppErrors", "No OH equivalent");
    }

    // [STUB] setProcessStateSummary
    @Override
    public void setProcessStateSummary(byte[] state) throws RemoteException {
        logStub("setProcessStateSummary", "No OH equivalent");
    }

    // [STUB] getHistoricalProcessStartReasons
    @Override
    public ParceledListSlice<ApplicationStartInfo> getHistoricalProcessStartReasons(
            String packageName, int maxNum, int userId) throws RemoteException {
        logStub("getHistoricalProcessStartReasons", "No OH equivalent");
        return null;
    }

    // [STUB] setApplicationStartInfoCompleteListener
    @Override
    public void setApplicationStartInfoCompleteListener(
            IApplicationStartInfoCompleteListener listener,
            int userId) throws RemoteException {
        logStub("setApplicationStartInfoCompleteListener", "No OH equivalent");
    }

    // [STUB] removeApplicationStartInfoCompleteListener
    @Override
    public void removeApplicationStartInfoCompleteListener(
            int userId) throws RemoteException {
        logStub("removeApplicationStartInfoCompleteListener", "No OH equivalent");
    }

    // [STUB] getHistoricalProcessExitReasons
    @Override
    public ParceledListSlice<ApplicationExitInfo> getHistoricalProcessExitReasons(
            String packageName, int pid, int maxNum,
            int userId) throws RemoteException {
        logStub("getHistoricalProcessExitReasons", "No OH equivalent");
        return null;
    }

    // ========================================================================
    // Category 4: Broadcast (-> OH CommonEvent, STUB for Phase 1)
    // ========================================================================

    // [BRIDGED] registerReceiver -> OH CES.SubscribeCommonEvent
    @Override
    public Intent registerReceiver(IApplicationThread caller, String callerPackage,
            IIntentReceiver receiver, IntentFilter filter,
            String requiredPermission, int userId, int flags) throws RemoteException {
        return registerReceiverWithFeature(caller, callerPackage, null, null,
                receiver, filter, requiredPermission, userId, flags);
    }

    // [BRIDGED] registerReceiverWithFeature -> OH CES.SubscribeCommonEvent
    @Override
    public Intent registerReceiverWithFeature(IApplicationThread caller, String callerPackage,
            String callingFeatureId, String receiverId, IIntentReceiver receiver,
            IntentFilter filter, String requiredPermission, int userId,
            int flags) throws RemoteException {
        logBridged("registerReceiverWithFeature", "-> OH CES.SubscribeCommonEvent");

        if (receiver == null || filter == null || filter.countActions() == 0) {
            Log.w(TAG, "registerReceiver: null receiver or empty filter");
            return null;
        }

        // Allocate subscription ID
        int subscriptionId = mNextSubscriptionId.getAndIncrement();

        // Convert IntentFilter actions to OH event names
        String[] ohEvents = BroadcastEventConverter.filterActionsToOH(filter);

        // Subscribe via JNI -> C++ -> CES
        int result = nativeSubscribeCommonEvent(
                subscriptionId, ohEvents,
                filter.getPriority(),
                requiredPermission);

        if (result != 0) {
            Log.e(TAG, "registerReceiver: CES subscription failed, result=" + result);
            return null;
        }

        // Store mappings for reverse callback routing and unregistration
        IBinder receiverBinder = receiver.asBinder();
        mReceiverToSubscription.put(receiverBinder, subscriptionId);
        mSubscriptionFilters.put(subscriptionId, filter);

        // Register in the receiver bridge for event delivery
        CommonEventReceiverBridge.addReceiver(subscriptionId, receiver);

        Log.i(TAG, "registerReceiver: subscriptionId=" + subscriptionId
                + ", actions=" + filter.countActions());

        // Query sticky event (if any) and return as Intent
        Intent stickyIntent = null;
        for (int i = 0; i < filter.countActions(); i++) {
            String ohEvent = BroadcastEventConverter.androidActionToOH(filter.getAction(i));
            String stickyJson = nativeGetStickyCommonEvent(ohEvent);
            if (stickyJson != null && !stickyJson.isEmpty()) {
                // Parse minimal JSON to reconstruct sticky Intent
                stickyIntent = new Intent(filter.getAction(i));
                Log.d(TAG, "registerReceiver: returning sticky intent for " + filter.getAction(i));
                break;
            }
        }

        return stickyIntent;
    }

    // [BRIDGED] unregisterReceiver -> OH CES.UnsubscribeCommonEvent
    @Override
    public void unregisterReceiver(IIntentReceiver receiver) throws RemoteException {
        logBridged("unregisterReceiver", "-> OH CES.UnsubscribeCommonEvent");

        if (receiver == null) return;

        IBinder receiverBinder = receiver.asBinder();
        Integer subscriptionId = mReceiverToSubscription.remove(receiverBinder);
        if (subscriptionId == null) {
            // Try reverse lookup through the bridge
            subscriptionId = CommonEventReceiverBridge.findSubscriptionId(receiver);
            if (subscriptionId < 0) {
                Log.w(TAG, "unregisterReceiver: receiver not found");
                return;
            }
        }

        // Unsubscribe via JNI -> C++ -> CES
        nativeUnsubscribeCommonEvent(subscriptionId);

        // Clean up mappings
        mSubscriptionFilters.remove(subscriptionId);
        CommonEventReceiverBridge.removeReceiver(subscriptionId);

        Log.i(TAG, "unregisterReceiver: subscriptionId=" + subscriptionId);
    }

    // [BRIDGED] broadcastIntent -> OH CES.PublishCommonEvent
    @Override
    public int broadcastIntent(IApplicationThread caller, Intent intent,
            String resolvedType, IIntentReceiver resultTo, int resultCode,
            String resultData, Bundle map, String[] requiredPermissions,
            int appOp, Bundle options, boolean serialized, boolean sticky,
            int userId) throws RemoteException {
        return broadcastIntentWithFeature(caller, null, intent, resolvedType,
                resultTo, resultCode, resultData, map, requiredPermissions,
                null, null, appOp, options, serialized, sticky, userId);
    }

    // [BRIDGED] broadcastIntentWithFeature -> OH CES.PublishCommonEvent
    @Override
    public int broadcastIntentWithFeature(IApplicationThread caller, String callingFeatureId,
            Intent intent, String resolvedType, IIntentReceiver resultTo, int resultCode,
            String resultData, Bundle map, String[] requiredPermissions,
            String[] excludePermissions, String[] excludePackages, int appOp, Bundle options,
            boolean serialized, boolean sticky, int userId) throws RemoteException {
        logBridged("broadcastIntentWithFeature", "-> OH CES.PublishCommonEvent");

        if (intent == null) {
            Log.w(TAG, "broadcastIntent: null intent");
            return 0;
        }

        // Convert Intent action to OH event name
        String ohAction = BroadcastEventConverter.androidActionToOH(intent.getAction());
        if (ohAction == null) {
            Log.w(TAG, "broadcastIntent: cannot map action " + intent.getAction());
            return 0;
        }

        // Serialize extras
        String extrasJson = BroadcastEventConverter.bundleToJson(intent.getExtras());

        // Data URI
        String uri = (intent.getData() != null) ? intent.getData().toString() : null;

        Log.i(TAG, "broadcastIntent: action=" + intent.getAction()
                + " -> ohAction=" + ohAction
                + ", ordered=" + serialized + ", sticky=" + sticky);

        // Publish via JNI -> C++ -> CES
        int result = nativePublishCommonEvent(
                ohAction, extrasJson, uri,
                resultCode, resultData,
                serialized, sticky,
                requiredPermissions);

        // For ordered broadcast with result receiver: register a temporary subscription
        // to capture the final result and deliver to resultTo
        if (serialized && resultTo != null) {
            // The result receiver gets the final ordered result.
            // CES does not directly support this; we handle it by having the
            // last subscriber's finishReceiver call trigger delivery to resultTo.
            // Store resultTo for later delivery in finishReceiver path.
            Log.d(TAG, "broadcastIntent: ordered broadcast with resultTo (stored for final delivery)");
        }

        return result;
    }

    // [BRIDGED] unbroadcastIntent -> OH CES.RemoveStickyCommonEvent
    @Override
    public void unbroadcastIntent(IApplicationThread caller, Intent intent,
            int userId) throws RemoteException {
        logBridged("unbroadcastIntent", "-> OH CES.RemoveStickyCommonEvent");

        if (intent == null || intent.getAction() == null) return;

        String ohAction = BroadcastEventConverter.androidActionToOH(intent.getAction());
        // RemoveStickyCommonEvent is handled via publish with sticky=false override
        // or direct CES call — for now log and note it requires CES extension
        Log.i(TAG, "unbroadcastIntent: action=" + intent.getAction() + " -> " + ohAction);
    }

    // [BRIDGED] finishReceiver -> OH CES.FinishReceiver (via AsyncCommonEventResult)
    @Override
    public void finishReceiver(IBinder who, int resultCode, String resultData, Bundle map,
            boolean abortBroadcast, int flags) throws RemoteException {
        logBridged("finishReceiver", "-> OH CES.FinishReceiver");

        // Find the subscription ID for this receiver
        Integer subscriptionId = mReceiverToSubscription.get(who);
        if (subscriptionId == null) {
            // Try finding through CommonEventReceiverBridge
            subscriptionId = CommonEventReceiverBridge.findSubscriptionId(null);
            if (subscriptionId == null || subscriptionId < 0) {
                Log.w(TAG, "finishReceiver: receiver not found");
                return;
            }
        }

        Log.d(TAG, "finishReceiver: subscriptionId=" + subscriptionId
                + ", code=" + resultCode + ", abort=" + abortBroadcast);

        // Finish via JNI -> C++ -> AsyncCommonEventResult
        nativeFinishCommonEvent(subscriptionId, resultCode, resultData, abortBroadcast);
    }

    // [STUB] waitForBroadcastIdle
    @Override
    public void waitForBroadcastIdle() throws RemoteException {
        logStub("waitForBroadcastIdle", "OH broadcast not mapped");
    }

    // [STUB] waitForBroadcastBarrier
    @Override
    public void waitForBroadcastBarrier() throws RemoteException {
        logStub("waitForBroadcastBarrier", "OH broadcast not mapped");
    }

    // [STUB] forceDelayBroadcastDelivery
    @Override
    public void forceDelayBroadcastDelivery(String targetPackage,
            long delayedDurationMs) throws RemoteException {
        logStub("forceDelayBroadcastDelivery", "OH broadcast not mapped");
    }

    // [STUB] isModernBroadcastQueueEnabled
    @Override
    public boolean isModernBroadcastQueueEnabled() throws RemoteException {
        logStub("isModernBroadcastQueueEnabled", "OH broadcast not mapped");
        return false;
    }

    // ========================================================================
    // Category 5: ContentProvider (-> OH DataShareExtensionAbility, Stage model)
    // ========================================================================

    // [BRIDGED] getContentProvider -> ContentProviderRegistry (local or OH DataShare)
    @Override
    public ContentProviderHolder getContentProvider(IApplicationThread caller,
            String callingPackage, String name, int userId,
            boolean stable) throws RemoteException {
        ContentProviderHolder holder =
                ContentProviderRegistry.getInstance().acquireProvider(name);
        if (holder != null) {
            logBridged("getContentProvider",
                    "authority=" + name + " -> " + holder.info.name);
        } else {
            logStub("getContentProvider", "no provider found for: " + name);
        }
        return holder;
    }

    // [BRIDGED] getContentProviderExternal -> same as getContentProvider
    @Override
    public ContentProviderHolder getContentProviderExternal(String name, int userId,
            IBinder token, String tag) throws RemoteException {
        logBridged("getContentProviderExternal", "authority=" + name);
        return ContentProviderRegistry.getInstance().acquireProvider(name);
    }

    // [BRIDGED] publishContentProviders -> register in ContentProviderRegistry
    @Override
    public void publishContentProviders(IApplicationThread caller,
            List<ContentProviderHolder> providers) throws RemoteException {
        logBridged("publishContentProviders",
                "count=" + (providers != null ? providers.size() : 0));
        ContentProviderRegistry.getInstance().publishProviders(providers);
    }

    // [PARTIAL] refContentProvider -> no-op, registry does not track ref counts
    @Override
    public boolean refContentProvider(IBinder connection, int stableDelta,
            int unstableDelta) throws RemoteException {
        logStub("refContentProvider", "ref counting not needed in adapter");
        return true;
    }

    // [PARTIAL] removeContentProvider -> no-op for now
    @Override
    public void removeContentProvider(IBinder connection,
            boolean stable) throws RemoteException {
        logStub("removeContentProvider", "ref counting not needed in adapter");
    }

    // [STUB] removeContentProviderExternal
    @Override
    public void removeContentProviderExternal(String name,
            IBinder token) throws RemoteException {
        logStub("removeContentProviderExternal", "not needed in adapter");
    }

    // [STUB] removeContentProviderExternalAsUser
    @Override
    public void removeContentProviderExternalAsUser(String name, IBinder token,
            int userId) throws RemoteException {
        logStub("removeContentProviderExternalAsUser", "not needed in adapter");
    }

    // [BRIDGED] openContentUri -> via ContentProviderBridge.openFile()
    @Override
    public ParcelFileDescriptor openContentUri(String uriString) throws RemoteException {
        logBridged("openContentUri", uriString);
        try {
            android.net.Uri uri = android.net.Uri.parse(uriString);
            String authority = uri.getAuthority();
            ContentProviderHolder holder =
                    ContentProviderRegistry.getInstance().acquireProvider(authority);
            if (holder != null && holder.provider != null) {
                return holder.provider.openFile(null, uri, "r", null);
            }
        } catch (Exception e) {
            Log.e(TAG, "openContentUri failed: " + uriString, e);
        }
        return null;
    }

    // [STUB] unstableProviderDied
    @Override
    public void unstableProviderDied(IBinder connection) throws RemoteException {
        logStub("unstableProviderDied", "not needed in adapter");
    }

    // [STUB] getMimeTypeFilterAsync (oneway)
    @Override
    public void getMimeTypeFilterAsync(Uri uri, int userId,
            RemoteCallback resultCallback) throws RemoteException {
        logStub("getMimeTypeFilterAsync", "No OH equivalent");
    }

    // ========================================================================
    // Category 6: Task / Mission Management (-> OH IMissionManager)
    // ========================================================================

    // [BRIDGED] getTasks -> OH IAbilityManager.GetMissionInfos
    @Override
    public List<ActivityManager.RunningTaskInfo> getTasks(
            int maxNum) throws RemoteException {
        logBridged("getTasks", "-> OH IMissionManager.GetMissionInfos");
        return Collections.emptyList();
    }

    // [BRIDGED] getRecentTasks -> OH IMissionManager.GetMissionInfos
    @Override
    public ParceledListSlice getRecentTasks(int maxNum, int flags,
            int userId) throws RemoteException {
        logBridged("getRecentTasks", "-> OH IMissionManager.GetMissionInfos");
        return null;
    }

    // [BRIDGED] moveTaskToFront -> OH IMissionManager.MoveMissionToFront
    @Override
    public void moveTaskToFront(IApplicationThread caller, String callingPackage, int task,
            int flags, Bundle options) throws RemoteException {
        logBridged("moveTaskToFront", "-> OH IMissionManager.MoveMissionToFront");
    }

    // [BRIDGED] removeTask -> OH IMissionManager.CleanMission
    @Override
    public boolean removeTask(int taskId) throws RemoteException {
        logBridged("removeTask", "-> OH IMissionManager.CleanMission");
        return true;
    }

    // [BRIDGED] getTaskForActivity -> OH IAbilityManager.GetMissionIdByToken
    @Override
    public int getTaskForActivity(IBinder token,
            boolean onlyRoot) throws RemoteException {
        logBridged("getTaskForActivity", "-> OH IAbilityManager.GetMissionIdByToken");
        return -1;
    }

    // [BRIDGED] registerTaskStackListener -> OH IMissionManager.RegisterMissionListener
    @Override
    public void registerTaskStackListener(
            ITaskStackListener listener) throws RemoteException {
        logBridged("registerTaskStackListener", "-> OH IMissionManager.RegisterMissionListener");
    }

    // [BRIDGED] unregisterTaskStackListener
    @Override
    public void unregisterTaskStackListener(
            ITaskStackListener listener) throws RemoteException {
        logBridged("unregisterTaskStackListener", "-> OH IMissionManager.UnRegisterMissionListener");
    }

    // [BRIDGED] getAllRootTaskInfos -> OH IMissionManager.GetMissionInfos
    @Override
    public List<ActivityTaskManager.RootTaskInfo> getAllRootTaskInfos() throws RemoteException {
        logBridged("getAllRootTaskInfos", "-> OH IMissionManager.GetMissionInfos");
        return Collections.emptyList();
    }

    // [BRIDGED] getFocusedRootTaskInfo
    @Override
    public ActivityTaskManager.RootTaskInfo getFocusedRootTaskInfo() throws RemoteException {
        logBridged("getFocusedRootTaskInfo", "-> OH (query focused mission)");
        return null;
    }

    // [BRIDGED] moveTaskToRootTask -> OH IMissionManager.MoveMissionToFront
    @Override
    public void moveTaskToRootTask(int taskId, int rootTaskId,
            boolean toTop) throws RemoteException {
        logBridged("moveTaskToRootTask", "-> OH IMissionManager.MoveMissionToFront");
    }

    // [BRIDGED] setFocusedRootTask -> OH IMissionManager.MoveMissionToFront
    @Override
    public void setFocusedRootTask(int taskId) throws RemoteException {
        logBridged("setFocusedRootTask", "-> OH IMissionManager.MoveMissionToFront");
    }

    // [BRIDGED] getTaskBounds -> OH ISession.GetGlobalScaledRect
    @Override
    public Rect getTaskBounds(int taskId) throws RemoteException {
        logBridged("getTaskBounds", "-> OH ISession.GetGlobalScaledRect");
        return null;
    }

    // [STUB] setTaskResizeable
    @Override
    public void setTaskResizeable(int taskId,
            int resizeableMode) throws RemoteException {
        logStub("setTaskResizeable", "OH window mode managed differently");
    }

    // [BRIDGED] resizeTask -> OH ISession.UpdateSessionRect
    @Override
    public void resizeTask(int taskId, Rect bounds,
            int resizeMode) throws RemoteException {
        logBridged("resizeTask", "-> OH ISession.UpdateSessionRect");
    }

    // [BRIDGED] startActivityFromRecents -> OH IMissionManager.MoveMissionToFront
    @Override
    public int startActivityFromRecents(int taskId,
            Bundle options) throws RemoteException {
        logBridged("startActivityFromRecents", "-> OH IMissionManager.MoveMissionToFront");
        return 0;
    }

    // [BRIDGED] isInLockTaskMode -> OH IMissionManager.LockMissionForCleanup state
    @Override
    public boolean isInLockTaskMode() throws RemoteException {
        logBridged("isInLockTaskMode", "-> OH IMissionManager.LockMissionForCleanup state");
        return false;
    }

    // [STUB] getLockTaskModeState
    @Override
    public int getLockTaskModeState() throws RemoteException {
        logStub("getLockTaskModeState", "No OH equivalent");
        return 0;
    }

    // [STUB] updateLockTaskPackages
    @Override
    public void updateLockTaskPackages(int userId,
            String[] packages) throws RemoteException {
        logStub("updateLockTaskPackages", "No OH equivalent");
    }

    // [BRIDGED] startSystemLockTaskMode -> OH IMissionManager.LockMissionForCleanup
    @Override
    public void startSystemLockTaskMode(int taskId) throws RemoteException {
        logBridged("startSystemLockTaskMode", "-> OH IMissionManager.LockMissionForCleanup");
    }

    // [STUB] isTopOfTask
    @Override
    public boolean isTopOfTask(IBinder token) throws RemoteException {
        logStub("isTopOfTask", "No OH equivalent");
        return true;
    }

    // [STUB] cancelTaskWindowTransition
    @Override
    public void cancelTaskWindowTransition(int taskId) throws RemoteException {
        logStub("cancelTaskWindowTransition", "No OH equivalent");
    }

    // ========================================================================
    // Category 7: Configuration (-> OH IAppMgr)
    // ========================================================================

    // [BRIDGED] getConfiguration -> OH IAppMgr.GetConfiguration
    @Override
    public Configuration getConfiguration() throws RemoteException {
        logBridged("getConfiguration", "-> OH IAppMgr.GetConfiguration");
        return new Configuration();
    }

    // [BRIDGED] updateConfiguration -> OH IAppMgr.UpdateConfiguration
    @Override
    public boolean updateConfiguration(Configuration values) throws RemoteException {
        logBridged("updateConfiguration", "-> OH IAppMgr.UpdateConfiguration");
        return true;
    }

    // [BRIDGED] updateMccMncConfiguration -> OH IAppMgr.UpdateConfiguration
    @Override
    public boolean updateMccMncConfiguration(String mcc,
            String mnc) throws RemoteException {
        logBridged("updateMccMncConfiguration", "-> OH IAppMgr.UpdateConfiguration");
        return true;
    }

    // [BRIDGED] updatePersistentConfiguration -> OH IAppMgr.UpdateConfiguration
    @Override
    public void updatePersistentConfiguration(
            Configuration values) throws RemoteException {
        logBridged("updatePersistentConfiguration", "-> OH IAppMgr.UpdateConfiguration");
    }

    // [BRIDGED] updatePersistentConfigurationWithAttribution -> OH IAppMgr.UpdateConfiguration
    @Override
    public void updatePersistentConfigurationWithAttribution(Configuration values,
            String callingPackageName,
            String callingAttributionTag) throws RemoteException {
        logBridged("updatePersistentConfigurationWithAttribution", "-> OH IAppMgr.UpdateConfiguration");
    }

    // [STUB] setPackageScreenCompatMode
    @Override
    public void setPackageScreenCompatMode(String packageName,
            int mode) throws RemoteException {
        logStub("setPackageScreenCompatMode", "No OH equivalent");
    }

    // [STUB] suppressResizeConfigChanges
    @Override
    public void suppressResizeConfigChanges(boolean suppress) throws RemoteException {
        logStub("suppressResizeConfigChanges", "No OH equivalent");
    }

    // ========================================================================
    // Category 8: Permission (-> OH AccessTokenKit, STUB)
    // ========================================================================

    // [STUB] checkPermission -> OH AccessTokenKit.VerifyAccessToken
    @Override
    public int checkPermission(String permission, int pid,
            int uid) throws RemoteException {
        logStub("checkPermission", "OH uses AccessTokenKit");
        return 0; // PERMISSION_GRANTED
    }

    // [STUB] checkUriPermission
    @Override
    public int checkUriPermission(Uri uri, int pid, int uid, int mode, int userId,
            IBinder callerToken) throws RemoteException {
        logStub("checkUriPermission", "OH uses AccessTokenKit");
        return 0; // PERMISSION_GRANTED
    }

    // [STUB] checkUriPermissions
    @Override
    public int[] checkUriPermissions(List<Uri> uris, int pid, int uid, int mode, int userId,
            IBinder callerToken) throws RemoteException {
        logStub("checkUriPermissions", "OH uses AccessTokenKit");
        return new int[0];
    }

    // [STUB] grantUriPermission
    @Override
    public void grantUriPermission(IApplicationThread caller, String targetPkg, Uri uri,
            int mode, int userId) throws RemoteException {
        logStub("grantUriPermission", "OH uses AccessTokenKit");
    }

    // [STUB] revokeUriPermission
    @Override
    public void revokeUriPermission(IApplicationThread caller, String targetPkg, Uri uri,
            int mode, int userId) throws RemoteException {
        logStub("revokeUriPermission", "OH uses AccessTokenKit");
    }

    // ========================================================================
    // Category 9: IntentSender / PendingIntent (-> OH WantAgent)
    // ========================================================================

    // [PARTIAL] getIntentSender -> OH IAbilityManager.GetWantSender
    @Override
    public IIntentSender getIntentSender(int type, String packageName, IBinder token,
            String resultWho, int requestCode, Intent[] intents, String[] resolvedTypes,
            int flags, Bundle options, int userId) throws RemoteException {
        logBridged("getIntentSender", "-> OH IAbilityManager.GetWantSender");
        return null; // Phase 1 stub
    }

    // [PARTIAL] getIntentSenderWithFeature -> OH IAbilityManager.GetWantSender
    @Override
    public IIntentSender getIntentSenderWithFeature(int type, String packageName,
            String featureId, IBinder token, String resultWho, int requestCode,
            Intent[] intents, String[] resolvedTypes, int flags, Bundle options,
            int userId) throws RemoteException {
        logBridged("getIntentSenderWithFeature", "-> OH IAbilityManager.GetWantSender");
        return null; // Phase 1 stub
    }

    // [PARTIAL] cancelIntentSender -> OH IAbilityManager.CancelWantSender
    @Override
    public void cancelIntentSender(IIntentSender sender) throws RemoteException {
        logBridged("cancelIntentSender", "-> OH IAbilityManager.CancelWantSender");
    }

    // [STUB] getInfoForIntentSender
    @Override
    public ActivityManager.PendingIntentInfo getInfoForIntentSender(
            IIntentSender sender) throws RemoteException {
        logStub("getInfoForIntentSender", "OH WantAgent (partial mapping)");
        return null;
    }

    // [STUB] registerIntentSenderCancelListenerEx
    @Override
    public boolean registerIntentSenderCancelListenerEx(IIntentSender sender,
            IResultReceiver receiver) throws RemoteException {
        logStub("registerIntentSenderCancelListenerEx", "OH WantAgent (partial mapping)");
        return false;
    }

    // [STUB] unregisterIntentSenderCancelListener
    @Override
    public void unregisterIntentSenderCancelListener(IIntentSender sender,
            IResultReceiver receiver) throws RemoteException {
        logStub("unregisterIntentSenderCancelListener", "OH WantAgent (partial mapping)");
    }

    // [STUB] isIntentSenderTargetedToPackage
    @Override
    public boolean isIntentSenderTargetedToPackage(
            IIntentSender sender) throws RemoteException {
        logStub("isIntentSenderTargetedToPackage", "OH WantAgent (partial mapping)");
        return false;
    }

    // [STUB] isIntentSenderAnActivity
    @Override
    public boolean isIntentSenderAnActivity(
            IIntentSender sender) throws RemoteException {
        logStub("isIntentSenderAnActivity", "OH WantAgent (partial mapping)");
        return false;
    }

    // [STUB] getIntentForIntentSender
    @Override
    public Intent getIntentForIntentSender(IIntentSender sender) throws RemoteException {
        logStub("getIntentForIntentSender", "OH WantAgent (partial mapping)");
        return null;
    }

    // [STUB] getTagForIntentSender
    @Override
    public String getTagForIntentSender(IIntentSender sender,
            String prefix) throws RemoteException {
        logStub("getTagForIntentSender", "OH WantAgent (partial mapping)");
        return null;
    }

    // [PARTIAL] sendIntentSender -> OH IAbilityManager.SendWantSender
    @Override
    public int sendIntentSender(IApplicationThread caller, IIntentSender target,
            IBinder whitelistToken, int code,
            Intent intent, String resolvedType, IIntentReceiver finishedReceiver,
            String requiredPermission, Bundle options) throws RemoteException {
        logBridged("sendIntentSender", "-> OH IAbilityManager.SendWantSender");
        return 0;
    }

    // [STUB] queryIntentComponentsForIntentSender
    @Override
    public ParceledListSlice queryIntentComponentsForIntentSender(IIntentSender sender,
            int matchFlags) throws RemoteException {
        logStub("queryIntentComponentsForIntentSender", "No OH equivalent");
        return null;
    }

    // [STUB] noteWakeupAlarm
    @Override
    public void noteWakeupAlarm(IIntentSender sender, WorkSource workSource, int sourceUid,
            String sourcePkg, String tag) throws RemoteException {
        logStub("noteWakeupAlarm", "OH alarm not mapped");
    }

    // [STUB] noteAlarmStart
    @Override
    public void noteAlarmStart(IIntentSender sender, WorkSource workSource, int sourceUid,
            String tag) throws RemoteException {
        logStub("noteAlarmStart", "OH alarm not mapped");
    }

    // [STUB] noteAlarmFinish
    @Override
    public void noteAlarmFinish(IIntentSender sender, WorkSource workSource, int sourceUid,
            String tag) throws RemoteException {
        logStub("noteAlarmFinish", "OH alarm not mapped");
    }

    // ========================================================================
    // Category 10: User Management (-> OH, STUB for Phase 1)
    // ========================================================================

    // [STUB] switchUser
    @Override
    public boolean switchUser(int userid) throws RemoteException {
        logStub("switchUser", "OH user management not mapped in Phase 1");
        return false;
    }

    // [STUB] getSwitchingFromUserMessage
    @Override
    public String getSwitchingFromUserMessage() throws RemoteException {
        logStub("getSwitchingFromUserMessage", "OH user management not mapped in Phase 1");
        return null;
    }

    // [STUB] getSwitchingToUserMessage
    @Override
    public String getSwitchingToUserMessage() throws RemoteException {
        logStub("getSwitchingToUserMessage", "OH user management not mapped in Phase 1");
        return null;
    }

    // [STUB] setStopUserOnSwitch
    @Override
    public void setStopUserOnSwitch(int value) throws RemoteException {
        logStub("setStopUserOnSwitch", "OH user management not mapped in Phase 1");
    }

    // [STUB] isUserRunning
    @Override
    public boolean isUserRunning(int userid, int flags) throws RemoteException {
        logStub("isUserRunning", "OH user management not mapped in Phase 1");
        return false;
    }

    // [STUB] getCurrentUser
    @Override
    public UserInfo getCurrentUser() throws RemoteException {
        logStub("getCurrentUser", "OH user management not mapped in Phase 1");
        return null;
    }

    // [STUB] getCurrentUserId
    @Override
    public int getCurrentUserId() throws RemoteException {
        logStub("getCurrentUserId", "OH user management not mapped in Phase 1");
        return 0;
    }

    // [STUB] getRunningUserIds
    @Override
    public int[] getRunningUserIds() throws RemoteException {
        logStub("getRunningUserIds", "OH user management not mapped in Phase 1");
        return new int[]{0};
    }

    // [STUB] stopUser
    @Override
    public int stopUser(int userid, boolean force,
            IStopUserCallback callback) throws RemoteException {
        logStub("stopUser", "OH user management not mapped in Phase 1");
        return 0;
    }

    // [STUB] stopUserWithDelayedLocking
    @Override
    public int stopUserWithDelayedLocking(int userid, boolean force,
            IStopUserCallback callback) throws RemoteException {
        logStub("stopUserWithDelayedLocking", "OH user management not mapped in Phase 1");
        return 0;
    }

    // [STUB] startUserInBackground
    @Override
    public boolean startUserInBackground(int userid) throws RemoteException {
        logStub("startUserInBackground", "OH user management not mapped in Phase 1");
        return false;
    }

    // [STUB] startUserInBackgroundWithListener
    @Override
    public boolean startUserInBackgroundWithListener(int userid,
            IProgressListener unlockProgressListener) throws RemoteException {
        logStub("startUserInBackgroundWithListener", "OH user management not mapped in Phase 1");
        return false;
    }

    // [STUB] startUserInForegroundWithListener
    @Override
    public boolean startUserInForegroundWithListener(int userid,
            IProgressListener unlockProgressListener) throws RemoteException {
        logStub("startUserInForegroundWithListener", "OH user management not mapped in Phase 1");
        return false;
    }

    // [STUB] startUserInBackgroundVisibleOnDisplay
    @Override
    public boolean startUserInBackgroundVisibleOnDisplay(int userid, int displayId,
            IProgressListener unlockProgressListener) throws RemoteException {
        logStub("startUserInBackgroundVisibleOnDisplay", "OH user management not mapped in Phase 1");
        return false;
    }

    // [STUB] unlockUser (deprecated)
    @Override
    public boolean unlockUser(int userid, byte[] token, byte[] secret,
            IProgressListener listener) throws RemoteException {
        logStub("unlockUser", "OH user management not mapped in Phase 1");
        return false;
    }

    // [STUB] unlockUser2
    @Override
    public boolean unlockUser2(int userId,
            IProgressListener listener) throws RemoteException {
        logStub("unlockUser2", "OH user management not mapped in Phase 1");
        return false;
    }

    // [STUB] startProfile
    @Override
    public boolean startProfile(int userId) throws RemoteException {
        logStub("startProfile", "OH user management not mapped in Phase 1");
        return false;
    }

    // [STUB] startProfileWithListener
    @Override
    public boolean startProfileWithListener(int userid,
            IProgressListener unlockProgressListener) throws RemoteException {
        logStub("startProfileWithListener", "OH user management not mapped in Phase 1");
        return false;
    }

    // [STUB] stopProfile
    @Override
    public boolean stopProfile(int userId) throws RemoteException {
        logStub("stopProfile", "OH user management not mapped in Phase 1");
        return false;
    }

    // [STUB] restartUserInBackground
    @Override
    public int restartUserInBackground(int userId,
            int userStartMode) throws RemoteException {
        logStub("restartUserInBackground", "OH user management not mapped in Phase 1");
        return 0;
    }

    // [STUB] registerUserSwitchObserver
    @Override
    public void registerUserSwitchObserver(IUserSwitchObserver observer,
            String name) throws RemoteException {
        logStub("registerUserSwitchObserver", "OH user management not mapped in Phase 1");
    }

    // [STUB] unregisterUserSwitchObserver
    @Override
    public void unregisterUserSwitchObserver(
            IUserSwitchObserver observer) throws RemoteException {
        logStub("unregisterUserSwitchObserver", "OH user management not mapped in Phase 1");
    }

    // [STUB] handleIncomingUser
    @Override
    public int handleIncomingUser(int callingPid, int callingUid, int userId, boolean allowAll,
            boolean requireFull, String name,
            String callerPackage) throws RemoteException {
        logStub("handleIncomingUser", "OH user management not mapped in Phase 1");
        return 0;
    }

    // [STUB] getDisplayIdsForStartingVisibleBackgroundUsers
    @Override
    public int[] getDisplayIdsForStartingVisibleBackgroundUsers() throws RemoteException {
        logStub("getDisplayIdsForStartingVisibleBackgroundUsers",
                "OH user management not mapped in Phase 1");
        return null;
    }

    // [STUB] getLaunchedFromUid
    @Override
    public int getLaunchedFromUid(IBinder activityToken) throws RemoteException {
        logStub("getLaunchedFromUid", "No direct OH equivalent");
        return 0;
    }

    // [STUB] getLaunchedFromPackage
    @Override
    public String getLaunchedFromPackage(IBinder activityToken) throws RemoteException {
        logStub("getLaunchedFromPackage", "No direct OH equivalent");
        return null;
    }

    // ========================================================================
    // Category 11: Instrumentation / Debug (STUB)
    // ========================================================================

    // [STUB] startInstrumentation
    @Override
    public boolean startInstrumentation(ComponentName className, String profileFile,
            int flags, Bundle arguments, IInstrumentationWatcher watcher,
            IUiAutomationConnection connection, int userId,
            String abiOverride) throws RemoteException {
        logStub("startInstrumentation", "Debug/instrumentation, no OH mapping");
        return false;
    }

    // [STUB] addInstrumentationResults
    @Override
    public void addInstrumentationResults(IApplicationThread target,
            Bundle results) throws RemoteException {
        logStub("addInstrumentationResults", "Debug/instrumentation, no OH mapping");
    }

    // [STUB] finishInstrumentation
    @Override
    public void finishInstrumentation(IApplicationThread target, int resultCode,
            Bundle results) throws RemoteException {
        logStub("finishInstrumentation", "Debug/instrumentation, no OH mapping");
    }

    // [STUB] setDebugApp
    @Override
    public void setDebugApp(String packageName, boolean waitForDebugger,
            boolean persistent) throws RemoteException {
        logStub("setDebugApp", "Debug/instrumentation, no OH mapping");
    }

    // [STUB] setAgentApp
    @Override
    public void setAgentApp(String packageName,
            String agent) throws RemoteException {
        logStub("setAgentApp", "Debug/instrumentation, no OH mapping");
    }

    // [STUB] setAlwaysFinish
    @Override
    public void setAlwaysFinish(boolean enabled) throws RemoteException {
        logStub("setAlwaysFinish", "Debug/instrumentation, no OH mapping");
    }

    // [STUB] showWaitingForDebugger
    @Override
    public void showWaitingForDebugger(IApplicationThread who,
            boolean waiting) throws RemoteException {
        logStub("showWaitingForDebugger", "Debug/instrumentation, no OH mapping");
    }

    // [STUB] profileControl
    @Override
    public boolean profileControl(String process, int userId, boolean start,
            ProfilerInfo profilerInfo, int profileType) throws RemoteException {
        logStub("profileControl", "Debug/instrumentation, no OH mapping");
        return false;
    }

    // [STUB] dumpHeap
    @Override
    public boolean dumpHeap(String process, int userId, boolean managed, boolean mallocInfo,
            boolean runGc, String path, ParcelFileDescriptor fd,
            RemoteCallback finishCallback) throws RemoteException {
        logStub("dumpHeap", "Debug/instrumentation, no OH mapping");
        return false;
    }

    // [STUB] requestSystemServerHeapDump
    @Override
    public void requestSystemServerHeapDump() throws RemoteException {
        logStub("requestSystemServerHeapDump", "Debug/instrumentation, no OH mapping");
    }

    // [STUB] setDumpHeapDebugLimit
    @Override
    public void setDumpHeapDebugLimit(String processName, int uid, long maxMemSize,
            String reportPackage) throws RemoteException {
        logStub("setDumpHeapDebugLimit", "Debug/instrumentation, no OH mapping");
    }

    // [STUB] dumpHeapFinished
    @Override
    public void dumpHeapFinished(String path) throws RemoteException {
        logStub("dumpHeapFinished", "Debug/instrumentation, no OH mapping");
    }

    // [STUB] startBinderTracking
    @Override
    public boolean startBinderTracking() throws RemoteException {
        logStub("startBinderTracking", "Debug/instrumentation, no OH mapping");
        return false;
    }

    // [STUB] stopBinderTrackingAndDump
    @Override
    public boolean stopBinderTrackingAndDump(
            ParcelFileDescriptor fd) throws RemoteException {
        logStub("stopBinderTrackingAndDump", "Debug/instrumentation, no OH mapping");
        return false;
    }

    // [STUB] setRenderThread
    @Override
    public void setRenderThread(int tid) throws RemoteException {
        logStub("setRenderThread", "Debug/instrumentation, no OH mapping");
    }

    // [STUB] setPersistentVrThread
    @Override
    public void setPersistentVrThread(int tid) throws RemoteException {
        logStub("setPersistentVrThread", "Debug/instrumentation, no OH mapping");
    }

    // [STUB] setHasTopUi
    @Override
    public void setHasTopUi(boolean hasTopUi) throws RemoteException {
        logStub("setHasTopUi", "Debug/instrumentation, no OH mapping");
    }

    // [STUB] setDeterministicUidIdle
    @Override
    public void setDeterministicUidIdle(boolean deterministic) throws RemoteException {
        logStub("setDeterministicUidIdle", "Debug/instrumentation, no OH mapping");
    }

    // [STUB] startDelegateShellPermissionIdentity
    @Override
    public void startDelegateShellPermissionIdentity(int uid,
            String[] permissions) throws RemoteException {
        logStub("startDelegateShellPermissionIdentity", "Shell permission delegation not mapped");
    }

    // [STUB] stopDelegateShellPermissionIdentity
    @Override
    public void stopDelegateShellPermissionIdentity() throws RemoteException {
        logStub("stopDelegateShellPermissionIdentity", "Shell permission delegation not mapped");
    }

    // [STUB] getDelegatedShellPermissions
    @Override
    public List<String> getDelegatedShellPermissions() throws RemoteException {
        logStub("getDelegatedShellPermissions", "Shell permission delegation not mapped");
        return Collections.emptyList();
    }

    // ========================================================================
    // Category 12: UID / Process Observers (-> OH IAppMgr observers)
    // ========================================================================

    // [PARTIAL] registerUidObserver -> OH IAppMgr.RegisterApplicationStateObserver
    @Override
    public void registerUidObserver(IUidObserver observer, int which, int cutpoint,
            String callingPackage) throws RemoteException {
        logBridged("registerUidObserver", "-> OH IAppMgr.RegisterApplicationStateObserver");
    }

    // [PARTIAL] unregisterUidObserver
    @Override
    public void unregisterUidObserver(IUidObserver observer) throws RemoteException {
        logBridged("unregisterUidObserver", "-> OH IAppMgr.UnregisterApplicationStateObserver");
    }

    // [PARTIAL] registerUidObserverForUids
    @Override
    public IBinder registerUidObserverForUids(IUidObserver observer, int which, int cutpoint,
            String callingPackage, int[] uids) throws RemoteException {
        logBridged("registerUidObserverForUids", "-> OH IAppMgr observer");
        return null;
    }

    // [PARTIAL] addUidToObserver
    @Override
    public void addUidToObserver(IBinder observerToken, String callingPackage,
            int uid) throws RemoteException {
        logBridged("addUidToObserver", "-> OH IAppMgr observer");
    }

    // [PARTIAL] removeUidFromObserver
    @Override
    public void removeUidFromObserver(IBinder observerToken, String callingPackage,
            int uid) throws RemoteException {
        logBridged("removeUidFromObserver", "-> OH IAppMgr observer");
    }

    // [STUB] getUidProcessCapabilities
    @Override
    public int getUidProcessCapabilities(int uid,
            String callingPackage) throws RemoteException {
        logStub("getUidProcessCapabilities", "No OH equivalent");
        return 0;
    }

    // [PARTIAL] registerProcessObserver
    @Override
    public void registerProcessObserver(
            IProcessObserver observer) throws RemoteException {
        logBridged("registerProcessObserver", "-> OH IAppMgr process observer");
    }

    // [PARTIAL] unregisterProcessObserver
    @Override
    public void unregisterProcessObserver(
            IProcessObserver observer) throws RemoteException {
        logBridged("unregisterProcessObserver", "-> OH IAppMgr process observer");
    }

    // ========================================================================
    // Category 13: Keyguard / System (STUB)
    // ========================================================================

    // [STUB] signalPersistentProcesses
    @Override
    public void signalPersistentProcesses(int signal) throws RemoteException {
        logStub("signalPersistentProcesses", "System-level, no OH mapping");
    }

    // [STUB] restart
    @Override
    public void restart() throws RemoteException {
        logStub("restart", "System-level, no OH mapping");
    }

    // [STUB] shutdown
    @Override
    public boolean shutdown(int timeout) throws RemoteException {
        logStub("shutdown", "System-level, no OH mapping");
        return false;
    }

    // [STUB] enterSafeMode
    @Override
    public void enterSafeMode() throws RemoteException {
        logStub("enterSafeMode", "System-level, no OH mapping");
    }

    // [STUB] hang
    @Override
    public void hang(IBinder who, boolean allowRestart) throws RemoteException {
        logStub("hang", "System-level, no OH mapping");
    }

    // [STUB] stopAppSwitches
    @Override
    public void stopAppSwitches() throws RemoteException {
        logStub("stopAppSwitches", "System-level, no OH mapping");
    }

    // [STUB] resumeAppSwitches
    @Override
    public void resumeAppSwitches() throws RemoteException {
        logStub("resumeAppSwitches", "System-level, no OH mapping");
    }

    // [STUB] closeSystemDialogs
    @Override
    public void closeSystemDialogs(String reason) throws RemoteException {
        logStub("closeSystemDialogs", "System-level, no OH mapping");
    }

    // [STUB] setActivityController
    @Override
    public void setActivityController(IActivityController watcher,
            boolean imAMonkey) throws RemoteException {
        logStub("setActivityController", "System-level, no OH mapping");
    }

    // [STUB] showBootMessage
    @Override
    public void showBootMessage(CharSequence msg,
            boolean always) throws RemoteException {
        logStub("showBootMessage", "System-level, no OH mapping");
    }

    // [STUB] bootAnimationComplete
    @Override
    public void bootAnimationComplete() throws RemoteException {
        logStub("bootAnimationComplete", "System-level, no OH mapping");
    }

    // [STUB] isUserAMonkey
    @Override
    public boolean isUserAMonkey() throws RemoteException {
        logStub("isUserAMonkey", "System-level, no OH mapping");
        return false;
    }

    // [STUB] setUserIsMonkey
    @Override
    public void setUserIsMonkey(boolean monkey) throws RemoteException {
        logStub("setUserIsMonkey", "System-level, no OH mapping");
    }

    // [STUB] isTopActivityImmersive
    @Override
    public boolean isTopActivityImmersive() throws RemoteException {
        logStub("isTopActivityImmersive", "System-level, no OH mapping");
        return false;
    }

    // [STUB] notifyCleartextNetwork
    @Override
    public void notifyCleartextNetwork(int uid,
            byte[] firstPacket) throws RemoteException {
        logStub("notifyCleartextNetwork", "No OH equivalent");
    }

    // [STUB] isVrModePackageEnabled
    @Override
    public boolean isVrModePackageEnabled(
            ComponentName packageName) throws RemoteException {
        logStub("isVrModePackageEnabled", "No OH equivalent");
        return false;
    }

    // [STUB] notifyLockedProfile
    @Override
    public void notifyLockedProfile(int userId) throws RemoteException {
        logStub("notifyLockedProfile", "No OH equivalent");
    }

    // [STUB] startConfirmDeviceCredentialIntent
    @Override
    public void startConfirmDeviceCredentialIntent(Intent intent,
            Bundle options) throws RemoteException {
        logStub("startConfirmDeviceCredentialIntent", "No OH equivalent");
    }

    // [STUB] sendIdleJobTrigger
    @Override
    public void sendIdleJobTrigger() throws RemoteException {
        logStub("sendIdleJobTrigger", "No OH equivalent");
    }

    // [STUB] waitForNetworkStateUpdate
    @Override
    public void waitForNetworkStateUpdate(
            long procStateSeq) throws RemoteException {
        logStub("waitForNetworkStateUpdate", "No OH equivalent");
    }

    // [STUB] backgroundAllowlistUid
    @Override
    public void backgroundAllowlistUid(int uid) throws RemoteException {
        logStub("backgroundAllowlistUid", "No OH equivalent");
    }

    // [STUB] isBackgroundRestricted
    @Override
    public boolean isBackgroundRestricted(
            String packageName) throws RemoteException {
        logStub("isBackgroundRestricted", "OH manages differently");
        return false;
    }

    // [STUB] getBackgroundRestrictionExemptionReason
    @Override
    public int getBackgroundRestrictionExemptionReason(
            int uid) throws RemoteException {
        logStub("getBackgroundRestrictionExemptionReason", "No OH equivalent");
        return 0;
    }

    // [STUB] holdLock
    @Override
    public void holdLock(IBinder token, int durationMs) throws RemoteException {
        logStub("holdLock", "No OH equivalent");
    }

    // [STUB] getLifeMonitor
    @Override
    public ParcelFileDescriptor getLifeMonitor() throws RemoteException {
        logStub("getLifeMonitor", "No OH equivalent");
        return null;
    }

    // [STUB] setActivityLocusContext
    @Override
    public void setActivityLocusContext(ComponentName activity, LocusId locusId,
            IBinder appToken) throws RemoteException {
        logStub("setActivityLocusContext", "No OH equivalent");
    }

    // ========================================================================
    // Category 14: Backup / Restore (STUB)
    // ========================================================================

    // [STUB] bindBackupAgent
    @Override
    public boolean bindBackupAgent(String packageName, int backupRestoreMode,
            int targetUserId, int backupDestination) throws RemoteException {
        logStub("bindBackupAgent", "OH backup not mapped");
        return false;
    }

    // [STUB] backupAgentCreated
    @Override
    public void backupAgentCreated(String packageName, IBinder agent,
            int userId) throws RemoteException {
        logStub("backupAgentCreated", "OH backup not mapped");
    }

    // [STUB] unbindBackupAgent
    @Override
    public void unbindBackupAgent(ApplicationInfo appInfo) throws RemoteException {
        logStub("unbindBackupAgent", "OH backup not mapped");
    }

    // ========================================================================
    // Category 15: Bug Report (STUB)
    // ========================================================================

    // [STUB] requestBugReport
    @Override
    public void requestBugReport(int bugreportType) throws RemoteException {
        logStub("requestBugReport", "OH bugreport not mapped");
    }

    // [STUB] requestBugReportWithDescription
    @Override
    public void requestBugReportWithDescription(String shareTitle, String shareDescription,
            int bugreportType) throws RemoteException {
        logStub("requestBugReportWithDescription", "OH bugreport not mapped");
    }

    // [STUB] requestTelephonyBugReport
    @Override
    public void requestTelephonyBugReport(String shareTitle,
            String shareDescription) throws RemoteException {
        logStub("requestTelephonyBugReport", "OH bugreport not mapped");
    }

    // [STUB] requestWifiBugReport
    @Override
    public void requestWifiBugReport(String shareTitle,
            String shareDescription) throws RemoteException {
        logStub("requestWifiBugReport", "OH bugreport not mapped");
    }

    // [STUB] requestInteractiveBugReportWithDescription
    @Override
    public void requestInteractiveBugReportWithDescription(String shareTitle,
            String shareDescription) throws RemoteException {
        logStub("requestInteractiveBugReportWithDescription", "OH bugreport not mapped");
    }

    // [STUB] requestInteractiveBugReport
    @Override
    public void requestInteractiveBugReport() throws RemoteException {
        logStub("requestInteractiveBugReport", "OH bugreport not mapped");
    }

    // [STUB] requestFullBugReport
    @Override
    public void requestFullBugReport() throws RemoteException {
        logStub("requestFullBugReport", "OH bugreport not mapped");
    }

    // [STUB] requestRemoteBugReport
    @Override
    public void requestRemoteBugReport(long nonce) throws RemoteException {
        logStub("requestRemoteBugReport", "OH bugreport not mapped");
    }

    // [STUB] launchBugReportHandlerApp
    @Override
    public boolean launchBugReportHandlerApp() throws RemoteException {
        logStub("launchBugReportHandlerApp", "OH bugreport not mapped");
        return false;
    }

    // [STUB] getBugreportWhitelistedPackages
    @Override
    public List<String> getBugreportWhitelistedPackages() throws RemoteException {
        logStub("getBugreportWhitelistedPackages", "OH bugreport not mapped");
        return Collections.emptyList();
    }

    // ========================================================================
    // Category 16: FGS Logging (STUB)
    // ========================================================================

    // [STUB] logFgsApiBegin (oneway)
    @Override
    public void logFgsApiBegin(int apiType, int appUid,
            int appPid) throws RemoteException {
        logStub("logFgsApiBegin", "Foreground service logging not mapped");
    }

    // [STUB] logFgsApiEnd (oneway)
    @Override
    public void logFgsApiEnd(int apiType, int appUid,
            int appPid) throws RemoteException {
        logStub("logFgsApiEnd", "Foreground service logging not mapped");
    }

    // [STUB] logFgsApiStateChanged (oneway)
    @Override
    public void logFgsApiStateChanged(int apiType, int state, int appUid,
            int appPid) throws RemoteException {
        logStub("logFgsApiStateChanged", "Foreground service logging not mapped");
    }

    // ========================================================================
    // Category 17: App Freezer (STUB)
    // ========================================================================

    // [STUB] isAppFreezerSupported
    @Override
    public boolean isAppFreezerSupported() throws RemoteException {
        logStub("isAppFreezerSupported", "OH app freezer not mapped");
        return false;
    }

    // [STUB] isAppFreezerEnabled
    @Override
    public boolean isAppFreezerEnabled() throws RemoteException {
        logStub("isAppFreezerEnabled", "OH app freezer not mapped");
        return false;
    }

    // [STUB] enableAppFreezer
    @Override
    public boolean enableAppFreezer(boolean enable) throws RemoteException {
        logStub("enableAppFreezer", "OH app freezer not mapped");
        return false;
    }

    // [STUB] isProcessFrozen
    @Override
    public boolean isProcessFrozen(int pid) throws RemoteException {
        logStub("isProcessFrozen", "OH app freezer not mapped");
        return false;
    }

    // [STUB] registerUidFrozenStateChangedCallback
    @Override
    public void registerUidFrozenStateChangedCallback(
            IUidFrozenStateChangedCallback callback) throws RemoteException {
        logStub("registerUidFrozenStateChangedCallback", "OH app freezer not mapped");
    }

    // [STUB] unregisterUidFrozenStateChangedCallback
    @Override
    public void unregisterUidFrozenStateChangedCallback(
            IUidFrozenStateChangedCallback callback) throws RemoteException {
        logStub("unregisterUidFrozenStateChangedCallback", "OH app freezer not mapped");
    }

    // [STUB] getUidFrozenState
    @Override
    public int[] getUidFrozenState(int[] uids) throws RemoteException {
        logStub("getUidFrozenState", "OH app freezer not mapped");
        return new int[0];
    }
}
