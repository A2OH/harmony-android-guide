// SPDX-License-Identifier: Apache-2.0
//
// Westlake M4c -- compile-time stub for android.content.pm.dex.IArtManager
// (AOSP-hidden; stripped from aosp-shim.dex via framework_duplicates.txt).
// Real interface lives in framework.jar.

package android.content.pm.dex;

import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteException;

public interface IArtManager extends IInterface {
    public static abstract class Stub extends android.os.Binder implements IArtManager {
        private static final String DESCRIPTOR = "android.content.pm.dex.IArtManager";
        public Stub() { attachInterface(this, DESCRIPTOR); }
        public static IArtManager asInterface(IBinder obj) {
            if (obj == null) return null;
            IInterface i = obj.queryLocalInterface(DESCRIPTOR);
            return (i instanceof IArtManager) ? (IArtManager) i : null;
        }
        @Override public IBinder asBinder() { return this; }
    }
}
