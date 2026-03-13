package android.net.rtp;

public class AudioCodec {
    public AudioCodec(Object... args) {}
    public AudioCodec(int type, String rtpmap, String fmtp) {}
    public static final int AMR = 0;
    public static final int GSM = 0;
    public static final int GSM_EFR = 0;
    public static final int PCMA = 0;
    public static final int PCMU = 0;
    public int fmtp = 0;
    public int rtpmap = 0;
    public int type = 0;

    public AudioCodec() {}

    public static AudioCodec getCodec(int p0, String p1, String p2) { return null; }
    public static AudioCodec[] getCodecs() { return null; }
}
