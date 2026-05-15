// SPDX-License-Identifier: Apache-2.0
//
// Westlake M4a -- compile-time stub for android.app.IInstrumentationWatcher
// (AOSP-hidden; stripped from aosp-shim.dex via framework_duplicates.txt).
// Real interface lives in framework.jar.

package android.app;

import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteException;

public interface IInstrumentationWatcher extends IInterface {
    public static abstract class Stub extends android.os.Binder implements IInstrumentationWatcher {
        private static final String DESCRIPTOR = "android.app.IInstrumentationWatcher";
        public Stub() { attachInterface(this, DESCRIPTOR); }
        public static IInstrumentationWatcher asInterface(IBinder obj) {
            if (obj == null) return null;
            IInterface i = obj.queryLocalInterface(DESCRIPTOR);
            return (i instanceof IInstrumentationWatcher) ? (IInstrumentationWatcher) i : null;
        }
        @Override public IBinder asBinder() { return this; }
    }
}
