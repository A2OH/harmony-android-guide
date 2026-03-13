package android.content;

import java.util.ArrayList;

/**
 * Android-compatible Entity shim.
 * Represents a content-provider entity: a primary {@link ContentValues} row
 * plus zero or more sub-values (e.g. related rows from a joined table).
 */
public final class Entity {

    /**
     * A single named sub-value associated with an entity.
     */
    public static final class NamedContentValues {
        /** The URI that identifies where the sub-values belong. */
        public final Object uri;
        /** The sub-values themselves. */
        public final ContentValues values;

        public NamedContentValues(Object uri, ContentValues values) {
            this.uri    = uri;
            this.values = values;
        }
    }

    private final ContentValues mValues;
    private final ArrayList<NamedContentValues> mSubValues;

    /**
     * Construct an Entity with the given primary values.
     *
     * @param values the primary row data; must not be null
     */
    public Entity(ContentValues values) {
        if (values == null) throw new IllegalArgumentException("values must not be null");
        mValues    = values;
        mSubValues = new ArrayList<>();
    }

    /**
     * Return the primary {@link ContentValues} for this entity.
     */
    public ContentValues getEntityValues() {
        return mValues;
    }

    /**
     * Return the list of sub-values attached to this entity.
     * The list is mutable — callers may add or remove entries.
     */
    public ArrayList<NamedContentValues> getSubValues() {
        return mSubValues;
    }

    /**
     * Convenience method to append a single sub-value to this entity.
     *
     * @param uri    the URI identifying the sub-value's origin
     * @param values the sub-value data
     */
    public void addSubValue(Object uri, ContentValues values) {
        mSubValues.add(new NamedContentValues(uri, values));
    }

    @Override
    public String toString() {
        return "Entity(values=" + mValues + ", subValues=" + mSubValues.size() + ")";
    }
}
