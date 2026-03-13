package android.media.session;

import android.media.MediaMetadata;
import java.util.ArrayList;
import java.util.List;

/**
 * Android-compatible MediaController shim. Stub for controlling a MediaSession.
 */
public final class MediaController {

    private final Object            mContext;
    private final MediaSession.Token mToken;
    private PlaybackState           mPlaybackState;
    private MediaMetadata           mMetadata;
    private final List<Callback>    mCallbacks = new ArrayList<>();

    public MediaController(Object context, MediaSession.Token token) {
        mContext = context;
        mToken   = token;
    }

    public PlaybackState getPlaybackState() { return mPlaybackState; }
    public MediaMetadata  getMetadata()     { return mMetadata; }

    /** For testing: inject a PlaybackState. */
    public void setPlaybackState(PlaybackState state) {
        mPlaybackState = state;
        for (Callback cb : mCallbacks) { cb.onPlaybackStateChanged(state); }
    }

    /** For testing: inject MediaMetadata. */
    public void setMetadata(MediaMetadata metadata) {
        mMetadata = metadata;
        for (Callback cb : mCallbacks) { cb.onMetadataChanged(metadata); }
    }

    public TransportControls getTransportControls() {
        return new TransportControls();
    }

    public void registerCallback(Callback callback) {
        if (callback != null && !mCallbacks.contains(callback)) {
            mCallbacks.add(callback);
        }
    }

    public void unregisterCallback(Callback callback) {
        mCallbacks.remove(callback);
    }

    public MediaSession.Token getSessionToken() { return mToken; }

    @Override
    public String toString() {
        return "MediaController{token=" + mToken + "}";
    }

    // -----------------------------------------------------------------------
    public final class TransportControls {
        private TransportControls() {}

        public void play()                        { /* stub */ }
        public void pause()                       { /* stub */ }
        public void stop()                        { /* stub */ }
        public void skipToNext()                  { /* stub */ }
        public void skipToPrevious()              { /* stub */ }
        public void seekTo(long pos)              { /* stub */ }
        public void fastForward()                 { /* stub */ }
        public void rewind()                      { /* stub */ }
        public void playPause()                   { /* stub */ }
        public void playFromMediaId(String mediaId, Object extras) { /* stub */ }
        public void playFromSearch(String query,  Object extras)   { /* stub */ }
        public void skipToQueueItem(long id)      { /* stub */ }
        public void sendCustomAction(String action, Object args)   { /* stub */ }
        public void prepare()                     { /* stub */ }
    }

    // -----------------------------------------------------------------------
    public abstract static class Callback {
        public void onSessionDestroyed()                          {}
        public void onPlaybackStateChanged(PlaybackState state)   {}
        public void onMetadataChanged(MediaMetadata metadata)     {}
        public void onQueueChanged(List<?> queue)                 {}
        public void onQueueTitleChanged(CharSequence title)       {}
        public void onExtrasChanged(Object extras)                {}
        public void onAudioInfoChanged(Object info)               {}
        public void onSessionEvent(String event, Object extras)   {}
    }
}
