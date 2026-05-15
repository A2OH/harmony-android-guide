// SPDX-License-Identifier: Apache-2.0
//
// Westlake M4a -- WestlakeActivityManagerService
//
// Minimum-surface implementation of android.app.IActivityManager.Stub for
// the Westlake dalvikvm sandbox.  Implements the Tier-1 transactions a
// typical Android-app Activity launch needs (per M4_DISCOVERY.md sec 7
// and BINDER_PIVOT_MILESTONES.md M4a) and provides safe-default no-ops
// for the remaining ~250 abstract methods so the JVM can instantiate
// this class.
//
// Same-process Stub.asInterface elision:
//   When framework code does
//     IActivityManager am = IActivityManager.Stub.asInterface(
//         ServiceManager.getService("activity"));
//   the Stub looks up queryLocalInterface("android.app.IActivityManager")
//   on the IBinder, which returns THIS instance (because Stub() called
//   attachInterface(this, DESCRIPTOR)).  asInterface then returns the
//   raw object cast to IActivityManager -- no Parcel marshaling, no
//   onTransact dispatch, no kernel hop.  Methods are direct Java vtable
//   calls on this class.
//
// Compile-time vs runtime hierarchy:
//   Compile-time: extends shim's android.app.IActivityManager$Stub
//                 (abstract; ~8 declared abstract methods that we list
//                  in shim/java/android/app/IActivityManager.java).
//   Runtime:      extends framework.jar's
//                 android.app.IActivityManager$Stub (abstract; ~267
//                 abstract methods inherited from IActivityManager).
//   The runtime parent has many more abstract methods than the
//   compile-time parent.  To satisfy the JVM at `new
//   WestlakeActivityManagerService(...)` time, this class implements
//   every method of the Android 16 framework.jar IActivityManager.aidl
//   surface.  Most are safe-default no-ops; the Tier-1 set has real
//   behaviour at the top of the class.
//
// Constructor:
//   IActivityManager.Stub's deprecated no-arg constructor calls
//     PermissionEnforcer.fromContext(ActivityThread.currentActivityThread()
//                                         .getSystemContext())
//   which NPEs in the Westlake sandbox (ActivityThread is null).  We
//   bypass it by calling the alternate Stub(PermissionEnforcer)
//   constructor with a subclassed PermissionEnforcer whose protected
//   no-arg ctor sets mContext=null and returns.  No system services
//   are touched.
//
// Rationale (see docs/engine/M4_DISCOVERY.md sec 13.7):
//   noice's MainActivity launch (driven separately by M4-PRE3) will
//   eventually trigger framework lookups of these IActivityManager
//   methods.  Stubbing the Tier-1 set lets that path proceed; the rest
//   are no-ops so the JVM doesn't reject the class.  No per-app
//   branches: same shim works for noice, mock apps, future real APKs.
//
// Method count: 267 IActivityManager methods.  16 Tier-1 real impls,
//   ~251 fail-loud unobserved-method overrides.
//
// CR2 (2026-05-12): the safe-default no-ops were converted to throw
// UnsupportedOperationException via ServiceMethodMissing.fail("activity", ...)
// per codex review §2 Tier 2 #1.  Rationale (also AGENT_SWARM_PLAYBOOK.md
// §3.5 "Speculative completeness"):
//
//   The previous bodies silently returned 0/null/false/no-op for ~254
//   methods.  Any app/framework code that landed on one of those
//   methods got the wrong answer with no diagnostic.  By making
//   unobserved methods fail loud, the discovery harness now surfaces
//   such calls as obvious stack traces -- they become Tier-1
//   candidates whose semantics we then implement properly.
//
//   The 16 Tier-1 implementations at the top of the file remain real:
//   discovery observed those calls; they have real (or known-safe)
//   semantics for the Westlake sandbox.
//
// Reference AIDL (Android 16, framework.jar):
//   https://android.googlesource.com/platform/frameworks/base/+/refs/tags/android-16.0.0_r1/core/java/android/app/IActivityManager.aidl

package com.westlake.services;

import android.app.IActivityManager;
import android.app.IApplicationThread;
import android.app.IProcessObserver;
import android.app.IServiceConnection;
import android.app.ProfilerInfo;
import android.content.IIntentReceiver;
import android.content.IIntentSender;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Debug;
import android.os.IBinder;
import android.os.PermissionEnforcer;
import android.os.RemoteException;
import android.os.UserHandle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Minimum-surface IActivityManager implementation for the Westlake
 * sandbox.  Thirteen methods have real (Tier-1) behavior; the remaining
 * ~254 are safe no-ops.
 */
public final class WestlakeActivityManagerService extends IActivityManager.Stub {

    /** UserHandle.USER_SYSTEM (0).  We are a single-user sandbox. */
    private static final int USER_SYSTEM = 0;

    /** Tracks registered IProcessObservers so unregister is symmetric.
     *  We never call onForegroundActivitiesChanged / onProcessDied / etc.
     *  from the sandbox -- there is no real process scheduler. */
    private final Set<IProcessObserver> mProcessObservers =
            Collections.synchronizedSet(new HashSet<IProcessObserver>());

    /** Cached single-element list returned by getRunningAppProcesses().
     *  Built lazily so the class can be constructed without touching the
     *  android.app.ActivityManager.RunningAppProcessInfo class loader. */
    private volatile List mRunningProcessesCache;

    // ------------------------------------------------------------------
    // Construction
    // ------------------------------------------------------------------
    //
    // PermissionEnforcer subclass nested here so users don't need to
    // import it; protected constructor of PermissionEnforcer is
    // accessible to subclasses regardless of package.
    private static final class NoopPermissionEnforcer extends PermissionEnforcer {
        NoopPermissionEnforcer() { super(); }
    }

    public WestlakeActivityManagerService() {
        super(new NoopPermissionEnforcer());
    }

    // ------------------------------------------------------------------
    //   IMPLEMENTED (Tier-1) METHODS
    // ------------------------------------------------------------------

    /** Return USER_SYSTEM (0) -- single-user sandbox. */
    @Override
    public int getCurrentUserId() throws RemoteException {
        return USER_SYSTEM;
    }

