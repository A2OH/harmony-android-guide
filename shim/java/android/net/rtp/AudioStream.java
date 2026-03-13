package android.net.rtp;

import java.net.InetAddress;

/**
 * Android-compatible AudioStream shim for A2OH migration.
 * Extends RtpStream to add codec and AudioGroup management.
 * All audio I/O operations are no-ops.
 */
public class AudioStream extends RtpStream {

    // -----------------------------------------------------------------------
    // Inner class: AudioCodec

    public static final class AudioCodec {
        public static final AudioCodec PCMU = new AudioCodec(0,  "PCMU", 8000, 1);
        public static final AudioCodec PCMA = new AudioCodec(8,  "PCMA", 8000, 1);
        public static final AudioCodec GSM  = new AudioCodec(3,  "GSM",  8000, 1);
        public static final AudioCodec AMR  = new AudioCodec(99, "AMR",  8000, 1);

        public final int    type;
        public final String rtpmap;
        public final int    sampleRate;
        public final int    channelCount;

        private AudioCodec(int type, String rtpmap, int sampleRate, int channelCount) {
            this.type         = type;
            this.rtpmap       = rtpmap;
            this.sampleRate   = sampleRate;
            this.channelCount = channelCount;
        }

        @Override
        public String toString() {
            return rtpmap;
        }
    }

    // -----------------------------------------------------------------------

    private AudioGroup mGroup  = null;
    private AudioCodec mCodec  = null;

    public AudioStream(InetAddress address) {
        super(address);
    }

    /**
     * Joins this stream to an AudioGroup, leaving any previous group first.
     * Pass null to detach from the current group.
     */
    public void join(AudioGroup group) {
        if (mGroup != null) {
            mGroup.removeStream(this);
        }
        mGroup = group;
        if (mGroup != null) {
            mGroup.addStream(this);
        }
    }

    /** Returns the AudioGroup this stream belongs to, or null. */
    public AudioGroup getGroup() {
        return mGroup;
    }

    public AudioCodec getCodec() {
        return mCodec;
    }

    public void setCodec(AudioCodec codec) {
        mCodec = codec;
    }
}
