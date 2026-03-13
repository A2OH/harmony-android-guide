package android.net;
import android.os.CancellationSignal;
import java.util.concurrent.Executor;

public final class DnsResolver {
    public static final int CLASS_IN = 0;
    public static final int ERROR_PARSE = 0;
    public static final int ERROR_SYSTEM = 0;
    public static final int FLAG_EMPTY = 0;
    public static final int FLAG_NO_CACHE_LOOKUP = 0;
    public static final int FLAG_NO_CACHE_STORE = 0;
    public static final int FLAG_NO_RETRY = 0;
    public static final int TYPE_A = 0;
    public static final int TYPE_AAAA = 0;

    public DnsResolver() {}

    public void query(Network p0, String p1, int p2, Executor p3, CancellationSignal p4, Object p5) {}
    public void query(Network p0, String p1, int p2, int p3, Executor p4, CancellationSignal p5, Object p6) {}
    public void rawQuery(Network p0, byte[] p1, int p2, Executor p3, CancellationSignal p4, Object p5) {}
    public void rawQuery(Network p0, String p1, int p2, int p3, int p4, Executor p5, CancellationSignal p6, Object p7) {}
}
