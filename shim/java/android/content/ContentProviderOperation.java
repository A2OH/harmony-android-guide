package android.content;

import android.net.Uri;

import java.util.ArrayList;

/**
 * Android-compatible ContentProviderOperation shim. Stub implementation
 * with Builder pattern for newInsert/newUpdate/newDelete/newAssertQuery.
 */
public class ContentProviderOperation {

    private static final int TYPE_INSERT = 1;
    private static final int TYPE_UPDATE = 2;
    private static final int TYPE_DELETE = 3;
    private static final int TYPE_ASSERT = 4;

    private final int mType;
    private final Uri mUri;
    private final ContentValues mValues;
    private final String mSelection;
    private final String[] mSelectionArgs;

    private ContentProviderOperation(Builder builder) {
        mType = builder.mType;
        mUri = builder.mUri;
        mValues = builder.mValues;
        mSelection = builder.mSelection;
        mSelectionArgs = builder.mSelectionArgs;
    }

    public Uri getUri() {
        return mUri;
    }

    public boolean isInsert() {
        return mType == TYPE_INSERT;
    }

    public boolean isUpdate() {
        return mType == TYPE_UPDATE;
    }

    public boolean isDelete() {
        return mType == TYPE_DELETE;
    }

    public boolean isAssertQuery() {
        return mType == TYPE_ASSERT;
    }

    /**
     * Apply this operation to the given provider. Stub -- returns empty result.
     */
    public ContentProviderResult apply(ContentProvider provider) {
        switch (mType) {
            case TYPE_INSERT:
                Uri result = provider.insert(mUri, mValues);
                return new ContentProviderResult(result != null ? result : mUri);
            case TYPE_UPDATE:
                int updated = provider.update(mUri, mValues, mSelection, mSelectionArgs);
                return new ContentProviderResult(updated);
            case TYPE_DELETE:
                int deleted = provider.delete(mUri, mSelection, mSelectionArgs);
                return new ContentProviderResult(deleted);
            case TYPE_ASSERT:
                return new ContentProviderResult(0);
            default:
                return new ContentProviderResult(0);
        }
    }

    public static Builder newInsert(Uri uri) {
        return new Builder(TYPE_INSERT, uri);
    }

    public static Builder newUpdate(Uri uri) {
        return new Builder(TYPE_UPDATE, uri);
    }

    public static Builder newDelete(Uri uri) {
        return new Builder(TYPE_DELETE, uri);
    }

    public static Builder newAssertQuery(Uri uri) {
        return new Builder(TYPE_ASSERT, uri);
    }

    /**
     * Builder for ContentProviderOperation.
     */
    public static class Builder {
        private final int mType;
        private final Uri mUri;
        private ContentValues mValues;
        private String mSelection;
        private String[] mSelectionArgs;
        private Integer mExpectedCount;

        private Builder(int type, Uri uri) {
            mType = type;
            mUri = uri;
            mValues = new ContentValues();
        }

        public Builder withValues(ContentValues values) {
            mValues = new ContentValues(values);
            return this;
        }

        public Builder withValue(String key, Object value) {
            if (value == null) {
                mValues.putNull(key);
            } else if (value instanceof String) {
                mValues.put(key, (String) value);
            } else if (value instanceof Integer) {
                mValues.put(key, (Integer) value);
            } else if (value instanceof Long) {
                mValues.put(key, (Long) value);
            } else if (value instanceof Float) {
                mValues.put(key, (Float) value);
            } else if (value instanceof Double) {
                mValues.put(key, (Double) value);
            } else if (value instanceof Boolean) {
                mValues.put(key, (Boolean) value);
            } else if (value instanceof byte[]) {
                mValues.put(key, (byte[]) value);
            }
            return this;
        }

        public Builder withSelection(String selection, String[] selectionArgs) {
            mSelection = selection;
            mSelectionArgs = selectionArgs;
            return this;
        }

        public Builder withExpectedCount(int count) {
            mExpectedCount = count;
            return this;
        }

        public Builder withValueBackReference(String key, int previousResult) {
            // stub -- back references not implemented
            return this;
        }

        public Builder withSelectionBackReference(int selectionArgIndex, int previousResult) {
            // stub
            return this;
        }

        public ContentProviderOperation build() {
            return new ContentProviderOperation(this);
        }
    }
}
