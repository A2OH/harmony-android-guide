package android.os;

/**
 * Shim: android.os.Message — pure Java implementation.
 *
 * Carries data for Handler.sendMessage(). Supports a simple
 * object pool (obtain/recycle) matching the AOSP API surface.
 */
public final class Message {

    /** User-defined message code. */
    public int what;

    /** Arbitrary int arguments. */
    public int arg1;
    public int arg2;

    /** Arbitrary object payload. */
    public Object obj;

    /** The Handler that will receive this message (set by obtain(Handler,…)). */
    public Handler target;

    // Extra Bundle data, lazily created.
    private Bundle data;

    // ── Simple pool ──────────────────────────────────────────────────────────

    private static final int POOL_SIZE = 50;
    private static final Object sPoolLock = new Object();
    private static Message sPool;
    private static int sPoolCount;
    private Message next; // pool chain

    // ── Factory ──────────────────────────────────────────────────────────────

    /**
     * Return a new Message from the global pool, or allocate one if the pool
     * is empty.
     */
    public static Message obtain() {
        synchronized (sPoolLock) {
            if (sPool != null) {
                Message m = sPool;
                sPool = m.next;
                m.next = null;
                sPoolCount--;
                return m;
            }
        }
        return new Message();
    }

    /** Obtain a message pre-configured with a target Handler and what code. */
    public static Message obtain(Handler h, int what) {
        Message m = obtain();
        m.target = h;
        m.what = what;
        return m;
    }

    /** Obtain a message pre-configured with target, what, and obj. */
    public static Message obtain(Handler h, int what, Object obj) {
        Message m = obtain(h, what);
        m.obj = obj;
        return m;
    }

    /** Obtain a message pre-configured with target, what, arg1, arg2. */
    public static Message obtain(Handler h, int what, int arg1, int arg2) {
        Message m = obtain(h, what);
        m.arg1 = arg1;
        m.arg2 = arg2;
        return m;
    }

    /** Return this message to the pool. Reset all fields. */
    public void recycle() {
        what = 0;
        arg1 = 0;
        arg2 = 0;
        obj = null;
        target = null;
        data = null;
        synchronized (sPoolLock) {
            if (sPoolCount < POOL_SIZE) {
                next = sPool;
                sPool = this;
                sPoolCount++;
            }
        }
    }

    // ── Data Bundle ──────────────────────────────────────────────────────────

    /** Return the Bundle attached to this message, creating it if absent. */
    public Bundle getData() {
        if (data == null) data = new Bundle();
        return data;
    }

    /**
     * Peek at the Bundle without creating one. Returns null if no Bundle has
     * been set.
     */
    public Bundle peekData() { return data; }

    /** Attach an existing Bundle to this message. */
    public void setData(Bundle data) { this.data = data; }

    // ── Dispatch ─────────────────────────────────────────────────────────────

    /**
     * Send this message to the Handler stored in {@link #target}.
     * Equivalent to {@code target.sendMessage(this)}.
     */
    public void sendToTarget() {
        if (target == null) throw new IllegalStateException("Message has no target Handler");
        target.sendMessage(this);
    }

    // ── Object ───────────────────────────────────────────────────────────────

    @Override
    public String toString() {
        return "Message{what=" + what + ", arg1=" + arg1 + ", arg2=" + arg2
                + ", obj=" + obj + "}";
    }
}
