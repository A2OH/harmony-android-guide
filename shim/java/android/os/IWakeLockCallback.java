// SPDX-License-Identifier: Apache-2.0
//
// Westlake M4-power -- compile-time stub for android.os.IWakeLockCallback
// (AOSP-hidden; stripped from aosp-shim.dex via framework_duplicates.txt).
// Real interface lives in framework.jar.

package android.os;

public interface IWakeLockCallback extends android.os.IInterface {
    void onStateChanged(boolean enabled) throws RemoteException;

    public static abstract class Stub extends android.os.Binder implements IWakeLockCallback {
        private static final String DESCRIPTOR = "android.os.IWakeLockCallback";
        public Stub() { attachInterface(this, DESCRIPTOR); }
        public static IWakeLockCallback asInterface(IBinder obj) {
            if (obj == null) return null;
            IInterface i = obj.queryLocalInterface(DESCRIPTOR);
            return (i instanceof IWakeLockCallback) ? (IWakeLockCallback) i : null;
        }
        @Override public IBinder asBinder() { return this; }
    }
}
