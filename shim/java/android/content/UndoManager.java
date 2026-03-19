package android.content;

import android.os.Parcel;

/**
 * Stub for android.content.UndoManager.
 * Manages undo/redo operations for text editing.
 */
public class UndoManager {

    public static final int MERGE_MODE_NONE = 0;
    public static final int MERGE_MODE_UNIQUE = 1;
    public static final int MERGE_MODE_ANY = 2;

    public UndoManager() {}

    public UndoOwner getOwner(String tag, Object data) { return new UndoOwner(); }
    public void saveInstanceState(Parcel p) {}
    public void restoreInstanceState(Parcel p, ClassLoader loader) {}
    public void forgetUndos(UndoOwner[] owners, int count) {}
    public void forgetRedos(UndoOwner[] owners, int count) {}
    public int countUndos(UndoOwner[] owners) { return 0; }
    public int countRedos(UndoOwner[] owners) { return 0; }
    public void undo(UndoOwner[] owners, int count) {}
    public void redo(UndoOwner[] owners, int count) {}
    public boolean isInUndo() { return false; }
    public void beginUpdate(String label) {}
    public void endUpdate() {}
    public void commitState(UndoOwner owner) {}

    public <T extends UndoOperation> T getLastOperation(Class<T> clazz, UndoOwner owner, int mergeMode) {
        return null;
    }

    public void addOperation(UndoOperation op, int mergeMode) {}
}
