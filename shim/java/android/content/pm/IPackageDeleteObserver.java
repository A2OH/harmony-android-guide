// SPDX-License-Identifier: Apache-2.0
//
// Westlake M4-PRE5 -- compile-time stub for android.content.pm.IPackageDeleteObserver
// (AOSP-hidden; stripped from aosp-shim.dex via framework_duplicates.txt).
// Real interface lives in framework.jar.
//
// Referenced by PackageManager abstract methods:
//   deletePackage(String, IPackageDeleteObserver, int)
//   deletePackageAsUser(String, IPackageDeleteObserver, int, int)

package android.content.pm;

import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteException;

public interface IPackageDeleteObserver extends IInterface {
    public static abstract class Stub extends android.os.Binder implements IPackageDeleteObserver {
        private static final String DESCRIPTOR = "android.content.pm.IPackageDeleteObserver";
        public Stub() { attachInterface(this, DESCRIPTOR); }
        public static IPackageDeleteObserver asInterface(IBinder obj) {
            if (obj == null) return null;
            IInterface i = obj.queryLocalInterface(DESCRIPTOR);
            return (i instanceof IPackageDeleteObserver) ? (IPackageDeleteObserver) i : null;
        }
        @Override public IBinder asBinder() { return this; }
    }
}
