package android.os;

/**
 * Shim: android.os.Parcelable — interface for classes whose instances
 * can be written to and restored from a Parcel.
 */
public interface Parcelable {

    /** Flag for use with {@link #writeToParcel}: the object being written is a return value. */
    int PARCELABLE_WRITE_RETURN_VALUE = 0x0001;

    /** Bitmask for use with {@link #describeContents()}: each bit represents a kind of special object. */
    int CONTENTS_FILE_DESCRIPTOR = 0x0001;

    /**
     * Describe the kinds of special objects contained in this Parcelable
     * instance's marshalled representation.
     */
    int describeContents();

    /**
     * Flatten this object into a Parcel.
     */
    void writeToParcel(Parcel dest, int flags);

    /**
     * Interface that must be implemented and provided as a public CREATOR
     * field that generates instances of your Parcelable class from a Parcel.
     */
    public interface Creator<Object> {
        /**
         * Create a new instance of the Parcelable class, instantiating it
         * from the given Parcel whose data had previously been written by
         * {@link Parcelable#writeToParcel}.
         */
        Object createFromParcel(Parcel source);

        /**
         * Create a new array of the Parcelable class.
         */
        Object[] newArray(int size);
    }

    /**
     * Specialization of {@link Creator} that allows you to receive the
     * ClassLoader the object is being created in.
     */
    public interface ClassLoaderCreator<Object> extends Creator<Object> {
        Object createFromParcel(Parcel source, ClassLoader loader);
    }
}
