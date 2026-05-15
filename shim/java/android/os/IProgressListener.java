// SPDX-License-Identifier: Apache-2.0
//
// Westlake M4a -- compile-time stub for android.os.IProgressListener
// (AOSP-hidden; stripped from aosp-shim.dex via framework_duplicates.txt).
// Real interface lives in framework.jar.

package android.os;

import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteException;

public interface IProgressListener extends IInterface {
    public static abstract class Stub extends android.os.Binder implements IProgressListener {
        private static final String DESCRIPTOR = "android.os.IProgressListener";
        public Stub() { attachInterface(this, DESCRIPTOR); }
        public static IProgressListener asInterface(IBinder obj) {
            if (obj == null) return null;
            IInterface i = obj.queryLocalInterface(DESCRIPTOR);
            return (i instanceof IProgressListener) ? (IProgressListener) i : null;
        }
        @Override public IBinder asBinder() { return this; }
    }
}
