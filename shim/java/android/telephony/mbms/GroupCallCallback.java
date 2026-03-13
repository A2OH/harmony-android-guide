package android.telephony.mbms;

public interface GroupCallCallback {
    void onBroadcastSignalStrengthUpdated(int p1);
    void onError(int p0, String p1);
    void onGroupCallStateChanged(int p0, int p1);
}
