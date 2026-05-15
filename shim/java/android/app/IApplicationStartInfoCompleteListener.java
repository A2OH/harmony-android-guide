// SPDX-License-Identifier: Apache-2.0
//
// Westlake M4a -- compile-time stub for android.app.IApplicationStartInfoCompleteListener
// (AOSP-hidden; stripped from aosp-shim.dex via framework_duplicates.txt).
// Real interface lives in framework.jar.

package android.app;

import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteException;

public interface IApplicationStartInfoCompleteListener extends IInterface {
    public static abstract class Stub extends android.os.Binder implements IApplicationStartInfoCompleteListener {
        private static final String DESCRIPTOR = "android.app.IApplicationStartInfoCompleteListener";
        public Stub() { attachInterface(this, DESCRIPTOR); }
        public static IApplicationStartInfoCompleteListener asInterface(IBinder obj) {
            if (obj == null) return null;
            IInterface i = obj.queryLocalInterface(DESCRIPTOR);
            return (i instanceof IApplicationStartInfoCompleteListener) ? (IApplicationStartInfoCompleteListener) i : null;
        }
        @Override public IBinder asBinder() { return this; }
    }
}
