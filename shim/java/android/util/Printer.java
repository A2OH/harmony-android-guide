package android.util;

/**
 * Android-compatible Printer interface shim.
 * Simple interface for printing a line of text.
 */
public interface Printer {
    /**
     * Print a line of text.
     *
     * @param x the text to print
     */
    void println(String x);
}
