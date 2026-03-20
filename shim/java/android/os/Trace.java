package android.os;

/**
 * A2OH shim: Trace - performance tracing markers.
 * All methods are no-ops; no real tracing infrastructure is present on OpenHarmony.
 */
public final class Trace {

    public static final long TRACE_TAG_VIEW = 1L << 3;
    public static final long TRACE_TAG_INPUT = 1L << 2;
    public static final long TRACE_TAG_APP = 1L << 12;
    public static final long TRACE_TAG_RESOURCES = 1L << 13;

    private Trace() {}

    /** Writes a trace begin event for the given section name. No-op in shim. */
    public static void beginSection(String sectionName) {}

    /** Writes a trace end event for the most recently begun section. No-op in shim. */
    public static void endSection() {}

    /** Writes an async trace begin event. No-op in shim. */
    public static void beginAsyncSection(String methodName, int cookie) {}

    /** Writes an async trace end event. No-op in shim. */
    public static void endAsyncSection(String methodName, int cookie) {}

    /**
     * Returns whether a given trace tag is enabled.
     * Always returns {@code false} in the shim.
     */
    public static boolean isTagEnabled(long traceTag) {
        return false;
    }

    /** Writes a trace counter event. No-op in shim. */
    public static void setCounter(String counterName, long counterValue) {}
    public static void traceBegin(long traceTag, String methodName) {}
    public static void traceEnd(long traceTag) {}
    public static void asyncTraceBegin(long traceTag, String methodName, int cookie) {}
    public static void asyncTraceEnd(long traceTag, String methodName, int cookie) {}
    public static void traceCounter(long traceTag, String counterName, int counterValue) {}
}
