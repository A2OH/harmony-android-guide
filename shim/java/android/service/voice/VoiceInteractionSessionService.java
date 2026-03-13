package android.service.voice;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;

public class VoiceInteractionSessionService extends Service {
    public VoiceInteractionSessionService() {}

    public IBinder onBind(Intent p0) { return null; }
    public VoiceInteractionSession onNewSession(Bundle p0) { return null; }
}
