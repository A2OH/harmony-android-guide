/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * drm_red — standalone aarch64 OHOS DRM/KMS modesetter that paints
 *           pure red on the DSI panel (PF-ohos-mvp-003).
 *
 * Pipeline:
 *   1. open /dev/dri/card0
 *   2. SET_MASTER (will fail unless composer_host is stopped first)
 *   3. enumerate resources → pick connector DSI-1, its encoder, its CRTC
 *   4. CREATE_DUMB (BGRA8888 720x1280)  → handle + pitch + size
 *   5. MAP_DUMB → mmap offset → mmap the buffer
 *   6. memset to BGRA pure red
 *   7. ADDFB2 → fb_id
 *   8. SETCRTC: bind fb_id to CRTC + connector + mode
 *   9. sleep 8 seconds for camera capture
 *  10. DROP_MASTER, close
 *
 * Run on board (composer_host MUST be stopped first, else SET_MASTER
 * fails with EBUSY):
 *
 *   hdc shell "service_control stop hdf_devmgr"        # this is wrong on this build;
 *                                                       use kill instead
 *   hdc shell "kill -9 <composer_host pid>"            # see ps -ef | grep composer
 *   hdc shell "/data/local/tmp/drm_red"
 *
 * Argument-free; geometry is auto-discovered from the DSI-1 connector.
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

static int drm_fd = -1;

#define DIE(msg)                                                              \
    do {                                                                      \
        fprintf(stderr, "FAIL: %s: %s\n", msg, strerror(errno));              \
        return 1;                                                             \
    } while (0)

