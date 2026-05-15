// SPDX-License-Identifier: Apache-2.0
//
// Westlake M4b -- compile-time stub for android.view.ISystemGestureExclusionListener.
// Real interface lives in framework.jar; this shim is stripped via
// framework_duplicates.txt.

package android.view;

import android.os.IInterface;
import android.os.RemoteException;

public interface ISystemGestureExclusionListener extends IInterface {
    void onSystemGestureExclusionChanged(int displayId, android.graphics.Region systemGestureExclusion, android.graphics.Region unrestrictedOrNull) throws RemoteException;
}
