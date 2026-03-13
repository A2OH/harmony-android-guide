package android.speech.tts;
import android.os.Bundle;

public final class SynthesisRequest {
    public SynthesisRequest(String p0, Bundle p1) {}
    public SynthesisRequest(CharSequence p0, Bundle p1) {}

    public int getCallerUid() { return 0; }
    public CharSequence getCharSequenceText() { return null; }
    public String getCountry() { return null; }
    public String getLanguage() { return null; }
    public Bundle getParams() { return null; }
    public int getPitch() { return 0; }
    public int getSpeechRate() { return 0; }
    public String getVariant() { return null; }
    public String getVoiceName() { return null; }
}
