package android.app;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;

public class Notification implements Parcelable {
    // ── Audio attributes ─────────────────────────────────────────
    public static final int AUDIO_ATTRIBUTES_DEFAULT = 0;

    // ── Badge icon types ─────────────────────────────────────────
    public static final int BADGE_ICON_NONE = 0;
    public static final int BADGE_ICON_SMALL = 1;
    public static final int BADGE_ICON_LARGE = 2;

    // ── Category constants (String, per AOSP) ────────────────────
    public static final String CATEGORY_ALARM = "alarm";
    public static final String CATEGORY_CALL = "call";
    public static final String CATEGORY_EMAIL = "email";
    public static final String CATEGORY_ERROR = "err";
    public static final String CATEGORY_EVENT = "event";
    public static final String CATEGORY_MESSAGE = "msg";
    public static final String CATEGORY_NAVIGATION = "navigation";
    public static final String CATEGORY_PROGRESS = "progress";
    public static final String CATEGORY_PROMO = "promo";
    public static final String CATEGORY_RECOMMENDATION = "recommendation";
    public static final String CATEGORY_REMINDER = "reminder";
    public static final String CATEGORY_SERVICE = "service";
    public static final String CATEGORY_SOCIAL = "social";
    public static final String CATEGORY_STATUS = "status";
    public static final String CATEGORY_SYSTEM = "sys";
    public static final String CATEGORY_TRANSPORT = "transport";

    // ── Defaults ─────────────────────────────────────────────────
    public static final int DEFAULT_SOUND = 1;
    public static final int DEFAULT_VIBRATE = 2;
    public static final int DEFAULT_LIGHTS = 4;
    public static final int DEFAULT_ALL = ~0;

    // ── Extra keys (String, per AOSP) ────────────────────────────
    public static final String EXTRA_AUDIO_CONTENTS_URI = "android.audioContents";
    public static final String EXTRA_BACKGROUND_IMAGE_URI = "android.backgroundImageUri";
    public static final String EXTRA_BIG_TEXT = "android.bigText";
    public static final String EXTRA_CHANNEL_GROUP_ID = "android.channelGroupId";
    public static final String EXTRA_CHANNEL_ID = "android.channelId";
    public static final String EXTRA_CHRONOMETER_COUNT_DOWN = "android.chronometerCountDown";
    public static final String EXTRA_COLORIZED = "android.colorized";
    public static final String EXTRA_COMPACT_ACTIONS = "android.compactActions";
    public static final String EXTRA_CONVERSATION_TITLE = "android.conversationTitle";
    public static final String EXTRA_HISTORIC_MESSAGES = "android.messages.historic";
    public static final String EXTRA_INFO_TEXT = "android.infoText";
    public static final String EXTRA_IS_GROUP_CONVERSATION = "android.isGroupConversation";
    public static final String EXTRA_LARGE_ICON_BIG = "android.largeIcon.big";
    public static final String EXTRA_MEDIA_SESSION = "android.mediaSession";
    public static final String EXTRA_MESSAGES = "android.messages";
    public static final String EXTRA_MESSAGING_PERSON = "android.messagingUser";
    public static final String EXTRA_NOTIFICATION_ID = "android.notification.id";
    public static final String EXTRA_NOTIFICATION_TAG = "android.notification.tag";
    public static final String EXTRA_PEOPLE_LIST = "android.people.list";
    public static final String EXTRA_PICTURE = "android.picture";
    public static final String EXTRA_PROGRESS = "android.progress";
    public static final String EXTRA_PROGRESS_INDETERMINATE = "android.progressIndeterminate";
    public static final String EXTRA_PROGRESS_MAX = "android.progressMax";
    public static final String EXTRA_REMOTE_INPUT_DRAFT = "android.remoteInputDraft";
    public static final String EXTRA_REMOTE_INPUT_HISTORY = "android.remoteInputHistory";
    public static final String EXTRA_SHOW_CHRONOMETER = "android.showChronometer";
    public static final String EXTRA_SHOW_WHEN = "android.showWhen";
    public static final String EXTRA_SUB_TEXT = "android.subText";
    public static final String EXTRA_SUMMARY_TEXT = "android.summaryText";
    public static final String EXTRA_TEMPLATE = "android.template";
    public static final String EXTRA_TEXT = "android.text";
    public static final String EXTRA_TEXT_LINES = "android.textLines";
    public static final String EXTRA_TITLE = "android.title";
    public static final String EXTRA_TITLE_BIG = "android.title.big";

