package android.media.metrics;

/**
 * Android-compatible MediaMetricsManager stub (API 31+).
 * A2OH shim layer — OpenHarmony migration.
 *
 * All factory methods return null; actual metric collection is not
 * supported in the compatibility shim.
 */
public class MediaMetricsManager {

    /** Stub — returns null. */
    public PlaybackSession createPlaybackSession() {
        return null;
    }

    /** Stub — returns null. */
    public RecordingSession createRecordingSession() {
        return null;
    }

    /** Stub — returns null. */
    public TranscodingSession createTranscodingSession() {
        return null;
    }

    /** Stub — returns null. */
    public EditingSession createEditingSession() {
        return null;
    }

    /** Stub — returns null. */
    public BundleSession createBundleSession() {
        return null;
    }
}
