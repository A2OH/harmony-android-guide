package android.icu.text;

/**
 * Android ICU DecimalFormat shim. Extends NumberFormat and wraps java.text.DecimalFormat.
 */
public class DecimalFormat extends NumberFormat {

    private java.text.DecimalFormat decimalDelegate;

    public DecimalFormat() {
        super(new java.text.DecimalFormat());
        this.decimalDelegate = (java.text.DecimalFormat) delegate;
    }

    public DecimalFormat(String pattern) {
        super(new java.text.DecimalFormat(pattern));
        this.decimalDelegate = (java.text.DecimalFormat) delegate;
    }

    public DecimalFormat(String pattern, DecimalFormatSymbols symbols) {
        super(new java.text.DecimalFormat(pattern, symbols.toJavaDecimalFormatSymbols()));
        this.decimalDelegate = (java.text.DecimalFormat) delegate;
    }

    // ---- Pattern methods ----

    public void applyPattern(String pattern) {
        decimalDelegate.applyPattern(pattern);
    }

    public String toPattern() {
        return decimalDelegate.toPattern();
    }

    // ---- Symbols ----

    public DecimalFormatSymbols getDecimalFormatSymbols() {
        java.text.DecimalFormatSymbols jdfs = decimalDelegate.getDecimalFormatSymbols();
        // Wrap in our shim
        DecimalFormatSymbols result = new DecimalFormatSymbols();
        result.setDecimalSeparator(jdfs.getDecimalSeparator());
        result.setGroupingSeparator(jdfs.getGroupingSeparator());
        return result;
    }

    public void setDecimalFormatSymbols(DecimalFormatSymbols symbols) {
        decimalDelegate.setDecimalFormatSymbols(symbols.toJavaDecimalFormatSymbols());
    }

    // ---- Multiplier ----

    public void setMultiplier(int multiplier) {
        decimalDelegate.setMultiplier(multiplier);
    }

    public int getMultiplier() {
        return decimalDelegate.getMultiplier();
    }

    // ---- Grouping size ----

    public void setGroupingSize(int newValue) {
        decimalDelegate.setGroupingSize(newValue);
    }

    public int getGroupingSize() {
        return decimalDelegate.getGroupingSize();
    }
}
