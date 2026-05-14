// SPDX-License-Identifier: Apache-2.0
//
// DrmPresenter — write a SoftwareCanvas as BGRA8888 to a file on
// /data/local/tmp so the driver-side `drm_present` aarch64 binary
// can mmap it into a DRM dumb BO and SETCRTC it to DSI-1.
//
// Why a file (not direct DRM ioctls from Java)?
//   The dalvikvm we run is statically linked aarch64; it has no
//   System.loadLibrary path wired for arbitrary .so files.
//   The wired Posix.* JNI surface (libcore_bridge.cpp) exposes
//   open/writeBytes/close — sufficient to dump pixels to a file —
//   but does NOT expose ioctl. Adding ioctl would be a per-app
//   feature in the shim layer (forbidden by the macro-shim contract).
//
//   Instead we let the driver script handle the DRM master + ioctls,
//   and just dump pixels Java-side. RedView.onDraw -> SoftwareCanvas
//   remains the source of truth; this Java class is the same role as
//   Fb0Presenter, just targeting a regular file instead of /dev/fb0.
//
// Macro-shim contract:
//   - No setAccessible, no Unsafe.
//   - Reflects only the public `libcore.io.Libcore.os` field and the
//     public `Os` interface (same surface Fb0Presenter uses).
//   - No per-app branches; no new methods on WestlakeContextImpl.

package com.westlake.ohostests.red;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public final class DrmPresenter {

    /** O_WRONLY | O_CREAT | O_TRUNC on Linux/aarch64. */
    private static final int O_WRONLY = 1;
    private static final int O_CREAT  = 0x40;
    private static final int O_TRUNC  = 0x200;
    /** rw-rw-rw- so the user-shell driver script can read it. */
    private static final int FILE_MODE = 0666;

    private DrmPresenter() {}

    /**
     * Render the SoftwareCanvas as BGRA8888 little-endian rows into
     * {@code targetPath}. Returns true on success.
     *
     * The format matches what `drm_present` reads from stdin:
     * width*height*4 bytes, row-major, with each pixel encoded as
     * (B,G,R,A) — the same byte order rk3568 dumb BO uses for
     * DRM_FORMAT_XRGB8888 little-endian.
     */
    public static boolean present(SoftwareCanvas canvas,
                                  String targetPath,
                                  java.io.PrintStream log) {
        if (canvas == null) { log.println("[DrmPresenter] null canvas"); return false; }
        int width  = canvas.getWidth();
        int height = canvas.getHeight();
        log.println("[DrmPresenter] target=" + targetPath
                + " geometry=" + width + "x" + height
                + " bg=" + (canvas.hasBackground()
                        ? "0x" + Integer.toHexString(canvas.getBackgroundARGB()) : "(none)"));

        Object os;
        Class<?> osInterface;
        Class<?> fdClass;
        try {
            Class<?> libcore = Class.forName("libcore.io.Libcore");
            Field osField = libcore.getField("os");
            os = osField.get(null);
            if (os == null) {
                log.println("[DrmPresenter] Libcore.os is null");
                return false;
            }
            osInterface = Class.forName("libcore.io.Os");
            fdClass = Class.forName("java.io.FileDescriptor");
        } catch (Throwable t) {
            log.println("[DrmPresenter] reflect Libcore.os failed: " + t);
            t.printStackTrace(log);
            return false;
        }

        Object fd;
        try {
            Method open = osInterface.getMethod("open",
                    String.class, int.class, int.class);
            fd = open.invoke(os, targetPath,
                    O_WRONLY | O_CREAT | O_TRUNC, FILE_MODE);
            if (fd == null) {
                log.println("[DrmPresenter] Os.open returned null for " + targetPath);
                return false;
            }
            log.println("[DrmPresenter] opened " + targetPath + " fd=" + fd);
        } catch (Throwable t) {
            log.println("[DrmPresenter] Os.open threw: " + t);
            t.printStackTrace(log);
            return false;
        }

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
            log.println("[DrmPresenter] resolve writeBytes/close failed: " + t);
            return false;
        }

        byte[] rowBuf = new byte[width * 4];
        long totalBytes = 0;
        boolean ok = true;
        try {
            for (int y = 0; y < height; y++) {
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
                        log.println("[DrmPresenter] write returned " + n
                                + " at row " + y + " off=" + off);
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
            log.println("[DrmPresenter] write loop threw: " + t);
            t.printStackTrace(log);
            ok = false;
        }
        log.println("[DrmPresenter] wrote " + totalBytes + " bytes total");

        try {
            close.invoke(os, fd);
            log.println("[DrmPresenter] closed fd");
        } catch (Throwable t) {
            log.println("[DrmPresenter] close threw (non-fatal): " + t);
        }
        return ok;
    }
}
