# MVP-2 red-square DRM/KMS — PASS Report (2026-05-14, agent 7)

**Status:** ✅ **DRM SCAN-OUT PASS** — `Smart0-win0` plane on `video_port1`
(DSI-1) actively scanning our 720×1280 XRGB8888 framebuffer allocated by
`drm_present`. Kernel debugfs evidence captured mid-flight; pixel-source
verified pure red (00 00 FF FF every BGRA pixel) in `red_bgra_decoded_proof.png`.

## Run record

| Artifact | Path | Notes |
|---|---|---|
| End-to-end log | `20260514_144724/stage-b.log` | Stage B kernel dumps + drm_present logs |
| Java stdout | `20260514_144724/dalvikvm.stdout` | OhosRedSquare markers + drawing trace |
| BGRA dump | `20260514_144724/red_bgra.bin` | 3,686,400 bytes from Java side |
| Decoded PNG | `red_bgra_decoded_proof.png` | every pixel = (255, 0, 0, 255) |
| Pre-state | `20260514_144724/drm-state-pre.txt` | composer_host master, plane_mask=0 |
| Post-state | `20260514_144724/drm-state-post.txt` | composer_host respawned, fb dropped |
| Result | `20260514_144724/result.txt` | `DRM_SCANOUT_PASS` |
| Helpers | `drm_probe`, `drm_red`, `drm_present` | aarch64 static binaries |

## Reproducer

```bash
cd /home/dspfac/android-to-openharmony-migration
bash scripts/run-ohos-test.sh red-square-drm
# Expected final line: "MVP-2 DRM SCAN-OUT PASS"
```

## Architecture (delivered)

```
[dalvikvm (aarch64 OHOS, statically linked) — runs MainActivity via OhosMvpLauncher]
    └── RedView.onDraw(canvas) { canvas.drawColor(Color.RED) }   ← source of truth
            └── SoftwareCanvas (records drawColor as background ARGB)
                    └── Fb0Presenter   → /dev/graphics/fb0   (kept for parity)
                    └── DrmPresenter   → /data/local/tmp/red_bgra.bin (3.6 MB BGRA)
                          ▲
                          │  (Java side ends here; rest is driver-side)
                          ▼
[scripts/run-ohos-test.sh red-square-drm — stage B]
    └── kill -9 $(pidof composer_host)               # release DRM master
    └── /data/local/tmp/drm_present 12 < red_bgra.bin
            └── open(/dev/dri/card0, O_RDWR)
            └── DRM_IOCTL_SET_MASTER
            └── DRM_IOCTL_MODE_GETRESOURCES (3 crtc / 2 connector / 3 encoder)
            └── pick DSI-1 (conn 159, status connected, mode "720x1280")
            └── DRM_IOCTL_MODE_GETENCODER → CRTC 92 (video_port1)
            └── DRM_IOCTL_MODE_CREATE_DUMB (720×1280×32bpp → handle=1)
            └── DRM_IOCTL_MODE_MAP_DUMB + mmap (3.6 MB at offset)
            └── read stdin row-by-row into BO (3,686,400 bytes consumed)
            └── DRM_IOCTL_MODE_ADDFB2 (XRGB8888 / XR24 little-endian → fb_id=160)
            └── DRM_IOCTL_MODE_SETCRTC (fb 160 → crtc 92, conn [159], mode 720x1280)
            └── sleep 12  ←  *** panel is RED here ***
            └── RMFB + DESTROY_DUMB + DROP_MASTER + close
[hdf_devmgr] respawns composer_host automatically (no manual recovery needed)
```

## Key technical findings

1. **DRM master held by `composer_host` (pid 5957, `hdf_devhost` binary).**
   The user-visible task name is `composer_host` — that's a thread/secon
   label; the actual binary supervised by HDF is `hdf_devhost`. Kill the
   process and `hdf_devmgr` respawns it within ~1 s.

2. **`plane_mask=0` at idle.** Before our run, all 4 rk3568 planes
   (Smart0/1, Esmart0/1) had `crtc=(null) fb=0`. The DSI panel was
   displaying a *stale* frame — composer_host was master but not actively
   driving any plane. This made SETCRTC very fast and uncontested
   once we held master.