    // ── Flags (bit flags, per AOSP) ──────────────────────────────
    public static final int FLAG_ONGOING_EVENT = 0x00000002;
    public static final int FLAG_INSISTENT = 0x00000004;
    public static final int FLAG_ONLY_ALERT_ONCE = 0x00000008;
    public static final int FLAG_AUTO_CANCEL = 0x00000010;
    public static final int FLAG_NO_CLEAR = 0x00000020;
    public static final int FLAG_FOREGROUND_SERVICE = 0x00000040;
    public static final int FLAG_LOCAL_ONLY = 0x00000100;
    public static final int FLAG_GROUP_SUMMARY = 0x00000200;
    public static final int FLAG_BUBBLE = 0x00001000;

    // ── Group alert behavior ─────────────────────────────────────
    public static final int GROUP_ALERT_ALL = 0;
    public static final int GROUP_ALERT_CHILDREN = 1;
    public static final int GROUP_ALERT_SUMMARY = 2;

    // ── Intent category (String, per AOSP) ───────────────────────
    public static final String INTENT_CATEGORY_NOTIFICATION_PREFERENCES = "android.intent.category.NOTIFICATION_PREFERENCES";

    // ── Visibility ───────────────────────────────────────────────
    public static final int VISIBILITY_PUBLIC = 1;
    public static final int VISIBILITY_PRIVATE = 0;
    public static final int VISIBILITY_SECRET = -1;

    // ── Priority (deprecated but still used) ─────────────────────
    public static final int PRIORITY_DEFAULT = 0;
    public static final int PRIORITY_LOW = -1;
    public static final int PRIORITY_MIN = -2;
    public static final int PRIORITY_HIGH = 1;
    public static final int PRIORITY_MAX = 2;

    // ── Public instance fields (matching AOSP signatures) ────────
    public Action[] actions;
    public String category;
    public PendingIntent contentIntent;
    public PendingIntent deleteIntent;
    public Bundle extras = new Bundle();
    public int flags = 0;
    public PendingIntent fullScreenIntent;
    public int icon;
    public int iconLevel;
    public int number;
    public Notification publicVersion;
    public CharSequence tickerText;
    public int visibility;
    public long when;
    public int color;
    public int defaults;
    public int ledARGB;
    public int ledOnMS;
    public int ledOffMS;
    public long[] vibrate;
    public int priority;

    // ── Shim fields for bridge data extraction ───────────────────
    String shimTitle;
    String shimText;
    String shimChannelId;
    int shimPriority;
    private String shimGroup;
    private String shimSortKey;
    private int shimGroupAlertBehavior = GROUP_ALERT_ALL;
    private int shimBadgeIconType = BADGE_ICON_NONE;
    private long shimTimeoutAfter;
    private boolean shimAllowSystemGeneratedContextualActions = true;
    private String shimShortcutId;
    private CharSequence shimSettingsText;
    private boolean shimAutoCancel;
    private boolean shimOngoing;
    private boolean shimShowWhen;
    private boolean shimUsesChronometer;
    private boolean shimOnlyAlertOnce;
    private boolean shimGroupSummary;
    private boolean shimColorized;
    private CharSequence shimSubText;

    public Notification() {
        this.when = System.currentTimeMillis();
    }

    public Notification(Parcel p0) {
        // Parcel deserialization stub
    }

