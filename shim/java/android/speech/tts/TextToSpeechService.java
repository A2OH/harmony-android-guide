package android.speech.tts;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import java.util.List;
import java.util.Set;

public class TextToSpeechService extends Service {
    public TextToSpeechService() {}

    public IBinder onBind(Intent p0) { return null; }
    public String onGetDefaultVoiceNameFor(String p0, String p1, String p2) { return null; }
    public Set<?> onGetFeaturesForLanguage(String p0, String p1, String p2) { return null; }
    public String[] onGetLanguage() { return null; }
    public List<?> onGetVoices() { return null; }
    public int onIsLanguageAvailable(String p0, String p1, String p2) { return 0; }
    public int onIsValidVoiceName(String p0) { return 0; }
    public int onLoadLanguage(String p0, String p1, String p2) { return 0; }
    public int onLoadVoice(String p0) { return 0; }
    public void onStop() {}
    public void onSynthesizeText(SynthesisRequest p0, SynthesisCallback p1) {}
}
