package android.media;
import android.se.omapi.Session;
import android.se.omapi.Session;
import java.nio.ByteBuffer;

public final class MediaDescrambler implements AutoCloseable {
    public static final int SCRAMBLE_CONTROL_EVEN_KEY = 0;
    public static final int SCRAMBLE_CONTROL_ODD_KEY = 0;
    public static final int SCRAMBLE_CONTROL_RESERVED = 0;
    public static final int SCRAMBLE_CONTROL_UNSCRAMBLED = 0;
    public static final int SCRAMBLE_FLAG_PES_HEADER = 0;

    public MediaDescrambler(int p0) {}

    public void close() {}
    public int descramble(ByteBuffer p0, ByteBuffer p1, Object p2) { return 0; }
    public void finalize() {}
    public boolean requiresSecureDecoderComponent(String p0) { return false; }
    public void setMediaCasSession(Session p0) {}
}
