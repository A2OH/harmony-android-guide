// SPDX-License-Identifier: Apache-2.0
//
// Westlake M4c -- WestlakePackageManagerService
//
// Minimum-surface implementation of
// android.content.pm.IPackageManager.Stub for the Westlake dalvikvm
// sandbox.  15 Tier-1 real impls + 208 fail-loud unobserved-method
// overrides (per codex CR2 / ServiceMethodMissing.fail pattern).
//
// Distinct from M4-PRE5's WestlakePackageManagerStub (which extends
// android.content.pm.PackageManager and is returned by
// WestlakeContextImpl.getPackageManager()).  This M4c class is the
// IBinder-backed service registered as "package" in ServiceManager so
// callers using `ServiceManager.getService("package")` (Hilt-internal
// PackageManager lookups, AppOpsManager binder probes, etc.) get a real
// IPackageManager.Stub.
//
// Same-process Stub.asInterface elision:
//   When framework code does
//     IPackageManager pm = IPackageManager.Stub.asInterface(
//         ServiceManager.getService("package"));
//   the Stub looks up queryLocalInterface(IPackageManager.DESCRIPTOR)
//   on the IBinder, which returns THIS instance (because Stub() called
//   attachInterface(this, DESCRIPTOR)).  asInterface then returns the
//   raw object cast to IPackageManager -- no Parcel marshaling, no
//   onTransact dispatch.
//
// Compile-time vs runtime hierarchy:
//   Compile-time: extends shim's android.content.pm.IPackageManager$Stub
//                 (abstract; 223 abstract methods declared in
//                  shim/java/android/content/pm/IPackageManager.java).
//   Runtime:      extends framework.jar's
//                 android.content.pm.IPackageManager$Stub.  Shim Stub is
//                 stripped from aosp-shim.dex via
//                 scripts/framework_duplicates.txt so the real Stub wins.
//
// Construction:
//   IPackageManager.Stub's deprecated no-arg constructor calls
//     PermissionEnforcer.fromContext(ActivityThread.currentActivityThread()
//                                         .getSystemContext())
//   which NPEs in the Westlake sandbox (ActivityThread is null).  We
//   bypass it by calling the alternate Stub(PermissionEnforcer)
//   constructor with a subclassed PermissionEnforcer whose protected
//   no-arg ctor sets mContext=null and returns.  Same pattern as M4a/M4b/
//   M4-power.
//
// Rationale (see docs/engine/M4_DISCOVERY.md sec 7):
//   Hilt-using apps probe "package" service via ServiceManager.getService
//   for binder-side AppLocalesMetadataHolderService inspection, content
//   provider authority resolution, and permission checks during DI
//   bootstrap.  Without an IPackageManager.Stub registered under
//   "package", ServiceManager.getService returns null and downstream code
//   NPEs.  M4c registers a minimum-surface service that:
//     - reports our package's PackageInfo / ApplicationInfo
//     - returns single-element lists from getInstalledPackages /
//       getInstalledApplications
//     - returns null for resolveIntent / resolveService / *Info-by-component
//       (no other installed components exist in the sandbox)
//     - reports `false` for every hasSystemFeature probe
//     - throws ServiceMethodMissing.fail for everything else
//
//   This matches the architectural constraint that the Westlake sandbox
//   is the SOLE app on the system -- there is one package (the host),
//   one ApplicationInfo, one PackageInfo.
//
// Method count: 223 IPackageManager methods.  Tier-1 (real, 15):
//   getPackageInfo, getApplicationInfo, getInstalledPackages,
//   getInstalledApplications, resolveIntent, resolveService,
//   hasSystemFeature, getNameForUid, getPackagesForUid,
//   getInstallerPackageName, getServiceInfo, getActivityInfo,
//   getReceiverInfo, getProviderInfo, queryContentProviders.
//   Remaining 208 fail loud.
//
// Per CR2: every remaining abstract method throws
// UnsupportedOperationException via ServiceMethodMissing.fail("package", ...)
// so unobserved transactions surface as obvious stack traces during M4
// discovery rather than masquerading as "success".
//
// Reference: android.content.pm.IPackageManager class on Android 16
// framework.jar (dexdump from the deployed phone, 2026-05-12; 223
// abstract methods).

package com.westlake.services;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageManager;
import android.content.pm.PackageInfo;
import android.content.pm.ParceledListSlice;
import android.content.pm.ProviderInfo;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.os.IBinder;
import android.os.PermissionEnforcer;
import android.os.Process;
import android.os.RemoteException;

import java.util.ArrayList;
import java.util.List;

public final class WestlakePackageManagerService extends IPackageManager.Stub {

    /** Our (single, sandbox-owned) package name.  Used by every Tier-1
     *  method to (a) decide whether the requested PackageInfo / lookup
     *  matches us, and (b) populate returned info fields.
     *
     *  Looked up from the same System property M4-PRE5's
     *  WestlakePackageManagerStub uses ({@code westlake.apk.package}),
     *  with a sensible default for the synthetic smoke test. */
    private final String mPackageName;

    /** Cached ApplicationInfo for our package; built lazily on first
     *  request.  Reused across all Tier-1 methods so the returned
     *  ServiceInfo / ActivityInfo / etc. all carry a CONSISTENT
     *  ApplicationInfo reference (some framework callers do reference
     *  equality on info.applicationInfo). */
    private volatile ApplicationInfo mApplicationInfo;

    /** Cached PackageInfo for our package; same lazy-build rationale. */
    private volatile PackageInfo mPackageInfo;

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

