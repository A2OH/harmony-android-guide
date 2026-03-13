package android.media;

/**
 * Android-compatible ToneGenerator shim. Stub — no audio output.
 */
public class ToneGenerator {

    // ---- stream type constants (mirrors AudioManager) ----

    public static final int STREAM_VOICE_CALL   = 0;
    public static final int STREAM_SYSTEM       = 1;
    public static final int STREAM_RING         = 2;
    public static final int STREAM_MUSIC        = 3;
    public static final int STREAM_ALARM        = 4;
    public static final int STREAM_NOTIFICATION = 5;
    public static final int STREAM_DTMF         = 8;

    // ---- max volume constant ----

    public static final int MAX_VOLUME = 100;

    // ---- DTMF tone constants ----

    public static final int TONE_DTMF_0 = 0;
    public static final int TONE_DTMF_1 = 1;
    public static final int TONE_DTMF_2 = 2;
    public static final int TONE_DTMF_3 = 3;
    public static final int TONE_DTMF_4 = 4;
    public static final int TONE_DTMF_5 = 5;
    public static final int TONE_DTMF_6 = 6;
    public static final int TONE_DTMF_7 = 7;
    public static final int TONE_DTMF_8 = 8;
    public static final int TONE_DTMF_9 = 9;
    public static final int TONE_DTMF_S = 10; // *
    public static final int TONE_DTMF_P = 11; // #
    public static final int TONE_DTMF_A = 12;
    public static final int TONE_DTMF_B = 13;
    public static final int TONE_DTMF_C = 14;
    public static final int TONE_DTMF_D = 15;

    // ---- SUP (supervisory) tone constants ----

    public static final int TONE_SUP_DIAL          = 16;
    public static final int TONE_SUP_BUSY          = 17;
    public static final int TONE_SUP_CONGESTION    = 18;
    public static final int TONE_SUP_RADIO_ACK     = 19;
    public static final int TONE_SUP_RADIO_NOTAVAIL = 20;
    public static final int TONE_SUP_ERROR         = 21;
    public static final int TONE_SUP_CALL_WAITING  = 22;
    public static final int TONE_SUP_RINGTONE      = 23;

    // ---- PROP tone constants ----

    public static final int TONE_PROP_BEEP         = 24;
    public static final int TONE_PROP_ACK          = 25;
    public static final int TONE_PROP_NACK         = 26;
    public static final int TONE_PROP_PROMPT       = 27;
    public static final int TONE_PROP_BEEP2        = 28;

    // ---- private state ----

    private final int mStreamType;
    private final int mVolume;
    private boolean   mPlaying;

    public ToneGenerator(int streamType, int volume) {
        mStreamType = streamType;
        mVolume     = volume;
    }

    // ---- control ----

    /**
     * Start tone with an optional duration (-1 = until stopTone()).
     */
    public boolean startTone(int toneType, int durationMs) {
        mPlaying = true;
        return true; // stub: always succeeds
    }

    public boolean startTone(int toneType) {
        return startTone(toneType, -1);
    }

    public void stopTone() {
        mPlaying = false;
    }

    public void release() {
        mPlaying = false;
    }

    public int getStreamType() { return mStreamType; }
    public int getVolume()     { return mVolume; }
    public boolean isPlaying() { return mPlaying; }
}
