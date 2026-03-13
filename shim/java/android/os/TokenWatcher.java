package android.os;

import java.util.HashMap;
import java.util.Map;

/**
 * A2OH shim: TokenWatcher - tracks a set of IBinder tokens.
 *
 * When the first token is acquired {@link #acquired()} is called; when the last
 * token is released (or dies) {@link #released()} is called.  Subclasses must
 * implement both abstract callbacks.
 */
public abstract class TokenWatcher {

    private final Handler mHandler;
    private final String mTag;
    private final Map<IBinder, String> mTokens = new HashMap<>();

    public TokenWatcher(Handler h, String tag) {
        mHandler = h;
        mTag = tag;
    }

    // ---- Abstract callbacks -------------------------------------------------

    /** Called when the first token is acquired. */
    public abstract void acquired();

    /** Called when the last token is released. */
    public abstract void released();

    // ---- Token management ---------------------------------------------------

    /**
     * Records that the given binder has acquired a token with the given name.
     * If this is the first token, {@link #acquired()} is called.
     */
    public synchronized void acquire(IBinder token, String tag) {
        if (token == null) return;
        boolean wasEmpty = mTokens.isEmpty();
        mTokens.put(token, tag != null ? tag : "");
        if (wasEmpty && !mTokens.isEmpty()) {
            acquired();
        }
    }

    /**
     * Releases the token held by the given binder.
     * If this was the last token, {@link #released()} is called.
     */
    public synchronized void release(IBinder token) {
        if (token == null) return;
        if (mTokens.remove(token) != null && mTokens.isEmpty()) {
            released();
        }
    }

    /**
     * Returns {@code true} if at least one token is currently held.
     */
    public synchronized boolean isAcquired() {
        return !mTokens.isEmpty();
    }

    /**
     * Releases all tokens and, if any were held, calls {@link #released()}.
     */
    public synchronized void cleanup() {
        boolean wasAcquired = !mTokens.isEmpty();
        mTokens.clear();
        if (wasAcquired) {
            released();
        }
    }

    /** Returns the tag passed to the constructor. */
    public String getTag() {
        return mTag;
    }
}
