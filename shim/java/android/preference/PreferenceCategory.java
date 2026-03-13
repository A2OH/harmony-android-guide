package android.preference;

import java.util.ArrayList;
import java.util.List;

/**
 * Android-compatible PreferenceCategory shim.
 * Groups related Preference objects within a PreferenceScreen.
 * A category is always disabled (not directly clickable).
 */
public class PreferenceCategory extends Preference {
    private final List<Preference> mPreferences = new ArrayList<>();

    public PreferenceCategory(Object context) {}

    public PreferenceCategory(Object context, Object attrs) {}

    /**
     * Called before a Preference is added to this category.
     * @return true to allow adding the preference.
     */
    public boolean onPrepareAddPreference(Object preference) {
        return true;
    }

    /** A category itself is never enabled. */
    @Override
    public boolean isEnabled() {
        return false;
    }

    public void addPreference(Preference preference) {
        if (preference != null) {
            mPreferences.add(preference);
        }
    }

    public boolean removePreference(Preference preference) {
        return mPreferences.remove(preference);
    }

    public int getPreferenceCount() {
        return mPreferences.size();
    }

    public Preference getPreference(int index) {
        return mPreferences.get(index);
    }
}
