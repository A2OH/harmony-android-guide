package android.speech.tts;

/**
 * Shim for android.speech.tts.UtteranceProgressListener.
 * Abstract base with default no-op methods where applicable.
 */
public class UtteranceProgressListener {

    public void onStart(String utteranceId) {}

    public void onDone(String utteranceId) {}

    public void onError(String utteranceId) {}

    public void onStop(String utteranceId, boolean interrupted) {
    }

    public void onBeginSynthesis(String utteranceId, int sampleRateInHz, int audioFormat,
            int channelCount) {
    }
}
