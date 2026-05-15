// SPDX-License-Identifier: Apache-2.0
//
// Westlake M4e -- compile-time stub for ICallNotificationEventCallback.

package android.app;

import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteException;

public interface ICallNotificationEventCallback extends IInterface {
    static final String DESCRIPTOR = "android.app.ICallNotificationEventCallback";

    abstract class Stub extends android.os.Binder implements ICallNotificationEventCallback {
        public static ICallNotificationEventCallback asInterface(IBinder obj) { return null; }
        @Override public IBinder asBinder() { return this; }
    }
}
