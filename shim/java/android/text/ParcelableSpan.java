package android.text;
import android.os.Parcel;
import android.os.Parcel;

/**
 * Shim: android.text.ParcelableSpan
 *
 * A span that is capable of being written to and restored from a Parcel.
 * On OpenHarmony there is no Android Parcel IPC layer, so this interface is
 * provided purely for source-level compatibility.
 *
 * Implementations only need to provide a numeric span type identifier via
 * {@link #getSpanTypeId()}; the serialisation methods are stubs because the
 * shim has no functioning Parcel.
 *
 * Bridge delegation: none required — this is a marker/typing interface.
 */
public interface ParcelableSpan {

    /**
     * Returns a unique identifier for this span type, used when parcelling.
     * Values mirror Android's {@code android.text.TextUtils} span-type table.
     *
     * @return an integer type tag for this span
     */
    int getSpanTypeId();

    // writeToParcel / describeContents are intentionally omitted because the
    // shim does not have an android.os.Parcel implementation that would make
    // them useful.  Code that genuinely needs parcelling should use the
    // android.os.Parcel shim directly.
}
