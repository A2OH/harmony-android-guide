/*
 * drm_inproc_bridge.c — CR60 follow-up E9b (Path Y).
 *
 * In-process JNI variant of agent 7's standalone `drm_present` helper
 * (commit 44686464 / `dalvik-port/compat/drm_present.c`). The MVP-2
 * red-square-drm test already proved DRM/KMS direct scan-out works
 * from a non-host root process on the DAYU200 rk3568, but the call
 * site was a separate aarch64 static binary invoked from the driver
 * shell. CR60 E9b retargets the same pipeline as a JNI bridge so the
 * dalvikvm-arm32-dynamic process can drive the panel directly — no
 * second binary, no driver-side stage.
 *
 * Pipeline (matches drm_present.c step-for-step):
 *   1. open("/dev/dri/card0", O_RDWR)
 *   2. DRM_IOCTL_SET_MASTER - requires composer_host to NOT own it.
 *      The bridge KILLS composer_host itself (walks /proc by pid),
 *      reading each /proc/<pid>/comm to find the right process, just
 *      before opening card0, then retries SET_MASTER with 50ms backoff
 *      up to 5 times. This is necessary because hdf_devmgr respawns
 *      composer_host within ~0.3s of SIGKILL - too fast for a driver-
 *      side kill to coexist with VM startup (~5s). Doing the kill from
 *      inside the JNI bridge eliminates the round-trip latency.
 *      Same end-state as red-square-drm's protocol; after the hold
 *      window closes and we DROP_MASTER, hdf_devmgr respawns one more
 *      time and the system returns to the compositor.
 *   3. DRM_IOCTL_MODE_GETRESOURCES → enumerate crtc/conn/enc.
 *   4. Pick first connected connector + its first mode.
 *   5. Resolve CRTC via the connector's encoder.
 *   6. DRM_IOCTL_MODE_CREATE_DUMB (w, h, bpp=32).
 *   7. DRM_IOCTL_MODE_MAP_DUMB + mmap.
 *   8. Fill BO with BGRA RED (0xFFFF0000 little-endian → bytes
 *      00 00 FF FF, interpreted by rk3568 XRGB8888 as B=0 G=0 R=FF X=FF).
 *   9. DRM_IOCTL_MODE_ADDFB2 (DRM_FORMAT_XRGB8888 / XR24).
 *  10. DRM_IOCTL_MODE_SETCRTC — *** PANEL TURNS RED HERE ***
 *  11. sleep(holdSecs)
 *  12. DRM_IOCTL_MODE_RMFB + DESTROY_DUMB + DROP_MASTER + close.
 *
 * Caller protocol:
 *   - Java side: System.loadLibrary("drm_inproc_bridge");
 *     int rc = DrmInProcessBridge.nativePresent(holdSecs);
 *     // rc == 0 on success; >0 on the failing step (see DRM_FAIL_*).
 *   - The driver subcommand (hello-drm-inprocess) takes care of
 *     stopping composer_host beforehand, capturing kernel debugfs
 *     state before/after, and letting hdf_devmgr respawn the
 *     compositor after. Documented as a known transient regression
 *     vs. Path X (which would require composer_host coexistence).
 *
 * Macro-shim contract: NO Unsafe / setAccessible / per-app branches
 * (the contract applies to the Java side; this is pure C). Pointer-
 * sized values use uintptr_t / size_t per
 * feedback_bitness_as_parameter.md. The kernel uapi `ptr`-typed
 * fields (e.g. drm_mode_card_res.crtc_id_ptr) require an explicit
 * (uintptr_t) cast on 32-bit ARM because the kernel always treats
 * them as 64-bit.
 *
 * Author: agent 15 (CR60 follow-up #3 / E9b, 2026-05-14).
 */

#define _GNU_SOURCE
#define _DEFAULT_SOURCE
#include <ctype.h>
#include <dirent.h>
#include <errno.h>
#include <fcntl.h>
#include <jni.h>
#include <signal.h>
#include <stdarg.h>
#include <stdint.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/ioctl.h>
#include <sys/mman.h>
#include <sys/types.h>
#include <unistd.h>

#include <drm/drm.h>
#include <drm/drm_mode.h>
#include <drm/drm_fourcc.h>

