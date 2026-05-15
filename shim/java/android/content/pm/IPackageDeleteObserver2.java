// SPDX-License-Identifier: Apache-2.0
//
// Westlake M4c -- compile-time stub for android.content.pm.IPackageDeleteObserver2
// (AOSP-hidden; stripped from aosp-shim.dex via framework_duplicates.txt).
// Real interface lives in framework.jar.

package android.content.pm;

import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteException;

public interface IPackageDeleteObserver2 extends IInterface {
    public static abstract class Stub extends android.os.Binder implements IPackageDeleteObserver2 {
        private static final String DESCRIPTOR = "android.content.pm.IPackageDeleteObserver2";
        public Stub() { attachInterface(this, DESCRIPTOR); }
        public static IPackageDeleteObserver2 asInterface(IBinder obj) {
            if (obj == null) return null;
            IInterface i = obj.queryLocalInterface(DESCRIPTOR);
            return (i instanceof IPackageDeleteObserver2) ? (IPackageDeleteObserver2) i : null;
        }
        @Override public IBinder asBinder() { return this; }
    }
}
