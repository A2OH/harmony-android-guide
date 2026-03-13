package android.util;

/**
 * Android-compatible TimingLogger shim. No-op implementation of the deprecated
 * android.util.TimingLogger class.
 *
 * @deprecated TimingLogger is deprecated in Android API 30 and removed in later releases.
 *             This shim exists solely for source-level compatibility.
 */
@Deprecated
public final class TimingLogger {

    private String mTag;
    private String mLabel;

    /**
     * Creates a new TimingLogger instance.
     *
     * @param tag   the log tag to use for all log output
     * @param label a string to identify this timing sequence in the output
     */
    public TimingLogger(String tag, String label) {
        reset(tag, label);
    }

    /**
     * Resets this TimingLogger. This may be called to reuse the logger after the last
     * {@link #dumpToLog()} call.
     *
     * @param tag   the log tag
     * @param label a string to identify this timing sequence
     */
    public void reset(String tag, String label) {
        mTag   = tag;
        mLabel = label;
    }

    /**
     * Resets the timing logger using the previously set tag and label.
     */
    public void reset() {
        // No-op: no accumulated splits to clear in this shim
    }

    /**
     * Adds a split for the specified label. No-op in this shim.
     *
     * @param splitLabel a label identifying this split, or null
     */
    public void addSplit(String splitLabel) {
        // No-op
    }

    /**
     * Dumps the splits to the log. No-op in this shim.
     */
    public void dumpToLog() {
        // No-op
    }
}
