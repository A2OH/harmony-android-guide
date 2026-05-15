// SPDX-License-Identifier: Apache-2.0
//
// Westlake M4e -- WestlakeNotificationManagerService
//
// Minimum-surface implementation of android.app.INotificationManager.Stub
// for the Westlake dalvikvm sandbox.  ~5 Tier-1 real impls + ~162
// fail-loud unobserved-method overrides (per codex CR2 /
// ServiceMethodMissing.fail pattern).
//
// Same-process Stub.asInterface elision:
//   When framework code does
//     INotificationManager nm = INotificationManager.Stub.asInterface(
//         ServiceManager.getService("notification"));
//   the Stub looks up queryLocalInterface(INotificationManager.DESCRIPTOR)
//   on the IBinder, which returns THIS instance (because Stub() called
//   attachInterface(this, DESCRIPTOR)).  asInterface then returns the
//   raw object cast to INotificationManager -- no Parcel marshaling, no
//   onTransact dispatch.
//
// Compile-time vs runtime hierarchy:
//   Compile-time: extends shim's android.app.INotificationManager$Stub
//                 (abstract; 167 abstract methods declared in
//                  shim/java/android/app/INotificationManager.java).
//   Runtime:      extends framework.jar's
//                 android.app.INotificationManager$Stub.  Shim Stub is
//                 stripped from aosp-shim.dex via
//                 scripts/framework_duplicates.txt so the real Stub wins.
//
// Rationale (see docs/engine/M4_DISCOVERY.md sec 7):
//   App-side NotificationManager calls .areNotificationsEnabled(),
//   .getNotificationChannels(), .getNotificationChannel(channelId),
//   and the framework probes .getZenMode() / .getEffectsSuppressor()
//   during onCreate of Notification-aware components (push libraries,
//   media controls).  Without an INotificationManager registered under
//   "notification", ServiceManager.getService returns null and these
//   calls NPE.  M4e registers a minimal INotificationManager that
//   reports "notifications are enabled, zen=off, no effects suppressor,
//   no channels yet".  All other notification paths fail loud so we can
//   discover Tier-1 candidates.
//
// Method count: 167 INotificationManager methods.  Tier-1 (real):
//   areNotificationsEnabled, getZenMode, getEffectsSuppressor,
//   getNotificationChannels, getNotificationChannel.  Remaining 162
//   fail loud.
//
// CR17 (2026-05-12): per codex review #2 HIGH finding -- the previously
// used `super()` ctor expands to
//     PermissionEnforcer.fromContext(ActivityThread.currentActivityThread()
//                                         .getSystemContext())
// which NPEs in the Westlake sandbox (ActivityThread is null unless
// CharsetPrimer.primeActivityThread() runs first).  Production-path
// callers via ServiceRegistrar.registerAllServices() don't run the test
// harness primer, so the default ctor NPE'd there.  Fixed by adopting
// the same PermissionEnforcer-bypass pattern used by M4a/M4b/M4c/M4-power
// (subclassed PermissionEnforcer whose protected no-arg ctor sets
// mContext=null and returns).  No system services are touched.

package com.westlake.services;

import android.app.INotificationManager;
import android.os.PermissionEnforcer;

public final class WestlakeNotificationManagerService extends INotificationManager.Stub {

    /** AOSP-stable constant: ZEN_MODE_OFF = 0 (no DND). */
    private static final int ZEN_MODE_OFF = 0;

    // PermissionEnforcer subclass nested here so users don't need to
    // import it; protected constructor of PermissionEnforcer is
    // accessible to subclasses regardless of package.
    private static final class NoopPermissionEnforcer extends PermissionEnforcer {
        NoopPermissionEnforcer() { super(); }
    }

