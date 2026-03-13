package android.database;

/**
 * Android-compatible DataSetObservable shim.
 * Notifies a list of {@link DataSetObserver} objects when data set changes occur.
 */
public class DataSetObservable extends Observable<DataSetObserver> {

    /**
     * Notifies all observers that the data has changed.
     * Unlike {@link #notifyInvalidated()}, no assumption is made about whether
     * the old data is still valid.
     */
    public void notifyChanged() {
        synchronized (mObservers) {
            for (int i = mObservers.size() - 1; i >= 0; i--) {
                mObservers.get(i).onChanged();
            }
        }
    }

    /**
     * Notifies all observers that the data is no longer valid, e.g. because
     * the backing cursor was closed.
     */
    public void notifyInvalidated() {
        synchronized (mObservers) {
            for (int i = mObservers.size() - 1; i >= 0; i--) {
                mObservers.get(i).onInvalidated();
            }
        }
    }
}
