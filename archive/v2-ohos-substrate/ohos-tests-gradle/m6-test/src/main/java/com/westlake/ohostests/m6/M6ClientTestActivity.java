// SPDX-License-Identifier: Apache-2.0
//
// M6ClientTestActivity — drives M6DrmClient against the live m6-drm-daemon
// (PF-ohos-m6-002). Sole job is to prove the Java client matches the C
// --test-client's behavior end-to-end on the rk3568 board.
//
// onCreate:
//   1. Build an M6FramePainter at the daemon's panel geometry (720x1280).
//   2. Connect M6DrmClient to /data/local/tmp/westlake/m6-drm.sock
//      (filesystem; falls back to abstract @m6-drm.sock if needed).
//   3. Submit FRAMES total: first SPLIT frames RED, remaining BLUE.
//   4. Track per-frame send+ACK timing; print stats matching the C
//      client's M6_TEST_CLIENT_DONE line.
//   5. Disconnect cleanly.
//
// We run the submit loop directly on the Activity thread (no background
// thread). The daemon is sync-ACK per-frame so this naturally rate-limits
// to vsync. Marker contract — driver script greps these:
//
//   "OhosM6ClientTest.onCreate reached pid=<N>"
//   "OhosM6ClientTest.client connected sock=<path>"
//   "M6_JAVA_CLIENT_DONE ok=<N> fail=<N> elapsed_ms=<...> avg_per_frame_ms=<...>"
//   "OhosM6ClientTest.disconnect OK"
//
// Macro-shim contract: no Unsafe / setAccessible / per-app branches.

package com.westlake.ohostests.m6;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;

public class M6ClientTestActivity extends Activity {

    private static final String TAG = "OhosM6ClientTest";

