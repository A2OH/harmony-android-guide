// SPDX-License-Identifier: Apache-2.0
//
// InProcDrawSource — opt-in contract Activities implement so the E12
// in-process launcher can locate the View to paint.
//
// Rationale: the V2 substrate's Window / DecorView are stubs as of
// 2026-05-15, so walking Activity.getWindow().getDecorView() can't yet
// give us the app's drawable root. Until those are wired, the
// in-process launcher discovers the View through this explicit
// interface — straight method call, no reflection, no setAccessible,
// no per-app branches in the shim. Apps that target the in-process
// pipeline implement getDrawView() on their MainActivity.
//
// Once getWindow().getDecorView() works for real Android APKs (a Phase 2
// item independent of E12), this interface becomes optional — the
// launcher would fall back to the Activity standard accessor when an
// app doesn't implement it. For E12 smoke we only need the interface
// path.

package com.westlake.ohostests.inproc;

import android.view.View;

public interface InProcDrawSource {

    /**
     * Return the View whose draw(Canvas) the launcher should call.
     * Must be non-null after Activity.onCreate has returned. May
     * return the same View instance on repeated calls — the launcher
     * only calls draw() once per frame and never caches across
     * Activities.
     */
    View getDrawView();
}
