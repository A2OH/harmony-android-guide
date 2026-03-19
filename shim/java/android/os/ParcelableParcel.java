package android.os;

/** Stub for android.os.ParcelableParcel. */
public class ParcelableParcel implements Parcelable {
    private Parcel mParcel;
    private ClassLoader mClassLoader;

    public ParcelableParcel(ClassLoader loader) {
        mClassLoader = loader;
        mParcel = Parcel.obtain();
    }

    public ParcelableParcel() {
        this(null);
    }

    public Parcel getParcel() { return mParcel; }
    public ClassLoader getClassLoader() { return mClassLoader; }

    public void writeToParcel(Parcel out, int flags) {}
    public int describeContents() { return 0; }

    public static final Parcelable.Creator<ParcelableParcel> CREATOR =
        new Parcelable.Creator<ParcelableParcel>() {
            public ParcelableParcel createFromParcel(Parcel in) { return new ParcelableParcel(); }
            public ParcelableParcel[] newArray(int size) { return new ParcelableParcel[size]; }
        };
}
