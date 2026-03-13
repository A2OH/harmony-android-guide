package android.app;

import java.util.ArrayList;
import java.util.List;

/**
 * Shim: android.app.NotificationChannelGroup
 * Tier 1 — data holder for a group of notification channels.
 *
 * OH mapping: OH notification slots have a "slotGroup" field but no first-class
 * channel-group concept; groups are represented by a groupId string on each slot.
 * This shim stores the group metadata so that NotificationManager.createNotificationChannelGroup()
 * can propagate the groupId when creating slots.
 */
public class NotificationChannelGroup {

    private final String mId;
    private final CharSequence mName;
    private String mDescription;
    private final List<NotificationChannel> mChannels = new ArrayList<>();

    /**
     * Creates a notification channel group.
     *
     * @param id   The id of the group; must be unique per package.
     *             The value may be truncated if it is too long.
     * @param name The user visible name of the group.
     */
    public NotificationChannelGroup(String id, CharSequence name) {
        mId = id;
        mName = name;
    }

    // ── Getters ──

    /**
     * Returns the id of this group.
     */
    public String getId() {
        return mId;
    }

    /**
     * Returns the user-visible name of this group.
     */
    public CharSequence getName() {
        return mName;
    }

    /**
     * Returns the user-visible description of this group, or null if none was set.
     */
    public String getDescription() {
        return mDescription;
    }

    /**
     * Returns the list of channels that be(long to this group, in the order they were added.
     * This list is mutable — channels can be added directly to support the shim's
     * NotificationManager.createNotificationChannel() registration flow.
     */
    public List<NotificationChannel> getChannels() {
        return mChannels;
    }

    // ── Setters ──

    /**
     * Sets the user-visible description of this group.
     *
     * @param description The description, or null to clear it.
     */
    public void setDescription(String description) {
        mDescription = description;
    }

    // ── Internal helper (used by NotificationManager shim) ──

    /**
     * Adds a channel to this group.  Called by NotificationManager when a channel
     * whose groupId matches this group's id is registered.
     */
    public void addChannel(NotificationChannel channel) {
        if (channel != null) {
            mChannels.add(channel);
        }
    }
}
