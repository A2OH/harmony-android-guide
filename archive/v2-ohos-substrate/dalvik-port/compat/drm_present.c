/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * drm_present — read BGRA8888 720x1280 pixels from stdin, present to
 *               /dev/dri/card0 DSI-1 via DRM/KMS legacy modeset.
 *
 * Companion to DrmPresenter.java (PF-ohos-mvp-003 MVP-2). The Java
 * pipeline (RedView.onDraw -> SoftwareCanvas -> per-row BGRA byte[])
 * forks this tool, pipes the rendered frame into stdin, and the tool
 * holds scan-out for ~hold_secs (default 12s) so a phone-camera or
 * hdmi-capture rig can witness the display.
 *
 * Inputs:
 *   argv[1] (optional, default 12)   seconds to hold scan-out
 *   stdin                            exactly width*height*4 bytes BGRA
 *                                    (little-endian XRGB8888 memory layout
 *                                     used by rk3568 dumb BO).
 *
 * Output (stderr):
 *   step-by-step progress for the parent to capture into a log file.
 *
 * Side effect: takes DRM master on /dev/dri/card0. The caller MUST
 * stop composer_host first (kill -9 pidof composer_host) — otherwise
 * SET_MASTER returns EBUSY.
 */
#include <fcntl.h>
#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>
#include <string.h>
#include <sys/ioctl.h>
#include <sys/mman.h>
#include <unistd.h>
#include <errno.h>

#include <drm/drm.h>
#include <drm/drm_mode.h>
#include <drm/drm_fourcc.h>

#define DRM_MODE_CONNECTED 1

static void log_err(const char *step, int err) {
    fprintf(stderr, "[drm_present] FAIL %s: %s\n", step, strerror(err));
}

