// SPDX-License-Identifier: Apache-2.0
//
// Westlake M4a -- compile-time stub for android.content.IIntentReceiver
// (AOSP-hidden; stripped from aosp-shim.dex via framework_duplicates.txt).
// Real interface lives in framework.jar.

package android.content;

import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteException;

public interface IIntentReceiver extends IInterface {
    public static abstract class Stub extends android.os.Binder implements IIntentReceiver {
        private static final String DESCRIPTOR = "android.content.IIntentReceiver";
        public Stub() { attachInterface(this, DESCRIPTOR); }
        public static IIntentReceiver asInterface(IBinder obj) {
            if (obj == null) return null;
            IInterface i = obj.queryLocalInterface(DESCRIPTOR);
            return (i instanceof IIntentReceiver) ? (IIntentReceiver) i : null;
        }
        @Override public IBinder asBinder() { return this; }
    }
}
