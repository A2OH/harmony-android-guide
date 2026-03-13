package android.media;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcel;
import android.os.Parcelable;

public final class MediaRoute2Info implements Parcelable {
    public static final int CONNECTION_STATE_CONNECTED = 0;
    public static final int CONNECTION_STATE_CONNECTING = 0;
    public static final int CONNECTION_STATE_DISCONNECTED = 0;
    public static final int FEATURE_LIVE_AUDIO = 0;
    public static final int FEATURE_LIVE_VIDEO = 0;
    public static final int FEATURE_REMOTE_AUDIO_PLAYBACK = 0;
    public static final int FEATURE_REMOTE_PLAYBACK = 0;
    public static final int FEATURE_REMOTE_VIDEO_PLAYBACK = 0;
    public static final int PLAYBACK_VOLUME_FIXED = 0;
    public static final int PLAYBACK_VOLUME_VARIABLE = 0;

    public MediaRoute2Info() {}

    public int describeContents() { return 0; }
    public int getConnectionState() { return 0; }
    public int getVolume() { return 0; }
    public int getVolumeHandling() { return 0; }
    public int getVolumeMax() { return 0; }
    public boolean isSystemRoute() { return false; }
    public void writeToParcel(Parcel p0, int p1) {}
}
