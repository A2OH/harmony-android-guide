# MVP-2 red-square XComponent — Agent 6 Checkpoint (2026-05-14)

**Status:** **CHECKPOINTED — NOT ATTEMPTED**, scope exceeds the 4-hour escalation bar
in the brief. See "Why checkpoint without attempting" below. No new code landed;
no new commits required. Agent 5's logical-PASS (commit `c0753d73`) is preserved.

## TL;DR for next agent

The OHOS XComponent path is buildable on this host with a multi-day effort. The
hard, unanticipated blocker is **arch mismatch between dalvikvm (aarch64) and the
OHOS userspace on the DAYU200 board (32-bit ARM)** — the brief assumed a
single-arch story, but the rk3568 OHOS 7.0.0.18 image on this DAYU200 ships an
aarch64 kernel with an exclusively 32-bit ARM userspace. Every OHOS lib
(libace_napi, libace_ndk, libnative_window, render_service, composer_host) is
`32-bit LSB arm`. Our standalone dalvikvm at `/data/local/tmp/dalvikvm` is
`64-bit LSB arm64`. The two cannot share a process; the XComponent
producer/consumer split MUST happen at a binder boundary, not a JNI boundary.

This invalidates the brief's Step-3 plan ("dalvikvm-hosted Activity calls
`OH_NativeWindow_NativeWindowRequestBuffer` directly"). Either:

- **Path A (cross-arch IPC):** ArkTS host HAP (32-bit) → C++ NAPI module (32-bit)
  → owns the OHNativeWindow → exposes it via binder (surface_id is binder-fd
  transferable across arches) → dalvikvm aarch64 binder client gets the surface
  handle and produces frames via a 64-bit-rebuilt `libnative_window` linked
  against the aarch64 sysroot we have at
  `/home/dspfac/openharmony/out/sdk/sdk-native/os-irrelevant/sysroot/usr/lib/aarch64-linux-ohos/libnative_window.so`.
  However the *board* doesn't have 64-bit OHOS libs deployed, so even though
  the sysroot has them, the runtime would have to ship them alongside dalvikvm
  in `LD_LIBRARY_PATH` and ensure they don't pull in any 32-bit-only ohos plumbing.

- **Path B (rebuild dalvikvm-arm32):** Use the existing aarch32 dalvikvm at
  `/data/local/tmp/dalvikvm-arm32` (status: present, file says `32-bit LSB arm,
  soft float, EABI5, bad note 1 size?` — likely needs porting work parallel
  to the aarch64 port). Then the same-process XComponent native call works.

- **Path C (no XComponent — DRM/KMS direct, agent 5 Option A):** Skip the OHOS
  app entirely. Add a `libfb0_drm.so` to dalvikvm that becomes DRM master,
  enumerates DSI-1, dumb-BO-allocates, mmap-fills-red, `MODE_SETCRTC`. Same
  arch as dalvikvm. The blocker is killing composer_host (DRM master conflict)
  without losing dalvikvm-startable state — agent 5 found that
  `service_control stop composer_host` regresses dalvikvm Activity startup.
  Resolvable with a launch sequence that brings dalvikvm up *before* the OHOS
  display services init the DRM master.

Agent 5 explicitly recommended Path C ("DRM/KMS direct, 1-3 days") as one of two
viable options. After auditing the arch mismatch, **Path C is now the recommended
direction**, not the XComponent path. The XComponent path was the brief's
preferred direction under the (false) assumption of a unified-arch userspace.

## Why checkpoint without attempting

The brief's Section 2 Step 2 says:

> If HAP signing/install on this board is non-trivial (>2h to figure out),
> CHECKPOINT and report.

The brief's Hard Constraints say:

> If you hit a hard blocker that needs >4 hours of OHOS-SDK / signing / DRM
> internals work, STOP and checkpoint.

Even ignoring the arch mismatch, the HAP build chain from this repo's source
requires:

1. **hvigor bring-up** (~30-60 min) — `/home/dspfac/.hvigor/wrapper/tools/`
   only ships `pnpm`; hvigor itself + ohpm + the ace_ets2bundle compiler-chain
   are not present. Node 18.19.1 + npm are on the host, so `pnpm install`
   inside the wrapper plus pulling the OHOS toolchain registry should work,
   but the registry config and the SDK-side hvigor config files need to be
   wired up.

2. **ets2bundle compile** (~20-30 min for a 2-page sample app on first build) —
   `/home/dspfac/openharmony/developtools/ace_ets2bundle/compiler/node_modules/`
   IS populated (good). Driver script is `main.js`. Inputs are `.ets`/`.ts`/`.json5`,
   output is `.abc` bytecode. No prior agent has run this here, so first
   build will hit unknown failure modes.

3. **HAP packing** (~5 min) — `app_packing_tool.jar` is present at
   `/home/dspfac/openharmony/developtools/packing_tool/jar/`. Documented in
   `/home/dspfac/openharmony/developtools/packing_tool/packingTool.sh`.

