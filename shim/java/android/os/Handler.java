package android.os;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Shim: android.os.Handler — pure Java implementation.
 *
 * Design notes
 * ────────────
 * Android's Handler dispatches Runnables and Messages on the thread that owns
 * a Looper.  In the shim layer there is no real Looper, so we adopt the
 * simplest strategy that makes apps work in both unit-test and headless
 * environments:
 *
 *  • post(r)  / sendMessage(msg)  — execute immediately on the calling thread.
 *  • postDelayed / sendMessageDelayed / postAtTime — schedule via a shared
 *    ScheduledExecutorService daemon thread pool.
 *
 * This matches the behaviour expected by most Android apps when unit-tested
 * without a real Looper (e.g. with Robolectric's "paused" mode disabled).
 *
 * The Callback interface and handleMessage(Message) override point are fully
 * supported.
 */
public class Handler {

    // ── Callback interface ───────────────────────────────────────────────────

    /**
     * Callback that can intercept messages before they reach
     * {@link #handleMessage(Message)}.
     */
    public interface Callback {
        /**
         * @return true if no further handling is desired, false to let
         *         {@link Handler#handleMessage(Message)} run afterwards.
         */
        boolean handleMessage(Message msg);
    }

    // ── Shared scheduler ─────────────────────────────────────────────────────

    /**
     * Single shared pool used by all Handler instances for delayed dispatch.
     * Daemon threads so they don't prevent JVM exit.
     */
    private static final ScheduledExecutorService sScheduler =
            Executors.newScheduledThreadPool(2, r -> {
                Thread t = new Thread(r, "Handler-shim-scheduler");
                t.setDaemon(true);
                return t;
            });

    // ── Instance state ───────────────────────────────────────────────────────

    private final Callback mCallback;
    private final Looper   mLooper;

    // Pending scheduled tasks — tracked so removeCallbacks / removeMessages
    // can cancel them.
    private final List<PendingRunnable>  mPendingRunnables = new ArrayList<>();
    private final List<PendingMessage>   mPendingMessages  = new ArrayList<>();

    // ── Constructors ─────────────────────────────────────────────────────────

    /** Construct a Handler with no Callback and the default (stub) Looper. */
    public Handler() {
        this(null, null);
    }

    /**
     * Construct a Handler with a Callback that intercepts messages.
     * @param callback optional Callback; may be null.
     */
    public Handler(Callback callback) {
        this(null, callback);
    }

    /**
     * Construct a Handler bound to a specific Looper stub.
     * @param looper stub Looper; if null, {@link Looper#myLooper()} is used.
     */
    public Handler(Looper looper) {
        this(looper, null);
    }

    /**
     * Full constructor.
     * @param looper   stub Looper (may be null → uses myLooper stub)
     * @param callback optional message interceptor (may be null)
     */
    public Handler(Looper looper, Callback callback) {
        mLooper   = looper != null ? looper : Looper.myLooper();
        mCallback = callback;
    }

    // ── Core override point ──────────────────────────────────────────────────

    /**
     * Subclasses override this to handle messages delivered to this Handler.
     * Default implementation is a no-op.
     */
    public void handleMessage(Message msg) {
        // default: no-op — subclasses override
    }

    // ── Internal dispatch ────────────────────────────────────────────────────

    /**
     * Dispatch a Message through the Callback (if any) and then to
     * {@link #handleMessage(Message)}.
     */
    private void dispatchMessage(Message msg) {
        if (mCallback != null) {
            if (mCallback.handleMessage(msg)) return; // consumed
        }
        handleMessage(msg);
    }

    // ── post / postDelayed / postAtTime ──────────────────────────────────────

    /**
     * Run {@code r} immediately on the calling thread.
     * Returns true (always succeeds in the shim).
     */
    public boolean post(Runnable r) {
        if (r == null) return false;
        r.run();
        return true;
    }

    /**
     * Schedule {@code r} to run after at least {@code delayMillis} ms.
     * @return true if the runnable was successfully queued.
     */
    public boolean postDelayed(Runnable r, long delayMillis) {
        if (r == null) return false;
        if (delayMillis <= 0) return post(r);

        PendingRunnable pr = new PendingRunnable(r);
        ScheduledFuture<?> future = sScheduler.schedule(() -> {
            boolean removed;
            synchronized (mPendingRunnables) {
                removed = mPendingRunnables.remove(pr);
            }
            if (removed) r.run();
        }, delayMillis, TimeUnit.MILLISECONDS);
        pr.future = future;

        synchronized (mPendingRunnables) {
            mPendingRunnables.add(pr);
        }
        return true;
    }

    /**
     * Schedule {@code r} to run at an absolute uptime (ms since boot).
     * In the shim, we compute the delay relative to {@link SystemClock#uptimeMillis()}.
     */
    public boolean postAtTime(Runnable r, long uptimeMillis) {
        long delay = uptimeMillis - SystemClock.uptimeMillis();
        return postDelayed(r, Math.max(0, delay));
    }

    /**
     * Schedule {@code r} to run at an absolute uptime (token overload, ignores token).
     */
    public boolean postAtTime(Runnable r, Object token, long uptimeMillis) {
        return postAtTime(r, uptimeMillis);
    }

    /**
     * Post {@code r} at the front of the queue (runs immediately in the shim).
     */
    public boolean postAtFrontOfQueue(Runnable r) {
        return post(r);
    }

    // ── sendMessage family ───────────────────────────────────────────────────

    /**
     * Dispatch {@code msg} immediately to this Handler's {@link #handleMessage}.
     */
    public boolean sendMessage(Message msg) {
        if (msg == null) return false;
        msg.target = this;
        dispatchMessage(msg);
        return true;
    }

    /**
     * Create a Message with the given {@code what} and send it immediately.
     */
    public boolean sendEmptyMessage(int what) {
        Message msg = Message.obtain(this, what);
        return sendMessage(msg);
    }

    /**
     * Schedule a Message with the given {@code what} to be sent after
     * {@code delayMillis} ms.
     */
    public boolean sendEmptyMessageDelayed(int what, long delayMillis) {
        Message msg = Message.obtain(this, what);
        return sendMessageDelayed(msg, delayMillis);
    }

    /**
     * Schedule {@code msg} to be dispatched after {@code delayMillis} ms.
     */
    public boolean sendMessageDelayed(Message msg, long delayMillis) {
        if (msg == null) return false;
        if (delayMillis <= 0) return sendMessage(msg);

        msg.target = this;
        PendingMessage pm = new PendingMessage(msg);
        ScheduledFuture<?> future = sScheduler.schedule(() -> {
            boolean removed;
            synchronized (mPendingMessages) {
                removed = mPendingMessages.remove(pm);
            }
            if (removed) dispatchMessage(pm.msg);
        }, delayMillis, TimeUnit.MILLISECONDS);
        pm.future = future;

        synchronized (mPendingMessages) {
            mPendingMessages.add(pm);
        }
        return true;
    }

    /**
     * Schedule {@code msg} to be dispatched at absolute uptime {@code uptimeMillis}.
     */
    public boolean sendMessageAtTime(Message msg, long uptimeMillis) {
        long delay = uptimeMillis - SystemClock.uptimeMillis();
        return sendMessageDelayed(msg, Math.max(0, delay));
    }

    /**
     * Send {@code msg} at the front of the queue (immediate in the shim).
     */
    public boolean sendMessageAtFrontOfQueue(Message msg) {
        return sendMessage(msg);
    }

    // ── removeCallbacks / removeMessages ─────────────────────────────────────

    /**
     * Cancel any pending posts of {@code r}.
     */
    public void removeCallbacks(Runnable r) {
        if (r == null) return;
        synchronized (mPendingRunnables) {
            Iterator<PendingRunnable> it = mPendingRunnables.iterator();
            while (it.hasNext()) {
                PendingRunnable pr = it.next();
                if (pr.runnable == r) {
                    if (pr.future != null) pr.future.cancel(false);
                    it.remove();
                }
            }
        }
    }

    /**
     * Cancel all pending {@code sendMessage} calls with the given {@code what} code.
     */
    public void removeMessages(int what) {
        synchronized (mPendingMessages) {
            Iterator<PendingMessage> it = mPendingMessages.iterator();
            while (it.hasNext()) {
                PendingMessage pm = it.next();
                if (pm.msg.what == what) {
                    if (pm.future != null) pm.future.cancel(false);
                    it.remove();
                }
            }
        }
    }

    /**
     * Cancel all pending Runnables and Messages with the given {@code what}.
     */
    public void removeCallbacksAndMessages(Object token) {
        // token is not tracked in this shim — clear everything as a safe fallback
        synchronized (mPendingRunnables) {
            for (PendingRunnable pr : mPendingRunnables) {
                if (pr.future != null) pr.future.cancel(false);
            }
            mPendingRunnables.clear();
        }
        synchronized (mPendingMessages) {
            for (PendingMessage pm : mPendingMessages) {
                if (pm.future != null) pm.future.cancel(false);
            }
            mPendingMessages.clear();
        }
    }

    // ── Looper accessor ──────────────────────────────────────────────────────

    /** Return the Looper stub that this Handler is bound to. Never null. */
    public Looper getLooper() {
        return mLooper;
    }

    // ── hasMessages ──────────────────────────────────────────────────────────

    /** Return true if there are pending messages with the given what code. */
    public boolean hasMessages(int what) {
        synchronized (mPendingMessages) {
            for (PendingMessage pm : mPendingMessages) {
                if (pm.msg.what == what) return true;
            }
        }
        return false;
    }

    /** Return true if there are pending posts of the given Runnable. */
    public boolean hasCallbacks(Runnable r) {
        synchronized (mPendingRunnables) {
            for (PendingRunnable pr : mPendingRunnables) {
                if (pr.runnable == r) return true;
            }
        }
        return false;
    }

    // ── Internal helpers ─────────────────────────────────────────────────────

    /** Wrapper that keeps a reference to both the Runnable and its Future. */
    private static final class PendingRunnable {
        final Runnable runnable;
        Future<?> future;

        PendingRunnable(Runnable r) { this.runnable = r; }
    }

    /** Wrapper that keeps a reference to both the Message and its Future. */
    private static final class PendingMessage {
        final Message msg;
        Future<?> future;

        PendingMessage(Message m) { this.msg = m; }
    }
}
