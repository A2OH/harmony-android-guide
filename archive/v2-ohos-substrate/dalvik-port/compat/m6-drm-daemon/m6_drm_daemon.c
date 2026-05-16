/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * m6-drm-daemon — long-lived OHOS DRM/KMS owner with vsync'd page flip.
 *
 * Phase 2 M6 daemon, OHOS variant (sibling of aosp-surface-daemon-port for
 * Android Phase 1). Owns /dev/dri/card0 master, allocates two dumb BOs at
 * the DSI-1 native mode, exposes an AF_UNIX SOCK_SEQPACKET listener at
 * /data/local/tmp/westlake/m6-drm.sock (or abstract @m6-drm.sock fallback),
 * and accepts memfd handoffs from dalvikvm-side clients. On each frame:
 *
 *   1. recvmsg() the client's memfd via SCM_RIGHTS.
 *   2. mmap() it (size = width*height*4 BGRA).
 *   3. memcpy() into the back buffer's dumb BO mapping.
 *   4. drmModePageFlip(back fb_id, DRM_MODE_PAGE_FLIP_EVENT, slot).
 *   5. poll(drm_fd) for DRM_EVENT_FLIP_COMPLETE.
 *   6. swap front/back; send 4-byte 'A','C','K',slot to client.
 *
 * Modes:
 *   --self-test            no AF_UNIX; daemon page-flips red/blue at 60 Hz
 *                          for SELF_TEST_SECS (default 5s) to validate
 *                          DRM/page-flip/vsync standalone. Exits cleanly.
 *   --accept-client        runs the AF_UNIX listener; one client at a time.
 *                          (Default; standalone test client uses this.)
 *
 * Composer-host coexistence: tries SET_MASTER first without killing
 * composer_host. If SET_MASTER fails with EBUSY, falls back to killing
 * composer_host (same path as MVP-2 drm_present). Either way,
 * drmDropMaster on exit so composer_host can reclaim.
 *
 * Wire format (binary, little-endian):
 *
 *   client→daemon (per frame):
 *     sendmsg with 1 fd (memfd_create'd, ftruncate'd to width*height*4)
 *     ancillary SCM_RIGHTS. Payload bytes (12):
 *       uint32  magic = 0x4D364652  ("M6FR")
 *       uint32  frame_seq
 *       uint32  size_bytes (= width*height*4, sanity check)
 *
 *   daemon→client (after flip):
 *     send (no fd). Payload bytes (12):
 *       uint32  magic = 0x4D36414B  ("M6AK")
 *       uint32  frame_seq (echo)
 *       uint32  flip_ns_low / actually a status code: 0=OK, !=0=fail
 *
 * Wire is intentionally fresh — no Phase 1 (DLST) compat. Phase 1 was a
 * pipe-to-host-APK on Android with a different consumer (SurfaceView blit);
 * here the consumer is the DRM kernel and the producer/consumer share no
 * legacy wire. CR35 (A11→A15 drift) doesn't apply.
 *
 * Build: clang --target=aarch64-linux-ohos --sysroot=<ohos-sysroot> -static
 *        -O2 -o m6-drm-daemon m6_drm_daemon.c
 */
#define _GNU_SOURCE
#include <dirent.h>
#include <errno.h>
#include <fcntl.h>
#include <poll.h>
#include <stddef.h>
#include <signal.h>
#include <stdarg.h>
#include <stdint.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/ioctl.h>
#include <sys/mman.h>
#include <sys/socket.h>
#include <sys/stat.h>
#include <sys/syscall.h>
#include <sys/time.h>
#include <sys/types.h>
#include <sys/un.h>
#include <sys/wait.h>
#include <time.h>
#include <unistd.h>

#include <drm/drm.h>
#include <drm/drm_mode.h>
#include <drm/drm_fourcc.h>

/* DRM event reader needs these — they're plain C, no libdrm needed. */
#ifndef DRM_EVENT_FLIP_COMPLETE
#define DRM_EVENT_FLIP_COMPLETE 0x02
#endif
#ifndef DRM_MODE_PAGE_FLIP_EVENT
#define DRM_MODE_PAGE_FLIP_EVENT 0x01
#endif
#ifndef DRM_MODE_CONNECTED
#define DRM_MODE_CONNECTED 1
#endif

/* memfd_create is in OHOS uapi but not always in the libc headers. */
#ifndef MFD_CLOEXEC
#define MFD_CLOEXEC 0x0001
#endif

#ifndef SYS_memfd_create
#define SYS_memfd_create 279  /* arm64 */
#endif

#define DEFAULT_SOCKET_PATH "/data/local/tmp/westlake/m6-drm.sock"
#define ABSTRACT_SOCKET_PATH "@m6-drm.sock"
#define MAX_BACKLOG 4
#define MAX_FRAME_PAYLOAD 32
#define MSG_MAGIC_FRAME 0x4D364652u  /* M6FR */
#define MSG_MAGIC_ACK   0x4D36414Bu  /* M6AK */
#define WIRE_SIZE 12

static int g_log_fd = 2; /* stderr */
static volatile sig_atomic_t g_stop = 0;

static void onsig(int sig) { (void)sig; g_stop = 1; }

