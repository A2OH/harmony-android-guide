// SPDX-License-Identifier: Apache-2.0
//
// Westlake M4c -- shim/java/android/content/pm/IPackageManager.java
//
// COMPILE-TIME STUB for android.content.pm.IPackageManager.  AOSP marks
// the real interface @hide so it isn't in the public SDK android.jar;
// this stub supplies just enough surface for the Westlake shim to
// compile against the same AIDL methods that framework.jar's
// IPackageManager declares.
//
// At RUNTIME this class is stripped from aosp-shim.dex by the entry in
// scripts/framework_duplicates.txt -- framework.jar's real
// IPackageManager.Stub wins, and WestlakePackageManagerService is loaded
// as a subclass of the real Stub.  Bytecode compatibility relies on
// (a) identical FQCN `android.content.pm.IPackageManager$Stub`, (b)
// Stub being abstract and extending android.os.Binder, and (c) all 223
// method signatures matching the Android 16 IPackageManager surface.
//
// Method count: 223 declared abstract methods, matching the
// android.content.pm.IPackageManager class on Android 16's
// framework.jar (dexdump from the deployed phone, 2026-05-12).
//
// If you add/remove methods here, also update framework_duplicates.txt.
//
// PermissionEnforcer ctor: IPackageManager.Stub provides BOTH a
// deprecated no-arg ctor (which calls
// ActivityThread.currentActivityThread().getSystemContext() and NPEs in
// our sandbox) AND a Stub(PermissionEnforcer) ctor.
// WestlakePackageManagerService uses the second form with a no-op
// enforcer, same as M4a/M4b/M4-power.

package android.content.pm;

import android.os.IBinder;
import android.os.IInterface;
import android.os.PermissionEnforcer;
import android.os.RemoteException;

public interface IPackageManager extends IInterface {

    static final String DESCRIPTOR = "android.content.pm.IPackageManager";

    // --- 223 abstract methods (generated from dexdump on framework.jar Android 16) ---