    public Notification clone() {
        Notification n = new Notification();
        n.when = this.when;
        n.icon = this.icon;
        n.iconLevel = this.iconLevel;
        n.number = this.number;
        n.contentIntent = this.contentIntent;
        n.deleteIntent = this.deleteIntent;
        n.fullScreenIntent = this.fullScreenIntent;
        n.tickerText = this.tickerText;
        n.flags = this.flags;
        n.visibility = this.visibility;
        n.category = this.category;
        n.color = this.color;
        n.defaults = this.defaults;
        n.priority = this.priority;
        n.vibrate = this.vibrate != null ? this.vibrate.clone() : null;
        n.ledARGB = this.ledARGB;
        n.ledOnMS = this.ledOnMS;
        n.ledOffMS = this.ledOffMS;
        n.extras = this.extras != null ? new Bundle(this.extras) : new Bundle();
        if (this.actions != null) {
            n.actions = new Action[this.actions.length];
            for (int i = 0; i < this.actions.length; i++) {
                n.actions[i] = this.actions[i] != null ? this.actions[i].clone() : null;
            }
        }
        if (this.publicVersion != null) {
            n.publicVersion = this.publicVersion.clone();
        }
        n.shimTitle = this.shimTitle;
        n.shimText = this.shimText;
        n.shimChannelId = this.shimChannelId;
        n.shimPriority = this.shimPriority;
        n.shimGroup = this.shimGroup;
        n.shimSortKey = this.shimSortKey;
        n.shimGroupAlertBehavior = this.shimGroupAlertBehavior;
        n.shimBadgeIconType = this.shimBadgeIconType;
        n.shimTimeoutAfter = this.shimTimeoutAfter;
        n.shimAllowSystemGeneratedContextualActions = this.shimAllowSystemGeneratedContextualActions;
        n.shimShortcutId = this.shimShortcutId;
        n.shimSettingsText = this.shimSettingsText;
        n.shimAutoCancel = this.shimAutoCancel;
        n.shimOngoing = this.shimOngoing;
        n.shimShowWhen = this.shimShowWhen;
        n.shimUsesChronometer = this.shimUsesChronometer;
        n.shimOnlyAlertOnce = this.shimOnlyAlertOnce;
        n.shimGroupSummary = this.shimGroupSummary;
        n.shimColorized = this.shimColorized;
        n.shimSubText = this.shimSubText;
        return n;
    }

    public int describeContents() { return 0; }

    public boolean getAllowSystemGeneratedContextualActions() {
        return shimAllowSystemGeneratedContextualActions;
    }

    public int getBadgeIconType() { return shimBadgeIconType; }

    public String getChannelId() { return shimChannelId; }

    public String getGroup() { return shimGroup; }

    public int getGroupAlertBehavior() { return shimGroupAlertBehavior; }

    public Icon getLargeIcon() { return null; }

    public CharSequence getSettingsText() { return shimSettingsText; }

    public String getShortcutId() { return shimShortcutId; }

    public Icon getSmallIcon() { return null; }

    public String getSortKey() { return shimSortKey; }

    public long getTimeoutAfter() { return shimTimeoutAfter; }

    public void writeToParcel(Parcel p0, int p1) {}

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Notification(");
        sb.append("channel=").append(shimChannelId);
        if (shimTitle != null) sb.append(" title=").append(shimTitle);
        if (shimText != null) sb.append(" text=").append(shimText);
        if (category != null) sb.append(" category=").append(category);
        sb.append(" pri=").append(shimPriority);
        sb.append(" flags=0x").append(Integer.toHexString(flags));
        sb.append(" vis=").append(visibility);
        if (shimGroup != null) sb.append(" group=").append(shimGroup);
        if (actions != null) sb.append(" actions=").append(actions.length);
        sb.append(")");
        return sb.toString();
    }

    // ── Action inner class ───────────────────────────────────────
    public static class Action implements Parcelable {
        public int icon;
        public CharSequence title;
        public PendingIntent actionIntent;

        public Action(int icon, CharSequence title, PendingIntent intent) {
            this.icon = icon;
            this.title = title;
            this.actionIntent = intent;
        }

        public Action clone() {
            return new Action(this.icon, this.title, this.actionIntent);
        }

        public PendingIntent getActionIntent() { return actionIntent; }
        public Icon getIcon() { return null; }
        public CharSequence getTitle() { return title; }

        public int describeContents() { return 0; }
        public void writeToParcel(Parcel p0, int p1) {}

