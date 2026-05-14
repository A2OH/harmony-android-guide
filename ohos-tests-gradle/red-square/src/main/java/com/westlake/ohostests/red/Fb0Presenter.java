// SPDX-License-Identifier: Apache-2.0
//
// Fb0Presenter — write a SoftwareCanvas to /dev/graphics/fb0 (PF-ohos-mvp-003).
//
// Why not java.io.FileOutputStream? Because the OHOS dalvikvm port
// ships a minimal libcore — IoBridge's native registrations are not
// present, so `new FileOutputStream("/dev/graphics/fb0")` would die
// with UnsatisfiedLinkError on its native open. What IS wired (via
// `dalvik-port/compat/libcore_bridge.cpp`) is `libcore.io.Posix`'s
// `open`, `writeBytes`, `close`, `lseek` — see the JNINativeMethod
// table at line 1098 of libcore_bridge.cpp.
//
// We reach the wired natives via `libcore.io.Libcore.os` and call
// the Os interface methods directly (public AOSP API; no
// setAccessible required, so the macro-shim contract is respected).
//
// Streaming strategy: a full 720x1280 ARGB8888 int[] (3.6 MB) made
// dalvik-kitkat's heap-marking GC segfault on the rk3568 board.
// Instead we allocate ONE row-byte buffer (720*4 = 2880 B) and
// re-fill it for each of the 1280 rows, writing it out per row.
// Total transient heap = ~3 KB instead of ~7 MB.
//
// Channel order: DAYU200 panel reports 32 bpp at 720x1280 (verified
// via /sys/class/graphics/fb0/{virtual_size,bits_per_pixel,stride}).
// rk3568 framebuffers on OHOS are little-endian BGRA8888 in memory
// (red.offset=16, green.offset=8, blue.offset=0, transp.offset=24),
// so we serialize ARGB ints as B, G, R, A bytes.

package com.westlake.ohostests.red;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public final class Fb0Presenter {

    private static final String FB0_PATH = "/dev/graphics/fb0";

    /** O_WRONLY on Linux/aarch64. Don't rely on OsConstants.O_WRONLY,
     *  whose initConstants native may not be wired. */
    private static final int O_WRONLY = 1;

    private Fb0Presenter() {}

    /**
     * Render the SoftwareCanvas to /dev/graphics/fb0. Streams row by row
     * to avoid allocating a full-frame int[] (3.6 MB triggers a heap GC
     * crash on dalvik-kitkat OHOS aarch64).
     *
     * @return true on success (open + every-row write + close all OK).
     */
    public static boolean present(SoftwareCanvas canvas, java.io.PrintStream log) {
        if (canvas == null) {
            log.println("[Fb0Presenter] null canvas");
            return false;
        }
        int width  = canvas.getWidth();
        int height = canvas.getHeight();
        log.println("[Fb0Presenter] target=" + FB0_PATH
                + " geometry=" + width + "x" + height
                + " bg=" + (canvas.hasBackground()
                        ? "0x" + Integer.toHexString(canvas.getBackgroundARGB()) : "(none)")
                + " rect=" + (canvas.hasRect()
                        ? "0x" + Integer.toHexString(canvas.getRectColor())
                          + "@" + canvas.getRectX0() + "," + canvas.getRectY0()
                          + "-" + canvas.getRectX1() + "," + canvas.getRectY1()
                        : "(none)"));

        // ── Step 1: resolve libcore.io.Libcore.os ──
        Object os;
        Class<?> osInterface;
        Class<?> fdClass;
        try {
            Class<?> libcore = Class.forName("libcore.io.Libcore");
            Field osField = libcore.getField("os");          // public static
            os = osField.get(null);
            if (os == null) {
                log.println("[Fb0Presenter] libcore.io.Libcore.os is null");
                return false;
            }
            osInterface = Class.forName("libcore.io.Os");
            fdClass = Class.forName("java.io.FileDescriptor");
            log.println("[Fb0Presenter] Libcore.os = " + os.getClass().getName());
        } catch (Throwable t) {
            log.println("[Fb0Presenter] reflect Libcore.os failed: " + t);
            t.printStackTrace(log);
            return false;
        }

        // ── Step 2: open /dev/graphics/fb0 ──
        Object fd;
        Method open;
        try {
            open = osInterface.getMethod("open", String.class, int.class, int.class);
            fd = open.invoke(os, FB0_PATH, O_WRONLY, 0);
            if (fd == null) {
                log.println("[Fb0Presenter] Os.open returned null for " + FB0_PATH);
                return false;
            }
            log.println("[Fb0Presenter] opened " + FB0_PATH + " fd=" + fd);
        } catch (Throwable t) {
            log.println("[Fb0Presenter] Os.open threw: " + t);
            t.printStackTrace(log);
            return false;
        }

        // ── Step 3: resolve writeBytes / close ──
        Method writeBytes;
        Method close;
        try {
            try {
                writeBytes = osInterface.getMethod("writeBytes",
                        fdClass, Object.class, int.class, int.class);
            } catch (NoSuchMethodException nsme) {
                writeBytes = osInterface.getMethod("write",
                        fdClass, byte[].class, int.class, int.class);
            }
            close = osInterface.getMethod("close", fdClass);
        } catch (Throwable t) {
            log.println("[Fb0Presenter] resolve writeBytes/close failed: " + t);
            t.printStackTrace(log);
            return false;
        }

        // ── Step 4: per-row write ──
        byte[] rowBuf = new byte[width * 4];
        long totalBytes = 0;
        boolean ok = true;
        try {
            for (int y = 0; y < height; y++) {
                // Build the row from the canvas's recorded ops.
                for (int x = 0; x < width; x++) {
                    int argb = canvas.sampleArgb(x, y);
                    int j = x * 4;
                    rowBuf[j]     = (byte) (argb & 0xFF);          // B
                    rowBuf[j + 1] = (byte) ((argb >> 8)  & 0xFF);  // G
                    rowBuf[j + 2] = (byte) ((argb >> 16) & 0xFF);  // R
                    rowBuf[j + 3] = (byte) ((argb >> 24) & 0xFF);  // A
                }
                int off = 0;
                int remaining = rowBuf.length;
                while (remaining > 0) {
                    int n = (Integer) writeBytes.invoke(os, fd, rowBuf, off, remaining);
                    if (n <= 0) {
                        log.println("[Fb0Presenter] write returned " + n
                                + " on row " + y + " off=" + off);
                        ok = false;
                        break;
                    }
                    off += n;
                    remaining -= n;
                    totalBytes += n;
                }
                if (!ok) break;
            }
        } catch (Throwable t) {
            log.println("[Fb0Presenter] write loop threw: " + t);
            t.printStackTrace(log);
            ok = false;
        }
        log.println("[Fb0Presenter] wrote " + totalBytes + " bytes total");

        // ── Step 5: close ──
        try {
            close.invoke(os, fd);
            log.println("[Fb0Presenter] closed fd");
        } catch (Throwable t) {
            log.println("[Fb0Presenter] close threw (non-fatal): " + t);
        }

        return ok;
    }
}
