package android.media;

/**
 * Android-compatible AudioTrack shim. Stub for PCM audio playback.
 */
public class AudioTrack {

    /** Audio playback modes. */
    public static final int MODE_STATIC = 0;
    public static final int MODE_STREAM = 1;

    /** State constants. */
    public static final int STATE_UNINITIALIZED = 0;
    public static final int STATE_INITIALIZED   = 1;
    public static final int STATE_NO_STATIC_DATA = 2;

    /** Playback state constants. */
    public static final int PLAYSTATE_STOPPED = 1;
    public static final int PLAYSTATE_PAUSED  = 2;
    public static final int PLAYSTATE_PLAYING = 3;

    /** Error codes. */
    public static final int SUCCESS          =  0;
    public static final int ERROR            = -1;
    public static final int ERROR_BAD_VALUE  = -2;
    public static final int ERROR_INVALID_OPERATION = -3;

    private final int mStreamType;
    private final int mSampleRate;
    private final int mChannelConfig;
    private final int mAudioFormat;
    private final int mBufferSizeInBytes;
    private final int mMode;
    private int       mPlayState = PLAYSTATE_STOPPED;
    private float     mLeftVolume  = 1.0f;
    private float     mRightVolume = 1.0f;
    private boolean   mReleased    = false;
    private int       mPlaybackHead = 0;

    /**
     * Legacy constructor (API level 1).
     *
     * @param streamType    audio stream type (e.g. {@link android.media.AudioManager#STREAM_MUSIC})
     * @param sampleRate    output sample rate in Hz
     * @param channelConfig channel configuration mask
     * @param audioFormat   PCM encoding constant
     * @param bufferSizeInBytes size of the audio data buffer
     * @param mode          {@link #MODE_STATIC} or {@link #MODE_STREAM}
     */
    public AudioTrack(int streamType, int sampleRate, int channelConfig,
                      int audioFormat, int bufferSizeInBytes, int mode) {
        mStreamType       = streamType;
        mSampleRate       = sampleRate;
        mChannelConfig    = channelConfig;
        mAudioFormat      = audioFormat;
        mBufferSizeInBytes = bufferSizeInBytes;
        mMode             = mode;
    }

    /**
     * Returns the minimum buffer size required for the given parameters,
     * or {@link #ERROR_BAD_VALUE} if the parameters are not supported.
     */
    public static int getMinBufferSize(int sampleRate, int channelConfig, int audioFormat) {
        if (sampleRate <= 0) return ERROR_BAD_VALUE;
        int bytesPerSample;
        switch (audioFormat) {
            case AudioFormat.ENCODING_PCM_8BIT:  bytesPerSample = 1; break;
            case AudioFormat.ENCODING_PCM_16BIT: bytesPerSample = 2; break;
            case AudioFormat.ENCODING_PCM_FLOAT: bytesPerSample = 4; break;
            default: return ERROR_BAD_VALUE;
        }
        int channels = (channelConfig == AudioFormat.CHANNEL_OUT_STEREO) ? 2 : 1;
        return (sampleRate / 50) * channels * bytesPerSample;
    }

    public void play() {
        checkNotReleased();
        mPlayState = PLAYSTATE_PLAYING;
        System.out.println("[AudioTrack] play");
    }

    public void pause() {
        checkNotReleased();
        mPlayState = PLAYSTATE_PAUSED;
        System.out.println("[AudioTrack] pause");
    }

    public void stop() {
        checkNotReleased();
        mPlayState = PLAYSTATE_STOPPED;
        System.out.println("[AudioTrack] stop");
    }

    public void release() {
        mReleased  = true;
        mPlayState = PLAYSTATE_STOPPED;
        System.out.println("[AudioTrack] release");
    }

    /**
     * Writes PCM audio data for playback.
     *
     * @return number of bytes written, or a negative error code
     */
    public int write(byte[] audioData, int offsetInBytes, int sizeInBytes) {
        if (mReleased) return ERROR_INVALID_OPERATION;
        if (audioData == null || offsetInBytes < 0 || sizeInBytes < 0
                || offsetInBytes + sizeInBytes > audioData.length) return ERROR_BAD_VALUE;
        mPlaybackHead += sizeInBytes / 2; // approximate frame count
        return sizeInBytes;
    }

    /**
     * Writes PCM audio data (short array).
     */
    public int write(short[] audioData, int offsetInShorts, int sizeInShorts) {
        if (mReleased) return ERROR_INVALID_OPERATION;
        if (audioData == null || offsetInShorts < 0 || sizeInShorts < 0
                || offsetInShorts + sizeInShorts > audioData.length) return ERROR_BAD_VALUE;
        mPlaybackHead += sizeInShorts;
        return sizeInShorts;
    }

    /**
     * Writes PCM audio data (float array).
     */
    public int write(float[] audioData, int offsetInFloats, int sizeInFloats, int writeMode) {
        if (mReleased) return ERROR_INVALID_OPERATION;
        if (audioData == null || offsetInFloats < 0 || sizeInFloats < 0
                || offsetInFloats + sizeInFloats > audioData.length) return ERROR_BAD_VALUE;
        mPlaybackHead += sizeInFloats;
        return sizeInFloats;
    }

    /**
     * Sets the left and right volume levels for this track, in the range [0.0, 1.0].
     *
     * @return {@link #SUCCESS} or an error code
     */
    public int setStereoVolume(float leftVolume, float rightVolume) {
        if (leftVolume < 0 || leftVolume > 1 || rightVolume < 0 || rightVolume > 1) {
            return ERROR_BAD_VALUE;
        }
        mLeftVolume  = leftVolume;
        mRightVolume = rightVolume;
        return SUCCESS;
    }

    /** Returns the playback head position in frames. */
    public int getPlaybackHeadPosition() {
        return mPlaybackHead;
    }

    public int getStreamType()        { return mStreamType;        }
    public int getSampleRate()        { return mSampleRate;        }
    public int getChannelConfiguration() { return mChannelConfig;  }
    public int getAudioFormat()       { return mAudioFormat;       }
    public int getBufferSizeInBytes() { return mBufferSizeInBytes; }
    public int getMode()              { return mMode;              }
    public int getPlayState()         { return mPlayState;         }
    public float getLeftVolume()      { return mLeftVolume;        }
    public float getRightVolume()     { return mRightVolume;       }

    private void checkNotReleased() {
        if (mReleased) throw new IllegalStateException("AudioTrack already released");
    }
}
