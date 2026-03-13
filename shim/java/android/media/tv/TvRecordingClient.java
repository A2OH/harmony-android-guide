package android.media.tv;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;

public class TvRecordingClient {
    public TvRecordingClient(Context p0, String p1, Object p2, Handler p3) {}

    public void release() {}
    public void sendAppPrivateCommand(String p0, Bundle p1) {}
    public void startRecording(Uri p0) {}
    public void startRecording(Uri p0, Bundle p1) {}
    public void stopRecording() {}
    public void tune(String p0, Uri p1) {}
    public void tune(String p0, Uri p1, Bundle p2) {}
    public void onConnectionFailed(String p0) {}
    public void onDisconnected(String p0) {}
    public void onError(int p0) {}
    public void onRecordingStopped(Uri p0) {}
    public void onTuned(Uri p0) {}
}
