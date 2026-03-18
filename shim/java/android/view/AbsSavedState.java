package android.view;

/**
 * Android-compatible AbsSavedState shim.
 * Base class for saved view state.
 */
public class AbsSavedState implements android.os.Parcelable {

    public static final AbsSavedState EMPTY_STATE = new AbsSavedState() {};

    private final android.os.Parcelable mSuperState;

    protected AbsSavedState() {
        mSuperState = null;
    }

    protected AbsSavedState(android.os.Parcelable superState) {
        if (superState == null) {
            throw new IllegalArgumentException("superState must not be null");
        }
        mSuperState = superState == EMPTY_STATE ? null : superState;
    }

    protected AbsSavedState(android.os.Parcel source) {
        mSuperState = null;
    }

    protected AbsSavedState(android.os.Parcel source, ClassLoader loader) {
        mSuperState = null;
    }

    public final android.os.Parcelable getSuperState() {
        return mSuperState == null ? EMPTY_STATE : mSuperState;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(android.os.Parcel dest, int flags) {}
}
