package android.media.midi;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

public final class MidiDeviceInfo implements Parcelable {
    public static final int PROPERTY_BLUETOOTH_DEVICE = 0;
    public static final int PROPERTY_MANUFACTURER = 0;
    public static final int PROPERTY_NAME = 0;
    public static final int PROPERTY_PRODUCT = 0;
    public static final int PROPERTY_SERIAL_NUMBER = 0;
    public static final int PROPERTY_USB_DEVICE = 0;
    public static final int PROPERTY_VERSION = 0;
    public static final int TYPE_BLUETOOTH = 0;
    public static final int TYPE_USB = 0;
    public static final int TYPE_VIRTUAL = 0;

    public MidiDeviceInfo() {}

    public int describeContents() { return 0; }
    public int getId() { return 0; }
    public int getInputPortCount() { return 0; }
    public int getOutputPortCount() { return 0; }
    public Object[] getPorts() { return null; }
    public Bundle getProperties() { return null; }
    public int getType() { return 0; }
    public boolean isPrivate() { return false; }
    public void writeToParcel(Parcel p0, int p1) {}
}
