package android.media;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcel;
import android.os.Parcelable;

public final class AudioRecordingConfiguration implements Parcelable {
    public AudioRecordingConfiguration() {}

    public int describeContents() { return 0; }
    public AudioDeviceInfo getAudioDevice() { return null; }
    public int getAudioSource() { return 0; }
    public int getClientAudioSessionId() { return 0; }
    public int getClientAudioSource() { return 0; }
    public AudioFormat getClientFormat() { return null; }
    public AudioFormat getFormat() { return null; }
    public boolean isClientSilenced() { return false; }
    public void writeToParcel(Parcel p0, int p1) {}
}