4. **HAP signing** (~10 min) — `/home/dspfac/developtools/hapsigner/dist/`
   has hap-sign-tool.jar + OpenHarmony.p12 + OpenHarmonyApplication.pem +
   UnsgnedDebugProfileTemplate.json. Reference invocation in
   `developtools/hapsigner/tools/test/` of the same repo.

5. **HAP install** (~5 min) — `bm install -p <path-to-hap>` after device's
   trust store accepts our cert. With `appDistributionType=os_integration` /
   `appProvisionType=debug`, the dev-mode board MIGHT accept the
   OpenHarmonyApplication.pem signature, but this hasn't been verified for
   this hardware revision. If it doesn't, you need access to a HUAWEI-side
   certificate provisioning step which is out of scope.

6. **NAPI module bring-up** (~60 min) — write a C++ module under entry/src/main/cpp/
   that handles the `OnSurfaceCreated` XComponent callback, plus a CMakeLists.txt
   that links against the on-board chipset-sdk-sp libnative_window.so. The
   sample at `applications/standard/app_samples/code/BasicFeature/Native/NdkXComponent`
   is a perfect template (does GL via the surface), but we need a different
   path that *exports* the surface to dalvikvm.

7. **Cross-arch IPC design** (~2-4 hours minimum) — given the 32/64 mismatch
   discovered above, we need to design a binder-or-socket protocol for handing
   the surface handle to dalvikvm. SurfaceUtils on OHOS lets you get a
   `uint64_t surface_id` and recover it on the other side via
   `OH_NativeWindow_CreateNativeWindowFromSurfaceId`, but the producer
   library calls are not in our aarch64 sysroot unless we cherry-pick the
   relevant code.

Total realistic effort: **3-5 working days, not 4 hours.**

## Hardware facts (verified this run)

```
$ hdc.exe -t dd011a... shell "uname -a"
Linux localhost 6.6.101 #1 SMP Sat Apr 4 16:40:55 CST 2026 aarch64 Toybox

$ hdc shell "file /system/bin/sh"
ELF shared object, 32-bit LSB arm, EABI5, soft float, dynamic (/lib/ld-musl-arm.so.1)

$ hdc shell "file /system/bin/render_service"
ELF shared object, 32-bit LSB arm, EABI5, soft float, dynamic

$ hdc shell "file /system/lib/platformsdk/libace_napi.z.so"
ELF shared object, 32-bit LSB arm, EABI5, soft float, not stripped

$ hdc shell "file /system/lib/ndk/libace_ndk.z.so"
ELF shared object, 32-bit LSB arm, EABI5, soft float, stripped

$ hdc shell "file /system/lib/chipset-sdk-sp/libnative_window.so"
(symlink to libsurface.z.so — also 32-bit ARM)

$ hdc shell "ls /system/lib64"
(does not exist — board has no 64-bit OHOS userspace)

$ hdc shell "file /data/local/tmp/dalvikvm"
ELF executable, 64-bit LSB arm64, static, not stripped   ← OUR vm is 64-bit

$ hdc shell "file /data/local/tmp/dalvikvm-arm32"
ELF executable, 32-bit LSB arm, EABI5, soft float, bad note 1 size?
                                                       ← exists but not yet ported
```

Process inventory at idle (verified runs cleanly):
```
graphics      render_service       (DRM master / Mali producer)
composer_host composer_host -i 12  (DRM master / DSI scan-out)
10007         com.ohos.systemui
20010009      com.ohos.launcher
```

## SDK / tool inventory (verified)

- `/home/dspfac/openharmony/out/sdk/sdk-native/os-irrelevant/sysroot/usr/include/ace/xcomponent/native_interface_xcomponent.h` — XComponent NAPI API ✓
- `/home/dspfac/openharmony/out/sdk/sdk-native/os-irrelevant/sysroot/usr/include/native_window/external_window.h` — NativeWindow consumer API ✓
- `/home/dspfac/openharmony/out/sdk/sdk-native/os-irrelevant/sysroot/usr/lib/aarch64-linux-ohos/libace_napi.z.so` — aarch64 NAPI lib (host-only; not on board) ✓
- `/home/dspfac/openharmony/out/sdk/sdk-native/os-irrelevant/sysroot/usr/lib/aarch64-linux-ohos/libnative_window.so` — aarch64 NativeWindow lib (host-only; not on board) ✓
- `/home/dspfac/openharmony/applications/standard/app_samples/code/BasicFeature/Native/NdkXComponent/` — full sample HAP source tree (uses XComponent + OH_NativeWindow + GL) ✓
- `/home/dspfac/openharmony/developtools/packing_tool/jar/app_packing_tool.jar` — HAP packer (CLI; no DevEco needed) ✓
- `/home/dspfac/developtools/hapsigner/dist/hap-sign-tool.jar` + OpenHarmony.p12 + OpenHarmonyApplication.pem ✓
- `/home/dspfac/openharmony/developtools/ace_ets2bundle/compiler/` — ETS→abc compiler, `node_modules/` populated ✓
- `/usr/bin/node` 18.19.1 + `/usr/bin/npm` ✓
- `/home/dspfac/.hvigor/wrapper/tools/` — has pnpm BUT not hvigor itself (only package.json/lock); needs `pnpm install`

