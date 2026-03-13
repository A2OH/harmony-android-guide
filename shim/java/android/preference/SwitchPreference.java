package android.preference;

/**
 * Android-compatible SwitchPreference shim.
 * A Preference that provides a two-state toggleable switch widget,
 * with configurable on/off text labels.
 */
public class SwitchPreference extends Preference {
    private boolean mChecked = false;
    private CharSequence mSwitchTextOn;
    private CharSequence mSwitchTextOff;

    public SwitchPreference() {}

    public boolean isChecked() { return mChecked; }

    public void setChecked(boolean checked) {
        if (mChecked != checked) {
            mChecked = checked;
            notifyChanged(checked);
        }
    }

    public CharSequence getSwitchTextOn() { return mSwitchTextOn; }

    public void setSwitchTextOn(CharSequence textOn) {
        mSwitchTextOn = textOn;
    }

    public CharSequence getSwitchTextOff() { return mSwitchTextOff; }

    public void setSwitchTextOff(CharSequence textOff) {
        mSwitchTextOff = textOff;
    }
}