__attribute__((format(printf, 1, 2)))
static void logf_(const char *fmt, ...) {
    char buf[1024];
    va_list ap;
    va_start(ap, fmt);
    int n = vsnprintf(buf, sizeof(buf), fmt, ap);
    va_end(ap);
    if (n < 0) return;
    if ((size_t)n > sizeof(buf) - 1) n = sizeof(buf) - 1;
    /* prefix with monotonic timestamp ms */
    struct timespec ts;
    clock_gettime(CLOCK_MONOTONIC, &ts);
    uint64_t ms = (uint64_t)ts.tv_sec * 1000 + ts.tv_nsec / 1000000;
    char hdr[64];
    int hn = snprintf(hdr, sizeof(hdr), "[m6drm %llu] ",
                      (unsigned long long)ms);
    if (hn > 0) (void)!write(g_log_fd, hdr, hn);
    (void)!write(g_log_fd, buf, n);
    (void)!write(g_log_fd, "\n", 1);
}

/* ---------------- DRM helpers ------------------------------------------- */

struct dumb_bo {
    uint32_t handle;
    uint32_t pitch;
    uint64_t size;
    uint8_t *map;
    uint32_t fb_id;
};

struct drm_state {
    int fd;
    uint32_t connector_id;
    uint32_t encoder_id;
    uint32_t crtc_id;
    struct drm_mode_modeinfo mode;
    struct dumb_bo bo[2]; /* double buffer */
    int front; /* index of currently-scanning bo */
    int composer_host_killed;
};

static int try_set_master(int fd) {
    if (ioctl(fd, DRM_IOCTL_SET_MASTER, 0) == 0) return 0;
    return -errno;
}

static int kill_composer_host(void) {
    /* Scan /proc/<pid>/comm for "composer_host" and SIGKILL it.
     * Avoid popen/pidof to keep no shell dependency. */
    int n = 0;
    DIR *d = opendir("/proc");
    if (!d) return -errno;
    struct dirent *de;
    while ((de = readdir(d)) != NULL) {
        if (de->d_name[0] < '0' || de->d_name[0] > '9') continue;
        char path[64], comm[64];
        snprintf(path, sizeof(path), "/proc/%s/comm", de->d_name);
        int cfd = open(path, O_RDONLY | O_CLOEXEC);
        if (cfd < 0) continue;
        ssize_t r = read(cfd, comm, sizeof(comm) - 1);
        close(cfd);
        if (r <= 0) continue;
        comm[r] = '\0';
        char *nl = strchr(comm, '\n'); if (nl) *nl = '\0';
        if (strcmp(comm, "composer_host") != 0) continue;
        long pid = strtol(de->d_name, NULL, 10);
        if (pid > 0 && kill((pid_t)pid, SIGKILL) == 0) {
            logf_("killed composer_host pid=%ld", pid);
            n++;
        }
    }
    closedir(d);
    /* Give the kernel a moment to release master refcounts. */
    if (n > 0) usleep(200 * 1000);
    return n;
}

static int drm_open_and_master(struct drm_state *s, int allow_kill) {
    s->fd = open("/dev/dri/card0", O_RDWR | O_CLOEXEC);
    if (s->fd < 0) {
        logf_("open card0 failed: %s", strerror(errno));
        return -1;
    }
    int rc = try_set_master(s->fd);
    if (rc == 0) {
        logf_("SET_MASTER OK without killing composer_host (master was free)");
        return 0;
    }
    logf_("SET_MASTER first try failed: %s (errno=%d) — composer_host owns it?",
          strerror(-rc), -rc);
    if (!allow_kill) return -1;
    int killed = kill_composer_host();
    if (killed < 0) {
        logf_("kill_composer_host failed");
        return -1;
    }
    rc = try_set_master(s->fd);
    if (rc != 0) {
        logf_("SET_MASTER second try failed: %s", strerror(-rc));
        return -1;
    }
    s->composer_host_killed = 1;
    logf_("SET_MASTER OK after killing composer_host (killed=%d)", killed);
    return 0;
}

static int drm_pick_connector(struct drm_state *s) {
    struct drm_mode_card_res res;
    memset(&res, 0, sizeof(res));
    if (ioctl(s->fd, DRM_IOCTL_MODE_GETRESOURCES, &res) < 0) return -1;
    uint32_t *crtcs = calloc(res.count_crtcs, sizeof(uint32_t));
    uint32_t *conns = calloc(res.count_connectors, sizeof(uint32_t));
    uint32_t *encs  = calloc(res.count_encoders, sizeof(uint32_t));
    if (!crtcs || !conns || !encs) {
        free(crtcs); free(conns); free(encs);
        return -1;
    }
    res.crtc_id_ptr = (uintptr_t)crtcs;
    res.connector_id_ptr = (uintptr_t)conns;
    res.encoder_id_ptr = (uintptr_t)encs;
    res.fb_id_ptr = 0; res.count_fbs = 0;
    if (ioctl(s->fd, DRM_IOCTL_MODE_GETRESOURCES, &res) < 0) {
        free(crtcs); free(conns); free(encs);
        return -1;
    }
    for (uint32_t i = 0; i < res.count_connectors; i++) {
        struct drm_mode_get_connector c;
        memset(&c, 0, sizeof(c));
        c.connector_id = conns[i];
        if (ioctl(s->fd, DRM_IOCTL_MODE_GETCONNECTOR, &c) < 0) continue;
        if (c.connection != DRM_MODE_CONNECTED || c.count_modes == 0) continue;
        struct drm_mode_modeinfo *modes = calloc(c.count_modes, sizeof(*modes));
        uint32_t *enc = calloc(c.count_encoders, sizeof(uint32_t));
        c.modes_ptr = (uintptr_t)modes;
        c.encoders_ptr = (uintptr_t)enc;
        c.props_ptr = 0; c.prop_values_ptr = 0; c.count_props = 0;
        if (ioctl(s->fd, DRM_IOCTL_MODE_GETCONNECTOR, &c) >= 0) {
            s->connector_id = c.connector_id;
            s->encoder_id = c.encoder_id;
            s->mode = modes[0];
            free(modes); free(enc);
            /* Resolve CRTC from encoder. */
            struct drm_mode_get_encoder e;
            memset(&e, 0, sizeof(e));
            e.encoder_id = s->encoder_id;
            if (ioctl(s->fd, DRM_IOCTL_MODE_GETENCODER, &e) < 0) {
                free(crtcs); free(conns); free(encs);
                return -1;
            }
            if (e.crtc_id) s->crtc_id = e.crtc_id;
            else {
                for (uint32_t k = 0; k < res.count_crtcs; k++) {
                    if (e.possible_crtcs & (1u << k)) { s->crtc_id = crtcs[k]; break; }
                }
            }
            free(crtcs); free(conns); free(encs);
            return s->crtc_id ? 0 : -1;
        }
        free(modes); free(enc);
    }
    free(crtcs); free(conns); free(encs);
    return -1;
}

