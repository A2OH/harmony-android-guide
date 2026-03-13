package android.telecom;

/**
 * Android-compatible PhoneAccountHandle shim. Stub implementation for mock testing.
 */
public class PhoneAccountHandle {
    private final Object mComponentName; // ComponentName (stub as Object)
    private final String mId;

    /**
     * Constructs a PhoneAccountHandle.
     * @param componentName the ComponentName (passed as Object in shim)
     * @param id            unique identifier within the component
     */
    public PhoneAccountHandle(Object componentName, String id) {
        mComponentName = componentName;
        mId = id;
    }

    /** Returns the ComponentName associated with this handle (returned as Object). */
    public Object getComponentName() {
        return mComponentName;
    }

    /** Returns the identifier string for this handle. */
    public String getId() {
        return mId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PhoneAccountHandle)) return false;
        PhoneAccountHandle other = (PhoneAccountHandle) o;
        boolean componentEquals = (mComponentName == null)
                ? other.mComponentName == null
                : mComponentName.equals(other.mComponentName);
        boolean idEquals = (mId == null) ? other.mId == null : mId.equals(other.mId);
        return componentEquals && idEquals;
    }

    @Override
    public int hashCode() {
        int result = (mComponentName != null ? mComponentName.hashCode() : 0);
        result = 31 * result + (mId != null ? mId.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "PhoneAccountHandle{component=" + mComponentName + ", id=" + mId + "}";
    }
}
