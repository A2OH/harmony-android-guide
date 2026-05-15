// SPDX-License-Identifier: Apache-2.0
//
// Westlake M4e -- compile-time stub for IConditionProvider.

package android.service.notification;

import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteException;

public interface IConditionProvider extends IInterface {
    static final String DESCRIPTOR = "android.service.notification.IConditionProvider";

    abstract class Stub extends android.os.Binder implements IConditionProvider {
        public static IConditionProvider asInterface(IBinder obj) { return null; }
        @Override public IBinder asBinder() { return this; }
    }
}
