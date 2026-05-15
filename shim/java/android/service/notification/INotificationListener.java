// SPDX-License-Identifier: Apache-2.0
//
// Westlake M4e -- compile-time stub for INotificationListener.

package android.service.notification;

import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteException;

public interface INotificationListener extends IInterface {
    static final String DESCRIPTOR = "android.service.notification.INotificationListener";

    abstract class Stub extends android.os.Binder implements INotificationListener {
        public static INotificationListener asInterface(IBinder obj) { return null; }
        @Override public IBinder asBinder() { return this; }
    }
}
