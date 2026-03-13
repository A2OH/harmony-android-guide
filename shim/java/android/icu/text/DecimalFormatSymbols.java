package android.icu.text;

import android.icu.util.ULocale;

/**
 * Android ICU DecimalFormatSymbols shim. Wraps java.text.DecimalFormatSymbols.
 */
public class DecimalFormatSymbols {

    private java.text.DecimalFormatSymbols delegate;

    public DecimalFormatSymbols() {
        this.delegate = new java.text.DecimalFormatSymbols();
    }

    public DecimalFormatSymbols(ULocale locale) {
        this.delegate = new java.text.DecimalFormatSymbols(locale.toLocale());
    }

    private DecimalFormatSymbols(java.text.DecimalFormatSymbols dfs) {
        this.delegate = dfs;
    }

    // ---- Static factory ----

    public static DecimalFormatSymbols getInstance() {
        return new DecimalFormatSymbols(java.text.DecimalFormatSymbols.getInstance());
    }

    public static DecimalFormatSymbols getInstance(ULocale locale) {
        return new DecimalFormatSymbols(java.text.DecimalFormatSymbols.getInstance(locale.toLocale()));
    }

    // ---- Instance methods ----

    public char getDecimalSeparator() {
        return delegate.getDecimalSeparator();
    }

    public void setDecimalSeparator(char decimalSeparator) {
        delegate.setDecimalSeparator(decimalSeparator);
    }

    public char getGroupingSeparator() {
        return delegate.getGroupingSeparator();
    }

    public void setGroupingSeparator(char groupingSeparator) {
        delegate.setGroupingSeparator(groupingSeparator);
    }

    public String getPercentString() {
        return String.valueOf(delegate.getPercent());
    }

    public String getMinusSignString() {
        return String.valueOf(delegate.getMinusSign());
    }

    public char getZeroDigit() {
        return delegate.getZeroDigit();
    }

    /** Returns the underlying delegate for interop with DecimalFormat. */
    java.text.DecimalFormatSymbols toJavaDecimalFormatSymbols() {
        return delegate;
    }
}
