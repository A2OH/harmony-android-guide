// SPDX-License-Identifier: Apache-2.0
//
// Westlake M4b -- compile-time stub for android.view.IDisplayChangeWindowController.
// Real interface lives in framework.jar; this shim is stripped via
// framework_duplicates.txt.

package android.view;

import android.os.IInterface;
import android.os.RemoteException;

public interface IDisplayChangeWindowController extends IInterface {
    void onDisplayChange(int displayId, int fromRotation, int toRotation, android.view.IDisplayWindowInsetsController callback) throws RemoteException;
}
