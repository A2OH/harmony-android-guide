package android.media;

import java.util.HashMap;
import java.util.Map;

/**
 * Android-compatible SoundPool shim. Stub for short audio clip playback.
 *
 * Note: android.content.Context is not referenced here to keep this shim
 * self-contained. The load(context, resId, priority) overload accepts Object
 * in place of Context.
 */
public class SoundPool {

    /** Listener invoked when a sound has finished loading. */
    public interface OnLoadCompleteListener {
        void onLoadComplete(SoundPool soundPool, int sampleId, int status);
    }

    private final int mMaxStreams;
    private final AudioAttributes mAudioAttributes;
    private int mNextSoundId = 1;
    private int mNextStreamId = 1;
    private final Map<Integer, String> mSounds = new HashMap<>();
    private boolean mReleased = false;
    private OnLoadCompleteListener mLoadCompleteListener;

    // Private constructor — use Builder.
    private SoundPool(int maxStreams, AudioAttributes audioAttributes) {
        mMaxStreams      = maxStreams;
        mAudioAttributes = audioAttributes;
    }

    // ── Loading ──

    /**
     * Loads a sound from a file path.
     *
     * @param path     path to the audio file
     * @param priority loading priority (higher = more important)
     * @return a positive sound ID on success
     */
    public int load(String path, int priority) {
        checkNotReleased();
        int soundId = mNextSoundId++;
        mSounds.put(soundId, path);
        System.out.println("[SoundPool] load(" + path + ") -> soundId=" + soundId);
        if (mLoadCompleteListener != null) {
            mLoadCompleteListener.onLoadComplete(this, soundId, 0);
        }
        return soundId;
    }

    /**
     * Loads a sound from a resource ID (stub — resource system is not available in this shim).
     *
     * @param context ignored (accepts Object to avoid android.content.Context dependency)
     * @param resId   Android resource identifier
     * @param priority loading priority
     * @return a positive sound ID on success
     */
    public int load(Object context, int resId, int priority) {
        return load("res:" + resId, priority);
    }

    // ── Playback ──

    /**
     * Plays the sound identified by {@code soundId}.
     *
     * @param soundId     ID returned by {@link #load}
     * @param leftVolume  left channel volume [0.0, 1.0]
     * @param rightVolume right channel volume [0.0, 1.0]
     * @param priority    stream priority (0 = lowest)
     * @param loop        0 = no loop; -1 = loop forever
     * @param rate        playback rate [0.5, 2.0]
     * @return a positive stream ID, or 0 on failure
     */
    public int play(int soundId, float leftVolume, float rightVolume,
                    int priority, int loop, float rate) {
        checkNotReleased();
        if (!mSounds.containsKey(soundId)) return 0;
        int streamId = mNextStreamId++;
        System.out.println("[SoundPool] play(soundId=" + soundId + ") -> streamId=" + streamId);
        return streamId;
    }

    public void pause(int streamId) {
        checkNotReleased();
        System.out.println("[SoundPool] pause(streamId=" + streamId + ")");
    }

    public void resume(int streamId) {
        checkNotReleased();
        System.out.println("[SoundPool] resume(streamId=" + streamId + ")");
    }

    public void stop(int streamId) {
        checkNotReleased();
        System.out.println("[SoundPool] stop(streamId=" + streamId + ")");
    }

    public void setVolume(int streamId, float leftVolume, float rightVolume) {
        checkNotReleased();
        System.out.println("[SoundPool] setVolume(streamId=" + streamId
                + ", l=" + leftVolume + ", r=" + rightVolume + ")");
    }

    public void setLoop(int streamId, int loop) {
        checkNotReleased();
        System.out.println("[SoundPool] setLoop(streamId=" + streamId + ", loop=" + loop + ")");
    }

    public void setRate(int streamId, float rate) {
        checkNotReleased();
        System.out.println("[SoundPool] setRate(streamId=" + streamId + ", rate=" + rate + ")");
    }

    public void setOnLoadCompleteListener(OnLoadCompleteListener listener) {
        mLoadCompleteListener = listener;
    }

    // ── Lifecycle ──

    public void autoPause() {
        System.out.println("[SoundPool] autoPause");
    }

    public void autoResume() {
        System.out.println("[SoundPool] autoResume");
    }

    public void release() {
        mReleased = true;
        mSounds.clear();
        System.out.println("[SoundPool] release");
    }

    // ── Accessors ──

    public int getMaxStreams()                  { return mMaxStreams;      }
    public AudioAttributes getAudioAttributes() { return mAudioAttributes; }

    private void checkNotReleased() {
        if (mReleased) throw new IllegalStateException("SoundPool already released");
    }

    // ── Builder ──

    public static class Builder {
        private int mMaxStreams = 1;
        private AudioAttributes mAudioAttributes;

        public Builder() {}

        public Builder setMaxStreams(int maxStreams) {
            if (maxStreams <= 0) throw new IllegalArgumentException("maxStreams must be > 0");
            mMaxStreams = maxStreams;
            return this;
        }

        public Builder setAudioAttributes(AudioAttributes attributes) {
            mAudioAttributes = attributes;
            return this;
        }

        public SoundPool build() {
            if (mAudioAttributes == null) {
                mAudioAttributes = new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build();
            }
            return new SoundPool(mMaxStreams, mAudioAttributes);
        }
    }
}
