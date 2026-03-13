package android.media;
import android.icu.util.Output;
import android.icu.util.Output;
import java.io.IOException;

/**
 * Android-compatible MediaRecorder shim. Stub for audio/video recording.
 */
public class MediaRecorder {

    /** Audio source constants. */
    public static final class AudioSource {
        public static final int DEFAULT    = 0;
        public static final int MIC        = 1;
        public static final int VOICE_UPLINK   = 2;
        public static final int VOICE_DOWNLINK = 3;
        public static final int VOICE_CALL     = 4;
        public static final int CAMCORDER      = 5;
        public static final int VOICE_RECOGNITION = 6;
        public static final int VOICE_COMMUNICATION = 7;
        public static final int REMOTE_SUBMIX  = 8;
        public static final int UNPROCESSED    = 9;
        private AudioSource() {}
    }

    /** Video source constants. */
    public static final class VideoSource {
        public static final int DEFAULT = 0;
        public static final int CAMERA  = 1;
        public static final int SURFACE = 2;
        private VideoSource() {}
    }

    /** Output format constants. */
    public static final class OutputFormat {
        public static final int DEFAULT       = 0;
        public static final int THREE_GPP     = 1;
        public static final int MPEG_4        = 2;
        public static final int AMR_NB        = 3;
        public static final int AMR_WB        = 4;
        public static final int AAC_ADTS      = 6;
        public static final int WEBM          = 9;
        public static final int OGG           = 11;
        private OutputFormat() {}
    }

    /** Audio encoder constants. */
    public static final class AudioEncoder {
        public static final int DEFAULT  = 0;
        public static final int AMR_NB   = 1;
        public static final int AMR_WB   = 2;
        public static final int AAC      = 3;
        public static final int HE_AAC   = 4;
        public static final int AAC_ELD  = 5;
        public static final int VORBIS   = 6;
        public static final int OPUS     = 7;
        private AudioEncoder() {}
    }

    /** Video encoder constants. */
    public static final class VideoEncoder {
        public static final int DEFAULT   = 0;
        public static final int H263      = 1;
        public static final int H264      = 2;
        public static final int MPEG_4_SP = 3;
        public static final int VP8       = 4;
        public static final int HEVC      = 5;
        private VideoEncoder() {}
    }

    private int    mAudioSource   = AudioSource.DEFAULT;
    private int    mVideoSource   = VideoSource.DEFAULT;
    private int    mOutputFormat  = OutputFormat.DEFAULT;
    private int    mAudioEncoder  = AudioEncoder.DEFAULT;
    private int    mVideoEncoder  = VideoEncoder.DEFAULT;
    private String mOutputFile    = null;
    private boolean mReleased     = false;

    public MediaRecorder() {}

    public void setAudioSource(int audioSource) {
        mAudioSource = audioSource;
    }

    public void setVideoSource(int videoSource) {
        mVideoSource = videoSource;
    }

    public void setOutputFormat(int outputFormat) {
        mOutputFormat = outputFormat;
    }

    public void setAudioEncoder(int audioEncoder) {
        mAudioEncoder = audioEncoder;
    }

    public void setVideoEncoder(int videoEncoder) {
        mVideoEncoder = videoEncoder;
    }

    public void setOutputFile(String path) {
        mOutputFile = path;
    }

    public void prepare() throws java.io.IOException {
        if (mOutputFile == null) {
            throw new java.io.IOException("Output file not set");
        }
        System.out.println("[MediaRecorder] prepare: " + mOutputFile);
    }

    public void start() {
        System.out.println("[MediaRecorder] start");
    }

    public void stop() {
        System.out.println("[MediaRecorder] stop");
    }

    public void reset() {
        mAudioSource  = AudioSource.DEFAULT;
        mVideoSource  = VideoSource.DEFAULT;
        mOutputFormat = OutputFormat.DEFAULT;
        mAudioEncoder = AudioEncoder.DEFAULT;
        mVideoEncoder = VideoEncoder.DEFAULT;
        mOutputFile   = null;
        System.out.println("[MediaRecorder] reset");
    }

    public void release() {
        mReleased = true;
        System.out.println("[MediaRecorder] release");
    }

    public int getAudioSource()  { return mAudioSource;  }
    public int getVideoSource()  { return mVideoSource;  }
    public int getOutputFormat() { return mOutputFormat; }
    public int getAudioEncoder() { return mAudioEncoder; }
    public int getVideoEncoder() { return mVideoEncoder; }
    public String getOutputFile() { return mOutputFile;  }
}