    /** Single-element list with our process info.
     *  Reflection-built so this class can load without
     *  android.app.ActivityManager.RunningAppProcessInfo on the boot
     *  classpath at compile time.  At runtime the class is in
     *  framework.jar's bootclasspath; reflection succeeds. */
    @Override
    public java.util.List getRunningAppProcesses() throws RemoteException {
        List cached = mRunningProcessesCache;
        if (cached != null) return cached;
        try {
            Class<?> clazz = Class.forName("android.app.ActivityManager$RunningAppProcessInfo");
            Object info = clazz.getDeclaredConstructor().newInstance();
            // Set processName, pid, uid via field reflection so we don't
            // hard-bind to the @hide constructor signature.
            setField(clazz, info, "processName", "com.westlake.dalvikvm");
            setField(clazz, info, "pid", android.os.Process.myPid());
            setField(clazz, info, "uid", android.os.Process.myUid());
            // importance=FOREGROUND (100) so callers that check think we're alive.
            setField(clazz, info, "importance", 100);
            List<Object> list = new ArrayList<>();
            list.add(info);
            mRunningProcessesCache = list;
            return list;
        } catch (Throwable t) {
            // RunningAppProcessInfo unavailable; return empty list.
            return new ArrayList<>();
        }
    }

    private static void setField(Class<?> clazz, Object inst, String name, Object value) {
        try {
            java.lang.reflect.Field f = clazz.getField(name);
            f.set(inst, value);
        } catch (Throwable ignored) { /* field not present in this Android version */ }
    }

    // CR1-fix HIGH-3: setField variant that uses getDeclaredField + setAccessible
    // so we can populate private @hide fields of framework.jar classes.
    private static void trySetField(Class<?> clazz, Object inst, String name, Object value) {
        try {
            java.lang.reflect.Field f = clazz.getDeclaredField(name);
            f.setAccessible(true);
            f.set(inst, value);
        } catch (Throwable ignored) { /* field absent in this version */ }
    }

    /** Cached UserInfo for USER_SYSTEM; built once, reused on every
     *  getCurrentUser() call (CR1-fix HIGH-3). */
    private volatile android.content.pm.UserInfo mSystemUserInfo;

    /** Build a framework.jar android.content.pm.UserInfo for USER_SYSTEM
     *  (the only user in our single-user sandbox).  Reflection used because
     *  the compile-time shim UserInfo has only the no-arg ctor; the real
     *  framework.jar class has richer constructors. */
    private android.content.pm.UserInfo buildSystemUserInfo() {
        android.content.pm.UserInfo cached = mSystemUserInfo;
        if (cached != null) return cached;
        try {
            Class<?> cls = Class.forName("android.content.pm.UserInfo");
            // FLAG_PRIMARY = 0x00000001 (AOSP-stable).
            final int flagPrimary = 0x00000001;
            android.content.pm.UserInfo info;
            try {
                // Preferred: (int id, String name, int flags) -- stable
                // across Android 10..16.
                java.lang.reflect.Constructor<?> ctor3 =
                        cls.getDeclaredConstructor(int.class, String.class, int.class);
                ctor3.setAccessible(true);
                info = (android.content.pm.UserInfo) ctor3.newInstance(
                        USER_SYSTEM, "Owner", flagPrimary);
            } catch (NoSuchMethodException nsme) {
                java.lang.reflect.Constructor<?> ctor0 = cls.getDeclaredConstructor();
                ctor0.setAccessible(true);
                info = (android.content.pm.UserInfo) ctor0.newInstance();
                trySetField(cls, info, "id", USER_SYSTEM);
                trySetField(cls, info, "name", "Owner");
                trySetField(cls, info, "flags", flagPrimary);
            }
            // Populate richer fields whether ctor variant did or not.
            trySetField(cls, info, "userType", "android.os.usertype.full.SYSTEM");
            trySetField(cls, info, "serialNumber", 0);
            trySetField(cls, info, "profileGroupId", USER_SYSTEM);
            mSystemUserInfo = info;
            return info;
        } catch (Throwable t) {
            // Fallback path: at minimum return a non-null UserInfo with id=0
            // so callers don't NPE.
            try {
                Class<?> cls = Class.forName("android.content.pm.UserInfo");
                java.lang.reflect.Constructor<?> ctor = cls.getDeclaredConstructor();
                ctor.setAccessible(true);
                android.content.pm.UserInfo info =
                        (android.content.pm.UserInfo) ctor.newInstance();
                trySetField(cls, info, "id", USER_SYSTEM);
                mSystemUserInfo = info;
                return info;
            } catch (Throwable t2) {
                return null;
            }
        }
    }

    /** Track the observer; we never dispatch process events. */
    @Override
    public void registerProcessObserver(IProcessObserver observer) throws RemoteException {
        if (observer != null) mProcessObservers.add(observer);
    }

    @Override
    public void unregisterProcessObserver(IProcessObserver observer) throws RemoteException {
        if (observer != null) mProcessObservers.remove(observer);
    }

    /** Apps using PendingIntent.getCreatorPackage / getIntent may probe
     *  this for null-safety.  Return null is the safe default. */
    @Override
    public Intent getIntentForIntentSender(IIntentSender sender) throws RemoteException {
        return null;
    }

    /** Return empty list -- there are no other tasks in the sandbox.
     *  The AIDL name is `getTasks(int)`; framework code that calls
     *  `getRunningTasks(int)` (older API surface) routes here in
     *  Android 16+. */
    @Override
    public java.util.List getTasks(int maxNum) throws RemoteException {
        return new ArrayList<>();
    }

    /** Return per-pid Debug.MemoryInfo array (zero-filled).  AOSP
     *  ActivityManager.getProcessMemoryInfo expects an array sized to
     *  match pids.length; we honor that. */
    @Override
    public Debug.MemoryInfo[] getProcessMemoryInfo(int[] pids) throws RemoteException {
        if (pids == null) return new Debug.MemoryInfo[0];
        Debug.MemoryInfo[] result = new Debug.MemoryInfo[pids.length];
        for (int i = 0; i < pids.length; i++) {
            result[i] = new Debug.MemoryInfo();
        }
        return result;
    }

