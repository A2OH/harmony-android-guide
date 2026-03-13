package android.app;

import com.ohos.shim.bridge.OHBridge;

/**
 * Shim: android.app.NotificationManager → @ohos.notificationManager
 * Tier 1 — near-direct mapping.
 */
public class NotificationManager {
    public static final int IMPORTANCE_NONE = 0;
    public static final int IMPORTANCE_MIN = 1;
    public static final int IMPORTANCE_LOW = 2;
    public static final int IMPORTANCE_DEFAULT = 3;
    public static final int IMPORTANCE_HIGH = 4;

    public void notify(int id, Notification notification) {
        OHBridge.notificationPublish(
            id,
            notification.title != null ? notification.title : "",
            notification.text != null ? notification.text : "",
            notification.channelId != null ? notification.channelId : "default",
            notification.priority
        );
    }

    public void notify(String tag, int id, Notification notification) {
        // tag is not supported in OH — use id only
        notify(id, notification);
    }

    public void cancel(int id) {
        OHBridge.notificationCancel(id);
    }

    public void cancel(String tag, int id) {
        cancel(id);
    }

    public void cancelAll() {
        // TODO: OH notificationManager.cancelAll()
    }

    public void createNotificationChannel(NotificationChannel channel) {
        OHBridge.notificationAddSlot(
            channel.getId(),
            channel.getName() != null ? channel.getName().toString() : channel.getId(),
            channel.getImportance()
        );
    }
}
