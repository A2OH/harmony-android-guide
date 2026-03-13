package android.net.wifi;
import android.os.Parcel;

public enum SupplicantState {
    ASSOCIATED,
    ASSOCIATING,
    AUTHENTICATING,
    COMPLETED,
    DISCONNECTED,
    DORMANT,
    FOUR_WAY_HANDSHAKE,
    GROUP_HANDSHAKE,
    INACTIVE,
    INTERFACE_DISABLED,
    INVALID,
    SCANNING,
    UNINITIALIZED;
    public int describeContents() { return 0; }
    public static boolean isValidState(SupplicantState p0) { return false; }
    public void writeToParcel(Parcel p0, int p1) {}
}
