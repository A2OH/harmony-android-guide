/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * drm_probe — standalone aarch64 OHOS DRM/KMS probe.
 *
 * Purpose (PF-ohos-mvp-003 step 3 — de-risk the toolchain BEFORE wiring
 * a JNI helper into dalvikvm):
 *
 *   1. open("/dev/dri/card0", O_RDWR)
 *   2. enumerate connectors / encoders / CRTCs / planes / framebuffers
 *   3. print every connector's modes
 *   4. attempt SET_MASTER (may fail with EBUSY if composer_host holds it;
 *      that's fine — we just report)
 *   5. exit cleanly so subsequent runs of composer_host aren't blocked
 *
 * Cross-compiled with the OHOS LLVM at /home/dspfac/openharmony/prebuilts/.
 * Same toolchain as dalvikvm; sysroot at
 * dalvik-port/ohos-sysroot which has full DRM uapi headers.
 *
 * Run on the board:
 *   hdc file send drm_probe /data/local/tmp/drm_probe
 *   hdc shell "chmod 0755 /data/local/tmp/drm_probe && /data/local/tmp/drm_probe"
 */
#include <fcntl.h>
#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>
#include <string.h>
#include <sys/ioctl.h>
#include <unistd.h>
#include <errno.h>

#include <drm/drm.h>
#include <drm/drm_mode.h>
#include <drm/drm_fourcc.h>

/* These connection-state constants are libdrm-internal; the kernel
 * uapi header doesn't expose them. Hand-define to keep the probe
 * standalone (matches libdrm xf86drmMode.h). */
#define DRM_MODE_CONNECTED         1
#define DRM_MODE_DISCONNECTED      2
#define DRM_MODE_UNKNOWNCONNECTION 3

static const char *conn_type_str(uint32_t t) {
    switch (t) {
    case DRM_MODE_CONNECTOR_Unknown:     return "Unknown";
    case DRM_MODE_CONNECTOR_VGA:         return "VGA";
    case DRM_MODE_CONNECTOR_DVII:        return "DVI-I";
    case DRM_MODE_CONNECTOR_DVID:        return "DVI-D";
    case DRM_MODE_CONNECTOR_DVIA:        return "DVI-A";
    case DRM_MODE_CONNECTOR_Composite:   return "Composite";
    case DRM_MODE_CONNECTOR_SVIDEO:      return "S-Video";
    case DRM_MODE_CONNECTOR_LVDS:        return "LVDS";
    case DRM_MODE_CONNECTOR_Component:   return "Component";
    case DRM_MODE_CONNECTOR_9PinDIN:     return "9-Pin DIN";
    case DRM_MODE_CONNECTOR_DisplayPort: return "DisplayPort";
    case DRM_MODE_CONNECTOR_HDMIA:       return "HDMI-A";
    case DRM_MODE_CONNECTOR_HDMIB:       return "HDMI-B";
    case DRM_MODE_CONNECTOR_TV:          return "TV";
    case DRM_MODE_CONNECTOR_eDP:         return "eDP";
    case DRM_MODE_CONNECTOR_VIRTUAL:     return "Virtual";
    case DRM_MODE_CONNECTOR_DSI:         return "DSI";
    case DRM_MODE_CONNECTOR_DPI:         return "DPI";
    case DRM_MODE_CONNECTOR_WRITEBACK:   return "Writeback";
    default:                             return "?";
    }
}

static const char *conn_status_str(uint32_t s) {
    switch (s) {
    case DRM_MODE_CONNECTED:        return "connected";
    case DRM_MODE_DISCONNECTED:     return "disconnected";
    case DRM_MODE_UNKNOWNCONNECTION:return "unknown";
    default:                        return "?";
    }
}

int main(void) {
    printf("== drm_probe (PF-ohos-mvp-003) — pid=%d ==\n", getpid());

    int fd = open("/dev/dri/card0", O_RDWR | O_CLOEXEC);
    if (fd < 0) {
        printf("FAIL: open /dev/dri/card0: %s\n", strerror(errno));
        return 1;
    }
    printf("ok: opened /dev/dri/card0 fd=%d\n", fd);

    /* Try SET_MASTER right away (no arg). */
    if (ioctl(fd, DRM_IOCTL_SET_MASTER, 0) == 0) {
        printf("ok: SET_MASTER succeeded (we are DRM master)\n");
    } else {
        printf("note: SET_MASTER failed: %s (composer_host likely holds master)\n",
               strerror(errno));
    }

    /* Get resources. */
    struct drm_mode_card_res res;
    memset(&res, 0, sizeof(res));
    if (ioctl(fd, DRM_IOCTL_MODE_GETRESOURCES, &res) < 0) {
        printf("FAIL: GETRESOURCES (count): %s\n", strerror(errno));
        close(fd);
        return 1;
    }
    printf("resources: %u fb / %u crtc / %u connector / %u encoder\n",
           res.count_fbs, res.count_crtcs, res.count_connectors, res.count_encoders);
    printf("  min res: %ux%u  max res: %ux%u\n",
           res.min_width, res.min_height, res.max_width, res.max_height);

    uint32_t *fb_ids        = calloc(res.count_fbs,        sizeof(uint32_t));
    uint32_t *crtc_ids      = calloc(res.count_crtcs,      sizeof(uint32_t));
    uint32_t *connector_ids = calloc(res.count_connectors, sizeof(uint32_t));
    uint32_t *encoder_ids   = calloc(res.count_encoders,   sizeof(uint32_t));
    res.fb_id_ptr        = (uintptr_t)fb_ids;
    res.crtc_id_ptr      = (uintptr_t)crtc_ids;
    res.connector_id_ptr = (uintptr_t)connector_ids;
    res.encoder_id_ptr   = (uintptr_t)encoder_ids;
    if (ioctl(fd, DRM_IOCTL_MODE_GETRESOURCES, &res) < 0) {
        printf("FAIL: GETRESOURCES (ids): %s\n", strerror(errno));
        close(fd);
        return 1;
    }

    printf("crtcs: ");
    for (uint32_t i = 0; i < res.count_crtcs; i++) printf("%u ", crtc_ids[i]);
    printf("\n");

    printf("encoders: ");
    for (uint32_t i = 0; i < res.count_encoders; i++) printf("%u ", encoder_ids[i]);
    printf("\n");

    /* Enumerate connectors. */
    for (uint32_t i = 0; i < res.count_connectors; i++) {
        struct drm_mode_get_connector c;
        memset(&c, 0, sizeof(c));
        c.connector_id = connector_ids[i];
        if (ioctl(fd, DRM_IOCTL_MODE_GETCONNECTOR, &c) < 0) {
            printf("  connector %u: GETCONNECTOR count failed: %s\n",
                   connector_ids[i], strerror(errno));
            continue;
        }
        /* Allocate buffers and re-query. */
        struct drm_mode_modeinfo *modes = calloc(c.count_modes, sizeof(*modes));
        uint32_t *enc = calloc(c.count_encoders, sizeof(uint32_t));
        uint32_t *prop_ids = calloc(c.count_props, sizeof(uint32_t));
        uint64_t *prop_values = calloc(c.count_props, sizeof(uint64_t));
        c.modes_ptr = (uintptr_t)modes;
        c.encoders_ptr = (uintptr_t)enc;
        c.props_ptr = (uintptr_t)prop_ids;
        c.prop_values_ptr = (uintptr_t)prop_values;
        if (ioctl(fd, DRM_IOCTL_MODE_GETCONNECTOR, &c) < 0) {
            printf("  connector %u: GETCONNECTOR data failed: %s\n",
                   connector_ids[i], strerror(errno));
            free(modes); free(enc); free(prop_ids); free(prop_values);
            continue;
        }
        printf("  connector %u: type=%s-%u status=%s encoder=%u %u modes  (mm %ux%u)\n",
               c.connector_id, conn_type_str(c.connector_type), c.connector_type_id,
               conn_status_str(c.connection), c.encoder_id,
               c.count_modes, c.mm_width, c.mm_height);
        for (uint32_t m = 0; m < c.count_modes && m < 3; m++) {
            printf("    mode[%u]: %ux%u @%u Hz \"%s\" clock=%u flags=0x%x type=0x%x\n",
                   m, modes[m].hdisplay, modes[m].vdisplay,
                   modes[m].vrefresh, modes[m].name,
                   modes[m].clock, modes[m].flags, modes[m].type);
        }
        free(modes); free(enc); free(prop_ids); free(prop_values);
    }

    /* Enumerate encoders. */
    for (uint32_t i = 0; i < res.count_encoders; i++) {
        struct drm_mode_get_encoder e;
        memset(&e, 0, sizeof(e));
        e.encoder_id = encoder_ids[i];
        if (ioctl(fd, DRM_IOCTL_MODE_GETENCODER, &e) < 0) {
            printf("  encoder %u: failed: %s\n", encoder_ids[i], strerror(errno));
            continue;
        }
        printf("  encoder %u: type=%u crtc=%u possible_crtcs=0x%x clones=0x%x\n",
               e.encoder_id, e.encoder_type, e.crtc_id,
               e.possible_crtcs, e.possible_clones);
    }

    /* Enumerate CRTCs. */
    for (uint32_t i = 0; i < res.count_crtcs; i++) {
        struct drm_mode_crtc crtc;
        memset(&crtc, 0, sizeof(crtc));
        crtc.crtc_id = crtc_ids[i];
        if (ioctl(fd, DRM_IOCTL_MODE_GETCRTC, &crtc) < 0) {
            printf("  crtc %u: failed: %s\n", crtc_ids[i], strerror(errno));
            continue;
        }
        printf("  crtc %u: fb=%u (%u,%u) mode_valid=%u mode=\"%s\" %ux%u\n",
               crtc.crtc_id, crtc.fb_id, crtc.x, crtc.y,
               crtc.mode_valid, crtc.mode.name,
               crtc.mode.hdisplay, crtc.mode.vdisplay);
    }

    /* Try to drop our master claim cleanly (no-op if we never had it). */
    if (ioctl(fd, DRM_IOCTL_DROP_MASTER, 0) == 0) {
        printf("ok: DROP_MASTER succeeded\n");
    }

    close(fd);
    printf("== drm_probe done ==\n");
    return 0;
}
