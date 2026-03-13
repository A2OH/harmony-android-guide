package android.content;

/**
 * Android-compatible RestrictionEntry shim. Stub — no-op implementation for A2OH migration.
 */
public class RestrictionEntry {

    // Type constants
    public static final int TYPE_NULL         = 0;
    public static final int TYPE_BOOLEAN      = 1;
    public static final int TYPE_CHOICE       = 2;
    public static final int TYPE_MULTI_SELECT = 4;
    public static final int TYPE_INTEGER      = 5;
    public static final int TYPE_STRING       = 6;
    public static final int TYPE_BUNDLE       = 7;
    public static final int TYPE_BUNDLE_ARRAY = 8;

    private final String mKey;
    private String       mTitle;
    private String       mDescription;
    private int          mType;
    private String       mCurrentValue;
    private String[]     mAllSelectedStrings;
    private int          mIntValue;

    /**
     * Primary constructor used by restriction providers.
     *
     * @param key   Unique key identifying this restriction entry.
     * @param value The initial string value for this entry.
     */
    public RestrictionEntry(String key, String value) {
        mKey          = key;
        mCurrentValue = value;
        mType         = TYPE_STRING;
    }

    /** Returns the key for this restriction. */
    public String getKey() {
        return mKey;
    }

    /** Returns the user-visible title for this restriction. */
    public String getTitle() {
        return mTitle;
    }

    /** Sets the user-visible title. */
    public void setTitle(String title) {
        mTitle = title;
    }

    /** Returns the user-visible description for this restriction. */
    public String getDescription() {
        return mDescription;
    }

    /** Sets the user-visible description. */
    public void setDescription(String description) {
        mDescription = description;
    }

    /** Returns the type of this restriction entry. */
    public int getType() {
        return mType;
    }

    /** Sets the type of this restriction entry. */
    public void setType(int type) {
        mType = type;
    }

    /** Returns the currently selected string value. */
    public String getSelectedString() {
        return mCurrentValue;
    }

    /** Sets the currently selected string value. */
    public void setSelectedString(String selectedString) {
        mCurrentValue = selectedString;
    }

    /** Returns the list of all selected strings (for multi-select types). */
    public String[] getAllSelectedStrings() {
        return mAllSelectedStrings;
    }

    /** Sets the list of all selected strings. */
    public void setAllSelectedStrings(String[] allSelectedStrings) {
        mAllSelectedStrings = allSelectedStrings;
    }

    /** Returns the integer value for integer-type restrictions. */
    public int getIntValue() {
        return mIntValue;
    }

    /** Sets the integer value for integer-type restrictions. */
    public void setIntValue(int intValue) {
        mIntValue = intValue;
    }

    @Override
    public String toString() {
        return "RestrictionEntry{key=" + mKey + ", type=" + mType + ", value=" + mCurrentValue + "}";
    }
}
