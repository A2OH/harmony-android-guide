package android.os;

/**
 * A2OH shim: Trace - performance tracing markers.
 * All methods are no-ops; no real tracing infrastructure is present on OpenHarmony.
 */
public final class Trace {

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
}