    public WestlakePackageManagerService() {
        // Bypass the deprecated no-arg constructor that NPEs in the
        // sandbox (ActivityThread.getSystemContext() returns null); use
        // the Stub(PermissionEnforcer) overload with a no-op enforcer.
        // Base Stub still calls attachInterface(this, DESCRIPTOR), so
        // queryLocalInterface("android.content.pm.IPackageManager") returns this.
        super(new NoopPermissionEnforcer());
        String pkg = null;
        try {
            pkg = System.getProperty("westlake.apk.package");
        } catch (Throwable ignored) {}
        if (pkg == null || pkg.isEmpty()) {
            pkg = "com.westlake.host";
        }
        mPackageName = pkg;
    }

    // ------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------

    /** Build (and cache) our ApplicationInfo.  Reflection NOT required
     *  -- ApplicationInfo has a public no-arg ctor and public fields. */
    private ApplicationInfo applicationInfo() {
        ApplicationInfo cached = mApplicationInfo;
        if (cached != null) return cached;
        synchronized (this) {
            if (mApplicationInfo != null) return mApplicationInfo;
            ApplicationInfo info = new ApplicationInfo();
            info.packageName = mPackageName;
            info.processName = mPackageName;
            info.uid = Process.myUid();
            // dataDir set to a path inside Westlake's deploy dir so apps
            // probing getDataDir / dataDir don't crash when constructing
            // File objects from it.
            info.dataDir = "/data/local/tmp/westlake/" + mPackageName;
            // Best-effort APK path.  M4-PRE5's stub reads
            // westlake.apk.sourceDir; mirror that.
            try {
                String src = System.getProperty("westlake.apk.sourceDir");
                if (src != null) {
                    info.sourceDir = src;
                    info.publicSourceDir = src;
                }
            } catch (Throwable ignored) {}
            mApplicationInfo = info;
            return info;
        }
    }

    /** Build (and cache) our PackageInfo.  Pre-populates the
     *  applicationInfo cross-reference so callers that walk through it
     *  observe the same ApplicationInfo instance. */
    private PackageInfo packageInfo() {
        PackageInfo cached = mPackageInfo;
        if (cached != null) return cached;
        synchronized (this) {
            if (mPackageInfo != null) return mPackageInfo;
            PackageInfo pi = new PackageInfo();
            pi.packageName = mPackageName;
            pi.applicationInfo = applicationInfo();
            // versionName / versionCode default to "1.0" / 1 -- sufficient
            // for callers that only sanity-check non-null.
            try {
                pi.versionName = "1.0";
            } catch (Throwable ignored) {}
            mPackageInfo = pi;
            return pi;
        }
    }

    /** True if the requested package name matches our package. */
    private boolean matchesOurPackage(String pkg) {
        return pkg != null && pkg.equals(mPackageName);
    }

    /** Build a ParceledListSlice wrapping the given list using whatever
     *  ctor framework.jar's ParceledListSlice exposes.  Reflection
     *  because the shim's compile-time ParceledListSlice has only a
     *  no-arg ctor (the real one takes a List).  If construction fails
     *  for any reason, return a new no-arg ParceledListSlice (the
     *  compile-time stub variant) -- some callers tolerate that, others
     *  will fail loud later, which is the correct discovery signal. */
    private static ParceledListSlice asSlice(List<?> list) {
        try {
            Class<?> cls = Class.forName("android.content.pm.ParceledListSlice");
            try {
                java.lang.reflect.Constructor<?> ctor =
                        cls.getDeclaredConstructor(List.class);
                ctor.setAccessible(true);
                Object inst = ctor.newInstance(list);
                if (inst instanceof ParceledListSlice) {
                    return (ParceledListSlice) inst;
                }
            } catch (NoSuchMethodException nsme) {
                // try the empty() factory next
            }
            // Try ParceledListSlice.emptyList() / .empty() factory.
            try {
                java.lang.reflect.Method m = cls.getDeclaredMethod("emptyList");
                m.setAccessible(true);
                Object inst = m.invoke(null);
                if (inst instanceof ParceledListSlice) {
                    return (ParceledListSlice) inst;
                }
            } catch (NoSuchMethodException nsme) {
                // fall through
            }
        } catch (Throwable ignored) {}
        return new ParceledListSlice();
    }

    // ------------------------------------------------------------------
    //   IMPLEMENTED (Tier-1) METHODS
    // ------------------------------------------------------------------

    /** Return a populated PackageInfo if the requested package is ours;
     *  otherwise null (matching the AOSP no-package-found null path). */
    @Override
    public PackageInfo getPackageInfo(java.lang.String p0, long p1, int p2)
            throws android.os.RemoteException {
        if (matchesOurPackage(p0)) {
            return packageInfo();
        }
        return null;
    }

    /** Return our cached ApplicationInfo if the requested package is
     *  ours; otherwise null. */
    @Override
    public ApplicationInfo getApplicationInfo(java.lang.String p0, long p1, int p2)
            throws android.os.RemoteException {
        if (matchesOurPackage(p0)) {
            return applicationInfo();
        }
        return null;
    }

    /** Single-element installed-packages list (our package). */
    @Override
    public ParceledListSlice getInstalledPackages(long p0, int p1)
            throws android.os.RemoteException {
        List<PackageInfo> list = new ArrayList<>(1);
        list.add(packageInfo());
        return asSlice(list);
    }

