package android.media;

import com.ohos.shim.bridge.OHBridge;
import java.net.URL;

/**
 * Shim for android.media.MediaPlayer → @ohos.multimedia.media.AVPlayer
 *
 * Maps the Android MediaPlayer state machine onto OpenHarmony AVPlayer via
 * the OHBridge JNI layer.
 *
 * State machine: IDLE → INITIALIZED → PREPARING → PREPARED →
 *                STARTED → PAUSED → STOPPED → END
 */
public class MediaPlayer {

    // ── State machine ──────────────────────────────────────────────

    public enum State {
        IDLE,
        INITIALIZED,
        PREPARING,
        PREPARED,
        STARTED,
        PAUSED,
        STOPPED,
        END
    }

    // ── Listener interfaces ────────────────────────────────────────

    public interface OnPreparedListener {
        void onPrepared(MediaPlayer mp);
    }

    public interface OnCompletionListener {
        void onCompletion(MediaPlayer mp);
    }

    public interface Object {
        /** Return true if the error has been handled. */
        boolean onError(MediaPlayer mp, int what, int extra);
    }

    public interface OnSeekCompleteListener {
        void onSeekComplete(MediaPlayer mp);
    }

    public interface OnBufferingUpdateListener {
        void onBufferingUpdate(MediaPlayer mp, int percent);
    }

    // ── Fields ─────────────────────────────────────────────────────

    private long mHandle;
    private State mState = State.IDLE;

    private OnPreparedListener       mOnPreparedListener;
    private OnCompletionListener     mOnCompletionListener;
    private Object          mOnErrorListener;
    private OnSeekCompleteListener   mOnSeekCompleteListener;
    private OnBufferingUpdateListener mOnBufferingUpdateListener;

    // ── Constructor ────────────────────────────────────────────────

    public MediaPlayer() {
        mHandle = OHBridge.mediaPlayerCreate();
        mState = State.IDLE;
    }

    // ── Data source ────────────────────────────────────────────────

    /**
     * Sets the data source (file path or URL).
     * Corresponds to AVPlayer.url.
     */
    public void setDataSource(String path) {
        checkState("setDataSource", State.IDLE);
        OHBridge.mediaPlayerSetDataSource(mHandle, path);
        mState = State.INITIALIZED;
    }

    // ── Prepare ────────────────────────────────────────────────────

    /** Synchronous prepare → AVPlayer.prepare. */
    public void prepare() throws IllegalStateException {
        checkState("prepare", State.INITIALIZED, State.STOPPED);
        mState = State.PREPARING;
        OHBridge.mediaPlayerPrepare(mHandle);
        mState = State.PREPARED;
        if (mOnPreparedListener != null) {
            mOnPreparedListener.onPrepared(this);
        }
    }

    /**
     * Asynchronous prepare → AVPlayer.prepare (async).
     * Fires onPrepared callback when ready.
     */
    public void prepareAsync() throws IllegalStateException {
        checkState("prepareAsync", State.INITIALIZED, State.STOPPED);
        mState = State.PREPARING;
        // Bridge call is the same; callback delivery is simulated synchronously here.
        OHBridge.mediaPlayerPrepare(mHandle);
        mState = State.PREPARED;
        if (mOnPreparedListener != null) {
            mOnPreparedListener.onPrepared(this);
        }
    }

    // ── Playback control ───────────────────────────────────────────

    /** Start/resume playback → AVPlayer.play. */
    public void start() throws IllegalStateException {
        checkState("start", State.PREPARED, State.PAUSED, State.STARTED);
        OHBridge.mediaPlayerStart(mHandle);
        mState = State.STARTED;
    }

    /** Pause playback → AVPlayer.pause. */
    public void pause() throws IllegalStateException {
        checkState("pause", State.STARTED, State.PAUSED);
        OHBridge.mediaPlayerPause(mHandle);
        mState = State.PAUSED;
    }

