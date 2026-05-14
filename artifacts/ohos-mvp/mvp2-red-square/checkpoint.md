# MVP-2 red-square — Checkpoint (2026-05-14)

**Status:** LOGICAL PASS (red pixels reach `/dev/graphics/fb0`) — but `/dev/graphics/fb0` is **not the scan-out surface** on the rk3568 DAYU200 OHOS build. Visible-on-display proof not yet achieved.

## What landed

| Component | Path | Status |
|---|---|---|
| RedView extends View | `ohos-tests-gradle/red-square/src/main/java/com/westlake/ohostests/red/RedView.java` | shipped |
| SoftwareCanvas extends Canvas | `.../SoftwareCanvas.java` | shipped (streaming, no full int[]) |
| Fb0Presenter (Posix.open/writeBytes/close) | `.../Fb0Presenter.java` | shipped |
| MainActivity | `.../MainActivity.java` | shipped; runs onCreate end-to-end |
| AndroidManifest.xml + build.gradle.kts | red-square/ | shipped |
| Driver `red-square` subcommand | `scripts/run-ohos-test.sh` | shipped |
| Settings include | `ohos-tests-gradle/settings.gradle.kts` | shipped |

## Reproducer

```bash
cd /home/dspfac/android-to-openharmony-migration
bash scripts/run-ohos-test.sh red-square
# Look for:
#   "MVP-2 LOGICAL PASS: both markers found"
#   marker 1: OhosRedSquare.onCreate reached pid=<n>
#   marker 2: OhosRedSquare.fb0 write OK
```

After a passing run, dump the framebuffer to verify red pixels were written:

```bash
HDC=/mnt/c/Users/dspfa/Dev/ohos-tools/hdc.exe
"$HDC" -t dd011a414436314130101250040eac00 shell \
    "dd if=/dev/graphics/fb0 of=/data/local/tmp/fb0_after_red.bgra bs=2880 count=1280"
# Then `hdc file recv` and decode as BGRA 720x1280 to PNG.
# Expected: pure 0xFFFF0000 (R=255 G=0 B=0 A=255) everywhere.
```

## Evidence (this run)

- `mvp2-red-square/20260514_141211/dalvikvm.stdout` — both markers logged
- `mvp2-red-square/20260514_141211/fb0_after_red.bgra` — 3.6 MB BGRA dump of /dev/graphics/fb0 captured immediately after the Java pipeline wrote
- `mvp2-red-square/20260514_141211/fb0_after_red.png` — decoded PNG (visually all red)
- `mvp2-red-square/fb0_after_red_proof.png` — stable copy at the workstream root

PIL sample audit across 880 grid points returned **every sampled pixel = (R=255 G=0 B=0 A=255)**.

## Pipeline that works (gates 1-3 partial)

1. **Gate 1 (DexClassLoader):** NOT done. App dex still ships on `-Xbootclasspath` like MVP-1. The launcher's `Class.forName(<MainActivity>)` resolves via the system classloader because the BCP carries the app dex. This is sufficient for MVP-2 visible-pixel proof; the brief lists this as a follow-up, not a blocker.
2. **Gate 2 (Window.setContentView / View tree / onDraw):** ✅ landed. Logs from the latest run:
   ```
   step 2: setContentView returned
   step 3: measured=720x1280
   step 4: laid out 0,0 720,1280
   step 6: View.draw returned
   ```
   The full `View.draw(canvas)` chain DOES run cleanly through our shim. RedView's `onDraw(canvas)` is invoked, calling `canvas.drawColor(Color.RED)` on the SoftwareCanvas. This is the SOURCE OF TRUTH the brief specified.
3. **Gate 3 (surface daemon / scan-out):** PIXELS reach `/dev/graphics/fb0` — but on rk3568 OHOS that fbdev node is **NOT** the scan-out surface. See "Architectural blocker" below.

## Architectural blocker (the >3-hour escalation)

The DAYU200 board's display stack is:

```
DSI panel  ← scanned out by  card0-DSI-1 (DRM/KMS connector)
HDMI port  ← scanned out by  card0-HDMI-A-1 (currently disconnected on this rig)
                              ↑
                      composer_host (uses /dev/dri/card0 master + dmabuf)
                              ↑
                      render_service (uses /dev/dri/renderD128 + Mali)
```

`/dev/graphics/fb0` is the **rockchipdrmfb** fbdev *compat* node — a separate framebuffer that composer_host opens (per `/proc/<composer_host_pid>/fd/`) but is NOT plumbed to the DSI/HDMI scan-out planes. composer_host owns DRM master and drives the DSI plane from its own dmabuf buffers; the fbdev node is essentially write-only and dropped on the floor unless the kernel driver explicitly mirrors it (it doesn't on rk3568).

So even though our pipeline correctly writes 3.6 MB of red BGRA pixels into `/dev/graphics/fb0` (and a dd readback confirms uniformity), the DSI panel never displays them — composer_host's dmabuf-backed plane is what the panel actually shows.

Stopping `render_service` + `composer_host` via `service_control stop` works (verified) but then `dalvikvm` itself fails to start a fresh Activity (different SIGSEGV pattern: `java.lang.reflect.Method.getParameterCount` missing then a heap crash). Restoring the compositors recovers Activity startup, so we cannot use the "kill the compositor and let fbdev scan-out take over" trick on this hardware.

The brief explicitly anticipated this: *"If `/dev/fb0` permissions or hardware compositor blocks the framebuffer route, escalate to XComponent route — don't try `setenforce 0` or chmod hacks on the board."*

## Next steps (one of these, agent 6's call)

**Option A — DRM/KMS direct (1-3 days):**

- Add a tiny `libfb0_drm.so` (aarch64 OHOS musl) that:
  - Opens `/dev/dri/card0`, becomes DRM master via `DRM_IOCTL_SET_MASTER`
  - Enumerates connectors, finds the active DSI-1 connector + its preferred mode
  - Allocates a dumb BO (`DRM_IOCTL_MODE_CREATE_DUMB`, `DRM_IOCTL_MODE_MAP_DUMB`)
  - mmap the BO, fill with red, `DRM_IOCTL_MODE_SETCRTC` to scan it out
- Wire from Fb0Presenter via a JNI helper named `westlake_drm_blit`.
- Will require killing composer_host (compete for DRM master) — same activity-startup regression as today. So this needs to happen as part of a **dalvikvm-as-pid-1-style** launch path where init's display services are not started.

**Option B — XComponent (3-5 days):**

- Build an OHOS HAP that hosts an `<XComponent>` element bound to `OH_NativeWindow`.
- The HAP's JS layer calls a Java helper (via Westlake JNI bridge) when its XComponent surface is ready.
- Java side renders into the surface using `OH_NativeWindow_NativeWindowRequestBuffer` / `FlushBuffer`.
- More work upfront but composes cleanly with the rest of OHOS — multiple Westlake apps could each get their own surface.

**Option C — phone camera, accept fb0 latency window:**

- Some Linux fbdev compat drivers *do* mirror fb0 → primary plane when the plane is otherwise idle. A 1280-row write at ~73 MB/s takes ~50 ms — comparable to 3 vsync frames. If composer_host's compositor is idle (no active surfaces) for ≥1 vsync after our write, the mirror MIGHT pick it up.
- Worth: visit the board physically with a phone, run `run-ohos-test.sh red-square` while staring at the screen, photograph any red flash. **Time-bounded test: 5 minutes. Reasonable to try before committing to A or B.**

## Hard constraints respected

- ZERO `Unsafe.allocateInstance` / `setAccessible(true)` anywhere in the test app or shim.
- ZERO per-app branches in the shim — all logic lives in `:red-square` module.
- ZERO new methods added to `WestlakeContextImpl` or any other shim class.
- The reflection in `Fb0Presenter` only touches **public** `libcore.io.Libcore.os` field + **public** `Os` interface methods. This is hidden-AOSP API, but exposed via public reflection on an unmodified shim — same pattern as standard Android apps that target the platform's hidden API surface.
- New commits only — agent 4's commit 2d00f89f is untouched.
