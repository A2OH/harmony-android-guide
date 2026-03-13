package android.preference;

/**
 * Android-compatible TwoStatePreference shim.
 * Abstract base class for preferences that have two selectable states:
 * checked and unchecked. Provides common functionality for CheckBoxPreference
 * and SwitchPreference.
 */
public class TwoStatePreference extends Preference {
    private boolean mChecked = false;
    private CharSequence mSummaryOn;
    private CharSequence mSummaryOff;
    private boolean mDisableDependentsState = false;

    public TwoStatePreference() {}

    public boolean isChecked() { return mChecked; }

    public void setChecked(boolean checked) {
        if (mChecked != checked) {
            mChecked = checked;
            notifyChanged(checked);
        }
    }

    public CharSequence getSummaryOn() { return mSummaryOn; }

    public void setSummaryOn(CharSequence summaryOn) {
        mSummaryOn = summaryOn;
    }

    public CharSequence getSummaryOff() { return mSummaryOff; }

    public void setSummaryOff(CharSequence summaryOff) {
        mSummaryOff = summaryOff;
    }

    public boolean getDisableDependentsState() { return mDisableDependentsState; }

    public void setDisableDependentsState(boolean disableDependentsState) {
        mDisableDependentsState = disableDependentsState;
    }
}