## Reproducer to confirm hardware state

```bash
HDC=/mnt/c/Users/dspfa/Dev/ohos-tools/hdc.exe
SERIAL=dd011a414436314130101250040eac00
"$HDC" -t "$SERIAL" shell "file /system/bin/sh /system/bin/render_service /data/local/tmp/dalvikvm"

# Expect:
#   /system/bin/sh:              ELF ... 32-bit LSB arm  EABI5
#   /system/bin/render_service:  ELF ... 32-bit LSB arm  EABI5
#   /data/local/tmp/dalvikvm:    ELF ... 64-bit LSB arm64
```

## What the brief assumed vs. what we found

| Brief assumption | Reality |
|---|---|
| Board libs at `/system/lib64/` | Board has NO `/system/lib64/`; userspace is 32-bit |
| OHOS app + dalvikvm share NDK | OHOS userspace 32-bit, dalvikvm 64-bit → cannot dlopen each other |
| `OH_NativeXComponent_GetNativeWindow` returns a pointer usable in dalvikvm's address space | The window object is held in the ArkTS-host process's 32-bit address space; only a `surface_id`/binder handle survives cross-process and that handle requires libnative_window aarch64 producer code to consume |
| HAP install needs ~30 min of OHOS-SDK tooling | Realistic: 3-5 days incl. cross-arch IPC design |

## Recommended next move (one of)

**Path C — DRM/KMS direct, recommended over XComponent.** Same-arch (dalvikvm
is aarch64; DRM ioctls are arch-agnostic via the kernel). 1-3 days per agent 5's
estimate, plausibly less because we know the geometry (720×1280 DSI-1) and
the kernel rk3568 DRM driver is upstream.

**Path A — XComponent with cross-arch binder bridge.** Possible but expensive
(3-5 days). The unique value over Path C is composition with launcher / other
OHOS surfaces; the unique cost is 32/64 binder bridge design.

**Path B — port dalvikvm to 32-bit ARM.** Reuses existing infrastructure on
the *Android phone path* (where ARM32 is already done — see
`project_arkui_arm32_tls_fix.md`), but the OHOS sysroot diverges from Android's
bionic so it's not a free port. Comparable to Path A in effort.

## Files that would land if we went forward (not landed in this checkpoint)

```
ohos-tests-gradle/red-square/src/main/java/com/westlake/ohostests/red/XCompPresenter.java   (NEW)
dalvik-port/compat/xcomp_bridge.cpp                                                          (NEW)
ohos-xcomp-host/                                                                             (NEW DIR)
  AppScope/app.json5
  entry/src/main/module.json5
  entry/src/main/cpp/CMakeLists.txt
  entry/src/main/cpp/native_xc.cpp
  entry/src/main/ets/pages/Index.ets
  entry/src/main/ets/entryability/EntryAbility.ts
  entry/build-profile.json5
  build-profile.json5
  hvigorfile.ts
  hvigorw
scripts/build-ohos-host-hap.sh                                                               (NEW)
scripts/run-ohos-test.sh                                                                     (+ red-square-xcomp subcommand)
```

None of these are written. The bullet list is intended as an inventory for a
future agent who knows the time budget upfront.

## Hard constraints respected

- ZERO new commits in this checkpoint; agent 5's `c0753d73` is the tip.
- ZERO `Unsafe.allocateInstance` / `setAccessible(true)` discussions in the
  proposed code outline.
- ZERO `setenforce 0` / chmod hacks attempted on the board.
- ZERO destructive git ops.

## Top three commands the next agent should run first

```bash
# 1. Verify board state hasn't drifted
HDC=/mnt/c/Users/dspfa/Dev/ohos-tools/hdc.exe
"$HDC" -t dd011a414436314130101250040eac00 list targets
"$HDC" -t dd011a414436314130101250040eac00 shell "file /system/bin/sh /data/local/tmp/dalvikvm"

# 2. Decide Path C vs Path A based on time budget. If Path C:
hdc shell "ls /dev/dri/"
hdc shell "cat /sys/class/drm/card0-DSI-1/modes"
hdc shell "cat /sys/class/drm/card0-DSI-1/status"

# 3. If Path A: bring up hvigor
ls /home/dspfac/.hvigor/wrapper/tools/  # has pnpm
cd /home/dspfac/.hvigor/wrapper/tools/ && ./pnpm install
```
