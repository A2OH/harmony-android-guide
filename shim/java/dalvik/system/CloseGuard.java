package dalvik.system;

/**
 * Stub for dalvik.system.CloseGuard.
 * Tracks whether resources have been properly closed.
 */
public class CloseGuard {
    private static final CloseGuard INSTANCE = new CloseGuard();

    public static CloseGuard get() {
        return INSTANCE;
    }

    public void open(String closer) {}
    public void close() {}
    public void warnIfOpen() {}
}
