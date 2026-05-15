// SPDX-License-Identifier: Apache-2.0
//
// Westlake M4d -- compile-time stub for IVirtualDisplayCallback.

package android.hardware.display;

import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteException;

public interface IVirtualDisplayCallback extends IInterface {
    static final String DESCRIPTOR = "android.hardware.display.IVirtualDisplayCallback";

    abstract class Stub extends android.os.Binder implements IVirtualDisplayCallback {
        public static IVirtualDisplayCallback asInterface(IBinder obj) { return null; }
        @Override public IBinder asBinder() { return this; }
    }
}
