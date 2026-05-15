// SPDX-License-Identifier: Apache-2.0
//
// Westlake M4b -- compile-time stub for android.view.IWindowSessionCallback.
// Real interface lives in framework.jar; this shim is stripped via
// framework_duplicates.txt so the framework.jar Stub wins at runtime.
//
// Used only as a parameter type in IWindowManager.openSession.

package android.view;

import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteException;

public interface IWindowSessionCallback extends IInterface {
    void onAnimatorScaleChanged(float scale) throws RemoteException;

    public static abstract class Stub extends android.os.Binder implements IWindowSessionCallback {
        private static final String DESCRIPTOR = "android.view.IWindowSessionCallback";
        public Stub() { attachInterface(this, DESCRIPTOR); }
        public static IWindowSessionCallback asInterface(IBinder obj) {
            if (obj == null) return null;
            IInterface i = obj.queryLocalInterface(DESCRIPTOR);
            return (i instanceof IWindowSessionCallback)
                    ? (IWindowSessionCallback) i : null;
        }
        @Override public IBinder asBinder() { return this; }
    }
}
