// SPDX-License-Identifier: Apache-2.0
//
// Westlake M4a -- compile-time stub for android.app.IUiAutomationConnection
// (AOSP-hidden; stripped from aosp-shim.dex via framework_duplicates.txt).
// Real interface lives in framework.jar.

package android.app;

import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteException;

public interface IUiAutomationConnection extends IInterface {
    public static abstract class Stub extends android.os.Binder implements IUiAutomationConnection {
        private static final String DESCRIPTOR = "android.app.IUiAutomationConnection";
        public Stub() { attachInterface(this, DESCRIPTOR); }
        public static IUiAutomationConnection asInterface(IBinder obj) {
            if (obj == null) return null;
            IInterface i = obj.queryLocalInterface(DESCRIPTOR);
            return (i instanceof IUiAutomationConnection) ? (IUiAutomationConnection) i : null;
        }
        @Override public IBinder asBinder() { return this; }
    }
}
