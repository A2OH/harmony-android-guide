package android.media;

/**
 * Android-compatible AudioAttributes shim. Describes the intended use of audio streams.
 */
public class AudioAttributes {

    // Content type constants
    public static final int CONTENT_TYPE_UNKNOWN      = 0;
    public static final int CONTENT_TYPE_SPEECH       = 1;
    public static final int CONTENT_TYPE_MUSIC        = 2;
    public static final int CONTENT_TYPE_MOVIE        = 3;
    public static final int CONTENT_TYPE_SONIFICATION = 4;

    // Usage constants
    public static final int USAGE_UNKNOWN                         = 0;
    public static final int USAGE_MEDIA                           = 1;
    public static final int USAGE_VOICE_COMMUNICATION             = 2;
    public static final int USAGE_VOICE_COMMUNICATION_SIGNALLING  = 3;
    public static final int USAGE_ALARM                           = 4;
    public static final int USAGE_NOTIFICATION                    = 5;
    public static final int USAGE_NOTIFICATION_RINGTONE           = 6;
    public static final int USAGE_NOTIFICATION_COMMUNICATION_REQUEST = 7;
    public static final int USAGE_NOTIFICATION_COMMUNICATION_INSTANT = 8;
    public static final int USAGE_NOTIFICATION_COMMUNICATION_DELAYED = 9;
    public static final int USAGE_NOTIFICATION_EVENT              = 10;
    public static final int USAGE_ASSISTANCE_ACCESSIBILITY        = 11;
    public static final int USAGE_ASSISTANCE_NAVIGATION_GUIDANCE  = 12;
    public static final int USAGE_ASSISTANCE_SONIFICATION         = 13;
    public static final int USAGE_GAME                            = 14;
    public static final int USAGE_ASSISTANT                       = 16;

    // Flag constants
    public static final int FLAG_AUDIBILITY_ENFORCED = 0x1;
    public static final int FLAG_HW_AV_SYNC          = 0x10;
    public static final int FLAG_LOW_LATENCY          = 0x100;

    private final int mContentType;
    private final int mUsage;
    private final int mFlags;

    private AudioAttributes(int contentType, int usage, int flags) {
        mContentType = contentType;
        mUsage       = usage;
        mFlags       = flags;
    }

    public int getContentType() { return mContentType; }
    public int getUsage()       { return mUsage;       }
    public int getFlags()       { return mFlags;       }

    @Override
    public String toString() {
        return "AudioAttributes{contentType=" + mContentType
                + ", usage=" + mUsage + ", flags=" + mFlags + "}";
    }

    /** Builder for AudioAttributes. */
    public static class Builder {
        private int mContentType = CONTENT_TYPE_UNKNOWN;
        private int mUsage       = USAGE_UNKNOWN;
        private int mFlags       = 0;

        public Builder() {}

        /** Copy constructor. */
        public Builder(AudioAttributes aa) {
            mContentType = aa.mContentType;
            mUsage       = aa.mUsage;
            mFlags       = aa.mFlags;
        }

        public Builder setContentType(int contentType) {
            mContentType = contentType;
            return this;
        }

        public Builder setUsage(int usage) {
            mUsage = usage;
            return this;
        }

        public Builder setFlags(int flags) {
            mFlags = flags;
            return this;
        }

        public AudioAttributes build() {
            return new AudioAttributes(mContentType, mUsage, mFlags);
        }
    }
}