int main(int argc, char **argv) {
    int sleep_secs = 10;
    if (argc > 1) sleep_secs = atoi(argv[1]);

    printf("== drm_red (PF-ohos-mvp-003) — pid=%d sleep=%ds ==\n",
           getpid(), sleep_secs);

    drm_fd = open("/dev/dri/card0", O_RDWR | O_CLOEXEC);
    if (drm_fd < 0) DIE("open /dev/dri/card0");
    printf("ok: opened /dev/dri/card0 fd=%d\n", drm_fd);

    if (ioctl(drm_fd, DRM_IOCTL_SET_MASTER, 0) < 0) {
        fprintf(stderr,
                "FAIL: SET_MASTER: %s — composer_host still owns master.\n"
                "      Stop it first:  kill -9 $(pidof composer_host)\n",
                strerror(errno));
        close(drm_fd);
        return 2;
    }
    printf("ok: SET_MASTER (we are DRM master)\n");

    /* Get resources. */
    struct drm_mode_card_res res;
    memset(&res, 0, sizeof(res));
    if (ioctl(drm_fd, DRM_IOCTL_MODE_GETRESOURCES, &res) < 0)
        DIE("GETRESOURCES count");
    uint32_t *crtc_ids      = calloc(res.count_crtcs,      sizeof(uint32_t));
    uint32_t *connector_ids = calloc(res.count_connectors, sizeof(uint32_t));
    uint32_t *encoder_ids   = calloc(res.count_encoders,   sizeof(uint32_t));
    res.crtc_id_ptr      = (uintptr_t)crtc_ids;
    res.connector_id_ptr = (uintptr_t)connector_ids;
    res.encoder_id_ptr   = (uintptr_t)encoder_ids;
    res.count_fbs = 0;          /* don't enumerate fbs */
    res.fb_id_ptr = 0;
    if (ioctl(drm_fd, DRM_IOCTL_MODE_GETRESOURCES, &res) < 0)
        DIE("GETRESOURCES");
    printf("res: %u crtc %u connector %u encoder\n",
           res.count_crtcs, res.count_connectors, res.count_encoders);

    /* Pick the connected connector. */
    uint32_t pick_connector = 0;
    uint32_t pick_encoder   = 0;
    struct drm_mode_modeinfo pick_mode;
    memset(&pick_mode, 0, sizeof(pick_mode));
    for (uint32_t i = 0; i < res.count_connectors; i++) {
        struct drm_mode_get_connector c;
        memset(&c, 0, sizeof(c));
        c.connector_id = connector_ids[i];
        if (ioctl(drm_fd, DRM_IOCTL_MODE_GETCONNECTOR, &c) < 0) continue;
        if (c.connection != DRM_MODE_CONNECTED || c.count_modes == 0) {
            printf("  skip connector %u (status=%u modes=%u)\n",
                   c.connector_id, c.connection, c.count_modes);
            continue;
        }
        struct drm_mode_modeinfo *modes = calloc(c.count_modes, sizeof(*modes));
        uint32_t *enc = calloc(c.count_encoders, sizeof(uint32_t));
        c.modes_ptr = (uintptr_t)modes;
        c.encoders_ptr = (uintptr_t)enc;
        c.props_ptr = 0;
        c.prop_values_ptr = 0;
        c.count_props = 0;
        if (ioctl(drm_fd, DRM_IOCTL_MODE_GETCONNECTOR, &c) < 0) {
            free(modes); free(enc);
            continue;
        }
        printf("  pick connector %u  encoder=%u  mode=%ux%u \"%s\"\n",
               c.connector_id, c.encoder_id, modes[0].hdisplay,
               modes[0].vdisplay, modes[0].name);
        pick_connector = c.connector_id;
        pick_encoder = c.encoder_id;
        pick_mode = modes[0];
        free(modes); free(enc);
        break;
    }
    if (!pick_connector) {
        fprintf(stderr, "FAIL: no connected connector with a mode\n");
        return 3;
    }

    /* Resolve CRTC from encoder. */
    uint32_t pick_crtc = 0;
    {
        struct drm_mode_get_encoder e;
        memset(&e, 0, sizeof(e));
        e.encoder_id = pick_encoder;
        if (ioctl(drm_fd, DRM_IOCTL_MODE_GETENCODER, &e) < 0)
            DIE("GETENCODER");
        if (e.crtc_id) {
            pick_crtc = e.crtc_id;
            printf("  encoder %u currently bound to crtc=%u\n",
                   pick_encoder, pick_crtc);
        } else {
            /* Walk possible_crtcs and pick the first one. */
            for (uint32_t i = 0; i < res.count_crtcs; i++) {
                if (e.possible_crtcs & (1u << i)) {
                    pick_crtc = crtc_ids[i];
                    printf("  picked crtc=%u from possible_crtcs=0x%x\n",
                           pick_crtc, e.possible_crtcs);
                    break;
                }
            }
        }
    }
    if (!pick_crtc) {
        fprintf(stderr, "FAIL: could not resolve CRTC\n");
        return 4;
    }

    /* Create a dumb BO. */
    struct drm_mode_create_dumb cdumb;
    memset(&cdumb, 0, sizeof(cdumb));
    cdumb.width  = pick_mode.hdisplay;
    cdumb.height = pick_mode.vdisplay;
    cdumb.bpp    = 32;
    if (ioctl(drm_fd, DRM_IOCTL_MODE_CREATE_DUMB, &cdumb) < 0)
        DIE("CREATE_DUMB");
    printf("ok: CREATE_DUMB handle=%u pitch=%u size=%llu\n",
           cdumb.handle, cdumb.pitch, (unsigned long long)cdumb.size);

    /* Map it. */
    struct drm_mode_map_dumb mdumb;
    memset(&mdumb, 0, sizeof(mdumb));
    mdumb.handle = cdumb.handle;
    if (ioctl(drm_fd, DRM_IOCTL_MODE_MAP_DUMB, &mdumb) < 0)
        DIE("MAP_DUMB");
    void *bo = mmap(NULL, cdumb.size, PROT_READ | PROT_WRITE,
                    MAP_SHARED, drm_fd, mdumb.offset);
    if (bo == MAP_FAILED) DIE("mmap dumb");
    printf("ok: mmap'd %llu bytes at %p\n",
           (unsigned long long)cdumb.size, bo);

    /* Fill with pure red.  rk3568 dumb BO is BGRA/ARGB layout @ 32 bpp;
     * legacy ADDFB takes depth/bpp = 24/32 which is XRGB8888 little-endian.
     * That means in memory: B, G, R, X (or B,G,R,A for ARGB).
     * Red = 0x00FF0000 stored as bytes 00 00 FF 00 little-endian on
     * 32-bit word access — but byte order in memory for ARGB8888
     * (drm_fourcc XR24/AR24 little-endian) is:  B G R A.
     * Pure red is therefore  0x00 0x00 0xFF 0xFF  per pixel.
     */
    uint32_t *px = (uint32_t *)bo;
    size_t pixels = cdumb.size / 4;
    for (size_t i = 0; i < pixels; i++) px[i] = 0xFFFF0000u; /* ARGB pure red */
    printf("ok: filled %zu pixels with pure red (0xFFFF0000)\n", pixels);

    /* Add as a framebuffer. Use ADDFB2 with DRM_FORMAT_XRGB8888 (XR24)
     * to match what most rk3568 planes accept by default. */
    struct drm_mode_fb_cmd2 fb2;
    memset(&fb2, 0, sizeof(fb2));
    fb2.width  = pick_mode.hdisplay;
    fb2.height = pick_mode.vdisplay;
    fb2.pixel_format = DRM_FORMAT_XRGB8888;
    fb2.handles[0] = cdumb.handle;
    fb2.pitches[0] = cdumb.pitch;
    fb2.offsets[0] = 0;
    if (ioctl(drm_fd, DRM_IOCTL_MODE_ADDFB2, &fb2) < 0)
        DIE("ADDFB2 XRGB8888");
    printf("ok: ADDFB2 fb_id=%u (XRGB8888 %ux%u pitch=%u)\n",
           fb2.fb_id, fb2.width, fb2.height, cdumb.pitch);

    /* Set CRTC: connect fb_id to crtc + connector + mode. */
    uint32_t conn_arr[1] = { pick_connector };
    struct drm_mode_crtc setcrtc;
    memset(&setcrtc, 0, sizeof(setcrtc));
    setcrtc.crtc_id = pick_crtc;
    setcrtc.fb_id   = fb2.fb_id;
    setcrtc.x       = 0;
    setcrtc.y       = 0;
    setcrtc.set_connectors_ptr = (uintptr_t)conn_arr;
    setcrtc.count_connectors   = 1;
    setcrtc.mode = pick_mode;
    setcrtc.mode_valid = 1;
    if (ioctl(drm_fd, DRM_IOCTL_MODE_SETCRTC, &setcrtc) < 0) {
        fprintf(stderr, "FAIL: SETCRTC: %s\n", strerror(errno));
        return 5;
    }
    printf("ok: SETCRTC crtc=%u fb=%u conn=%u mode=%ux%u\n",
           pick_crtc, fb2.fb_id, pick_connector,
           pick_mode.hdisplay, pick_mode.vdisplay);

    printf("== holding scan-out for %d seconds — TAKE A PHOTO NOW ==\n",
           sleep_secs);
    fflush(stdout);
    sleep(sleep_secs);

    /* Best-effort teardown. */
    /* RMFB so the buffer object can be reclaimed. */
    uint32_t fb_to_rm = fb2.fb_id;
    if (ioctl(drm_fd, DRM_IOCTL_MODE_RMFB, &fb_to_rm) < 0)
        fprintf(stderr, "warn: RMFB: %s\n", strerror(errno));

    /* Destroy dumb. */
    struct drm_mode_destroy_dumb ddumb;
    memset(&ddumb, 0, sizeof(ddumb));
    ddumb.handle = cdumb.handle;
    if (ioctl(drm_fd, DRM_IOCTL_MODE_DESTROY_DUMB, &ddumb) < 0)
        fprintf(stderr, "warn: DESTROY_DUMB: %s\n", strerror(errno));

    if (ioctl(drm_fd, DRM_IOCTL_DROP_MASTER, 0) < 0)
        fprintf(stderr, "warn: DROP_MASTER: %s\n", strerror(errno));

    munmap(bo, cdumb.size);
    close(drm_fd);
    printf("== drm_red done ==\n");
    return 0;
}
