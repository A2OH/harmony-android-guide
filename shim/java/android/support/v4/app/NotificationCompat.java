package android.support.v4.app;

import android.app.Notification;
import android.content.Context;
import android.graphics.Bitmap;

/**
 * Android-compatible NotificationCompat shim (AndroidX/support-v4).
 * Delegates to the android.app.Notification shim.
 *
 * NotificationCompat.Builder is the primary way to build notifications in
 * support-library-using apps. It wraps Notification.Builder with backward-compat
 * priority constants and large icon support.
 */
public class NotificationCompat {

    public static final int PRIORITY_DEFAULT = Notification.PRIORITY_DEFAULT;
    public static final int PRIORITY_HIGH    = Notification.PRIORITY_HIGH;
    public static final int PRIORITY_LOW     = Notification.PRIORITY_LOW;
    public static final int PRIORITY_MIN     = Notification.PRIORITY_MIN;
    public static final int PRIORITY_MAX     = Notification.PRIORITY_MAX;

    private NotificationCompat() {}

    /**
     * NotificationCompat.Builder — mirrors the AndroidX builder API.
     * Maps to OH NotificationRequest via the Notification shim.
     */
    public static class Builder {
        private final Notification.Builder mDelegate;
        private android.graphics.Bitmap mLargeIcon;

        public Builder(Context context, String channelId) {
            mDelegate = new Notification.Builder(context, channelId);
        }

        public Builder(Context context) {
            mDelegate = new Notification.Builder(context);
        }

        public Builder setContentTitle(CharSequence title) {
            mDelegate.setContentTitle(title);
            return this;
        }

        public Builder setContentText(CharSequence text) {
            mDelegate.setContentText(text);
            return this;
        }

        public Builder setSmallIcon(int icon) {
            mDelegate.setSmallIcon(icon);
            return this;
        }

        public Builder setLargeIcon(android.graphics.Bitmap bitmap) {
            // Large icon is not mapped in the OH shim — stored for API completeness.
            mLargeIcon = bitmap;
            return this;
        }

        public Builder setAutoCancel(boolean autoCancel) {
            mDelegate.setAutoCancel(autoCancel);
            return this;
        }

        public Builder setPriority(int priority) {
            mDelegate.setPriority(priority);
            return this;
        }

        public Builder setChannelId(String channelId) {
            mDelegate.setChannelId(channelId);
            return this;
        }

        public Notification build() {
            return mDelegate.build();
        }
    }
}
