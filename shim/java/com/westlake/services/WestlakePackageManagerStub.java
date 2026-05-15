// SPDX-License-Identifier: Apache-2.0
//
// Westlake M4-PRE5 -- WestlakePackageManagerStub
//
// Local (NOT binder-backed) PackageManager implementation that returns
// meaningful ServiceInfo / ActivityInfo / ApplicationInfo / PackageInfo
// for the host app's components.  Plugged into WestlakeContextImpl
// .getPackageManager() so that Activity.attach's call chain
//   e.q.attachBaseContext(Context) -> e.v.c(Context) ->
//   Context.getPackageManager().getServiceInfo(ComponentName, int)
// returns a non-null ServiceInfo instead of NPEing.
//
// Rationale (see docs/engine/M4_DISCOVERY.md sec 16):
//   M4-PRE4 cleared the Looper/MessageQueue blocker, so PHASE G3 of the
//   noice-discover harness reaches Activity.attach with 17/20 args
//   correctly populated and then NPEs at the very first line of noice's
//   compiled Hilt-DI-generated attachBaseContext override (after R8
//   obfuscation: e.q.attachBaseContext -> e.v.c -> pm.getServiceInfo).
//   Returning a populated ServiceInfo lets that path proceed; subsequent
//   PHASE G3 failures (or G4 entry) become the M4-PRE6 candidate.
//
// Architecture:
//   - Compile-time parent: shim's android.content.pm.PackageManager (concrete
//     class with Object-typed stubs for most methods + ~13 real-impl
//     methods).  This lets the shim compile via javac.
//   - Runtime parent: framework.jar's android.content.pm.PackageManager
//     (abstract; 179 abstract methods on Android 11 SDK 30 surface).
//     scripts/framework_duplicates.txt strips the shim's PackageManager
//     from aosp-shim.dex so framework.jar wins.  This class must
//     implement every abstract method or 
//     throws InstantiationError.
//   - This is NOT an IPackageManager.Stub.  Context.getPackageManager()
//     returns a java.lang.PackageManager (abstract class), not an IBinder.
//     The binder-backed IPackageManager service is M4c's separate work.
//
// Manifest source:
//   - APK path passed via constructor.  AndroidManifest.xml is parsed
//     lazily via android.content.pm.ManifestParser (already in the shim;
//     see shim/java/android/content/pm/ManifestParser.java).
//   - Parsed manifest is cached so repeated getServiceInfo/getActivityInfo
//     lookups are O(1).
//   - For components not in the parsed manifest we return a synthetic
//     populated ServiceInfo/ActivityInfo (rather than throw
//     NameNotFoundException) so noice's Hilt-generated attach helpers
//     proceed with a reasonable default.  Tightening to throw is a future
//     refinement once discovery says it's needed.
//
// Method count: 179 abstract methods overridden; 11 with real behavior,
// 168 fail-loud throws (CR19 — was 168 silent safe-defaults pre-CR19).
//
// CR19 (2026-05-12) — fail-loud conversion of non-Tier-1 bodies:
//   Codex review #2 Tier 2 finding: this LOCAL PackageManager (returned by
//   Context.getPackageManager()) had 168 silent stubs returning
//   null/false/0/empty when the JVM accepted the class as concrete.  That
//   meant Hilt-DI / AndroidX / app code could call e.g.
//   getInstalledApplications(0) and silently receive `null` instead of
//   tripping a clear "method not observed during discovery" signal.
//   CR2 established the fail-loud pattern (ServiceMethodMissing.fail)
//   for the binder-backed Stubs in M4a/M4b/M4c/M4d/M4e/M4-power;  CR19
//   applies the SAME pattern here so this LOCAL PM no longer bypasses the
//   discovery signal.  Tier-1 methods (the 11 listed above) are preserved
//   unchanged.
//
// Reference (Android 11 SDK 30 PackageManager.aidl):
//   https://android.googlesource.com/platform/frameworks/base/+/refs/tags/android-11.0.0_r48/core/java/android/content/pm/PackageManager.java
//
// Author: M4-PRE5 agent (orig 2026-05-12) + CR19 fail-loud conversion (2026-05-12)

package com.westlake.services;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.ManifestParser;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Local PackageManager stub for the Westlake dalvikvm sandbox.  Plugged
 * into WestlakeContextImpl.getPackageManager() to satisfy Hilt-DI
 * Activity.attach() PackageManager calls.
 */
public final class WestlakePackageManagerStub extends PackageManager {

    private final String mPackageName;
    private final String mApkPath;
    private final ApplicationInfo mApplicationInfo;

    // Cached parsed manifest (lazy).  null until first parse; on failure
    // remains null and we fall back to "all components allowed" mode.
    private volatile ManifestParser.ManifestInfo mManifestInfo;
    private volatile boolean mManifestParseAttempted;

    // Quick-lookup sets built from mManifestInfo on first access.
    private volatile Set<String> mActivityNames;
    private volatile Set<String> mServiceNames;
    private volatile Set<String> mProviderNames;

    /**
     * Build a PackageManager for the given package.
     *
     * @param packageName      package name from the APK manifest
     * @param apkPath          absolute path to the APK on disk
     * @param applicationInfo  the ApplicationInfo that
     *                         WestlakeContextImpl.getApplicationInfo()
     *                         returns; shared by reference so all
     *                         PackageManager-returned ApplicationInfos
     *                         match the Context's view of the app.
     */
    public WestlakePackageManagerStub(String packageName, String apkPath,
            ApplicationInfo applicationInfo) {
        if (packageName == null || packageName.isEmpty()) {
            throw new IllegalArgumentException("packageName must be non-empty");
        }
        mPackageName = packageName;
        mApkPath = apkPath;
        mApplicationInfo = applicationInfo != null ? applicationInfo
                : buildDefaultApplicationInfo(packageName, apkPath);
    }

