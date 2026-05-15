// SPDX-License-Identifier: Apache-2.0
//
// Westlake M4c -- compile-time stub for android.content.pm.IPackageMoveObserver
// (AOSP-hidden; stripped from aosp-shim.dex via framework_duplicates.txt).
// Real interface lives in framework.jar.

package android.content.pm;

import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteException;

public interface IPackageMoveObserver extends IInterface {
    public static abstract class Stub extends android.os.Binder implements IPackageMoveObserver {
        private static final String DESCRIPTOR = "android.content.pm.IPackageMoveObserver";
        public Stub() { attachInterface(this, DESCRIPTOR); }
        public static IPackageMoveObserver asInterface(IBinder obj) {
            if (obj == null) return null;
            IInterface i = obj.queryLocalInterface(DESCRIPTOR);
            return (i instanceof IPackageMoveObserver) ? (IPackageMoveObserver) i : null;
        }
        @Override public IBinder asBinder() { return this; }
    }
}
