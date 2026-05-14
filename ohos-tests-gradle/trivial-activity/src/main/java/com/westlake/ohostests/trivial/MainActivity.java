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
        Log.i(TAG, "OhosTrivialActivity.onCreate reached pid=" + android.os.Process.myPid());
        Log.i(TAG, "os.arch=" + System.getProperty("os.arch")
                + " java.vm.version=" + System.getProperty("java.vm.version"));
        finish();
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "OhosTrivialActivity.onDestroy reached");
        super.onDestroy();
    }
}