    private static ApplicationInfo buildDefaultApplicationInfo(
            String packageName, String apkPath) {
        ApplicationInfo info = new ApplicationInfo();
        info.packageName = packageName;
        info.processName = packageName;
        info.sourceDir = apkPath;
        info.publicSourceDir = apkPath;
        info.dataDir = "/data/local/tmp/westlake/" + packageName;
        info.uid = android.os.Process.myUid();
        return info;
    }

    // ----------------------------------------------------------------------
    // Manifest lazy parse + lookup helpers
    // ----------------------------------------------------------------------

    private ManifestParser.ManifestInfo manifestInfo() {
        ManifestParser.ManifestInfo cached = mManifestInfo;
        if (cached != null) return cached;
        synchronized (this) {
            if (mManifestInfo != null) return mManifestInfo;
            if (mManifestParseAttempted) return null;
            mManifestParseAttempted = true;

            if (mApkPath == null || mApkPath.isEmpty()) return null;
            try {
                byte[] axml = readManifestBytes(mApkPath);
                if (axml == null) return null;
                mManifestInfo = ManifestParser.parse(axml);
                if (mManifestInfo != null) {
                    Set<String> a = new HashSet<>();
                    if (mManifestInfo.activities != null) a.addAll(mManifestInfo.activities);
                    mActivityNames = a;
                    Set<String> s = new HashSet<>();
                    if (mManifestInfo.services != null) s.addAll(mManifestInfo.services);
                    mServiceNames = s;
                    Set<String> p = new HashSet<>();
                    if (mManifestInfo.providers != null) p.addAll(mManifestInfo.providers);
                    mProviderNames = p;
                }
            } catch (Throwable t) {
                // Best-effort: AXML parse failure means we fall back to
                // synthetic ServiceInfo / ActivityInfo for any requested
                // component.  Log to stderr (println would NPE in this
                // dalvikvm — same constraint as NoiceDiscoverWrapper).
                try {
                    System.err.println("[WLK-pm] manifest parse failed: " + t);
                } catch (Throwable ignored) {}
            }
            return mManifestInfo;
        }
    }

    /** Read AndroidManifest.xml bytes from the APK zip. */
    private static byte[] readManifestBytes(String apkPath) {
        ZipFile zip = null;
        try {
            zip = new ZipFile(apkPath);
            ZipEntry e = zip.getEntry("AndroidManifest.xml");
            if (e == null) return null;
            InputStream is = zip.getInputStream(e);
            ByteArrayOutputStream baos = new ByteArrayOutputStream(
                    (int) Math.max(4096, e.getSize()));
            byte[] buf = new byte[8192];
            int n;
            while ((n = is.read(buf)) >= 0) baos.write(buf, 0, n);
            is.close();
            return baos.toByteArray();
        } catch (Throwable t) {
            return null;
        } finally {
            if (zip != null) try { zip.close(); } catch (Throwable ignored) {}
        }
    }

    /** Does the parsed manifest declare an activity with this FQCN? */
    private boolean isKnownActivity(String fqcn) {
        manifestInfo();
        Set<String> set = mActivityNames;
        return set != null && set.contains(fqcn);
    }

    /** Does the parsed manifest declare a service with this FQCN? */
    private boolean isKnownService(String fqcn) {
        manifestInfo();
        Set<String> set = mServiceNames;
        return set != null && set.contains(fqcn);
    }

    // ----------------------------------------------------------------------
    // REAL implementations — the methods discovery showed Hilt actually uses
    // ----------------------------------------------------------------------

    /**
     * Return a populated ServiceInfo for the requested component.  This
     * is the method that drove M4-PRE5 — Hilt-generated
     * attachBaseContext helpers (e.v.c in noice's R8 obfuscation) call
     * pm.getServiceInfo() to inspect their @AndroidEntryPoint services'
     * metadata before binding.
     *
     * Strategy:
     *   - If the component's package matches our package AND it's
     *     declared in the parsed manifest, return a fully populated info.
     *   - If the component's package matches us but isn't in the
     *     manifest (or manifest parse failed), return a synthetic info
     *     anyway — Hilt-generated lookups for AppLocalesMetadataHolderService
     *     etc are common and should not block attach.
     *   - If the package doesn't match ours, throw NameNotFoundException
     *     (the AOSP-canonical behavior for a foreign package query).
     */
    @Override
    public ServiceInfo getServiceInfo(ComponentName component, int flags)
            throws PackageManager.NameNotFoundException {
        if (component == null) {
            throw new PackageManager.NameNotFoundException("component is null");
        }
        if (!matchesOurPackage(component.getPackageName())) {
            throw new PackageManager.NameNotFoundException(component.flattenToString());
        }
        ServiceInfo info = new ServiceInfo();
        info.packageName = mPackageName;
        info.name = component.getClassName();
        info.applicationInfo = mApplicationInfo;
        info.enabled = true;
        info.exported = false;
        try {
            info.processName = mPackageName;
        } catch (Throwable ignored) {}
        try {
            info.metaData = new android.os.Bundle();
        } catch (Throwable ignored) {}

        // For AndroidX startup compatibility we tolerate any service name
        // (Hilt creates many SyntheticEntryPoint services); the synthetic
        // info above carries enough fields for AppLocalesMetadataHolderService,
        // Hilt's HiltViewModelExtensions, etc. to proceed.
        return info;
    }

