// SPDX-License-Identifier: Apache-2.0
//
// Westlake M4e -- shim/java/android/app/INotificationManager.java
//
// COMPILE-TIME STUB for android.app.INotificationManager.  AOSP marks
// the real interface @hide so it isn't in the public SDK android.jar;
// this stub supplies just enough surface for the Westlake shim to
// compile against the same AIDL methods that framework.jar's
// INotificationManager declares.
//
// At RUNTIME this class is stripped from aosp-shim.dex by the entry in
// scripts/framework_duplicates.txt -- framework.jar's real
// INotificationManager.Stub wins, and WestlakeNotificationManagerService
// is loaded as a subclass of the real Stub.  Bytecode compatibility
// relies on (a) identical FQCN `android.app.INotificationManager$Stub`,
// (b) Stub being abstract and extending android.os.Binder, and (c) every
// method signature of WestlakeNotificationManagerService matching the
// framework.jar INotificationManager.aidl surface (Android 16 has 167
// abstract methods).
//
// Method count: 167 declared abstract methods, matching Android 16
// INotificationManager.aidl.

package android.app;

import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteException;

public interface INotificationManager extends IInterface {

    static final String DESCRIPTOR = "android.app.INotificationManager";

    // --- 167 abstract methods (generated from baksmali on framework.jar Android 16) ---

