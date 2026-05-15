// SPDX-License-Identifier: Apache-2.0
//
// Westlake M4b -- compile-time stub for android.view.IDisplayWindowListener.
// Real interface lives in framework.jar; this shim is stripped via
// framework_duplicates.txt.

package android.view;

import android.os.IInterface;
import android.os.RemoteException;

public interface IDisplayWindowListener extends IInterface {
    void onDisplayAdded(int displayId) throws RemoteException;
    void onDisplayRemoved(int displayId) throws RemoteException;
    void onDisplayConfigurationChanged(int displayId, android.content.res.Configuration newConfig) throws RemoteException;
}
