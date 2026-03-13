package android.os;

/**
 * Shim: android.os.Looper — stub implementation.
 *
 * In the shim layer there is no real message-loop infrastructure.
 * Handler dispatches messages synchronously (or via a ScheduledExecutor for
 * delayed posts), so Looper exists only to satisfy API references.
 *
 * {@link #getMainLooper()} returns a singleton stub tied to the main thread.
 * {@link #myLooper()} returns a per-thread stub via a ThreadLocal.
 */
public final class Looper {

    // ── Singleton / thread-local stubs ───────────────────────────────────────

    private static final Looper sMainLooper = new Looper(Thread.currentThread());

    private static final ThreadLocal<Looper> sThreadLocal = new ThreadLocal<Looper>() {
        @Override
        protected Looper initialValue() {
            return new Looper(Thread.currentThread());
        }
    };

    private final Thread mThread;

    private Looper(Thread thread) {
        mThread = thread;
    }

    // ── Public API ───────────────────────────────────────────────────────────

    /**
     * Return the main-thread Looper stub.
     * Always non-null; the associated thread is the thread that first loaded
     * this class (typically the JVM main thread).
     */
    public static Looper getMainLooper() {
        return sMainLooper;
    }

    /**
     * Return the Looper stub for the calling thread.
     * Creates a new stub on first call for each thread.
     */
    public static Looper myLooper() {
        return sThreadLocal.get();
    }

    /**
     * Prepare a Looper for the current thread (no-op in the shim;
     * present so code that calls {@code Looper.prepare()} compiles).
     */
    public static void prepare() {
        // no-op
    }

    /**
     * Start the message loop (no-op in the shim; present for API
     * compatibility with code that calls {@code Looper.loop()}).
     */
    public static void loop() {
        // no-op
    }

    /** Return the thread that this Looper is attached to. */
    public Thread getThread() {
        return mThread;
    }

    /** Return true if this is the main-thread Looper. */
    public boolean isCurrentThread() {
        return mThread == Thread.currentThread();
    }

    @Override
    public String toString() {
        return "Looper{thread=" + mThread.getName() + "}";
    }
}
