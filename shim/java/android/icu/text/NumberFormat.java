package android.icu.text;

import android.icu.util.ULocale;
import java.text.ParseException;

/**
 * Android ICU NumberFormat shim. Abstract wrapper around java.text.NumberFormat.
 */
public abstract class NumberFormat {

    protected java.text.NumberFormat delegate;

    protected NumberFormat(java.text.NumberFormat delegate) {
        this.delegate = delegate;
    }

    // ---- Static factories ----

    public static NumberFormat getInstance() {
        return new NumberFormatImpl(java.text.NumberFormat.getInstance());
    }

    public static NumberFormat getInstance(ULocale locale) {
        return new NumberFormatImpl(java.text.NumberFormat.getInstance(locale.toLocale()));
    }

    public static NumberFormat getCurrencyInstance() {
        return new NumberFormatImpl(java.text.NumberFormat.getCurrencyInstance());
    }

    public static NumberFormat getCurrencyInstance(ULocale locale) {
        return new NumberFormatImpl(java.text.NumberFormat.getCurrencyInstance(locale.toLocale()));
    }

    public static NumberFormat getPercentInstance() {
        return new NumberFormatImpl(java.text.NumberFormat.getPercentInstance());
    }

    public static NumberFormat getPercentInstance(ULocale locale) {
        return new NumberFormatImpl(java.text.NumberFormat.getPercentInstance(locale.toLocale()));
    }

    public static NumberFormat getIntegerInstance() {
        return new NumberFormatImpl(java.text.NumberFormat.getIntegerInstance());
    }

    public static NumberFormat getIntegerInstance(ULocale locale) {
        return new NumberFormatImpl(java.text.NumberFormat.getIntegerInstance(locale.toLocale()));
    }

    // ---- Instance methods ----

    public String format(long number) {
        return delegate.format(number);
    }

    public String format(double number) {
        return delegate.format(number);
    }

    public Number parse(String text) throws ParseException {
        return delegate.parse(text);
    }

    public void setMaximumFractionDigits(int newValue) {
        delegate.setMaximumFractionDigits(newValue);
    }

    public void setMinimumFractionDigits(int newValue) {
        delegate.setMinimumFractionDigits(newValue);
    }

    public int getMaximumFractionDigits() {
        return delegate.getMaximumFractionDigits();
    }

    public int getMinimumFractionDigits() {
        return delegate.getMinimumFractionDigits();
    }

    // ---- Default concrete subclass ----

    private static final class NumberFormatImpl extends NumberFormat {
        NumberFormatImpl(java.text.NumberFormat fmt) {
            super(fmt);
        }
    }
}
