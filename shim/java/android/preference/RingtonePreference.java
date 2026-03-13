package android.preference;
import android.media.Ringtone;
import android.media.Ringtone;
import java.net.URI;

/**
 * Android-compatible RingtonePreference shim.
 * A Preference that allows the user to choose a ringtone from those on the device.
 * The chosen ringtone URI is persisted as a string.
 */
public class RingtonePreference extends Preference {

    /** Ringtone type: ringtones. */
    public static final int TYPE_RINGTONE = 1;

    /** Ringtone type: notification sounds. */
    public static final int TYPE_NOTIFICATION = 2;

    /** Ringtone type: alarm sounds. */
    public static final int TYPE_ALARM = 4;

    /** Ringtone type: all available sound types. */
    public static final int TYPE_ALL = 7;

    private int mRingtoneType = TYPE_RINGTONE;
    private boolean mShowDefault = true;
    private boolean mShowSilent = true;

    public RingtonePreference() {
        super();
    }

    /**
     * Returns the sound type(s) that are shown in the picker.
     * @return the ringtone type flags
     */
    public int getRingtoneType() {
        return mRingtoneType;
    }

    /**
     * Sets the sound type(s) that are shown in the picker.
     * @param type one or a combination of TYPE_RINGTONE, TYPE_NOTIFICATION,
     *             TYPE_ALARM, or TYPE_ALL
     */
    public void setRingtoneType(int type) {
        mRingtoneType = type;
    }

    /**
     * Returns whether to show an item for a default sound.
     * @return true if the default option is shown
     */
    public boolean getShowDefault() {
        return mShowDefault;
    }

    /**
     * Sets whether to show an item for a default sound.
     * @param showDefault true to show the default option
     */
    public void setShowDefault(boolean showDefault) {
        mShowDefault = showDefault;
    }

    /**
     * Returns whether to show an item for 'Silent'.
     * @return true if the silent option is shown
     */
    public boolean getShowSilent() {
        return mShowSilent;
    }

    /**
     * Sets whether to show an item for 'Silent'.
     * @param showSilent true to show the silent option
     */
    public void setShowSilent(boolean showSilent) {
        mShowSilent = showSilent;
    }

    /**
     * Prepares the intent to launch the ringtone picker.
     * Subclasses can override to add extra data to the intent.
     * @param ringtonePickerIntent the intent being prepared
     */
    public void onPrepareRingtonePickerIntent(Object ringtonePickerIntent) {
        // Stub — subclasses may override to customize the picker intent
    }

    /**
     * Called when a ringtone is chosen.
     * @param ringtoneUri the URI of the chosen ringtone, or null for silence
     */
    public void onSaveRingtone(Object ringtoneUri) {
        // Stub — subclasses may override to persist the chosen ringtone
    }

    /**
     * Called to restore a previously saved ringtone.
     * @return the previously saved ringtone URI, or null if none
     */
    public Object onRestoreRingtone() {
        return null;
    }
}
