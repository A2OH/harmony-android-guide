// SPDX-License-Identifier: Apache-2.0
//
// Westlake M4a -- compile-time stub for android.os.PermissionEnforcer
// (AOSP-hidden; stripped from aosp-shim.dex via framework_duplicates.txt).
// Real class lives in framework.jar (Android 16).
//
// Why we stub:
//   IActivityManager.Stub's no-arg constructor calls
//     PermissionEnforcer.fromContext(ActivityThread.currentActivityThread()
//                                         .getSystemContext())
//   which NPEs in the Westlake sandbox where ActivityThread is null.  Our
//   WestlakeActivityManagerService passes a subclassed PermissionEnforcer
//   instance to the Stub(PermissionEnforcer) constructor to bypass that
//   path.  PermissionEnforcer's protected no-arg constructor in
//   framework.jar sets mContext=null and returns; no system services are
//   touched.

package android.os;

import android.content.AttributionSource;
import android.content.Context;

public class PermissionEnforcer {
    protected PermissionEnforcer() {}
    public PermissionEnforcer(Context context) {}
    public static PermissionEnforcer fromContext(Context context) { return null; }
    public void enforcePermission(String permission, int pid, int uid) {}
    public void enforcePermission(String permission, AttributionSource source) {}
    public void enforcePermissionAllOf(String[] permissions, int pid, int uid) {}
    public void enforcePermissionAllOf(String[] permissions, AttributionSource source) {}
    public void enforcePermissionAnyOf(String[] permissions, int pid, int uid) {}
    public void enforcePermissionAnyOf(String[] permissions, AttributionSource source) {}
}