static int drm_alloc_dumb(struct drm_state *s, int idx) {
    struct drm_mode_create_dumb cdumb;
    memset(&cdumb, 0, sizeof(cdumb));
    cdumb.width = s->mode.hdisplay;
    cdumb.height = s->mode.vdisplay;
    cdumb.bpp = 32;
    if (ioctl(s->fd, DRM_IOCTL_MODE_CREATE_DUMB, &cdumb) < 0) return -1;
    s->bo[idx].handle = cdumb.handle;
    s->bo[idx].pitch = cdumb.pitch;
    s->bo[idx].size = cdumb.size;

    struct drm_mode_map_dumb mdumb;
    memset(&mdumb, 0, sizeof(mdumb));
    mdumb.handle = cdumb.handle;
    if (ioctl(s->fd, DRM_IOCTL_MODE_MAP_DUMB, &mdumb) < 0) return -1;
    s->bo[idx].map = mmap(NULL, cdumb.size, PROT_READ | PROT_WRITE,
                          MAP_SHARED, s->fd, mdumb.offset);
    if (s->bo[idx].map == MAP_FAILED) return -1;

    struct drm_mode_fb_cmd2 fb2;
    memset(&fb2, 0, sizeof(fb2));
    fb2.width = s->mode.hdisplay;
    fb2.height = s->mode.vdisplay;
    fb2.pixel_format = DRM_FORMAT_XRGB8888;
    fb2.handles[0] = cdumb.handle;
    fb2.pitches[0] = cdumb.pitch;
    fb2.offsets[0] = 0;
    if (ioctl(s->fd, DRM_IOCTL_MODE_ADDFB2, &fb2) < 0) return -1;
    s->bo[idx].fb_id = fb2.fb_id;
    logf_("dumb[%d] handle=%u pitch=%u size=%llu fb_id=%u",
          idx, cdumb.handle, cdumb.pitch,
          (unsigned long long)cdumb.size, fb2.fb_id);
    return 0;
}

static int drm_setcrtc(struct drm_state *s, int idx) {
    uint32_t conn_arr[1] = { s->connector_id };
    struct drm_mode_crtc setcrtc;
    memset(&setcrtc, 0, sizeof(setcrtc));
    setcrtc.crtc_id = s->crtc_id;
    setcrtc.fb_id = s->bo[idx].fb_id;
    setcrtc.set_connectors_ptr = (uintptr_t)conn_arr;
    setcrtc.count_connectors = 1;
    setcrtc.mode = s->mode;
    setcrtc.mode_valid = 1;
    if (ioctl(s->fd, DRM_IOCTL_MODE_SETCRTC, &setcrtc) < 0) return -1;
    s->front = idx;
    return 0;
}

static int drm_page_flip(struct drm_state *s, int back_idx, uint64_t user_data) {
    struct drm_mode_crtc_page_flip flip;
    memset(&flip, 0, sizeof(flip));
    flip.crtc_id = s->crtc_id;
    flip.fb_id = s->bo[back_idx].fb_id;
    flip.flags = DRM_MODE_PAGE_FLIP_EVENT;
    flip.user_data = user_data;
    if (ioctl(s->fd, DRM_IOCTL_MODE_PAGE_FLIP, &flip) < 0) return -errno;
    return 0;
}

static int drm_wait_flip(struct drm_state *s, uint64_t *out_user_data,
                         uint64_t *out_tv_sec, uint32_t *out_tv_usec) {
    struct pollfd pfd = { .fd = s->fd, .events = POLLIN };
    int rc = poll(&pfd, 1, 1000); /* 1s timeout — way longer than 16.7ms */
    if (rc <= 0) return -ETIME;
    /* Read DRM event header + body. drm_event_vblank = 24 bytes (vblank +
     * crtc_id + user_data). We just read until we see FLIP_COMPLETE. */
    struct {
        uint32_t type;
        uint32_t length;
        uint32_t user_data_lo;
        uint32_t user_data_hi;
        uint32_t tv_sec;
        uint32_t tv_usec;
        uint32_t sequence;
        uint32_t crtc_id;
    } ev;
    /* Older kernel layout: type, length, user_data(8B), tv_sec, tv_usec,
     * seq, [optional crtc_id]. We'll read what's available. */
    char buf[64];
    ssize_t n = read(s->fd, buf, sizeof(buf));
    if (n < 8) return -EIO;
    memcpy(&ev, buf, (size_t)n < sizeof(ev) ? (size_t)n : sizeof(ev));
    if (ev.type != DRM_EVENT_FLIP_COMPLETE) {
        logf_("unexpected DRM event type=0x%x", ev.type);
        return -EINVAL;
    }
    if (out_user_data) {
        uint64_t ud = ((uint64_t)ev.user_data_hi << 32) | ev.user_data_lo;
        *out_user_data = ud;
    }
    if (out_tv_sec) *out_tv_sec = ev.tv_sec;
    if (out_tv_usec) *out_tv_usec = ev.tv_usec;
    return 0;
}

