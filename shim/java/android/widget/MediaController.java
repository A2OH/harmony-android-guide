package android.widget;

/**
 * Shim: android.widget.MediaController
 *
 * Provides playback controls (play/pause, seek bar, fast-forward/rewind)
 * that float above a media surface. In the shim this is a no-op FrameLayout.
 *
 * OH mapping: custom ArkUI overlay component — not yet implemented.
 */
public class MediaController extends FrameLayout {

    /** Implemented by the media player that this controller drives. */
    public interface MediaPlayerControl {
        void  start();
        void  pause();
        int   getDuration();
        int   getCurrentPosition();
        void  seekTo(int pos);
        boolean isPlaying();
        int   getBufferPercentage();
        boolean canPause();
        boolean canSeekBackward();
        boolean canSeekForward();
        boolean isFullScreen();
        void  toggleFullScreen();
    }

    private MediaPlayerControl mPlayer;
    private boolean            mShowing;

    public MediaController() {
        super();
    }

    public MediaController(Object context) {
        super();
    }

    public MediaController(Object context, boolean useFastForward) {
        super();
    }

    public MediaController(Object context, Object attrs) {
        super();
    }

    /**
     * Attach the controller to the player whose state it should reflect.
     *
     * @param player  the media player to control
     * @param anchor  the view that should be used as the anchor (ignored in shim)
     */
    public void setMediaPlayer(MediaPlayerControl player) {
        this.mPlayer = player;
    }

    /**
     * Sets the anchor view. The controller floats above this view.
     * No-op in shim.
     */
    public void setAnchorView(android.view.View view) {
        // no-op in shim
    }

    /**
     * Show the controller for the default timeout (3 s on Android).
     * No-op in shim.
     */
    public void show() {
        mShowing = true;
    }

    /**
     * Show the controller for a specific duration (ms). 0 means indefinitely.
     * No-op in shim.
     */
    public void show(int timeout) {
        mShowing = true;
    }

    /** @return whether the controller overlay is currently visible. */
    public boolean isShowing() {
        return mShowing;
    }

    /** Hide the controller overlay. No-op in shim. */
    public void hide() {
        mShowing = false;
    }

    /**
     * Set a custom title for the controller (e.g., current media title).
     * No-op in shim.
     */
    public void setMediaTitle(CharSequence title) {
        // no-op in shim
    }

    /**
     * Enable or disable the previous/next track buttons.
     * No-op in shim.
     */
    public void setPrevNextListeners(android.view.View.OnClickListener next,
                                     android.view.View.OnClickListener prev) {
        // no-op in shim
    }
}