    public WestlakeNotificationManagerService() {
        // Bypass the deprecated no-arg constructor that NPEs in the
        // sandbox (ActivityThread.getSystemContext() returns null); use
        // the Stub(PermissionEnforcer) overload with a no-op enforcer.
        // Base Stub still calls attachInterface(this, DESCRIPTOR), so
        // queryLocalInterface("android.app.INotificationManager") returns this.
        super(new NoopPermissionEnforcer());
    }

    // ------------------------------------------------------------------
    //   IMPLEMENTED (Tier-1) METHODS
    // ------------------------------------------------------------------

    /** Sandbox: notifications are always enabled. */
    @Override
    public boolean areNotificationsEnabled(java.lang.String p0) throws android.os.RemoteException {
        return true;
    }

    /** ZEN_MODE_OFF: no Do-Not-Disturb in the sandbox. */
    @Override
    public int getZenMode() throws android.os.RemoteException {
        return ZEN_MODE_OFF;
    }

    /** No effects suppressor: notifications produce sound/vibration. */
    @Override
    public android.content.ComponentName getEffectsSuppressor() throws android.os.RemoteException {
        return null;
    }

    /** Return an empty ParceledListSlice for any channel lookup.
     *  Reflective construction so we don't bind to a specific
     *  framework.jar constructor signature at compile time. */
    @Override
    public android.content.pm.ParceledListSlice getNotificationChannels(
            java.lang.String p0, java.lang.String p1, int p2) throws android.os.RemoteException {
        return emptyParceledListSlice();
    }

    /** Sandbox: no NotificationChannel is registered for any
     *  (pkg, userId, targetPkg, channelId) tuple yet.  Apps that probe
     *  for a channel get null and proceed with the deprecated v25-and-
     *  earlier code path (or fall back to default-channel semantics). */
    @Override
    public android.app.NotificationChannel getNotificationChannel(
            java.lang.String p0, int p1, java.lang.String p2, java.lang.String p3)
            throws android.os.RemoteException {
        return null;
    }

    /** Build an empty ParceledListSlice.  Reflection because the
     *  compile-time shim ParceledListSlice may have a different
     *  constructor surface than framework.jar's runtime class. */
    private static android.content.pm.ParceledListSlice emptyParceledListSlice() {
        try {
            Class<?> cls = Class.forName("android.content.pm.ParceledListSlice");
            // Try (List<T>) ctor first -- canonical AOSP signature.
            try {
                java.lang.reflect.Constructor<?> ctor =
                        cls.getDeclaredConstructor(java.util.List.class);
                ctor.setAccessible(true);
                return (android.content.pm.ParceledListSlice)
                        ctor.newInstance(java.util.Collections.emptyList());
            } catch (NoSuchMethodException nsme) {
                // Fall through to no-arg.
            }
            java.lang.reflect.Constructor<?> ctor0 = cls.getDeclaredConstructor();
            ctor0.setAccessible(true);
            return (android.content.pm.ParceledListSlice) ctor0.newInstance();
        } catch (Throwable t) {
            // Last resort: null.  Callers (real Android code) should
            // null-check; if they don't, the test should catch it and we
            // promote this stub to a richer impl.
            return null;
        }
    }

    // ------------------------------------------------------------------
    //   FAIL-LOUD UNOBSERVED METHODS (CR2 / codex Tier 2 #2)
    //
    //   How to promote a method to Tier-1: delete the throw body here and
    //   add a real implementation in the IMPLEMENTED block above.  Update
    //   NotificationServiceTest to exercise the new path.
    // ------------------------------------------------------------------

