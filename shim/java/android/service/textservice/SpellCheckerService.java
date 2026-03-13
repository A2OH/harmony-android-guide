package android.service.textservice;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.se.omapi.Session;
import android.view.textservice.SentenceSuggestionsInfo;
import android.view.textservice.SuggestionsInfo;
import android.view.textservice.TextInfo;

public class SpellCheckerService extends Service {
    public static final int SERVICE_INTERFACE = 0;

    public SpellCheckerService() {}

    public Session createSession() { return null; }
    public IBinder onBind(Intent p0) { return null; }
    public Bundle getBundle() { return null; }
    public String getLocale() { return null; }
    public void onCancel() {}
    public void onClose() {}
    public void onCreate() {}
    public SentenceSuggestionsInfo[] onGetSentenceSuggestionsMultiple(TextInfo[] p0, int p1) { return null; }
    public SuggestionsInfo onGetSuggestions(TextInfo p0, int p1) { return null; }
    public SuggestionsInfo[] onGetSuggestionsMultiple(TextInfo[] p0, int p1, boolean p2) { return null; }
}
