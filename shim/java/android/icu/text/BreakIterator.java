package android.icu.text;
import android.icu.util.ULocale;
import android.icu.util.ULocale;

import android.icu.util.ULocale;

/**
 * Android ICU BreakIterator shim. Abstract wrapper around java.text.BreakIterator.
 */
public class BreakIterator {

    public static final int DONE = -1;

    protected java.text.BreakIterator delegate;

    protected BreakIterator(java.text.BreakIterator bi) {
        this.delegate = bi;
    }

    // ---- Static factories ----

    public static BreakIterator getWordInstance() {
        return new BreakIteratorImpl(java.text.BreakIterator.getWordInstance());
    }

    public static BreakIterator getWordInstance(ULocale locale) {
        return new BreakIteratorImpl(java.text.BreakIterator.getWordInstance(locale.toLocale()));
    }

    public static BreakIterator getLineInstance() {
        return new BreakIteratorImpl(java.text.BreakIterator.getLineInstance());
    }

    public static BreakIterator getLineInstance(ULocale locale) {
        return new BreakIteratorImpl(java.text.BreakIterator.getLineInstance(locale.toLocale()));
    }

    public static BreakIterator getSentenceInstance() {
        return new BreakIteratorImpl(java.text.BreakIterator.getSentenceInstance());
    }

    public static BreakIterator getSentenceInstance(ULocale locale) {
        return new BreakIteratorImpl(java.text.BreakIterator.getSentenceInstance(locale.toLocale()));
    }

    public static BreakIterator getCharacterInstance() {
        return new BreakIteratorImpl(java.text.BreakIterator.getCharacterInstance());
    }

    public static BreakIterator getCharacterInstance(ULocale locale) {
        return new BreakIteratorImpl(java.text.BreakIterator.getCharacterInstance(locale.toLocale()));
    }

    // ---- Instance methods ----

    public void setText(String text) {
        delegate.setText(text);
    }

    public int next() {
        int pos = delegate.next();
        return pos == java.text.BreakIterator.DONE ? DONE : pos;
    }

    public int previous() {
        int pos = delegate.previous();
        return pos == java.text.BreakIterator.DONE ? DONE : pos;
    }

    public int first() {
        return delegate.first();
    }

    public int last() {
        return delegate.last();
    }

    public int following(int offset) {
        int pos = delegate.following(offset);
        return pos == java.text.BreakIterator.DONE ? DONE : pos;
    }

    public int preceding(int offset) {
        int pos = delegate.preceding(offset);
        return pos == java.text.BreakIterator.DONE ? DONE : pos;
    }

    public int current() {
        return delegate.current();
    }

    // ---- Concrete impl ----

    private static final class BreakIteratorImpl extends BreakIterator {
        BreakIteratorImpl(java.text.BreakIterator bi) {
            super(bi);
        }
    }
}
