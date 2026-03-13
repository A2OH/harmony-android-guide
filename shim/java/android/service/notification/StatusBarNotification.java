package android.service.notification;

/**
 * Android-compatible StatusBarNotification shim. Stub — no-op implementation.
 *
 * OH mapping: No direct equivalent; notification metadata is surfaced via
 * @ohos.notificationManager event callbacks on the OpenHarmony target.
 */
public class StatusBarNotification {

    private final String packageName;
    private final String opPackageName;
    private final int id;
    private final String tag;
    private final int uid;
    private final int initialPid;
    private final Object notification;
    private final Object user;
    private final String overrideGroupKey;
    private final long postTime;

    public StatusBarNotification(
            String pkg,
            String opPkg,
            int id,
            String tag,
            int uid,
            int initialPid,
            Object notification,
            Object user,
            String overrideGroupKey,
            long postTime) {
        this.packageName     = pkg;
        this.opPackageName   = opPkg;
        this.id              = id;
        this.tag             = tag;
        this.uid             = uid;
        this.initialPid      = initialPid;
        this.notification    = notification;
        this.user            = user;
        this.overrideGroupKey = overrideGroupKey;
        this.postTime        = postTime;
    }

    /** Returns the package name of the app that posted this notification. */
    public String getPackageName() { return packageName; }

    /** Returns the notification ID set by the posting app. */
    public int getId() { return id; }

    /** Returns the notification tag, or {@code null} if none was set. */
    public String getTag() { return tag; }

    /**
     * Returns the {@link android.app.Notification} object.
     * Typed as {@code Object} in this shim to avoid a hard dependency on
     * the Notification shim class.
     */
    public Object getNotification() { return notification; }

    /** Returns the time at which this notification was posted (ms since epoch). */
    public long getPostTime() { return postTime; }

    /**
     * Returns the {@link android.os.UserHandle} associated with this notification.
     * Typed as {@code Object} in this shim.
     */
    public Object getUser() { return user; }

    /**
     * Returns the unique key that identifies this notification across packages and users.
     * Format: {@code "<uid>|<packageName>|<id>|<tag>|<userId>"}.
     */
    public String getKey() {
        return uid + "|" + packageName + "|" + id + "|" + tag + "|0";
    }

    /**
     * Returns {@code true} if this notification belongs to a notification group.
     * A notification is considered part of a group when a non-null group key is set.
     */
    public boolean isGroup() { return getGroupKey() != null; }

    /**
     * Returns the group key for this notification, or {@code null} if the notification
     * does not belong to a group.  The override group key (if set) takes precedence over
     * the group key embedded in the Notification object.
     */
    public String getGroupKey() { return overrideGroupKey; }

    /**
     * Returns {@code true} if this notification is ongoing (i.e. not clearable by the user).
     */
    public boolean isOngoing() { return false; }

    /**
     * Returns {@code true} if the user can dismiss this notification.
     */
    public boolean isClearable() { return !isOngoing(); }

    @Override
    public String toString() {
        return "StatusBarNotification(pkg=" + packageName
                + " id=" + id
                + " tag=" + tag
                + " key=" + getKey() + ")";
    }
}
