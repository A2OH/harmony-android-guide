// SPDX-License-Identifier: Apache-2.0
//
// Westlake M4b -- compile-time stub for android.view.IDisplayFoldListener.
// Real interface lives in framework.jar; this shim is stripped via
// framework_duplicates.txt.

package android.view;

import android.os.IInterface;
import android.os.RemoteException;

public interface IDisplayFoldListener extends IInterface {
    void onDisplayFoldChanged(int displayId, boolean folded) throws RemoteException;
}
