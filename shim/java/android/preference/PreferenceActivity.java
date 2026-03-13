package android.preference;

import android.content.SharedPreferences;

/**
 * Android-compatible PreferenceActivity shim.
 *
 * <p>On real Android this extends {@code android.app.Activity}. Because
 * {@code android.app.Activity} is not yet available in this shim tree the
 * class extends Object and every Activity-derived method is stubbed out here.
 *
 * @deprecated Use {@code androidx.preference.PreferenceFragmentCompat} instead.
 */
@Deprecated
public class PreferenceActivity {

    // -------------------------------------------------------------------------
    // Inner class: Header
    // -------------------------------------------------------------------------

    /**
     * Describes a single section (header) in a multi-pane preference UI.
     */
    public static class Header {
        /** Unique identifier for this header, or -1 if not set. */
        public long id = -1;

        /** Title of this header. */
        public CharSequence title;

        /** Summary of this header. */
        public CharSequence summary;

        /** Breadcrumb title shown when the header is selected. */
        public CharSequence breadCrumbTitle;

        /** Icon resource id for this header, or 0 if not set. */
        public int iconRes;

        /** Fragment class name to instantiate when this header is selected. */
        public String fragment;

        /** Extra arguments to supply to the associated Fragment. */
        public Object fragmentArguments; // android.os.Bundle — use Object
    }

    // -------------------------------------------------------------------------
    // Preference screen management
    // -------------------------------------------------------------------------

    /** The root preference screen (stub). */
    private Object mPreferenceScreen; // android.preference.PreferenceScreen

    /**
     * Inflates the given XML resource and adds the preference hierarchy to the
     * current preference hierarchy.  Stub — no-op on OpenHarmony.
     *
     * @param preferencesResId the XML resource id of the preferences
     */
    public void addPreferencesFromResource(int preferencesResId) {
        // stub — no-op on OpenHarmony
    }

    /**
     * Finds a {@link Preference} based on its key.
     *
     * @param key the key of the preference
     * @return the found preference, or {@code null} (stub always returns null)
     */
    public Object findPreference(CharSequence key) {
        return null; // stub
    }

    /**
     * Returns the root {@link PreferenceScreen} object.
     *
     * @return the root preference screen (always null in this stub)
     */
    public Object getPreferenceScreen() {
        return mPreferenceScreen;
    }

    /**
     * Called when a preference in the tree was clicked.
     * Override to handle clicks.
     *
     * @param preferenceScreen the screen that contains the clicked preference
     * @param preference       the clicked preference
     * @return {@code true} if the click was handled
     */
    public boolean onPreferenceTreeClick(Object preferenceScreen, Object preference) {
        return false; // stub
    }

    // -------------------------------------------------------------------------
    // Stub Activity lifecycle methods (Activity is not shimmed yet)
    // -------------------------------------------------------------------------

    /** Called when the activity is starting. */
    protected void onCreate(Object savedInstanceState) {}

    /** Called after onCreate has finished. */
    protected void onStart() {}

    /** Called when the activity will start interacting with the user. */
    protected void onResume() {}

    /** Called when the system is about to start resuming a previous activity. */
    protected void onPause() {}

    /** Called when the activity is no longer visible to the user. */
    protected void onStop() {}

    /** Called before the activity is destroyed. */
    protected void onDestroy() {}

    /**
     * Returns the SystemService identified by the given name.
     * Stub always returns null.
     *
     * @param name the service name (e.g., {@code Context.SENSOR_SERVICE})
     * @return null (stub)
     */
    public Object getSystemService(String name) {
        return null; // stub
    }

    /**
     * Returns a SharedPreferences instance for the given name.
     *
     * @param name the name of the preferences file
     * @param mode operating mode
     * @return a stub SharedPreferences
     */
    public SharedPreferences getSharedPreferences(String name, int mode) {
        return new SharedPreferences.StubSharedPreferences();
    }
}
