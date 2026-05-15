// SPDX-License-Identifier: Apache-2.0
//
// Westlake M4a -- compile-time stub for android.app.IUserSwitchObserver
// (AOSP-hidden; stripped from aosp-shim.dex via framework_duplicates.txt).
// Real interface lives in framework.jar.

package android.app;

import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteException;

public interface IUserSwitchObserver extends IInterface {
    public static abstract class Stub extends android.os.Binder implements IUserSwitchObserver {
        private static final String DESCRIPTOR = "android.app.IUserSwitchObserver";
        public Stub() { attachInterface(this, DESCRIPTOR); }
        public static IUserSwitchObserver asInterface(IBinder obj) {
            if (obj == null) return null;
            IInterface i = obj.queryLocalInterface(DESCRIPTOR);
            return (i instanceof IUserSwitchObserver) ? (IUserSwitchObserver) i : null;
        }
        @Override public IBinder asBinder() { return this; }
    }
}
