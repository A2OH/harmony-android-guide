package android.media;

/**
 * Android-compatible AudioRecord shim. Stub for PCM audio capture.
 */
public class AudioRecord {

    public static final int SUCCESS          =  0;
    public static final int ERROR            = -1;
    public static final int ERROR_BAD_VALUE  = -2;
    public static final int ERROR_INVALID_OPERATION = -3;
    public static final int ERROR_DEAD_OBJECT = -6;

    public static final int RECORDSTATE_STOPPED  = 1;
    public static final int RECORDSTATE_RECORDING = 3;

    public static final int READ_BLOCKING     = 0;
    public static final int READ_NON_BLOCKING = 1;

    private final int mAudioSource;
    private final int mSampleRate;
    private final int mChannelConfig;
    private final int mAudioFormat;
    private final int mBufferSize;
    private int       mRecordingState = RECORDSTATE_STOPPED;
    private boolean   mReleased       = false;

    /**
     * Minimal constructor matching Android's primary constructor.
     *
     * @param audioSource  one of {@link MediaRecorder.AudioSource} constants
     * @param sampleRate   sample rate in Hz
     * @param channelConfig {@link AudioFormat#CHANNEL_IN_MONO} or {@link AudioFormat#CHANNEL_IN_STEREO}
     * @param audioFormat  {@link AudioFormat#ENCODING_PCM_16BIT}, etc.
     * @param bufferSize   size of the internal PCM buffer in bytes; use {@link #getMinBufferSize}
     */
    public AudioRecord(int audioSource, int sampleRate, int channelConfig,
                       int audioFormat, int bufferSize) {
        mAudioSource  = audioSource;
        mSampleRate   = sampleRate;
        mChannelConfig = channelConfig;
        mAudioFormat  = audioFormat;
        mBufferSize   = bufferSize;
    }

    /**
     * Returns the minimum buffer size required for the given parameters.
     * Returns {@link #ERROR_BAD_VALUE} if parameters are unsupported.
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
        int channels = (channelConfig == AudioFormat.CHANNEL_IN_STEREO) ? 2 : 1;
        // 20 ms frame
        return (sampleRate / 50) * channels * bytesPerSample;
    }

    public void startRecording() {
        checkNotReleased();
        mRecordingState = RECORDSTATE_RECORDING;
        System.out.println("[AudioRecord] startRecording");
    }

    public void stop() {
        mRecordingState = RECORDSTATE_STOPPED;
        System.out.println("[AudioRecord] stop");
    }

    public void release() {
        mReleased = true;
        System.out.println("[AudioRecord] release");
    }

    /**
     * Reads audio data into the given byte array.
     *
     * @param audioData destination buffer
     * @param offsetInBytes offset within the buffer
     * @param sizeInBytes   number of bytes to read
     * @return number of bytes read, or a negative error code
     */
    public int read(byte[] audioData, int offsetInBytes, int sizeInBytes) {
        if (mReleased || mRecordingState != RECORDSTATE_RECORDING) return ERROR_INVALID_OPERATION;
        if (audioData == null || offsetInBytes < 0 || sizeInBytes <= 0
                || offsetInBytes + sizeInBytes > audioData.length) return ERROR_BAD_VALUE;
        // Return silence
        java.util.Arrays.fill(audioData, offsetInBytes, offsetInBytes + sizeInBytes, (byte) 0);
        return sizeInBytes;
    }

    /**
     * Reads audio data into the given short array.
     */
    public int read(short[] audioData, int offsetInShorts, int sizeInShorts) {
        if (mReleased || mRecordingState != RECORDSTATE_RECORDING) return ERROR_INVALID_OPERATION;
        if (audioData == null || offsetInShorts < 0 || sizeInShorts <= 0
                || offsetInShorts + sizeInShorts > audioData.length) return ERROR_BAD_VALUE;
        java.util.Arrays.fill(audioData, offsetInShorts, offsetInShorts + sizeInShorts, (short) 0);
        return sizeInShorts;
    }

    /**
     * Reads audio data into a {@link java.nio.ByteBuffer}.
     */
    public int read(java.nio.ByteBuffer audioBuffer, int sizeInBytes) {
        if (mReleased || mRecordingState != RECORDSTATE_RECORDING) return ERROR_INVALID_OPERATION;
        if (audioBuffer == null || sizeInBytes <= 0) return ERROR_BAD_VALUE;
        int toRead = Math.min(sizeInBytes, audioBuffer.remaining());
        for (int i = 0; i < toRead; i++) audioBuffer.put((byte) 0);
        return toRead;
    }

    public int getAudioSource()   { return mAudioSource;   }
    public int getSampleRate()    { return mSampleRate;     }
    public int getChannelConfig() { return mChannelConfig;  }
    public int getAudioFormat()   { return mAudioFormat;    }
    public int getBufferSize()    { return mBufferSize;     }
    public int getRecordingState() { return mRecordingState; }

    private void checkNotReleased() {
        if (mReleased) throw new IllegalStateException("AudioRecord already released");
    }
}
