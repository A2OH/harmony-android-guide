package android.graphics;

import java.util.Iterator;

/**
 * Shim: android.graphics.PathIterator (API 34+)
 *
 * Iterates over the verbs/points of a Path.  This stub always reports an
 * empty path (hasNext() → false, peek() → VERB_DONE).
 */
public class PathIterator implements Iterator<Object> {

    // ── Verb constants ────────────────────────────────────────────────────────

    public static final int VERB_MOVE  = 0;
    public static final int VERB_LINE  = 1;
    public static final int VERB_QUAD  = 2;
    public static final int VERB_CONIC = 3;
    public static final int VERB_CUBIC = 4;
    public static final int VERB_CLOSE = 5;
    public static final int VERB_DONE  = 6;

    // ── Constructor ───────────────────────────────────────────────────────────

    public PathIterator() {}

    // ── Iterator<Object> ──────────────────────────────────────────────────────

    @Override
    public boolean hasNext() {
        return false;
    }

    @Override
    public Object next() {
        return null;
    }

    // ── PathIterator-specific ─────────────────────────────────────────────────

    /**
     * Returns the verb type of the next segment without advancing the iterator.
     * Always returns VERB_DONE in this stub.
     */
    public int peek() {
        return VERB_DONE;
    }
}
