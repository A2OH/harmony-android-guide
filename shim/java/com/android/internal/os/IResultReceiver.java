// SPDX-License-Identifier: Apache-2.0
//
// Westlake M4a -- compile-time stub for com.android.internal.os.IResultReceiver
// (AOSP-hidden; stripped from aosp-shim.dex via framework_duplicates.txt).
// Real interface lives in framework.jar.

package com.android.internal.os;

import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteException;

public interface IResultReceiver extends IInterface {
    public static abstract class Stub extends android.os.Binder implements IResultReceiver {
        private static final String DESCRIPTOR = "com.android.internal.os.IResultReceiver";
        public Stub() { attachInterface(this, DESCRIPTOR); }
        public static IResultReceiver asInterface(IBinder obj) {
            if (obj == null) return null;
            IInterface i = obj.queryLocalInterface(DESCRIPTOR);
            return (i instanceof IResultReceiver) ? (IResultReceiver) i : null;
        }
        @Override public IBinder asBinder() { return this; }
    }
}
