package android.media;

public final class MediaParser {
    public static final int PARAMETER_ADTS_ENABLE_CBR_SEEKING = 0;
    public static final int PARAMETER_AMR_ENABLE_CBR_SEEKING = 0;
    public static final int PARAMETER_FLAC_DISABLE_ID3 = 0;
    public static final int PARAMETER_MATROSKA_DISABLE_CUES_SEEKING = 0;
    public static final int PARAMETER_MP3_DISABLE_ID3 = 0;
    public static final int PARAMETER_MP3_ENABLE_CBR_SEEKING = 0;
    public static final int PARAMETER_MP3_ENABLE_INDEX_SEEKING = 0;
    public static final int PARAMETER_MP4_IGNORE_EDIT_LISTS = 0;
    public static final int PARAMETER_MP4_IGNORE_TFDT_BOX = 0;
    public static final int PARAMETER_MP4_TREAT_VIDEO_FRAMES_AS_KEYFRAMES = 0;
    public static final int PARAMETER_TS_ALLOW_NON_IDR_AVC_KEYFRAMES = 0;
    public static final int PARAMETER_TS_DETECT_ACCESS_UNITS = 0;
    public static final int PARAMETER_TS_ENABLE_HDMV_DTS_AUDIO_STREAMS = 0;
    public static final int PARAMETER_TS_IGNORE_AAC_STREAM = 0;
    public static final int PARAMETER_TS_IGNORE_AVC_STREAM = 0;
    public static final int PARAMETER_TS_IGNORE_SPLICE_INFO_STREAM = 0;
    public static final int PARAMETER_TS_MODE = 0;
    public static final int PARSER_NAME_AC3 = 0;
    public static final int PARSER_NAME_AC4 = 0;
    public static final int PARSER_NAME_ADTS = 0;
    public static final int PARSER_NAME_AMR = 0;
    public static final int PARSER_NAME_FLAC = 0;
    public static final int PARSER_NAME_FLV = 0;
    public static final int PARSER_NAME_FMP4 = 0;
    public static final int PARSER_NAME_MATROSKA = 0;
    public static final int PARSER_NAME_MP3 = 0;
    public static final int PARSER_NAME_MP4 = 0;
    public static final int PARSER_NAME_OGG = 0;
    public static final int PARSER_NAME_PS = 0;
    public static final int PARSER_NAME_TS = 0;
    public static final int PARSER_NAME_UNKNOWN = 0;
    public static final int PARSER_NAME_WAV = 0;
    public static final int SAMPLE_FLAG_DECODE_ONLY = 0;
    public static final int SAMPLE_FLAG_ENCRYPTED = 0;
    public static final int SAMPLE_FLAG_HAS_SUPPLEMENTAL_DATA = 0;
    public static final int SAMPLE_FLAG_KEY_FRAME = 0;
    public static final int SAMPLE_FLAG_LAST_SAMPLE = 0;

    public MediaParser() {}

    public boolean advance(Object p0) { return false; }
    public void release() {}
    public void seek(Object p0) {}
    public boolean supportsParameter(String p0) { return false; }
}