    /** No real Activity launches in the sandbox.  Return BAD_VALUE (-1).
     *  M4-PRE3 / discovery agents drive launches via local
     *  Activity.attach(); they don't go through IActivityManager. */
    public int startActivity(IApplicationThread caller, String callingPackage,
            Intent intent, String resolvedType, IBinder resultTo, String resultWho,
            int requestCode, int flags, ProfilerInfo profilerInfo, Bundle options)
            throws RemoteException {
        return -1; // ActivityManager.START_BAD_VALUE
    }

    public int startActivityWithFeature(IApplicationThread caller, String callingPackage,
            String callingFeatureId, Intent intent, String resolvedType, IBinder resultTo,
            String resultWho, int requestCode, int flags, ProfilerInfo profilerInfo,
            Bundle options) throws RemoteException {
        return -1; // ActivityManager.START_BAD_VALUE
    }

    /** App process bind prologue.  In real Android the system_server
     *  calls this back into the app's IApplicationThread to deliver
     *  config, ActivityInfo, etc.  In the sandbox we don't have a
     *  system_server -- M4-PRE3's mini-ActivityThread drives this
     *  directly.  We accept the call so framework code path proceeds. */
    public void attachApplication(IApplicationThread app, long startSeq) throws RemoteException {
        // no-op: M4-PRE3 drives bindApplication directly on the
        // ApplicationThread, bypassing the system_server callback path.
    }

    /** Service bind: return 0 (refused, but no exception).  Real bind
     *  semantics belong to the dispatcher milestone after M4a. */
    public int bindService(IApplicationThread caller, IBinder token, Intent service,
            String resolvedType, IServiceConnection connection, long flags,
            String callingPackage, int userId) throws RemoteException {
        return 0;
    }

    /** Android 16 replacement for bindIsolatedService. */
    public int bindServiceInstance(IApplicationThread caller, IBinder token, Intent service,
            String resolvedType, IServiceConnection connection, long flags,
            String instanceName, String callingPackage, int userId) throws RemoteException {
        return 0;
    }

    @Override
    public boolean unbindService(IServiceConnection connection) throws RemoteException {
        return true; // pretend unbind succeeded
    }

    /** Broadcasts: no-op, return 0 (BROADCAST_SUCCESS).  noice's
     *  AlarmInitReceiver registration goes through here; we ignore it. */
    public int broadcastIntentWithFeature(IApplicationThread caller, String callingFeatureId,
            Intent intent, String resolvedType, IIntentReceiver resultTo, int resultCode,
            String resultData, Bundle map, String[] requiredPermissions,
            String[] excludedPermissions, String[] excludedPackages, int appOp,
            Bundle options, boolean serialized, boolean sticky, int userId)
            throws RemoteException {
        return 0;
    }

    /** Receiver registration: return null (no sticky broadcast).  Hilt
     *  and AndroidX call this during DI bootstrap; we acknowledge. */
    public Intent registerReceiverWithFeature(IApplicationThread caller, String callerPackage,
            String callingFeatureId, String receiverId, IIntentReceiver receiver,
            IntentFilter filter, String requiredPermission, int userId, int flags)
            throws RemoteException {
        return null;
    }

    public void unregisterReceiver(IIntentReceiver receiver) throws RemoteException {
        // no-op
    }

    // ------------------------------------------------------------------
    //  FAIL-LOUD UNOBSERVED METHODS (CR2 / codex Tier 2 #1)
    //
    //  Every remaining abstract IActivityManager method has a concrete
    //  override here that throws ServiceMethodMissing.fail("activity", ...).
    //  Rationale: see file-header CR2 block.  The throws are unchecked
    //  UnsupportedOperationException; callers see a stack trace pointing
    //  at the call site, which is the discovery signal we want.
    //
    //  How to promote a method to Tier-1: delete the throw body here and
    //  add a real implementation in the IMPLEMENTED block above.  Update
    //  ActivityServiceTest to exercise the new path.
    //
    //  Cross-reference against the AIDL (Android 16 / 267 methods):
    //  https://android.googlesource.com/platform/frameworks/base/+/refs/tags/android-16.0.0_r1/core/java/android/app/IActivityManager.aidl
    // ------------------------------------------------------------------


