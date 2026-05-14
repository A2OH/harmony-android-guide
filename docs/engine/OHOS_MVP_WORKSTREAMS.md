# OHOS Phase 2 — MVP Validation Workstreams

**Date:** 2026-05-14
**Hardware:** Yue-D200 / DAYU200 dev board (rk3568, Cortex-A55 ×4, aarch64 userspace, OHOS 7.0.0.18 Beta1)
**Goal:** smallest possible visible proof that Westlake can run unmodified Android APKs on OpenHarmony, before committing to the full 13-person-day Phase 2 roadmap (`CR41_PHASE2_OHOS_ROADMAP.md`).

---

## Board pre-flight findings (already validated 2026-05-14)

| Check | Result |
|---|---|
| hdc connection | ✅ `dd011a414436314130101250040eac00` via USB on Windows host |
| Kernel | Linux 6.6.101 SMP aarch64 Toybox (Apr 2026 build) |
| Userspace bitness | aarch64 (`uname -m` → `aarch64`, `ld-musl-arm.so.1` is OHOS musl-aarch64 naming) |
| CPU | 4× Cortex-A55 with FP/ASIMD/AES/CRC32/atomics/asimddp |
| Storage | `/data` 19 GB free / `/system` 857 MB free |
| **Binder devices** | ✅ `/dev/binder`, `/dev/hwbinder`, `/dev/vndbinder` all present |
| Logging | `hilog` available at `/system/bin/hilog` |
| Existing dalvik/art | ❌ none (we ship our own) |
| Native lib paths | `/system/lib/`, `/system/lib64/`, `/vendor/lib*` populated with OHOS Z-libs |
| App runtime | ArkUI / Ark VM (`/bin/ark_aot`, `/bin/ark_aptool`) — not relevant to us, OHOS uses ArkTS/JS instead of Java |

## Already verified on board (free wins)

- ✅ `dalvikvm` (5.8 MB aarch64 static binary from `dalvik-port/build-ohos-aarch64/`) **runs** on the board — `--help` output prints correctly, exit code 0
- ❌ **VM init SIGSEGVs** after `[V/dalvikvm] Using executionMode 1` — first bug to fix in MVP-0

These two together mean: we don't need to cross-compile from scratch, we just need to find one bug.

---

## Workstream A — MVP-0: dalvikvm executes "Hello OHOS"

**Goal:** prove the JVM itself runs on the board.

### Open work

