package android.app.people;

/**
 * Shim: android.app.people.PeopleManager (API 31+)
 * Tier 3 — stub / no-op (no direct OpenHarmony equivalent).
 *
 * Provides conversation and status APIs used by apps that interact
 * with the Android People system (notification conversations, statuses).
 * On OpenHarmony these concepts do not exist, so all methods are no-ops
 * or return safe defaults.
 */
public class PeopleManager {

    /**
     * Returns whether the given shortcut is a conversation.
     * Stub always returns false — OpenHarmony has no conversation concept.
     */
    public boolean isConversation(String packageName, String shortcutId) {
        return false;
    }

    /**
     * Adds or updates a status associated with a conversation.
     * No-op on OpenHarmony.
     *
     * @param packageName  the package that owns the conversation
     * @param conversationId  the conversation / shortcut id
     * @param status  the status object (ConversationStatus on real Android)
     */
    public void addOrUpdateStatus(String packageName, String conversationId, Object status) {
        // no-op
    }

    /**
     * Clears any previously-published status for the given conversation.
     * No-op on OpenHarmony.
     */
    public void clearStatus(String packageName, String conversationId) {
        // no-op
    }
}
