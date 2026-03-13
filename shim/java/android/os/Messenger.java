package android.os;

public final class Messenger implements Parcelable {
    public Messenger(Handler p0) {}
    public Messenger(IBinder p0) {}

    public int describeContents() { return 0; }
    public IBinder getBinder() { return null; }
    public static Messenger readMessengerOrNullFromParcel(Parcel p0) { return null; }
    public void send(Message p0) {}
    public static void writeMessengerOrNullToParcel(Messenger p0, Parcel p1) {}
    public void writeToParcel(Parcel p0, int p1) {}
}
