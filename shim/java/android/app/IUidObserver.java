// SPDX-License-Identifier: Apache-2.0
//
// Westlake M4a -- compile-time stub for android.app.IUidObserver
// (AOSP-hidden; stripped from aosp-shim.dex via framework_duplicates.txt).
// Real interface lives in framework.jar.

package android.app;

import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteException;

public interface IUidObserver extends IInterface {
    public static abstract class Stub extends android.os.Binder implements IUidObserver {
        private static final String DESCRIPTOR = "android.app.IUidObserver";
        public Stub() { attachInterface(this, DESCRIPTOR); }
        public static IUidObserver asInterface(IBinder obj) {
            if (obj == null) return null;
            IInterface i = obj.queryLocalInterface(DESCRIPTOR);
            return (i instanceof IUidObserver) ? (IUidObserver) i : null;
        }
        @Override public IBinder asBinder() { return this; }
    }
}
