// SPDX-License-Identifier: Apache-2.0
//
// Westlake M4a -- compile-time stub for android.app.IServiceConnection
// (AOSP-hidden; stripped from aosp-shim.dex via framework_duplicates.txt).
// Real interface lives in framework.jar.

package android.app;

import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteException;

public interface IServiceConnection extends IInterface {
    public static abstract class Stub extends android.os.Binder implements IServiceConnection {
        private static final String DESCRIPTOR = "android.app.IServiceConnection";
        public Stub() { attachInterface(this, DESCRIPTOR); }
        public static IServiceConnection asInterface(IBinder obj) {
            if (obj == null) return null;
            IInterface i = obj.queryLocalInterface(DESCRIPTOR);
            return (i instanceof IServiceConnection) ? (IServiceConnection) i : null;
        }
        @Override public IBinder asBinder() { return this; }
    }
}