    @Override public java.lang.String addAutomaticZenRule(android.app.AutomaticZenRule p0, java.lang.String p1, boolean p2) throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "addAutomaticZenRule"); }
    @Override public void allowAssistantAdjustment(java.lang.String p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "allowAssistantAdjustment"); }
    // CR32: promote safe sandbox defaults (boolean probes -> "no/disabled by policy"; cancel -> no-op).
    @Override public boolean appCanBePromoted(java.lang.String p0, int p1) throws android.os.RemoteException { return false; }
    @Override public void applyAdjustmentFromAssistant(android.service.notification.INotificationListener p0, android.service.notification.Adjustment p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "applyAdjustmentFromAssistant"); }
    @Override public void applyAdjustmentsFromAssistant(android.service.notification.INotificationListener p0, java.util.List p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "applyAdjustmentsFromAssistant"); }
    @Override public void applyEnqueuedAdjustmentFromAssistant(android.service.notification.INotificationListener p0, android.service.notification.Adjustment p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "applyEnqueuedAdjustmentFromAssistant"); }
    @Override public void applyRestore(byte[] p0, int p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "applyRestore"); }
    @Override public boolean areBubblesAllowed(java.lang.String p0) throws android.os.RemoteException { return false; }
    @Override public boolean areBubblesEnabled(android.os.UserHandle p0) throws android.os.RemoteException { return false; }
    @Override public boolean areChannelsBypassingDnd() throws android.os.RemoteException { return false; }
    @Override public boolean areNotificationsEnabledForPackage(java.lang.String p0, int p1) throws android.os.RemoteException { return true; /* sandbox: enabled (same as areNotificationsEnabled) */ }
    @Override public boolean canBePromoted(java.lang.String p0) throws android.os.RemoteException { return false; }
    @Override public boolean canNotifyAsPackage(java.lang.String p0, java.lang.String p1, int p2) throws android.os.RemoteException { return false; }
    @Override public boolean canShowBadge(java.lang.String p0, int p1) throws android.os.RemoteException { return true; /* default Android: badges allowed */ }
    @Override public boolean canUseFullScreenIntent(android.content.AttributionSource p0) throws android.os.RemoteException { return false; }
    @Override public void cancelAllNotifications(java.lang.String p0, int p1) throws android.os.RemoteException { /* no-op (sandbox has no notifications to cancel) */ }
    @Override public void cancelNotificationFromListener(android.service.notification.INotificationListener p0, java.lang.String p1, java.lang.String p2, int p3) throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "cancelNotificationFromListener"); }
    @Override public void cancelNotificationWithTag(java.lang.String p0, java.lang.String p1, java.lang.String p2, int p3, int p4) throws android.os.RemoteException { /* no-op */ }
    @Override public void cancelNotificationsFromListener(android.service.notification.INotificationListener p0, java.lang.String[] p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "cancelNotificationsFromListener"); }
    @Override public void cancelToast(java.lang.String p0, android.os.IBinder p1) throws android.os.RemoteException { /* no-op */ }
    @Override public void cleanUpCallersAfter(long p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "cleanUpCallersAfter"); }
    @Override public void clearData(java.lang.String p0, int p1, boolean p2) throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "clearData"); }
    @Override public void clearRequestedListenerHints(android.service.notification.INotificationListener p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "clearRequestedListenerHints"); }
    @Override public void createConversationNotificationChannelForPackage(java.lang.String p0, int p1, android.app.NotificationChannel p2, java.lang.String p3) throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "createConversationNotificationChannelForPackage"); }
    @Override public android.app.NotificationChannel createConversationNotificationChannelForPackageFromPrivilegedListener(android.service.notification.INotificationListener p0, java.lang.String p1, android.os.UserHandle p2, java.lang.String p3, java.lang.String p4) throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "createConversationNotificationChannelForPackageFromPrivilegedListener"); }
    @Override public void createNotificationChannelGroups(java.lang.String p0, android.content.pm.ParceledListSlice p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "createNotificationChannelGroups"); }
    @Override public void createNotificationChannels(java.lang.String p0, android.content.pm.ParceledListSlice p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "createNotificationChannels"); }
    @Override public void createNotificationChannelsForPackage(java.lang.String p0, int p1, android.content.pm.ParceledListSlice p2) throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "createNotificationChannelsForPackage"); }
    @Override public void deleteNotificationChannel(java.lang.String p0, java.lang.String p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "deleteNotificationChannel"); }
    @Override public void deleteNotificationChannelGroup(java.lang.String p0, java.lang.String p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "deleteNotificationChannelGroup"); }
    @Override public void deleteNotificationHistoryItem(java.lang.String p0, int p1, long p2) throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "deleteNotificationHistoryItem"); }
    @Override public void disallowAssistantAdjustment(java.lang.String p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "disallowAssistantAdjustment"); }
    @Override public void enqueueNotificationWithTag(java.lang.String p0, java.lang.String p1, java.lang.String p2, int p3, android.app.Notification p4, int p5) throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "enqueueNotificationWithTag"); }
    @Override public boolean enqueueTextToast(java.lang.String p0, android.os.IBinder p1, java.lang.CharSequence p2, int p3, boolean p4, int p5, android.app.ITransientNotificationCallback p6) throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "enqueueTextToast"); }
    @Override public boolean enqueueToast(java.lang.String p0, android.os.IBinder p1, android.app.ITransientNotification p2, int p3, boolean p4, int p5) throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "enqueueToast"); }
    @Override public void finishToken(java.lang.String p0, android.os.IBinder p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "finishToken"); }
    @Override public android.service.notification.StatusBarNotification[] getActiveNotifications(java.lang.String p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "getActiveNotifications"); }
    @Override public android.content.pm.ParceledListSlice getActiveNotificationsFromListener(android.service.notification.INotificationListener p0, java.lang.String[] p1, int p2) throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "getActiveNotificationsFromListener"); }
    @Override public android.service.notification.StatusBarNotification[] getActiveNotificationsWithAttribution(java.lang.String p0, java.lang.String p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "getActiveNotificationsWithAttribution"); }
    @Override public int[] getAllowedAdjustmentKeyTypes() throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "getAllowedAdjustmentKeyTypes"); }
    @Override public java.util.List getAllowedAssistantAdjustments(java.lang.String p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "getAllowedAssistantAdjustments"); }
    @Override public android.content.ComponentName getAllowedNotificationAssistant() throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "getAllowedNotificationAssistant"); }
    @Override public android.content.ComponentName getAllowedNotificationAssistantForUser(int p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "getAllowedNotificationAssistantForUser"); }
    @Override public android.content.pm.ParceledListSlice getAppActiveNotifications(java.lang.String p0, int p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "getAppActiveNotifications"); }
    @Override public android.app.AutomaticZenRule getAutomaticZenRule(java.lang.String p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "getAutomaticZenRule"); }
    @Override public int getAutomaticZenRuleState(java.lang.String p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "getAutomaticZenRuleState"); }
    @Override public android.content.pm.ParceledListSlice getAutomaticZenRules() throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "getAutomaticZenRules"); }
    @Override public byte[] getBackupPayload(int p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "getBackupPayload"); }
    @Override public int getBlockedChannelCount(java.lang.String p0, int p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "getBlockedChannelCount"); }
    @Override public int getBubblePreferenceForPackage(java.lang.String p0, int p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "getBubblePreferenceForPackage"); }
    @Override public android.app.NotificationManager.Policy getConsolidatedNotificationPolicy() throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "getConsolidatedNotificationPolicy"); }
    @Override public android.app.NotificationChannel getConversationNotificationChannel(java.lang.String p0, int p1, java.lang.String p2, java.lang.String p3, boolean p4, java.lang.String p5) throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "getConversationNotificationChannel"); }
    @Override public android.content.pm.ParceledListSlice getConversations(boolean p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "getConversations"); }
    @Override public android.content.pm.ParceledListSlice getConversationsForPackage(java.lang.String p0, int p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "getConversationsForPackage"); }
    @Override public android.content.ComponentName getDefaultNotificationAssistant() throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "getDefaultNotificationAssistant"); }
    @Override public android.service.notification.ZenPolicy getDefaultZenPolicy() throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "getDefaultZenPolicy"); }
    @Override public int getDeletedChannelCount(java.lang.String p0, int p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "getDeletedChannelCount"); }
    @Override public java.util.List getEnabledNotificationListenerPackages() throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "getEnabledNotificationListenerPackages"); }
    @Override public java.util.List getEnabledNotificationListeners(int p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "getEnabledNotificationListeners"); }
    @Override public int getHintsFromListener(android.service.notification.INotificationListener p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "getHintsFromListener"); }
    @Override public int getHintsFromListenerNoToken() throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "getHintsFromListenerNoToken"); }
    @Override public android.service.notification.StatusBarNotification[] getHistoricalNotifications(java.lang.String p0, int p1, boolean p2) throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "getHistoricalNotifications"); }
    @Override public android.service.notification.StatusBarNotification[] getHistoricalNotificationsWithAttribution(java.lang.String p0, java.lang.String p1, int p2, boolean p3) throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "getHistoricalNotificationsWithAttribution"); }
    @Override public int getInterruptionFilterFromListener(android.service.notification.INotificationListener p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "getInterruptionFilterFromListener"); }
    @Override public android.service.notification.NotificationListenerFilter getListenerFilter(android.content.ComponentName p0, int p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "getListenerFilter"); }
    @Override public android.app.NotificationChannel getNotificationChannelForPackage(java.lang.String p0, int p1, java.lang.String p2, java.lang.String p3, boolean p4) throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "getNotificationChannelForPackage"); }
    @Override public android.app.NotificationChannelGroup getNotificationChannelGroup(java.lang.String p0, java.lang.String p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "getNotificationChannelGroup"); }
    @Override public android.app.NotificationChannelGroup getNotificationChannelGroupForPackage(java.lang.String p0, java.lang.String p1, int p2) throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "getNotificationChannelGroupForPackage"); }
    @Override public android.content.pm.ParceledListSlice getNotificationChannelGroups(java.lang.String p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "getNotificationChannelGroups"); }
    @Override public android.content.pm.ParceledListSlice getNotificationChannelGroupsForPackage(java.lang.String p0, int p1, boolean p2) throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "getNotificationChannelGroupsForPackage"); }
    @Override public android.content.pm.ParceledListSlice getNotificationChannelGroupsFromPrivilegedListener(android.service.notification.INotificationListener p0, java.lang.String p1, android.os.UserHandle p2) throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "getNotificationChannelGroupsFromPrivilegedListener"); }
    @Override public android.content.pm.ParceledListSlice getNotificationChannelsBypassingDnd(java.lang.String p0, int p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "getNotificationChannelsBypassingDnd"); }
    @Override public android.content.pm.ParceledListSlice getNotificationChannelsForPackage(java.lang.String p0, int p1, boolean p2) throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "getNotificationChannelsForPackage"); }
    @Override public android.content.pm.ParceledListSlice getNotificationChannelsFromPrivilegedListener(android.service.notification.INotificationListener p0, java.lang.String p1, android.os.UserHandle p2) throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "getNotificationChannelsFromPrivilegedListener"); }
    @Override public java.lang.String getNotificationDelegate(java.lang.String p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "getNotificationDelegate"); }
    @Override public android.app.NotificationHistory getNotificationHistory(java.lang.String p0, java.lang.String p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "getNotificationHistory"); }
    @Override public android.app.NotificationManager.Policy getNotificationPolicy(java.lang.String p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "getNotificationPolicy"); }
    @Override public int getNumNotificationChannelsForPackage(java.lang.String p0, int p1, boolean p2) throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "getNumNotificationChannelsForPackage"); }
    @Override public int getPackageImportance(java.lang.String p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "getPackageImportance"); }
    @Override public android.content.pm.ParceledListSlice getPackagesBypassingDnd(int p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "getPackagesBypassingDnd"); }
    @Override public android.app.NotificationChannelGroup getPopulatedNotificationChannelGroupForPackage(java.lang.String p0, int p1, java.lang.String p2, boolean p3) throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "getPopulatedNotificationChannelGroupForPackage"); }
    @Override public boolean getPrivateNotificationsAllowed() throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "getPrivateNotificationsAllowed"); }
    @Override public android.content.pm.ParceledListSlice getRecentBlockedNotificationChannelGroupsForPackage(java.lang.String p0, int p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "getRecentBlockedNotificationChannelGroupsForPackage"); }
    @Override public int getRuleInstanceCount(android.content.ComponentName p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "getRuleInstanceCount"); }
    @Override public android.content.pm.ParceledListSlice getSnoozedNotificationsFromListener(android.service.notification.INotificationListener p0, int p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "getSnoozedNotificationsFromListener"); }
    @Override public java.lang.String[] getTypeAdjustmentDeniedPackages() throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "getTypeAdjustmentDeniedPackages"); }
    @Override public java.util.List getUnsupportedAdjustmentTypes() throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "getUnsupportedAdjustmentTypes"); }
    @Override public android.service.notification.ZenModeConfig getZenModeConfig() throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "getZenModeConfig"); }
    @Override public android.content.pm.ParceledListSlice getZenRules() throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "getZenRules"); }
    @Override public boolean hasEnabledNotificationListener(java.lang.String p0, int p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "hasEnabledNotificationListener"); }
    @Override public boolean hasSentValidBubble(java.lang.String p0, int p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "hasSentValidBubble"); }
    @Override public boolean hasSentValidMsg(java.lang.String p0, int p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "hasSentValidMsg"); }
    @Override public boolean hasUserDemotedInvalidMsgApp(java.lang.String p0, int p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "hasUserDemotedInvalidMsgApp"); }
    @Override public boolean isImportanceLocked(java.lang.String p0, int p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "isImportanceLocked"); }
    @Override public boolean isInCall(java.lang.String p0, int p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "isInCall"); }
    @Override public boolean isInInvalidMsgState(java.lang.String p0, int p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "isInInvalidMsgState"); }
    @Override public boolean isNotificationAssistantAccessGranted(android.content.ComponentName p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "isNotificationAssistantAccessGranted"); }
    @Override public boolean isNotificationListenerAccessGranted(android.content.ComponentName p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "isNotificationListenerAccessGranted"); }
    @Override public boolean isNotificationListenerAccessGrantedForUser(android.content.ComponentName p0, int p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "isNotificationListenerAccessGrantedForUser"); }
    @Override public boolean isNotificationPolicyAccessGranted(java.lang.String p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "isNotificationPolicyAccessGranted"); }
    @Override public boolean isNotificationPolicyAccessGrantedForPackage(java.lang.String p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "isNotificationPolicyAccessGrantedForPackage"); }
    @Override public boolean isPackagePaused(java.lang.String p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "isPackagePaused"); }
    @Override public boolean isPermissionFixed(java.lang.String p0, int p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "isPermissionFixed"); }
    @Override public boolean isSystemConditionProviderEnabled(java.lang.String p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "isSystemConditionProviderEnabled"); }
    @Override public boolean matchesCallFilter(android.os.Bundle p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "matchesCallFilter"); }
    @Override public void migrateNotificationFilter(android.service.notification.INotificationListener p0, int p1, java.util.List p2) throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "migrateNotificationFilter"); }
    @Override public void notifyConditions(java.lang.String p0, android.service.notification.IConditionProvider p1, android.service.notification.Condition[] p2) throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "notifyConditions"); }
    @Override public boolean onlyHasDefaultChannel(java.lang.String p0, int p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "onlyHasDefaultChannel"); }
    @Override public long pullStats(long p0, int p1, boolean p2, java.util.List p3) throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "pullStats"); }
    @Override public void registerCallNotificationEventListener(java.lang.String p0, android.os.UserHandle p1, android.app.ICallNotificationEventCallback p2) throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "registerCallNotificationEventListener"); }
    @Override public void registerListener(android.service.notification.INotificationListener p0, android.content.ComponentName p1, int p2) throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "registerListener"); }
    @Override public boolean removeAutomaticZenRule(java.lang.String p0, boolean p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "removeAutomaticZenRule"); }
    @Override public boolean removeAutomaticZenRules(java.lang.String p0, boolean p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "removeAutomaticZenRules"); }
    @Override public void requestBindListener(android.content.ComponentName p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "requestBindListener"); }
    @Override public void requestBindProvider(android.content.ComponentName p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "requestBindProvider"); }
    @Override public void requestHintsFromListener(android.service.notification.INotificationListener p0, int p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "requestHintsFromListener"); }
    @Override public void requestInterruptionFilterFromListener(android.service.notification.INotificationListener p0, int p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "requestInterruptionFilterFromListener"); }
    @Override public void requestUnbindListener(android.service.notification.INotificationListener p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "requestUnbindListener"); }
    @Override public void requestUnbindListenerComponent(android.content.ComponentName p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "requestUnbindListenerComponent"); }
    @Override public void requestUnbindProvider(android.service.notification.IConditionProvider p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "requestUnbindProvider"); }
    @Override public void setAdjustmentTypeSupportedState(android.service.notification.INotificationListener p0, java.lang.String p1, boolean p2) throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "setAdjustmentTypeSupportedState"); }
    @Override public void setAssistantAdjustmentKeyTypeState(int p0, boolean p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "setAssistantAdjustmentKeyTypeState"); }
    @Override public void setAutomaticZenRuleState(java.lang.String p0, android.service.notification.Condition p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "setAutomaticZenRuleState"); }
    @Override public void setBubblesAllowed(java.lang.String p0, int p1, int p2) throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "setBubblesAllowed"); }
    @Override public void setCanBePromoted(java.lang.String p0, int p1, boolean p2, boolean p3) throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "setCanBePromoted"); }
    @Override public void setHideSilentStatusIcons(boolean p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "setHideSilentStatusIcons"); }
    @Override public void setInterruptionFilter(java.lang.String p0, int p1, boolean p2) throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "setInterruptionFilter"); }
    @Override public void setInvalidMsgAppDemoted(java.lang.String p0, int p1, boolean p2) throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "setInvalidMsgAppDemoted"); }
    @Override public void setListenerFilter(android.content.ComponentName p0, int p1, android.service.notification.NotificationListenerFilter p2) throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "setListenerFilter"); }
    @Override public void setManualZenRuleDeviceEffects(android.service.notification.ZenDeviceEffects p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "setManualZenRuleDeviceEffects"); }
    @Override public void setNASMigrationDoneAndResetDefault(int p0, boolean p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "setNASMigrationDoneAndResetDefault"); }
    @Override public void setNotificationAssistantAccessGranted(android.content.ComponentName p0, boolean p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "setNotificationAssistantAccessGranted"); }
    @Override public void setNotificationAssistantAccessGrantedForUser(android.content.ComponentName p0, int p1, boolean p2) throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "setNotificationAssistantAccessGrantedForUser"); }
    @Override public void setNotificationDelegate(java.lang.String p0, java.lang.String p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "setNotificationDelegate"); }
    @Override public void setNotificationListenerAccessGranted(android.content.ComponentName p0, boolean p1, boolean p2) throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "setNotificationListenerAccessGranted"); }
    @Override public void setNotificationListenerAccessGrantedForUser(android.content.ComponentName p0, int p1, boolean p2, boolean p3) throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "setNotificationListenerAccessGrantedForUser"); }
    @Override public void setNotificationPolicy(java.lang.String p0, android.app.NotificationManager.Policy p1, boolean p2) throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "setNotificationPolicy"); }
    @Override public void setNotificationPolicyAccessGranted(java.lang.String p0, boolean p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "setNotificationPolicyAccessGranted"); }
    @Override public void setNotificationPolicyAccessGrantedForUser(java.lang.String p0, int p1, boolean p2) throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "setNotificationPolicyAccessGrantedForUser"); }
    @Override public void setNotificationsEnabledForPackage(java.lang.String p0, int p1, boolean p2) throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "setNotificationsEnabledForPackage"); }
    @Override public void setNotificationsEnabledWithImportanceLockForPackage(java.lang.String p0, int p1, boolean p2) throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "setNotificationsEnabledWithImportanceLockForPackage"); }
    @Override public void setNotificationsShownFromListener(android.service.notification.INotificationListener p0, java.lang.String[] p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "setNotificationsShownFromListener"); }
    @Override public void setOnNotificationPostedTrimFromListener(android.service.notification.INotificationListener p0, int p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "setOnNotificationPostedTrimFromListener"); }
    @Override public void setPrivateNotificationsAllowed(boolean p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "setPrivateNotificationsAllowed"); }
    @Override public void setShowBadge(java.lang.String p0, int p1, boolean p2) throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "setShowBadge"); }
    @Override public void setToastRateLimitingEnabled(boolean p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "setToastRateLimitingEnabled"); }
    @Override public void setTypeAdjustmentForPackageState(java.lang.String p0, boolean p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "setTypeAdjustmentForPackageState"); }
    @Override public void setZenMode(int p0, android.net.Uri p1, java.lang.String p2, boolean p3) throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "setZenMode"); }
    @Override public boolean shouldHideSilentStatusIcons(java.lang.String p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "shouldHideSilentStatusIcons"); }
    @Override public void silenceNotificationSound() throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "silenceNotificationSound"); }
    @Override public void snoozeNotificationUntilContextFromListener(android.service.notification.INotificationListener p0, java.lang.String p1, java.lang.String p2) throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "snoozeNotificationUntilContextFromListener"); }
    @Override public void snoozeNotificationUntilFromListener(android.service.notification.INotificationListener p0, java.lang.String p1, long p2) throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "snoozeNotificationUntilFromListener"); }
    @Override public void unlockAllNotificationChannels() throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "unlockAllNotificationChannels"); }
    @Override public void unlockNotificationChannel(java.lang.String p0, int p1, java.lang.String p2) throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "unlockNotificationChannel"); }
    @Override public void unregisterCallNotificationEventListener(java.lang.String p0, android.os.UserHandle p1, android.app.ICallNotificationEventCallback p2) throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "unregisterCallNotificationEventListener"); }
    @Override public void unregisterListener(android.service.notification.INotificationListener p0, int p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "unregisterListener"); }
    @Override public void unsnoozeNotificationFromAssistant(android.service.notification.INotificationListener p0, java.lang.String p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "unsnoozeNotificationFromAssistant"); }
    @Override public void unsnoozeNotificationFromSystemListener(android.service.notification.INotificationListener p0, java.lang.String p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "unsnoozeNotificationFromSystemListener"); }
    @Override public boolean updateAutomaticZenRule(java.lang.String p0, android.app.AutomaticZenRule p1, boolean p2) throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "updateAutomaticZenRule"); }
    @Override public void updateNotificationChannelForPackage(java.lang.String p0, int p1, android.app.NotificationChannel p2) throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "updateNotificationChannelForPackage"); }
    @Override public void updateNotificationChannelFromPrivilegedListener(android.service.notification.INotificationListener p0, java.lang.String p1, android.os.UserHandle p2, android.app.NotificationChannel p3) throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "updateNotificationChannelFromPrivilegedListener"); }
    @Override public void updateNotificationChannelGroupForPackage(java.lang.String p0, int p1, android.app.NotificationChannelGroup p2) throws android.os.RemoteException { throw ServiceMethodMissing.fail("notification", "updateNotificationChannelGroupForPackage"); }

    @Override
    public String toString() {
        return "WestlakeNotificationManagerService{}";
    }
}
