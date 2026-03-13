package android.media;

/**
 * Android-compatible AudioFormat shim. Defines PCM encoding and channel mask constants.
 */
public class AudioFormat {

    public static final int ENCODING_INVALID      = 0;
    public static final int ENCODING_DEFAULT       = 1;
    public static final int ENCODING_PCM_16BIT     = 2;
    public static final int ENCODING_PCM_8BIT      = 3;
    public static final int ENCODING_PCM_FLOAT     = 4;
    public static final int ENCODING_AC3           = 5;
    public static final int ENCODING_E_AC3         = 6;
    public static final int ENCODING_DTS           = 7;
    public static final int ENCODING_DTS_HD        = 8;
    public static final int ENCODING_MP3           = 9;
    public static final int ENCODING_AAC_LC        = 10;
    public static final int ENCODING_AAC_HE_V1     = 11;
    public static final int ENCODING_AAC_HE_V2     = 12;
    public static final int ENCODING_IEC61937      = 13;
    public static final int ENCODING_DOLBY_TRUEHD  = 14;
    public static final int ENCODING_AAC_ELD       = 15;
    public static final int ENCODING_AAC_XHE       = 16;
    public static final int ENCODING_AC4            = 17;
    public static final int ENCODING_E_AC3_JOC     = 18;
    public static final int ENCODING_DOLBY_MAT     = 19;
    public static final int ENCODING_OPUS           = 20;
    public static final int ENCODING_PCM_24BIT_PACKED = 21;
    public static final int ENCODING_PCM_32BIT      = 22;

    public static final int CHANNEL_INVALID              = 0;
    public static final int CHANNEL_IN_DEFAULT           = 1;
    public static final int CHANNEL_IN_LEFT              = 0x4;
    public static final int CHANNEL_IN_RIGHT             = 0x8;
    public static final int CHANNEL_IN_FRONT             = 0x10;
    public static final int CHANNEL_IN_BACK              = 0x20;
    public static final int CHANNEL_IN_MONO              = CHANNEL_IN_FRONT;
    public static final int CHANNEL_IN_STEREO            = CHANNEL_IN_LEFT | CHANNEL_IN_RIGHT;

    public static final int CHANNEL_OUT_FRONT_LEFT       = 0x4;
    public static final int CHANNEL_OUT_FRONT_RIGHT      = 0x8;
    public static final int CHANNEL_OUT_FRONT_CENTER     = 0x10;
    public static final int CHANNEL_OUT_LOW_FREQUENCY    = 0x20;
    public static final int CHANNEL_OUT_BACK_LEFT        = 0x40;
    public static final int CHANNEL_OUT_BACK_RIGHT       = 0x80;
    public static final int CHANNEL_OUT_MONO             = CHANNEL_OUT_FRONT_LEFT;
    public static final int CHANNEL_OUT_STEREO           = CHANNEL_OUT_FRONT_LEFT | CHANNEL_OUT_FRONT_RIGHT;
    public static final int CHANNEL_OUT_QUAD             = CHANNEL_OUT_STEREO | CHANNEL_OUT_BACK_LEFT | CHANNEL_OUT_BACK_RIGHT;
    public static final int CHANNEL_OUT_SURROUND         = CHANNEL_OUT_STEREO | CHANNEL_OUT_FRONT_CENTER | CHANNEL_OUT_BACK_LEFT;
    public static final int CHANNEL_OUT_5POINT1          = CHANNEL_OUT_QUAD  | CHANNEL_OUT_FRONT_CENTER | CHANNEL_OUT_LOW_FREQUENCY;

    private int mEncoding     = ENCODING_INVALID;
    private int mSampleRate   = 0;
    private int mChannelMask  = CHANNEL_INVALID;

    // package-private constructor used by Builder
    AudioFormat(int encoding, int sampleRate, int channelMask) {
        mEncoding    = encoding;
        mSampleRate  = sampleRate;
        mChannelMask = channelMask;
    }

    public int getEncoding()    { return mEncoding;    }
    public int getSampleRate()  { return mSampleRate;  }
    public int getChannelMask() { return mChannelMask; }

    @Override
    public String toString() {
        return "AudioFormat{encoding=" + mEncoding + ", sampleRate=" + mSampleRate
                + ", channelMask=" + mChannelMask + "}";
    }

    /** Builder for AudioFormat. */
    public static class Builder {
        private int mEncoding    = ENCODING_DEFAULT;
        private int mSampleRate  = 44100;
        private int mChannelMask = CHANNEL_OUT_STEREO;

        public Builder() {}

        public Builder setEncoding(int encoding) {
            mEncoding = encoding;
            return this;
        }

        public Builder setSampleRate(int sampleRate) {
            mSampleRate = sampleRate;
            return this;
        }

        public Builder setChannelMask(int channelMask) {
            mChannelMask = channelMask;
            return this;
        }

        public AudioFormat build() {
            return new AudioFormat(mEncoding, mSampleRate, mChannelMask);
        }
    }
}
