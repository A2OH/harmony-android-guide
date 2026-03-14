package android.app;
import android.graphics.drawable.Icon;
import android.os.Parcel;
import android.os.Parcelable;

public class Notification implements Parcelable {
    public static final int AUDIO_ATTRIBUTES_DEFAULT = 0;
    public static final int BADGE_ICON_LARGE = 0;
    public static final int BADGE_ICON_NONE = 0;
    public static final int BADGE_ICON_SMALL = 0;
    public static final int CATEGORY_ALARM = 0;
    public static final int CATEGORY_CALL = 0;
    public static final int CATEGORY_EMAIL = 0;
    public static final int CATEGORY_ERROR = 0;
    public static final int CATEGORY_EVENT = 0;
    public static final int CATEGORY_MESSAGE = 0;
    public static final int CATEGORY_NAVIGATION = 0;
    public static final int CATEGORY_PROGRESS = 0;
    public static final int CATEGORY_PROMO = 0;
    public static final int CATEGORY_RECOMMENDATION = 0;
    public static final int CATEGORY_REMINDER = 0;
    public static final int CATEGORY_SERVICE = 0;
    public static final int CATEGORY_SOCIAL = 0;
    public static final int CATEGORY_STATUS = 0;
    public static final int CATEGORY_SYSTEM = 0;
    public static final int CATEGORY_TRANSPORT = 0;
    public static final int DEFAULT_ALL = 0;
    public static final int DEFAULT_LIGHTS = 0;
    public static final int DEFAULT_SOUND = 0;
    public static final int DEFAULT_VIBRATE = 0;
    public static final int EXTRA_AUDIO_CONTENTS_URI = 0;
    public static final int EXTRA_BACKGROUND_IMAGE_URI = 0;
    public static final int EXTRA_BIG_TEXT = 0;
    public static final int EXTRA_CHANNEL_GROUP_ID = 0;
    public static final int EXTRA_CHANNEL_ID = 0;
    public static final int EXTRA_CHRONOMETER_COUNT_DOWN = 0;
    public static final int EXTRA_COLORIZED = 0;
    public static final int EXTRA_COMPACT_ACTIONS = 0;
    public static final int EXTRA_CONVERSATION_TITLE = 0;
    public static final int EXTRA_HISTORIC_MESSAGES = 0;
    public static final int EXTRA_INFO_TEXT = 0;
    public static final int EXTRA_IS_GROUP_CONVERSATION = 0;
    public static final int EXTRA_LARGE_ICON_BIG = 0;
    public static final int EXTRA_MEDIA_SESSION = 0;
    public static final int EXTRA_MESSAGES = 0;
    public static final int EXTRA_MESSAGING_PERSON = 0;
    public static final int EXTRA_NOTIFICATION_ID = 0;
    public static final int EXTRA_NOTIFICATION_TAG = 0;
    public static final int EXTRA_PEOPLE_LIST = 0;
    public static final int EXTRA_PICTURE = 0;
    public static final int EXTRA_PROGRESS = 0;
    public static final int EXTRA_PROGRESS_INDETERMINATE = 0;
    public static final int EXTRA_PROGRESS_MAX = 0;
    public static final int EXTRA_REMOTE_INPUT_DRAFT = 0;
    public static final int EXTRA_REMOTE_INPUT_HISTORY = 0;
    public static final int EXTRA_SHOW_CHRONOMETER = 0;
    public static final int EXTRA_SHOW_WHEN = 0;
    public static final int EXTRA_SUB_TEXT = 0;
    public static final int EXTRA_SUMMARY_TEXT = 0;
    public static final int EXTRA_TEMPLATE = 0;
    public static final int EXTRA_TEXT = 0;
    public static final int EXTRA_TEXT_LINES = 0;
    public static final int EXTRA_TITLE = 0;
    public static final int EXTRA_TITLE_BIG = 0;
    public static final int FLAG_AUTO_CANCEL = 0;
    public static final int FLAG_BUBBLE = 0;
    public static final int FLAG_FOREGROUND_SERVICE = 0;
    public static final int FLAG_GROUP_SUMMARY = 0;
    public static final int FLAG_INSISTENT = 0;
    public static final int FLAG_LOCAL_ONLY = 0;
    public static final int FLAG_NO_CLEAR = 0;
    public static final int FLAG_ONGOING_EVENT = 0;
    public static final int FLAG_ONLY_ALERT_ONCE = 0;
    public static final int GROUP_ALERT_ALL = 0;
    public static final int GROUP_ALERT_CHILDREN = 0;
    public static final int GROUP_ALERT_SUMMARY = 0;
    public static final int INTENT_CATEGORY_NOTIFICATION_PREFERENCES = 0;
    public static final int VISIBILITY_PRIVATE = 0;
    public static final int VISIBILITY_PUBLIC = 0;
    public static final int VISIBILITY_SECRET = 0;
    public int actions = 0;
    public int category = 0;
    public int contentIntent = 0;
    public int deleteIntent = 0;
    public int extras = 0;
    public int flags = 0;
    public int fullScreenIntent = 0;
    public int iconLevel = 0;
    public int number = 0;
    public int publicVersion = 0;
    public int tickerText = 0;
    public int visibility = 0;
    public int when = 0;