        @Override
        public String toString() {
            return "Action(" + title + ")";
        }
    }

    // ── Builder (commonly used by Android apps) ──────────────────
    public static class Builder {
        private android.content.Context mContext;
        private String title;
        private String text;
        private String channelId;
        private int priority;
        private int smallIcon;
        private boolean autoCancel;
        private boolean ongoing;
        private PendingIntent contentIntent;
        private PendingIntent deleteIntent;
        private PendingIntent fullScreenIntent;
        private String category;
        private int visibility;
        private String group;
        private boolean groupSummary;
        private String sortKey;
        private int color;
        private int number;
        private CharSequence subText;
        private boolean showWhen = true;
        private long when;
        private boolean usesChronometer;
        private boolean onlyAlertOnce;
        private CharSequence tickerText;
        private int defaults;
        private long[] vibrate;
        private int ledArgb;
        private int ledOnMs;
        private int ledOffMs;
        private boolean lightsSet;
        private int badgeIconType = BADGE_ICON_NONE;
        private long timeoutAfter;
        private int groupAlertBehavior = GROUP_ALERT_ALL;
        private String shortcutId;
        private boolean colorized;
        private CharSequence settingsText;
        private Bundle extras = new Bundle();
        private ArrayList<Action> actions = new ArrayList<Action>();

        public Builder(android.content.Context context, String channelId) {
            this.mContext = context;
            this.channelId = channelId;
            this.when = System.currentTimeMillis();
        }

