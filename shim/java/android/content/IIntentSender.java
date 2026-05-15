// SPDX-License-Identifier: Apache-2.0
//
// Westlake M4a -- compile-time stub for android.content.IIntentSender
// (AOSP-hidden; stripped from aosp-shim.dex via framework_duplicates.txt).
// Real interface lives in framework.jar.

package android.content;

import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteException;

public interface IIntentSender extends IInterface {
    public static abstract class Stub extends android.os.Binder implements IIntentSender {
        private static final String DESCRIPTOR = "android.content.IIntentSender";
        public Stub() { attachInterface(this, DESCRIPTOR); }
        public static IIntentSender asInterface(IBinder obj) {
            if (obj == null) return null;
            IInterface i = obj.queryLocalInterface(DESCRIPTOR);
            return (i instanceof IIntentSender) ? (IIntentSender) i : null;
        }
        @Override public IBinder asBinder() { return this; }
    }
}
