package android.util;

/**
 * Android-compatible CloseGuard shim.
 * Tracks resource leaks — warns if close() not called before GC.
 */
public class CloseGuard {
    private String mCloserName;
    private boolean mClosed;

    public static CloseGuard get() {
        return new CloseGuard();
    }

    public void open(String closer) {
        mCloserName = closer;
        mClosed = false;
    }

    public void close() {
        mClosed = true;
    }

    public void warnIfOpen() {
        if (!mClosed && mCloserName != null) {
            System.err.println("A resource was acquired but never released. "
                + "See java.io.Closeable for info on avoiding resource leaks. "
                + "Callsite: " + mCloserName);
        }
    }
}
