package android.preference;
import android.widget.EditText;
import android.widget.EditText;

import android.widget.EditText;

/**
 * Android-compatible EditTextPreference shim.
 * A Preference that holds a free-form String value, normally edited via
 * a dialog containing an EditText widget.
 *
 * In this A2OH shim the dialog/EditText UI is not available; getText()
 * and setText() work with the stored string and getEditText() returns null.
 */
public class EditTextPreference extends Preference {
    private String mText;

    public EditTextPreference() {}

    /**
     * Saves the text to the current data storage.
     * @param text The text to save.
     */
    public void setText(String text) {
        String oldText = mText;
        mText = text;
        if ((oldText == null && text != null) || (oldText != null && !oldText.equals(text))) {
            notifyChanged(text);
        }
    }

    /**
     * Gets the text from the current data storage.
     * @return The current preference value.
     */
    public String getText() {
        return mText;
    }

    /**
     * Returns the EditText widget that is shown in the dialog.
     * In this shim the dialog UI is not available, so this always returns null.
     * @return null (EditText not available in shim layer).
     */
    public EditText getEditText() {
        return null;
    }
}
