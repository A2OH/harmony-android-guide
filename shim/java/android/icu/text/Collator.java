package android.icu.text;

import android.icu.util.ULocale;
import java.text.CollationKey;

/**
 * Android ICU Collator shim. Abstract wrapper around java.text.Collator.
 */
public abstract class Collator {

    public static final int PRIMARY   = java.text.Collator.PRIMARY;
    public static final int SECONDARY = java.text.Collator.SECONDARY;
    public static final int TERTIARY  = java.text.Collator.TERTIARY;
    public static final int IDENTICAL = 15;

    protected java.text.Collator delegate;

    protected Collator(java.text.Collator col) {
        this.delegate = col;
    }

    // ---- Static factories ----

    public static Collator getInstance() {
        return new CollatorImpl(java.text.Collator.getInstance());
    }

    public static Collator getInstance(ULocale locale) {
        return new CollatorImpl(java.text.Collator.getInstance(locale.toLocale()));
    }

    // ---- Instance methods ----

    public int compare(String source, String target) {
        return delegate.compare(source, target);
    }

    public CollationKey getCollationKey(String source) {
        return delegate.getCollationKey(source);
    }

    public void setStrength(int newStrength) {
        if (newStrength == IDENTICAL) {
            delegate.setStrength(java.text.Collator.IDENTICAL);
        } else {
            delegate.setStrength(newStrength);
        }
    }

    public int getStrength() {
        int s = delegate.getStrength();
        return (s == java.text.Collator.IDENTICAL) ? IDENTICAL : s;
    }

    // ---- Concrete impl ----

    private static final class CollatorImpl extends Collator {
        CollatorImpl(java.text.Collator col) {
            super(col);
        }
    }
}
