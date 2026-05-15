// SPDX-License-Identifier: Apache-2.0
//
// Westlake M4c -- compile-time stub for android.content.pm.IPackageInstaller
// (AOSP-hidden; stripped from aosp-shim.dex via framework_duplicates.txt).
// Real interface lives in framework.jar.

package android.content.pm;

import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteException;

public interface IPackageInstaller extends IInterface {
    public static abstract class Stub extends android.os.Binder implements IPackageInstaller {
        private static final String DESCRIPTOR = "android.content.pm.IPackageInstaller";
        public Stub() { attachInterface(this, DESCRIPTOR); }
        public static IPackageInstaller asInterface(IBinder obj) {
            if (obj == null) return null;
            IInterface i = obj.queryLocalInterface(DESCRIPTOR);
            return (i instanceof IPackageInstaller) ? (IPackageInstaller) i : null;
        }
        @Override public IBinder asBinder() { return this; }
    }
}
