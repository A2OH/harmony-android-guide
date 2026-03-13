package android.media.audiofx;

/**
 * Android-compatible Visualizer shim. Stub for audio visualizer capture.
 */
public class Visualizer {
    public static final int SUCCESS              =  0;
    public static final int ERROR                = -1;
    public static final int ALREADY_EXISTS       = -2;
    public static final int ERROR_NO_MEMORY      = -12;
    public static final int ERROR_BAD_VALUE      = -4;
    public static final int ERROR_INVALID_OPERATION = -5;
    public static final int ERROR_DEAD_OBJECT    = -7;

    public static final int SCALING_MODE_NORMALIZED = 0;
    public static final int SCALING_MODE_AS_PLAYED  = 1;

    public static final int MEASUREMENT_MODE_NONE   = 0;
    public static final int MEASUREMENT_MODE_PEAK_RMS = 1;

    private static final int DEFAULT_CAPTURE_SIZE = 1024;
    private static final int[] CAPTURE_SIZE_RANGE = { 128, 32768 };

    private final int mAudioSessionId;
    private int       mCaptureSize;
    private boolean   mEnabled;
    private boolean   mReleased;
    private OnDataCaptureListener mListener;
    private int       mRateHz;

    public Visualizer(int audioSessionId) {
        mAudioSessionId = audioSessionId;
        mCaptureSize    = DEFAULT_CAPTURE_SIZE;
        mEnabled        = false;
        mReleased       = false;
    }

    public int setEnabled(boolean enabled) {
        if (mReleased) return ERROR_INVALID_OPERATION;
        mEnabled = enabled;
        return SUCCESS;
    }

    public boolean getEnabled() {
        return mEnabled && !mReleased;
    }

    public int setCaptureSize(int size) {
        if (mReleased || mEnabled) return ERROR_INVALID_OPERATION;
        if (size < CAPTURE_SIZE_RANGE[0] || size > CAPTURE_SIZE_RANGE[1]) return ERROR_BAD_VALUE;
        mCaptureSize = size;
        return SUCCESS;
    }

    public int getCaptureSize() {
        return mCaptureSize;
    }

    public int[] getCaptureSizeRange() {
        return new int[]{ CAPTURE_SIZE_RANGE[0], CAPTURE_SIZE_RANGE[1] };
    }

    public int getSamplingRate() {
        // stub: return a common sampling rate in mHz (44100 Hz)
        return 44100000;
    }

    public int getWaveForm(byte[] waveform) {
        if (mReleased || !mEnabled) return ERROR_INVALID_OPERATION;
        if (waveform == null || waveform.length < mCaptureSize) return ERROR_BAD_VALUE;
        // stub: fill with silence (128 = 0 in unsigned 8-bit PCM)
        for (int i = 0; i < mCaptureSize; i++) { waveform[i] = (byte) 0x80; }
        return SUCCESS;
    }

    public int getFft(byte[] fft) {
        if (mReleased || !mEnabled) return ERROR_INVALID_OPERATION;
        if (fft == null || fft.length < mCaptureSize) return ERROR_BAD_VALUE;
        // stub: silence spectrum
        for (int i = 0; i < mCaptureSize; i++) { fft[i] = 0; }
        return SUCCESS;
    }

    public int setDataCaptureListener(OnDataCaptureListener listener,
            int rate, boolean waveform, boolean fft) {
        if (mReleased) return ERROR_INVALID_OPERATION;
        mListener = listener;
        mRateHz   = rate;
        return SUCCESS;
    }

    public void release() {
        mReleased = true;
        mEnabled  = false;
        mListener = null;
    }

    public int getAudioSessionId() { return mAudioSessionId; }

    @Override
    public String toString() {
        return "Visualizer{session=" + mAudioSessionId + ", size=" + mCaptureSize
                + ", enabled=" + mEnabled + "}";
    }

    // -----------------------------------------------------------------------
    public interface OnDataCaptureListener {
        void onWaveFormDataCapture(Visualizer visualizer, byte[] waveform, int samplingRate);
        void onFftDataCapture(Visualizer visualizer, byte[] fft, int samplingRate);
    }
}