static void drm_teardown(struct drm_state *s) {
    for (int i = 0; i < 2; i++) {
        if (s->bo[i].fb_id) {
            uint32_t fb = s->bo[i].fb_id;
            ioctl(s->fd, DRM_IOCTL_MODE_RMFB, &fb);
        }
        if (s->bo[i].map && s->bo[i].map != MAP_FAILED) {
            munmap(s->bo[i].map, s->bo[i].size);
        }
        if (s->bo[i].handle) {
            struct drm_mode_destroy_dumb d;
            memset(&d, 0, sizeof(d));
            d.handle = s->bo[i].handle;
            ioctl(s->fd, DRM_IOCTL_MODE_DESTROY_DUMB, &d);
        }
    }
    if (s->fd >= 0) {
        ioctl(s->fd, DRM_IOCTL_DROP_MASTER, 0);
        close(s->fd);
    }
}

/* ---------------- Fill helpers ------------------------------------------ */

static void fill_solid_bgra(uint8_t *map, uint32_t pitch,
                            uint32_t width, uint32_t height,
                            uint8_t b, uint8_t g, uint8_t r, uint8_t a) {
    for (uint32_t y = 0; y < height; y++) {
        uint8_t *row = map + (size_t)y * pitch;
        for (uint32_t x = 0; x < width; x++) {
            row[x*4+0] = b;
            row[x*4+1] = g;
            row[x*4+2] = r;
            row[x*4+3] = a;
        }
    }
}

/* ---------------- AF_UNIX wire ------------------------------------------ */

static int recv_frame_msg(int cfd, uint32_t *out_seq, uint32_t *out_size,
                          int *out_memfd) {
    char ctrl[CMSG_SPACE(sizeof(int))];
    char payload[WIRE_SIZE];
    struct iovec iov = { .iov_base = payload, .iov_len = sizeof(payload) };
    struct msghdr msg = {0};
    msg.msg_iov = &iov;
    msg.msg_iovlen = 1;
    msg.msg_control = ctrl;
    msg.msg_controllen = sizeof(ctrl);
    ssize_t n = recvmsg(cfd, &msg, 0);
    if (n <= 0) return -errno ? -errno : -1;
    if ((size_t)n < sizeof(payload)) return -EINVAL;
    uint32_t magic = ((uint32_t)(uint8_t)payload[0])
                   | ((uint32_t)(uint8_t)payload[1] << 8)
                   | ((uint32_t)(uint8_t)payload[2] << 16)
                   | ((uint32_t)(uint8_t)payload[3] << 24);
    if (magic != MSG_MAGIC_FRAME) {
        logf_("bad magic 0x%08x (want 0x%08x)", magic, MSG_MAGIC_FRAME);
        return -EBADMSG;
    }
    uint32_t seq = ((uint32_t)(uint8_t)payload[4])
                 | ((uint32_t)(uint8_t)payload[5] << 8)
                 | ((uint32_t)(uint8_t)payload[6] << 16)
                 | ((uint32_t)(uint8_t)payload[7] << 24);
    uint32_t sz = ((uint32_t)(uint8_t)payload[8])
                | ((uint32_t)(uint8_t)payload[9] << 8)
                | ((uint32_t)(uint8_t)payload[10] << 16)
                | ((uint32_t)(uint8_t)payload[11] << 24);
    int memfd = -1;
    for (struct cmsghdr *c = CMSG_FIRSTHDR(&msg); c; c = CMSG_NXTHDR(&msg, c)) {
        if (c->cmsg_level == SOL_SOCKET && c->cmsg_type == SCM_RIGHTS) {
            memcpy(&memfd, CMSG_DATA(c), sizeof(int));
            break;
        }
    }
    if (memfd < 0) return -ENOENT;
    if (out_seq) *out_seq = seq;
    if (out_size) *out_size = sz;
    if (out_memfd) *out_memfd = memfd;
    return 0;
}

static int send_ack(int cfd, uint32_t seq, uint32_t status) {
    uint8_t payload[WIRE_SIZE];
    payload[0] = MSG_MAGIC_ACK & 0xFF;
    payload[1] = (MSG_MAGIC_ACK >> 8) & 0xFF;
    payload[2] = (MSG_MAGIC_ACK >> 16) & 0xFF;
    payload[3] = (MSG_MAGIC_ACK >> 24) & 0xFF;
    payload[4] = seq & 0xFF;
    payload[5] = (seq >> 8) & 0xFF;
    payload[6] = (seq >> 16) & 0xFF;
    payload[7] = (seq >> 24) & 0xFF;
    payload[8] = status & 0xFF;
    payload[9] = (status >> 8) & 0xFF;
    payload[10] = (status >> 16) & 0xFF;
    payload[11] = (status >> 24) & 0xFF;
    ssize_t n = send(cfd, payload, sizeof(payload), MSG_NOSIGNAL);
    return n == sizeof(payload) ? 0 : -errno;
}

