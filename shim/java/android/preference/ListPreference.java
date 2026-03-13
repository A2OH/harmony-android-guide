package android.preference;

/**
 * Android-compatible ListPreference shim.
 * Presents a list of entries in a dialog, where each entry has a corresponding
 * value. The selected value is persisted as a String.
 */
public class ListPreference extends Preference {
    private CharSequence[] mEntries;
    private CharSequence[] mEntryValues;
    private String mValue;

    public ListPreference() {
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
     * Sets the currently selected value. If the value changed and should be
     * persisted (as indicated by the change listener), the value is stored.
     */
    public void setValue(String value) {
        if (notifyChanged(value)) {
            mValue = value;
        }
    }

    /**
     * Returns the currently selected value.
     */
    public String getValue() {
        return mValue;
    }

    /**
     * Returns the entry (human-readable label) corresponding to the current value.
     * Returns null if no matching entry is found or if entries/values are not set.
     */
    public CharSequence getEntry() {
        int index = findIndexOfValue(mValue);
        if (index >= 0 && mEntries != null) {
            return mEntries[index];
        }
        return null;
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

    /**
     * Sets the value to the entry-value at the given index.
     */
    public void setValueIndex(int index) {
        if (mEntryValues != null) {
            setValue(mEntryValues[index].toString());
        }
    }
}