int main(int argc, char **argv) {
    int hold_secs = (argc > 1) ? atoi(argv[1]) : 12;
    if (hold_secs <= 0) hold_secs = 12;

    fprintf(stderr, "[drm_present] start pid=%d hold=%ds\n",
            getpid(), hold_secs);

    int drm_fd = open("/dev/dri/card0", O_RDWR | O_CLOEXEC);
    if (drm_fd < 0) { log_err("open card0", errno); return 11; }
    fprintf(stderr, "[drm_present] opened card0 fd=%d\n", drm_fd);

    if (ioctl(drm_fd, DRM_IOCTL_SET_MASTER, 0) < 0) {
        log_err("SET_MASTER (composer_host still owns it?)", errno);
        close(drm_fd);
        return 12;
    }
    fprintf(stderr, "[drm_present] SET_MASTER OK\n");

    struct drm_mode_card_res res;
    memset(&res, 0, sizeof(res));
    if (ioctl(drm_fd, DRM_IOCTL_MODE_GETRESOURCES, &res) < 0) {
        log_err("GETRESOURCES (count)", errno); return 13;
    }
    uint32_t *crtc_ids      = calloc(res.count_crtcs,      sizeof(uint32_t));
    uint32_t *connector_ids = calloc(res.count_connectors, sizeof(uint32_t));
    uint32_t *encoder_ids   = calloc(res.count_encoders,   sizeof(uint32_t));
    res.crtc_id_ptr      = (uintptr_t)crtc_ids;
    res.connector_id_ptr = (uintptr_t)connector_ids;
    res.encoder_id_ptr   = (uintptr_t)encoder_ids;
    res.fb_id_ptr = 0; res.count_fbs = 0;
    if (ioctl(drm_fd, DRM_IOCTL_MODE_GETRESOURCES, &res) < 0) {
        log_err("GETRESOURCES", errno); return 14;
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
    if (!pick_connector) {
        fprintf(stderr, "[drm_present] FAIL: no connected connector\n");
        return 15;
    }
    fprintf(stderr, "[drm_present] connector=%u encoder=%u mode=%ux%u\n",
            pick_connector, pick_encoder, pick_mode.hdisplay, pick_mode.vdisplay);

    /* Resolve CRTC from encoder. */
    uint32_t pick_crtc = 0;
    struct drm_mode_get_encoder e;
    memset(&e, 0, sizeof(e));
    e.encoder_id = pick_encoder;
    if (ioctl(drm_fd, DRM_IOCTL_MODE_GETENCODER, &e) < 0) {
        log_err("GETENCODER", errno); return 16;
    }
    if (e.crtc_id) pick_crtc = e.crtc_id;
    else {
        for (uint32_t i = 0; i < res.count_crtcs; i++) {
            if (e.possible_crtcs & (1u << i)) { pick_crtc = crtc_ids[i]; break; }
        }
    }
    if (!pick_crtc) {
        fprintf(stderr, "[drm_present] FAIL: no CRTC for encoder\n"); return 17;
    }
    fprintf(stderr, "[drm_present] crtc=%u\n", pick_crtc);

    /* Allocate dumb BO matching connector's mode. */
    struct drm_mode_create_dumb cdumb;
    memset(&cdumb, 0, sizeof(cdumb));
    cdumb.width  = pick_mode.hdisplay;
    cdumb.height = pick_mode.vdisplay;
    cdumb.bpp = 32;
    if (ioctl(drm_fd, DRM_IOCTL_MODE_CREATE_DUMB, &cdumb) < 0) {
        log_err("CREATE_DUMB", errno); return 18;
    }
    fprintf(stderr, "[drm_present] dumb handle=%u pitch=%u size=%llu\n",
            cdumb.handle, cdumb.pitch,
            (unsigned long long)cdumb.size);

    struct drm_mode_map_dumb mdumb;
    memset(&mdumb, 0, sizeof(mdumb));
    mdumb.handle = cdumb.handle;
    if (ioctl(drm_fd, DRM_IOCTL_MODE_MAP_DUMB, &mdumb) < 0) {
        log_err("MAP_DUMB", errno); return 19;
    }
    uint8_t *bo = mmap(NULL, cdumb.size, PROT_READ | PROT_WRITE,
                       MAP_SHARED, drm_fd, mdumb.offset);
    if (bo == MAP_FAILED) { log_err("mmap dumb", errno); return 20; }
    fprintf(stderr, "[drm_present] mmap'd %llu bytes\n",
            (unsigned long long)cdumb.size);

    /* Slurp BGRA pixels from stdin into the BO.
     * Expected size = width*height*4 (but BO pitch may be > width*4
     * due to alignment, so write per-row). */
    size_t row_bytes = (size_t)pick_mode.hdisplay * 4;
    size_t total_rows = pick_mode.vdisplay;
    size_t total_in_bytes = 0;
    uint8_t *rowbuf = malloc(row_bytes);
    if (!rowbuf) { fprintf(stderr, "[drm_present] OOM\n"); return 21; }
    for (size_t y = 0; y < total_rows; y++) {
        size_t off = 0;
        while (off < row_bytes) {
            ssize_t n = read(0, rowbuf + off, row_bytes - off);
            if (n <= 0) {
                fprintf(stderr, "[drm_present] short stdin: y=%zu off=%zu err=%s\n",
                        y, off, strerror(errno));
                /* Use whatever we got + leave the rest as-is (likely zero from
                 * fresh dumb BO). Continue rather than abort, so the parent
                 * still gets useful display. */
                break;
            }
            off += (size_t)n;
            total_in_bytes += (size_t)n;
        }
        memcpy(bo + y * cdumb.pitch, rowbuf, off);
    }
    free(rowbuf);
    fprintf(stderr, "[drm_present] stdin read %zu bytes total (expected %zu)\n",
            total_in_bytes, row_bytes * total_rows);

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
        log_err("ADDFB2", errno); return 22;
    }
    fprintf(stderr, "[drm_present] ADDFB2 fb_id=%u\n", fb2.fb_id);

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
        log_err("SETCRTC", errno); return 23;
    }
    fprintf(stderr,
            "[drm_present] SCANOUT_LIVE: crtc=%u fb=%u conn=%u mode=%ux%u\n",
            pick_crtc, fb2.fb_id, pick_connector,
            pick_mode.hdisplay, pick_mode.vdisplay);

    /* On stdout, emit the success marker so the Java parent can grep it. */
    printf("DRM_SCANOUT_OK crtc=%u fb=%u conn=%u mode=%ux%u\n",
           pick_crtc, fb2.fb_id, pick_connector,
           pick_mode.hdisplay, pick_mode.vdisplay);
    fflush(stdout);

    fprintf(stderr, "[drm_present] holding scan-out %d seconds\n", hold_secs);
    sleep(hold_secs);

    /* Teardown — leave panel in a known state (composer_host will respawn). */
    uint32_t rm_fb = fb2.fb_id;
    ioctl(drm_fd, DRM_IOCTL_MODE_RMFB, &rm_fb);
    struct drm_mode_destroy_dumb ddumb;
    memset(&ddumb, 0, sizeof(ddumb));
    ddumb.handle = cdumb.handle;
    ioctl(drm_fd, DRM_IOCTL_MODE_DESTROY_DUMB, &ddumb);
    ioctl(drm_fd, DRM_IOCTL_DROP_MASTER, 0);
    munmap(bo, cdumb.size);
    close(drm_fd);
    fprintf(stderr, "[drm_present] clean exit\n");
    return 0;
}
