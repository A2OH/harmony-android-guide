package com.android.internal.view;

import android.os.IBinder;

/**
 * Stub for com.android.internal.view.IDragAndDropPermissions AIDL interface.
 */
public interface IDragAndDropPermissions {
    public abstract static class Stub implements IDragAndDropPermissions {
        public static IDragAndDropPermissions asInterface(IBinder binder) {
            return null;
        }
        public IBinder asBinder() { return null; }
    }

    IBinder asBinder();
}
