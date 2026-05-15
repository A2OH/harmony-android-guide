// SPDX-License-Identifier: Apache-2.0
//
// Westlake M4e -- compile-time stub for IInputMethodClient.

package com.android.internal.inputmethod;

import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteException;

public interface IInputMethodClient extends IInterface {
    static final String DESCRIPTOR = "com.android.internal.inputmethod.IInputMethodClient";

    abstract class Stub extends android.os.Binder implements IInputMethodClient {
        public static IInputMethodClient asInterface(IBinder obj) { return null; }
        @Override public IBinder asBinder() { return this; }
    }
}
