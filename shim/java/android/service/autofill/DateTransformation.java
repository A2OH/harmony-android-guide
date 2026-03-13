package android.service.autofill;
import android.icu.text.DateFormat;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.autofill.AutofillId;
import android.icu.text.DateFormat;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.autofill.AutofillId;

public final class DateTransformation implements Parcelable, Transformation {
    public DateTransformation(AutofillId p0, DateFormat p1) {}

    public int describeContents() { return 0; }
    public void writeToParcel(Parcel p0, int p1) {}
}