    java.lang.String addAutomaticZenRule(android.app.AutomaticZenRule p0, java.lang.String p1, boolean p2) throws android.os.RemoteException;
    void allowAssistantAdjustment(java.lang.String p0) throws android.os.RemoteException;
    boolean appCanBePromoted(java.lang.String p0, int p1) throws android.os.RemoteException;
    void applyAdjustmentFromAssistant(android.service.notification.INotificationListener p0, android.service.notification.Adjustment p1) throws android.os.RemoteException;
    void applyAdjustmentsFromAssistant(android.service.notification.INotificationListener p0, java.util.List p1) throws android.os.RemoteException;
    void applyEnqueuedAdjustmentFromAssistant(android.service.notification.INotificationListener p0, android.service.notification.Adjustment p1) throws android.os.RemoteException;
    void applyRestore(byte[] p0, int p1) throws android.os.RemoteException;
    boolean areBubblesAllowed(java.lang.String p0) throws android.os.RemoteException;
    boolean areBubblesEnabled(android.os.UserHandle p0) throws android.os.RemoteException;
    boolean areChannelsBypassingDnd() throws android.os.RemoteException;
    boolean areNotificationsEnabled(java.lang.String p0) throws android.os.RemoteException;
    boolean areNotificationsEnabledForPackage(java.lang.String p0, int p1) throws android.os.RemoteException;
    boolean canBePromoted(java.lang.String p0) throws android.os.RemoteException;
    boolean canNotifyAsPackage(java.lang.String p0, java.lang.String p1, int p2) throws android.os.RemoteException;
    boolean canShowBadge(java.lang.String p0, int p1) throws android.os.RemoteException;
    boolean canUseFullScreenIntent(android.content.AttributionSource p0) throws android.os.RemoteException;
    void cancelAllNotifications(java.lang.String p0, int p1) throws android.os.RemoteException;
    void cancelNotificationFromListener(android.service.notification.INotificationListener p0, java.lang.String p1, java.lang.String p2, int p3) throws android.os.RemoteException;
    void cancelNotificationWithTag(java.lang.String p0, java.lang.String p1, java.lang.String p2, int p3, int p4) throws android.os.RemoteException;
    void cancelNotificationsFromListener(android.service.notification.INotificationListener p0, java.lang.String[] p1) throws android.os.RemoteException;
    void cancelToast(java.lang.String p0, android.os.IBinder p1) throws android.os.RemoteException;
    void cleanUpCallersAfter(long p0) throws android.os.RemoteException;
    void clearData(java.lang.String p0, int p1, boolean p2) throws android.os.RemoteException;
    void clearRequestedListenerHints(android.service.notification.INotificationListener p0) throws android.os.RemoteException;
    void createConversationNotificationChannelForPackage(java.lang.String p0, int p1, android.app.NotificationChannel p2, java.lang.String p3) throws android.os.RemoteException;
    android.app.NotificationChannel createConversationNotificationChannelForPackageFromPrivilegedListener(android.service.notification.INotificationListener p0, java.lang.String p1, android.os.UserHandle p2, java.lang.String p3, java.lang.String p4) throws android.os.RemoteException;
    void createNotificationChannelGroups(java.lang.String p0, android.content.pm.ParceledListSlice p1) throws android.os.RemoteException;
    void createNotificationChannels(java.lang.String p0, android.content.pm.ParceledListSlice p1) throws android.os.RemoteException;
    void createNotificationChannelsForPackage(java.lang.String p0, int p1, android.content.pm.ParceledListSlice p2) throws android.os.RemoteException;
    void deleteNotificationChannel(java.lang.String p0, java.lang.String p1) throws android.os.RemoteException;
    void deleteNotificationChannelGroup(java.lang.String p0, java.lang.String p1) throws android.os.RemoteException;
    void deleteNotificationHistoryItem(java.lang.String p0, int p1, long p2) throws android.os.RemoteException;
    void disallowAssistantAdjustment(java.lang.String p0) throws android.os.RemoteException;
    void enqueueNotificationWithTag(java.lang.String p0, java.lang.String p1, java.lang.String p2, int p3, android.app.Notification p4, int p5) throws android.os.RemoteException;
    boolean enqueueTextToast(java.lang.String p0, android.os.IBinder p1, java.lang.CharSequence p2, int p3, boolean p4, int p5, android.app.ITransientNotificationCallback p6) throws android.os.RemoteException;
    boolean enqueueToast(java.lang.String p0, android.os.IBinder p1, android.app.ITransientNotification p2, int p3, boolean p4, int p5) throws android.os.RemoteException;
    void finishToken(java.lang.String p0, android.os.IBinder p1) throws android.os.RemoteException;
    android.service.notification.StatusBarNotification[] getActiveNotifications(java.lang.String p0) throws android.os.RemoteException;
    android.content.pm.ParceledListSlice getActiveNotificationsFromListener(android.service.notification.INotificationListener p0, java.lang.String[] p1, int p2) throws android.os.RemoteException;
    android.service.notification.StatusBarNotification[] getActiveNotificationsWithAttribution(java.lang.String p0, java.lang.String p1) throws android.os.RemoteException;
    int[] getAllowedAdjustmentKeyTypes() throws android.os.RemoteException;
    java.util.List getAllowedAssistantAdjustments(java.lang.String p0) throws android.os.RemoteException;
    android.content.ComponentName getAllowedNotificationAssistant() throws android.os.RemoteException;
    android.content.ComponentName getAllowedNotificationAssistantForUser(int p0) throws android.os.RemoteException;
    android.content.pm.ParceledListSlice getAppActiveNotifications(java.lang.String p0, int p1) throws android.os.RemoteException;
    android.app.AutomaticZenRule getAutomaticZenRule(java.lang.String p0) throws android.os.RemoteException;
    int getAutomaticZenRuleState(java.lang.String p0) throws android.os.RemoteException;
    android.content.pm.ParceledListSlice getAutomaticZenRules() throws android.os.RemoteException;
    byte[] getBackupPayload(int p0) throws android.os.RemoteException;
    int getBlockedChannelCount(java.lang.String p0, int p1) throws android.os.RemoteException;
    int getBubblePreferenceForPackage(java.lang.String p0, int p1) throws android.os.RemoteException;
    android.app.NotificationManager.Policy getConsolidatedNotificationPolicy() throws android.os.RemoteException;
    android.app.NotificationChannel getConversationNotificationChannel(java.lang.String p0, int p1, java.lang.String p2, java.lang.String p3, boolean p4, java.lang.String p5) throws android.os.RemoteException;
    android.content.pm.ParceledListSlice getConversations(boolean p0) throws android.os.RemoteException;
    android.content.pm.ParceledListSlice getConversationsForPackage(java.lang.String p0, int p1) throws android.os.RemoteException;
    android.content.ComponentName getDefaultNotificationAssistant() throws android.os.RemoteException;
    android.service.notification.ZenPolicy getDefaultZenPolicy() throws android.os.RemoteException;
    int getDeletedChannelCount(java.lang.String p0, int p1) throws android.os.RemoteException;
    android.content.ComponentName getEffectsSuppressor() throws android.os.RemoteException;
    java.util.List getEnabledNotificationListenerPackages() throws android.os.RemoteException;
    java.util.List getEnabledNotificationListeners(int p0) throws android.os.RemoteException;
    int getHintsFromListener(android.service.notification.INotificationListener p0) throws android.os.RemoteException;
    int getHintsFromListenerNoToken() throws android.os.RemoteException;
    android.service.notification.StatusBarNotification[] getHistoricalNotifications(java.lang.String p0, int p1, boolean p2) throws android.os.RemoteException;
    android.service.notification.StatusBarNotification[] getHistoricalNotificationsWithAttribution(java.lang.String p0, java.lang.String p1, int p2, boolean p3) throws android.os.RemoteException;
    int getInterruptionFilterFromListener(android.service.notification.INotificationListener p0) throws android.os.RemoteException;
    android.service.notification.NotificationListenerFilter getListenerFilter(android.content.ComponentName p0, int p1) throws android.os.RemoteException;
    android.app.NotificationChannel getNotificationChannel(java.lang.String p0, int p1, java.lang.String p2, java.lang.String p3) throws android.os.RemoteException;
    android.app.NotificationChannel getNotificationChannelForPackage(java.lang.String p0, int p1, java.lang.String p2, java.lang.String p3, boolean p4) throws android.os.RemoteException;
    android.app.NotificationChannelGroup getNotificationChannelGroup(java.lang.String p0, java.lang.String p1) throws android.os.RemoteException;
    android.app.NotificationChannelGroup getNotificationChannelGroupForPackage(java.lang.String p0, java.lang.String p1, int p2) throws android.os.RemoteException;
    android.content.pm.ParceledListSlice getNotificationChannelGroups(java.lang.String p0) throws android.os.RemoteException;
    android.content.pm.ParceledListSlice getNotificationChannelGroupsForPackage(java.lang.String p0, int p1, boolean p2) throws android.os.RemoteException;
    android.content.pm.ParceledListSlice getNotificationChannelGroupsFromPrivilegedListener(android.service.notification.INotificationListener p0, java.lang.String p1, android.os.UserHandle p2) throws android.os.RemoteException;
    android.content.pm.ParceledListSlice getNotificationChannels(java.lang.String p0, java.lang.String p1, int p2) throws android.os.RemoteException;
    android.content.pm.ParceledListSlice getNotificationChannelsBypassingDnd(java.lang.String p0, int p1) throws android.os.RemoteException;
    android.content.pm.ParceledListSlice getNotificationChannelsForPackage(java.lang.String p0, int p1, boolean p2) throws android.os.RemoteException;
    android.content.pm.ParceledListSlice getNotificationChannelsFromPrivilegedListener(android.service.notification.INotificationListener p0, java.lang.String p1, android.os.UserHandle p2) throws android.os.RemoteException;
    java.lang.String getNotificationDelegate(java.lang.String p0) throws android.os.RemoteException;
    android.app.NotificationHistory getNotificationHistory(java.lang.String p0, java.lang.String p1) throws android.os.RemoteException;
    android.app.NotificationManager.Policy getNotificationPolicy(java.lang.String p0) throws android.os.RemoteException;
    int getNumNotificationChannelsForPackage(java.lang.String p0, int p1, boolean p2) throws android.os.RemoteException;
    int getPackageImportance(java.lang.String p0) throws android.os.RemoteException;
    android.content.pm.ParceledListSlice getPackagesBypassingDnd(int p0) throws android.os.RemoteException;
    android.app.NotificationChannelGroup getPopulatedNotificationChannelGroupForPackage(java.lang.String p0, int p1, java.lang.String p2, boolean p3) throws android.os.RemoteException;
    boolean getPrivateNotificationsAllowed() throws android.os.RemoteException;
    android.content.pm.ParceledListSlice getRecentBlockedNotificationChannelGroupsForPackage(java.lang.String p0, int p1) throws android.os.RemoteException;
    int getRuleInstanceCount(android.content.ComponentName p0) throws android.os.RemoteException;
    android.content.pm.ParceledListSlice getSnoozedNotificationsFromListener(android.service.notification.INotificationListener p0, int p1) throws android.os.RemoteException;
    java.lang.String[] getTypeAdjustmentDeniedPackages() throws android.os.RemoteException;
    java.util.List getUnsupportedAdjustmentTypes() throws android.os.RemoteException;
    int getZenMode() throws android.os.RemoteException;
    android.service.notification.ZenModeConfig getZenModeConfig() throws android.os.RemoteException;
    android.content.pm.ParceledListSlice getZenRules() throws android.os.RemoteException;
    boolean hasEnabledNotificationListener(java.lang.String p0, int p1) throws android.os.RemoteException;
    boolean hasSentValidBubble(java.lang.String p0, int p1) throws android.os.RemoteException;
    boolean hasSentValidMsg(java.lang.String p0, int p1) throws android.os.RemoteException;
    boolean hasUserDemotedInvalidMsgApp(java.lang.String p0, int p1) throws android.os.RemoteException;
    boolean isImportanceLocked(java.lang.String p0, int p1) throws android.os.RemoteException;
    boolean isInCall(java.lang.String p0, int p1) throws android.os.RemoteException;
    boolean isInInvalidMsgState(java.lang.String p0, int p1) throws android.os.RemoteException;
    boolean isNotificationAssistantAccessGranted(android.content.ComponentName p0) throws android.os.RemoteException;
    boolean isNotificationListenerAccessGranted(android.content.ComponentName p0) throws android.os.RemoteException;
    boolean isNotificationListenerAccessGrantedForUser(android.content.ComponentName p0, int p1) throws android.os.RemoteException;
    boolean isNotificationPolicyAccessGranted(java.lang.String p0) throws android.os.RemoteException;
    boolean isNotificationPolicyAccessGrantedForPackage(java.lang.String p0) throws android.os.RemoteException;
    boolean isPackagePaused(java.lang.String p0) throws android.os.RemoteException;
    boolean isPermissionFixed(java.lang.String p0, int p1) throws android.os.RemoteException;
    boolean isSystemConditionProviderEnabled(java.lang.String p0) throws android.os.RemoteException;
    boolean matchesCallFilter(android.os.Bundle p0) throws android.os.RemoteException;
    void migrateNotificationFilter(android.service.notification.INotificationListener p0, int p1, java.util.List p2) throws android.os.RemoteException;
    void notifyConditions(java.lang.String p0, android.service.notification.IConditionProvider p1, android.service.notification.Condition[] p2) throws android.os.RemoteException;
    boolean onlyHasDefaultChannel(java.lang.String p0, int p1) throws android.os.RemoteException;
    long pullStats(long p0, int p1, boolean p2, java.util.List p3) throws android.os.RemoteException;
    void registerCallNotificationEventListener(java.lang.String p0, android.os.UserHandle p1, android.app.ICallNotificationEventCallback p2) throws android.os.RemoteException;
    void registerListener(android.service.notification.INotificationListener p0, android.content.ComponentName p1, int p2) throws android.os.RemoteException;
    boolean removeAutomaticZenRule(java.lang.String p0, boolean p1) throws android.os.RemoteException;
    boolean removeAutomaticZenRules(java.lang.String p0, boolean p1) throws android.os.RemoteException;
    void requestBindListener(android.content.ComponentName p0) throws android.os.RemoteException;
    void requestBindProvider(android.content.ComponentName p0) throws android.os.RemoteException;
    void requestHintsFromListener(android.service.notification.INotificationListener p0, int p1) throws android.os.RemoteException;
    void requestInterruptionFilterFromListener(android.service.notification.INotificationListener p0, int p1) throws android.os.RemoteException;
    void requestUnbindListener(android.service.notification.INotificationListener p0) throws android.os.RemoteException;
    void requestUnbindListenerComponent(android.content.ComponentName p0) throws android.os.RemoteException;
    void requestUnbindProvider(android.service.notification.IConditionProvider p0) throws android.os.RemoteException;
    void setAdjustmentTypeSupportedState(android.service.notification.INotificationListener p0, java.lang.String p1, boolean p2) throws android.os.RemoteException;
    void setAssistantAdjustmentKeyTypeState(int p0, boolean p1) throws android.os.RemoteException;
    void setAutomaticZenRuleState(java.lang.String p0, android.service.notification.Condition p1) throws android.os.RemoteException;
    void setBubblesAllowed(java.lang.String p0, int p1, int p2) throws android.os.RemoteException;
    void setCanBePromoted(java.lang.String p0, int p1, boolean p2, boolean p3) throws android.os.RemoteException;
    void setHideSilentStatusIcons(boolean p0) throws android.os.RemoteException;
    void setInterruptionFilter(java.lang.String p0, int p1, boolean p2) throws android.os.RemoteException;
    void setInvalidMsgAppDemoted(java.lang.String p0, int p1, boolean p2) throws android.os.RemoteException;
    void setListenerFilter(android.content.ComponentName p0, int p1, android.service.notification.NotificationListenerFilter p2) throws android.os.RemoteException;
    void setManualZenRuleDeviceEffects(android.service.notification.ZenDeviceEffects p0) throws android.os.RemoteException;
    void setNASMigrationDoneAndResetDefault(int p0, boolean p1) throws android.os.RemoteException;
    void setNotificationAssistantAccessGranted(android.content.ComponentName p0, boolean p1) throws android.os.RemoteException;
    void setNotificationAssistantAccessGrantedForUser(android.content.ComponentName p0, int p1, boolean p2) throws android.os.RemoteException;
    void setNotificationDelegate(java.lang.String p0, java.lang.String p1) throws android.os.RemoteException;
    void setNotificationListenerAccessGranted(android.content.ComponentName p0, boolean p1, boolean p2) throws android.os.RemoteException;
    void setNotificationListenerAccessGrantedForUser(android.content.ComponentName p0, int p1, boolean p2, boolean p3) throws android.os.RemoteException;
    void setNotificationPolicy(java.lang.String p0, android.app.NotificationManager.Policy p1, boolean p2) throws android.os.RemoteException;
    void setNotificationPolicyAccessGranted(java.lang.String p0, boolean p1) throws android.os.RemoteException;
    void setNotificationPolicyAccessGrantedForUser(java.lang.String p0, int p1, boolean p2) throws android.os.RemoteException;
    void setNotificationsEnabledForPackage(java.lang.String p0, int p1, boolean p2) throws android.os.RemoteException;
    void setNotificationsEnabledWithImportanceLockForPackage(java.lang.String p0, int p1, boolean p2) throws android.os.RemoteException;
    void setNotificationsShownFromListener(android.service.notification.INotificationListener p0, java.lang.String[] p1) throws android.os.RemoteException;
    void setOnNotificationPostedTrimFromListener(android.service.notification.INotificationListener p0, int p1) throws android.os.RemoteException;
    void setPrivateNotificationsAllowed(boolean p0) throws android.os.RemoteException;
    void setShowBadge(java.lang.String p0, int p1, boolean p2) throws android.os.RemoteException;
    void setToastRateLimitingEnabled(boolean p0) throws android.os.RemoteException;
    void setTypeAdjustmentForPackageState(java.lang.String p0, boolean p1) throws android.os.RemoteException;
    void setZenMode(int p0, android.net.Uri p1, java.lang.String p2, boolean p3) throws android.os.RemoteException;
    boolean shouldHideSilentStatusIcons(java.lang.String p0) throws android.os.RemoteException;
    void silenceNotificationSound() throws android.os.RemoteException;
    void snoozeNotificationUntilContextFromListener(android.service.notification.INotificationListener p0, java.lang.String p1, java.lang.String p2) throws android.os.RemoteException;
    void snoozeNotificationUntilFromListener(android.service.notification.INotificationListener p0, java.lang.String p1, long p2) throws android.os.RemoteException;
    void unlockAllNotificationChannels() throws android.os.RemoteException;
    void unlockNotificationChannel(java.lang.String p0, int p1, java.lang.String p2) throws android.os.RemoteException;
    void unregisterCallNotificationEventListener(java.lang.String p0, android.os.UserHandle p1, android.app.ICallNotificationEventCallback p2) throws android.os.RemoteException;
    void unregisterListener(android.service.notification.INotificationListener p0, int p1) throws android.os.RemoteException;
    void unsnoozeNotificationFromAssistant(android.service.notification.INotificationListener p0, java.lang.String p1) throws android.os.RemoteException;
    void unsnoozeNotificationFromSystemListener(android.service.notification.INotificationListener p0, java.lang.String p1) throws android.os.RemoteException;
    boolean updateAutomaticZenRule(java.lang.String p0, android.app.AutomaticZenRule p1, boolean p2) throws android.os.RemoteException;
    void updateNotificationChannelForPackage(java.lang.String p0, int p1, android.app.NotificationChannel p2) throws android.os.RemoteException;
    void updateNotificationChannelFromPrivilegedListener(android.service.notification.INotificationListener p0, java.lang.String p1, android.os.UserHandle p2, android.app.NotificationChannel p3) throws android.os.RemoteException;
    void updateNotificationChannelGroupForPackage(java.lang.String p0, int p1, android.app.NotificationChannelGroup p2) throws android.os.RemoteException;

    // --- AIDL-generated Stub abstract class -------------------------------
    public static abstract class Stub extends android.os.Binder implements INotificationManager {
        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }
        // CR17: Android 16 alternate constructor that accepts a
        // PermissionEnforcer.  The real framework.jar Stub uses this to
        // bypass the ActivityThread-dependent default ctor (which NPEs
        // in the Westlake sandbox).  WestlakeNotificationManagerService
        // invokes super(NoopPermissionEnforcer) via this overload.
        public Stub(android.os.PermissionEnforcer enforcer) {
            attachInterface(this, DESCRIPTOR);
        }
        public static INotificationManager asInterface(IBinder obj) {
            if (obj == null) return null;
            IInterface i = obj.queryLocalInterface(DESCRIPTOR);
            if (i instanceof INotificationManager) return (INotificationManager) i;
            return null;
        }
        @Override public IBinder asBinder() { return this; }
    }
}
