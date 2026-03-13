package android.util;

import java.io.PrintWriter;

/**
 * Android-compatible PrintWriterPrinter shim.
 * Implementation of Printer that sends output to a PrintWriter.
 */
public class PrintWriterPrinter implements Printer {

    private final PrintWriter mPW;

    /**
     * Create a new PrintWriterPrinter.
     *
     * @param pw The PrintWriter to write to.
     */
    public PrintWriterPrinter(PrintWriter pw) {
        mPW = pw;
    }

    @Override
    public void println(String x) {
        mPW.println(x);
    }
}
