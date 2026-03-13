package android.media.session;

/**
 * Android-compatible MediaSession shim. Stub — no IPC or media focus handling.
 */
public class MediaSession {

    // ---- Token inner class ----

    public static final class Token {
        private final String mTag;

        Token(String tag) {
            mTag = tag;
        }

        public String getTag() { return mTag; }

        @Override
        public String toString() { return "Token(" + mTag + ")"; }
    }

    // ---- Callback abstract inner class ----

    public static abstract class Callback {
        /** Called when a controller requests play. */
        public void onPlay() {}

        /** Called when a controller requests pause. */
        public void onPause() {}

        /** Called when a controller requests stop. */
        public void onStop() {}

        /** Called when a controller requests skip to next item. */
        public void onSkipToNext() {}

        /** Called when a controller requests skip to previous item. */
        public void onSkipToPrevious() {}

        /** Called when a controller sends a custom action. */
        public void onCustomAction(String action, android.os.Bundle extras) {}

        /** Called when a controller sends a play-from-search request. */
        public void onPlayFromSearch(String query, android.os.Bundle extras) {}

        /** Called when a controller sends a play-from-media-id request. */
        public void onPlayFromMediaId(String mediaId, android.os.Bundle extras) {}

        /** Called when a controller sets the repeat mode. */
        public void onSetRepeatMode(int repeatMode) {}

        /** Called when a controller sets the shuffle mode. */
        public void onSetShuffleMode(int shuffleMode) {}
    }

    // ---- FLAG_* constants ----

    public static final int FLAG_HANDLES_MEDIA_BUTTONS     = 1 << 0;
    public static final int FLAG_HANDLES_TRANSPORT_CONTROLS = 1 << 1;
    public static final int FLAG_HANDLES_QUEUE_COMMANDS    = 1 << 2;

    // ---- private state ----

    private final String  mTag;
    private final Token   mToken;
    private Callback      mCallback;
    private boolean       mActive;
    private int           mFlags;

    /**
     * @param context ignored in shim; may be null
     * @param tag     a short human-readable label for debugging
     */
    public MediaSession(Object context, String tag) {
        mTag   = (tag != null) ? tag : "MediaSession";
        mToken = new Token(mTag);
    }

    // ---- configuration ----

    public void setCallback(Callback callback) {
        mCallback = callback;
    }

    public void setFlags(int flags) {
        mFlags = flags;
    }

    public void setActive(boolean active) {
        mActive = active;
    }

    // ---- token ----

    public Token getSessionToken() {
        return mToken;
    }

    // ---- teardown ----

    public void release() {
        mActive   = false;
        mCallback = null;
    }

    // ---- accessors ----

    public boolean isActive()    { return mActive; }
    public String  getTag()      { return mTag; }
    public int     getFlags()    { return mFlags; }
    public Callback getCallback(){ return mCallback; }
}