    boolean activitySupportsIntentAsUser(android.content.ComponentName p0, android.content.Intent p1, java.lang.String p2, int p3) throws android.os.RemoteException;
    void addCrossProfileIntentFilter(android.content.IntentFilter p0, java.lang.String p1, int p2, int p3, int p4) throws android.os.RemoteException;
    boolean addPermission(android.content.pm.PermissionInfo p0) throws android.os.RemoteException;
    boolean addPermissionAsync(android.content.pm.PermissionInfo p0) throws android.os.RemoteException;
    void addPersistentPreferredActivity(android.content.IntentFilter p0, android.content.ComponentName p1, int p2) throws android.os.RemoteException;
    void addPreferredActivity(android.content.IntentFilter p0, int p1, android.content.ComponentName[] p2, android.content.ComponentName p3, int p4, boolean p5) throws android.os.RemoteException;
    boolean canForwardTo(android.content.Intent p0, java.lang.String p1, int p2, int p3) throws android.os.RemoteException;
    boolean[] canPackageQuery(java.lang.String p0, java.lang.String[] p1, int p2) throws android.os.RemoteException;
    boolean canRequestPackageInstalls(java.lang.String p0, int p1) throws android.os.RemoteException;
    java.lang.String[] canonicalToCurrentPackageNames(java.lang.String[] p0) throws android.os.RemoteException;
    void checkPackageStartable(java.lang.String p0, int p1) throws android.os.RemoteException;
    int checkPermission(java.lang.String p0, java.lang.String p1, int p2) throws android.os.RemoteException;
    int checkSignatures(java.lang.String p0, java.lang.String p1, int p2) throws android.os.RemoteException;
    int checkUidPermission(java.lang.String p0, int p1) throws android.os.RemoteException;
    int checkUidSignatures(int p0, int p1) throws android.os.RemoteException;
    void clearApplicationProfileData(java.lang.String p0) throws android.os.RemoteException;
    void clearApplicationUserData(java.lang.String p0, android.content.pm.IPackageDataObserver p1, int p2) throws android.os.RemoteException;
    void clearCrossProfileIntentFilters(int p0, java.lang.String p1) throws android.os.RemoteException;
    void clearPackagePersistentPreferredActivities(java.lang.String p0, int p1) throws android.os.RemoteException;
    void clearPackagePreferredActivities(java.lang.String p0) throws android.os.RemoteException;
    void clearPersistentPreferredActivity(android.content.IntentFilter p0, int p1) throws android.os.RemoteException;
    java.lang.String[] currentToCanonicalPackageNames(java.lang.String[] p0) throws android.os.RemoteException;
    void deleteApplicationCacheFiles(java.lang.String p0, android.content.pm.IPackageDataObserver p1) throws android.os.RemoteException;
    void deleteApplicationCacheFilesAsUser(java.lang.String p0, int p1, android.content.pm.IPackageDataObserver p2) throws android.os.RemoteException;
    void deleteExistingPackageAsUser(android.content.pm.VersionedPackage p0, android.content.pm.IPackageDeleteObserver2 p1, int p2) throws android.os.RemoteException;
    void deletePackageAsUser(java.lang.String p0, int p1, android.content.pm.IPackageDeleteObserver p2, int p3, int p4) throws android.os.RemoteException;
    void deletePackageVersioned(android.content.pm.VersionedPackage p0, android.content.pm.IPackageDeleteObserver2 p1, int p2, int p3) throws android.os.RemoteException;
    void deletePreloadsFileCache() throws android.os.RemoteException;
    void enterSafeMode() throws android.os.RemoteException;
    void extendVerificationTimeout(int p0, int p1, long p2) throws android.os.RemoteException;
    android.content.pm.ResolveInfo findPersistentPreferredActivity(android.content.Intent p0, int p1) throws android.os.RemoteException;
    void finishPackageInstall(int p0, boolean p1) throws android.os.RemoteException;
    void flushPackageRestrictionsAsUser(int p0) throws android.os.RemoteException;
    void freeStorage(java.lang.String p0, long p1, int p2, android.content.IntentSender p3) throws android.os.RemoteException;
    void freeStorageAndNotify(java.lang.String p0, long p1, int p2, android.content.pm.IPackageDataObserver p3) throws android.os.RemoteException;
    android.content.pm.ActivityInfo getActivityInfo(android.content.ComponentName p0, long p1, int p2) throws android.os.RemoteException;
    java.util.List getAllApexDirectories() throws android.os.RemoteException;
    android.content.pm.ParceledListSlice getAllIntentFilters(java.lang.String p0) throws android.os.RemoteException;
    java.util.List getAllPackages() throws android.os.RemoteException;
    android.os.ParcelFileDescriptor getAppMetadataFd(java.lang.String p0, int p1) throws android.os.RemoteException;
    int getAppMetadataSource(java.lang.String p0, int p1) throws android.os.RemoteException;
    java.lang.String[] getAppOpPermissionPackages(java.lang.String p0, int p1) throws android.os.RemoteException;
    java.lang.String getAppPredictionServicePackageName() throws android.os.RemoteException;
    int getApplicationEnabledSetting(java.lang.String p0, int p1) throws android.os.RemoteException;
    boolean getApplicationHiddenSettingAsUser(java.lang.String p0, int p1) throws android.os.RemoteException;
    android.content.pm.ApplicationInfo getApplicationInfo(java.lang.String p0, long p1, int p2) throws android.os.RemoteException;
    android.graphics.Bitmap getArchivedAppIcon(java.lang.String p0, android.os.UserHandle p1, java.lang.String p2) throws android.os.RemoteException;
    android.content.pm.ArchivedPackageParcel getArchivedPackage(java.lang.String p0, int p1) throws android.os.RemoteException;
    android.content.pm.dex.IArtManager getArtManager() throws android.os.RemoteException;
    java.lang.String getAttentionServicePackageName() throws android.os.RemoteException;
    boolean getBlockUninstallForUser(java.lang.String p0, int p1) throws android.os.RemoteException;
    android.content.pm.ChangedPackages getChangedPackages(int p0, int p1) throws android.os.RemoteException;
    int getComponentEnabledSetting(android.content.ComponentName p0, int p1) throws android.os.RemoteException;
    android.content.pm.ParceledListSlice getDeclaredSharedLibraries(java.lang.String p0, long p1, int p2) throws android.os.RemoteException;
    byte[] getDefaultAppsBackup(int p0) throws android.os.RemoteException;
    java.lang.String getDefaultTextClassifierPackageName() throws android.os.RemoteException;
    android.content.ComponentName getDomainVerificationAgent(int p0) throws android.os.RemoteException;
    byte[] getDomainVerificationBackup(int p0) throws android.os.RemoteException;
    int getFlagsForUid(int p0) throws android.os.RemoteException;
    java.lang.CharSequence getHarmfulAppWarning(java.lang.String p0, int p1) throws android.os.RemoteException;
    android.os.IBinder getHoldLockToken() throws android.os.RemoteException;
    android.content.ComponentName getHomeActivities(java.util.List p0) throws android.os.RemoteException;
    java.lang.String getIncidentReportApproverPackageName() throws android.os.RemoteException;
    java.util.List getInitialNonStoppedSystemPackages() throws android.os.RemoteException;
    int getInstallLocation() throws android.os.RemoteException;
    int getInstallReason(java.lang.String p0, int p1) throws android.os.RemoteException;
    android.content.pm.InstallSourceInfo getInstallSourceInfo(java.lang.String p0, int p1) throws android.os.RemoteException;
    android.content.pm.ParceledListSlice getInstalledApplications(long p0, int p1) throws android.os.RemoteException;
    java.util.List getInstalledModules(int p0) throws android.os.RemoteException;
    android.content.pm.ParceledListSlice getInstalledPackages(long p0, int p1) throws android.os.RemoteException;
    java.lang.String getInstallerPackageName(java.lang.String p0) throws android.os.RemoteException;
    java.lang.String getInstantAppAndroidId(java.lang.String p0, int p1) throws android.os.RemoteException;
    byte[] getInstantAppCookie(java.lang.String p0, int p1) throws android.os.RemoteException;
    android.graphics.Bitmap getInstantAppIcon(java.lang.String p0, int p1) throws android.os.RemoteException;
    android.content.ComponentName getInstantAppInstallerComponent() throws android.os.RemoteException;
    android.content.ComponentName getInstantAppResolverComponent() throws android.os.RemoteException;
    android.content.ComponentName getInstantAppResolverSettingsComponent() throws android.os.RemoteException;
    android.content.pm.ParceledListSlice getInstantApps(int p0) throws android.os.RemoteException;
    android.content.pm.InstrumentationInfo getInstrumentationInfoAsUser(android.content.ComponentName p0, int p1, int p2) throws android.os.RemoteException;
    android.content.pm.ParceledListSlice getIntentFilterVerifications(java.lang.String p0) throws android.os.RemoteException;
    int getIntentVerificationStatus(java.lang.String p0, int p1) throws android.os.RemoteException;
    android.content.pm.KeySet getKeySetByAlias(java.lang.String p0, java.lang.String p1) throws android.os.RemoteException;
    android.content.pm.ResolveInfo getLastChosenActivity(android.content.Intent p0, java.lang.String p1, int p2) throws android.os.RemoteException;
    android.content.IntentSender getLaunchIntentSenderForPackage(java.lang.String p0, java.lang.String p1, java.lang.String p2, int p3) throws android.os.RemoteException;
    java.util.List getMimeGroup(java.lang.String p0, java.lang.String p1) throws android.os.RemoteException;
    android.content.pm.ModuleInfo getModuleInfo(java.lang.String p0, int p1) throws android.os.RemoteException;
    int getMoveStatus(int p0) throws android.os.RemoteException;
    java.lang.String getNameForUid(int p0) throws android.os.RemoteException;
    java.lang.String[] getNamesForUids(int[] p0) throws android.os.RemoteException;
    int[] getPackageGids(java.lang.String p0, long p1, int p2) throws android.os.RemoteException;
    android.content.pm.PackageInfo getPackageInfo(java.lang.String p0, long p1, int p2) throws android.os.RemoteException;
    android.content.pm.PackageInfo getPackageInfoVersioned(android.content.pm.VersionedPackage p0, long p1, int p2) throws android.os.RemoteException;
    android.content.pm.IPackageInstaller getPackageInstaller() throws android.os.RemoteException;
    void getPackageSizeInfo(java.lang.String p0, int p1, android.content.pm.IPackageStatsObserver p2) throws android.os.RemoteException;
    int getPackageUid(java.lang.String p0, long p1, int p2) throws android.os.RemoteException;
    java.lang.String[] getPackagesForUid(int p0) throws android.os.RemoteException;
    android.content.pm.ParceledListSlice getPackagesHoldingPermissions(java.lang.String[] p0, long p1, int p2) throws android.os.RemoteException;
    java.lang.String getPageSizeCompatWarningMessage(java.lang.String p0) throws android.os.RemoteException;
    java.lang.String getPermissionControllerPackageName() throws android.os.RemoteException;
    android.content.pm.PermissionGroupInfo getPermissionGroupInfo(java.lang.String p0, int p1) throws android.os.RemoteException;
    android.content.pm.ParceledListSlice getPersistentApplications(int p0) throws android.os.RemoteException;
    int getPreferredActivities(java.util.List p0, java.util.List p1, java.lang.String p2) throws android.os.RemoteException;
    byte[] getPreferredActivityBackup(int p0) throws android.os.RemoteException;
    int getPrivateFlagsForUid(int p0) throws android.os.RemoteException;
    android.content.pm.PackageManager.Property getPropertyAsUser(java.lang.String p0, java.lang.String p1, java.lang.String p2, int p3) throws android.os.RemoteException;
    android.content.pm.ProviderInfo getProviderInfo(android.content.ComponentName p0, long p1, int p2) throws android.os.RemoteException;
    android.content.pm.ActivityInfo getReceiverInfo(android.content.ComponentName p0, long p1, int p2) throws android.os.RemoteException;
    java.lang.String getRotationResolverPackageName() throws android.os.RemoteException;
    int getRuntimePermissionsVersion(int p0) throws android.os.RemoteException;
    java.lang.String getSdkSandboxPackageName() throws android.os.RemoteException;
    android.content.pm.ServiceInfo getServiceInfo(android.content.ComponentName p0, long p1, int p2) throws android.os.RemoteException;
    java.lang.String getServicesSystemSharedLibraryPackageName() throws android.os.RemoteException;
    java.lang.String getSetupWizardPackageName() throws android.os.RemoteException;
    android.content.pm.ParceledListSlice getSharedLibraries(java.lang.String p0, long p1, int p2) throws android.os.RemoteException;
    java.lang.String getSharedSystemSharedLibraryPackageName() throws android.os.RemoteException;
    android.content.pm.KeySet getSigningKeySet(java.lang.String p0) throws android.os.RemoteException;
    java.lang.String getSplashScreenTheme(java.lang.String p0, int p1) throws android.os.RemoteException;
    android.os.Bundle getSuspendedPackageAppExtras(java.lang.String p0, int p1) throws android.os.RemoteException;
    java.lang.String getSuspendingPackage(java.lang.String p0, int p1) throws android.os.RemoteException;
    android.content.pm.ParceledListSlice getSystemAvailableFeatures() throws android.os.RemoteException;
    java.lang.String getSystemCaptionsServicePackageName() throws android.os.RemoteException;
    java.lang.String[] getSystemSharedLibraryNames() throws android.os.RemoteException;
    java.util.Map getSystemSharedLibraryNamesAndPaths() throws android.os.RemoteException;
    java.lang.String getSystemTextClassifierPackageName() throws android.os.RemoteException;
    int getTargetSdkVersion(java.lang.String p0) throws android.os.RemoteException;
    int getUidForSharedUser(java.lang.String p0) throws android.os.RemoteException;
    java.lang.String[] getUnsuspendablePackagesForUser(java.lang.String[] p0, int p1) throws android.os.RemoteException;
    int getUserMinAspectRatio(java.lang.String p0, int p1) throws android.os.RemoteException;
    android.content.pm.VerifierDeviceIdentity getVerifierDeviceIdentity() throws android.os.RemoteException;
    java.lang.String getWellbeingPackageName() throws android.os.RemoteException;
    void grantRuntimePermission(java.lang.String p0, java.lang.String p1, int p2) throws android.os.RemoteException;
    boolean hasSigningCertificate(java.lang.String p0, byte[] p1, int p2) throws android.os.RemoteException;
    boolean hasSystemFeature(java.lang.String p0, int p1) throws android.os.RemoteException;
    boolean hasSystemUidErrors() throws android.os.RemoteException;
    boolean hasUidSigningCertificate(int p0, byte[] p1, int p2) throws android.os.RemoteException;
    void holdLock(android.os.IBinder p0, int p1) throws android.os.RemoteException;
    int installExistingPackageAsUser(java.lang.String p0, int p1, int p2, int p3, java.util.List p4) throws android.os.RemoteException;
    boolean isAppArchivable(java.lang.String p0, android.os.UserHandle p1) throws android.os.RemoteException;
    boolean isAutoRevokeWhitelisted(java.lang.String p0) throws android.os.RemoteException;
    boolean isDeviceUpgrading() throws android.os.RemoteException;
    boolean isFirstBoot() throws android.os.RemoteException;
    boolean isInstantApp(java.lang.String p0, int p1) throws android.os.RemoteException;
    boolean isPackageAvailable(java.lang.String p0, int p1) throws android.os.RemoteException;
    boolean isPackageDeviceAdminOnAnyUser(java.lang.String p0) throws android.os.RemoteException;
    boolean isPackageQuarantinedForUser(java.lang.String p0, int p1) throws android.os.RemoteException;
    boolean isPackageSignedByKeySet(java.lang.String p0, android.content.pm.KeySet p1) throws android.os.RemoteException;
    boolean isPackageSignedByKeySetExactly(java.lang.String p0, android.content.pm.KeySet p1) throws android.os.RemoteException;
    boolean isPackageStateProtected(java.lang.String p0, int p1) throws android.os.RemoteException;
    boolean isPackageStoppedForUser(java.lang.String p0, int p1) throws android.os.RemoteException;
    boolean isPackageSuspendedForUser(java.lang.String p0, int p1) throws android.os.RemoteException;
    boolean isPageSizeCompatEnabled(java.lang.String p0) throws android.os.RemoteException;
    boolean isProtectedBroadcast(java.lang.String p0) throws android.os.RemoteException;
    boolean isSafeMode() throws android.os.RemoteException;
    boolean isStorageLow() throws android.os.RemoteException;
    boolean isUidPrivileged(int p0) throws android.os.RemoteException;
    void logAppProcessStartIfNeeded(java.lang.String p0, java.lang.String p1, int p2, java.lang.String p3, java.lang.String p4, int p5) throws android.os.RemoteException;
    void makeProviderVisible(int p0, java.lang.String p1) throws android.os.RemoteException;
    void makeUidVisible(int p0, int p1) throws android.os.RemoteException;
    int movePackage(java.lang.String p0, java.lang.String p1) throws android.os.RemoteException;
    int movePrimaryStorage(java.lang.String p0) throws android.os.RemoteException;
    void notifyDexLoad(java.lang.String p0, java.util.Map p1, java.lang.String p2) throws android.os.RemoteException;
    void notifyPackageUse(java.lang.String p0, int p1) throws android.os.RemoteException;
    void notifyPackagesReplacedReceived(java.lang.String[] p0) throws android.os.RemoteException;
    void overrideLabelAndIcon(android.content.ComponentName p0, java.lang.String p1, int p2, int p3) throws android.os.RemoteException;
    boolean performDexOptMode(java.lang.String p0, boolean p1, java.lang.String p2, boolean p3, boolean p4, java.lang.String p5) throws android.os.RemoteException;
    boolean performDexOptSecondary(java.lang.String p0, java.lang.String p1, boolean p2) throws android.os.RemoteException;
    android.content.pm.ParceledListSlice queryContentProviders(java.lang.String p0, int p1, long p2, java.lang.String p3) throws android.os.RemoteException;
    android.content.pm.ParceledListSlice queryInstrumentationAsUser(java.lang.String p0, int p1, int p2) throws android.os.RemoteException;
    android.content.pm.ParceledListSlice queryIntentActivities(android.content.Intent p0, java.lang.String p1, long p2, int p3) throws android.os.RemoteException;
    android.content.pm.ParceledListSlice queryIntentActivityOptions(android.content.ComponentName p0, android.content.Intent[] p1, java.lang.String[] p2, android.content.Intent p3, java.lang.String p4, long p5, int p6) throws android.os.RemoteException;
    android.content.pm.ParceledListSlice queryIntentContentProviders(android.content.Intent p0, java.lang.String p1, long p2, int p3) throws android.os.RemoteException;
    android.content.pm.ParceledListSlice queryIntentReceivers(android.content.Intent p0, java.lang.String p1, long p2, int p3) throws android.os.RemoteException;
    android.content.pm.ParceledListSlice queryIntentServices(android.content.Intent p0, java.lang.String p1, long p2, int p3) throws android.os.RemoteException;
    android.content.pm.ParceledListSlice queryProperty(java.lang.String p0, int p1) throws android.os.RemoteException;
    void querySyncProviders(java.util.List p0, java.util.List p1) throws android.os.RemoteException;
    void registerDexModule(java.lang.String p0, java.lang.String p1, boolean p2, android.content.pm.IDexModuleRegisterCallback p3) throws android.os.RemoteException;
    void registerMoveCallback(android.content.pm.IPackageMoveObserver p0) throws android.os.RemoteException;
    void registerPackageMonitorCallback(android.os.IRemoteCallback p0, int p1) throws android.os.RemoteException;
    void relinquishUpdateOwnership(java.lang.String p0) throws android.os.RemoteException;
    boolean removeCrossProfileIntentFilter(android.content.IntentFilter p0, java.lang.String p1, int p2, int p3, int p4) throws android.os.RemoteException;
    void removePermission(java.lang.String p0) throws android.os.RemoteException;
    void replacePreferredActivity(android.content.IntentFilter p0, int p1, android.content.ComponentName[] p2, android.content.ComponentName p3, int p4) throws android.os.RemoteException;
    void requestPackageChecksums(java.lang.String p0, boolean p1, int p2, int p3, java.util.List p4, android.content.pm.IOnChecksumsReadyListener p5, int p6) throws android.os.RemoteException;
    void resetApplicationPreferences(int p0) throws android.os.RemoteException;
    android.content.pm.ProviderInfo resolveContentProvider(java.lang.String p0, long p1, int p2) throws android.os.RemoteException;
    android.content.pm.ResolveInfo resolveIntent(android.content.Intent p0, java.lang.String p1, long p2, int p3) throws android.os.RemoteException;
    android.content.pm.ResolveInfo resolveService(android.content.Intent p0, java.lang.String p1, long p2, int p3) throws android.os.RemoteException;
    void restoreDefaultApps(byte[] p0, int p1) throws android.os.RemoteException;
    void restoreDomainVerification(byte[] p0, int p1) throws android.os.RemoteException;
    void restoreLabelAndIcon(android.content.ComponentName p0, int p1) throws android.os.RemoteException;
    void restorePreferredActivities(byte[] p0, int p1) throws android.os.RemoteException;
    void sendDeviceCustomizationReadyBroadcast() throws android.os.RemoteException;
    void setApplicationCategoryHint(java.lang.String p0, int p1, java.lang.String p2) throws android.os.RemoteException;
    void setApplicationEnabledSetting(java.lang.String p0, int p1, int p2, int p3, java.lang.String p4) throws android.os.RemoteException;
    boolean setApplicationHiddenSettingAsUser(java.lang.String p0, boolean p1, int p2) throws android.os.RemoteException;
    boolean setBlockUninstallForUser(java.lang.String p0, boolean p1, int p2) throws android.os.RemoteException;
    void setComponentEnabledSetting(android.content.ComponentName p0, int p1, int p2, int p3, java.lang.String p4) throws android.os.RemoteException;
    void setComponentEnabledSettings(java.util.List p0, int p1, java.lang.String p2) throws android.os.RemoteException;
    java.lang.String[] setDistractingPackageRestrictionsAsUser(java.lang.String[] p0, int p1, int p2) throws android.os.RemoteException;
    void setHarmfulAppWarning(java.lang.String p0, java.lang.CharSequence p1, int p2) throws android.os.RemoteException;
    void setHomeActivity(android.content.ComponentName p0, int p1) throws android.os.RemoteException;
    boolean setInstallLocation(int p0) throws android.os.RemoteException;
    void setInstallerPackageName(java.lang.String p0, java.lang.String p1) throws android.os.RemoteException;
    boolean setInstantAppCookie(java.lang.String p0, byte[] p1, int p2) throws android.os.RemoteException;
    void setKeepUninstalledPackages(java.util.List p0) throws android.os.RemoteException;
    void setLastChosenActivity(android.content.Intent p0, java.lang.String p1, int p2, android.content.IntentFilter p3, int p4, android.content.ComponentName p5) throws android.os.RemoteException;
    void setMimeGroup(java.lang.String p0, java.lang.String p1, java.util.List p2) throws android.os.RemoteException;
    void setPackageStoppedState(java.lang.String p0, boolean p1, int p2) throws android.os.RemoteException;
    java.lang.String[] setPackagesSuspendedAsUser(java.lang.String[] p0, boolean p1, android.os.PersistableBundle p2, android.os.PersistableBundle p3, android.content.pm.SuspendDialogInfo p4, int p5, java.lang.String p6, int p7, int p8) throws android.os.RemoteException;
    void setPageSizeAppCompatFlagsSettingsOverride(java.lang.String p0, boolean p1) throws android.os.RemoteException;
    boolean setRequiredForSystemUser(java.lang.String p0, boolean p1) throws android.os.RemoteException;
    void setRuntimePermissionsVersion(int p0, int p1) throws android.os.RemoteException;
    void setSplashScreenTheme(java.lang.String p0, java.lang.String p1, int p2) throws android.os.RemoteException;
    void setSystemAppHiddenUntilInstalled(java.lang.String p0, boolean p1) throws android.os.RemoteException;
    boolean setSystemAppInstallState(java.lang.String p0, boolean p1, int p2) throws android.os.RemoteException;
    void setUpdateAvailable(java.lang.String p0, boolean p1) throws android.os.RemoteException;
    void setUserMinAspectRatio(java.lang.String p0, int p1, int p2) throws android.os.RemoteException;
    void unregisterMoveCallback(android.content.pm.IPackageMoveObserver p0) throws android.os.RemoteException;
    void unregisterPackageMonitorCallback(android.os.IRemoteCallback p0) throws android.os.RemoteException;
    boolean updateIntentVerificationStatus(java.lang.String p0, int p1, int p2) throws android.os.RemoteException;
    void verifyIntentFilter(int p0, int p1, java.util.List p2) throws android.os.RemoteException;
    void verifyPendingInstall(int p0, int p1) throws android.os.RemoteException;
    boolean waitForHandler(long p0, boolean p1) throws android.os.RemoteException;

