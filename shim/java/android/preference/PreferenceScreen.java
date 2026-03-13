package android.preference;

import java.util.ArrayList;
import java.util.List;

/**
 * Android-compatible PreferenceScreen shim.
 * Container for a hierarchy of Preference objects.
 */
public class PreferenceScreen extends Preference {
    private final List<Preference> mPreferences = new ArrayList<>();

    public PreferenceScreen() {}

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
