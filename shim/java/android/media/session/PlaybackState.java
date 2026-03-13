package android.media.session;

/**
 * Android-compatible PlaybackState shim. Stub for media session playback state.
 */
public final class PlaybackState {
    public static final int STATE_NONE                   = 0;
    public static final int STATE_STOPPED                = 1;
    public static final int STATE_PAUSED                 = 2;
    public static final int STATE_PLAYING                = 3;
    public static final int STATE_FAST_FORWARDING        = 4;
    public static final int STATE_REWINDING              = 5;
    public static final int STATE_BUFFERING              = 6;
    public static final int STATE_ERROR                  = 7;
    public static final int STATE_CONNECTING             = 8;
    public static final int STATE_SKIPPING_TO_PREVIOUS   = 9;
    public static final int STATE_SKIPPING_TO_NEXT       = 10;
    public static final int STATE_SKIPPING_TO_QUEUE_ITEM = 11;

    public static final long ACTION_STOP              = 1L << 0;
    public static final long ACTION_PAUSE             = 1L << 1;
    public static final long ACTION_PLAY              = 1L << 2;
    public static final long ACTION_REWIND            = 1L << 3;
    public static final long ACTION_SKIP_TO_PREVIOUS  = 1L << 4;
    public static final long ACTION_SKIP_TO_NEXT      = 1L << 5;
    public static final long ACTION_FAST_FORWARD      = 1L << 6;
    public static final long ACTION_SET_RATING        = 1L << 7;
    public static final long ACTION_SEEK_TO           = 1L << 8;
    public static final long ACTION_PLAY_PAUSE        = 1L << 9;
    public static final long ACTION_PLAY_FROM_MEDIA_ID = 1L << 10;
    public static final long ACTION_PLAY_FROM_SEARCH  = 1L << 11;
    public static final long ACTION_SKIP_TO_QUEUE_ITEM = 1L << 12;
    public static final long ACTION_PLAY_FROM_URI     = 1L << 13;
    public static final long ACTION_PREPARE           = 1L << 14;
    public static final long ACTION_PREPARE_FROM_MEDIA_ID = 1L << 15;
    public static final long ACTION_PREPARE_FROM_SEARCH = 1L << 16;
    public static final long ACTION_PREPARE_FROM_URI  = 1L << 17;

    public static final long PLAYBACK_POSITION_UNKNOWN = -1L;

    private final int mState;
    private final long mPosition;
    private final float mSpeed;
    private final long mActions;
    private final long mUpdateTime;

    private PlaybackState(Builder b) {
        mState      = b.mState;
        mPosition   = b.mPosition;
        mSpeed      = b.mSpeed;
        mActions    = b.mActions;
        mUpdateTime = b.mUpdateTime;
    }

    public int   getState()                  { return mState; }
    public long  getPosition()               { return mPosition; }
    public float getPlaybackSpeed()          { return mSpeed; }
    public long  getActions()                { return mActions; }
    public long  getLastPositionUpdateTime() { return mUpdateTime; }

    @Override
    public String toString() {
        return "PlaybackState{state=" + mState + ", position=" + mPosition
                + ", speed=" + mSpeed + ", actions=" + mActions + "}";
    }

    // -----------------------------------------------------------------------
    public static final class Builder {
        private int   mState      = STATE_NONE;
        private long  mPosition   = PLAYBACK_POSITION_UNKNOWN;
        private float mSpeed      = 1.0f;
        private long  mActions    = 0;
        private long  mUpdateTime = 0;

        public Builder() {}

        public Builder(PlaybackState from) {
            if (from != null) {
                mState      = from.mState;
                mPosition   = from.mPosition;
                mSpeed      = from.mSpeed;
                mActions    = from.mActions;
                mUpdateTime = from.mUpdateTime;
            }
        }

        public Builder setState(int state, long position, float playbackSpeed) {
            mState    = state;
            mPosition = position;
            mSpeed    = playbackSpeed;
            mUpdateTime = System.currentTimeMillis();
            return this;
        }

        public Builder setState(int state, long position, float playbackSpeed, long updateTime) {
            mState      = state;
            mPosition   = position;
            mSpeed      = playbackSpeed;
            mUpdateTime = updateTime;
            return this;
        }

        public Builder setActions(long actions) {
            mActions = actions;
            return this;
        }

        public PlaybackState build() {
            return new PlaybackState(this);
        }
    }
}
