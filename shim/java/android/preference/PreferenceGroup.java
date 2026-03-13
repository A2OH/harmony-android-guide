package android.preference;

/**
 * Android-compatible PreferenceGroup shim.
 * Abstract container for multiple {@link Preference} objects.
 * Stub implementation for the A2OH shim layer.
 */
public class PreferenceGroup extends Preference {

    public PreferenceGroup() {
        super();
    }

    /**
     * Adds a Preference to this group.
     * @param preference the Preference to add
     * @return true (stub always succeeds)
     */
    public boolean addPreference(Object preference) {
        return true;
    }

    /**
     * Removes a Preference from this group.
     * @param preference the Preference to remove
     * @return true (stub always succeeds)
     */
    public boolean removePreference(Object preference) {
        return true;
    }

    /** Removes all Preferences from this group. */
    public void removeAll() {
        // stub
    }

    /**
     * Returns the number of Preferences in this group.
     * @return 0 (stub has no children)
     */
    public int getPreferenceCount() {
        return 0;
    }

    /**
     * Returns the Preference at the given index.
     * @param index the index of the Preference to retrieve
     * @return null (stub has no children)
     */
    public Preference getPreference(int index) {
        return null;
    }

    /**
     * Finds a Preference by its key.
     * @param key the key of the Preference to find
     * @return null (stub has no children)
     */
    public Preference findPreference(CharSequence key) {
        return null;
    }
}
