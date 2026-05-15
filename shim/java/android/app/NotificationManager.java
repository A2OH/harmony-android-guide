package android.app;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Shim: android.app.NotificationManager → OH ANS (Advanced Notification Service)
 *
 * Bridges to OHBridge via reflection to avoid Dalvik crash from System.loadLibrary
 * in the static initializer of OHBridge.
 */
public class NotificationManager {

    // ── Track active notification IDs and channels ──────────────
    private final Set<Integer> activeIds = new HashSet<Integer>();
    private final List<NotificationChannel> channels = new ArrayList<NotificationChannel>();

    public NotificationManager() {}

    // ── Importance constants (match Android values) ─────────────
    public static final int IMPORTANCE_UNSPECIFIED = -1000;
    public static final int IMPORTANCE_NONE = 0;
    public static final int IMPORTANCE_MIN = 1;
    public static final int IMPORTANCE_LOW = 2;
    public static final int IMPORTANCE_DEFAULT = 3;
    public static final int IMPORTANCE_HIGH = 4;
    public static final int IMPORTANCE_MAX = 5;

    // ── Other constants (stubs) ─────────────────────────────────
    public static final int ACTION_APP_BLOCK_STATE_CHANGED = 0;
    public static final int ACTION_AUTOMATIC_ZEN_RULE = 0;
    public static final int ACTION_AUTOMATIC_ZEN_RULE_STATUS_CHANGED = 0;
    public static final int ACTION_INTERRUPTION_FILTER_CHANGED = 0;
    public static final int ACTION_NOTIFICATION_CHANNEL_BLOCK_STATE_CHANGED = 0;
    public static final int ACTION_NOTIFICATION_CHANNEL_GROUP_BLOCK_STATE_CHANGED = 0;
    public static final int ACTION_NOTIFICATION_POLICY_ACCESS_GRANTED_CHANGED = 0;
    public static final int ACTION_NOTIFICATION_POLICY_CHANGED = 0;
    public static final int AUTOMATIC_RULE_STATUS_DISABLED = 0;
    public static final int AUTOMATIC_RULE_STATUS_ENABLED = 0;
    public static final int AUTOMATIC_RULE_STATUS_REMOVED = 0;
    public static final int AUTOMATIC_RULE_STATUS_UNKNOWN = 0;
    public static final int EXTRA_AUTOMATIC_RULE_ID = 0;
    public static final int EXTRA_AUTOMATIC_ZEN_RULE_ID = 0;
    public static final int EXTRA_AUTOMATIC_ZEN_RULE_STATUS = 0;
    public static final int EXTRA_BLOCKED_STATE = 0;
    public static final int EXTRA_NOTIFICATION_CHANNEL_GROUP_ID = 0;
    public static final int EXTRA_NOTIFICATION_CHANNEL_ID = 0;
    public static final int INTERRUPTION_FILTER_ALARMS = 0;
    public static final int INTERRUPTION_FILTER_ALL = 0;
    public static final int INTERRUPTION_FILTER_NONE = 0;
    public static final int INTERRUPTION_FILTER_PRIORITY = 0;
    public static final int INTERRUPTION_FILTER_UNKNOWN = 0;
    public static final int META_DATA_AUTOMATIC_RULE_TYPE = 0;
    public static final int META_DATA_RULE_INSTANCE_LIMIT = 0;

    // ── Reflection-based bridge call (avoids direct OHBridge import) ──

    private static Object callBridge(String methodName, Class<?>[] types, Object... args) {
        try {
            Class<?> c = Class.forName("com.ohos.shim.bridge.OHBridge");
            return c.getMethod(methodName, types).invoke(null, args);
        } catch (Throwable t) {
            return null;
        }
    }

    // ── Core notification methods ───────────────────────────────

    /**
     * Post a notification to be shown in the status bar.
     */
    public void notify(int id, Notification notification) {
        if (notification == null) return;
        String title = notification.shimTitle != null ? notification.shimTitle : "";
        String text = notification.shimText != null ? notification.shimText : "";
        String channelId = notification.shimChannelId != null ? notification.shimChannelId : "";
        int priority = notification.shimPriority;

        callBridge("notificationPublish",
                new Class<?>[]{ int.class, String.class, String.class, String.class, int.class },
                id, title, text, channelId, priority);
        activeIds.add(Integer.valueOf(id));
    }

    /**
     * Post a notification to be shown in the status bar (with tag).
     * The tag is ignored on the OH side; we use only the numeric id.
     */
    public void notify(String tag, int id, Notification notification) {
        notify(id, notification);
    }

    /**
     * Cancel a previously shown notification by id.
     */
    public void cancel(int id) {
        callBridge("notificationCancel",
                new Class<?>[]{ int.class },
                id);
        activeIds.remove(Integer.valueOf(id));
    }

    /**
     * Cancel a previously shown notification by tag and id.
     */
    public void cancel(String tag, int id) {
        cancel(id);
    }

    /**
     * Cancel all previously shown notifications.
     */
    public void cancelAll() {
        for (Integer id : new ArrayList<Integer>(activeIds)) {
            callBridge("notificationCancel",
                    new Class<?>[]{ int.class },
                    id.intValue());
        }
        activeIds.clear();
    }

