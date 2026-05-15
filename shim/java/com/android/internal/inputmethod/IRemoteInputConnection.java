// SPDX-License-Identifier: Apache-2.0
//
// Westlake M4e -- compile-time stub for IRemoteInputConnection.

package com.android.internal.inputmethod;

import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteException;

public interface IRemoteInputConnection extends IInterface {
    static final String DESCRIPTOR = "com.android.internal.inputmethod.IRemoteInputConnection";

    abstract class Stub extends android.os.Binder implements IRemoteInputConnection {
        public static IRemoteInputConnection asInterface(IBinder obj) { return null; }
        @Override public IBinder asBinder() { return this; }
    }
}
