// SPDX-License-Identifier: Apache-2.0
//
// Westlake M4a -- compile-time stub for android.app.ActivityTaskManager
// (AOSP-hidden; stripped from aosp-shim.dex via framework_duplicates.txt).
// Real class lives in framework.jar.
//
// We only need this so RootTaskInfo is referenceable from
// WestlakeActivityManagerService's IActivityManager method signatures.

package android.app;

public class ActivityTaskManager {
    public ActivityTaskManager() {}

    public static class RootTaskInfo {
        public RootTaskInfo() {}
    }
}