    /** Stop playback → AVPlayer.stop. */
    public void stop() throws IllegalStateException {
        checkState("stop", State.PREPARED, State.STARTED, State.PAUSED, State.STOPPED);
        OHBridge.mediaPlayerStop(mHandle);
        mState = State.STOPPED;
    }

    /** Release all resources → AVPlayer.release. */
    public void release() {
        if (mState != State.END) {
            OHBridge.mediaPlayerRelease(mHandle);
            mState = State.END;
        }
    }

    // ── Seek ───────────────────────────────────────────────────────

    /** Seek to position in milliseconds → AVPlayer.seek. */
    public void seekTo(int msec) throws IllegalStateException {
        checkState("seekTo", State.PREPARED, State.STARTED, State.PAUSED, State.STOPPED);
        OHBridge.mediaPlayerSeekTo(mHandle, msec);
        if (mOnSeekCompleteListener != null) {
            mOnSeekCompleteListener.onSeekComplete(this);
        }
    }

    // ── Reset ──────────────────────────────────────────────────────

    /** Reset to IDLE state → AVPlayer.reset. */
    public void reset() {
        OHBridge.mediaPlayerReset(mHandle);
        mState = State.IDLE;
    }

    // ── Queries ────────────────────────────────────────────────────

    /** Returns total duration in milliseconds → AVPlayer.duration. */
    public int getDuration() {
        checkNotEnd("getDuration");
        return OHBridge.mediaPlayerGetDuration(mHandle);
    }

    /** Returns current playback position in milliseconds → AVPlayer.currentTime. */
    public int getCurrentPosition() {
        checkNotEnd("getCurrentPosition");
        return OHBridge.mediaPlayerGetCurrentPosition(mHandle);
    }

    /** Returns true if currently playing → AVPlayer.state check. */
    public boolean isPlaying() {
        if (mState == State.END) return false;
        return OHBridge.mediaPlayerIsPlaying(mHandle);
    }

    // ── Volume ─────────────────────────────────────────────────────

    /**
     * Sets left and right channel volumes (0.0–1.0) → AVPlayer.setVolume.
     */
    public void setVolume(float leftVolume, float rightVolume) {
        checkNotEnd("setVolume");
        OHBridge.mediaPlayerSetVolume(mHandle, leftVolume, rightVolume);
    }

    // ── Looping ────────────────────────────────────────────────────

    /** Enable/disable looping → AVPlayer.loop. */
    public void setLooping(boolean looping) {
        checkNotEnd("setLooping");
        OHBridge.mediaPlayerSetLooping(mHandle, looping);
    }

    public boolean isLooping() {
        // State not tracked locally; bridge would need a getter.
        // Returning false as a safe default.
        return false;
    }

    // ── Listener setters ───────────────────────────────────────────

    public void setOnPreparedListener(OnPreparedListener listener) {
        mOnPreparedListener = listener;
    }

    public void setOnCompletionListener(OnCompletionListener listener) {
        mOnCompletionListener = listener;
    }

    public void setOnErrorListener(Object listener) {
        mOnErrorListener = listener;
    }

    public void setOnSeekCompleteListener(OnSeekCompleteListener listener) {
        mOnSeekCompleteListener = listener;
    }

    public void setOnBufferingUpdateListener(OnBufferingUpdateListener listener) {
        mOnBufferingUpdateListener = listener;
    }

    // ── State accessor ─────────────────────────────────────────────

    /** Returns the current internal state (not an Android API, useful for testing). */
    public State getState() {
        return mState;
    }

    // ── Private helpers ────────────────────────────────────────────

    private void checkState(String method, State... allowed) {
        for (State s : allowed) {
            if (mState == s) return;
        }
        throw new IllegalStateException(
            "MediaPlayer." + method + "() called in invalid state: " + mState);
    }

    private void checkNotEnd(String method) {
        if (mState == State.END) {
            throw new IllegalStateException(
                "MediaPlayer." + method + "() called after release()");
        }
    }
}