static int bind_listen_socket(const char *path) {
    int fd = socket(AF_UNIX, SOCK_SEQPACKET | SOCK_CLOEXEC, 0);
    if (fd < 0) return -errno;
    struct sockaddr_un addr;
    memset(&addr, 0, sizeof(addr));
    addr.sun_family = AF_UNIX;
    socklen_t alen;
    if (path[0] == '@') {
        /* Linux abstract namespace: leading NUL, no filesystem entry. */
        size_t plen = strlen(path) - 1;
        if (plen + 1 > sizeof(addr.sun_path) - 1) { close(fd); return -E2BIG; }
        addr.sun_path[0] = '\0';
        memcpy(addr.sun_path + 1, path + 1, plen);
        alen = (socklen_t)(offsetof(struct sockaddr_un, sun_path) + 1 + plen);
    } else {
        if (strlen(path) + 1 > sizeof(addr.sun_path)) { close(fd); return -E2BIG; }
        unlink(path);
        strncpy(addr.sun_path, path, sizeof(addr.sun_path) - 1);
        alen = (socklen_t)(offsetof(struct sockaddr_un, sun_path) + strlen(addr.sun_path) + 1);
    }
    if (bind(fd, (struct sockaddr *)&addr, alen) < 0) {
        int e = errno;
        close(fd);
        return -e;
    }
    if (listen(fd, MAX_BACKLOG) < 0) {
        int e = errno;
        close(fd);
        return -e;
    }
    return fd;
}

/* ---------------- Modes ------------------------------------------------- */

static int run_self_test(int secs, int allow_kill) {
    struct drm_state s = {0};
    s.fd = -1;
    if (drm_open_and_master(&s, allow_kill) < 0) return 1;
    if (drm_pick_connector(&s) < 0) {
        logf_("pick_connector failed"); drm_teardown(&s); return 2;
    }
    logf_("connector=%u encoder=%u crtc=%u mode=%ux%u",
          s.connector_id, s.encoder_id, s.crtc_id,
          s.mode.hdisplay, s.mode.vdisplay);
    if (drm_alloc_dumb(&s, 0) < 0 || drm_alloc_dumb(&s, 1) < 0) {
        logf_("alloc_dumb failed"); drm_teardown(&s); return 3;
    }
    /* Pre-fill: 0=red, 1=blue */
    fill_solid_bgra(s.bo[0].map, s.bo[0].pitch,
                    s.mode.hdisplay, s.mode.vdisplay, 0x00, 0x00, 0xFF, 0xFF);
    fill_solid_bgra(s.bo[1].map, s.bo[1].pitch,
                    s.mode.hdisplay, s.mode.vdisplay, 0xFF, 0x00, 0x00, 0xFF);
    if (drm_setcrtc(&s, 0) < 0) {
        logf_("SETCRTC failed: %s", strerror(errno));
        drm_teardown(&s); return 4;
    }
    logf_("SETCRTC OK, scanning fb_id=%u (RED)", s.bo[0].fb_id);

    /* Page-flip alternately between front and back. */
    int back = 1;
    int frame = 0;
    struct timespec t_first;
    clock_gettime(CLOCK_MONOTONIC, &t_first);
    struct timespec t_prev_ack = t_first;
    uint64_t intervals_us[256];
    int intervals_n = 0;

    while (!g_stop) {
        if (drm_page_flip(&s, back, (uint64_t)frame) < 0) {
            logf_("page_flip failed frame=%d: %s", frame, strerror(errno));
            break;
        }
        uint64_t ud; uint64_t ts; uint32_t tu;
        int rc = drm_wait_flip(&s, &ud, &ts, &tu);
        if (rc < 0) {
            logf_("wait_flip failed frame=%d: %s", frame, strerror(-rc));
            break;
        }
        struct timespec now;
        clock_gettime(CLOCK_MONOTONIC, &now);
        uint64_t dt_us = (now.tv_sec - t_prev_ack.tv_sec) * 1000000ULL
                       + (now.tv_nsec - t_prev_ack.tv_nsec) / 1000;
        if (frame > 0 && intervals_n < (int)(sizeof(intervals_us)/sizeof(*intervals_us))) {
            intervals_us[intervals_n++] = dt_us;
        }
        t_prev_ack = now;
        s.front = back;
        back = 1 - back;
        frame++;

        uint64_t total_us = (now.tv_sec - t_first.tv_sec) * 1000000ULL
                          + (now.tv_nsec - t_first.tv_nsec) / 1000;
        if (total_us >= (uint64_t)secs * 1000000ULL) break;
    }

    /* Vsync stats */
    if (intervals_n > 0) {
        uint64_t sum = 0, mn = ~0ULL, mx = 0;
        for (int i = 0; i < intervals_n; i++) {
            sum += intervals_us[i];
            if (intervals_us[i] < mn) mn = intervals_us[i];
            if (intervals_us[i] > mx) mx = intervals_us[i];
        }
        uint64_t avg = sum / (uint64_t)intervals_n;
        logf_("VSYNC_STATS frames=%d intervals=%d avg_us=%llu min_us=%llu max_us=%llu",
              frame, intervals_n,
              (unsigned long long)avg, (unsigned long long)mn, (unsigned long long)mx);
        printf("M6_SELF_TEST_OK frames=%d avg_us=%llu min_us=%llu max_us=%llu hz=%.2f\n",
               frame, (unsigned long long)avg, (unsigned long long)mn,
               (unsigned long long)mx,
               avg > 0 ? 1000000.0 / (double)avg : 0.0);
        fflush(stdout);
    } else {
        logf_("no intervals captured (frame=%d)", frame);
        printf("M6_SELF_TEST_NO_FLIPS frames=%d\n", frame);
    }

    drm_teardown(&s);
    return 0;
}

