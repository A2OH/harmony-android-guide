package android.view;

/**
 * Android-compatible AbsSavedState shim.
 * Base class for saved view state.
 */
public abstract class AbsSavedState {

    public static final AbsSavedState EMPTY_STATE = new AbsSavedState() {};

    private final Object mSuperState;

    protected AbsSavedState() {
        mSuperState = null;
    }

    protected AbsSavedState(Object superState) {
        if (superState == null) {
            throw new IllegalArgumentException("superState must not be null");
        }
        mSuperState = superState == EMPTY_STATE ? null : superState;
    }

    public final Object getSuperState() {
        return mSuperState == null ? EMPTY_STATE : mSuperState;
    }
}
