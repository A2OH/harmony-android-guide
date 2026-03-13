package android.service.notification;
import android.service.controls.Control;
import android.service.controls.Control;

/**
 * Android-compatible NotificationListenerService shim. Stub — no-op implementation.
 */
public class NotificationListenerService {

    // --- Lifecycle callbacks ---

    public void onListenerConnected() {}

    public void onListenerDisconnected() {}

    public void onNotificationPosted(StatusBarNotification sbn) {}

    public void onNotificationPosted(StatusBarNotification sbn, RankingMap rankingMap) {}

    public void onNotificationRemoved(StatusBarNotification sbn) {}

    public void onNotificationRemoved(StatusBarNotification sbn, RankingMap rankingMap) {}

    public void onNotificationRankingUpdate(RankingMap rankingMap) {}

    // --- Control methods ---

    public void cancelNotification(String key) {}

    public void cancelAllNotifications() {}

    public StatusBarNotification[] getActiveNotifications() { return new StatusBarNotification[0]; }

    // --- Inner classes ---

    public static class StatusBarNotification {
        private String key;
        private String packageName;

        public StatusBarNotification() {}

        public String getKey() { return key; }
        public String getPackageName() { return packageName; }
        public boolean isClearable() { return true; }
        public boolean isOngoing() { return false; }
        public long getPostTime() { return 0L; }
        public int getId() { return 0; }
        public String getTag() { return null; }
    }

    public static class Ranking {
        public static final int IMPORTANCE_NONE = 0;
        public static final int IMPORTANCE_MIN = 1;
        public static final int IMPORTANCE_LOW = 2;
        public static final int IMPORTANCE_DEFAULT = 3;
        public static final int IMPORTANCE_HIGH = 4;
        public static final int IMPORTANCE_MAX = 5;

        public Ranking() {}

        public int getImportance() { return IMPORTANCE_DEFAULT; }
        public boolean canShowBadge() { return true; }
        public boolean canBubble() { return false; }
        public boolean isSuspended() { return false; }
    }

    public static class RankingMap {
        private Ranking[] rankings;

        public RankingMap() { this.rankings = new Ranking[0]; }

        public boolean getRanking(String key, Ranking outRanking) { return false; }

        public String[] getOrderedKeys() { return new String[0]; }
    }
}