static int run_daemon(const char *sock_path, int allow_kill, int max_frames) {
    struct drm_state s = {0};
    s.fd = -1;
    if (drm_open_and_master(&s, allow_kill) < 0) return 1;
    if (drm_pick_connector(&s) < 0) {
        logf_("pick_connector failed"); drm_teardown(&s); return 2;
    }
    logf_("connector=%u encoder=%u crtc=%u mode=%ux%u",
          s.connector_id, s.encoder_id, s.crtc_id,
          s.mode.hdisplay, s.mode.vdisplay);
    if (drm_alloc_dumb(&s, 0) < 0 || drm_alloc_dumb(&s, 1) < 0) {
        logf_("alloc_dumb failed"); drm_teardown(&s); return 3;
    }
    /* Black initial scan */
    memset(s.bo[0].map, 0, s.bo[0].size);
    memset(s.bo[1].map, 0, s.bo[1].size);
    if (drm_setcrtc(&s, 0) < 0) {
        logf_("SETCRTC failed"); drm_teardown(&s); return 4;
    }
    int listen_fd = bind_listen_socket(sock_path);
    if (listen_fd < 0) {
        logf_("bind socket %s failed: %s", sock_path, strerror(-listen_fd));
        /* Try abstract fallback if filesystem path failed (SELinux etc). */
        if (sock_path[0] != '@') {
            logf_("retrying with abstract namespace %s", ABSTRACT_SOCKET_PATH);
            listen_fd = bind_listen_socket(ABSTRACT_SOCKET_PATH);
        }
        if (listen_fd < 0) {
            drm_teardown(&s);
            return 5;
        }
    }
    logf_("listening on %s", sock_path);
    /* Stable signal: print to stdout so launcher can detect "ready". */
    printf("M6_DAEMON_READY sock=%s w=%u h=%u\n",
           sock_path, s.mode.hdisplay, s.mode.vdisplay);
    fflush(stdout);

    int back = 1;
    int frames_handled = 0;
    struct timespec t_prev = {0};
    uint64_t intervals_us[1024];
    int intervals_n = 0;

    while (!g_stop) {
        struct pollfd pfd = { .fd = listen_fd, .events = POLLIN };
        int prc = poll(&pfd, 1, 5000);
        if (prc < 0) {
            if (errno == EINTR) continue;
            logf_("listen poll err: %s", strerror(errno));
            break;
        }
        if (prc == 0) {
            logf_("idle 5s — still listening...");
            continue;
        }
        int cfd = accept4(listen_fd, NULL, NULL, SOCK_CLOEXEC);
        if (cfd < 0) {
            logf_("accept err: %s", strerror(errno));
            continue;
        }
        logf_("client connected cfd=%d", cfd);

        for (;;) {
            uint32_t seq = 0, sz = 0;
            int memfd = -1;
            int rc = recv_frame_msg(cfd, &seq, &sz, &memfd);
            if (rc < 0) {
                logf_("client recv ended: %s", strerror(-rc));
                break;
            }
            /* mmap memfd and memcpy into back BO. */
            uint8_t *src = mmap(NULL, sz, PROT_READ, MAP_SHARED, memfd, 0);
            if (src == MAP_FAILED) {
                logf_("mmap memfd failed seq=%u sz=%u: %s",
                      seq, sz, strerror(errno));
                send_ack(cfd, seq, 1);
                close(memfd);
                continue;
            }
            /* If src pitch == bo pitch, single memcpy; else row-copy. */
            uint32_t row_bytes = s.mode.hdisplay * 4;
            if (s.bo[back].pitch == row_bytes && sz == s.bo[back].size) {
                memcpy(s.bo[back].map, src, sz);
            } else {
                /* sz is width*height*4 packed BGRA */
                uint32_t copy_rows = s.mode.vdisplay;
                if (sz < row_bytes * copy_rows) {
                    copy_rows = sz / row_bytes;
                }
                for (uint32_t y = 0; y < copy_rows; y++) {
                    memcpy(s.bo[back].map + (size_t)y * s.bo[back].pitch,
                           src + (size_t)y * row_bytes, row_bytes);
                }
            }
            munmap(src, sz);
            close(memfd);

            /* Page flip + wait. */
            rc = drm_page_flip(&s, back, (uint64_t)seq);
            if (rc < 0) {
                logf_("page_flip seq=%u failed: %s", seq, strerror(-rc));
                send_ack(cfd, seq, 2);
                continue;
            }
            uint64_t ud; uint64_t ts; uint32_t tu;
            rc = drm_wait_flip(&s, &ud, &ts, &tu);
            if (rc < 0) {
                logf_("wait_flip seq=%u failed: %s", seq, strerror(-rc));
                send_ack(cfd, seq, 3);
                continue;
            }
            struct timespec now;
            clock_gettime(CLOCK_MONOTONIC, &now);
            if (frames_handled > 0) {
                uint64_t dt_us = (now.tv_sec - t_prev.tv_sec) * 1000000ULL
                               + (now.tv_nsec - t_prev.tv_nsec) / 1000;
                if (intervals_n < (int)(sizeof(intervals_us)/sizeof(*intervals_us))) {
                    intervals_us[intervals_n++] = dt_us;
                }
            }
            t_prev = now;
            s.front = back;
            back = 1 - back;
            frames_handled++;

            if (send_ack(cfd, seq, 0) < 0) {
                logf_("ack send failed seq=%u", seq);
                break;
            }
            if (frames_handled % 30 == 0) {
                logf_("frames_handled=%d back=%d", frames_handled, back);
            }
            if (max_frames > 0 && frames_handled >= max_frames) {
                logf_("max_frames reached (%d) — finishing", max_frames);
                break;
            }
        }
        close(cfd);
        if (max_frames > 0 && frames_handled >= max_frames) break;
    }

    if (intervals_n > 0) {
        uint64_t sum = 0, mn = ~0ULL, mx = 0;
        for (int i = 0; i < intervals_n; i++) {
            sum += intervals_us[i];
            if (intervals_us[i] < mn) mn = intervals_us[i];
            if (intervals_us[i] > mx) mx = intervals_us[i];
        }
        uint64_t avg = sum / (uint64_t)intervals_n;
        logf_("VSYNC_STATS frames=%d intervals=%d avg_us=%llu min_us=%llu max_us=%llu",
              frames_handled, intervals_n,
              (unsigned long long)avg, (unsigned long long)mn, (unsigned long long)mx);
        printf("M6_DAEMON_DONE frames=%d avg_us=%llu min_us=%llu max_us=%llu hz=%.2f\n",
               frames_handled,
               (unsigned long long)avg, (unsigned long long)mn,
               (unsigned long long)mx,
               avg > 0 ? 1000000.0 / (double)avg : 0.0);
        fflush(stdout);
    } else {
        printf("M6_DAEMON_DONE frames=%d (no intervals)\n", frames_handled);
    }
    close(listen_fd);
    if (sock_path[0] != '@') unlink(sock_path);
    drm_teardown(&s);
    return 0;
}

