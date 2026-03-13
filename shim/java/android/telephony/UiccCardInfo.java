package android.telephony;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcel;
import android.os.Parcelable;

public final class UiccCardInfo implements Parcelable {
    public UiccCardInfo() {}

    public int describeContents() { return 0; }
    public int getCardId() { return 0; }
    public int getSlotIndex() { return 0; }
    public boolean isEuicc() { return false; }
    public boolean isRemovable() { return false; }
    public void writeToParcel(Parcel p0, int p1) {}
}
