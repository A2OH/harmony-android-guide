package android.telecom;
import android.bluetooth.BluetoothDevice;
import android.os.Parcel;
import android.os.Parcelable;
import android.bluetooth.BluetoothDevice;
import android.os.Parcel;
import android.os.Parcelable;
import java.util.Collection;

public final class CallAudioState implements Parcelable {
    public static final int ROUTE_BLUETOOTH = 0;
    public static final int ROUTE_EARPIECE = 0;
    public static final int ROUTE_SPEAKER = 0;
    public static final int ROUTE_WIRED_HEADSET = 0;
    public static final int ROUTE_WIRED_OR_EARPIECE = 0;

    public CallAudioState(boolean p0, int p1, int p2) {}

    public static String audioRouteToString(int p0) { return null; }
    public int describeContents() { return 0; }
    public BluetoothDevice getActiveBluetoothDevice() { return null; }
    public int getRoute() { return 0; }
    public Collection<?> getSupportedBluetoothDevices() { return null; }
    public int getSupportedRouteMask() { return 0; }
    public boolean isMuted() { return false; }
    public void writeToParcel(Parcel p0, int p1) {}
}