    public void addApplicationStartInfoCompleteListener(android.app.IApplicationStartInfoCompleteListener p0, int p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "addApplicationStartInfoCompleteListener"); }
    public void addInstrumentationResults(android.app.IApplicationThread p0, android.os.Bundle p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "addInstrumentationResults"); }
    public void addOverridePermissionState(int p0, int p1, java.lang.String p2, int p3) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "addOverridePermissionState"); }
    public void addPackageDependency(java.lang.String p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "addPackageDependency"); }
    public void addStartInfoTimestamp(int p0, long p1, int p2) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "addStartInfoTimestamp"); }
    public void addUidToObserver(android.os.IBinder p0, java.lang.String p1, int p2) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "addUidToObserver"); }
    public void appNotResponding(java.lang.String p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "appNotResponding"); }
    public void appNotRespondingViaProvider(android.os.IBinder p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "appNotRespondingViaProvider"); }
    public void backgroundAllowlistUid(int p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "backgroundAllowlistUid"); }
    public void backupAgentCreated(java.lang.String p0, android.os.IBinder p1, int p2) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "backupAgentCreated"); }
    public boolean bindBackupAgent(java.lang.String p0, int p1, int p2, int p3, boolean p4) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "bindBackupAgent"); }
    public void bootAnimationComplete() throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "bootAnimationComplete"); }
    public int broadcastIntent(android.app.IApplicationThread p0, android.content.Intent p1, java.lang.String p2, android.content.IIntentReceiver p3, int p4, java.lang.String p5, android.os.Bundle p6, java.lang.String[] p7, int p8, android.os.Bundle p9, boolean p10, boolean p11, int p12) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "broadcastIntent"); }
    public void cancelIntentSender(android.content.IIntentSender p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "cancelIntentSender"); }
    public void cancelTaskWindowTransition(int p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "cancelTaskWindowTransition"); }
    public int checkContentUriPermissionFull(android.net.Uri p0, int p1, int p2, int p3, int p4) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "checkContentUriPermissionFull"); }
    public int checkPermission(java.lang.String p0, int p1, int p2) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "checkPermission"); }
    public int checkPermissionForDevice(java.lang.String p0, int p1, int p2, int p3) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "checkPermissionForDevice"); }
    public int checkUriPermission(android.net.Uri p0, int p1, int p2, int p3, int p4, android.os.IBinder p5) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "checkUriPermission"); }
    public int[] checkUriPermissions(java.util.List p0, int p1, int p2, int p3, int p4, android.os.IBinder p5) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "checkUriPermissions"); }
    public void clearAllOverridePermissionStates(int p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "clearAllOverridePermissionStates"); }
    public boolean clearApplicationUserData(java.lang.String p0, boolean p1, android.content.pm.IPackageDataObserver p2, int p3) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "clearApplicationUserData"); }
    public void clearOverridePermissionStates(int p0, int p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "clearOverridePermissionStates"); }
    public void closeSystemDialogs(java.lang.String p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "closeSystemDialogs"); }
    public void crashApplicationWithType(int p0, int p1, java.lang.String p2, int p3, java.lang.String p4, boolean p5, int p6) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "crashApplicationWithType"); }
    public void crashApplicationWithTypeWithExtras(int p0, int p1, java.lang.String p2, int p3, java.lang.String p4, boolean p5, int p6, android.os.Bundle p7) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "crashApplicationWithTypeWithExtras"); }
    public boolean dumpHeap(java.lang.String p0, int p1, boolean p2, boolean p3, boolean p4, java.lang.String p5, java.lang.String p6, android.os.ParcelFileDescriptor p7, android.os.RemoteCallback p8) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "dumpHeap"); }
    public void dumpHeapFinished(java.lang.String p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "dumpHeapFinished"); }
    public boolean enableAppFreezer(boolean p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "enableAppFreezer"); }
    public boolean enableFgsNotificationRateLimit(boolean p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "enableFgsNotificationRateLimit"); }
    public void enterSafeMode() throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "enterSafeMode"); }
    public boolean finishActivity(android.os.IBinder p0, int p1, android.content.Intent p2, int p3) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "finishActivity"); }
    public void finishAttachApplication(long p0, long p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "finishAttachApplication"); }
    public void finishHeavyWeightApp() throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "finishHeavyWeightApp"); }
    public void finishInstrumentation(android.app.IApplicationThread p0, int p1, android.os.Bundle p2) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "finishInstrumentation"); }
    public void finishReceiver(android.os.IBinder p0, int p1, java.lang.String p2, android.os.Bundle p3, boolean p4, int p5) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "finishReceiver"); }
    public void forceDelayBroadcastDelivery(java.lang.String p0, long p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "forceDelayBroadcastDelivery"); }
    public void forceStopPackage(java.lang.String p0, int p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "forceStopPackage"); }
    public void forceStopPackageEvenWhenStopping(java.lang.String p0, int p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "forceStopPackageEvenWhenStopping"); }
    public void frozenBinderTransactionDetected(int p0, int p1, int p2, int p3) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "frozenBinderTransactionDetected"); }
    public java.util.List getAllRootTaskInfos() throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "getAllRootTaskInfos"); }
    public int getBackgroundRestrictionExemptionReason(int p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "getBackgroundRestrictionExemptionReason"); }
    public int getBindingUidProcessState(int p0, java.lang.String p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "getBindingUidProcessState"); }
    public java.util.List getBugreportWhitelistedPackages() throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "getBugreportWhitelistedPackages"); }
    public android.content.res.Configuration getConfiguration() throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "getConfiguration"); }
    public android.app.ContentProviderHolder getContentProvider(android.app.IApplicationThread p0, java.lang.String p1, java.lang.String p2, int p3, boolean p4) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "getContentProvider"); }
    public android.app.ContentProviderHolder getContentProviderExternal(java.lang.String p0, int p1, android.os.IBinder p2, java.lang.String p3) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "getContentProviderExternal"); }
    // CR1-fix: codex Tier 1 HIGH-3 -- getCurrentUser was a no-op returning
    // null even though M4 discovery flags it as a Tier-1 method (callers
    // that store the result in a UserInfo field will NPE).  Build a USER_SYSTEM
    // UserInfo reflectively so this class compiles against the compile-time
    // stub (shim/java/android/content/pm/UserInfo.java has only the no-arg
    // ctor) but produces the real framework.jar UserInfo at runtime (the
    // shim is stripped by scripts/framework_duplicates.txt).
    //
    // CR2 note: this method is a Tier-1 real impl (CR1's upgrade); it does
    // NOT throw ServiceMethodMissing.fail.  Do not "downgrade" it to a
    // throw -- CR1 promoted it because discovery proved it needed.
    public android.content.pm.UserInfo getCurrentUser() throws android.os.RemoteException {
        return buildSystemUserInfo();
    }
    public java.util.List getDelegatedShellPermissions() throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "getDelegatedShellPermissions"); }
    public int[] getDisplayIdsForStartingVisibleBackgroundUsers() throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "getDisplayIdsForStartingVisibleBackgroundUsers"); }
    public android.app.ActivityTaskManager.RootTaskInfo getFocusedRootTaskInfo() throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "getFocusedRootTaskInfo"); }
    public int getForegroundServiceType(android.content.ComponentName p0, android.os.IBinder p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "getForegroundServiceType"); }
    public android.content.pm.ParceledListSlice getHistoricalProcessExitReasons(java.lang.String p0, int p1, int p2, int p3) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "getHistoricalProcessExitReasons"); }
    public android.content.pm.ParceledListSlice getHistoricalProcessStartReasons(java.lang.String p0, int p1, int p2) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "getHistoricalProcessStartReasons"); }
    public android.app.ActivityManager.PendingIntentInfo getInfoForIntentSender(android.content.IIntentSender p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "getInfoForIntentSender"); }
    public android.content.IIntentSender getIntentSender(int p0, java.lang.String p1, android.os.IBinder p2, java.lang.String p3, int p4, android.content.Intent[] p5, java.lang.String[] p6, int p7, android.os.Bundle p8, int p9) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "getIntentSender"); }
    public android.content.IIntentSender getIntentSenderWithFeature(int p0, java.lang.String p1, java.lang.String p2, android.os.IBinder p3, java.lang.String p4, int p5, android.content.Intent[] p6, java.lang.String[] p7, int p8, android.os.Bundle p9, int p10) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "getIntentSenderWithFeature"); }
    public java.lang.String getLaunchedFromPackage(android.os.IBinder p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "getLaunchedFromPackage"); }
    public int getLaunchedFromUid(android.os.IBinder p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "getLaunchedFromUid"); }
    public android.os.ParcelFileDescriptor getLifeMonitor() throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "getLifeMonitor"); }
    public int getLockTaskModeState() throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "getLockTaskModeState"); }
    public void getMemoryInfo(android.app.ActivityManager.MemoryInfo p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "getMemoryInfo"); }
    public int getMemoryTrimLevel() throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "getMemoryTrimLevel"); }
    public void getMimeTypeFilterAsync(android.net.Uri p0, int p1, android.os.RemoteCallback p2) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "getMimeTypeFilterAsync"); }
    // CR1-fix: codex Tier 1 HIGH-3 -- getMyMemoryState was a no-op.  Real
    // callers (e.g. ActivityManager.getMyMemoryState) pass in a freshly
    // allocated RunningAppProcessInfo and expect it filled in with this
    // process's pid/uid/importance.  An empty struct passed back leaks the
    // zero-init defaults (pid=0, importance=0) which downstream callers
    // misinterpret as "process is gone".
    //
    // CR2 note: this method is a Tier-1 real impl (CR1's upgrade); it does
    // NOT throw ServiceMethodMissing.fail.  Do not "downgrade" it to a
    // throw -- CR1 promoted it because discovery proved it needed.
    public void getMyMemoryState(android.app.ActivityManager.RunningAppProcessInfo p0) throws android.os.RemoteException {
        if (p0 == null) return;
        // Use reflection -- the compile-time shim RunningAppProcessInfo has
        // fields, but the runtime class is from framework.jar (the shim is
        // stripped if listed; in any case the fields are public on both).
        try {
            Class<?> cls = p0.getClass();
            trySetField(cls, p0, "processName", "com.westlake.dalvikvm");
            trySetField(cls, p0, "pid", android.os.Process.myPid());
            trySetField(cls, p0, "uid", android.os.Process.myUid());
            // IMPORTANCE_FOREGROUND = 100 (stable AOSP constant since L).
            trySetField(cls, p0, "importance", 100);
            trySetField(cls, p0, "lru", 0);
            trySetField(cls, p0, "lastTrimLevel", 0);
            // REASON_UNKNOWN = 0.
            trySetField(cls, p0, "importanceReasonCode", 0);
            trySetField(cls, p0, "importanceReasonPid", 0);
        } catch (Throwable ignored) {
            // RunningAppProcessInfo unavailable / fields removed; caller's
            // outState stays as it was.
        }
    }
    public int getPackageProcessState(java.lang.String p0, java.lang.String p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "getPackageProcessState"); }
    public int getProcessLimit() throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "getProcessLimit"); }
    public long[] getProcessPss(int[] p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "getProcessPss"); }
    public java.util.List getProcessesInErrorState() throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "getProcessesInErrorState"); }
    public android.content.pm.ParceledListSlice getRecentTasks(int p0, int p1, int p2) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "getRecentTasks"); }
    public java.util.List getRegisteredIntentFilters(android.content.IIntentReceiver p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "getRegisteredIntentFilters"); }
    public java.util.List getRunningExternalApplications() throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "getRunningExternalApplications"); }
    public android.app.PendingIntent getRunningServiceControlPanel(android.content.ComponentName p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "getRunningServiceControlPanel"); }
    public int[] getRunningUserIds() throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "getRunningUserIds"); }
    public java.util.List getServices(int p0, int p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "getServices"); }
    public java.lang.String getSwitchingFromUserMessage() throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "getSwitchingFromUserMessage"); }
    public java.lang.String getSwitchingToUserMessage() throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "getSwitchingToUserMessage"); }
    public java.lang.String getTagForIntentSender(android.content.IIntentSender p0, java.lang.String p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "getTagForIntentSender"); }
    public android.graphics.Rect getTaskBounds(int p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "getTaskBounds"); }
    public int getTaskForActivity(android.os.IBinder p0, boolean p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "getTaskForActivity"); }
    public int[] getUidFrozenState(int[] p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "getUidFrozenState"); }
    public long getUidLastIdleElapsedTime(int p0, java.lang.String p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "getUidLastIdleElapsedTime"); }
    public int getUidProcessCapabilities(int p0, java.lang.String p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "getUidProcessCapabilities"); }
    public int getUidProcessState(int p0, java.lang.String p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "getUidProcessState"); }
    public void grantUriPermission(android.app.IApplicationThread p0, java.lang.String p1, android.net.Uri p2, int p3, int p4) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "grantUriPermission"); }
    public void handleApplicationCrash(android.os.IBinder p0, android.app.ApplicationErrorReport.ParcelableCrashInfo p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "handleApplicationCrash"); }
    public void handleApplicationStrictModeViolation(android.os.IBinder p0, int p1, android.os.StrictMode.ViolationInfo p2) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "handleApplicationStrictModeViolation"); }
    public boolean handleApplicationWtf(android.os.IBinder p0, java.lang.String p1, boolean p2, android.app.ApplicationErrorReport.ParcelableCrashInfo p3, int p4) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "handleApplicationWtf"); }
    public int handleIncomingUser(int p0, int p1, int p2, boolean p3, boolean p4, java.lang.String p5, java.lang.String p6) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "handleIncomingUser"); }
    public void hang(android.os.IBinder p0, boolean p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "hang"); }
    public boolean hasServiceTimeLimitExceeded(android.content.ComponentName p0, android.os.IBinder p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "hasServiceTimeLimitExceeded"); }
    public void holdLock(android.os.IBinder p0, int p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "holdLock"); }
    public boolean isAppFreezerEnabled() throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "isAppFreezerEnabled"); }
    public boolean isAppFreezerSupported() throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "isAppFreezerSupported"); }
    public boolean isBackgroundRestricted(java.lang.String p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "isBackgroundRestricted"); }
    public boolean isInLockTaskMode() throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "isInLockTaskMode"); }
    public boolean isIntentSenderAnActivity(android.content.IIntentSender p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "isIntentSenderAnActivity"); }
    public boolean isIntentSenderTargetedToPackage(android.content.IIntentSender p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "isIntentSenderTargetedToPackage"); }
    public boolean isProcessFrozen(int p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "isProcessFrozen"); }
    public boolean isTopActivityImmersive() throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "isTopActivityImmersive"); }
    public boolean isTopOfTask(android.os.IBinder p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "isTopOfTask"); }
    public boolean isUidActive(int p0, java.lang.String p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "isUidActive"); }
    public boolean isUserAMonkey() throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "isUserAMonkey"); }
    public boolean isUserRunning(int p0, int p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "isUserRunning"); }
    public boolean isVrModePackageEnabled(android.content.ComponentName p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "isVrModePackageEnabled"); }
    public void killAllBackgroundProcesses() throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "killAllBackgroundProcesses"); }
    public void killApplication(java.lang.String p0, int p1, int p2, java.lang.String p3, int p4) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "killApplication"); }
    public void killApplicationProcess(java.lang.String p0, int p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "killApplicationProcess"); }
    public void killBackgroundProcesses(java.lang.String p0, int p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "killBackgroundProcesses"); }
    public void killPackageDependents(java.lang.String p0, int p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "killPackageDependents"); }
    public boolean killPids(int[] p0, java.lang.String p1, boolean p2) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "killPids"); }
    public boolean killProcessesBelowForeground(java.lang.String p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "killProcessesBelowForeground"); }
    public void killProcessesWhenImperceptible(int[] p0, java.lang.String p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "killProcessesWhenImperceptible"); }
    public void killUid(int p0, int p1, java.lang.String p2) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "killUid"); }
    public void killUidForPermissionChange(int p0, int p1, java.lang.String p2) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "killUidForPermissionChange"); }
    public boolean launchBugReportHandlerApp() throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "launchBugReportHandlerApp"); }
    public void logFgsApiBegin(int p0, int p1, int p2) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "logFgsApiBegin"); }
    public void logFgsApiEnd(int p0, int p1, int p2) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "logFgsApiEnd"); }
    public void logFgsApiStateChanged(int p0, int p1, int p2, int p3) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "logFgsApiStateChanged"); }
    public void makePackageIdle(java.lang.String p0, int p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "makePackageIdle"); }
    public boolean moveActivityTaskToBack(android.os.IBinder p0, boolean p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "moveActivityTaskToBack"); }
    public void moveTaskToFront(android.app.IApplicationThread p0, java.lang.String p1, int p2, int p3, android.os.Bundle p4) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "moveTaskToFront"); }
    public void moveTaskToRootTask(int p0, int p1, boolean p2) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "moveTaskToRootTask"); }
    public void noteAlarmFinish(android.content.IIntentSender p0, android.os.WorkSource p1, int p2, java.lang.String p3) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "noteAlarmFinish"); }
    public void noteAlarmStart(android.content.IIntentSender p0, android.os.WorkSource p1, int p2, java.lang.String p3) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "noteAlarmStart"); }
    public void noteAppRestrictionEnabled(java.lang.String p0, int p1, int p2, boolean p3, int p4, java.lang.String p5, int p6, long p7) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "noteAppRestrictionEnabled"); }
    public void noteWakeupAlarm(android.content.IIntentSender p0, android.os.WorkSource p1, int p2, java.lang.String p3, java.lang.String p4) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "noteWakeupAlarm"); }
    public void notifyCleartextNetwork(int p0, byte[] p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "notifyCleartextNetwork"); }
    public void notifyLockedProfile(int p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "notifyLockedProfile"); }
    public android.os.ParcelFileDescriptor openContentUri(java.lang.String p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "openContentUri"); }
    public android.os.IBinder peekService(android.content.Intent p0, java.lang.String p1, java.lang.String p2) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "peekService"); }
    public void performIdleMaintenance() throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "performIdleMaintenance"); }
    public boolean profileControl(java.lang.String p0, int p1, boolean p2, android.app.ProfilerInfo p3, int p4) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "profileControl"); }
    public void publishContentProviders(android.app.IApplicationThread p0, java.util.List p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "publishContentProviders"); }
    public void publishService(android.os.IBinder p0, android.content.Intent p1, android.os.IBinder p2) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "publishService"); }
    public android.content.pm.ParceledListSlice queryIntentComponentsForIntentSender(android.content.IIntentSender p0, int p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "queryIntentComponentsForIntentSender"); }
    public boolean refContentProvider(android.os.IBinder p0, int p1, int p2) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "refContentProvider"); }
    public android.os.IBinder refreshIntentCreatorToken(android.content.Intent p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "refreshIntentCreatorToken"); }
    public boolean registerForegroundServiceObserver(android.app.IForegroundServiceObserver p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "registerForegroundServiceObserver"); }
    public boolean registerIntentSenderCancelListenerEx(android.content.IIntentSender p0, com.android.internal.os.IResultReceiver p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "registerIntentSenderCancelListenerEx"); }
    public android.content.Intent registerReceiver(android.app.IApplicationThread p0, java.lang.String p1, android.content.IIntentReceiver p2, android.content.IntentFilter p3, java.lang.String p4, int p5, int p6) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "registerReceiver"); }
    public void registerStrictModeCallback(android.os.IBinder p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "registerStrictModeCallback"); }
    public void registerTaskStackListener(android.app.ITaskStackListener p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "registerTaskStackListener"); }
    public void registerUidFrozenStateChangedCallback(android.app.IUidFrozenStateChangedCallback p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "registerUidFrozenStateChangedCallback"); }
    public void registerUidObserver(android.app.IUidObserver p0, int p1, int p2, java.lang.String p3) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "registerUidObserver"); }
    public android.os.IBinder registerUidObserverForUids(android.app.IUidObserver p0, int p1, int p2, java.lang.String p3, int[] p4) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "registerUidObserverForUids"); }
    public void registerUserSwitchObserver(android.app.IUserSwitchObserver p0, java.lang.String p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "registerUserSwitchObserver"); }
    public void removeApplicationStartInfoCompleteListener(android.app.IApplicationStartInfoCompleteListener p0, int p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "removeApplicationStartInfoCompleteListener"); }
    public void removeContentProvider(android.os.IBinder p0, boolean p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "removeContentProvider"); }
    public void removeContentProviderExternal(java.lang.String p0, android.os.IBinder p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "removeContentProviderExternal"); }
    public void removeContentProviderExternalAsUser(java.lang.String p0, android.os.IBinder p1, int p2) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "removeContentProviderExternalAsUser"); }
    public void removeOverridePermissionState(int p0, int p1, java.lang.String p2) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "removeOverridePermissionState"); }
    public boolean removeTask(int p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "removeTask"); }
    public void removeUidFromObserver(android.os.IBinder p0, java.lang.String p1, int p2) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "removeUidFromObserver"); }
    public void reportStartInfoViewTimestamps(long p0, long p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "reportStartInfoViewTimestamps"); }
    public void requestBugReport(int p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "requestBugReport"); }
    public void requestBugReportWithDescription(java.lang.String p0, java.lang.String p1, int p2) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "requestBugReportWithDescription"); }
    public void requestBugReportWithExtraAttachments(java.util.List p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "requestBugReportWithExtraAttachments"); }
    public void requestFullBugReport() throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "requestFullBugReport"); }
    public void requestInteractiveBugReport() throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "requestInteractiveBugReport"); }
    public void requestInteractiveBugReportWithDescription(java.lang.String p0, java.lang.String p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "requestInteractiveBugReportWithDescription"); }
    public void requestRemoteBugReport(long p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "requestRemoteBugReport"); }
    public void requestSystemServerHeapDump() throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "requestSystemServerHeapDump"); }
    public void requestTelephonyBugReport(java.lang.String p0, java.lang.String p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "requestTelephonyBugReport"); }
    public void requestWifiBugReport(java.lang.String p0, java.lang.String p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "requestWifiBugReport"); }
    public void resetAppErrors() throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "resetAppErrors"); }
    public void resizeTask(int p0, android.graphics.Rect p1, int p2) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "resizeTask"); }
    public void restart() throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "restart"); }
    public int restartUserInBackground(int p0, int p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "restartUserInBackground"); }
    public void resumeAppSwitches() throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "resumeAppSwitches"); }
    public void revokeUriPermission(android.app.IApplicationThread p0, java.lang.String p1, android.net.Uri p2, int p3, int p4) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "revokeUriPermission"); }
    public void scheduleApplicationInfoChanged(java.util.List p0, int p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "scheduleApplicationInfoChanged"); }
    public void sendIdleJobTrigger() throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "sendIdleJobTrigger"); }
    public int sendIntentSender(android.app.IApplicationThread p0, android.content.IIntentSender p1, android.os.IBinder p2, int p3, android.content.Intent p4, java.lang.String p5, android.content.IIntentReceiver p6, java.lang.String p7, android.os.Bundle p8) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "sendIntentSender"); }
    public void serviceDoneExecuting(android.os.IBinder p0, int p1, int p2, int p3, android.content.Intent p4) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "serviceDoneExecuting"); }
    public void setActivityController(android.app.IActivityController p0, boolean p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "setActivityController"); }
    public void setActivityLocusContext(android.content.ComponentName p0, android.content.LocusId p1, android.os.IBinder p2) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "setActivityLocusContext"); }
    public void setAgentApp(java.lang.String p0, java.lang.String p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "setAgentApp"); }
    public void setAlwaysFinish(boolean p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "setAlwaysFinish"); }
    public void setDebugApp(java.lang.String p0, boolean p1, boolean p2) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "setDebugApp"); }
    public void setDeterministicUidIdle(boolean p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "setDeterministicUidIdle"); }
    public void setDumpHeapDebugLimit(java.lang.String p0, int p1, long p2, java.lang.String p3) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "setDumpHeapDebugLimit"); }
    public void setFocusedRootTask(int p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "setFocusedRootTask"); }
    public void setHasTopUi(boolean p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "setHasTopUi"); }
    public void setPackageScreenCompatMode(java.lang.String p0, int p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "setPackageScreenCompatMode"); }
    public void setPersistentVrThread(int p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "setPersistentVrThread"); }
    public void setProcessImportant(android.os.IBinder p0, int p1, boolean p2, java.lang.String p3) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "setProcessImportant"); }
    public void setProcessLimit(int p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "setProcessLimit"); }
    public boolean setProcessMemoryTrimLevel(java.lang.String p0, int p1, int p2) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "setProcessMemoryTrimLevel"); }
    public void setProcessStateSummary(byte[] p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "setProcessStateSummary"); }
    public void setRenderThread(int p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "setRenderThread"); }
    public void setRequestedOrientation(android.os.IBinder p0, int p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "setRequestedOrientation"); }
    public void setServiceForeground(android.content.ComponentName p0, android.os.IBinder p1, int p2, android.app.Notification p3, int p4, int p5) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "setServiceForeground"); }
    public void setStopUserOnSwitch(int p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "setStopUserOnSwitch"); }
    public void setTaskResizeable(int p0, int p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "setTaskResizeable"); }
    public void setThemeOverlayReady(int p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "setThemeOverlayReady"); }
    public void setUserIsMonkey(boolean p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "setUserIsMonkey"); }
    public boolean shouldServiceTimeOut(android.content.ComponentName p0, android.os.IBinder p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "shouldServiceTimeOut"); }
    public void showBootMessage(java.lang.CharSequence p0, boolean p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "showBootMessage"); }
    public void showWaitingForDebugger(android.app.IApplicationThread p0, boolean p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "showWaitingForDebugger"); }
    public boolean shutdown(int p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "shutdown"); }
    public void signalPersistentProcesses(int p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "signalPersistentProcesses"); }
    public int startActivityAsUser(android.app.IApplicationThread p0, java.lang.String p1, android.content.Intent p2, java.lang.String p3, android.os.IBinder p4, java.lang.String p5, int p6, int p7, android.app.ProfilerInfo p8, android.os.Bundle p9, int p10) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "startActivityAsUser"); }
    public int startActivityAsUserWithFeature(android.app.IApplicationThread p0, java.lang.String p1, java.lang.String p2, android.content.Intent p3, java.lang.String p4, android.os.IBinder p5, java.lang.String p6, int p7, int p8, android.app.ProfilerInfo p9, android.os.Bundle p10, int p11) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "startActivityAsUserWithFeature"); }
    public int startActivityFromRecents(int p0, android.os.Bundle p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "startActivityFromRecents"); }
    public boolean startBinderTracking() throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "startBinderTracking"); }
    public void startConfirmDeviceCredentialIntent(android.content.Intent p0, android.os.Bundle p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "startConfirmDeviceCredentialIntent"); }
    public void startDelegateShellPermissionIdentity(int p0, java.lang.String[] p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "startDelegateShellPermissionIdentity"); }
    public boolean startInstrumentation(android.content.ComponentName p0, java.lang.String p1, int p2, android.os.Bundle p3, android.app.IInstrumentationWatcher p4, android.app.IUiAutomationConnection p5, int p6, java.lang.String p7) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "startInstrumentation"); }
    public boolean startProfile(int p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "startProfile"); }
    public boolean startProfileWithListener(int p0, android.os.IProgressListener p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "startProfileWithListener"); }
    public android.content.ComponentName startService(android.app.IApplicationThread p0, android.content.Intent p1, java.lang.String p2, boolean p3, java.lang.String p4, java.lang.String p5, int p6) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "startService"); }
    public void startSystemLockTaskMode(int p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "startSystemLockTaskMode"); }
    public boolean startUserInBackground(int p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "startUserInBackground"); }
    public boolean startUserInBackgroundVisibleOnDisplay(int p0, int p1, android.os.IProgressListener p2) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "startUserInBackgroundVisibleOnDisplay"); }
    public boolean startUserInBackgroundWithListener(int p0, android.os.IProgressListener p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "startUserInBackgroundWithListener"); }
    public boolean startUserInForegroundWithListener(int p0, android.os.IProgressListener p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "startUserInForegroundWithListener"); }
    public void stopAppForUser(java.lang.String p0, int p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "stopAppForUser"); }
    public void stopAppSwitches() throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "stopAppSwitches"); }
    public boolean stopBinderTrackingAndDump(android.os.ParcelFileDescriptor p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "stopBinderTrackingAndDump"); }
    public void stopDelegateShellPermissionIdentity() throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "stopDelegateShellPermissionIdentity"); }
    public boolean stopProfile(int p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "stopProfile"); }
    public int stopService(android.app.IApplicationThread p0, android.content.Intent p1, java.lang.String p2, int p3) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "stopService"); }
    public boolean stopServiceToken(android.content.ComponentName p0, android.os.IBinder p1, int p2) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "stopServiceToken"); }
    public int stopUser(int p0, boolean p1, android.app.IStopUserCallback p2) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "stopUser"); }
    public int stopUserExceptCertainProfiles(int p0, boolean p1, android.app.IStopUserCallback p2) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "stopUserExceptCertainProfiles"); }
    public int stopUserWithCallback(int p0, android.app.IStopUserCallback p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "stopUserWithCallback"); }
    public int stopUserWithDelayedLocking(int p0, android.app.IStopUserCallback p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "stopUserWithDelayedLocking"); }
    public void suppressResizeConfigChanges(boolean p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "suppressResizeConfigChanges"); }
    public boolean switchUser(int p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "switchUser"); }
    public void unbindBackupAgent(android.content.pm.ApplicationInfo p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "unbindBackupAgent"); }
    public void unbindFinished(android.os.IBinder p0, android.content.Intent p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "unbindFinished"); }
    public void unbroadcastIntent(android.app.IApplicationThread p0, android.content.Intent p1, int p2) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "unbroadcastIntent"); }
    public void unhandledBack() throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "unhandledBack"); }
    public boolean unlockUser(int p0, byte[] p1, byte[] p2, android.os.IProgressListener p3) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "unlockUser"); }
    public boolean unlockUser2(int p0, android.os.IProgressListener p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "unlockUser2"); }
    public void unregisterIntentSenderCancelListener(android.content.IIntentSender p0, com.android.internal.os.IResultReceiver p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "unregisterIntentSenderCancelListener"); }
    public void unregisterTaskStackListener(android.app.ITaskStackListener p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "unregisterTaskStackListener"); }
    public void unregisterUidFrozenStateChangedCallback(android.app.IUidFrozenStateChangedCallback p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "unregisterUidFrozenStateChangedCallback"); }
    public void unregisterUidObserver(android.app.IUidObserver p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "unregisterUidObserver"); }
    public void unregisterUserSwitchObserver(android.app.IUserSwitchObserver p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "unregisterUserSwitchObserver"); }
    public void unstableProviderDied(android.os.IBinder p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "unstableProviderDied"); }
    public boolean updateConfiguration(android.content.res.Configuration p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "updateConfiguration"); }
    public void updateLockTaskPackages(int p0, java.lang.String[] p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "updateLockTaskPackages"); }
    public boolean updateMccMncConfiguration(java.lang.String p0, java.lang.String p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "updateMccMncConfiguration"); }
    public void updatePersistentConfiguration(android.content.res.Configuration p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "updatePersistentConfiguration"); }
    public void updatePersistentConfigurationWithAttribution(android.content.res.Configuration p0, java.lang.String p1, java.lang.String p2) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "updatePersistentConfigurationWithAttribution"); }
    public void updateServiceGroup(android.app.IServiceConnection p0, int p1, int p2) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "updateServiceGroup"); }
    public void waitForBroadcastBarrier() throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "waitForBroadcastBarrier"); }
    public void waitForBroadcastIdle() throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "waitForBroadcastIdle"); }
    public void waitForNetworkStateUpdate(long p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("activity", "waitForNetworkStateUpdate"); }
}