    // ── Channel management ──────────────────────────────────────

    /**
     * Creates a notification channel (maps to OH notification slot).
     */
    public void createNotificationChannel(NotificationChannel channel) {
        if (channel == null) return;
        String id = channel.getId() != null ? channel.getId() : "";
        String name = channel.getName() != null ? channel.getName().toString() : "";
        int importance = channel.getImportance();

        callBridge("notificationAddSlot",
                new Class<?>[]{ String.class, String.class, int.class },
                id, name, importance);

        // Track locally (replace if same id already exists)
        for (int i = 0; i < channels.size(); i++) {
            if (id.equals(channels.get(i).getId())) {
                channels.set(i, channel);
                return;
            }
        }
        channels.add(channel);
    }

    /**
     * Creates multiple notification channels at once.
     */
    public void createNotificationChannels(List<NotificationChannel> channelList) {
        if (channelList == null) return;
        for (NotificationChannel ch : channelList) {
            createNotificationChannel(ch);
        }
    }

    /**
     * Returns all notification channels belonging to the calling package.
     */
    public List<NotificationChannel> getNotificationChannels() {
        return new ArrayList<NotificationChannel>(channels);
    }

    /**
     * Returns a specific notification channel by id.
     */
    public NotificationChannel getNotificationChannel(String channelId) {
        if (channelId == null) return null;
        for (NotificationChannel ch : channels) {
            if (channelId.equals(ch.getId())) {
                return ch;
            }
        }
        return null;
    }

    /**
     * Deletes a notification channel by id.
     */
    public void deleteNotificationChannel(String channelId) {
        if (channelId == null) return;
        for (int i = 0; i < channels.size(); i++) {
            if (channelId.equals(channels.get(i).getId())) {
                channels.remove(i);
                return;
            }
        }
    }

    // ── Stub methods (non-essential, return defaults) ───────────

    public boolean areNotificationsEnabled() { return true; }
    public boolean areNotificationsPaused() { return false; }
    public boolean areBubblesAllowed() { return false; }
    public boolean canNotifyAsPackage(String pkg) { return false; }
    public int getImportance() { return IMPORTANCE_DEFAULT; }
    public int getCurrentInterruptionFilter() { return 0; }
    public Object getActiveNotifications() { return null; }
    public Object getNotificationPolicy() { return null; }
    public boolean isNotificationListenerAccessGranted(Object component) { return false; }
    public boolean isNotificationPolicyAccessGranted() { return false; }
    public boolean shouldHideSilentStatusBarIcons() { return false; }

    public Object addAutomaticZenRule(Object rule) { return null; }
    public Object getAutomaticZenRule(Object id) { return null; }
    public Object getAutomaticZenRules() { return null; }
    public boolean removeAutomaticZenRule(Object ruleId) { return false; }
    public boolean updateAutomaticZenRule(Object ruleId, Object rule) { return false; }

    public void cancelAsPackage(String targetPackage, String tag, int id) {}
    public void createNotificationChannelGroup(Object group) {}
    public void createNotificationChannelGroups(Object groups) {}
    public void deleteNotificationChannelGroup(String groupId) {}
    public Object getNotificationChannelGroup(String groupId) { return null; }
    public Object getNotificationChannelGroups() { return null; }
    public void notifyAsPackage(String targetPackage, String tag, int id, Notification notification) {}
    public void setAutomaticZenRuleState(Object ruleId, Object condition) {}
    public void setInterruptionFilter(int interruptionFilter) {}
    public void setNotificationDelegate(Object delegate) {}
    public void setNotificationPolicy(Object policy) {}

    // ------------------------------------------------------------------
    // Compile-time stub for the nested Policy class.  M4e's
    // INotificationManager / WestlakeNotificationManagerService AIDL surface
    // references android.app.NotificationManager.Policy in three method
    // signatures (getConsolidatedNotificationPolicy,
    // getNotificationPolicy(String), setNotificationPolicy(String, Policy,
    // boolean)).  This nested class supplies enough surface for javac to
    // resolve those signatures.  At RUNTIME the duplicates list strips this
    // outer NotificationManager (and therefore Policy) from aosp-shim.dex so
    // framework.jar's real Policy wins.  See scripts/framework_duplicates.txt
    // line "android/app/NotificationManager".
    // ------------------------------------------------------------------
    public static class Policy {
        public int priorityCategories;
        public int priorityCallSenders;
        public int priorityMessageSenders;
        public int suppressedVisualEffects;
        public int state;
        public int priorityConversationSenders;

        public Policy() {}

        public Policy(int priorityCategories,
                int priorityCallSenders,
                int priorityMessageSenders) {
            this.priorityCategories = priorityCategories;
            this.priorityCallSenders = priorityCallSenders;
            this.priorityMessageSenders = priorityMessageSenders;
        }

        public Policy(int priorityCategories,
                int priorityCallSenders,
                int priorityMessageSenders,
                int suppressedVisualEffects) {
            this(priorityCategories, priorityCallSenders, priorityMessageSenders);
            this.suppressedVisualEffects = suppressedVisualEffects;
        }
    }
}