    // Match the C --test-client defaults.
    private static final int WIDTH  = 720;
    private static final int HEIGHT = 1280;
    private static final int FRAMES = 120;
    private static final int SPLIT  = 60; // RED for [0..SPLIT), BLUE for [SPLIT..FRAMES)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        emit(TAG + ".onCreate reached pid=" + android.os.Process.myPid());
        runSubmitLoop();
        emit(TAG + ".onCreate complete; exiting via System.exit(0)");
        // Per the brief: "After 120 frames, Activity calls
        // M6DrmClient.disconnect() and System.exit(0)". finish() then
        // System.exit so the daemon's accept-loop sees a clean close.
        finish();
        System.exit(0);
    }

    private void runSubmitLoop() {
        M6FramePainter painter = new M6FramePainter(WIDTH, HEIGHT);
        // Pre-paint both frames once — we recycle the same byte[] for
        // every red and every blue frame. Memfd contents are copied into
        // a fresh fd each submit, so this is safe.
        byte[] redFrame = painter.paint(Color.RED);
        byte[] blueFrame = painter.paint(Color.BLUE);
        emit("painter prepared: " + WIDTH + "x" + HEIGHT
                + " ("  + redFrame.length + " bytes/frame)");

        M6DrmClient client = new M6DrmClient();
        if (!client.connect(M6DrmClient.DEFAULT_SOCKET_PATH)) {
            emit(TAG + ".connect FAILED");
            return;
        }
        emit(TAG + ".client connected sock=" + client.connectedSocketPath());

        long t0Ns = System.nanoTime();
        long prevNs = t0Ns;
        int ok = 0, fail = 0;
        // Per-frame intervals (ack-to-ack) so we can compare to C baseline.
        long[] intervalsNs = new long[FRAMES];
        int intervalCount = 0;
        long minNs = Long.MAX_VALUE, maxNs = 0;

        for (int i = 0; i < FRAMES; i++) {
            byte[] frame = (i < SPLIT) ? redFrame : blueFrame;
            boolean rc = client.submit(frame, i);
            long now = System.nanoTime();
            if (rc) {
                ok++;
                if (i > 0) {
                    long dt = now - prevNs;
                    intervalsNs[intervalCount++] = dt;
                    if (dt < minNs) minNs = dt;
                    if (dt > maxNs) maxNs = dt;
                }
                if ((i + 1) % 30 == 0) {
                    emit("submit progress: " + (i + 1) + "/" + FRAMES
                            + " ok=" + ok + " fail=" + fail);
                }
            } else {
                fail++;
                emit("submit FAILED at frame=" + i + " (continuing)");
            }
            prevNs = now;
        }
        long t1Ns = System.nanoTime();
        long elapsedMs = (t1Ns - t0Ns) / 1_000_000L;
        double avgMs = ok > 0 ? (double)(t1Ns - t0Ns) / 1_000_000.0 / ok : 0.0;
        // Statistics over the interval samples (skip frame 0).
        long sumIntervalNs = 0;
        for (int i = 0; i < intervalCount; i++) sumIntervalNs += intervalsNs[i];
        double avgIntervalMs = intervalCount > 0
                ? (double)sumIntervalNs / 1_000_000.0 / intervalCount : 0.0;
        double minIntervalMs = minNs == Long.MAX_VALUE ? 0.0 : minNs / 1_000_000.0;
        double maxIntervalMs = maxNs / 1_000_000.0;
        double hz = avgIntervalMs > 0.0 ? 1000.0 / avgIntervalMs : 0.0;

        // String.format triggers libcore.icu.LocaleData.<clinit> which
        // hits an NPE on standalone OHOS dalvikvm (ICU resources not on
        // disk; the shim's getDefaultLocaleNativesInternal returns null).
        // Build the marker line via plain concatenation to bypass the
        // formatter entirely — keeps this path generic, no per-app hack.
        StringBuilder sb = new StringBuilder(192);
        sb.append("M6_JAVA_CLIENT_DONE ok=").append(ok);
        sb.append(" fail=").append(fail);
        sb.append(" elapsed_ms=").append(elapsedMs);
        sb.append(" avg_per_frame_ms=").append(formatTwo(avgMs));
        sb.append(" interval_avg_ms=").append(formatTwo(avgIntervalMs));
        sb.append(" interval_min_ms=").append(formatTwo(minIntervalMs));
        sb.append(" interval_max_ms=").append(formatTwo(maxIntervalMs));
        sb.append(" hz=").append(formatTwo(hz));
        emit(sb.toString());

        client.disconnect();
        if (client.isConnected()) {
            emit(TAG + ".disconnect WARN: client still reports connected");
        } else {
            emit(TAG + ".disconnect OK");
        }
    }

    @Override
    protected void onDestroy() {
        emit(TAG + ".onDestroy reached");
        super.onDestroy();
    }

    /**
     * Locale-free "%.2f" replacement. Multiplies by 100, rounds to long,
     * formats with manual integer/fraction split. Avoids
     * {@link String#format} which on standalone OHOS dalvikvm hits
     * {@code LocaleData.<clinit>} NPE.
     */
    private static String formatTwo(double v) {
        if (Double.isNaN(v)) return "NaN";
        if (Double.isInfinite(v)) return v > 0 ? "Inf" : "-Inf";
        boolean neg = v < 0;
        double abs = neg ? -v : v;
        long scaled = (long) (abs * 100.0 + 0.5);
        long intPart = scaled / 100;
        long fracPart = scaled % 100;
        StringBuilder sb = new StringBuilder(16);
        if (neg) sb.append('-');
        sb.append(intPart).append('.');
        if (fracPart < 10) sb.append('0');
        sb.append(fracPart);
        return sb.toString();
    }

    private static void emit(String msg) {
        // Same belt-and-suspenders pattern as MVP-1/2 — driver greps stdout,
        // hilog is unreliable on standalone OHOS dalvikvm.
        System.out.println(msg);
        try {
            android.util.Log.i(TAG, msg);
        } catch (Throwable ignored) {
            // Log can throw on dalvik-kitkat OHOS if its handler isn't wired.
        }
    }
}
