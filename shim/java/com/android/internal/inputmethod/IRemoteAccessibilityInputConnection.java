// SPDX-License-Identifier: Apache-2.0
//
// Westlake M4e -- compile-time stub for IRemoteAccessibilityInputConnection.

package com.android.internal.inputmethod;

import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteException;

public interface IRemoteAccessibilityInputConnection extends IInterface {
    static final String DESCRIPTOR = "com.android.internal.inputmethod.IRemoteAccessibilityInputConnection";

    abstract class Stub extends android.os.Binder implements IRemoteAccessibilityInputConnection {
        public static IRemoteAccessibilityInputConnection asInterface(IBinder obj) { return null; }
        @Override public IBinder asBinder() { return this; }
    }
}