    // ── Shim fields for bridge data extraction ──────────────────
    String shimTitle;
    String shimText;
    String shimChannelId;
    int shimPriority;

    public Notification() {}
    public Notification(Parcel p0) {}

    public Notification clone() { return null; }
    public int describeContents() { return 0; }
    public boolean getAllowSystemGeneratedContextualActions() { return false; }
    public int getBadgeIconType() { return 0; }
    public String getChannelId() { return shimChannelId; }
    public String getGroup() { return null; }
    public int getGroupAlertBehavior() { return 0; }
    public Icon getLargeIcon() { return null; }
    public CharSequence getSettingsText() { return null; }
    public String getShortcutId() { return null; }
    public Icon getSmallIcon() { return null; }
    public String getSortKey() { return null; }
    public long getTimeoutAfter() { return 0L; }
    public void writeToParcel(Parcel p0, int p1) {}

    // ── Builder (commonly used by Android apps) ─────────────────
    public static class Builder {
        private String title;
        private String text;
        private String channelId;
        private int priority;
        private int smallIcon;

        public Builder(android.content.Context context, String channelId) {
            this.channelId = channelId;
        }

        /** @deprecated Use Builder(Context, String) instead. */
        public Builder(android.content.Context context) {
            this.channelId = "";
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

        public Builder setChannelId(String channelId) {
            this.channelId = channelId;
            return this;
        }

        public Builder setPriority(int priority) {
            this.priority = priority;
            return this;
        }

        public Builder setAutoCancel(boolean autoCancel) { return this; }
        public Builder setOngoing(boolean ongoing) { return this; }
        public Builder setContentIntent(Object intent) { return this; }
        public Builder setDeleteIntent(Object intent) { return this; }
        public Builder setCategory(String category) { return this; }
        public Builder setVisibility(int visibility) { return this; }
        public Builder setGroup(String groupKey) { return this; }
        public Builder setGroupSummary(boolean isGroupSummary) { return this; }
        public Builder setSortKey(String sortKey) { return this; }
        public Builder setStyle(Object style) { return this; }
        public Builder setColor(int argb) { return this; }
        public Builder setNumber(int number) { return this; }
        public Builder setSubText(CharSequence text) { return this; }
        public Builder setShowWhen(boolean show) { return this; }
        public Builder setWhen(long when) { return this; }
        public Builder setUsesChronometer(boolean b) { return this; }
        public Builder setOnlyAlertOnce(boolean onlyAlertOnce) { return this; }
        public Builder setTicker(CharSequence tickerText) { return this; }
        public Builder setLargeIcon(Object icon) { return this; }
        public Builder setDefaults(int defaults) { return this; }
        public Builder setVibrate(long[] pattern) { return this; }
        public Builder setSound(Object sound) { return this; }
        public Builder setLights(int argb, int onMs, int offMs) { return this; }
        public Builder addAction(int icon, CharSequence title, Object intent) { return this; }

        public Notification build() {
            Notification n = new Notification();
            n.shimTitle = this.title;
            n.shimText = this.text;
            n.shimChannelId = this.channelId;
            n.shimPriority = this.priority;
            return n;
        }
    }
}
