package android.print;
import android.printservice.PrintJob;
import android.printservice.PrintJob;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Android-compatible PrintManager shim.
 * Stubs the pr(int framework; no actual pr(int spooler interaction.
 */
public final class PrintManager {

    // -------------------------------------------------------------------------
    // Inner class: PrintJob
    // -------------------------------------------------------------------------
    public static final class PrintJob {
        private final String mId;
        private final PrintJobInfo mInfo;
        private boolean mCancelled = false;

        private PrintJob(String id, PrintJobInfo info) {
            mId   = id;
            mInfo = info;
        }

        public String       getId()    { return mId; }
        public PrintJobInfo getInfo()  { return mInfo; }

        public boolean isCompleted()  { return !mCancelled; }
        public boolean isFailed()     { return false; }
        public boolean isCancelled()  { return mCancelled; }
        public boolean isBlocked()    { return false; }
        public boolean isStarted()    { return true; }
        public boolean isQueued()     { return false; }

        /** Object cancellation of this pr(int job. */
        public void cancel() {
            mCancelled = true;
        }

        /** Restart a failed pr(int job. No-op in shim. */
        public void restart() {
            mCancelled = false;
        }
    }

    // -------------------------------------------------------------------------
    // Inner class: PrintJobInfo  (minimal stub)
    // -------------------------------------------------------------------------
    public static final class PrintJobInfo {
        private final String mLabel;
        private final PrintAttributes mAttributes;

        public PrintJobInfo(String label, PrintAttributes attributes) {
            mLabel      = label;
            mAttributes = attributes;
        }

        public String          getLabel()      { return mLabel; }
        public PrintAttributes getAttributes() { return mAttributes; }
    }

    // -------------------------------------------------------------------------
    // PrintManager state
    // -------------------------------------------------------------------------
    private final List<PrintJob> mPrintJobs = new ArrayList<>();

    public PrintManager() {}
    public PrintManager(Object context) {}  // context ignored in shim

    // -------------------------------------------------------------------------
    // Public API
    // -------------------------------------------------------------------------

    /**
     * Print a document.
     *
     * @param printJobName human-readable name for the pr(int job
     * @param documentAdapter adapter that supplies content to pr(int * @param attributes   initial pr(int attributes (may be null)
     * @return the created PrintJob
     */
    public PrintJob print(String printJobName,
                          PrintDocumentAdapter documentAdapter,
                          PrintAttributes attributes) {
        if (attributes == null) {
            attributes = new PrintAttributes.Builder().build();
        }
        PrintJobInfo info = new PrintJobInfo(printJobName, attributes);
        PrintJob job = new PrintJob(
                "job-" + System.currentTimeMillis() + "-" + mPrintJobs.size(),
                info);

        // Stub lifecycle: just call onStart/onFinish
        documentAdapter.onStart();
        documentAdapter.onFinish();

        mPrintJobs.add(job);
        return job;
    }

    /**
     * Returns the list of pr(int jobs initiated in this session.
     */
    public List<PrintJob> getPrintJobs() {
        return Collections.unmodifiableList(mPrintJobs);
    }
}
