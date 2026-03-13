package android.preference;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Android-compatible MultiSelectListPreference shim.
 * Presents a list of entries in a dialog, where multiple entries can be
 * selected simultaneously. The selected values are persisted as a Set of
 * Strings.
 */
public class MultiSelectListPreference extends Preference {
    private CharSequence[] mEntries;
    private CharSequence[] mEntryValues;
    private Set<String> mValues = new HashSet<String>();

    public MultiSelectListPreference() {
        super();
    }

    /**
     * Sets the human-readable entries to be shown in the list.
     */
    public void setEntries(CharSequence[] entries) {
        mEntries = entries;
    }

    /**
     * Returns the list of entries to be shown in the list dialog.
     */
    public CharSequence[] getEntries() {
        return mEntries;
    }

    /**
     * Sets the array of values corresponding to each entry.
     */
    public void setEntryValues(CharSequence[] entryValues) {
        mEntryValues = entryValues;
    }

    /**
     * Returns the array of values corresponding to each entry.
     */
    public CharSequence[] getEntryValues() {
        return mEntryValues;
    }

    /**
     * Sets the currently selected values. If the change is accepted by the
     * change listener, the values are stored.
     */
    public void setValues(Set<String> values) {
        if (notifyChanged(values)) {
            mValues = new HashSet<String>(values);
        }
    }

    /**
     * Returns the currently selected values as an unmodifiable Set.
     */
    public Set<String> getValues() {
        return Collections.unmodifiableSet(mValues);
    }

    /**
     * Returns the index of the given value in the entry-values array.
     * Returns -1 if the value is not found or if entry values are not set.
     */
    public int findIndexOfValue(String value) {
        if (value != null && mEntryValues != null) {
            for (int i = mEntryValues.length - 1; i >= 0; i--) {
                if (value.equals(mEntryValues[i].toString())) {
                    return i;
                }
            }
        }
        return -1;
    }
}
