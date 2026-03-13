package android.media;

/**
 * Android-compatible Ringtone shim. Simple stub — no actual audio playback.
 */
public class Ringtone {

    private String  mTitle  = "";
    private float   mVolume = 1.0f;
    private boolean mPlaying;

    public Ringtone() {}

    // ---- playback control ----

    public void play() {
        mPlaying = true;
    }

    public void stop() {
        mPlaying = false;
    }

    public boolean isPlaying() {
        return mPlaying;
    }

    // ---- volume ----

    public void setVolume(float volume) {
        mVolume = Math.max(0f, Math.min(1f, volume));
    }

    public float getVolume() {
        return mVolume;
    }

    // ---- metadata ----

    public String getTitle() {
        return mTitle;
    }

    /** For shim use: allows the title to be set programmatically. */
    public void setTitle(String title) {
        mTitle = title != null ? title : "";
    }

    /** Looping control (stub). */
    public void setLooping(boolean looping) {
        // stub — no-op
    }

    public boolean isLooping() {
        return false;
    }
}
