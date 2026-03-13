package android.os;

public class ResultReceiver implements Parcelable {
    public ResultReceiver(Handler p0) {}

    public int describeContents() { return 0; }
    public void onReceiveResult(int p0, Bundle p1) {}
    public void send(int p0, Bundle p1) {}
    public void writeToParcel(Parcel p0, int p1) {}
}
