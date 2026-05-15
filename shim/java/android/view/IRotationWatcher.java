// SPDX-License-Identifier: Apache-2.0
//
// Westlake M4b -- compile-time stub for android.view.IRotationWatcher.
// Real interface lives in framework.jar; this shim is stripped via
// framework_duplicates.txt so the framework.jar Stub wins at runtime.

package android.view;

import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteException;

public interface IRotationWatcher extends IInterface {
    void onRotationChanged(int rotation) throws RemoteException;

    public static abstract class Stub extends android.os.Binder implements IRotationWatcher {
        private static final String DESCRIPTOR = "android.view.IRotationWatcher";
        public Stub() { attachInterface(this, DESCRIPTOR); }
        public static IRotationWatcher asInterface(IBinder obj) {
            if (obj == null) return null;
            IInterface i = obj.queryLocalInterface(DESCRIPTOR);
            return (i instanceof IRotationWatcher) ? (IRotationWatcher) i : null;
        }
        @Override public IBinder asBinder() { return this; }
    }
}
