# M6-OHOS-Step1 PASS report (PF-ohos-m6-001)

Date: 2026-05-14
Board: DAYU200 (rk3568, OHOS 7.0.0.18 Beta1), hdc serial `dd011a414436314130101250040eac00`
Daemon binary: aarch64 static ELF, ~74 KB, cross-compiled with OHOS LLVM 15
Reproducer: `bash scripts/run-ohos-test.sh m6-drm-daemon`

## Results

### Self-test (5 s, no client, page-flip red↔blue at native vsync)

- 346 page-flips in 5 s
- Average frame-to-frame interval: **14.48 ms (69.05 Hz)** — DSI-1 panel native refresh rate
- Min: 14.39 ms, Max: 14.57 ms — jitter < 200 µs
- composer_host pid unchanged

### End-to-end (daemon + AF_UNIX test client, 120 frames RED→BLUE split at frame 60)

- 120 frames sent / 120 ACK'd / 0 dropped
- Average frame-to-frame: **28.96 ms (34.5 Hz)** — exactly 2 × native vsync (sync send/ack pipeline gates on hardware)
- Min: 28.92 ms, Max: 29.03 ms — jitter < 110 µs
- Socket: `/data/local/tmp/westlake/m6-drm.sock` (AF_UNIX SOCK_SEQPACKET, SCM_RIGHTS memfd handoff)
- composer_host pid 6902 unchanged through entire test

### Kernel evidence mid-flight

```
/sys/kernel/debug/dri/0/clients:
  m6-drm-daemon  7363  0  y  y  0  0    ← master=y, active=y
  composer_host  6902  0  n  n  3036 0  ← still alive, no master

/sys/kernel/debug/dri/0/state plane[78] Smart0-win0:
  crtc=video_port1
  fb=163
  allocated by = m6-drm-daemon
  format=XR24 little-endian
  size=720x1280
```

## Files

- `m6-drm-daemon` — the binary (push to `/data/local/tmp/`)
- `self-test.log` — 5 s self-test stderr (vsync stats)
- `m6-end-to-end.log` — end-to-end run with mid-flight kernel state capture

## What was proven

- Long-lived DRM master ownership without killing composer_host (vs MVP-2 which had to)
- 60-Hz-class page-flip with vsync (`DRM_MODE_PAGE_FLIP_EVENT` + poll/read)
- AF_UNIX SOCK_SEQPACKET memfd handoff via SCM_RIGHTS round-trip
- 720×1280 XRGB8888 dumb BO double-buffered, scanned out on DSI-1 (CRTC 92)
- Clean teardown: socket unlinked, master dropped, composer_host pid intact

## What's NOT yet there

- Java-side `M6DrmClient` (test client is a C binary mode of the daemon). Wiring
  this from dalvikvm requires generic POSIX surfaces (memfd_create, socket,
  sendmsg/SCM_RIGHTS) on `libcore.io.Os` — not per-app shims, just standard POSIX
  same status as the already-wired `open`/`write`/`close`.
- Wiring `NoiceInProcessActivity` / `McdInProcessActivity` SoftwareCanvas to
  M6DrmClient (one line change in their renderer once the Java client lands).
