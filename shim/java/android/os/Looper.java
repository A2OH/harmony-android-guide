package android.os;

/**
 * Looper — message loop with MessageQueue for the engine runtime.
 *
 * In single-threaded engine mode, messages are queued and dispatched
 * via pumpMessages() / flushAll() rather than blocking in loop().
 */
public final class Looper {

    private static volatile Looper sMainLooper;
    private static Thread sMainThread;

    /** Call from the main thread before any other Looper use. */
    public static void prepareMainLooper() {
        if (sMainLooper == null) {
            sMainThread = Thread.currentThread();
            sMainLooper = new Looper(sMainThread);
        }
    }

    static {
        // Auto-initialize if not called explicitly
        prepareMainLooper();
    }

    private static final ThreadLocal<Looper> sThreadLocal = new ThreadLocal<Looper>() {
        @Override
        protected Looper initialValue() {
            return new Looper(Thread.currentThread());
        }
    };

    private final Thread mThread;
    private final MessageQueue mQueue;

    private Looper(Thread thread) {
        mThread = thread;
        mQueue = new MessageQueue();
    }

    /** Return the main-thread Looper. Always non-null. */
    public static Looper getMainLooper() {
        return sMainLooper;
    }

    /** Return the Looper for the calling thread. Returns main looper for the main thread. */
    public static Looper myLooper() {
        if (Thread.currentThread() == sMainLooper.mThread) {
            return sMainLooper;
        }
        return sThreadLocal.get();
    }

    /** No-op — present so code that calls Looper.prepare() compiles. */
    public static void prepare() {
        // no-op
    }

    /** No-op — present for API compatibility. */
    public static void loop() {
        // no-op
    }

    /** Return this Looper's MessageQueue. */
    public MessageQueue getQueue() {
        return mQueue;
    }

    /** Return the thread this Looper is attached to.
     *  For main looper: always returns current thread — bypasses lifecycle thread checks
     *  in AndroidX (ArchTaskExecutor.isMainThread). Safe because Westlake is single-threaded. */
    public Thread getThread() {
        if (this == sMainLooper) return Thread.currentThread();
        return mThread;
    }

    /** Return true if this is the main-thread Looper. */
    public boolean isCurrentThread() {
        return mThread == Thread.currentThread();
    }

    /** Process all ready messages. Call from the engine's main loop. */
    public static int pumpMessages() {
        Looper main = getMainLooper();
        if (main != null) return main.mQueue.drainReady();
        return 0;
    }

    /** Process ALL queued messages regardless of time (for testing). */
    public static int flushAll() {
        Looper main = getMainLooper();
        if (main != null) return main.mQueue.drainAll();
        return 0;
    }

    /** Quit the looper. No-op in engine mode. */
    public void quit() {
        // no-op
    }

    /** Quit the looper safely. No-op in engine mode. */
    public void quitSafely() {
        // no-op
    }

    @Override
    public String toString() {
        return "Looper{thread=" + mThread.getName() + "}";
    }
}
