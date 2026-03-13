package android.app;

public class NotificationManager {
    public NotificationManager() {}

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
    public static final int IMPORTANCE_DEFAULT = 0;
    public static final int IMPORTANCE_HIGH = 0;
    public static final int IMPORTANCE_LOW = 0;
    public static final int IMPORTANCE_MAX = 0;
    public static final int IMPORTANCE_MIN = 0;
    public static final int IMPORTANCE_NONE = 0;
    public static final int IMPORTANCE_UNSPECIFIED = 0;
    public static final int INTERRUPTION_FILTER_ALARMS = 0;
    public static final int INTERRUPTION_FILTER_ALL = 0;
    public static final int INTERRUPTION_FILTER_NONE = 0;
    public static final int INTERRUPTION_FILTER_PRIORITY = 0;
    public static final int INTERRUPTION_FILTER_UNKNOWN = 0;
    public static final int META_DATA_AUTOMATIC_RULE_TYPE = 0;
    public static final int META_DATA_RULE_INSTANCE_LIMIT = 0;
    public Object addAutomaticZenRule(Object p0) { return null; }
    public boolean areBubblesAllowed() { return false; }
    public boolean areNotificationsEnabled() { return false; }
    public boolean areNotificationsPaused() { return false; }
    public boolean canNotifyAsPackage(Object p0) { return false; }
    public void cancel(Object p0) {}
    public void cancel(Object p0, Object p1) {}
    public void cancelAll() {}
    public void cancelAsPackage(Object p0, Object p1, Object p2) {}
    public void createNotificationChannel(Object p0) {}
    public void createNotificationChannelGroup(Object p0) {}
    public void createNotificationChannelGroups(Object p0) {}
    public void createNotificationChannels(Object p0) {}
    public void deleteNotificationChannel(Object p0) {}
    public void deleteNotificationChannelGroup(Object p0) {}
    public Object getActiveNotifications() { return null; }
    public Object getAutomaticZenRule(Object p0) { return null; }
    public Object getAutomaticZenRules() { return null; }
    public int getCurrentInterruptionFilter() { return 0; }
    public int getImportance() { return 0; }
    public Object getNotificationChannel(Object p0) { return null; }
    public Object getNotificationChannelGroup(Object p0) { return null; }
    public Object getNotificationChannelGroups() { return null; }
    public Object getNotificationChannels() { return null; }
    public Object getNotificationPolicy() { return null; }
    public boolean isNotificationListenerAccessGranted(Object p0) { return false; }
    public boolean isNotificationPolicyAccessGranted() { return false; }
    public void notify(Object p0, Object p1) {}
    public void notify(Object p0, Object p1, Object p2) {}
    public void notifyAsPackage(Object p0, Object p1, Object p2, Object p3) {}
    public boolean removeAutomaticZenRule(Object p0) { return false; }
    public void setAutomaticZenRuleState(Object p0, Object p1) {}
    public void setInterruptionFilter(Object p0) {}
    public void setNotificationDelegate(Object p0) {}
    public void setNotificationPolicy(Object p0) {}
    public boolean shouldHideSilentStatusBarIcons() { return false; }
    public boolean updateAutomaticZenRule(Object p0, Object p1) { return false; }
}
