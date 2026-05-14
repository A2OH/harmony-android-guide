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

## Workstream C — MVP-2: Visible UI on OHOS display ⚠ LOGICAL PASS 2026-05-14

**Goal:** paint a red square (or any visible content) on the DAYU200's display from a Westlake-hosted APK.

**Result (2026-05-14):** LOGICAL PASS — `RedView.onDraw(canvas) { canvas.drawColor(Color.RED) }` runs through the V2 substrate, the full `View.draw(canvas)` chain executes, and 3.6 MB of red BGRA pixels reach `/dev/graphics/fb0` (verified via `dd` readback decoded as 720×1280 BGRA = uniform red 0xFFFF0000). **Visible-pixel proof deferred** — see "Architectural blocker" in `artifacts/ohos-mvp/mvp2-red-square/checkpoint.md`.

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

### Open / deferred

11. **OHOS-MVP-013 — Architectural: fb0 → DSI scan-out path** ⛔ BLOCKER for visible-pixel
    - On rk3568 DAYU200 OHOS 7.0, `/dev/graphics/fb0` is a `rockchipdrmfb` *fbdev compat node*. `composer_host` opens it (per `/proc/<pid>/fd`) but the panel scan-out is driven via DRM/KMS dmabuf paths through `render_service` + `composer_host`, NOT via fbdev. `dd if=/dev/graphics/fb0` after our write shows red, but the DSI panel does not display it.
    - Confirmed: DRM CRTC state (`/sys/kernel/debug/dri/0/state`) shows `video_port1` enabled with DSI-1 connector but ALL planes have `crtc=(null) fb=0` — no framebuffer is currently being scanned out at all. `snapshot_display` fails to produce a snapshot.
    - Killing render_service+composer_host via `service_control stop` works, but then `dalvikvm` fails with new SIGSEGV pattern (`java.lang.reflect.Method.getParameterCount` missing). Compositor presence is required for our Activity startup yet ALSO blocks the fb0 path.
    - Two paths forward, both >3 hours:
      - **DRM/KMS direct** (1-3 days): native helper that grabs DRM master, allocates a dumb BO, modeset DSI-1 to it.
      - **XComponent** (3-5 days): host an OHOS HAP with `<XComponent>` element, drive `OH_NativeWindow` from Java via Westlake JNI bridge.

**Success criterion:** ⚠ LOGICAL PASS achieved; visible-pixel proof requires OHOS-MVP-013.

**Actual effort:** ~3 hours after MVP-1 (gates 2+3-logical landed).

### Reproducer

```bash
cd /home/dspfac/android-to-openharmony-migration
bash scripts/run-ohos-test.sh red-square
# Look for: "MVP-2 LOGICAL PASS: both markers found"

# Then verify pixels on fb0:
HDC=/mnt/c/Users/dspfa/Dev/ohos-tools/hdc.exe
$HDC -t dd011a414436314130101250040eac00 shell \
    "od -An -tx1 -N 16 /dev/graphics/fb0"
# Expected: "00 00 ff ff" repeating (BGRA red).
```

See `artifacts/ohos-mvp/mvp2-red-square/checkpoint.md` for full state, blocker analysis, and follow-up options.

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
