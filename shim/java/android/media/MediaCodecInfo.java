package android.media;

/**
 * Android-compatible MediaCodecInfo shim. Stub for codec information.
 */
public class MediaCodecInfo {

    private final String mName;

    public MediaCodecInfo() {
        mName = "";
    }

    public String getName() { return mName; }

    public boolean isEncoder() { return false; }

    public String[] getSupportedTypes() { return new String[0]; }

    public CodecCapabilities getCapabilitiesForType(String type) {
        return new CodecCapabilities();
    }

    /**
     * Describes the capabilities of a codec for a given media type.
     */
    public static class CodecCapabilities {
        public static final int COLOR_FormatYUV420Flexible = 0x7F420888;
        public static final int COLOR_FormatSurface = 0x7F000789;

        public int[] colorFormats = new int[0];

        public boolean isFormatSupported(Object format) { return false; }

        public String getMimeType() { return ""; }

        public Object getDefaultFormat() { return null; }

        public Object getVideoCapabilities() { return null; }

        public Object getAudioCapabilities() { return null; }

        public Object getEncoderCapabilities() { return null; }
    }
}
