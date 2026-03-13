package android.hardware.usb;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcel;
import android.os.Parcelable;

public class UsbConfiguration implements Parcelable {
    public UsbConfiguration() {}

    public int describeContents() { return 0; }
    public int getId() { return 0; }
    public int getInterfaceCount() { return 0; }
    public int getMaxPower() { return 0; }
    public boolean isRemoteWakeup() { return false; }
    public boolean isSelfPowered() { return false; }
    public void writeToParcel(Parcel p0, int p1) {}
}
