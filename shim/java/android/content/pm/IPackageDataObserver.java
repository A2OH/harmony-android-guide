// SPDX-License-Identifier: Apache-2.0
//
// Westlake M4a -- compile-time stub for android.content.pm.IPackageDataObserver
// (AOSP-hidden; stripped from aosp-shim.dex via framework_duplicates.txt).
// Real interface lives in framework.jar.

package android.content.pm;

import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteException;

public interface IPackageDataObserver extends IInterface {
    public static abstract class Stub extends android.os.Binder implements IPackageDataObserver {
        private static final String DESCRIPTOR = "android.content.pm.IPackageDataObserver";
        public Stub() { attachInterface(this, DESCRIPTOR); }
        public static IPackageDataObserver asInterface(IBinder obj) {
            if (obj == null) return null;
            IInterface i = obj.queryLocalInterface(DESCRIPTOR);
            return (i instanceof IPackageDataObserver) ? (IPackageDataObserver) i : null;
        }
        @Override public IBinder asBinder() { return this; }
    }
}
