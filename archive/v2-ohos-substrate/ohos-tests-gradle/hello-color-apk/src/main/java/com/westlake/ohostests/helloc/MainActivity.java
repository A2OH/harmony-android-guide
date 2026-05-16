// SPDX-License-Identifier: Apache-2.0
//
// MainActivity — E12 smoke test target. Mimics the trivial-activity
// shape (Activity, onCreate, marker print, finish) but ALSO creates a
// ColorView and exposes it to the in-process launcher via the
// InProcDrawSource interface contract. The launcher then walks
// View.draw() to harvest BGRA pixels and presents them through the
// in-process DRM bridge.
//
// Interface-based exposure (vs. a public field or reflection) keeps the
// launcher's macro-shim contract clean: no setAccessible, no per-app
// branches. A real Android app would normally not need this hook since
// it could go through Activity.getWindow().getDecorView(); the V2
// substrate's Window / DecorView aren't yet wired for paint, so the
// interface keeps the E12 pixel pipeline isolated from those V2 gaps.

package com.westlake.ohostests.helloc;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.westlake.ohostests.inproc.InProcDrawSource;

public class MainActivity extends Activity implements InProcDrawSource {

    private static final String TAG = "HelloColorApk";

    /** The ColorView built in onCreate. Returned to the launcher via
     *  {@link #getDrawView()}. Null before onCreate runs. */
    private View drawView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String marker = "HelloColorApk.onCreate reached pid="
                + android.os.Process.myPid();
        System.out.println(marker);
        Log.i(TAG, marker);

        ColorView v = new ColorView(this);
        this.drawView = v;
        try {
            setContentView(v);
            System.out.println("HelloColorApk.setContentView returned");
        } catch (Throwable t) {
            // Non-fatal — the V2 substrate's setContentView may have
            // gaps; the launcher will still find drawView via the
            // InProcDrawSource interface and drive draw() directly.
            System.out.println("HelloColorApk.setContentView threw (continuing): " + t);
        }
        System.out.println("HelloColorApk.onCreate done drawView="
                + (drawView == null ? "null" : drawView.getClass().getName()));
    }

    @Override
    public View getDrawView() {
        return drawView;
    }

    @Override
    protected void onDestroy() {
        System.out.println("HelloColorApk.onDestroy reached");
        super.onDestroy();
    }
}
