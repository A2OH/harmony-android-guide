package android.webkit;
import android.net.Uri;
import android.net.Uri;

public class PermissionRequest {
    public static final int RESOURCE_AUDIO_CAPTURE = 0;
    public static final int RESOURCE_MIDI_SYSEX = 0;
    public static final int RESOURCE_PROTECTED_MEDIA_ID = 0;
    public static final int RESOURCE_VIDEO_CAPTURE = 0;

    public PermissionRequest() {}

    public  void deny() { return; }
    public Uri getOrigin() { return null; }
    public  String[] getResources() { return null; }
    public  void grant(String[] p0) { return; }
}