/* musl on the OHOS sysroot exposes signal.h's kill() and unistd.h's
 * usleep() only when _POSIX_C_SOURCE / _DEFAULT_SOURCE are visible.
 * The clang on the OHOS toolchain doesn't pre-define those, so we
 * declare them explicitly to suppress the implicit-declaration
 * warnings. Both are part of POSIX.1; signatures match the standard. */
extern int kill(pid_t pid, int sig);
extern int usleep(unsigned int useconds);

#define DRM_MODE_CONNECTED 1

/* Error codes returned by nativePresent. Anything > 0 indicates the
 * specific step that failed; the Java side maps these to log-level
 * markers the driver greps. 0 = full success. */
#define DRM_OK              0
#define DRM_FAIL_OPEN       11
#define DRM_FAIL_MASTER     12
#define DRM_FAIL_GETRES     13
#define DRM_FAIL_NO_CONN    14
#define DRM_FAIL_GETENC     15
#define DRM_FAIL_NO_CRTC    16
#define DRM_FAIL_CREATE     17
#define DRM_FAIL_MAP        18
#define DRM_FAIL_MMAP       19
#define DRM_FAIL_ADDFB      20
#define DRM_FAIL_SETCRTC    21

/* Last-error message for diagnostics. Populated whenever
 * nativePresent returns nonzero. */
static char g_drm_err[512] = {0};

static void drm_set_err(const char *step, int err) {
    snprintf(g_drm_err, sizeof(g_drm_err),
             "%s: %s (errno=%d)", step,
             err ? strerror(err) : "(no errno)", err);
}

static void drm_set_errf(const char *fmt, ...) {
    va_list ap;
    va_start(ap, fmt);
    vsnprintf(g_drm_err, sizeof(g_drm_err), fmt, ap);
    va_end(ap);
}

/* Walk /proc and SIGKILL every process whose /proc/<pid>/comm matches
 * "composer_host". Used immediately before SET_MASTER to minimize the
 * race window with hdf_devmgr's auto-respawn. Returns the number of
 * processes signalled. Idempotent: calling it again after respawn
 * kills the new pid too. */
static int kill_composer_host(void) {
    DIR *d = opendir("/proc");
    if (!d) return 0;
    int killed = 0;
    struct dirent *de;
    while ((de = readdir(d)) != NULL) {
        /* Only numeric pid directories. */
        const char *n = de->d_name;
        if (!isdigit((unsigned char)n[0])) continue;
        char comm_path[64];
        snprintf(comm_path, sizeof(comm_path), "/proc/%s/comm", n);
        FILE *f = fopen(comm_path, "r");
        if (!f) continue;
        char comm[64] = {0};
        if (fgets(comm, sizeof(comm), f)) {
            /* strip trailing newline */
            size_t L = strlen(comm);
            while (L > 0 && (comm[L-1] == '\n' || comm[L-1] == '\r')) {
                comm[--L] = 0;
            }
            if (strcmp(comm, "composer_host") == 0) {
                pid_t pid = (pid_t)atoi(n);
                if (pid > 1 && kill(pid, SIGKILL) == 0) {
                    killed++;
                }
            }
        }
        fclose(f);
    }
    closedir(d);
    return killed;
}

/* Core scan-out worker. Returns 0 on success, one of the DRM_FAIL_*
 * codes on failure. Mirrors drm_present.c main() but without stdin
 * read (we fill RED directly) and without argv parsing.
 *
 * Composer-host coexistence:
 *   - Before SET_MASTER, we kill composer_host. hdf_devmgr WILL
 *     respawn it within ~1 sec; that's a known regression vs. Path X.
 *   - We open /dev/dri/card0 + retry SET_MASTER up to 5 times with
 *     50ms backoff to win the race against the respawning compositor
 *     getting master back. Empirically (CR60 E9b first run) the race
 *     window is small but real: the new composer_host has to fork,
 *     reopen card0, and call SET_MASTER itself, which takes 50-200 ms.
 */
