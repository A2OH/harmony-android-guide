package android.media;

import java.util.HashMap;
import java.util.Map;

/**
 * Android-compatible CamcorderProfile shim. Stub with common quality profiles.
 */
public final class CamcorderProfile {
    public static final int QUALITY_LOW    = 0;
    public static final int QUALITY_HIGH   = 1;
    public static final int QUALITY_QCIF   = 2;
    public static final int QUALITY_CIF    = 3;
    public static final int QUALITY_480P   = 4;
    public static final int QUALITY_720P   = 5;
    public static final int QUALITY_1080P  = 6;
    public static final int QUALITY_2160P  = 8;

    public static final int QUALITY_TIME_LAPSE_LOW   = 1000;
    public static final int QUALITY_TIME_LAPSE_HIGH  = 1001;
    public static final int QUALITY_TIME_LAPSE_QCIF  = 1002;
    public static final int QUALITY_TIME_LAPSE_CIF   = 1003;
    public static final int QUALITY_TIME_LAPSE_480P  = 1004;
    public static final int QUALITY_TIME_LAPSE_720P  = 1005;
    public static final int QUALITY_TIME_LAPSE_1080P = 1006;

    public static final int VIDEO_CODEC_H264 = 2;
    public static final int VIDEO_CODEC_H263 = 1;
    public static final int VIDEO_CODEC_MPEG4 = 3;
    public static final int VIDEO_CODEC_HEVC  = 11;

    public static final int AUDIO_CODEC_AAC  = 3;
    public static final int AUDIO_CODEC_AMR_NB = 1;
    public static final int AUDIO_CODEC_AMR_WB = 2;

    public static final int OUTPUT_FORMAT_MPEG4 = 2;
    public static final int OUTPUT_FORMAT_3GPP  = 1;

    // -----------------------------------------------------------------------
    public int duration;        // seconds (0 = unlimited)
    public int quality;
    public int fileFormat;
    public int videoCodec;
    public int videoBitRate;
    public int videoFrameRate;
    public int videoFrameWidth;
    public int videoFrameHeight;
    public int audioBitRate;
    public int audioSampleRate;
    public int audioChannels;
    public int audioCodec;

    private CamcorderProfile() {}

    // -----------------------------------------------------------------------
    private static final Map<Integer, CamcorderProfile> sProfiles = new HashMap<>();

    static {
        // QUALITY_LOW  → QCIF  176x144
        sProfiles.put(QUALITY_LOW,   make(QUALITY_LOW,   176,  144,   384000, 15,  64000, 8000));
        // QUALITY_HIGH → 1080p
        sProfiles.put(QUALITY_HIGH,  make(QUALITY_HIGH, 1920, 1080, 17000000, 30, 128000, 48000));
        // QUALITY_QCIF → 176x144
        sProfiles.put(QUALITY_QCIF,  make(QUALITY_QCIF,  176,  144,   384000, 15,  64000, 8000));
        // QUALITY_CIF  → 352x288
        sProfiles.put(QUALITY_CIF,   make(QUALITY_CIF,   352,  288,  1536000, 24,  96000, 44100));
        // QUALITY_480P → 720x480
        sProfiles.put(QUALITY_480P,  make(QUALITY_480P,  720,  480,  5000000, 30,  96000, 44100));
        // QUALITY_720P → 1280x720
        sProfiles.put(QUALITY_720P,  make(QUALITY_720P, 1280,  720,  8000000, 30, 128000, 48000));
        // QUALITY_1080P → 1920x1080
        sProfiles.put(QUALITY_1080P, make(QUALITY_1080P,1920, 1080, 17000000, 30, 128000, 48000));
        // QUALITY_2160P → 3840x2160
        sProfiles.put(QUALITY_2160P, make(QUALITY_2160P,3840, 2160, 50000000, 30, 256000, 48000));
    }

    private static CamcorderProfile make(int quality,
            int w, int h, int videoBitRate, int fps, int audioBitRate, int audioSampleRate) {
        CamcorderProfile p = new CamcorderProfile();
        p.duration        = 0;
        p.quality         = quality;
        p.fileFormat      = OUTPUT_FORMAT_MPEG4;
        p.videoCodec      = VIDEO_CODEC_H264;
        p.videoBitRate    = videoBitRate;
        p.videoFrameRate  = fps;
        p.videoFrameWidth = w;
        p.videoFrameHeight = h;
        p.audioBitRate    = audioBitRate;
        p.audioSampleRate = audioSampleRate;
        p.audioChannels   = 2;
        p.audioCodec      = AUDIO_CODEC_AAC;
        return p;
    }

    // -----------------------------------------------------------------------
    public static CamcorderProfile get(int quality) {
        CamcorderProfile p = sProfiles.get(quality);
        if (p == null) p = sProfiles.get(QUALITY_HIGH);
        return p;
    }

    public static CamcorderProfile get(int cameraId, int quality) {
        // stub: ignore cameraId
        return get(quality);
    }

    public static boolean hasProfile(int quality) {
        return sProfiles.containsKey(quality);
    }

    public static boolean hasProfile(int cameraId, int quality) {
        return hasProfile(quality);
    }

    @Override
    public String toString() {
        return "CamcorderProfile{quality=" + quality
                + ", " + videoFrameWidth + "x" + videoFrameHeight
                + "@" + videoFrameRate + "fps, videoBitRate=" + videoBitRate + "}";
    }
}
