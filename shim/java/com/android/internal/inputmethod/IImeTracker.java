// SPDX-License-Identifier: Apache-2.0
//
// Westlake M4e -- compile-time stub for IImeTracker.

package com.android.internal.inputmethod;

import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteException;

public interface IImeTracker extends IInterface {
    static final String DESCRIPTOR = "com.android.internal.inputmethod.IImeTracker";

    abstract class Stub extends android.os.Binder implements IImeTracker {
        public static IImeTracker asInterface(IBinder obj) { return null; }
        @Override public IBinder asBinder() { return this; }
    }
}
