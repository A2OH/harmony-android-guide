package android.media;

/**
 * Android-compatible AudioFocusRequest shim. Stub for audio focus requests.
 */
public class AudioFocusRequest {

    private final int mFocusGain;

    private AudioFocusRequest(int focusGain) {
        mFocusGain = focusGain;
    }

    public int getFocusGain() { return mFocusGain; }

    public Object getAudioAttributes() { return null; }

    /**
     * Builder for AudioFocusRequest.
     */
    public static class Builder {
        private int mFocusGain;
        private boolean mPauseOnDuck;
        private boolean mAcceptsDelayed;

        public Builder(int focusGain) {
            mFocusGain = focusGain;
        }

        public Builder setFocusGain(int focusGain) {
            mFocusGain = focusGain;
            return this;
        }

        public Builder setAudioAttributes(Object attributes) {
            return this;
        }

        public Builder setWillPauseWhenDucked(boolean pauseOnDuck) {
            mPauseOnDuck = pauseOnDuck;
            return this;
        }

        public Builder setAcceptsDelayedFocusGain(boolean acceptsDelayed) {
            mAcceptsDelayed = acceptsDelayed;
            return this;
        }

        public Builder setOnAudioFocusChangeListener(Object listener) {
            return this;
        }

        public Builder setOnAudioFocusChangeListener(Object listener, Object handler) {
            return this;
        }

        public AudioFocusRequest build() {
            return new AudioFocusRequest(mFocusGain);
        }
    }
}
