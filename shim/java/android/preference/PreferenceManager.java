package android.preference;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Android-compatible PreferenceManager shim.
 * Provides access to the default SharedPreferences for an application.
 *
 * Maps to @ohos.data.preferences with the package name as the store name.
 */
public class PreferenceManager {

    private static final String DEFAULT_PREFS_NAME = "default_preferences";

    private PreferenceManager() {}

    /**
     * Returns the default SharedPreferences for the given context.
     * On Android this uses the package name as the preferences file name.
     */
    public static SharedPreferences getDefaultSharedPreferences(Context context) {
        String name = context.getPackageName() + "_preferences";
        return context.getSharedPreferences(name, Context.MODE_PRIVATE);
    }

    /**
     * Returns a named SharedPreferences instance for the given context.
     */
    public static SharedPreferences getSharedPreferences(Context context, String name) {
        return context.getSharedPreferences(name, Context.MODE_PRIVATE);
    }
}
