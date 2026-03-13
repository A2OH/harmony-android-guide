package android.media;
import android.content.Context;
import android.os.Handler;
import android.se.omapi.Session;

public final class MediaCas implements AutoCloseable {
    public static final int PLUGIN_STATUS_PHYSICAL_MODULE_CHANGED = 0;
    public static final int PLUGIN_STATUS_SESSION_NUMBER_CHANGED = 0;
    public static final int SCRAMBLING_MODE_AES128 = 0;
    public static final int SCRAMBLING_MODE_AES_ECB = 0;
    public static final int SCRAMBLING_MODE_AES_SCTE52 = 0;
    public static final int SCRAMBLING_MODE_DVB_CISSA_V1 = 0;
    public static final int SCRAMBLING_MODE_DVB_CSA1 = 0;
    public static final int SCRAMBLING_MODE_DVB_CSA2 = 0;
    public static final int SCRAMBLING_MODE_DVB_CSA3_ENHANCE = 0;
    public static final int SCRAMBLING_MODE_DVB_CSA3_MINIMAL = 0;
    public static final int SCRAMBLING_MODE_DVB_CSA3_STANDARD = 0;
    public static final int SCRAMBLING_MODE_DVB_IDSA = 0;
    public static final int SCRAMBLING_MODE_MULTI2 = 0;
    public static final int SCRAMBLING_MODE_RESERVED = 0;
    public static final int SCRAMBLING_MODE_TDES_ECB = 0;
    public static final int SCRAMBLING_MODE_TDES_SCTE52 = 0;
    public static final int SESSION_USAGE_LIVE = 0;
    public static final int SESSION_USAGE_PLAYBACK = 0;
    public static final int SESSION_USAGE_RECORD = 0;
    public static final int SESSION_USAGE_TIMESHIFT = 0;

    public MediaCas(int p0) {}
    public MediaCas(Context p0, int p1, String p2, int p3) {}

    public void close() {}
    public static Object[] enumeratePlugins() { return null; }
    public void finalize() {}
    public static boolean isSystemIdSupported(int p0) { return false; }
    public Session openSession() { return null; }
    public void processEmm(byte[] p0, int p1, int p2) {}
    public void processEmm(byte[] p0) {}
    public void provision(String p0) {}
    public void refreshEntitlements(int p0, byte[] p1) {}
    public void sendEvent(int p0, int p1, byte[] p2) {}
    public void setEventListener(Object p0, Handler p1) {}
    public void setPrivateData(byte[] p0) {}
}