static int drm_present_red(int hold_secs) {
    /* Step 0: clear the path. composer_host respawns fast, so we
     * retry kill+open+master a few times. */
    int drm_fd = -1;
    int rc_master = -1;
    int errno_master = 0;
    for (int attempt = 0; attempt < 5; attempt++) {
        int killed = kill_composer_host();
        (void)killed;
        /* Give the kernel a moment to release the old struct file +
         * drop master before our open(). 30ms is empirically enough. */
        usleep(30 * 1000);
        if (drm_fd < 0) {
            drm_fd = open("/dev/dri/card0", O_RDWR | O_CLOEXEC);
            if (drm_fd < 0) {
                drm_set_err("open /dev/dri/card0", errno);
                /* Retry rather than fail outright — composer_host
                 * sometimes holds the file briefly during respawn. */
                continue;
            }
        }
        rc_master = ioctl(drm_fd, DRM_IOCTL_SET_MASTER, 0);
        if (rc_master == 0) break;
        errno_master = errno;
        /* Master grab failed; close the fd so the next attempt's
         * kill+reopen actually gets a fresh state. Without this, the
         * already-opened fd retains its non-master status. */
        close(drm_fd);
        drm_fd = -1;
        usleep(50 * 1000);
    }
    if (drm_fd < 0) {
        drm_set_err("open /dev/dri/card0", errno);
        return DRM_FAIL_OPEN;
    }
    if (rc_master != 0) {
        drm_set_err("SET_MASTER after 5 kill+retry attempts", errno_master);
        close(drm_fd);
        return DRM_FAIL_MASTER;
    }

    struct drm_mode_card_res res;
    memset(&res, 0, sizeof(res));
    if (ioctl(drm_fd, DRM_IOCTL_MODE_GETRESOURCES, &res) < 0) {
        drm_set_err("GETRESOURCES (count)", errno);
        close(drm_fd);
        return DRM_FAIL_GETRES;
    }
    uint32_t *crtc_ids      = calloc(res.count_crtcs,      sizeof(uint32_t));
    uint32_t *connector_ids = calloc(res.count_connectors, sizeof(uint32_t));
    uint32_t *encoder_ids   = calloc(res.count_encoders,   sizeof(uint32_t));
    if (!crtc_ids || !connector_ids || !encoder_ids) {
        drm_set_errf("OOM allocating DRM resource arrays");
        free(crtc_ids); free(connector_ids); free(encoder_ids);
        close(drm_fd);
        return DRM_FAIL_GETRES;
    }
    /* The .._ptr fields are __u64 in the kernel uapi; cast through
     * uintptr_t to silence the 32→64 promotion warning and match
     * agent 7's existing pattern in drm_present.c. */
    res.crtc_id_ptr      = (uintptr_t)crtc_ids;
    res.connector_id_ptr = (uintptr_t)connector_ids;
    res.encoder_id_ptr   = (uintptr_t)encoder_ids;
    res.fb_id_ptr = 0; res.count_fbs = 0;
    if (ioctl(drm_fd, DRM_IOCTL_MODE_GETRESOURCES, &res) < 0) {
        drm_set_err("GETRESOURCES", errno);
        free(crtc_ids); free(connector_ids); free(encoder_ids);
        close(drm_fd);
        return DRM_FAIL_GETRES;
    }

    uint32_t pick_connector = 0, pick_encoder = 0;
    struct drm_mode_modeinfo pick_mode;
    memset(&pick_mode, 0, sizeof(pick_mode));
    for (uint32_t i = 0; i < res.count_connectors; i++) {
        struct drm_mode_get_connector c;
        memset(&c, 0, sizeof(c));
        c.connector_id = connector_ids[i];
        if (ioctl(drm_fd, DRM_IOCTL_MODE_GETCONNECTOR, &c) < 0) continue;
        if (c.connection != DRM_MODE_CONNECTED || c.count_modes == 0) continue;
        struct drm_mode_modeinfo *modes = calloc(c.count_modes, sizeof(*modes));
        uint32_t *enc = calloc(c.count_encoders, sizeof(uint32_t));
        c.modes_ptr = (uintptr_t)modes;
        c.encoders_ptr = (uintptr_t)enc;
        c.props_ptr = 0; c.prop_values_ptr = 0; c.count_props = 0;
        if (ioctl(drm_fd, DRM_IOCTL_MODE_GETCONNECTOR, &c) >= 0) {
            pick_connector = c.connector_id;
            pick_encoder = c.encoder_id;
            pick_mode = modes[0];
        }
        free(modes); free(enc);
        if (pick_connector) break;
    }
    free(crtc_ids); free(connector_ids); free(encoder_ids);
    if (!pick_connector) {
        drm_set_errf("no connected connector with modes");
        close(drm_fd);
        return DRM_FAIL_NO_CONN;
    }

    /* Resolve CRTC from encoder. */
    uint32_t pick_crtc = 0;
    struct drm_mode_get_encoder enc_info;
    memset(&enc_info, 0, sizeof(enc_info));
    enc_info.encoder_id = pick_encoder;
    if (ioctl(drm_fd, DRM_IOCTL_MODE_GETENCODER, &enc_info) < 0) {
        drm_set_err("GETENCODER", errno);
        close(drm_fd);
        return DRM_FAIL_GETENC;
    }
    if (enc_info.crtc_id) {
        pick_crtc = enc_info.crtc_id;
    } else {
        /* Fallback: scan possible_crtcs mask. We already freed the
         * crtc_ids array above; this fallback is rarely taken on this
         * board (encoder 158 → CRTC 92 is wired up at boot), so we
         * accept the small inefficiency of a second GETRESOURCES pass
         * instead of widening the function's locals. */
        struct drm_mode_card_res res2;
        memset(&res2, 0, sizeof(res2));
        if (ioctl(drm_fd, DRM_IOCTL_MODE_GETRESOURCES, &res2) >= 0
                && res2.count_crtcs > 0) {
            uint32_t *c2 = calloc(res2.count_crtcs, sizeof(uint32_t));
            if (c2) {
                res2.crtc_id_ptr = (uintptr_t)c2;
                res2.connector_id_ptr = 0; res2.count_connectors = 0;
                res2.encoder_id_ptr = 0;   res2.count_encoders   = 0;
                res2.fb_id_ptr = 0;        res2.count_fbs        = 0;
                if (ioctl(drm_fd, DRM_IOCTL_MODE_GETRESOURCES, &res2) >= 0) {
                    for (uint32_t i = 0; i < res2.count_crtcs; i++) {
                        if (enc_info.possible_crtcs & (1u << i)) {
                            pick_crtc = c2[i];
                            break;
                        }
                    }
                }
                free(c2);
            }
        }
    }
    if (!pick_crtc) {
        drm_set_errf("no CRTC for encoder %u", pick_encoder);
        close(drm_fd);
        return DRM_FAIL_NO_CRTC;
    }

    /* Allocate dumb BO matching connector's mode. */
    struct drm_mode_create_dumb cdumb;
    memset(&cdumb, 0, sizeof(cdumb));
    cdumb.width  = pick_mode.hdisplay;
    cdumb.height = pick_mode.vdisplay;
    cdumb.bpp = 32;
    if (ioctl(drm_fd, DRM_IOCTL_MODE_CREATE_DUMB, &cdumb) < 0) {
        drm_set_err("CREATE_DUMB", errno);
        close(drm_fd);
        return DRM_FAIL_CREATE;
    }

    struct drm_mode_map_dumb mdumb;
    memset(&mdumb, 0, sizeof(mdumb));
    mdumb.handle = cdumb.handle;
    if (ioctl(drm_fd, DRM_IOCTL_MODE_MAP_DUMB, &mdumb) < 0) {
        drm_set_err("MAP_DUMB", errno);
        close(drm_fd);
        return DRM_FAIL_MAP;
    }
    /* The kernel uapi `offset` is __u64; cast through size_t (32-bit
     * on ARM EABI) for the mmap call. Buffers we allocate here are
     * always small enough to fit. */
    uint8_t *bo = mmap(NULL, (size_t)cdumb.size,
                       PROT_READ | PROT_WRITE,
                       MAP_SHARED, drm_fd, (off_t)mdumb.offset);
    if (bo == MAP_FAILED) {
        drm_set_err("mmap dumb", errno);
        close(drm_fd);
        return DRM_FAIL_MMAP;
    }

    /* Fill BO with RED. rk3568 dumb BO is XRGB8888 in memory (little
     * endian: bytes B G R X per pixel). Pure red = 00 00 FF FF.
     * Write per row, respecting cdumb.pitch (may exceed w*4 due to
     * alignment). */
    size_t row_bytes = (size_t)pick_mode.hdisplay * 4;
    for (size_t y = 0; y < (size_t)pick_mode.vdisplay; y++) {
        uint8_t *row = bo + y * (size_t)cdumb.pitch;
        for (size_t x = 0; x < (size_t)pick_mode.hdisplay; x++) {
            uint8_t *p = row + x * 4;
            p[0] = 0x00;  /* B */
            p[1] = 0x00;  /* G */
            p[2] = 0xFF;  /* R */
            p[3] = 0xFF;  /* X (alpha-byte; rk3568 ignores) */
        }
        (void)row_bytes;  /* silence unused warning if optimizer drops */
    }

    /* Add framebuffer + bind. */
    struct drm_mode_fb_cmd2 fb2;
    memset(&fb2, 0, sizeof(fb2));
    fb2.width  = pick_mode.hdisplay;
    fb2.height = pick_mode.vdisplay;
    fb2.pixel_format = DRM_FORMAT_XRGB8888;
    fb2.handles[0] = cdumb.handle;
    fb2.pitches[0] = cdumb.pitch;
    fb2.offsets[0] = 0;
    if (ioctl(drm_fd, DRM_IOCTL_MODE_ADDFB2, &fb2) < 0) {
        drm_set_err("ADDFB2", errno);
        munmap(bo, (size_t)cdumb.size);
        close(drm_fd);
        return DRM_FAIL_ADDFB;
    }

    uint32_t conn_arr[1] = { pick_connector };
    struct drm_mode_crtc setcrtc;
    memset(&setcrtc, 0, sizeof(setcrtc));
    setcrtc.crtc_id = pick_crtc;
    setcrtc.fb_id = fb2.fb_id;
    setcrtc.set_connectors_ptr = (uintptr_t)conn_arr;
    setcrtc.count_connectors = 1;
    setcrtc.mode = pick_mode;
    setcrtc.mode_valid = 1;
    if (ioctl(drm_fd, DRM_IOCTL_MODE_SETCRTC, &setcrtc) < 0) {
        drm_set_err("SETCRTC", errno);
        uint32_t rm_fb = fb2.fb_id;
        ioctl(drm_fd, DRM_IOCTL_MODE_RMFB, &rm_fb);
        munmap(bo, (size_t)cdumb.size);
        close(drm_fd);
        return DRM_FAIL_SETCRTC;
    }

    /* *** PANEL IS RED ***  Pin scan-out for the requested duration.
     * Caller (driver) typically captures kernel-debugfs state during
     * this window for evidence. */
    if (hold_secs > 0) sleep((unsigned int)hold_secs);

    /* Teardown — leave panel in a defined state; hdf_devmgr will
     * respawn composer_host within ~1 sec and the panel returns to
     * whatever the system wanted to display. */
    uint32_t rm_fb = fb2.fb_id;
    ioctl(drm_fd, DRM_IOCTL_MODE_RMFB, &rm_fb);
    struct drm_mode_destroy_dumb ddumb;
    memset(&ddumb, 0, sizeof(ddumb));
    ddumb.handle = cdumb.handle;
    ioctl(drm_fd, DRM_IOCTL_MODE_DESTROY_DUMB, &ddumb);
    ioctl(drm_fd, DRM_IOCTL_DROP_MASTER, 0);
    munmap(bo, (size_t)cdumb.size);
    close(drm_fd);

    /* Side-channel info for the Java caller — encoded as the result
     * marker; success returns 0. */
    snprintf(g_drm_err, sizeof(g_drm_err),
             "OK crtc=%u fb=%u conn=%u mode=%ux%u hold=%ds",
             pick_crtc, fb2.fb_id, pick_connector,
             pick_mode.hdisplay, pick_mode.vdisplay, hold_secs);
    return DRM_OK;
}

/* =========================================================================
 * JNI entry points. Class: com.westlake.ohostests.hello.DrmInProcessBridge
 *
 *   static int  nativePresent(int holdSecs)
 *   static String nativeLastError()
 * ========================================================================= */

JNIEXPORT jint JNICALL
Java_com_westlake_ohostests_hello_DrmInProcessBridge_nativePresent(
        JNIEnv *env, jclass cls, jint holdSecs) {
    /* holdSecs clamped on the C side to a sane range; the Java
     * caller normalizes too (driver default is 6s). */
    int hs = (int)holdSecs;
    if (hs < 0) hs = 0;
    if (hs > 120) hs = 120;
    return (jint)drm_present_red(hs);
}

JNIEXPORT jstring JNICALL
Java_com_westlake_ohostests_hello_DrmInProcessBridge_nativeLastError(
        JNIEnv *env, jclass cls) {
    return (*env)->NewStringUTF(env, g_drm_err);
}
