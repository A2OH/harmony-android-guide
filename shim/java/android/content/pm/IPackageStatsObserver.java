// SPDX-License-Identifier: Apache-2.0
//
// Westlake M4-PRE5 -- compile-time stub for android.content.pm.IPackageStatsObserver
// (AOSP-hidden; stripped from aosp-shim.dex via framework_duplicates.txt).
// Real interface lives in framework.jar.
//
// Referenced by PackageManager abstract methods:
//   getPackageSizeInfoAsUser(String, int, IPackageStatsObserver)

package android.content.pm;

import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteException;

public interface IPackageStatsObserver extends IInterface {
    public static abstract class Stub extends android.os.Binder implements IPackageStatsObserver {
        private static final String DESCRIPTOR = "android.content.pm.IPackageStatsObserver";
        public Stub() { attachInterface(this, DESCRIPTOR); }
        public static IPackageStatsObserver asInterface(IBinder obj) {
            if (obj == null) return null;
            IInterface i = obj.queryLocalInterface(DESCRIPTOR);
            return (i instanceof IPackageStatsObserver) ? (IPackageStatsObserver) i : null;
        }
        @Override public IBinder asBinder() { return this; }
    }
}
