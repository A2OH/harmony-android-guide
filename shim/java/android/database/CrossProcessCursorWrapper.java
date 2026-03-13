package android.database;

/**
 * Android-compatible CrossProcessCursorWrapper shim.
 * Wraps a {@link Cursor} and also implements {@link CrossProcessCursor}, enabling
 * the wrapped cursor to be used across process boundaries via Binder IPC.
 * If the wrapped cursor itself implements CrossProcessCursor its window and onMove
 * implementations are delegated; otherwise stub implementations are provided.
 */
public class CrossProcessCursorWrapper extends CursorWrapper implements CrossProcessCursor {

    public CrossProcessCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    /**
     * {@inheritDoc}
     * Delegates to the wrapped cursor if it is a {@link CrossProcessCursor},
     * otherwise fills the window by iterating rows (stub).
     */
    @Override
    public void fillWindow(int position, Object window) {
        if (mCursor instanceof CrossProcessCursor) {
            ((CrossProcessCursor) mCursor).fillWindow(position, window);
        }
        // stub: no-op when the wrapped cursor does not support windows
    }

    /**
     * {@inheritDoc}
     * Returns the cursor window from the wrapped cursor if it is a
     * {@link CrossProcessCursor}, otherwise returns null.
     */
    @Override
    public Object getWindow() {
        if (mCursor instanceof CrossProcessCursor) {
            return ((CrossProcessCursor) mCursor).getWindow();
        }
        return null;
    }

    /**
     * {@inheritDoc}
     * Delegates to the wrapped cursor if it is a {@link CrossProcessCursor},
     * otherwise always returns true.
     */
    @Override
    public boolean onMove(int oldPosition, int newPosition) {
        if (mCursor instanceof CrossProcessCursor) {
            return ((CrossProcessCursor) mCursor).onMove(oldPosition, newPosition);
        }
        return true;
    }
}
