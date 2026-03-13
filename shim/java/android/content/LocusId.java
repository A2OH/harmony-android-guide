package android.content;

/**
 * Android-compatible LocusId shim (API 29+).
 *
 * An identifier for a unique state (locus) in the app. A locus is a
 * specific, identifiable task the user can perform, such as viewing
 * a particular conversation thread. The system uses LocusId to
 * correlate content across surfaces (shortcuts, notifications, etc.).
 *
 * Pure Java stub — no OH bridge calls required.
 */
public final class LocusId {

    private final String mId;

    /**
     * Create a new LocusId.
     * @param id unique identifier for this locus; must not be null or empty
     */
    public LocusId(String id) {
        if (id == null) throw new NullPointerException("id is null");
        if (id.isEmpty()) throw new IllegalArgumentException("id is empty");
        mId = id;
    }

    /**
     * Returns the identifier of this locus.
     */
    public String getId() {
        return mId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LocusId)) return false;
        return mId.equals(((LocusId) o).mId);
    }

    @Override
    public int hashCode() {
        return mId.hashCode();
    }

    @Override
    public String toString() {
        return "LocusId[" + mId + "]";
    }
}
