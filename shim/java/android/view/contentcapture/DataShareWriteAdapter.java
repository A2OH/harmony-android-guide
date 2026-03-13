package android.view.contentcapture;

public interface DataShareWriteAdapter {

    void onError(Object p0);
    void onRejected();
    void onWrite(Object p0);
}
