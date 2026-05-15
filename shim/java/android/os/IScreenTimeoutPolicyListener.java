// SPDX-License-Identifier: Apache-2.0
//
// Westlake M4-power -- compile-time stub for IScreenTimeoutPolicyListener.
// Real interface lives in framework.jar; this shim is stripped via
// framework_duplicates.txt.

package android.os;

public interface IScreenTimeoutPolicyListener extends android.os.IInterface {
    void onScreenTimeoutPolicyChanged(int policy) throws RemoteException;

    public static abstract class Stub extends android.os.Binder implements IScreenTimeoutPolicyListener {
        private static final String DESCRIPTOR = "android.os.IScreenTimeoutPolicyListener";
        public Stub() { attachInterface(this, DESCRIPTOR); }
        public static IScreenTimeoutPolicyListener asInterface(IBinder obj) {
            if (obj == null) return null;
            IInterface i = obj.queryLocalInterface(DESCRIPTOR);
            return (i instanceof IScreenTimeoutPolicyListener)
                    ? (IScreenTimeoutPolicyListener) i : null;
        }
        @Override public IBinder asBinder() { return this; }
    }
}