/* ---------------- client (test) ----------------------------------------- */

static int connect_socket(const char *path) {
    int fd = socket(AF_UNIX, SOCK_SEQPACKET | SOCK_CLOEXEC, 0);
    if (fd < 0) return -errno;
    struct sockaddr_un addr;
    memset(&addr, 0, sizeof(addr));
    addr.sun_family = AF_UNIX;
    socklen_t alen;
    if (path[0] == '@') {
        size_t plen = strlen(path) - 1;
        addr.sun_path[0] = '\0';
        memcpy(addr.sun_path + 1, path + 1, plen);
        alen = (socklen_t)(offsetof(struct sockaddr_un, sun_path) + 1 + plen);
    } else {
        strncpy(addr.sun_path, path, sizeof(addr.sun_path) - 1);
        alen = (socklen_t)(offsetof(struct sockaddr_un, sun_path) + strlen(addr.sun_path) + 1);
    }
    if (connect(fd, (struct sockaddr *)&addr, alen) < 0) {
        int e = errno;
        close(fd);
        return -e;
    }
    return fd;
}

static int do_memfd_create(const char *name, unsigned int flags) {
    return (int)syscall(SYS_memfd_create, name, flags);
}

static int send_frame(int cfd, uint32_t seq, uint32_t width, uint32_t height,
                      uint8_t b, uint8_t g, uint8_t r, uint8_t a) {
    size_t sz = (size_t)width * height * 4;
    int memfd = do_memfd_create("m6frame", MFD_CLOEXEC);
    if (memfd < 0) return -errno;
    if (ftruncate(memfd, (off_t)sz) < 0) { close(memfd); return -errno; }
    uint8_t *map = mmap(NULL, sz, PROT_READ | PROT_WRITE, MAP_SHARED, memfd, 0);
    if (map == MAP_FAILED) { close(memfd); return -errno; }
    for (uint32_t y = 0; y < height; y++) {
        uint8_t *row = map + (size_t)y * width * 4;
        for (uint32_t x = 0; x < width; x++) {
            row[x*4+0] = b; row[x*4+1] = g;
            row[x*4+2] = r; row[x*4+3] = a;
        }
    }
    munmap(map, sz);

    uint8_t payload[WIRE_SIZE];
    payload[0] = MSG_MAGIC_FRAME & 0xFF;
    payload[1] = (MSG_MAGIC_FRAME >> 8) & 0xFF;
    payload[2] = (MSG_MAGIC_FRAME >> 16) & 0xFF;
    payload[3] = (MSG_MAGIC_FRAME >> 24) & 0xFF;
    payload[4] = seq & 0xFF;
    payload[5] = (seq >> 8) & 0xFF;
    payload[6] = (seq >> 16) & 0xFF;
    payload[7] = (seq >> 24) & 0xFF;
    uint32_t sz32 = (uint32_t)sz;
    payload[8] = sz32 & 0xFF;
    payload[9] = (sz32 >> 8) & 0xFF;
    payload[10] = (sz32 >> 16) & 0xFF;
    payload[11] = (sz32 >> 24) & 0xFF;
    char ctrl[CMSG_SPACE(sizeof(int))];
    struct iovec iov = { .iov_base = payload, .iov_len = sizeof(payload) };
    struct msghdr msg = {0};
    msg.msg_iov = &iov; msg.msg_iovlen = 1;
    msg.msg_control = ctrl; msg.msg_controllen = sizeof(ctrl);
    struct cmsghdr *c = CMSG_FIRSTHDR(&msg);
    c->cmsg_level = SOL_SOCKET;
    c->cmsg_type = SCM_RIGHTS;
    c->cmsg_len = CMSG_LEN(sizeof(int));
    memcpy(CMSG_DATA(c), &memfd, sizeof(int));
    msg.msg_controllen = c->cmsg_len;
    ssize_t n = sendmsg(cfd, &msg, MSG_NOSIGNAL);
    int err = (n == sizeof(payload)) ? 0 : -errno;
    close(memfd);
    if (err) return err;

    /* Wait ACK. */
    uint8_t ack[WIRE_SIZE];
    n = recv(cfd, ack, sizeof(ack), 0);
    if (n != sizeof(ack)) return -EIO;
    uint32_t magic = (uint32_t)ack[0] | ((uint32_t)ack[1]<<8)
                    | ((uint32_t)ack[2]<<16) | ((uint32_t)ack[3]<<24);
    if (magic != MSG_MAGIC_ACK) return -EBADMSG;
    uint32_t status = (uint32_t)ack[8] | ((uint32_t)ack[9]<<8)
                    | ((uint32_t)ack[10]<<16) | ((uint32_t)ack[11]<<24);
    if (status != 0) return -(int)status;
    return 0;
}

