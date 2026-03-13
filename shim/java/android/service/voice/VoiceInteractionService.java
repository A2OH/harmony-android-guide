package android.service.voice;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import java.util.Locale;

public class VoiceInteractionService extends Service {
    public static final int SERVICE_INTERFACE = 0;
    public static final int SERVICE_META_DATA = 0;

    public VoiceInteractionService() {}

    public AlwaysOnHotwordDetector createAlwaysOnHotwordDetector(String p0, Locale p1, Object p2) { return null; }
    public int getDisabledShowContext() { return 0; }
    public static boolean isActiveService(Context p0, ComponentName p1) { return false; }
    public IBinder onBind(Intent p0) { return null; }
    public void onLaunchVoiceAssistFromKeyguard() {}
    public void onReady() {}
    public void onShutdown() {}
    public void setDisabledShowContext(int p0) {}
    public void setUiHints(Bundle p0) {}
    public void showSession(Bundle p0, int p1) {}
}
