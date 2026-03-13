package android.media;

/**
 * Android-compatible MediaCodecList shim. Stub for enumerating available codecs.
 */
public class MediaCodecList {

    public static final int ALL_CODECS = 1;
    public static final int REGULAR_CODECS = 0;

    private final int mKind;

    public MediaCodecList(int kind) {
        mKind = kind;
    }

    @SuppressWarnings("deprecation")
    public static int getCodecCount() { return 0; }

    @SuppressWarnings("deprecation")
    public static MediaCodecInfo getCodecInfoAt(int index) { return new MediaCodecInfo(); }

    public MediaCodecInfo[] getCodecInfos() { return new MediaCodecInfo[0]; }

    public String findEncoderForFormat(Object format) { return null; }

    public String findDecoderForFormat(Object format) { return null; }
}
