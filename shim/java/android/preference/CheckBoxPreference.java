package android.preference;
import android.content.SharedPreferences;
import android.content.SharedPreferences;

/**
 * Android-compatible CheckBoxPreference shim.
 * A Preference that holds a boolean (checked/unchecked) value.
 * Maps to a toggle preference backed by SharedPreferences.
 */
public class CheckBoxPreference extends Preference {
    private boolean mChecked = false;

    public CheckBoxPreference() {}

    public boolean isChecked() { return mChecked; }

    public void setChecked(boolean checked) {
        if (mChecked != checked) {
            mChecked = checked;
            notifyChanged(checked);
        }
    }
}
