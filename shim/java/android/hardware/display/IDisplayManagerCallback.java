// SPDX-License-Identifier: Apache-2.0
//
// Westlake M4d -- compile-time stub for IDisplayManagerCallback.
//
// At runtime framework.jar provides the real interface; shim entry in
// scripts/framework_duplicates.txt strips this class from aosp-shim.dex.

package android.hardware.display;

import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteException;

public interface IDisplayManagerCallback extends IInterface {
    static final String DESCRIPTOR = "android.hardware.display.IDisplayManagerCallback";

    void onDisplayEvent(int displayId, int event) throws RemoteException;

    abstract class Stub extends android.os.Binder implements IDisplayManagerCallback {
        public static IDisplayManagerCallback asInterface(IBinder obj) { return null; }
        @Override public IBinder asBinder() { return this; }
    }
}
