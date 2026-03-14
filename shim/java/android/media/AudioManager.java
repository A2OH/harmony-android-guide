package android.media;

/**
 * Shim for android.media.AudioManager → @ohos.multimedia.audio.AudioManager
 *
 * Maps Android audio management APIs onto OpenHarmony audio APIs via
 * the OHBridge JNI layer (accessed by reflection to avoid static init crash).
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

    // ── Bridge helper (reflection, no static OHBridge dep) ────────

    private static Object callBridge(String method, Class<?>[] types, Object... args) {
        try {
            Class<?> c = Class.forName("com.ohos.shim.bridge.OHBridge");
            return c.getMethod(method, types).invoke(null, args);
        } catch (Throwable t) { return null; }
    }

    private static int callBridgeInt(String method, Class<?>[] types, Object... args) {
        Object r = callBridge(method, types, args);
        return r instanceof Number ? ((Number) r).intValue() : 0;
    }

    private static boolean callBridgeBool(String method, Class<?>[] types, Object... args) {
        Object r = callBridge(method, types, args);
        return r instanceof Boolean && (Boolean) r;
    }

    // ── In-process state ───────────────────────────────────────────

    private int mMode = MODE_NORMAL;
    private boolean mSpeakerphoneOn = false;

    // ── Volume ─────────────────────────────────────────────────────

    public int getStreamVolume(int streamType) {
        return callBridgeInt("audioGetStreamVolume", new Class<?>[]{int.class}, streamType);
    }

    public int getStreamMaxVolume(int streamType) {
        return callBridgeInt("audioGetStreamMaxVolume", new Class<?>[]{int.class}, streamType);
    }

    public void setStreamVolume(int streamType, int index, int flags) {
        callBridge("audioSetStreamVolume", new Class<?>[]{int.class, int.class, int.class}, streamType, index, flags);
    }

    public void adjustStreamVolume(int streamType, int direction, int flags) {
        int current = getStreamVolume(streamType);
        int max     = getStreamMaxVolume(streamType);
        int next    = Math.max(0, Math.min(max, current + direction));
        setStreamVolume(streamType, next, flags);
    }

    // ── Ringer mode ────────────────────────────────────────────────

    public int getRingerMode() {
        return callBridgeInt("audioGetRingerMode", new Class<?>[0]);
    }

    public void setRingerMode(int mode) {
        callBridge("audioSetRingerMode", new Class<?>[]{int.class}, mode);
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
        return callBridgeBool("audioIsMusicActive", new Class<?>[0]);
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
        return AUDIOFOCUS_REQUEST_GRANTED;
    }

    public int abandonAudioFocus(OnAudioFocusChangeListener listener) {
        return AUDIOFOCUS_REQUEST_GRANTED;
    }
}