    /**
     * Return ActivityInfo for the requested component.  Used during
     * Activity.attach to populate mActivityInfo before onCreate runs.
     */
    @Override
    public ActivityInfo getActivityInfo(ComponentName component, int flags)
            throws PackageManager.NameNotFoundException {
        if (component == null) {
            throw new PackageManager.NameNotFoundException("component is null");
        }
        if (!matchesOurPackage(component.getPackageName())) {
            throw new PackageManager.NameNotFoundException(component.flattenToString());
        }
        ActivityInfo info = new ActivityInfo();
        info.packageName = mPackageName;
        info.name = component.getClassName();
        info.applicationInfo = mApplicationInfo;
        info.enabled = true;
        info.exported = true;
        try {
            info.processName = mPackageName;
        } catch (Throwable ignored) {}
        try {
            info.metaData = new android.os.Bundle();
        } catch (Throwable ignored) {}
        return info;
    }

    /** Return our cached ApplicationInfo for the requested package. */
    @Override
    public ApplicationInfo getApplicationInfo(String packageName, int flags)
            throws PackageManager.NameNotFoundException {
        if (packageName == null) {
            throw new PackageManager.NameNotFoundException("packageName is null");
        }
        if (matchesOurPackage(packageName)) {
            return mApplicationInfo;
        }
        throw new PackageManager.NameNotFoundException(packageName);
    }

    /** Return a populated PackageInfo for our package. */
    @Override
    public PackageInfo getPackageInfo(String packageName, int flags)
            throws PackageManager.NameNotFoundException {
        if (packageName == null) {
            throw new PackageManager.NameNotFoundException("packageName is null");
        }
        if (!matchesOurPackage(packageName)) {
            throw new PackageManager.NameNotFoundException(packageName);
        }
        PackageInfo pi = new PackageInfo();
        pi.packageName = mPackageName;
        pi.versionCode = 1;
        pi.versionName = "1.0";
        pi.applicationInfo = mApplicationInfo;
        long now = System.currentTimeMillis();
        pi.firstInstallTime = now;
        pi.lastUpdateTime = now;
        return pi;
    }

    /** No system features advertised — safe default. */
    @Override
    public boolean hasSystemFeature(String name) {
        return false;
    }

    /** No system features at any version — safe default.  Not in shim's
     *  PackageManager base; overrides framework.jar's abstract at runtime. */
    public boolean hasSystemFeature(String name, int version) {
        return false;
    }

    /** No registered activity resolver — return null. */
    @Override
    public android.content.pm.ResolveInfo resolveActivity(Intent intent, int flags) {
        return null;
    }

    /** No registered service resolver — return null.  Not in shim's
     *  PackageManager base; overrides framework.jar's abstract at runtime. */
    public android.content.pm.ResolveInfo resolveService(Intent intent, int flags) {
        return null;
    }

    /** Empty activity list — safe default.  No @Override because the shim
     *  PackageManager declares the return type as List<ResolveInfo>; we
     *  use raw List to match framework.jar's abstract method descriptor. */
    public List queryIntentActivities(Intent intent, int flags) {
        return new ArrayList<>();
    }

    /** Single-item list with our own PackageInfo — safe default.  Not in
     *  shim's PackageManager base; overrides framework.jar's abstract. */
    public List getInstalledPackages(int flags) {
        ArrayList<PackageInfo> out = new ArrayList<>();
        try {
            out.add(getPackageInfo(mPackageName, flags));
        } catch (PackageManager.NameNotFoundException ignored) {}
        return out;
    }

    /** Sandbox runs as system uid — everything is granted. */
    @Override
    public int checkPermission(String permission, String packageName) {
        return PackageManager.PERMISSION_GRANTED;
    }

    private boolean matchesOurPackage(String pkg) {
        return pkg != null && pkg.equals(mPackageName);
    }

    // ----------------------------------------------------------------------
    //  CR19 FAIL-LOUD METHODS for the remaining 168 abstract PackageManager
    //  methods (Android 11 SDK 30 surface, framework.jar).
    //
    //  Generated from a dexdump of framework.jar's PackageManager abstract
    //  method list.  Method signatures match exactly so the JVM accepts
    //  WestlakePackageManagerStub as concrete at <init> time.
    //
    //  Each method now throws ServiceMethodMissing.fail("packageManager",
    //  "<methodName>") -- pre-CR19 these returned 0/null/false/empty
    //  silently, which masked unobserved call sites and let app code limp
    //  along on wrong defaults.  Per codex review #2 Tier 2: silent
    //  fall-through is precisely the smell CR2 fixed for the binder-backed
    //  M4 services; CR19 applies the same convention here.
    //
    //  Some methods declare `throws NameNotFoundException` (a checked
    //  exception).  ServiceMethodMissing.fail returns an
    //  UnsupportedOperationException (unchecked), so the throw statement
    //  satisfies the abstract-method signature without needing the checked
    //  clause -- unchecked exceptions bypass checked-exception decls.
    //
    //  Cross-reference against the AOSP PackageManager source:
    //  https://android.googlesource.com/platform/frameworks/base/+/refs/tags/android-11.0.0_r48/core/java/android/content/pm/PackageManager.java
    // ----------------------------------------------------------------------