    /** Single-element installed-applications list (our package). */
    @Override
    public ParceledListSlice getInstalledApplications(long p0, int p1)
            throws android.os.RemoteException {
        List<ApplicationInfo> list = new ArrayList<>(1);
        list.add(applicationInfo());
        return asSlice(list);
    }

    /** No other activities outside our package exist; AOSP-canonical
     *  null when nothing resolves. */
    @Override
    public ResolveInfo resolveIntent(android.content.Intent p0, java.lang.String p1, long p2, int p3)
            throws android.os.RemoteException {
        return null;
    }

    /** Same: no services resolve outside our package. */
    @Override
    public ResolveInfo resolveService(android.content.Intent p0, java.lang.String p1, long p2, int p3)
            throws android.os.RemoteException {
        return null;
    }

    /** No system features are available in the sandbox.  We deliberately
     *  return false for everything so apps that probe for OpenGL,
     *  PRINT_SERVICE, NFC, etc. take the no-hardware code path.  Apps
     *  that genuinely require a feature will surface a discovery hit
     *  (their code will probably crash or no-op gracefully), which is
     *  the right signal. */
    @Override
    public boolean hasSystemFeature(java.lang.String p0, int p1)
            throws android.os.RemoteException {
        return false;
    }

    /** Map every Westlake-sandbox UID to our package name.  Real Android
     *  returns different names for sharedUserIds, system_server, etc.;
     *  the sandbox only has one app process. */
    @Override
    public java.lang.String getNameForUid(int p0) throws android.os.RemoteException {
        return mPackageName;
    }

    /** Return our package name in a single-element array for every UID
     *  query (same single-process rationale as getNameForUid). */
    @Override
    public java.lang.String[] getPackagesForUid(int p0) throws android.os.RemoteException {
        return new java.lang.String[] { mPackageName };
    }

    /** Return null -- we have no installer-side metadata (sideloaded /
     *  dev-deployed app). */
    @Override
    public java.lang.String getInstallerPackageName(java.lang.String p0)
            throws android.os.RemoteException {
        return null;
    }

    /** Synthetic ServiceInfo for a component in our package.  Mirrors
     *  WestlakePackageManagerStub.getServiceInfo behaviour: we always
     *  return a populated info (with our ApplicationInfo back-ref) when
     *  the package matches, so Hilt-generated SyntheticEntryPoint
     *  service lookups proceed.  Foreign-package queries return null. */
    @Override
    public ServiceInfo getServiceInfo(android.content.ComponentName p0, long p1, int p2)
            throws android.os.RemoteException {
        if (p0 == null) return null;
        if (!matchesOurPackage(p0.getPackageName())) return null;
        ServiceInfo info = new ServiceInfo();
        info.packageName = mPackageName;
        info.name = p0.getClassName();
        info.applicationInfo = applicationInfo();
        try { info.enabled = true; } catch (Throwable ignored) {}
        try { info.exported = false; } catch (Throwable ignored) {}
        try { info.processName = mPackageName; } catch (Throwable ignored) {}
        return info;
    }

    /** Synthetic ActivityInfo for a component in our package.  Same
     *  contract as getServiceInfo. */
    @Override
    public ActivityInfo getActivityInfo(android.content.ComponentName p0, long p1, int p2)
            throws android.os.RemoteException {
        if (p0 == null) return null;
        if (!matchesOurPackage(p0.getPackageName())) return null;
        ActivityInfo info = new ActivityInfo();
        info.packageName = mPackageName;
        info.name = p0.getClassName();
        info.applicationInfo = applicationInfo();
        try { info.enabled = true; } catch (Throwable ignored) {}
        try { info.exported = true; } catch (Throwable ignored) {}
        try { info.processName = mPackageName; } catch (Throwable ignored) {}
        return info;
    }

    /** Synthetic ActivityInfo for a receiver in our package.  Note: real
     *  framework.jar's IPackageManager returns ActivityInfo (not
     *  ReceiverInfo) for receivers -- they share the same data class. */
    @Override
    public ActivityInfo getReceiverInfo(android.content.ComponentName p0, long p1, int p2)
            throws android.os.RemoteException {
        if (p0 == null) return null;
        if (!matchesOurPackage(p0.getPackageName())) return null;
        ActivityInfo info = new ActivityInfo();
        info.packageName = mPackageName;
        info.name = p0.getClassName();
        info.applicationInfo = applicationInfo();
        try { info.enabled = true; } catch (Throwable ignored) {}
        try { info.exported = true; } catch (Throwable ignored) {}
        try { info.processName = mPackageName; } catch (Throwable ignored) {}
        return info;
    }

    /** No content providers in the sandbox -- return null. */
    @Override
    public ProviderInfo getProviderInfo(android.content.ComponentName p0, long p1, int p2)
            throws android.os.RemoteException {
        return null;
    }

    /** Empty content-providers list.  Hilt's authority-resolution path
     *  enumerates all providers; we have none. */
    @Override
    public ParceledListSlice queryContentProviders(java.lang.String p0, int p1, long p2, java.lang.String p3)
            throws android.os.RemoteException {
        return asSlice(new ArrayList<Object>());
    }

    // ------------------------------------------------------------------
    //   FAIL-LOUD UNOBSERVED METHODS (CR2 / codex Tier 2 #2)
    //
    //   How to promote a method to Tier-1: delete the throw body here and
    //   add a real implementation in the IMPLEMENTED block above.  Update
    //   PackageServiceTest to exercise the new path.
    // ------------------------------------------------------------------