static int run_test_client(const char *sock_path, uint32_t width, uint32_t height,
                           int total_frames, int red_blue_split) {
    int cfd = connect_socket(sock_path);
    if (cfd < 0) {
        logf_("connect %s failed: %s", sock_path, strerror(-cfd));
        return 1;
    }
    logf_("test client connected to %s, sending %d frames (%dx%d)",
          sock_path, total_frames, width, height);
    struct timespec t_start;
    clock_gettime(CLOCK_MONOTONIC, &t_start);
    int ok = 0, fail = 0;
    for (int i = 0; i < total_frames; i++) {
        uint8_t b, g, r, a = 0xFF;
        if (i < red_blue_split) { b = 0; g = 0; r = 0xFF; }
        else                    { b = 0xFF; g = 0; r = 0; }
        int rc = send_frame(cfd, (uint32_t)i, width, height, b, g, r, a);
        if (rc < 0) { logf_("send_frame %d failed: %s", i, strerror(-rc)); fail++; break; }
        else ok++;
    }
    struct timespec t_end;
    clock_gettime(CLOCK_MONOTONIC, &t_end);
    uint64_t elapsed_us = (t_end.tv_sec - t_start.tv_sec) * 1000000ULL
                        + (t_end.tv_nsec - t_start.tv_nsec) / 1000;
    double per = ok > 0 ? (double)elapsed_us / ok : 0.0;
    printf("M6_TEST_CLIENT_DONE ok=%d fail=%d elapsed_us=%llu avg_per_frame_us=%.1f hz=%.2f\n",
           ok, fail, (unsigned long long)elapsed_us, per,
           per > 0 ? 1e6 / per : 0.0);
    close(cfd);
    return fail ? 2 : 0;
}

/* ---------------- main -------------------------------------------------- */

static void usage(const char *argv0) {
    fprintf(stderr,
        "Usage: %s --self-test [SECS] [--no-kill-composer]\n"
        "       %s --accept-client [--socket PATH] [--no-kill-composer] [--max-frames N]\n"
        "       %s --test-client   [--socket PATH] [--frames N] [--width W] [--height H]\n"
        "\n"
        "Default socket: %s (falls back to %s if filesystem path fails)\n",
        argv0, argv0, argv0, DEFAULT_SOCKET_PATH, ABSTRACT_SOCKET_PATH);
}

int main(int argc, char **argv) {
    signal(SIGINT, onsig);
    signal(SIGTERM, onsig);
    signal(SIGPIPE, SIG_IGN);

    if (argc < 2) { usage(argv[0]); return 2; }

    int self_test = 0;
    int accept_client = 0;
    int test_client = 0;
    int allow_kill = 1;
    int secs = 5;
    int frames = 60;
    int red_blue_split = 30;
    int width = 720, height = 1280;
    int max_frames = 0;
    const char *sock_path = DEFAULT_SOCKET_PATH;

    for (int i = 1; i < argc; i++) {
        if (!strcmp(argv[i], "--self-test")) self_test = 1;
        else if (!strcmp(argv[i], "--accept-client")) accept_client = 1;
        else if (!strcmp(argv[i], "--test-client")) test_client = 1;
        else if (!strcmp(argv[i], "--no-kill-composer")) allow_kill = 0;
        else if (!strcmp(argv[i], "--socket") && i+1 < argc) sock_path = argv[++i];
        else if (!strcmp(argv[i], "--frames") && i+1 < argc) frames = atoi(argv[++i]);
        else if (!strcmp(argv[i], "--max-frames") && i+1 < argc) max_frames = atoi(argv[++i]);
        else if (!strcmp(argv[i], "--width") && i+1 < argc) width = atoi(argv[++i]);
        else if (!strcmp(argv[i], "--height") && i+1 < argc) height = atoi(argv[++i]);
        else if (!strcmp(argv[i], "--split") && i+1 < argc) red_blue_split = atoi(argv[++i]);
        else if (self_test && argv[i][0] >= '0' && argv[i][0] <= '9') secs = atoi(argv[i]);
        else if (!strcmp(argv[i], "-h") || !strcmp(argv[i], "--help")) {
            usage(argv[0]); return 0;
        } else {
            fprintf(stderr, "unknown arg: %s\n", argv[i]);
            usage(argv[0]); return 2;
        }
    }

    if (self_test) return run_self_test(secs, allow_kill);
    if (accept_client) return run_daemon(sock_path, allow_kill, max_frames);
    if (test_client) return run_test_client(sock_path, width, height, frames, red_blue_split);

    usage(argv[0]); return 2;
}
