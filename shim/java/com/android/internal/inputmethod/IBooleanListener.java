// SPDX-License-Identifier: Apache-2.0
//
// Westlake M4e -- compile-time stub for IBooleanListener.

package com.android.internal.inputmethod;

import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteException;

public interface IBooleanListener extends IInterface {
    static final String DESCRIPTOR = "com.android.internal.inputmethod.IBooleanListener";

    abstract class Stub extends android.os.Binder implements IBooleanListener {
        public static IBooleanListener asInterface(IBinder obj) { return null; }
        @Override public IBinder asBinder() { return this; }
    }
}