    @Override public boolean activitySupportsIntentAsUser(android.content.ComponentName p0, android.content.Intent p1, java.lang.String p2, int p3) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "activitySupportsIntentAsUser"); }
    @Override public void addCrossProfileIntentFilter(android.content.IntentFilter p0, java.lang.String p1, int p2, int p3, int p4) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "addCrossProfileIntentFilter"); }
    @Override public boolean addPermission(android.content.pm.PermissionInfo p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "addPermission"); }
    @Override public boolean addPermissionAsync(android.content.pm.PermissionInfo p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "addPermissionAsync"); }
    @Override public void addPersistentPreferredActivity(android.content.IntentFilter p0, android.content.ComponentName p1, int p2) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "addPersistentPreferredActivity"); }
    @Override public void addPreferredActivity(android.content.IntentFilter p0, int p1, android.content.ComponentName[] p2, android.content.ComponentName p3, int p4, boolean p5) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "addPreferredActivity"); }
    @Override public boolean canForwardTo(android.content.Intent p0, java.lang.String p1, int p2, int p3) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "canForwardTo"); }
    @Override public boolean[] canPackageQuery(java.lang.String p0, java.lang.String[] p1, int p2) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "canPackageQuery"); }
    @Override public boolean canRequestPackageInstalls(java.lang.String p0, int p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "canRequestPackageInstalls"); }
    @Override public java.lang.String[] canonicalToCurrentPackageNames(java.lang.String[] p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "canonicalToCurrentPackageNames"); }
    @Override public void checkPackageStartable(java.lang.String p0, int p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "checkPackageStartable"); }
    @Override public int checkPermission(java.lang.String p0, java.lang.String p1, int p2) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "checkPermission"); }
    @Override public int checkSignatures(java.lang.String p0, java.lang.String p1, int p2) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "checkSignatures"); }
    @Override public int checkUidPermission(java.lang.String p0, int p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "checkUidPermission"); }
    @Override public int checkUidSignatures(int p0, int p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "checkUidSignatures"); }
    @Override public void clearApplicationProfileData(java.lang.String p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "clearApplicationProfileData"); }
    @Override public void clearApplicationUserData(java.lang.String p0, android.content.pm.IPackageDataObserver p1, int p2) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "clearApplicationUserData"); }
    @Override public void clearCrossProfileIntentFilters(int p0, java.lang.String p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "clearCrossProfileIntentFilters"); }
    @Override public void clearPackagePersistentPreferredActivities(java.lang.String p0, int p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "clearPackagePersistentPreferredActivities"); }
    @Override public void clearPackagePreferredActivities(java.lang.String p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "clearPackagePreferredActivities"); }
    @Override public void clearPersistentPreferredActivity(android.content.IntentFilter p0, int p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "clearPersistentPreferredActivity"); }
    @Override public java.lang.String[] currentToCanonicalPackageNames(java.lang.String[] p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "currentToCanonicalPackageNames"); }
    @Override public void deleteApplicationCacheFiles(java.lang.String p0, android.content.pm.IPackageDataObserver p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "deleteApplicationCacheFiles"); }
    @Override public void deleteApplicationCacheFilesAsUser(java.lang.String p0, int p1, android.content.pm.IPackageDataObserver p2) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "deleteApplicationCacheFilesAsUser"); }
    @Override public void deleteExistingPackageAsUser(android.content.pm.VersionedPackage p0, android.content.pm.IPackageDeleteObserver2 p1, int p2) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "deleteExistingPackageAsUser"); }
    @Override public void deletePackageAsUser(java.lang.String p0, int p1, android.content.pm.IPackageDeleteObserver p2, int p3, int p4) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "deletePackageAsUser"); }
    @Override public void deletePackageVersioned(android.content.pm.VersionedPackage p0, android.content.pm.IPackageDeleteObserver2 p1, int p2, int p3) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "deletePackageVersioned"); }
    @Override public void deletePreloadsFileCache() throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "deletePreloadsFileCache"); }
    @Override public void enterSafeMode() throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "enterSafeMode"); }
    @Override public void extendVerificationTimeout(int p0, int p1, long p2) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "extendVerificationTimeout"); }
    @Override public android.content.pm.ResolveInfo findPersistentPreferredActivity(android.content.Intent p0, int p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "findPersistentPreferredActivity"); }
    @Override public void finishPackageInstall(int p0, boolean p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "finishPackageInstall"); }
    @Override public void flushPackageRestrictionsAsUser(int p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "flushPackageRestrictionsAsUser"); }
    @Override public void freeStorage(java.lang.String p0, long p1, int p2, android.content.IntentSender p3) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "freeStorage"); }
    @Override public void freeStorageAndNotify(java.lang.String p0, long p1, int p2, android.content.pm.IPackageDataObserver p3) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "freeStorageAndNotify"); }
    @Override public java.util.List getAllApexDirectories() throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "getAllApexDirectories"); }
    @Override public android.content.pm.ParceledListSlice getAllIntentFilters(java.lang.String p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "getAllIntentFilters"); }
    @Override public java.util.List getAllPackages() throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "getAllPackages"); }
    @Override public android.os.ParcelFileDescriptor getAppMetadataFd(java.lang.String p0, int p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "getAppMetadataFd"); }
    @Override public int getAppMetadataSource(java.lang.String p0, int p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "getAppMetadataSource"); }
    @Override public java.lang.String[] getAppOpPermissionPackages(java.lang.String p0, int p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "getAppOpPermissionPackages"); }
    @Override public java.lang.String getAppPredictionServicePackageName() throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "getAppPredictionServicePackageName"); }
    @Override public int getApplicationEnabledSetting(java.lang.String p0, int p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "getApplicationEnabledSetting"); }
    @Override public boolean getApplicationHiddenSettingAsUser(java.lang.String p0, int p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "getApplicationHiddenSettingAsUser"); }
    @Override public android.graphics.Bitmap getArchivedAppIcon(java.lang.String p0, android.os.UserHandle p1, java.lang.String p2) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "getArchivedAppIcon"); }
    @Override public android.content.pm.ArchivedPackageParcel getArchivedPackage(java.lang.String p0, int p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "getArchivedPackage"); }
    @Override public android.content.pm.dex.IArtManager getArtManager() throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "getArtManager"); }
    @Override public java.lang.String getAttentionServicePackageName() throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "getAttentionServicePackageName"); }
    @Override public boolean getBlockUninstallForUser(java.lang.String p0, int p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "getBlockUninstallForUser"); }
    @Override public android.content.pm.ChangedPackages getChangedPackages(int p0, int p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "getChangedPackages"); }
    @Override public int getComponentEnabledSetting(android.content.ComponentName p0, int p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "getComponentEnabledSetting"); }
    @Override public android.content.pm.ParceledListSlice getDeclaredSharedLibraries(java.lang.String p0, long p1, int p2) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "getDeclaredSharedLibraries"); }
    @Override public byte[] getDefaultAppsBackup(int p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "getDefaultAppsBackup"); }
    @Override public java.lang.String getDefaultTextClassifierPackageName() throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "getDefaultTextClassifierPackageName"); }
    @Override public android.content.ComponentName getDomainVerificationAgent(int p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "getDomainVerificationAgent"); }
    @Override public byte[] getDomainVerificationBackup(int p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "getDomainVerificationBackup"); }
    @Override public int getFlagsForUid(int p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "getFlagsForUid"); }
    @Override public java.lang.CharSequence getHarmfulAppWarning(java.lang.String p0, int p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "getHarmfulAppWarning"); }
    @Override public android.os.IBinder getHoldLockToken() throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "getHoldLockToken"); }
    @Override public android.content.ComponentName getHomeActivities(java.util.List p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "getHomeActivities"); }
    @Override public java.lang.String getIncidentReportApproverPackageName() throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "getIncidentReportApproverPackageName"); }
    @Override public java.util.List getInitialNonStoppedSystemPackages() throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "getInitialNonStoppedSystemPackages"); }
    @Override public int getInstallLocation() throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "getInstallLocation"); }
    @Override public int getInstallReason(java.lang.String p0, int p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "getInstallReason"); }
    @Override public android.content.pm.InstallSourceInfo getInstallSourceInfo(java.lang.String p0, int p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "getInstallSourceInfo"); }
    @Override public java.util.List getInstalledModules(int p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "getInstalledModules"); }
    @Override public java.lang.String getInstantAppAndroidId(java.lang.String p0, int p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "getInstantAppAndroidId"); }
    @Override public byte[] getInstantAppCookie(java.lang.String p0, int p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "getInstantAppCookie"); }
    @Override public android.graphics.Bitmap getInstantAppIcon(java.lang.String p0, int p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "getInstantAppIcon"); }
    @Override public android.content.ComponentName getInstantAppInstallerComponent() throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "getInstantAppInstallerComponent"); }
    @Override public android.content.ComponentName getInstantAppResolverComponent() throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "getInstantAppResolverComponent"); }
    @Override public android.content.ComponentName getInstantAppResolverSettingsComponent() throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "getInstantAppResolverSettingsComponent"); }
    @Override public android.content.pm.ParceledListSlice getInstantApps(int p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "getInstantApps"); }
    @Override public android.content.pm.InstrumentationInfo getInstrumentationInfoAsUser(android.content.ComponentName p0, int p1, int p2) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "getInstrumentationInfoAsUser"); }
    @Override public android.content.pm.ParceledListSlice getIntentFilterVerifications(java.lang.String p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "getIntentFilterVerifications"); }
    @Override public int getIntentVerificationStatus(java.lang.String p0, int p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "getIntentVerificationStatus"); }
    @Override public android.content.pm.KeySet getKeySetByAlias(java.lang.String p0, java.lang.String p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "getKeySetByAlias"); }
    @Override public android.content.pm.ResolveInfo getLastChosenActivity(android.content.Intent p0, java.lang.String p1, int p2) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "getLastChosenActivity"); }
    @Override public android.content.IntentSender getLaunchIntentSenderForPackage(java.lang.String p0, java.lang.String p1, java.lang.String p2, int p3) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "getLaunchIntentSenderForPackage"); }
    @Override public java.util.List getMimeGroup(java.lang.String p0, java.lang.String p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "getMimeGroup"); }
    @Override public android.content.pm.ModuleInfo getModuleInfo(java.lang.String p0, int p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "getModuleInfo"); }
    @Override public int getMoveStatus(int p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "getMoveStatus"); }
    @Override public java.lang.String[] getNamesForUids(int[] p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "getNamesForUids"); }
    @Override public int[] getPackageGids(java.lang.String p0, long p1, int p2) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "getPackageGids"); }
    @Override public android.content.pm.PackageInfo getPackageInfoVersioned(android.content.pm.VersionedPackage p0, long p1, int p2) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "getPackageInfoVersioned"); }
    @Override public android.content.pm.IPackageInstaller getPackageInstaller() throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "getPackageInstaller"); }
    @Override public void getPackageSizeInfo(java.lang.String p0, int p1, android.content.pm.IPackageStatsObserver p2) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "getPackageSizeInfo"); }
    @Override public int getPackageUid(java.lang.String p0, long p1, int p2) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "getPackageUid"); }
    @Override public android.content.pm.ParceledListSlice getPackagesHoldingPermissions(java.lang.String[] p0, long p1, int p2) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "getPackagesHoldingPermissions"); }
    @Override public java.lang.String getPageSizeCompatWarningMessage(java.lang.String p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "getPageSizeCompatWarningMessage"); }
    @Override public java.lang.String getPermissionControllerPackageName() throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "getPermissionControllerPackageName"); }
    @Override public android.content.pm.PermissionGroupInfo getPermissionGroupInfo(java.lang.String p0, int p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "getPermissionGroupInfo"); }
    @Override public android.content.pm.ParceledListSlice getPersistentApplications(int p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "getPersistentApplications"); }
    @Override public int getPreferredActivities(java.util.List p0, java.util.List p1, java.lang.String p2) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "getPreferredActivities"); }
    @Override public byte[] getPreferredActivityBackup(int p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "getPreferredActivityBackup"); }
    @Override public int getPrivateFlagsForUid(int p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "getPrivateFlagsForUid"); }
    @Override public android.content.pm.PackageManager.Property getPropertyAsUser(java.lang.String p0, java.lang.String p1, java.lang.String p2, int p3) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "getPropertyAsUser"); }
    @Override public java.lang.String getRotationResolverPackageName() throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "getRotationResolverPackageName"); }
    @Override public int getRuntimePermissionsVersion(int p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "getRuntimePermissionsVersion"); }
    @Override public java.lang.String getSdkSandboxPackageName() throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "getSdkSandboxPackageName"); }
    @Override public java.lang.String getServicesSystemSharedLibraryPackageName() throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "getServicesSystemSharedLibraryPackageName"); }
    @Override public java.lang.String getSetupWizardPackageName() throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "getSetupWizardPackageName"); }
    @Override public android.content.pm.ParceledListSlice getSharedLibraries(java.lang.String p0, long p1, int p2) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "getSharedLibraries"); }
    @Override public java.lang.String getSharedSystemSharedLibraryPackageName() throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "getSharedSystemSharedLibraryPackageName"); }
    @Override public android.content.pm.KeySet getSigningKeySet(java.lang.String p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "getSigningKeySet"); }
    @Override public java.lang.String getSplashScreenTheme(java.lang.String p0, int p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "getSplashScreenTheme"); }
    @Override public android.os.Bundle getSuspendedPackageAppExtras(java.lang.String p0, int p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "getSuspendedPackageAppExtras"); }
    @Override public java.lang.String getSuspendingPackage(java.lang.String p0, int p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "getSuspendingPackage"); }
    @Override public android.content.pm.ParceledListSlice getSystemAvailableFeatures() throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "getSystemAvailableFeatures"); }
    @Override public java.lang.String getSystemCaptionsServicePackageName() throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "getSystemCaptionsServicePackageName"); }
    @Override public java.lang.String[] getSystemSharedLibraryNames() throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "getSystemSharedLibraryNames"); }
    @Override public java.util.Map getSystemSharedLibraryNamesAndPaths() throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "getSystemSharedLibraryNamesAndPaths"); }
    @Override public java.lang.String getSystemTextClassifierPackageName() throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "getSystemTextClassifierPackageName"); }
    @Override public int getTargetSdkVersion(java.lang.String p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "getTargetSdkVersion"); }
    @Override public int getUidForSharedUser(java.lang.String p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "getUidForSharedUser"); }
    @Override public java.lang.String[] getUnsuspendablePackagesForUser(java.lang.String[] p0, int p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "getUnsuspendablePackagesForUser"); }
    @Override public int getUserMinAspectRatio(java.lang.String p0, int p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "getUserMinAspectRatio"); }
    @Override public android.content.pm.VerifierDeviceIdentity getVerifierDeviceIdentity() throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "getVerifierDeviceIdentity"); }
    @Override public java.lang.String getWellbeingPackageName() throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "getWellbeingPackageName"); }
    @Override public void grantRuntimePermission(java.lang.String p0, java.lang.String p1, int p2) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "grantRuntimePermission"); }
    @Override public boolean hasSigningCertificate(java.lang.String p0, byte[] p1, int p2) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "hasSigningCertificate"); }
    @Override public boolean hasSystemUidErrors() throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "hasSystemUidErrors"); }
    @Override public boolean hasUidSigningCertificate(int p0, byte[] p1, int p2) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "hasUidSigningCertificate"); }
    @Override public void holdLock(android.os.IBinder p0, int p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "holdLock"); }
    @Override public int installExistingPackageAsUser(java.lang.String p0, int p1, int p2, int p3, java.util.List p4) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "installExistingPackageAsUser"); }
    @Override public boolean isAppArchivable(java.lang.String p0, android.os.UserHandle p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "isAppArchivable"); }
    @Override public boolean isAutoRevokeWhitelisted(java.lang.String p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "isAutoRevokeWhitelisted"); }
    @Override public boolean isDeviceUpgrading() throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "isDeviceUpgrading"); }
    @Override public boolean isFirstBoot() throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "isFirstBoot"); }
    @Override public boolean isInstantApp(java.lang.String p0, int p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "isInstantApp"); }
    @Override public boolean isPackageAvailable(java.lang.String p0, int p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "isPackageAvailable"); }
    @Override public boolean isPackageDeviceAdminOnAnyUser(java.lang.String p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "isPackageDeviceAdminOnAnyUser"); }
    @Override public boolean isPackageQuarantinedForUser(java.lang.String p0, int p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "isPackageQuarantinedForUser"); }
    @Override public boolean isPackageSignedByKeySet(java.lang.String p0, android.content.pm.KeySet p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "isPackageSignedByKeySet"); }
    @Override public boolean isPackageSignedByKeySetExactly(java.lang.String p0, android.content.pm.KeySet p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "isPackageSignedByKeySetExactly"); }
    @Override public boolean isPackageStateProtected(java.lang.String p0, int p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "isPackageStateProtected"); }
    @Override public boolean isPackageStoppedForUser(java.lang.String p0, int p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "isPackageStoppedForUser"); }
    @Override public boolean isPackageSuspendedForUser(java.lang.String p0, int p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "isPackageSuspendedForUser"); }
    @Override public boolean isPageSizeCompatEnabled(java.lang.String p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "isPageSizeCompatEnabled"); }
    @Override public boolean isProtectedBroadcast(java.lang.String p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "isProtectedBroadcast"); }
    @Override public boolean isSafeMode() throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "isSafeMode"); }
    @Override public boolean isStorageLow() throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "isStorageLow"); }
    @Override public boolean isUidPrivileged(int p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "isUidPrivileged"); }
    @Override public void logAppProcessStartIfNeeded(java.lang.String p0, java.lang.String p1, int p2, java.lang.String p3, java.lang.String p4, int p5) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "logAppProcessStartIfNeeded"); }
    @Override public void makeProviderVisible(int p0, java.lang.String p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "makeProviderVisible"); }
    @Override public void makeUidVisible(int p0, int p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "makeUidVisible"); }
    @Override public int movePackage(java.lang.String p0, java.lang.String p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "movePackage"); }
    @Override public int movePrimaryStorage(java.lang.String p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "movePrimaryStorage"); }
    @Override public void notifyDexLoad(java.lang.String p0, java.util.Map p1, java.lang.String p2) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "notifyDexLoad"); }
    @Override public void notifyPackageUse(java.lang.String p0, int p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "notifyPackageUse"); }
    @Override public void notifyPackagesReplacedReceived(java.lang.String[] p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "notifyPackagesReplacedReceived"); }
    @Override public void overrideLabelAndIcon(android.content.ComponentName p0, java.lang.String p1, int p2, int p3) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "overrideLabelAndIcon"); }
    @Override public boolean performDexOptMode(java.lang.String p0, boolean p1, java.lang.String p2, boolean p3, boolean p4, java.lang.String p5) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "performDexOptMode"); }
    @Override public boolean performDexOptSecondary(java.lang.String p0, java.lang.String p1, boolean p2) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "performDexOptSecondary"); }
    @Override public android.content.pm.ParceledListSlice queryInstrumentationAsUser(java.lang.String p0, int p1, int p2) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "queryInstrumentationAsUser"); }
    @Override public android.content.pm.ParceledListSlice queryIntentActivities(android.content.Intent p0, java.lang.String p1, long p2, int p3) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "queryIntentActivities"); }
    @Override public android.content.pm.ParceledListSlice queryIntentActivityOptions(android.content.ComponentName p0, android.content.Intent[] p1, java.lang.String[] p2, android.content.Intent p3, java.lang.String p4, long p5, int p6) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "queryIntentActivityOptions"); }
    @Override public android.content.pm.ParceledListSlice queryIntentContentProviders(android.content.Intent p0, java.lang.String p1, long p2, int p3) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "queryIntentContentProviders"); }
    @Override public android.content.pm.ParceledListSlice queryIntentReceivers(android.content.Intent p0, java.lang.String p1, long p2, int p3) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "queryIntentReceivers"); }
    @Override public android.content.pm.ParceledListSlice queryIntentServices(android.content.Intent p0, java.lang.String p1, long p2, int p3) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "queryIntentServices"); }
    @Override public android.content.pm.ParceledListSlice queryProperty(java.lang.String p0, int p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "queryProperty"); }
    @Override public void querySyncProviders(java.util.List p0, java.util.List p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "querySyncProviders"); }
    @Override public void registerDexModule(java.lang.String p0, java.lang.String p1, boolean p2, android.content.pm.IDexModuleRegisterCallback p3) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "registerDexModule"); }
    @Override public void registerMoveCallback(android.content.pm.IPackageMoveObserver p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "registerMoveCallback"); }
    @Override public void registerPackageMonitorCallback(android.os.IRemoteCallback p0, int p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "registerPackageMonitorCallback"); }
    @Override public void relinquishUpdateOwnership(java.lang.String p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "relinquishUpdateOwnership"); }
    @Override public boolean removeCrossProfileIntentFilter(android.content.IntentFilter p0, java.lang.String p1, int p2, int p3, int p4) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "removeCrossProfileIntentFilter"); }
    @Override public void removePermission(java.lang.String p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "removePermission"); }
    @Override public void replacePreferredActivity(android.content.IntentFilter p0, int p1, android.content.ComponentName[] p2, android.content.ComponentName p3, int p4) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "replacePreferredActivity"); }
    @Override public void requestPackageChecksums(java.lang.String p0, boolean p1, int p2, int p3, java.util.List p4, android.content.pm.IOnChecksumsReadyListener p5, int p6) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "requestPackageChecksums"); }
    @Override public void resetApplicationPreferences(int p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "resetApplicationPreferences"); }
    @Override public android.content.pm.ProviderInfo resolveContentProvider(java.lang.String p0, long p1, int p2) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "resolveContentProvider"); }
    @Override public void restoreDefaultApps(byte[] p0, int p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "restoreDefaultApps"); }
    @Override public void restoreDomainVerification(byte[] p0, int p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "restoreDomainVerification"); }
    @Override public void restoreLabelAndIcon(android.content.ComponentName p0, int p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "restoreLabelAndIcon"); }
    @Override public void restorePreferredActivities(byte[] p0, int p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "restorePreferredActivities"); }
    @Override public void sendDeviceCustomizationReadyBroadcast() throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "sendDeviceCustomizationReadyBroadcast"); }
    @Override public void setApplicationCategoryHint(java.lang.String p0, int p1, java.lang.String p2) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "setApplicationCategoryHint"); }
    @Override public void setApplicationEnabledSetting(java.lang.String p0, int p1, int p2, int p3, java.lang.String p4) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "setApplicationEnabledSetting"); }
    @Override public boolean setApplicationHiddenSettingAsUser(java.lang.String p0, boolean p1, int p2) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "setApplicationHiddenSettingAsUser"); }
    @Override public boolean setBlockUninstallForUser(java.lang.String p0, boolean p1, int p2) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "setBlockUninstallForUser"); }
    @Override public void setComponentEnabledSetting(android.content.ComponentName p0, int p1, int p2, int p3, java.lang.String p4) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "setComponentEnabledSetting"); }
    @Override public void setComponentEnabledSettings(java.util.List p0, int p1, java.lang.String p2) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "setComponentEnabledSettings"); }
    @Override public java.lang.String[] setDistractingPackageRestrictionsAsUser(java.lang.String[] p0, int p1, int p2) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "setDistractingPackageRestrictionsAsUser"); }
    @Override public void setHarmfulAppWarning(java.lang.String p0, java.lang.CharSequence p1, int p2) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "setHarmfulAppWarning"); }
    @Override public void setHomeActivity(android.content.ComponentName p0, int p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "setHomeActivity"); }
    @Override public boolean setInstallLocation(int p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "setInstallLocation"); }
    @Override public void setInstallerPackageName(java.lang.String p0, java.lang.String p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "setInstallerPackageName"); }
    @Override public boolean setInstantAppCookie(java.lang.String p0, byte[] p1, int p2) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "setInstantAppCookie"); }
    @Override public void setKeepUninstalledPackages(java.util.List p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "setKeepUninstalledPackages"); }
    @Override public void setLastChosenActivity(android.content.Intent p0, java.lang.String p1, int p2, android.content.IntentFilter p3, int p4, android.content.ComponentName p5) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "setLastChosenActivity"); }
    @Override public void setMimeGroup(java.lang.String p0, java.lang.String p1, java.util.List p2) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "setMimeGroup"); }
    @Override public void setPackageStoppedState(java.lang.String p0, boolean p1, int p2) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "setPackageStoppedState"); }
    @Override public java.lang.String[] setPackagesSuspendedAsUser(java.lang.String[] p0, boolean p1, android.os.PersistableBundle p2, android.os.PersistableBundle p3, android.content.pm.SuspendDialogInfo p4, int p5, java.lang.String p6, int p7, int p8) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "setPackagesSuspendedAsUser"); }
    @Override public void setPageSizeAppCompatFlagsSettingsOverride(java.lang.String p0, boolean p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "setPageSizeAppCompatFlagsSettingsOverride"); }
    @Override public boolean setRequiredForSystemUser(java.lang.String p0, boolean p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "setRequiredForSystemUser"); }
    @Override public void setRuntimePermissionsVersion(int p0, int p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "setRuntimePermissionsVersion"); }
    @Override public void setSplashScreenTheme(java.lang.String p0, java.lang.String p1, int p2) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "setSplashScreenTheme"); }
    @Override public void setSystemAppHiddenUntilInstalled(java.lang.String p0, boolean p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "setSystemAppHiddenUntilInstalled"); }
    @Override public boolean setSystemAppInstallState(java.lang.String p0, boolean p1, int p2) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "setSystemAppInstallState"); }
    @Override public void setUpdateAvailable(java.lang.String p0, boolean p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "setUpdateAvailable"); }
    @Override public void setUserMinAspectRatio(java.lang.String p0, int p1, int p2) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "setUserMinAspectRatio"); }
    @Override public void unregisterMoveCallback(android.content.pm.IPackageMoveObserver p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "unregisterMoveCallback"); }
    @Override public void unregisterPackageMonitorCallback(android.os.IRemoteCallback p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "unregisterPackageMonitorCallback"); }
    @Override public boolean updateIntentVerificationStatus(java.lang.String p0, int p1, int p2) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "updateIntentVerificationStatus"); }
    @Override public void verifyIntentFilter(int p0, int p1, java.util.List p2) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "verifyIntentFilter"); }
    @Override public void verifyPendingInstall(int p0, int p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "verifyPendingInstall"); }
    @Override public boolean waitForHandler(long p0, boolean p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("package", "waitForHandler"); }

    // ------------------------------------------------------------------
    //   Diagnostic helpers (not part of IPackageManager surface)
    // ------------------------------------------------------------------

    /** Our package name (for tests). */
    public String packageName() { return mPackageName; }

    @Override
    public String toString() {
        return "WestlakePackageManagerService{package=" + mPackageName + "}";
    }
}
