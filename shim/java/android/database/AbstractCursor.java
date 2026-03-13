package android.database;

import android.content.ContentResolver;
import android.net.Uri;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

/**
 * Android-compatible AbstractCursor shim. Pure Java stub.
 * Manages position, closed state, and self-notification via onChange().
 */
public abstract class AbstractCursor implements Cursor {

    /** Current row position; -1 = before first. */
    protected int mPos = -1;
    private boolean mClosed = false;
    private Uri mNotifyUri;
    private ContentResolver mContentResolver;

    private final List<ContentObserver> mContentObservers = new ArrayList<>();
    private final List<DataSetObserver>  mDataSetObservers  = new ArrayList<>();

    // ──────────────────────────────────────────────────────────
    // Abstract methods that concrete subclasses must implement
    // ──────────────────────────────────────────────────────────

    @Override
    public abstract int getCount();

    @Override
    public abstract String[] getColumnNames();

    @Override
    public abstract String  getString(int columnIndex);
    @Override
    public abstract short   getShort(int columnIndex);
    @Override
    public abstract int     getInt(int columnIndex);
    @Override
    public abstract long    getLong(int columnIndex);
    @Override
    public abstract float   getFloat(int columnIndex);
    @Override
    public abstract double  getDouble(int columnIndex);
    @Override
    public abstract boolean isNull(int columnIndex);

    // ──────────────────────────────────────────────────────────
    // Position management
    // ──────────────────────────────────────────────────────────

    @Override
    public final int getPosition() {
        return mPos;
    }

    @Override
    public final boolean isBeforeFirst() {
        if (getCount() == 0) return true;
        return mPos == -1;
    }

    @Override
    public final boolean isAfterLast() {
        if (getCount() == 0) return true;
        return mPos == getCount();
    }

    @Override
    public final boolean isFirst() {
        return mPos == 0 && getCount() != 0;
    }

    @Override
    public final boolean isLast() {
        int cnt = getCount();
        return mPos == (cnt - 1) && cnt != 0;
    }

    @Override
    public final boolean moveToFirst() {
        return moveToPosition(0);
    }

    @Override
    public final boolean moveToLast() {
        return moveToPosition(getCount() - 1);
    }

    @Override
    public final boolean moveToNext() {
        return moveToPosition(mPos + 1);
    }

    @Override
    public final boolean moveToPrevious() {
        return moveToPosition(mPos - 1);
    }

    @Override
    public boolean move(int offset) {
        return moveToPosition(mPos + offset);
    }

    @Override
    public boolean moveToPosition(int position) {
        final int count = getCount();
        if (position >= count) {
            mPos = count;
            return false;
        }
        if (position < 0) {
            mPos = -1;
            return false;
        }
        if (position == mPos) {
            return true;
        }
        mPos = position;
        return true;
    }

    // ──────────────────────────────────────────────────────────
    // Column helpers
    // ──────────────────────────────────────────────────────────

    @Override
    public int getColumnCount() {
        return getColumnNames().length;
    }

    @Override
    public int getColumnIndex(String columnName) {
        String[] names = getColumnNames();
        for (int i = 0; i < names.length; i++) {
            if (names[i].equals(columnName)) return i;
        }
        return -1;
    }

    @Override
    public int getColumnIndexOrThrow(String columnName) throws IllegalArgumentException {
        int idx = getColumnIndex(columnName);
        if (idx < 0) {
            throw new IllegalArgumentException("column '" + columnName + "' does not exist");
        }
        return idx;
    }

    @Override
    public String getColumnName(int columnIndex) {
        return getColumnNames()[columnIndex];
    }

    // ──────────────────────────────────────────────────────────
    // Default implementations
    // ──────────────────────────────────────────────────────────

    @Override
    public byte[] getBlob(int columnIndex) {
        String s = getString(columnIndex);
        return s == null ? null : s.getBytes();
    }

    @Override
    public int getType(int columnIndex) {
        return isNull(columnIndex) ? FIELD_TYPE_NULL : FIELD_TYPE_STRING;
    }

    @Override
    public boolean getWantsAllOnMoveCalls() {
        return false;
    }

    // ──────────────────────────────────────────────────────────
    // Close / lifecycle
    // ──────────────────────────────────────────────────────────

    @Override
    public void close() {
        mClosed = true;
        notifyDataSetInvalidated();
    }

    @Override
    public boolean isClosed() {
        return mClosed;
    }

    // ──────────────────────────────────────────────────────────
    // Observer registration
    // ──────────────────────────────────────────────────────────

    @Override
    public void registerContentObserver(ContentObserver observer) {
        synchronized (mContentObservers) {
            mContentObservers.add(observer);
        }
    }

    @Override
    public void unregisterContentObserver(ContentObserver observer) {
        synchronized (mContentObservers) {
            mContentObservers.remove(observer);
        }
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        synchronized (mDataSetObservers) {
            mDataSetObservers.add(observer);
        }
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        synchronized (mDataSetObservers) {
            mDataSetObservers.remove(observer);
        }
    }

    // ──────────────────────────────────────────────────────────
    // Notification helpers
    // ──────────────────────────────────────────────────────────

    /** Notify registered ContentObservers that data changed. */
    protected void onChange(boolean selfChange) {
        synchronized (mContentObservers) {
            for (ContentObserver obs : mContentObservers) {
                obs.dispatchChange(selfChange, mNotifyUri);
            }
        }
        if (mContentResolver != null && mNotifyUri != null) {
            mContentResolver.notifyChange(mNotifyUri, null);
        }
    }

    protected void notifyDataSetChanged() {
        synchronized (mDataSetObservers) {
            for (DataSetObserver obs : mDataSetObservers) {
                obs.onChanged();
            }
        }
    }

    protected void notifyDataSetInvalidated() {
        synchronized (mDataSetObservers) {
            for (DataSetObserver obs : mDataSetObservers) {
                obs.onInvalidated();
            }
        }
    }

    // ──────────────────────────────────────────────────────────
    // Notification URI
    // ──────────────────────────────────────────────────────────

    @Override
    public void setNotificationUri(ContentResolver cr, Uri uri) {
        mContentResolver = cr;
        mNotifyUri = uri;
    }

    @Override
    public Uri getNotificationUri() {
        return mNotifyUri;
    }

    // ──────────────────────────────────────────────────────────
    // Extras (stub)
    // ──────────────────────────────────────────────────────────

    @Override
    public void setExtras(Bundle extras) {
        // stub
    }

    @Override
    public Bundle getExtras() {
        return Bundle.EMPTY;
    }

    @Override
    public Bundle respond(Bundle extras) {
        return Bundle.EMPTY;
    }
}
