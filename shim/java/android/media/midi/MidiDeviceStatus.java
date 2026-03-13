package android.media.midi;
import android.os.Parcel;
import android.os.Parcelable;

public final class MidiDeviceStatus implements Parcelable {
    public MidiDeviceStatus() {}

    public int describeContents() { return 0; }
    public MidiDeviceInfo getDeviceInfo() { return null; }
    public int getOutputPortOpenCount(int p0) { return 0; }
    public boolean isInputPortOpen(int p0) { return false; }
    public void writeToParcel(Parcel p0, int p1) {}
}
