package android.app.admin;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcel;
import android.os.Parcelable;
import java.net.InetAddress;

public final class ConnectEvent extends NetworkEvent implements Parcelable {
    public ConnectEvent() {}

    public InetAddress getInetAddress() { return null; }
    public int getPort() { return 0; }
    public void writeToParcel(Parcel p0, int p1) {}
}
