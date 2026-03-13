package android.net.wifi;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcel;
import android.os.Parcelable;

public final class WifiNetworkSuggestion implements Parcelable {
    public WifiNetworkSuggestion() {}

    public int describeContents() { return 0; }
    public boolean isAppInteractionRequired() { return false; }
    public boolean isCredentialSharedWithUser() { return false; }
    public boolean isEnhancedOpen() { return false; }
    public boolean isHiddenSsid() { return false; }
    public boolean isInitialAutojoinEnabled() { return false; }
    public boolean isMetered() { return false; }
    public boolean isUntrusted() { return false; }
    public boolean isUserInteractionRequired() { return false; }
    public void writeToParcel(Parcel p0, int p1) {}
}