1. **OHOS-MVP-001 — Debug dalvikvm VM init SIGSEGV on aarch64 OHOS** (BLOCKER)
   - Repro: `./dalvikvm -cp HelloOhos.dex HelloOhos` → SIGSEGV after `[V/dalvikvm] Using executionMode 1`
   - Likely causes:
     - Bootstrap classloader can't load `boot.oat` / `boot.art` (we have them in `/home/dspfac/.claude/projects/-home-dspfac-openharmony/`, need to push)
     - Missing libnativehelper / libcrypto co-deps
     - OHOS musl differs from Android bionic for `sysconf(_SC_NPROCESSORS_ONLN)` or similar
     - TLS layout difference (Phase 1 had a `__init_tls` patch for ARM32 — `ohos-sysroot-arm32/usr/lib/libc_static_fixed.a` — may need aarch64 equivalent)
   - Approach: enable `-verbose:jni,class,gc`, capture last log line before crash, look for missing files via `strace` (board likely doesn't have strace; use `LD_DEBUG=files` if dynamic) OR add `printf`-style breadcrumbs to dalvikvm source.

2. **OHOS-MVP-002 — Boot classpath staging on board**
   - Push `boot-aosp-shim.{art,oat,vdex}`, `boot-core-icu4j.{art,oat,vdex}`, core-libart.jar to board (`/data/local/tmp/westlake/bcp/`)
   - Pass `-Xbootclasspath` correctly
   - Verify VM picks them up (`-verbose:class` should show class loading from BCP)

3. **OHOS-MVP-003 — HelloOhos test harness**
   - Build script: compile Java → dx/d8 → bundle as `.dex`
   - Wrapper: `scripts/run-ohos-hello.sh` that takes a class name + dex path + pushes + runs
   - Expected output: `westlake-dalvik on OHOS — main reached` to stdout

**Success criterion:** `hdc shell "/data/local/tmp/westlake/dalvikvm -cp HelloOhos.dex HelloOhos"` prints the marker string and exits 0.

**Estimated effort:** 1-2 days (most of it in OHOS-MVP-001 SIGSEGV debug).

---

## Workstream B — MVP-1: Trivial APK loads + Activity.onCreate runs ✅ PASS 2026-05-14

**Goal:** prove a synthetic Android Activity executes through the V2 substrate on OHOS.

**Result:** PASS. `OhosTrivialActivity.onCreate reached pid=4954` printed from the board (dalvikvm stdout). Driver `scripts/run-ohos-test.sh trivial-activity` returns 0 end-to-end. See `artifacts/ohos-mvp/mvp1-trivial/20260514_135137/`.

### Landed (post-MVP-0 fixes — what actually unblocked MVP-1)

4. **OHOS-MVP-005 — Stage V2 substrate BCP on board** ✅
   - Pushed `aosp-shim-ohos.dex` (4.9 MB, dex.035), `core-android-x86.jar` (1.2 MB, dex.035), `direct-print-stream.jar` to `/data/local/tmp/westlake/bcp/`.
   - Did **NOT** ship `framework.jar` (Android-phone's is dex.039, unloadable by dalvik-kitkat). Instead we rebuilt the AOSP shim WITHOUT `scripts/framework_duplicates.txt` stripping — produces a non-slim shim that carries `ContextThemeWrapper`, `Bundle`, `Process`, etc. directly.
   - Did **NOT** ship `core-kitkat.jar` (missing `java.util.concurrent.CopyOnWriteArrayList` etc.). Used the richer `dalvik-port/core-android-x86.jar`.

5. **OHOS-MVP-006 — Trivial Activity** ✅
   - `ohos-tests-gradle/trivial-activity` — single `MainActivity extends Activity` that logs marker via both `Log.i` AND `System.out.println` (Log alone doesn't reach stdout on standalone OHOS, so the test app belt-and-suspenders).
   - Re-dexed via `d8 --min-api 13` to land at dex.035 (the default dex.037 is unloadable).

6. **OHOS-MVP-007 — Minimal OhosMvpLauncher** ✅
   - New module: `ohos-tests-gradle/launcher/` (110 LOC).
   - Path: `Class.forName(...) → newInstance() → new Instrumentation().callActivityOnCreate(activity, null) → callActivityOnDestroy(activity) → exit(0)`.
   - **ZERO Unsafe / setAccessible / per-app branches** — fully compliant with the macro-shim contract. Uses only public API methods on classes we own (Activity, Instrumentation, Bundle).
   - Replaces the heavy `NoiceProductionLauncher` for MVP-1; same path will scale to apps that load by APK once `DexClassLoader` is verified on board.

7. **OHOS-MVP-004 — Cross-compile aosp-libbinder for OHOS aarch64 musl** (deferred)
   - Not required for MVP-1 (Activity.onCreate doesn't touch binder for this minimal app). Deferred to MVP-2/MVP-3.

**Success criterion:** ✅ `MainActivity.onCreate` ran; `OhosTrivialActivity.onCreate reached pid=4954` printed.

**Actual effort:** ~1 hour after MVP-0 PASS (the four-layer fix above; no dalvikvm internals work needed).

### Reproducer

```bash
cd /home/dspfac/android-to-openharmony-migration
bash scripts/run-ohos-test.sh trivial-activity
# Look for: "MVP-1 PASS: marker found"
# and:      "marker line: OhosTrivialActivity.onCreate reached pid=..."
```

---

## Workstream C — MVP-2: Visible UI on OHOS display ✅ DRM SCAN-OUT PASS 2026-05-14

**Goal:** paint a red square (or any visible content) on the DAYU200's display from a Westlake-hosted APK.

**Result (2026-05-14, agent 7):** ✅ **DRM SCAN-OUT PASS** via DRM/KMS direct path. `RedView.onDraw(canvas) { canvas.drawColor(Color.RED) }` runs through the V2 substrate; `DrmPresenter` dumps a 720×1280×4 = 3.6 MB BGRA buffer to `/data/local/tmp/red_bgra.bin` via the wired `libcore.io.Os` JNI surface; driver-side `drm_present` (aarch64 OHOS static binary) kills composer_host to release DRM master, `SET_MASTER`s, `CREATE_DUMB`s a 720×1280 XRGB8888 BO, mmaps it, slurps stdin BGRA into the BO, `ADDFB2 + SETCRTC` binds it to CRTC 92 (`video_port1`) with the DSI-1 connector, holds the scan-out 12 s. Kernel debug dumps mid-flight confirm `plane[78]: Smart0-win0  crtc=video_port1  fb=160  allocated by = drm_present` — i.e. the DSI panel hardware was actively scanning our buffer. Source-of-truth chain (RedView → SoftwareCanvas → BGRA file → DRM/KMS) is honored throughout. See `artifacts/ohos-mvp/mvp2-red-square-drm/`.

**Previous result (2026-05-14, agent 5):** LOGICAL PASS via fb0 (pixels reach `/dev/graphics/fb0` but rk3568 OHOS doesn't scan that node out; superseded by DRM/KMS direct).

### Landed (post-MVP-1)

8. **OHOS-MVP-008 — `:red-square` gradle module** ✅
   - 4 files (~330 LOC): `MainActivity`, `RedView`, `SoftwareCanvas extends Canvas`, `Fb0Presenter`.
   - **Macro-shim contract respected:** zero Unsafe/setAccessible, zero per-app branches in the shim, zero new methods on `WestlakeContextImpl`. Reflection only on public `libcore.io.Libcore.os` field and public `Os` interface methods.

9. **OHOS-MVP-009 — V2 substrate `setContentView` / View tree / onDraw** ✅
   - `setContentView(redView)` returns cleanly on OHOS aarch64.
   - `View.measure(EXACTLY 720, EXACTLY 1280)` → measured=720x1280.
   - `View.layout(0, 0, 720, 1280)` → laid out cleanly.
   - `redView.draw(canvas)` invokes the full draw chain (background → onDraw → dispatchDraw → foreground); RedView's `onDraw` calls `canvas.drawColor(Color.RED)` which our `SoftwareCanvas` records as the background fill.

10. **OHOS-MVP-010 — `/dev/graphics/fb0` write via `libcore.io.Os`** ✅ (logical)
    - Discovery: dalvik-port's `compat/libcore_bridge.cpp` registers `Posix.open`, `Posix.writeBytes`, `Posix.close` as JNI natives (see lines 1098-1107 of that file). No new natives needed.
    - `Fb0Presenter` opens `/dev/graphics/fb0` via `Libcore.os.open(..., O_WRONLY=1, 0)`, then streams the SoftwareCanvas's recorded ops row by row as BGRA8888 (rk3568 panel byte order; verified via `od -tx1 -N 16 /dev/graphics/fb0` shows `00 00 ff ff` repeating).
    - Streaming representation avoids the 3.6 MB int[] allocation that triggered a heap-mark GC segfault in earlier iterations.

### Landed (DRM/KMS direct path — agent 7, 2026-05-14)

11. **OHOS-MVP-013 — DRM/KMS direct scan-out** ✅
    - `dalvik-port/compat/drm_probe.c`, `drm_red.c`, `drm_present.c` — three aarch64 OHOS static binaries cross-compiled with the OHOS LLVM 15 toolchain + `dalvik-port/ohos-sysroot` (which already carries full `<drm/drm.h>`/`drm_mode.h`/`drm_fourcc.h` uapi headers; no libdrm needed — DRM ioctls are direct syscalls).
    - `drm_probe`: enumerates `/dev/dri/card0` resources — confirms DSI-1 connector (id 159, type=6) → encoder 158 → CRTC 92 (`video_port1`), with `mode_valid=1 mode="720x1280"` already locked in. composer_host (pid 5957 ≡ `hdf_devhost`) holds DRM master until killed.
    - `drm_present`: full producer pipeline — `SET_MASTER` → `CREATE_DUMB(720×1280×32bpp)` → `MAP_DUMB` + mmap → read 720×1280×4 BGRA from stdin → `ADDFB2(XR24)` → `SETCRTC` binding the new fb to CRTC 92 with the DSI-1 connector → sleep `hold_secs` → `RMFB` + `DESTROY_DUMB` + `DROP_MASTER` + close. Emits `DRM_SCANOUT_OK crtc=92 fb=160 conn=159 mode=720x1280` on stdout for the parent to grep.
    - Mid-flight kernel evidence (captured in the same shell invocation as `drm_present`):
      ```
      plane[78]: Smart0-win0
          crtc=video_port1
          fb=160
              allocated by = drm_present
              format=XR24 little-endian (0x34325258)
              size=720x1280  pitch=2880
      crtc[92]: video_port1  enable=1 active=1 plane_mask=2 mode="720x1280"
      Smart0-win0: ACTIVE  src: rect[720 x 1280]  dst: rect[720 x 1280]
      ```
    - composer_host respawns automatically (supervised by `hdf_devmgr`); board is fully usable post-run, no reboot required for this generation of the helper.

12. **OHOS-MVP-014 — Java-side `DrmPresenter`** ✅
    - `ohos-tests-gradle/red-square/src/main/java/com/westlake/ohostests/red/DrmPresenter.java` — sibling of `Fb0Presenter`; same reflection surface (public `libcore.io.Libcore.os` field + public `Os.open/writeBytes/close`); dumps BGRA8888 to `/data/local/tmp/red_bgra.bin` so the driver-side `drm_present` aarch64 binary can consume it via stdin redirect.
    - Why a file-based handoff instead of a JNI ioctl shim? The dalvikvm is statically linked and has no `System.loadLibrary` path wired for arbitrary .so files; adding ioctl to the libcore_bridge would be a per-app feature (forbidden by the macro-shim contract). File handoff keeps Java-side at the existing Posix surface and respects the contract.

13. **OHOS-MVP-015 — Driver-side `red-square-drm` subcommand** ✅
    - `scripts/run-ohos-test.sh red-square-drm` — two-stage runner: (A) build + push :red-square dex + drm_present helper; invoke dalvikvm to run MainActivity which calls DrmPresenter; (B) kill composer_host, run `drm_present hold=12 < /data/local/tmp/red_bgra.bin`, snapshot mid-flight kernel state, wait for hold to finish.
    - Captures: `dalvikvm.stdout`, `stage-b.log` (with mid-flight DRM state + framebuffer + summary dumps), `red_bgra.bin` (the 3.6 MB pixel dump), `drm-state-{pre,post}.txt`, `result.txt = DRM_SCANOUT_PASS`.

### Open / deferred

14. **Phone-camera or hdmi-capture photo of the panel during the hold window.** The agent 7 run produced kernel-side evidence that the DSI panel hardware was actively scanning out our buffer, but no physical photo was captured (the test harness has no camera; brief permits this — kernel evidence is treated as equivalent for harness-level pass).

15. **Atomic-modeset variant (cleaner than legacy SETCRTC).** Current implementation uses `DRM_IOCTL_MODE_SETCRTC` (legacy KMS). Atomic commit (`DRM_IOCTL_MODE_ATOMIC`) would let us avoid taking master / killing composer_host, but requires ~200 LOC of property-id discovery and per-plane state assembly. Not blocking MVP-2.

16. ~~**Long-lived render loop.** drm_present holds for a fixed seconds count, then tears down. A real Activity needs a continuous scan-out daemon with vsync-aligned page-flip. M6 surface daemon (memfd / 60 Hz) is the analog from Phase 1 / phone-path; an OHOS DRM port of that is the next milestone (would track as MVP-3 / Workstream D continuation).~~ **DONE 2026-05-14 M6-OHOS-Step1 (PF-ohos-m6-001):** `dalvik-port/compat/m6-drm-daemon/m6_drm_daemon.c` is a 700-LOC self-contained aarch64 daemon that takes DRM master without killing composer_host (the latter doesn't actively hold master at idle on rk3568), allocates two dumb BOs at 720×1280 XRGB8888, page-flips them with `DRM_MODE_PAGE_FLIP_EVENT` + poll/read for `FLIP_COMPLETE`. Self-test: 346 flips/5 s = **14.48 ms (69.05 Hz)** native vsync, jitter < 200 µs. AF_UNIX SOCK_SEQPACKET + SCM_RIGHTS memfd handoff round-trip works end-to-end with the daemon's built-in test client mode: 120 frames RED→BLUE @ 28.96 ms each (= 2 × vsync, sync send/ack pipeline). Kernel `clients` mid-flight: `m6-drm-daemon master=y` next to `composer_host master=n` (alive). Reproducer: `bash scripts/run-ohos-test.sh m6-drm-daemon`. Artifacts: `artifacts/ohos-mvp/m6-drm-daemon/<TS>/`.

17. ~~**Java-side `M6DrmClient` (next gate, ~0.5 day).** The test client today is C; for dalvikvm to push frames into the daemon, we need to extend `libcore_bridge.cpp` with generic POSIX `socket(AF_UNIX, SOCK_SEQPACKET)` / `connect` / `sendmsg(SCM_RIGHTS)` / `recv` and a userspace memfd_create syscall wrapper, and add a 100-LOC `M6DrmClient` Java class on the existing `libcore.io.Os` shape. None of these are per-app shims — they're standard POSIX surfaces, same status as the already-wired `open`/`write`/`close`. Once landed, `DrmPresenter` becomes async memfd handoff and `NoiceInProcessActivity` / `McdInProcessActivity` `SoftwareCanvas` outputs can stream into the daemon.~~ **DONE 2026-05-14 M6-OHOS-Step2 (PF-ohos-m6-002):** new shim class `com.westlake.compat.UnixSocketBridge` (generic AF_UNIX + memfd + SCM_RIGHTS) registered from `libcore_bridge.cpp` against the rebuilt `aosp-shim-ohos.dex` (now 4.88 MB; see `scripts/build-shim-dex-ohos.sh`). New gradle module `:m6-test` with `M6DrmClient` (~190 LOC) + `M6FramePainter` + `M6ClientTestActivity`. Driver subcommand `bash scripts/run-ohos-test.sh m6-java-client` runs end-to-end: daemon `--accept-client --no-kill-composer --max-frames 120` in the background, dalvikvm + OhosMvpLauncher invokes `M6ClientTestActivity`, 120 BGRA frames (60 RED + 60 BLUE) submitted at **28.97 ms/frame interval (34.52 Hz, 2× vsync)** — **within 0.04% of the C baseline (28.96 ms)**. composer_host pid stable pre/post (6902 → 6902). Kernel debugfs mid-flight: `framebuffer[160/163] allocated by = m6-drm-daemon`. Macro-shim contract compliant: zero `Unsafe.allocateInstance`, zero `setAccessible(true)`, zero per-app branches; all new methods on classes WE own (`UnixSocketBridge`, `M6DrmClient`, `M6FramePainter`, `M6ClientTestActivity`). Artifacts: `artifacts/ohos-mvp/m6-java-client/<TS>/`.

**Success criterion:** ✅ DRM scan-out confirmed by kernel debugfs mid-flight (Smart0-win0 plane crtc=video_port1 fb=160 allocated-by=drm_present, format XR24, src/dst 720×1280). Source-of-truth from RedView.onDraw to DRM framebuffer is verifiable end-to-end.

**Actual effort:** ~1 hour after MVP-2 fb0 logical pass (agent 7; agent 5 found the architecture, agent 6 ruled out XComponent, agent 7 implemented the cheaper of agent 5's two options).

### Reproducer

```bash
cd /home/dspfac/android-to-openharmony-migration
bash scripts/run-ohos-test.sh red-square-drm
# Look for:
#   "Stage A done — Java dumped 3686400 bytes BGRA to /data/local/tmp/red_bgra.bin"
#   "DRM_SCANOUT_OK marker: 1"
#   "kernel-side fb allocated by drm_present: 1"
#   "MVP-2 DRM SCAN-OUT PASS"

# Then physically observe the DAYU200 panel — it goes uniform red for ~12 s.
```

See `artifacts/ohos-mvp/mvp2-red-square-drm/` for kernel evidence, the rendered BGRA dump (`red_bgra_decoded_proof.png`), and per-run logs.

---

## Workstream D — Infrastructure (supports A/B/C in parallel)

11. **OHOS-MVP-011 — Build script `scripts/run-ohos-test.sh`**
    - One-stop: `./run-ohos-test.sh HelloOhos`
    - Compiles, dexes, pushes, runs, captures logs
    - Mirrors Phase 1's `scripts/run-noice-westlake.sh`

12. **OHOS-MVP-012 — Captured-evidence dir `artifacts/ohos-mvp/`**
    - Screenshot per milestone
    - logcat capture per milestone
    - hardware setup notes

---

## Open GitHub issues (created with this workstream doc)

Issues will be opened as `PF-ohos-mvp-001..012` with the work items above, labeled `enhancement` + `mvp` + `phase2`.

## Order of attack

```
OHOS-MVP-001 (debug SIGSEGV)    ← critical blocker, MVP-0
       │
       ▼
OHOS-MVP-002 + 003 (BCP + harness)   ← finishes MVP-0
       │
       ▼
OHOS-MVP-004 + 005 (libbinder + V2 substrate on board)
       │
       ▼
OHOS-MVP-006 + 007 (trivial APK + launcher)   ← finishes MVP-1
       │
       ▼
OHOS-MVP-008 + 009 + 010 (display)   ← finishes MVP-2
```

Workstream D (infrastructure) runs continuously alongside.

## Honest scope statement

This document scopes ~5 days of focused work to land MVP-0/1/2. The full Phase 2 roadmap (real noice or McD on OHOS) is the existing `CR41_PHASE2_OHOS_ROADMAP.md` — about 13 person-days on top of the MVP. The MVP exists to:

1. **De-risk the cross-compile + runtime story** before committing to full porting
2. **Generate concrete artifacts** (screenshots, logs) for stakeholder buy-in
3. **Identify the actual hard problems** vs. estimated hard problems

If MVP-0 succeeds cheaply (e.g., the SIGSEGV is a simple TLS fix), MVP-1+2 may proceed faster than estimated. If MVP-0 requires significant dalvikvm internals work, that itself is the signal to reconsider Phase 2 architecture.

## Reproducer (current state)

```bash
# From WSL on Windows host (board connected via USB):
HDC=/mnt/c/Users/dspfa/Dev/ohos-tools/hdc.exe

# Verify board is reachable
$HDC list targets        # should show: dd011a414436314130101250040eac00

# dalvikvm is already on board after our preflight
$HDC shell "/data/local/tmp/dalvikvm"          # ⇒ usage message (exit 0) — VM binary works
$HDC shell "/data/local/tmp/dalvikvm -cp HelloOhos.dex HelloOhos"  # ⇒ SIGSEGV after "Using executionMode 1"
```

## Key file pointers

- Dalvikvm aarch64 binary: `dalvik-port/build-ohos-aarch64/dalvikvm`
- Dalvikvm source: `/home/dspfac/dalvik-kitkat/` + patches at `dalvik-port/patches/`
- Cross-toolchain: `dalvik-port/ohos-sysroot-arm32/` (need aarch64 variant — see OHOS-MVP-004)
- V2 substrate: `shim/java/android/app/Westlake{Activity,Application,ActivityThread}.java`
- Compiled shim dex: `aosp-shim.dex` (1.3 MB after slimming)
- Phase 1 launcher pattern: `aosp-libbinder-port/test/NoiceProductionLauncher.java`
- Phase 1 binder regression: `scripts/binder-pivot-regression.sh` (14/14 PASS on Android phone)
