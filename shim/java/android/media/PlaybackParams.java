package android.media;

/**
 * Android-compatible PlaybackParams shim. Stub for playback parameter tuning.
 */
public class PlaybackParams {

    public static final int AUDIO_FALLBACK_MODE_DEFAULT = 0;
    public static final int AUDIO_FALLBACK_MODE_MUTE = 1;
    public static final int AUDIO_FALLBACK_MODE_FAIL = 2;

    private float mSpeed = 1.0f;
    private float mPitch = 1.0f;
    private int mAudioFallbackMode = AUDIO_FALLBACK_MODE_DEFAULT;

    public PlaybackParams() {}

    public PlaybackParams setSpeed(float speed) {
        mSpeed = speed;
        return this;
    }

    public float getSpeed() { return mSpeed; }

    public PlaybackParams setPitch(float pitch) {
        mPitch = pitch;
        return this;
    }

    public float getPitch() { return mPitch; }

    public PlaybackParams setAudioFallbackMode(int audioFallbackMode) {
        mAudioFallbackMode = audioFallbackMode;
        return this;
    }

    public int getAudioFallbackMode() { return mAudioFallbackMode; }

    public PlaybackParams allowDefaults() {
        return this;
    }
}
