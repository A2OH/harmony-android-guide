package android.database.sqlite;
import java.io.Closeable;

/**
 * Android-compatible SQLiteClosable shim.
 * Abstract base class for objects that can be closed (released) once all
 * references to them are dropped. Tracks a simple reference count.
 */
public class SQLiteClosable implements java.io.Closeable {

    private int mReferenceCount = 1;

    /**
     * Called when the last reference to this object is released and the object
     * should be freed / cleaned up.
     */
    protected void onAllReferencesReleased() {}

    /**
     * Acquires a reference to this closable, preventing it from being released
     * until {@link #releaseReference()} is called a matching number of times.
     *
     * @throws IllegalStateException if the object has already been closed
     */
    public void acquireReference() {
        synchronized (this) {
            if (mReferenceCount <= 0) {
                throw new IllegalStateException(
                        "attempt to acquire a reference on a closed SQLiteClosable");
            }
            mReferenceCount++;
        }
    }

    /**
     * Releases a reference to this closable. If the reference count drops to
     * zero {@link #onAllReferencesReleased()} is called.
     */
    public void releaseReference() {
        boolean refCountIsZero;
        synchronized (this) {
            refCountIsZero = (--mReferenceCount == 0);
        }
        if (refCountIsZero) {
            onAllReferencesReleased();
        }
    }

    /**
     * Releases a reference to this object, equivalent to {@link #releaseReference()}.
     * Implements {@link java.io.Closeable}.
     */
    @Override
    public void close() {
        releaseReference();
    }
}
