package android.media;

import com.ohos.shim.bridge.OHBridge;

/**
 * Shim for android.media.AudioManager → @ohos.multimedia.audio.AudioManager
 *
 * Maps Android audio management APIs onto OpenHarmony audio APIs via
 * the OHBridge JNI layer.
 */
public class AudioManager {

    // ── Stream types ───────────────────────────────────────────────

    public static final int STREAM_VOICE_CALL    = 0;
    public static final int STREAM_SYSTEM        = 1;
    public static final int STREAM_RING          = 2;
    public static final int STREAM_MUSIC         = 3;
    public static final int STREAM_ALARM         = 4;
    public static final int STREAM_NOTIFICATION  = 5;

    // ── Ringer modes ───────────────────────────────────────────────

    public static final int RINGER_MODE_SILENT   = 0;
    public static final int RINGER_MODE_VIBRATE  = 1;
    public static final int RINGER_MODE_NORMAL   = 2;

    // ── Audio modes ────────────────────────────────────────────────

    public static final int MODE_NORMAL          = 0;
    public static final int MODE_RINGTONE        = 1;
    public static final int MODE_IN_CALL         = 2;
    public static final int MODE_IN_COMMUNICATION = 3;

    // ── AudioFocus change hints ────────────────────────────────────

    public static final int AUDIOFOCUS_GAIN                    =  1;
    public static final int AUDIOFOCUS_LOSS                    = -1;
    public static final int AUDIOFOCUS_LOSS_TRANSIENT          = -2;
    public static final int AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK = -3;

    // ── AudioFocus request results ─────────────────────────────────

    public static final int AUDIOFOCUS_REQUEST_GRANTED = 1;
    public static final int AUDIOFOCUS_REQUEST_FAILED  = 0;

    // ── Listener interface ─────────────────────────────────────────

    public interface OnAudioFocusChangeListener {
        void onAudioFocusChange(int focusChange);
    }

    // ── Audio mode (in-process state) ─────────────────────────────

    private int mMode = MODE_NORMAL;

    // ── Speakerphone state (in-process state) ──────────────────────

    private boolean mSpeakerphoneOn = false;

    // ── Volume ─────────────────────────────────────────────────────

    public int getStreamVolume(int streamType) {
        return OHBridge.audioGetStreamVolume(streamType);
    }

    public int getStreamMaxVolume(int streamType) {
        return OHBridge.audioGetStreamMaxVolume(streamType);
    }

    public void setStreamVolume(int streamType, int index, int flags) {
        OHBridge.audioSetStreamVolume(streamType, index, flags);
    }

    public void adjustStreamVolume(int streamType, int direction, int flags) {
        int current = OHBridge.audioGetStreamVolume(streamType);
        int max     = OHBridge.audioGetStreamMaxVolume(streamType);
        int next    = Math.max(0, Math.min(max, current + direction));
        OHBridge.audioSetStreamVolume(streamType, next, flags);
    }

    // ── Ringer mode ────────────────────────────────────────────────

    public int getRingerMode() {
        return OHBridge.audioGetRingerMode();
    }

    public void setRingerMode(int mode) {
        OHBridge.audioSetRingerMode(mode);
    }

    // ── Audio mode ─────────────────────────────────────────────────

    public int getMode() {
        return mMode;
    }

    public void setMode(int mode) {
        mMode = mode;
    }

    // ── Music active ───────────────────────────────────────────────

    public boolean isMusicActive() {
        return OHBridge.audioIsMusicActive();
    }

    // ── Speakerphone ───────────────────────────────────────────────

    public void setSpeakerphoneOn(boolean on) {
        mSpeakerphoneOn = on;
    }

    public boolean isSpeakerphoneOn() {
        return mSpeakerphoneOn;
    }

    // ── AudioFocus ─────────────────────────────────────────────────

    public int requestAudioFocus(OnAudioFocusChangeListener listener,
                                 int streamType,
                                 int durationHint) {
        // OpenHarmony audio focus is managed natively; shim always grants.
        return AUDIOFOCUS_REQUEST_GRANTED;
    }

    public int abandonAudioFocus(OnAudioFocusChangeListener listener) {
        return AUDIOFOCUS_REQUEST_GRANTED;
    }
}
