package android.os;

public final class Message implements Parcelable {
    public int arg1 = 0;
    public int arg2 = 0;
    public int obj = 0;
    public int replyTo = 0;
    public int sendingUid = 0;
    public int what = 0;

    public Message() {}

    public void copyFrom(Message p0) {}
    public int describeContents() { return 0; }
    public Runnable getCallback() { return null; }
    public Bundle getData() { return null; }
    public Handler getTarget() { return null; }
    public long getWhen() { return 0L; }
    public boolean isAsynchronous() { return false; }
    public static Message obtain() { return null; }
    public static Message obtain(Message p0) { return null; }
    public static Message obtain(Handler p0, Runnable p1) { return null; }
    public static Message obtain(Handler p0, int p1, Object p2) { return null; }
    public static Message obtain(Handler p0, int p1, int p2, int p3) { return null; }
    public static Message obtain(Handler p0, int p1, int p2, int p3, Object p4) { return null; }
    public Bundle peekData() { return null; }
    public void recycle() {}
    public void sendToTarget() {}
    public void setAsynchronous(boolean p0) {}
    public void setData(Bundle p0) {}
    public void setTarget(Handler p0) {}
    public void writeToParcel(Parcel p0, int p1) {}
}
