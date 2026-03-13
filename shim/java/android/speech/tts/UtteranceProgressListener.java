package android.speech.tts;

/**
 * Android-compatible UtteranceProgressListener shim. Stub for A2OH migration.
 */
public abstract class UtteranceProgressListener {

    /** Called when the synthesis engine starts speaking. */
    public abstract void onStart(String utteranceId);

    /** Called when the synthesis engine finishes speaking. */
    public abstract void onDone(String utteranceId);

    /** Called when an error occurs during synthesis. */
    public abstract void onError(String utteranceId);

    /** Called when an error occurs (with error code). Default delegates to {@link #onError(String)}. */
    public void onError(String utteranceId, int errorCode) {
        onError(utteranceId);
    }

    /** Called when synthesis is stopped. Default is a no-op. */
    public void onStop(String utteranceId, boolean interrupted) {
        // default no-op
    }
}