    // --- AIDL-generated Stub abstract class -------------------------------
    //
    // Real Stub in framework.jar extends Binder, implements IPackageManager,
    // and provides onTransact() that dispatches by TRANSACTION_xxx code.
    // Our stub matches that surface exactly.  Because Stub is abstract,
    // subclasses (like WestlakePackageManagerService) must implement every
    // IPackageManager method or also be abstract.
    //
    // At runtime, the framework.jar Stub wins; the asInterface() and
    // attachInterface() wiring used by ServiceManager / queryLocalInterface
    // is the real one.
    //
    // BOTH constructors are exposed: the deprecated no-arg (which the real
    // framework.jar binds to ActivityThread.getSystemContext() and NPEs in
    // our cold-boot sandbox) AND the (PermissionEnforcer) overload that
    // WestlakePackageManagerService actually uses.
    public static abstract class Stub extends android.os.Binder implements IPackageManager {
        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }
        public Stub(PermissionEnforcer enforcer) {
            attachInterface(this, DESCRIPTOR);
            // Real Stub stashes the enforcer; the shim doesn't enforce
            // anything (the WestlakePackageManagerService overrides the
            // Tier-1 methods directly and the others throw via
            // ServiceMethodMissing.fail).
        }
        public static IPackageManager asInterface(IBinder obj) {
            if (obj == null) return null;
            IInterface i = obj.queryLocalInterface(DESCRIPTOR);
            if (i instanceof IPackageManager) return (IPackageManager) i;
            return null;  // shim doesn't implement Proxy
        }
        @Override public IBinder asBinder() { return this; }
    }
}
