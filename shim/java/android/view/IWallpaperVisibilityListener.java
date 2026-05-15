// SPDX-License-Identifier: Apache-2.0
//
// Westlake M4b -- compile-time stub for android.view.IWallpaperVisibilityListener.
// Real interface lives in framework.jar; this shim is stripped via
// framework_duplicates.txt.

package android.view;

import android.os.IInterface;
import android.os.RemoteException;

public interface IWallpaperVisibilityListener extends IInterface {
    void onWallpaperVisibilityChanged(boolean visible, int displayId) throws RemoteException;
}
