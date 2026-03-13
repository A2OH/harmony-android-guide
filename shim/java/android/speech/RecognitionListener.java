package android.speech;
import android.os.Bundle;

public interface RecognitionListener {
    void onBeginningOfSpeech();
    void onBufferReceived(byte[] p0);
    void onEndOfSpeech();
    void onError(int p0);
    void onEvent(int p0, Bundle p1);
    void onPartialResults(Bundle p0);
    void onReadyForSpeech(Bundle p0);
    void onResults(Bundle p0);
    void onRmsChanged(float p0);
}
