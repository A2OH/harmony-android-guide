// SPDX-License-Identifier: Apache-2.0
//
// Westlake M4d -- compile-time stub for IMediaProjection.

package android.media.projection;

import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteException;

public interface IMediaProjection extends IInterface {
    static final String DESCRIPTOR = "android.media.projection.IMediaProjection";

    abstract class Stub extends android.os.Binder implements IMediaProjection {
        public static IMediaProjection asInterface(IBinder obj) { return null; }
        @Override public IBinder asBinder() { return this; }
    }
}
