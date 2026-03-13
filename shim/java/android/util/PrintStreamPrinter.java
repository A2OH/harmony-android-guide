package android.util;

import java.io.PrintStream;

/**
 * Android-compatible PrintStreamPrinter shim.
 * Implements Printer by writing to a PrintStream.
 */
public class PrintStreamPrinter implements Printer {
    private final PrintStream mPrintStream;

    public PrintStreamPrinter(PrintStream printStream) {
        mPrintStream = printStream;
    }

    @Override
    public void println(String x) {
        mPrintStream.println(x);
    }
}
