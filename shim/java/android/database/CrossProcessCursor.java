package android.database;

/**
 * Android-compatible CrossProcessCursor shim.
 * A cross process cursor is an extension of a {@link Cursor} that also supports
 * filling a {@link android.database.CursorWindow} (represented here as Object for
 * portability) for transfer across process boundaries via Binder.
 */
public interface CrossProcessCursor extends Cursor {

    /**
     * Returns the cursor window owned by this cursor, if any.
     * The cursor window is typically a shared-memory region used for IPC.
     *
     * @return the cursor window, or null if none
     */
    Object getWindow();

    /**
     * Copies cursor data starting at the requested row position into the given window.
     *
     * @param position the zero-based start position for filling the window
     * @param window   the CursorWindow to fill (typed as Object for portability)
     */
    void fillWindow(int position, Object window);

    /**
     * Called when the cursor moves from one row to another. Implementations may
     * use this to do lightweight work such as updating state.
     *
     * @param oldPosition the previous cursor position
     * @param newPosition the new cursor position
     * @return true if the move is successful, false otherwise
     */
    boolean onMove(int oldPosition, int newPosition);
}
