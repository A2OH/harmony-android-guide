package android.preference;

/**
 * Android-compatible Preference shim.
 * Base class for all preference types in a PreferenceScreen hierarchy.
 */
public class Preference {
    private String mKey;
    private String mTitle;
    private String mSummary;
    private boolean mEnabled = true;
    private OnPreferenceChangeListener mChangeListener;
    private OnPreferenceClickListener mClickListener;

    public Preference() {}

    public String getKey() { return mKey; }
    public void setKey(String key) { mKey = key; }

    public String getTitle() { return mTitle; }
    public void setTitle(String title) { mTitle = title; }

    public String getSummary() { return mSummary; }
    public void setSummary(String summary) { mSummary = summary; }

    public boolean isEnabled() { return mEnabled; }
    public void setEnabled(boolean enabled) { mEnabled = enabled; }

    public void setOnPreferenceChangeListener(OnPreferenceChangeListener listener) {
        mChangeListener = listener;
    }

    public OnPreferenceChangeListener getOnPreferenceChangeListener() {
        return mChangeListener;
    }

    public void setOnPreferenceClickListener(OnPreferenceClickListener listener) {
        mClickListener = listener;
    }

    public OnPreferenceClickListener getOnPreferenceClickListener() {
        return mClickListener;
    }

    /**
     * Called when the preference value changes. Notifies the listener if set.
     * @return true if the change should be persisted.
     */
    protected boolean notifyChanged(Object newValue) {
        if (mChangeListener != null) {
            return mChangeListener.onPreferenceChange(this, newValue);
        }
        return true;
    }

    // ── Listener interfaces ──

    public interface OnPreferenceChangeListener {
        boolean onPreferenceChange(Preference preference, Object newValue);
    }

    public interface OnPreferenceClickListener {
        boolean onPreferenceClick(Preference preference);
    }
}
