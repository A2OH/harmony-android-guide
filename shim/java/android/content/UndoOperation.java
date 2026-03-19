package android.content;

import android.os.Parcel;

/**
 * Stub for android.content.UndoOperation.
 * Generic base class for undo operations.
 */
public abstract class UndoOperation<DATA> {
    public UndoOperation() {}
    public UndoOperation(UndoOwner owner) {}
    public UndoOperation(Parcel src, ClassLoader loader) {}

    public UndoOwner getOwner() { return null; }
    @SuppressWarnings("unchecked")
    public DATA getOwnerData() { return null; }

    public abstract void commit();
    public abstract void undo();
    public abstract void redo();

    public void writeToParcel(Parcel dest, int flags) {}

    public boolean allowMerge() { return true; }
}
