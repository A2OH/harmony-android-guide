package android.view.autofill;

/**
 * Android-compatible AutofillId shim. Identifies a view for autofill purposes.
 */
public final class AutofillId {

    private final int mViewId;

    public AutofillId(int viewId) {
        mViewId = viewId;
    }

    public AutofillId(AutofillId hostId, long virtualChildId, int sessionId) {
        mViewId = hostId != null ? hostId.mViewId : 0;
    }

    public int getViewId() { return mViewId; }
    public boolean isNonVirtual() { return true; }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof AutofillId)) return false;
        return mViewId == ((AutofillId) obj).mViewId;
    }

    @Override
    public int hashCode() {
        return mViewId;
    }

    @Override
    public String toString() {
        return "AutofillId(" + mViewId + ")";
    }
}