        /** @deprecated Use Builder(Context, String) instead. */
        public Builder(android.content.Context context) {
            this.mContext = context;
            this.channelId = "";
            this.when = System.currentTimeMillis();
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

        public Builder setAutoCancel(boolean autoCancel) {
            this.autoCancel = autoCancel;
            return this;
        }

        public Builder setOngoing(boolean ongoing) {
            this.ongoing = ongoing;
            return this;
        }

        public Builder setContentIntent(Object intent) {
            if (intent instanceof PendingIntent) {
                this.contentIntent = (PendingIntent) intent;
            }
            return this;
        }

        public Builder setDeleteIntent(Object intent) {
            if (intent instanceof PendingIntent) {
                this.deleteIntent = (PendingIntent) intent;
            }
            return this;
        }

        public Builder setFullScreenIntent(Object intent, boolean highPriority) {
            if (intent instanceof PendingIntent) {
                this.fullScreenIntent = (PendingIntent) intent;
            }
            return this;
        }

        public Builder setCategory(String category) {
            this.category = category;
            return this;
        }

        public Builder setVisibility(int visibility) {
            this.visibility = visibility;
            return this;
        }

        public Builder setGroup(String groupKey) {
            this.group = groupKey;
            return this;
        }

        public Builder setGroupSummary(boolean isGroupSummary) {
            this.groupSummary = isGroupSummary;
            return this;
        }

        public Builder setSortKey(String sortKey) {
            this.sortKey = sortKey;
            return this;
        }

        public Builder setStyle(Object style) { return this; }

        public Builder setColor(int argb) {
            this.color = argb;
            return this;
        }

        public Builder setNumber(int number) {
            this.number = number;
            return this;
        }

        public Builder setSubText(CharSequence text) {
            this.subText = text;
            return this;
        }

        public Builder setShowWhen(boolean show) {
            this.showWhen = show;
            return this;
        }

        public Builder setWhen(long when) {
            this.when = when;
            return this;
        }

        public Builder setUsesChronometer(boolean b) {
            this.usesChronometer = b;
            return this;
        }

        public Builder setOnlyAlertOnce(boolean onlyAlertOnce) {
            this.onlyAlertOnce = onlyAlertOnce;
            return this;
        }

        public Builder setTicker(CharSequence tickerText) {
            this.tickerText = tickerText;
            return this;
        }

        public Builder setLargeIcon(Object icon) { return this; }

        public Builder setDefaults(int defaults) {
            this.defaults = defaults;
            return this;
        }

        public Builder setVibrate(long[] pattern) {
            this.vibrate = pattern;
            return this;
        }

        public Builder setSound(Object sound) { return this; }

        public Builder setLights(int argb, int onMs, int offMs) {
            this.ledArgb = argb;
            this.ledOnMs = onMs;
            this.ledOffMs = offMs;
            this.lightsSet = true;
            return this;
        }

        public Builder addAction(int icon, CharSequence title, Object intent) {
            PendingIntent pi = (intent instanceof PendingIntent) ? (PendingIntent) intent : null;
            this.actions.add(new Action(icon, title, pi));
            return this;
        }

        public Builder addAction(Action action) {
            if (action != null) {
                this.actions.add(action);
            }
            return this;
        }

        public Builder setBadgeIconType(int type) {
            this.badgeIconType = type;
            return this;
        }

        public Builder setTimeoutAfter(long millis) {
            this.timeoutAfter = millis;
            return this;
        }

        public Builder setGroupAlertBehavior(int behavior) {
            this.groupAlertBehavior = behavior;
            return this;
        }

        public Builder setShortcutId(String shortcutId) {
            this.shortcutId = shortcutId;
            return this;
        }

        public Builder setColorized(boolean colorized) {
            this.colorized = colorized;
            return this;
        }

        public Builder setSettingsText(CharSequence text) {
            this.settingsText = text;
            return this;
        }

        public Builder setExtras(Bundle extras) {
            this.extras = extras != null ? extras : new Bundle();
            return this;
        }

        public Builder addExtras(Bundle extras) {
            if (extras != null) {
                this.extras.putAll(extras);
            }
            return this;
        }

        public Bundle getExtras() {
            return this.extras;
        }

        public Notification build() {
            Notification n = new Notification();
            n.shimTitle = this.title;
            n.shimText = this.text;
            n.shimChannelId = this.channelId;
            n.shimPriority = this.priority;
            n.when = this.when;
            n.icon = this.smallIcon;
            n.number = this.number;
            n.tickerText = this.tickerText;
            n.visibility = this.visibility;
            n.category = this.category;
            n.color = this.color;
            n.defaults = this.defaults;
            n.priority = this.priority;
            n.contentIntent = this.contentIntent;
            n.deleteIntent = this.deleteIntent;
            n.fullScreenIntent = this.fullScreenIntent;
            n.vibrate = this.vibrate;
            n.ledARGB = this.ledArgb;
            n.ledOnMS = this.ledOnMs;
            n.ledOffMS = this.ledOffMs;
            n.shimGroup = this.group;
            n.shimSortKey = this.sortKey;
            n.shimGroupAlertBehavior = this.groupAlertBehavior;
            n.shimBadgeIconType = this.badgeIconType;
            n.shimTimeoutAfter = this.timeoutAfter;
            n.shimShortcutId = this.shortcutId;
            n.shimSettingsText = this.settingsText;
            n.shimAutoCancel = this.autoCancel;
            n.shimOngoing = this.ongoing;
            n.shimShowWhen = this.showWhen;
            n.shimUsesChronometer = this.usesChronometer;
            n.shimOnlyAlertOnce = this.onlyAlertOnce;
            n.shimGroupSummary = this.groupSummary;
            n.shimColorized = this.colorized;
            n.shimSubText = this.subText;

            // Set extras with title/text
            n.extras = this.extras != null ? new Bundle(this.extras) : new Bundle();
            if (this.title != null) n.extras.putCharSequence(EXTRA_TITLE, this.title);
            if (this.text != null) n.extras.putCharSequence(EXTRA_TEXT, this.text);
            if (this.subText != null) n.extras.putCharSequence(EXTRA_SUB_TEXT, this.subText);

            // Build actions array
            if (!this.actions.isEmpty()) {
                n.actions = this.actions.toArray(new Action[this.actions.size()]);
            }

            // Set flags from builder booleans
            if (this.autoCancel) n.flags |= FLAG_AUTO_CANCEL;
            if (this.ongoing) n.flags |= FLAG_ONGOING_EVENT;
            if (this.onlyAlertOnce) n.flags |= FLAG_ONLY_ALERT_ONCE;
            if (this.groupSummary) n.flags |= FLAG_GROUP_SUMMARY;

            return n;
        }
    }
}
