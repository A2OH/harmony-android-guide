package android.service.autofill;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.autofill.AutofillId;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.autofill.AutofillId;
import java.util.regex.Pattern;

public final class RegexValidator implements Parcelable, Validator {
    public RegexValidator(AutofillId p0, Pattern p1) {}

    public int describeContents() { return 0; }
    public void writeToParcel(Parcel p0, int p1) {}
}
