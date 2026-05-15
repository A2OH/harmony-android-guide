// SPDX-License-Identifier: Apache-2.0
//
// Westlake M4b -- compile-time stub for android.os.IRemoteCallback.
// Real interface lives in framework.jar; this shim is stripped via
// framework_duplicates.txt.

package android.os;

public interface IRemoteCallback extends IInterface {
    void sendResult(Bundle data) throws RemoteException;
}
