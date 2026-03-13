package android.telecom;
import android.os.Parcel;
import android.os.Parcelable;

public class VideoProfile implements Parcelable {
    public static final int QUALITY_DEFAULT = 0;
    public static final int QUALITY_HIGH = 0;
    public static final int QUALITY_LOW = 0;
    public static final int QUALITY_MEDIUM = 0;
    public static final int STATE_AUDIO_ONLY = 0;
    public static final int STATE_BIDIRECTIONAL = 0;
    public static final int STATE_PAUSED = 0;
    public static final int STATE_RX_ENABLED = 0;
    public static final int STATE_TX_ENABLED = 0;

    public VideoProfile(int p0) {}
    public VideoProfile(int p0, int p1) {}

    public int describeContents() { return 0; }
    public int getQuality() { return 0; }
    public int getVideoState() { return 0; }
    public static boolean isAudioOnly(int p0) { return false; }
    public static boolean isBidirectional(int p0) { return false; }
    public static boolean isPaused(int p0) { return false; }
    public static boolean isReceptionEnabled(int p0) { return false; }
    public static boolean isTransmissionEnabled(int p0) { return false; }
    public static boolean isVideo(int p0) { return false; }
    public static String videoStateToString(int p0) { return null; }
    public void writeToParcel(Parcel p0, int p1) {}
}
