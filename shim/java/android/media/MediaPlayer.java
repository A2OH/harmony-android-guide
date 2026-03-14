package android.media;

/**
 * Shim for android.media.MediaPlayer → @ohos.multimedia.media.AVPlayer
 *
 * Maps the Android MediaPlayer state machine onto OpenHarmony AVPlayer via
 * the OHBridge JNI layer (accessed by reflection to avoid static init crash).
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

    public interface OnErrorListener {
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
    private OnErrorListener          mOnErrorListener;
    private OnSeekCompleteListener   mOnSeekCompleteListener;
    private OnBufferingUpdateListener mOnBufferingUpdateListener;

    // ── Bridge helper (reflection, no static OHBridge dep) ────────

    private static Object callBridge(String method, Class<?>[] types, Object... args) {
        try {
            Class<?> c = Class.forName("com.ohos.shim.bridge.OHBridge");
            return c.getMethod(method, types).invoke(null, args);
        } catch (Throwable t) { return null; }
    }

    private static long callBridgeLong(String method, Class<?>[] types, Object... args) {
        Object r = callBridge(method, types, args);
        return r instanceof Number ? ((Number) r).longValue() : 0L;
    }

    private static int callBridgeInt(String method, Class<?>[] types, Object... args) {
        Object r = callBridge(method, types, args);
        return r instanceof Number ? ((Number) r).intValue() : 0;
    }

    private static boolean callBridgeBool(String method, Class<?>[] types, Object... args) {
        Object r = callBridge(method, types, args);
        return r instanceof Boolean && (Boolean) r;
    }

    // ── Constructor ────────────────────────────────────────────────

    public MediaPlayer() {
        mHandle = callBridgeLong("mediaPlayerCreate", new Class<?>[0]);
        mState = State.IDLE;
    }

    // ── Data source ────────────────────────────────────────────────

    public void setDataSource(String path) {
        checkState("setDataSource", State.IDLE);
        callBridge("mediaPlayerSetDataSource", new Class<?>[]{long.class, String.class}, mHandle, path);
        mState = State.INITIALIZED;
    }

    // ── Prepare ────────────────────────────────────────────────────

    public void prepare() throws IllegalStateException {
        checkState("prepare", State.INITIALIZED, State.STOPPED);
        mState = State.PREPARING;
        callBridge("mediaPlayerPrepare", new Class<?>[]{long.class}, mHandle);
        mState = State.PREPARED;
        if (mOnPreparedListener != null) {
            mOnPreparedListener.onPrepared(this);
        }
    }

    public void prepareAsync() throws IllegalStateException {
        checkState("prepareAsync", State.INITIALIZED, State.STOPPED);
        mState = State.PREPARING;
        callBridge("mediaPlayerPrepare", new Class<?>[]{long.class}, mHandle);
        mState = State.PREPARED;
        if (mOnPreparedListener != null) {
            mOnPreparedListener.onPrepared(this);
        }
    }

    // ── Playback control ───────────────────────────────────────────

    public void start() throws IllegalStateException {
        checkState("start", State.PREPARED, State.PAUSED, State.STARTED);
        callBridge("mediaPlayerStart", new Class<?>[]{long.class}, mHandle);
        mState = State.STARTED;
    }

    public void pause() throws IllegalStateException {
        checkState("pause", State.STARTED, State.PAUSED);
        callBridge("mediaPlayerPause", new Class<?>[]{long.class}, mHandle);
        mState = State.PAUSED;
    }

    public void stop() throws IllegalStateException {
        checkState("stop", State.PREPARED, State.STARTED, State.PAUSED, State.STOPPED);
        callBridge("mediaPlayerStop", new Class<?>[]{long.class}, mHandle);
        mState = State.STOPPED;
    }

    public void release() {
        if (mState != State.END) {
            callBridge("mediaPlayerRelease", new Class<?>[]{long.class}, mHandle);
            mState = State.END;
        }
    }

    // ── Seek ───────────────────────────────────────────────────────

    public void seekTo(int msec) throws IllegalStateException {
        checkState("seekTo", State.PREPARED, State.STARTED, State.PAUSED, State.STOPPED);
        callBridge("mediaPlayerSeekTo", new Class<?>[]{long.class, int.class}, mHandle, msec);
        if (mOnSeekCompleteListener != null) {
            mOnSeekCompleteListener.onSeekComplete(this);
        }
    }

    // ── Reset ──────────────────────────────────────────────────────

    public void reset() {
        callBridge("mediaPlayerReset", new Class<?>[]{long.class}, mHandle);
        mState = State.IDLE;
    }

    // ── Queries ────────────────────────────────────────────────────

    public int getDuration() {
        checkNotEnd("getDuration");
        return callBridgeInt("mediaPlayerGetDuration", new Class<?>[]{long.class}, mHandle);
    }

    public int getCurrentPosition() {
        checkNotEnd("getCurrentPosition");
        return callBridgeInt("mediaPlayerGetCurrentPosition", new Class<?>[]{long.class}, mHandle);
    }

    public boolean isPlaying() {
        if (mState == State.END) return false;
        return callBridgeBool("mediaPlayerIsPlaying", new Class<?>[]{long.class}, mHandle);
    }

    // ── Volume ─────────────────────────────────────────────────────

    public void setVolume(float leftVolume, float rightVolume) {
        checkNotEnd("setVolume");
        callBridge("mediaPlayerSetVolume", new Class<?>[]{long.class, float.class, float.class}, mHandle, leftVolume, rightVolume);
    }

    // ── Looping ────────────────────────────────────────────────────

    public void setLooping(boolean looping) {
        checkNotEnd("setLooping");
        callBridge("mediaPlayerSetLooping", new Class<?>[]{long.class, boolean.class}, mHandle, looping);
    }

    public boolean isLooping() {
        return false;
    }

    // ── Listener setters ───────────────────────────────────────────

    public void setOnPreparedListener(OnPreparedListener listener) {
        mOnPreparedListener = listener;
    }

    public void setOnCompletionListener(OnCompletionListener listener) {
        mOnCompletionListener = listener;
    }

    public void setOnErrorListener(OnErrorListener listener) {
        mOnErrorListener = listener;
    }

    public void setOnSeekCompleteListener(OnSeekCompleteListener listener) {
        mOnSeekCompleteListener = listener;
    }

    public void setOnBufferingUpdateListener(OnBufferingUpdateListener listener) {
        mOnBufferingUpdateListener = listener;
    }

    // ── State accessor ─────────────────────────────────────────────

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
