package com.westlake.ohostests.trivial;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

/**
 * MVP-1 trivial Activity (#619).
 *
 * onCreate logs a stable marker and calls finish(). No view hierarchy,
 * no resources, no permissions.
 *
 * Acceptance marker (do NOT change wording without updating
 * scripts/run-ohos-test.sh):
 *     OhosTrivialActivity.onCreate reached
 */
public class MainActivity extends Activity {

    private static final String TAG = "OhosTrivialActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Print both via android.util.Log (driver greps hilog) AND via
        // System.out (driver greps dalvikvm.stdout). The OHOS board has
        // hilog wired through native code only — Log.i with arbitrary
        // tags doesn't reach the user's shell on dalvik-kitkat, so the
        // stdout marker is what the MVP-1 harness actually checks.
        String marker = "OhosTrivialActivity.onCreate reached pid="
                + android.os.Process.myPid();
        System.out.println(marker);
        Log.i(TAG, marker);
        String arch = "os.arch=" + System.getProperty("os.arch")
                + " java.vm.version=" + System.getProperty("java.vm.version");
        System.out.println(arch);
        Log.i(TAG, arch);
        finish();
    }

    @Override
    protected void onDestroy() {
        System.out.println("OhosTrivialActivity.onDestroy reached");
        Log.i(TAG, "OhosTrivialActivity.onDestroy reached");
        super.onDestroy();
    }
}
