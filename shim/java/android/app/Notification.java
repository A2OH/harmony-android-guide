package android.app;

import android.content.Context;

/**
 * Shim: android.app.Notification — data container for notification content.
 * Built via Notification.Builder (or NotificationCompat.Builder from AndroidX shim).
 */
public class Notification {
    // Priority constants
    public static final int PRIORITY_MIN = -2;
    public static final int PRIORITY_LOW = -1;
    public static final int PRIORITY_DEFAULT = 0;
    public static final int PRIORITY_HIGH = 1;
    public static final int PRIORITY_MAX = 2;

    // Package-visible fields used by NotificationManager shim
    String channelId;
    String title;
    String text;
    int priority = PRIORITY_DEFAULT;
    int smallIcon;
    boolean autoCancel;
    PendingIntent contentIntent;

    /**
     * Notification.Builder — the standard way to construct notifications.
     * Maps to OH NotificationRequest structure.
     */
    public static class Builder {
        private final Context context;
        private String channelId = "default";
        private String title;
        private String text;
        private int priority = PRIORITY_DEFAULT;
        private int smallIcon;
        private boolean autoCancel;
        private PendingIntent contentIntent;

        public Builder(Context context, String channelId) {
            this.context = context;
            this.channelId = channelId;
        }

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setContentTitle(CharSequence title) {
            this.title = title != null ? title.toString() : null;
            return this;
        }

        public Builder setContentText(CharSequence text) {
            this.text = text != null ? text.toString() : null;
            return this;
        }

        public Builder setSmallIcon(int icon) {
            this.smallIcon = icon;
            return this;
        }

        public Builder setPriority(int priority) {
            this.priority = priority;
            return this;
        }

        public Builder setAutoCancel(boolean autoCancel) {
            this.autoCancel = autoCancel;
            return this;
        }

        public Builder setContentIntent(PendingIntent intent) {
            this.contentIntent = intent;
            return this;
        }

        public Builder setChannelId(String channelId) {
            this.channelId = channelId;
            return this;
        }

        public Notification build() {
            Notification n = new Notification();
            n.channelId = this.channelId;
            n.title = this.title;
            n.text = this.text;
            n.priority = this.priority;
            n.smallIcon = this.smallIcon;
            n.autoCancel = this.autoCancel;
            n.contentIntent = this.contentIntent;
            return n;
        }
    }
}