3. **Connector / encoder / crtc topology (DAYU200 rk3568 OHOS 7.0.0.18):**
   - card0-DSI-1 (id 159, type 6 "DSI", status connected, 1 mode 720x1280)
   - card0-HDMI-A-1 (id 143, type 11, status disconnected on this rig)
   - card0-Writeback-1 (id 141, type 18, status unknown — could be used
     for non-destructive scan-out capture, *not implemented in this MVP*)
   - encoders 136, 142, 158; encoder 158 → CRTC 92 → DSI-1
   - CRTCs 70, 92, 114 (video_port0/1/2); only 92 active

4. **rk3568 dumb BO uses XRGB8888 (XR24) little-endian.** Pure red is
   stored as bytes `00 00 FF 00` per pixel in BO memory (or
   `00 00 FF FF` for ARGB / `0xFFFF0000` as a 32-bit little-endian word).
   Verified by reading back `red_bgra.bin` — every group of 4 bytes is
   `00 00 ff ff`.

5. **The OHOS sysroot at `dalvik-port/ohos-sysroot/usr/include/drm/`
   already had full uapi DRM headers** (drm.h, drm_mode.h, drm_fourcc.h),
   so no libdrm or extra header packaging was needed — DRM ioctls are
   direct syscalls. Hand-defined `DRM_MODE_CONNECTED=1` because the kernel
   uapi header doesn't expose it (libdrm-internal constant).

## What was NOT done

- **No physical phone-camera photo.** The test harness runs in WSL without
  camera access. Kernel debugfs evidence (Smart0-win0 plane bound to
  drm_present fb on video_port1 / DSI-1) is treated as equivalent at the
  harness level. To validate visually, run `bash scripts/run-ohos-test.sh
  red-square-drm` in front of the DAYU200 and watch the panel for ~12 s.

- **No atomic-modeset variant.** Legacy `SETCRTC` works fine here. Atomic
  would let us avoid killing composer_host but adds ~200 LOC.

- **No long-lived scan-out daemon.** drm_present is a one-shot hold-and-
  tear-down. A noice/McD-equivalent on OHOS needs an analog of the Phase 1
  M6 surface daemon (memfd + 60 Hz vsync). Tracked as the next milestone.

## Hard constraints respected

- ✅ Zero `Unsafe.allocateInstance` / `setAccessible(true)` in Java side.
- ✅ Zero per-app branches in the shim or `WestlakeContextImpl`.
- ✅ Zero new methods on `WestlakeContextImpl` or other shim classes.
- ✅ DrmPresenter uses only the public `libcore.io.Libcore.os` field +
  public `Os` interface (same surface Fb0Presenter uses; AOSP hidden API
  but accessible via public reflection).
- ✅ Zero `setenforce` / chmod hacks. SELinux remained `Enforcing`.
  /dev/dri/card0 is already `crw-rw-rw-` on this OHOS build.
- ✅ Zero destructive git ops. New commit only; agent 5/6 commits
  preserved.
- ✅ composer_host was killed (one-off race, brief permits this); it
  respawned cleanly within seconds — no reboot needed for recovery.

## What's next for noice/McD on OHOS

Path forward is the same as the brief outlined:

1. **Long-lived scan-out daemon.** Port Phase 1's M6 surface daemon
   pattern (memfd shared with dalvikvm, 60 Hz vsync page-flip in a child
   process). On OHOS DRM, this means a child holding DRM master, swapping
   between two dumb BOs via `DRM_IOCTL_MODE_PAGE_FLIP`, and the Java side
   double-buffering into the inactive one.

2. **composer_host coexistence.** Long-term, we don't want to kill
   composer_host every run. Options: (a) atomic commit on a free
   universal plane (composer_host owns master but doesn't use Smart0-win0),
   (b) implement a Westlake DRM service that composer_host itself routes
   through, (c) launch our display daemon before composer_host on boot
   (modify init.cfg).

3. **noice/McD plumbing.** The current Phase 1 in-process pattern
   (`NoiceInProcessActivity` / `McdInProcessActivity`) is what should
   target this scan-out. The fragment-nav / SplashActivity flows already
   produce pixels into `SoftwareCanvas`; pointing those at our
   DrmPresenter (or its future memfd-based successor) is a small change.

— agent 7, 2026-05-14
