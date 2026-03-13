package android.text.style;
import android.os.LocaleList;
import android.os.LocaleList;

import java.util.Locale;

/**
 * Android-compatible LocaleSpan shim for the A2OH compatibility layer.
 * Attaches a {@link Locale} to a range of text, which can be used by
 * the text layout engine to select locale-appropriate glyphs and metrics.
 */
public class LocaleSpan extends MetricAffectingSpan {

    private final Locale mLocale;

    /**
     * Creates a LocaleSpan for the given {@link Locale}.
     *
     * @param locale the locale to attach to the text
     */
    public LocaleSpan(Locale locale) {
        mLocale = locale;
    }

    /**
     * Returns the locale attached by this span.
     *
     * @return the {@link Locale} set on construction
     */
    public Locale getLocale() {
        return mLocale;
    }

    /**
     * Returns the locales attached by this span as a LocaleList.
     * Stub returns {@code null} because LocaleList is not yet shimmed.
     *
     * @return {@code null}
     */
    public Object getLocales() {
        return null;
    }

    /** No-op shim -- locale spans do not alter draw state in this layer. */
    @Override
    public void updateDrawState(Object tp) {
        // no-op
    }

    /** No-op shim -- locale spans do not alter measure state in this layer. */
    @Override
    public void updateMeasureState(Object tp) {
        // no-op
    }
}
