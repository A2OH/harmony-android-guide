package android.widget;

import android.view.SurfaceView;

/**
 * Shim: android.widget.VideoView
 *
 * Displays a video file. Wraps a SurfaceView and manages media-player
 * lifecycle on Android. In this shim all playback operations are no-ops;
 * the node maps to an ArkUI XComponent surface.
 *
 * OH mapping: XComponent (surface) + AVPlayer API — playback not yet wired.
 */
public class VideoView extends SurfaceView {

    /** Callback fired when a video is prepared and ready to play. */
    public interface OnPreparedListener {
        void onPrepared(VideoView view);
    }

    /** Callback fired when playback reaches end-of-stream. */
    public interface OnCompletionListener {
        void onCompletion(VideoView view);
    }

    /** Callback fired on a playback or setup error. */
    public interface OnErrorListener {
        /** @return true if the error was handled, false to show a default dialog. */
        boolean onError(VideoView view, int what, int extra);
    }

    /** Callback fired when an informational event occurs. */
    public interface OnInfoListener {
        boolean onInfo(VideoView view, int what, int extra);
    }

    private String                mVideoPath;
    private Object                mVideoUri;   // android.net.Uri — kept as Object to break deps
    private OnPreparedListener    mOnPrepared;
    private OnCompletionListener  mOnCompletion;
    private OnErrorListener       mOnError;
    private OnInfoListener        mOnInfo;
    private MediaController       mMediaController;
    private boolean               mStartWhenPrepared;
    private int                   mSeekWhenPrepared;

    public VideoView() {
        super();
    }

    public VideoView(Object context) {
        super();
    }

    public VideoView(Object context, Object attrs) {
        super();
    }

    public VideoView(Object context, Object attrs, int defStyleAttr) {
        super();
    }

    // ── Video source ──────────────────────────────────────────────────────────

    /**
     * Sets the video source as a file-system path or HTTP URL.
     *
     * @param path  file path or URI string
     */
    public void setVideoPath(String path) {
        this.mVideoPath = path;
        openVideo();
    }

    /**
     * Sets the video source as a URI object.
     *
     * @param uri  android.net.Uri (accepted as Object to avoid dependency)
     */
    public void setVideoURI(Object uri) {
        this.mVideoUri = uri;
        openVideo();
    }

    /** Clears the current video source and resets state. No-op in shim. */
    public void stopPlayback() {
        mVideoPath = null;
        mVideoUri  = null;
    }

    private void openVideo() {
        // No-op in shim — would initialise AVPlayer in OH.
    }

    // ── Playback control ──────────────────────────────────────────────────────

    /** Start or resume playback. No-op in shim. */
    public void start() { mStartWhenPrepared = true; }

    /** Pause playback. No-op in shim. */
    public void pause() { mStartWhenPrepared = false; }

    /** Stop playback and release resources. No-op in shim. */
    public void stopPlayBack() { stopPlayback(); }

    /** Resume playback. No-op in shim. */
    public void resume() { mStartWhenPrepared = true; }

    /** Seek to the given position in milliseconds. No-op in shim. */
    public void seekTo(int msec) { mSeekWhenPrepared = msec; }

    /** @return whether the video is currently playing. Always false in shim. */
    public boolean isPlaying() { return false; }

    /** @return current playback position in ms. Always 0 in shim. */
    public int getCurrentPosition() { return 0; }

    /** @return total duration of the video in ms. Always 0 in shim. */
    public int getDuration() { return 0; }

    /** @return buffer fill level as a percentage 0–100. Always 0 in shim. */
    public int getBufferPercentage() { return 0; }

    /** @return whether the media player can pause. */
    public boolean canPause() { return true; }

    /** @return whether backward seeking is supported. */
    public boolean canSeekBackward() { return true; }

    /** @return whether forward seeking is supported. */
    public boolean canSeekForward() { return true; }

    // ── Controller / listeners ────────────────────────────────────────────────

    /**
     * Attach a {@link MediaController} overlay to this VideoView.
     *
     * @param controller  the controller to attach, or null to detach.
     */
    public void setMediaController(MediaController controller) {
        this.mMediaController = controller;
    }

    public void setOnPreparedListener(OnPreparedListener listener) {
        mOnPrepared = listener;
    }

    public void setOnCompletionListener(OnCompletionListener listener) {
        mOnCompletion = listener;
    }

    public void setOnErrorListener(OnErrorListener listener) {
        mOnError = listener;
    }

    public void setOnInfoListener(OnInfoListener listener) {
        mOnInfo = listener;
    }
}
