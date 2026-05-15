// SPDX-License-Identifier: Apache-2.0
//
// Westlake M4e -- compile-time stub for IConnectionlessHandwritingCallback.

package com.android.internal.inputmethod;

import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteException;

public interface IConnectionlessHandwritingCallback extends IInterface {
    static final String DESCRIPTOR = "com.android.internal.inputmethod.IConnectionlessHandwritingCallback";

    abstract class Stub extends android.os.Binder implements IConnectionlessHandwritingCallback {
        public static IConnectionlessHandwritingCallback asInterface(IBinder obj) { return null; }
        @Override public IBinder asBinder() { return this; }
    }
}