    public void addCrossProfileIntentFilter(android.content.IntentFilter p0, int p1, int p2, int p3) { throw ServiceMethodMissing.fail("packageManager", "addCrossProfileIntentFilter"); }
    public void addOnPermissionsChangeListener(android.content.pm.PackageManager.OnPermissionsChangedListener p0) { throw ServiceMethodMissing.fail("packageManager", "addOnPermissionsChangeListener"); }
    public void addPackageToPreferred(java.lang.String p0) { throw ServiceMethodMissing.fail("packageManager", "addPackageToPreferred"); }
    public boolean addPermission(android.content.pm.PermissionInfo p0) { throw ServiceMethodMissing.fail("packageManager", "addPermission"); }
    public boolean addPermissionAsync(android.content.pm.PermissionInfo p0) { throw ServiceMethodMissing.fail("packageManager", "addPermissionAsync"); }
    public void addPreferredActivity(android.content.IntentFilter p0, int p1, android.content.ComponentName[] p2, android.content.ComponentName p3) { throw ServiceMethodMissing.fail("packageManager", "addPreferredActivity"); }
    public boolean arePermissionsIndividuallyControlled() { throw ServiceMethodMissing.fail("packageManager", "arePermissionsIndividuallyControlled"); }
    public boolean canRequestPackageInstalls() { throw ServiceMethodMissing.fail("packageManager", "canRequestPackageInstalls"); }
    public java.lang.String[] canonicalToCurrentPackageNames(java.lang.String[] p0) { throw ServiceMethodMissing.fail("packageManager", "canonicalToCurrentPackageNames"); }
    public int checkSignatures(int p0, int p1) { throw ServiceMethodMissing.fail("packageManager", "checkSignatures"); }
    public int checkSignatures(java.lang.String p0, java.lang.String p1) { throw ServiceMethodMissing.fail("packageManager", "checkSignatures"); }
    public void clearApplicationUserData(java.lang.String p0, android.content.pm.IPackageDataObserver p1) { throw ServiceMethodMissing.fail("packageManager", "clearApplicationUserData"); }
    public void clearCrossProfileIntentFilters(int p0) { throw ServiceMethodMissing.fail("packageManager", "clearCrossProfileIntentFilters"); }
    public void clearInstantAppCookie() { throw ServiceMethodMissing.fail("packageManager", "clearInstantAppCookie"); }
    public void clearPackagePreferredActivities(java.lang.String p0) { throw ServiceMethodMissing.fail("packageManager", "clearPackagePreferredActivities"); }
    public java.lang.String[] currentToCanonicalPackageNames(java.lang.String[] p0) { throw ServiceMethodMissing.fail("packageManager", "currentToCanonicalPackageNames"); }
    public void deleteApplicationCacheFiles(java.lang.String p0, android.content.pm.IPackageDataObserver p1) { throw ServiceMethodMissing.fail("packageManager", "deleteApplicationCacheFiles"); }
    public void deleteApplicationCacheFilesAsUser(java.lang.String p0, int p1, android.content.pm.IPackageDataObserver p2) { throw ServiceMethodMissing.fail("packageManager", "deleteApplicationCacheFilesAsUser"); }
    public void deletePackage(java.lang.String p0, android.content.pm.IPackageDeleteObserver p1, int p2) { throw ServiceMethodMissing.fail("packageManager", "deletePackage"); }
    public void deletePackageAsUser(java.lang.String p0, android.content.pm.IPackageDeleteObserver p1, int p2, int p3) { throw ServiceMethodMissing.fail("packageManager", "deletePackageAsUser"); }
    public void extendVerificationTimeout(int p0, int p1, long p2) { throw ServiceMethodMissing.fail("packageManager", "extendVerificationTimeout"); }
    public void flushPackageRestrictionsAsUser(int p0) { throw ServiceMethodMissing.fail("packageManager", "flushPackageRestrictionsAsUser"); }
    public void freeStorage(java.lang.String p0, long p1, android.content.IntentSender p2) { throw ServiceMethodMissing.fail("packageManager", "freeStorage"); }
    public void freeStorageAndNotify(java.lang.String p0, long p1, android.content.pm.IPackageDataObserver p2) { throw ServiceMethodMissing.fail("packageManager", "freeStorageAndNotify"); }
    public android.graphics.drawable.Drawable getActivityBanner(android.content.ComponentName p0) { throw ServiceMethodMissing.fail("packageManager", "getActivityBanner"); }
    public android.graphics.drawable.Drawable getActivityBanner(android.content.Intent p0) { throw ServiceMethodMissing.fail("packageManager", "getActivityBanner"); }
    public android.graphics.drawable.Drawable getActivityIcon(android.content.ComponentName p0) { throw ServiceMethodMissing.fail("packageManager", "getActivityIcon"); }
    public android.graphics.drawable.Drawable getActivityIcon(android.content.Intent p0) { throw ServiceMethodMissing.fail("packageManager", "getActivityIcon"); }
    public android.graphics.drawable.Drawable getActivityLogo(android.content.ComponentName p0) { throw ServiceMethodMissing.fail("packageManager", "getActivityLogo"); }
    public android.graphics.drawable.Drawable getActivityLogo(android.content.Intent p0) { throw ServiceMethodMissing.fail("packageManager", "getActivityLogo"); }
    public java.util.List getAllIntentFilters(java.lang.String p0) { throw ServiceMethodMissing.fail("packageManager", "getAllIntentFilters"); }
    public java.util.List getAllPermissionGroups(int p0) { throw ServiceMethodMissing.fail("packageManager", "getAllPermissionGroups"); }
    public android.graphics.drawable.Drawable getApplicationBanner(android.content.pm.ApplicationInfo p0) { throw ServiceMethodMissing.fail("packageManager", "getApplicationBanner"); }
    public android.graphics.drawable.Drawable getApplicationBanner(java.lang.String p0) { throw ServiceMethodMissing.fail("packageManager", "getApplicationBanner"); }
    public int getApplicationEnabledSetting(java.lang.String p0) { throw ServiceMethodMissing.fail("packageManager", "getApplicationEnabledSetting"); }
    public boolean getApplicationHiddenSettingAsUser(java.lang.String p0, android.os.UserHandle p1) { throw ServiceMethodMissing.fail("packageManager", "getApplicationHiddenSettingAsUser"); }
    public android.graphics.drawable.Drawable getApplicationIcon(android.content.pm.ApplicationInfo p0) { throw ServiceMethodMissing.fail("packageManager", "getApplicationIcon"); }
    public android.graphics.drawable.Drawable getApplicationIcon(java.lang.String p0) { throw ServiceMethodMissing.fail("packageManager", "getApplicationIcon"); }
    public android.content.pm.ApplicationInfo getApplicationInfoAsUser(java.lang.String p0, int p1, int p2) { throw ServiceMethodMissing.fail("packageManager", "getApplicationInfoAsUser"); }
    public java.lang.CharSequence getApplicationLabel(android.content.pm.ApplicationInfo p0) { throw ServiceMethodMissing.fail("packageManager", "getApplicationLabel"); }
    public android.graphics.drawable.Drawable getApplicationLogo(android.content.pm.ApplicationInfo p0) { throw ServiceMethodMissing.fail("packageManager", "getApplicationLogo"); }
    public android.graphics.drawable.Drawable getApplicationLogo(java.lang.String p0) { throw ServiceMethodMissing.fail("packageManager", "getApplicationLogo"); }
    public android.content.Intent getCarLaunchIntentForPackage(java.lang.String p0) { throw ServiceMethodMissing.fail("packageManager", "getCarLaunchIntentForPackage"); }
    public android.content.pm.ChangedPackages getChangedPackages(int p0) { throw ServiceMethodMissing.fail("packageManager", "getChangedPackages"); }
    public int getComponentEnabledSetting(android.content.ComponentName p0) { throw ServiceMethodMissing.fail("packageManager", "getComponentEnabledSetting"); }
    public android.graphics.drawable.Drawable getDefaultActivityIcon() { throw ServiceMethodMissing.fail("packageManager", "getDefaultActivityIcon"); }
    public java.lang.String getDefaultBrowserPackageNameAsUser(int p0) { throw ServiceMethodMissing.fail("packageManager", "getDefaultBrowserPackageNameAsUser"); }
    public android.graphics.drawable.Drawable getDrawable(java.lang.String p0, int p1, android.content.pm.ApplicationInfo p2) { throw ServiceMethodMissing.fail("packageManager", "getDrawable"); }
    public android.content.ComponentName getHomeActivities(java.util.List p0) { throw ServiceMethodMissing.fail("packageManager", "getHomeActivities"); }
    public int getInstallReason(java.lang.String p0, android.os.UserHandle p1) { throw ServiceMethodMissing.fail("packageManager", "getInstallReason"); }
    public java.util.List getInstalledApplications(int p0) { throw ServiceMethodMissing.fail("packageManager", "getInstalledApplications"); }
    public java.util.List getInstalledApplicationsAsUser(int p0, int p1) { throw ServiceMethodMissing.fail("packageManager", "getInstalledApplicationsAsUser"); }
    public java.util.List getInstalledPackagesAsUser(int p0, int p1) { throw ServiceMethodMissing.fail("packageManager", "getInstalledPackagesAsUser"); }
    public java.lang.String getInstallerPackageName(java.lang.String p0) { throw ServiceMethodMissing.fail("packageManager", "getInstallerPackageName"); }
    public java.lang.String getInstantAppAndroidId(java.lang.String p0, android.os.UserHandle p1) { throw ServiceMethodMissing.fail("packageManager", "getInstantAppAndroidId"); }
    public byte[] getInstantAppCookie() { throw ServiceMethodMissing.fail("packageManager", "getInstantAppCookie"); }
    public int getInstantAppCookieMaxBytes() { throw ServiceMethodMissing.fail("packageManager", "getInstantAppCookieMaxBytes"); }
    public int getInstantAppCookieMaxSize() { throw ServiceMethodMissing.fail("packageManager", "getInstantAppCookieMaxSize"); }
    public android.graphics.drawable.Drawable getInstantAppIcon(java.lang.String p0) { throw ServiceMethodMissing.fail("packageManager", "getInstantAppIcon"); }
    public android.content.ComponentName getInstantAppInstallerComponent() { throw ServiceMethodMissing.fail("packageManager", "getInstantAppInstallerComponent"); }
    public android.content.ComponentName getInstantAppResolverSettingsComponent() { throw ServiceMethodMissing.fail("packageManager", "getInstantAppResolverSettingsComponent"); }
    public java.util.List getInstantApps() { throw ServiceMethodMissing.fail("packageManager", "getInstantApps"); }
    public android.content.pm.InstrumentationInfo getInstrumentationInfo(android.content.ComponentName p0, int p1) { throw ServiceMethodMissing.fail("packageManager", "getInstrumentationInfo"); }
    public java.util.List getIntentFilterVerifications(java.lang.String p0) { throw ServiceMethodMissing.fail("packageManager", "getIntentFilterVerifications"); }
    public int getIntentVerificationStatusAsUser(java.lang.String p0, int p1) { throw ServiceMethodMissing.fail("packageManager", "getIntentVerificationStatusAsUser"); }
    public android.content.pm.KeySet getKeySetByAlias(java.lang.String p0, java.lang.String p1) { throw ServiceMethodMissing.fail("packageManager", "getKeySetByAlias"); }
    public android.content.Intent getLaunchIntentForPackage(java.lang.String p0) { throw ServiceMethodMissing.fail("packageManager", "getLaunchIntentForPackage"); }
    public android.content.Intent getLeanbackLaunchIntentForPackage(java.lang.String p0) { throw ServiceMethodMissing.fail("packageManager", "getLeanbackLaunchIntentForPackage"); }
    public int getMoveStatus(int p0) { throw ServiceMethodMissing.fail("packageManager", "getMoveStatus"); }
    public java.lang.String getNameForUid(int p0) { throw ServiceMethodMissing.fail("packageManager", "getNameForUid"); }
    public java.lang.String[] getNamesForUids(int[] p0) { throw ServiceMethodMissing.fail("packageManager", "getNamesForUids"); }
    public java.util.List getPackageCandidateVolumes(android.content.pm.ApplicationInfo p0) { throw ServiceMethodMissing.fail("packageManager", "getPackageCandidateVolumes"); }
    public android.os.storage.VolumeInfo getPackageCurrentVolume(android.content.pm.ApplicationInfo p0) { throw ServiceMethodMissing.fail("packageManager", "getPackageCurrentVolume"); }
    public int[] getPackageGids(java.lang.String p0) { throw ServiceMethodMissing.fail("packageManager", "getPackageGids"); }
    public int[] getPackageGids(java.lang.String p0, int p1) { throw ServiceMethodMissing.fail("packageManager", "getPackageGids"); }
    public android.content.pm.PackageInfo getPackageInfo(android.content.pm.VersionedPackage p0, int p1) { throw ServiceMethodMissing.fail("packageManager", "getPackageInfo"); }
    public android.content.pm.PackageInfo getPackageInfoAsUser(java.lang.String p0, int p1, int p2) { throw ServiceMethodMissing.fail("packageManager", "getPackageInfoAsUser"); }
    public android.content.pm.PackageInstaller getPackageInstaller() { throw ServiceMethodMissing.fail("packageManager", "getPackageInstaller"); }
    public void getPackageSizeInfoAsUser(java.lang.String p0, int p1, android.content.pm.IPackageStatsObserver p2) { throw ServiceMethodMissing.fail("packageManager", "getPackageSizeInfoAsUser"); }
    public int getPackageUid(java.lang.String p0, int p1) { throw ServiceMethodMissing.fail("packageManager", "getPackageUid"); }
    public int getPackageUidAsUser(java.lang.String p0, int p1) { throw ServiceMethodMissing.fail("packageManager", "getPackageUidAsUser"); }
    public int getPackageUidAsUser(java.lang.String p0, int p1, int p2) { throw ServiceMethodMissing.fail("packageManager", "getPackageUidAsUser"); }
    public java.lang.String[] getPackagesForUid(int p0) { throw ServiceMethodMissing.fail("packageManager", "getPackagesForUid"); }
    public java.util.List getPackagesHoldingPermissions(java.lang.String[] p0, int p1) { throw ServiceMethodMissing.fail("packageManager", "getPackagesHoldingPermissions"); }
    public int getPermissionFlags(java.lang.String p0, java.lang.String p1, android.os.UserHandle p2) { throw ServiceMethodMissing.fail("packageManager", "getPermissionFlags"); }
    public android.content.pm.PermissionGroupInfo getPermissionGroupInfo(java.lang.String p0, int p1) { throw ServiceMethodMissing.fail("packageManager", "getPermissionGroupInfo"); }
    public android.content.pm.PermissionInfo getPermissionInfo(java.lang.String p0, int p1) { throw ServiceMethodMissing.fail("packageManager", "getPermissionInfo"); }
    public int getPreferredActivities(java.util.List p0, java.util.List p1, java.lang.String p2) { throw ServiceMethodMissing.fail("packageManager", "getPreferredActivities"); }
    public java.util.List getPreferredPackages(int p0) { throw ServiceMethodMissing.fail("packageManager", "getPreferredPackages"); }
    public java.util.List getPrimaryStorageCandidateVolumes() { throw ServiceMethodMissing.fail("packageManager", "getPrimaryStorageCandidateVolumes"); }
    public android.os.storage.VolumeInfo getPrimaryStorageCurrentVolume() { throw ServiceMethodMissing.fail("packageManager", "getPrimaryStorageCurrentVolume"); }
    public android.content.pm.ProviderInfo getProviderInfo(android.content.ComponentName p0, int p1) { throw ServiceMethodMissing.fail("packageManager", "getProviderInfo"); }
    public android.content.pm.ActivityInfo getReceiverInfo(android.content.ComponentName p0, int p1) { throw ServiceMethodMissing.fail("packageManager", "getReceiverInfo"); }
    public android.content.res.Resources getResourcesForActivity(android.content.ComponentName p0) { throw ServiceMethodMissing.fail("packageManager", "getResourcesForActivity"); }
    public android.content.res.Resources getResourcesForApplication(android.content.pm.ApplicationInfo p0) { throw ServiceMethodMissing.fail("packageManager", "getResourcesForApplication"); }
    public android.content.res.Resources getResourcesForApplication(java.lang.String p0) { throw ServiceMethodMissing.fail("packageManager", "getResourcesForApplication"); }
    public android.content.res.Resources getResourcesForApplicationAsUser(java.lang.String p0, int p1) { throw ServiceMethodMissing.fail("packageManager", "getResourcesForApplicationAsUser"); }
    public java.lang.String getServicesSystemSharedLibraryPackageName() { throw ServiceMethodMissing.fail("packageManager", "getServicesSystemSharedLibraryPackageName"); }
    public java.util.List getSharedLibraries(int p0) { throw ServiceMethodMissing.fail("packageManager", "getSharedLibraries"); }
    public java.util.List getSharedLibrariesAsUser(int p0, int p1) { throw ServiceMethodMissing.fail("packageManager", "getSharedLibrariesAsUser"); }
    public java.lang.String getSharedSystemSharedLibraryPackageName() { throw ServiceMethodMissing.fail("packageManager", "getSharedSystemSharedLibraryPackageName"); }
    public android.content.pm.KeySet getSigningKeySet(java.lang.String p0) { throw ServiceMethodMissing.fail("packageManager", "getSigningKeySet"); }
    public android.content.pm.FeatureInfo[] getSystemAvailableFeatures() { throw ServiceMethodMissing.fail("packageManager", "getSystemAvailableFeatures"); }
    public java.lang.String[] getSystemSharedLibraryNames() { throw ServiceMethodMissing.fail("packageManager", "getSystemSharedLibraryNames"); }
    public java.lang.CharSequence getText(java.lang.String p0, int p1, android.content.pm.ApplicationInfo p2) { throw ServiceMethodMissing.fail("packageManager", "getText"); }
    public int getUidForSharedUser(java.lang.String p0) { throw ServiceMethodMissing.fail("packageManager", "getUidForSharedUser"); }
    public android.graphics.drawable.Drawable getUserBadgeForDensity(android.os.UserHandle p0, int p1) { throw ServiceMethodMissing.fail("packageManager", "getUserBadgeForDensity"); }
    public android.graphics.drawable.Drawable getUserBadgeForDensityNoBackground(android.os.UserHandle p0, int p1) { throw ServiceMethodMissing.fail("packageManager", "getUserBadgeForDensityNoBackground"); }
    public android.graphics.drawable.Drawable getUserBadgedDrawableForDensity(android.graphics.drawable.Drawable p0, android.os.UserHandle p1, android.graphics.Rect p2, int p3) { throw ServiceMethodMissing.fail("packageManager", "getUserBadgedDrawableForDensity"); }
    public android.graphics.drawable.Drawable getUserBadgedIcon(android.graphics.drawable.Drawable p0, android.os.UserHandle p1) { throw ServiceMethodMissing.fail("packageManager", "getUserBadgedIcon"); }
    public java.lang.CharSequence getUserBadgedLabel(java.lang.CharSequence p0, android.os.UserHandle p1) { throw ServiceMethodMissing.fail("packageManager", "getUserBadgedLabel"); }
    public android.content.pm.VerifierDeviceIdentity getVerifierDeviceIdentity() { throw ServiceMethodMissing.fail("packageManager", "getVerifierDeviceIdentity"); }
    public android.content.res.XmlResourceParser getXml(java.lang.String p0, int p1, android.content.pm.ApplicationInfo p2) { throw ServiceMethodMissing.fail("packageManager", "getXml"); }
    public void grantRuntimePermission(java.lang.String p0, java.lang.String p1, android.os.UserHandle p2) { throw ServiceMethodMissing.fail("packageManager", "grantRuntimePermission"); }
    public int installExistingPackage(java.lang.String p0) { throw ServiceMethodMissing.fail("packageManager", "installExistingPackage"); }
    public int installExistingPackage(java.lang.String p0, int p1) { throw ServiceMethodMissing.fail("packageManager", "installExistingPackage"); }
    public int installExistingPackageAsUser(java.lang.String p0, int p1) { throw ServiceMethodMissing.fail("packageManager", "installExistingPackageAsUser"); }
    public boolean isInstantApp() { throw ServiceMethodMissing.fail("packageManager", "isInstantApp"); }
    public boolean isInstantApp(java.lang.String p0) { throw ServiceMethodMissing.fail("packageManager", "isInstantApp"); }
    public boolean isPackageAvailable(java.lang.String p0) { throw ServiceMethodMissing.fail("packageManager", "isPackageAvailable"); }
    public boolean isPackageSuspendedForUser(java.lang.String p0, int p1) { throw ServiceMethodMissing.fail("packageManager", "isPackageSuspendedForUser"); }
    public boolean isPermissionRevokedByPolicy(java.lang.String p0, java.lang.String p1) { throw ServiceMethodMissing.fail("packageManager", "isPermissionRevokedByPolicy"); }
    public boolean isSafeMode() { throw ServiceMethodMissing.fail("packageManager", "isSafeMode"); }
    public boolean isSignedBy(java.lang.String p0, android.content.pm.KeySet p1) { throw ServiceMethodMissing.fail("packageManager", "isSignedBy"); }
    public boolean isSignedByExactly(java.lang.String p0, android.content.pm.KeySet p1) { throw ServiceMethodMissing.fail("packageManager", "isSignedByExactly"); }
    public boolean isUpgrade() { throw ServiceMethodMissing.fail("packageManager", "isUpgrade"); }
    public boolean isWirelessConsentModeEnabled() { throw ServiceMethodMissing.fail("packageManager", "isWirelessConsentModeEnabled"); }
    public android.graphics.drawable.Drawable loadItemIcon(android.content.pm.PackageItemInfo p0, android.content.pm.ApplicationInfo p1) { throw ServiceMethodMissing.fail("packageManager", "loadItemIcon"); }
    public android.graphics.drawable.Drawable loadUnbadgedItemIcon(android.content.pm.PackageItemInfo p0, android.content.pm.ApplicationInfo p1) { throw ServiceMethodMissing.fail("packageManager", "loadUnbadgedItemIcon"); }
    public int movePackage(java.lang.String p0, android.os.storage.VolumeInfo p1) { throw ServiceMethodMissing.fail("packageManager", "movePackage"); }
    public int movePrimaryStorage(android.os.storage.VolumeInfo p0) { throw ServiceMethodMissing.fail("packageManager", "movePrimaryStorage"); }
    public java.util.List queryBroadcastReceivers(android.content.Intent p0, int p1) { throw ServiceMethodMissing.fail("packageManager", "queryBroadcastReceivers"); }
    public java.util.List queryBroadcastReceiversAsUser(android.content.Intent p0, int p1, int p2) { throw ServiceMethodMissing.fail("packageManager", "queryBroadcastReceiversAsUser"); }
    public java.util.List queryContentProviders(java.lang.String p0, int p1, int p2) { throw ServiceMethodMissing.fail("packageManager", "queryContentProviders"); }
    public java.util.List queryInstrumentation(java.lang.String p0, int p1) { throw ServiceMethodMissing.fail("packageManager", "queryInstrumentation"); }
    public java.util.List queryIntentActivitiesAsUser(android.content.Intent p0, int p1, int p2) { throw ServiceMethodMissing.fail("packageManager", "queryIntentActivitiesAsUser"); }
    public java.util.List queryIntentActivityOptions(android.content.ComponentName p0, android.content.Intent[] p1, android.content.Intent p2, int p3) { throw ServiceMethodMissing.fail("packageManager", "queryIntentActivityOptions"); }
    public java.util.List queryIntentContentProviders(android.content.Intent p0, int p1) { throw ServiceMethodMissing.fail("packageManager", "queryIntentContentProviders"); }
    public java.util.List queryIntentContentProvidersAsUser(android.content.Intent p0, int p1, int p2) { throw ServiceMethodMissing.fail("packageManager", "queryIntentContentProvidersAsUser"); }
    public java.util.List queryIntentServices(android.content.Intent p0, int p1) { throw ServiceMethodMissing.fail("packageManager", "queryIntentServices"); }
    public java.util.List queryIntentServicesAsUser(android.content.Intent p0, int p1, int p2) { throw ServiceMethodMissing.fail("packageManager", "queryIntentServicesAsUser"); }
    public java.util.List queryPermissionsByGroup(java.lang.String p0, int p1) { throw ServiceMethodMissing.fail("packageManager", "queryPermissionsByGroup"); }
    public void registerDexModule(java.lang.String p0, android.content.pm.PackageManager.DexModuleRegisterCallback p1) { throw ServiceMethodMissing.fail("packageManager", "registerDexModule"); }
    public void registerMoveCallback(android.content.pm.PackageManager.MoveCallback p0, android.os.Handler p1) { throw ServiceMethodMissing.fail("packageManager", "registerMoveCallback"); }
    public void removeOnPermissionsChangeListener(android.content.pm.PackageManager.OnPermissionsChangedListener p0) { throw ServiceMethodMissing.fail("packageManager", "removeOnPermissionsChangeListener"); }
    public void removePackageFromPreferred(java.lang.String p0) { throw ServiceMethodMissing.fail("packageManager", "removePackageFromPreferred"); }
    public void removePermission(java.lang.String p0) { throw ServiceMethodMissing.fail("packageManager", "removePermission"); }
    public void replacePreferredActivity(android.content.IntentFilter p0, int p1, android.content.ComponentName[] p2, android.content.ComponentName p3) { throw ServiceMethodMissing.fail("packageManager", "replacePreferredActivity"); }
    public android.content.pm.ResolveInfo resolveActivityAsUser(android.content.Intent p0, int p1, int p2) { throw ServiceMethodMissing.fail("packageManager", "resolveActivityAsUser"); }
    public android.content.pm.ProviderInfo resolveContentProvider(java.lang.String p0, int p1) { throw ServiceMethodMissing.fail("packageManager", "resolveContentProvider"); }
    public android.content.pm.ProviderInfo resolveContentProviderAsUser(java.lang.String p0, int p1, int p2) { throw ServiceMethodMissing.fail("packageManager", "resolveContentProviderAsUser"); }
    public android.content.pm.ResolveInfo resolveServiceAsUser(android.content.Intent p0, int p1, int p2) { throw ServiceMethodMissing.fail("packageManager", "resolveServiceAsUser"); }
    public void revokeRuntimePermission(java.lang.String p0, java.lang.String p1, android.os.UserHandle p2) { throw ServiceMethodMissing.fail("packageManager", "revokeRuntimePermission"); }
    public void setApplicationCategoryHint(java.lang.String p0, int p1) { throw ServiceMethodMissing.fail("packageManager", "setApplicationCategoryHint"); }
    public void setApplicationEnabledSetting(java.lang.String p0, int p1, int p2) { throw ServiceMethodMissing.fail("packageManager", "setApplicationEnabledSetting"); }
    public boolean setApplicationHiddenSettingAsUser(java.lang.String p0, boolean p1, android.os.UserHandle p2) { throw ServiceMethodMissing.fail("packageManager", "setApplicationHiddenSettingAsUser"); }
    public void setComponentEnabledSetting(android.content.ComponentName p0, int p1, int p2) { throw ServiceMethodMissing.fail("packageManager", "setComponentEnabledSetting"); }
    public boolean setDefaultBrowserPackageNameAsUser(java.lang.String p0, int p1) { throw ServiceMethodMissing.fail("packageManager", "setDefaultBrowserPackageNameAsUser"); }
    public void setInstallerPackageName(java.lang.String p0, java.lang.String p1) { throw ServiceMethodMissing.fail("packageManager", "setInstallerPackageName"); }
    public boolean setInstantAppCookie(byte[] p0) { throw ServiceMethodMissing.fail("packageManager", "setInstantAppCookie"); }
    public void setUpdateAvailable(java.lang.String p0, boolean p1) { throw ServiceMethodMissing.fail("packageManager", "setUpdateAvailable"); }
    public boolean shouldShowRequestPermissionRationale(java.lang.String p0) { throw ServiceMethodMissing.fail("packageManager", "shouldShowRequestPermissionRationale"); }
    public void unregisterMoveCallback(android.content.pm.PackageManager.MoveCallback p0) { throw ServiceMethodMissing.fail("packageManager", "unregisterMoveCallback"); }
    public void updateInstantAppCookie(byte[] p0) { throw ServiceMethodMissing.fail("packageManager", "updateInstantAppCookie"); }
    public boolean updateIntentVerificationStatusAsUser(java.lang.String p0, int p1, int p2) { throw ServiceMethodMissing.fail("packageManager", "updateIntentVerificationStatusAsUser"); }
    public void updatePermissionFlags(java.lang.String p0, java.lang.String p1, int p2, int p3, android.os.UserHandle p4) { throw ServiceMethodMissing.fail("packageManager", "updatePermissionFlags"); }
    public void verifyIntentFilter(int p0, int p1, java.util.List p2) { throw ServiceMethodMissing.fail("packageManager", "verifyIntentFilter"); }
    public void verifyPendingInstall(int p0, int p1) { throw ServiceMethodMissing.fail("packageManager", "verifyPendingInstall"); }
}
