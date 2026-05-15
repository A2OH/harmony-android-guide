# Westlake Binder Pivot — Milestones & Test Plan

**Companion to:** `BINDER_PIVOT_DESIGN.md`
**Last updated:** 2026-05-12
**Audience:** human engineers + agent swarm

This document is the canonical work breakdown for the pivot. Every milestone has explicit acceptance criteria, a concrete test plan, and named risks. Agents and engineers should self-assign by claiming a milestone (set owner via `TaskUpdate`), do the work, then run the acceptance test and report.

---

## 0. Dependency Graph

```
Phase 1 (Android sandbox validation)

  C1 ──┐
  C2 ──┼──> (cleanup runs in parallel with foundation)
  C3 ──┤
  C4 ──┘

  M1 ──> M2 ──> M3 ──┬──> M4a (ActivityManagerService) ──┐
                     ├──> M4b (WindowManagerService)     │
                     ├──> M4c (PackageManagerService)    │
                     ├──> M4d (DisplayManagerService)    ├──> M7 (noice e2e)
                     ├──> M4e (NotificationManager)      │       │
                     ├──> M5 (audio daemon)              │       ├──> M8 (McDonald's regression)
                     └──> M6 (surface daemon) ───────────┘       │
                                                                 │
Phase 2 (OHOS port) — only begins after M7+M8 green               │
                                                                 ▼
  M9 ──> M10 ──┬──> M11 (audio→AudioRenderer)
               ├──> M12 (surface→XComponent)
               └──> M13 (noice on OHOS phone)

Phase 3+ (later)

  M14: WebView Chromium subprocess support
  M15: camera daemon
  M16: media daemon
  M17: 10-app benchmark suite
```

Critical path: **M1 → M2 → M3 → (M4a + M5 + M6) → M7**. Everything else runs around it.

---

## Phase 1: Cleanup (parallel with foundation)

These are pure removals — no architectural risk, no new code. Goal: delete the renderer-time bypass so the rest of Phase 1 builds on the correct (framework-driven) substrate. **All cleanup must be done before M7 acceptance** so the e2e test exercises the correct path.

### C1 — Remove `WestlakeFragmentLifecycle` bypass

**Goal:** delete the renderer-time reflective fragment-lifecycle driver and the single call site that uses it.

**Scope:**
- `shim/java/com/westlake/engine/WestlakeRenderer.java:500` — remove `liveRoot = WestlakeFragmentLifecycle.runLifecycleAndConvert(...)` call and surrounding fuse-against-live-tree code. The renderer should consume whatever View tree the framework's standard inflate/Fragment path produced.
- `shim/java/com/westlake/engine/WestlakeFragmentLifecycle.java` — delete the file entirely.
- `shim/java/com/westlake/engine/DexLambdaScanner.java` — delete (only used by WFL).
- Audit `WestlakeLauncher.java` and `WestlakeLayoutInflater.java` for stale references and remove.
- Confirm `FragmentManager.addFragmentInternal` (already in `shim/java/androidx/fragment/app/FragmentManager.java`) still compiles and runs.

**Deliverables:**
- Diff that removes `WestlakeFragmentLifecycle.java`, `DexLambdaScanner.java`, and all references.
- Updated `aosp-shim.dex` build artifact.
- Smoke test: McDonald's still runs (`./scripts/run-real-mcd-phone-gate.sh`).

**Acceptance:**
- `aosp-shim.dex` builds with zero references to `WestlakeFragmentLifecycle` (grep returns nothing).
- `grep -rn 'WestlakeFragmentLifecycle' shim/java/` returns nothing.
- McDonald's regression test passes — SplashActivity still reaches `performResume`, branded splash still renders.
- noice may temporarily look worse after this change — that's expected; M7 fixes it via correct services.

**Test plan:**
```bash
cd /home/dspfac/android-to-openharmony-migration
bash scripts/build-shim-dex.sh
# Push and run McDonald's
adb push out/aosp-shim.dex /data/local/tmp/westlake/
adb shell am force-stop com.westlake.host
adb shell am start -n com.westlake.host/.WestlakeActivity
sleep 30
adb logcat -d | grep -E "MCD_APP_PROOF|SplashActivity.*performResume" | head
# Expect: MCD_APP_PROOF lines present, no NoClassDefFoundError WestlakeFragmentLifecycle
```

**Risks:**
- Existing FragmentManager.addFragmentInternal may also be incomplete — but that's M4's problem to fix, not C1's. C1 just removes the bypass.

**Estimated effort:** 1–2 hours

---

### C2 — Remove per-app hardcoded constants from shim

**Goal:** strip per-app branches from generic shim files. These violate `feedback_no_per_app_hacks.md` and distort future architectural reasoning.

**Scope:**
- `shim/java/androidx/fragment/app/FragmentTransactionImpl.java`:
  - Remove `MCD_SIMPLE_PRODUCT_HOLDER_ID`, `MCD_ORDER_PDP_FRAGMENT` constants
  - Remove `isMcdPdpTarget`, `MCD_PDP_FRAGMENT_TX_COMMITTED` proof markers
- `shim/java/androidx/fragment/app/FragmentManager.java`:
  - Same audit; remove all `mcd*` / `MCD_*` references
- `shim/java/com/westlake/engine/WestlakeLauncher.java`:
  - Audit lines 4124, 4165, 5681 (`seedMcdonaldsApplicationContext`, `buildMcDonaldsUI`, `isLegacyMcdName`, etc.) — these are per-app paths. Tag for removal but DO NOT delete until M4 services are in place (some currently bootstrap McDonald's via this path).
- `shim/java/android/app/MiniActivityManager.java`:
  - Audit `isCutoffCanaryComponent`, `isControlledWestlakeComponent`, `isMcdOrderProductDetailsRecord`, `shouldRunLegacyMcdBootstrap` — same: tag, don't delete until M4.

**Deliverables:**
- FragmentTransactionImpl.java and FragmentManager.java MCD-free.
- A separate doc `docs/engine/PER_APP_HACK_AUDIT.md` listing the WestlakeLauncher.java and MiniActivityManager.java per-app entries with rationale for keeping each until its M-replacement exists.

**Acceptance:**
- `grep -rn 'mcd\|MCD_\|mcdonald' shim/java/androidx shim/java/com/westlake/engine/WestlakeRenderer*` returns nothing (just the broader engine + activity-manager files).
- McDonald's regression test passes.

**Test plan:**
```bash
bash scripts/build-shim-dex.sh
adb push out/aosp-shim.dex /data/local/tmp/westlake/
bash scripts/check-real-mcd-proof.sh
# Expect: MCD_APP_PROOF still emitted from McDonald's-launched path
```

**Risks:**
- Some "MCD_*" markers may be in code paths that other apps don't exercise; removing them could hide regressions. Mitigation: keep MiniActivityManager's MCD branches until M4a is live.

**Estimated effort:** 2–3 hours

---

### C3 — `DexLambdaScanner.java` deletion (folded into C1)

Already handled by C1's deletion list. Tracked separately only to be explicit that DexLambdaScanner has no other users.

---

### C4 — Audit and document `WestlakeLauncher.java` for slimming

**Goal:** identify which parts of the 22 K LOC launcher are bypassed once M3+M4 are live.

**Scope:**
- Read-only audit. Produce `docs/engine/WESTLAKE_LAUNCHER_AUDIT.md` listing each major section with one of: `[keep]`, `[delete after Mx]`, `[fold into Y]`.
- No code changes. Slim only happens during M7 once services are validated.

**Deliverables:**
- `docs/engine/WESTLAKE_LAUNCHER_AUDIT.md` (~2 K words)

**Acceptance:**
- Document identifies at least: app-load path, Activity-launch path, Hilt seeding, Coroutine runtime prep, render loop, McD bootstrap path. Each tagged with disposition.

**Estimated effort:** 3 hours

---

### C5 — Audit `shim/java/android/*` for over-shimming

**Goal:** identify framework classes shimmed in our tree that could be replaced by AOSP framework.jar verbatim once binder substitution makes their services reachable.

**Scope:**
- Read-only audit of:
  - `shim/java/android/view/View.java` (30 K LOC — AOSP-derived, keep)
  - `shim/java/android/view/ViewGroup.java` (9 K LOC — AOSP-derived, keep)
  - `shim/java/android/widget/*` — most are AOSP-derived, keep
  - `shim/java/android/app/WestlakeActivityThread.java` (5137 LOC — custom — audit whether AOSP ActivityThread can replace it post-M4)
  - `shim/java/android/app/MiniActivityManager.java` (4883 LOC — custom — replaced by M4a)
  - `shim/java/android/app/WestlakeInstrumentation.java` (2457 LOC — custom — audit whether AOSP Instrumentation can replace it)
  - `shim/java/android/os/ServiceManager.java` — replaced by M3's wiring to real servicemanager
- Produce `docs/engine/ANDROID_SHIM_AUDIT.md`.

**Deliverables:**
- Audit document with disposition tags per class.

**Acceptance:**
- Audit covers the 10 largest custom classes in `shim/java/android/`. Each has a recommendation: keep / replace-with-AOSP / fold-into-service.

**Estimated effort:** 4 hours

---

## Phase 1: Foundation (sequential critical path)

### M1 — `libbinder.so` musl port

**Goal:** produce a Westlake-owned `libbinder.so` that compiles against musl libc and works on both the Pixel 7 Pro (Android, kernel binder present) and OHOS (after M9). Includes the full Binder C++ runtime: `ProcessState`, `IPCThreadState`, `Parcel`, `Binder`, `BBinder`, `BpBinder`, `IServiceManager`.

**Scope:**
- Locate AOSP `frameworks/native/libs/binder` source (probably needs cloning from `https://android.googlesource.com/platform/frameworks/native`).
- Build environment: cross-compile to ARM64, link against musl from OHOS sysroot (`/home/dspfac/openharmony/prebuilts/clang/.../sysroot`).
- Patch out bionic-specific dependencies:
  - `pthread_setname_np` — musl has this since 1.1.16, may need name truncation
  - bionic TLS slot use in `IPCThreadState` — replace with `__thread` or `pthread_key_t`
  - `__system_property_get` and `init_proc` mock — stub or replace
  - `libcutils` dependencies — reimplement minimally or bring in `libcutils-mini`
- Output: `out/binder-port/libbinder.so`, ~5 MB stripped.

**Deliverables:**
- New directory `aosp-libbinder-port/` containing the build scripts, patches, and any minimal shim source.
- `out/binder-port/libbinder.so` arm64 binary.
- Build script `aosp-libbinder-port/build.sh`.
- Test harness `aosp-libbinder-port/test/binder_smoke.cc` — a C++ program that opens `/dev/binder`, fetches `IServiceManager`, lists services, exits cleanly.

**Acceptance:**
- `file out/binder-port/libbinder.so` shows ELF 64-bit ARM64, dynamic library.
- `LD_LIBRARY_PATH=out/binder-port out/binder-port/test/binder_smoke` on the Pixel exits 0 and prints the device's service list (>= 50 services since this is real Android).
- No bionic symbols referenced (`readelf -d out/binder-port/libbinder.so | grep NEEDED` shows only musl/standard libs).

**Test plan:**
```bash
# On dev box, cross-compile
cd aosp-libbinder-port
./build.sh

# Push to Pixel
adb push out/binder-port/libbinder.so /data/local/tmp/westlake/lib/
adb push out/binder-port/test/binder_smoke /data/local/tmp/westlake/bin/

# Run smoke test against Pixel's real /dev/binder
adb shell "cd /data/local/tmp/westlake && LD_LIBRARY_PATH=lib bin/binder_smoke"
# Expect: prints list of services like 'activity', 'window', 'package', 'audio', ...
# Exit code: 0
```

**Risks:**
- AOSP libbinder has accumulated extensive bionic coupling over the years. Some functions (e.g., `IPCThreadState::self()`) use TLS slots that bionic dedicates for this purpose; musl doesn't. Mitigation: use `__thread` storage. Backup: write a minimal `IPCThreadState` from scratch using `pthread_key_t`.
- `binderfs` is needed for Phase 2 OHOS but not for Phase 1 if we're using the phone's `/dev/binder`. Phase 1 can use the device-tree path directly.

**Estimated effort:** 3–5 days

**Dependencies:** None.

---

### M2 — `servicemanager` musl port

**Goal:** AOSP `servicemanager` binary recompiled for musl. The daemon the kernel hands "binder context 0" to.

**Scope:**
- AOSP source: `frameworks/native/cmds/servicemanager` (~2 K LOC C++).
- Same musl patches as M1.
- Link against our `libbinder.so` from M1.
- Standalone executable: `out/binder-port/servicemanager`.

**Deliverables:**
- `aosp-servicemanager-port/` build directory.
- `out/binder-port/servicemanager` binary.

**Acceptance:**
- `out/binder-port/servicemanager` runs on Pixel, owns binder context, accepts service registrations.
- Smoke test: `binder_smoke` from M1, modified to register a service and look it up, succeeds.

**Test plan:**
```bash
# Use a private binderfs to avoid colliding with phone's real servicemanager
adb shell "unshare -Uirmpf bash -c '
  mkdir -p /tmp/wlk-bfs && mount -t binder binder /tmp/wlk-bfs
  ANDROID_BINDER_DEVICE=/tmp/wlk-bfs/binder \
    /data/local/tmp/westlake/bin/servicemanager &
  sleep 1
  ANDROID_BINDER_DEVICE=/tmp/wlk-bfs/binder \
    /data/local/tmp/westlake/bin/binder_smoke --register-and-lookup
'"
# Expect: smoke test prints 'addService: ok' and 'getService: same binder'
```

**Risks:**
- userns + binderfs may require kernel features the Pixel's Android kernel doesn't enable by default. Check: `cat /proc/sys/kernel/unprivileged_userns_clone`. If disabled, run as adb-shell user with explicit `mount`.
- AOSP servicemanager has SELinux integration we'll need to disable for our sandbox (we're not in an SELinux-policied context).

**Estimated effort:** 2 days

**Dependencies:** M1

---

### M3 — dalvikvm wired against Westlake `libbinder.so`

**Goal:** dalvikvm boots AOSP framework.jar in a sandbox where the only available libbinder is ours and the only available servicemanager is ours. Verify framework lookup of services through real `ServiceManager.getService(name)` reaches our servicemanager.

**Scope:**
- Configure dalvikvm's `LD_LIBRARY_PATH` and/or linker namespace to load our `libbinder.so` instead of the phone's.
- Remove `shim/java/android/os/ServiceManager.java` from `aosp-shim.dex` so that AOSP framework's real `ServiceManager.java` (which calls into native libbinder via `android_os_ServiceManager_getService` JNI) is used instead.
- Verify framework calls to `ServiceManager.getService("xxx")` reach our servicemanager and return null (since no services are registered yet — that's M4's job).

**Deliverables:**
- Updated boot script (`scripts/run-noice-phone-gate.sh` or new `scripts/run-binder-sandbox.sh`) that sets up userns + binderfs + servicemanager + dalvikvm.
- aosp-shim.dex rebuilt without `android.os.ServiceManager` shim.

**Acceptance:**
- dalvikvm boots inside the sandbox.
- AOSP `Looper.prepareMainLooper()` runs without `ServiceManager`-related NPE.
- Logcat shows real AOSP `ServiceManager.getService("activity")` reaching our servicemanager (servicemanager logs the lookup) and returning null (since no services registered).
- A trivial `Hello World` Java program (just `System.out.println`) runs to completion.

**Test plan:**
```bash
# Inside sandbox
adb shell "unshare -Uirmpf /data/local/tmp/westlake/bin/sandbox-boot.sh hello_world.dex"
# Expect:
#   1. servicemanager: 'binder context 0 obtained'
#   2. dalvikvm: 'main loop starting'
#   3. (no shim) hello_world output
#   4. clean exit code 0
```

**Risks:**
- AOSP framework's `ServiceManager` JNI calls into `android_util_Binder.cpp` which is part of libandroid_runtime — we may need to port that too, or stub it. Investigate during M3.
- Class-loading order: framework.jar's static initializers fire on boot; some try to look up services. Need to verify they tolerate `getService → null`.

**Estimated effort:** 3 days

**Dependencies:** M1, M2

---

### M4 — Core Java service implementations

**Strategy:** discover required service methods by running noice under the Phase-1 sandbox with M3 in place, observe which Binder transactions hit each null service, then implement exactly those methods. Subtractive validation in action.

Each sub-milestone is independent after M3 is green. Multiple agents can claim sub-milestones in parallel.

#### DISCOVERY RESULTS (W2-discover, 2026-05-12)

**Canonical inventory:** `docs/engine/M4_DISCOVERY.md`
**Reproducible harness:** `aosp-libbinder-port/test/NoiceDiscoverWrapper.java` + `aosp-libbinder-port/test/noice-discover.sh`
**Test pattern:** dalvikvm boots with framework.jar + ext.jar + services.jar + aosp-shim.dex on `-Xbootclasspath`; drives noice's Application bootstrap through PathClassLoader; logs every `ServiceManager.getService(name)` lookup.

**Headline finding:** noice's `Application.onCreate()` does NOT reach any binder transaction. It fails earlier at `Context.getPackageName()` (the very first Hilt-DI bootstrap step in `com.github.ashutoshgngwr.noice.repository.p.<init>(Context)` → `m2.w.a(Context)`). The blocker is **Context plumbing, not binder service implementations**.

**New milestone M4-PRE** added below as a critical-path prerequisite to M4a-e. **M4 sub-milestones cannot start their acceptance testing without M4-PRE.**

**Tier-1 services for noice's first frame (from M4_DISCOVERY.md):**

* `M4-PRE` Minimal Context plumbing (option 5a in discovery doc) — **NEW**, blocks everything else.
* `M4a` `activity` (IActivityManager) — Tier 1, ~15 methods (broadcastIntent, registerReceiver, getCurrentUser, attachApplication)
* `M4b` `window` (IWindowManager) + IWindowSession — Tier 1, ~22 methods (openSession, addToDisplay, relayout, finishDrawing)
* `M4c` `package` (IPackageManager) — Tier 1, ~10 methods (getApplicationInfo, resolveIntent, queryBroadcastReceivers)
* `M4d` `display` (IDisplayManager) — Tier 1, 4 methods (getDisplayInfo, getDisplayIds, registerCallback)
* `M4e-notification` `notification` (INotificationManager) — Tier 2, 6 methods (notify, cancel, getActiveNotifications)
* `M4e-ime` `input_method` (IInputMethodManager) — Tier 2, 5 methods (showSoftInput, hideSoftInput)
* `M4-power` (NEW) `power` (IPowerManager) — Tier 1, 3 methods (acquireWakeLock, releaseWakeLock, isInteractive)
* `M4-appops` (NEW) `appops` (IAppOpsService) — Tier 2, 2 methods (checkOperation, noteOperation)

**Coordination contract:** Each M4 sub-milestone agent reads `docs/engine/M4_DISCOVERY.md` §7 first for its specific method list. The transaction codes listed there are *initial estimates from AIDL declaration order*; the implementing agent re-derives authoritative codes from the generated `IXxxService$Stub.TRANSACTION_*` constants via Java reflection at runtime, then writes those into the service implementation.

**Total estimated effort (per M4_DISCOVERY.md §9):** ~10.5 person-days solo, ~3-4 calendar days with 5 agents in swarm parallel after M4-PRE (1 day) completes.

---

#### M4-PRE — Minimum Context plumbing (NEW, critical-path prerequisite to M4a-e)

**Goal:** synthesize enough of a Context that `Application.attach(Context)` succeeds for noice (and other apps that use Hilt) without an `ActivityThread`. Recommended approach: option 5a from `M4_DISCOVERY.md` §5 — a `WestlakeMinimalContext extends Context` returning sensible defaults and forwarding `getSystemService` to the M4 binder services as they come online.

**Scope:**
* New file: `shim/java/com/westlake/services/WestlakeMinimalContext.java` (or in `services/`).
* Concrete impl of all 148 `Context` abstract methods. Most return null or throw `UnsupportedOperationException`; `getPackageName`/`getOpPackageName`/`getApplicationContext`/`getClassLoader`/`getMainLooper` return real values.
* `getSystemService(String)` dispatch table that maps `"activity"` → `new ActivityManager(...)`, `"window"` → `new WindowManagerImpl(...)` etc. once each M4 service is registered.
* A small bootstrap in `WestlakeLauncher` (or in a new `aosp-libbinder-port/test/NoiceLifecycleDriver.java`) that creates a `WestlakeMinimalContext`, calls `Application.attach(ctx)`, then `Application.onCreate()`.

**Deliverables:**
* `WestlakeMinimalContext.java` (~500 LOC, mostly null returns)
* Updated `NoiceDiscoverWrapper.java` PHASE D that actually constructs+attaches the context (the current iteration skipped this — see `M4_DISCOVERY.md` §5a).

**Acceptance:**
* noice's `Application.onCreate()` runs past the Hilt-DI Context-NPE. The next failure should be a `getSystemService("...")` returning null (which is the M4 sub-milestone signal).
* `NoiceDiscoverWrapper` PHASE D logs "attach OK", PHASE E logs at least 1-2 binder service lookups (Hilt waking up) before next failure.

**Test plan:**
```bash
cd /home/dspfac/android-to-openharmony-migration
bash scripts/build-shim-dex.sh                   # bundle new minimal context
bash aosp-libbinder-port/build_discover.sh       # rebuild test
$ADB push aosp-libbinder-port/out/NoiceDiscoverWrapper.dex /data/local/tmp/westlake/dex/
$ADB shell "su -c 'cd /data/local/tmp/westlake && bash bin-bionic/noice-discover.sh'"
# Expect: PHASE D PASSED, PHASE E reaches at least the first getSystemService lookup
```

**Risks:**
* Hilt DI may have undocumented expectations on Context methods (e.g. specific package info structure). Iterate: each new NPE adds one more concrete method.
* `getResources()` is hard — Resources requires AssetManager which requires real APK paths. Likely option 5a defers to throwing UnsupportedOperationException until a `Resources` proxy is added later.

**Estimated effort:** 1 day (single agent)

#### M4a — `WestlakeActivityManagerService`

**Goal:** implement `IActivityManager.Stub` covering the transactions noice's launch path exercises.

**Scope:**
- New file: `shim/java/com/westlake/services/WestlakeActivityManagerService.java`.
- Extends `android.app.IActivityManager$Stub` (from AOSP).
- Register at engine boot via `ServiceManager.addService("activity", new WestlakeActivityManagerService())`.
- Implement only the methods noice's launch logs as called (probably: `attachApplication`, `getRunningTasks`, `getProcessMemoryInfo`, `bindIsolatedService`, `unbindService`, `broadcastIntentWithFeature` — to be discovered).

**Acceptance:**
- noice's MainActivity.onCreate runs through `ActivityThread.handleLaunchActivity` without service-related NPE.
- Logcat shows no "transaction code X on IActivityManager not implemented" warnings during MainActivity setup.

**Test plan:**
```bash
# Run noice in sandbox; logcat should show ActivityManager transactions hitting our service
adb shell "logcat | grep -E 'WestlakeActivityManagerService|IActivityManager'"
# Verify each transaction code is handled (not falling through to default Stub.onTransact return false)
```

**Risks:**
- Service has many methods (~150 in modern AOSP). Implementing all is wasteful; implementing only those called requires careful coverage measurement.

**Estimated effort:** 2 days

#### M4b — `WestlakeWindowManagerService`

Same shape as M4a, for `IWindowManager.Stub`. Most important methods: `addView`/`removeView` (via WindowSession), `getCurrentImeTouchRegion`, display-info methods.

**Estimated effort:** 2 days

#### M4c — `WestlakePackageManagerService`

For `IPackageManager.Stub`. Returns info about loaded APK. Methods: `getPackageInfo`, `getApplicationInfo`, `resolveIntent`, `getInstalledApplications` (empty list).

**Estimated effort:** 1.5 days

#### M4d — `WestlakeDisplayManagerService`

For `IDisplayManager.Stub`. Returns sensible display info: 1080×2280, 60Hz, density 480. Methods: `getDisplayInfo`, `getDisplayIds`, `registerCallback`.

**Estimated effort:** 0.5 day

#### M4e — `WestlakeNotificationManagerService` + `WestlakeInputMethodManagerService`

Mostly empty stubs returning safe defaults. Required for getSystemService to not return null.

**Estimated effort:** 1 day each

---

### M5 — `westlake-audio-daemon`

**Scoping doc:** `docs/engine/M5_AUDIO_DAEMON_PLAN.md` (architect-produced 2026-05-12; AIDL surface inventory, build scaffold, backend abstraction, ~6.5-day Phase-1 effort estimate, risk register; mirrors `aosp-libbinder-port/BUILD_PLAN.md` shape).

**Goal:** native daemon implementing `IAudioFlinger` (and `IAudioPolicyService`) over real Binder. Phase-1 backend: bridge to Android's AAudio.

**Scope:**
- New directory `native/audio-daemon/`.
- C++ code linking against M1's libbinder.so.
- Implements `IAudioFlinger.Stub` — `createTrack`, `openOutput`, `setMasterVolume`, etc.
- Backend abstraction layer (so Phase 2 OHOS swap is `#ifdef`):
  ```cpp
  #ifdef PHASE1_AAUDIO
    #include "backend/aaudio.h"
  #elif OHOS_TARGET
    #include "backend/ohos_audiorenderer.h"
  #endif
  ```
- Boot: launched by orchestrator after servicemanager is up; registers `media.audio_flinger`.

**Deliverables:**
- `native/audio-daemon/audio-daemon` binary.
- Test harness: a small standalone program that creates an AudioTrack via libaudioclient.so, writes a 440Hz tone for 1 second, verifies daemon receives the data.

**Acceptance:**
- Daemon starts, registers service, accepts AudioTrack creation.
- AudioTrack.write() in dalvikvm produces audible output on the Pixel.
- noice's "Rain" sound plays when tapped.

**Test plan:**
```bash
# Standalone test (no dalvikvm needed)
adb shell "/data/local/tmp/westlake/bin/audio_smoke 440 1000"
# Expect: 1 second of 440Hz tone audible from speaker
```

**Risks:**
- IAudioFlinger uses shared-memory regions for audio buffers (BlobAlloc, MemoryDealer). Need ashmem support OR memfd-based replacement.
- AOSP's libaudioclient may use specific AIDL versions. Pin to one AOSP version.

**Estimated effort:** 4–5 days

**Dependencies:** M3 (libbinder + servicemanager working)

---

### M6 — `westlake-surface-daemon`

**Scoping doc:** `docs/engine/M6_SURFACE_DAEMON_PLAN.md` (architect-produced 2026-05-12; AIDL surface inventory across ISurfaceComposer/ISurfaceComposerClient/IGraphicBufferProducer/IDisplayEventConnection, memfd-backed GraphicBuffer substitute strategy, Phase-1 DLST pipe backend reuse, ~12-day Phase-1 effort estimate, risk register including HWUI/memfd spike requirement; mirrors `aosp-libbinder-port/BUILD_PLAN.md` shape).

**Goal:** native daemon implementing `ISurfaceComposer`. Phase-1 backend: render frames to current SurfaceView pipe; Phase 2 backend: direct to XComponent.

**Scope:**
- New directory `native/surface-daemon/`.
- Implements minimum `ISurfaceComposer.Stub` methods: `createConnection`, `getDisplayInfo`, plus `IGraphicBufferProducer.Stub` for BufferQueue.
- Frames received from libgui.so via real binder.
- Backend writes to:
  - Phase 1: existing pipe → Compose host SurfaceView mechanism (reuse current rendering)
  - Phase 2: OHOS XComponent surface (via OHBridge or direct)

**Deliverables:**
- `native/surface-daemon/surface-daemon` binary.
- Reuses the existing DLST pipe for Phase 1 — minimal protocol change.

**Acceptance:**
- noice's main_activity renders end-to-end through libgui → real binder → surface-daemon → pipe → SurfaceView.
- Rendering FPS ≥ 30 (subjective: no visible stutter on noice's idle UI).

**Test plan:**
```bash
# After M3, M5, M6 all up: launch noice
adb shell "logcat | grep -E 'surface-daemon|BufferQueue|SurfaceView'"
# Expect: BufferQueue.dequeueBuffer → queueBuffer → consumer (surface-daemon)
# Visual: noice UI visible on phone screen as before, but now driven by real binder
```

**Risks:**
- BufferQueue protocol is hot-path; we need shared-memory (gralloc) buffer handles, not Parcel-marshaled buffers. AOSP gralloc requires HAL; we need a memfd-based gralloc shim.
- This is the highest-risk M in Phase 1. Budget extra.

**Estimated effort:** 5–7 days

**Dependencies:** M3

---

## Phase 1: Integration

### M7 — noice end-to-end

> **CR38 update (2026-05-13):** Pre-scoping doc `docs/engine/CR38_M7_M8_INTEGRATION_SCOPING.md` (709 LOC) refines this entry without expanding scope. Highlights: target device is **OnePlus 6 cfb7c9e3** (not Pixel); canonical 12-step boot in CR38 §2; 5-process tree in CR38 §1; 7 specific acceptance signals in CR38 §5.1 (replacing the looser "no visible defects" bar); effort estimate narrowed to **1.5-2.0 person-days** (within V1's 2-3 day band). CR38 should be the M7 Integrator's first read.

**Goal:** noice runs in the binder-sandbox on the Pixel, renders main UI, plays audio, no visible defects vs. native run.

**Scope:** integration testing only. No new code beyond stitching previous milestones.

**Acceptance:**
- 10-minute interactive session: open noice, navigate through Library/Presets/Sleep Timer tabs, tap a sound to play it, pause it, navigate back. No crashes.
- Side-by-side screenshot comparison with noice running natively on the same phone. Layout matches within ±5px on key elements.
- logcat shape comparison: same major lifecycle events (Activity create/resume, Fragment add, view inflate). Our log lines (`WL`, `PF`) shouldn't show NPE/AIOOB/CCE.

**Test plan:**
```bash
# Boot binder sandbox
adb shell '/data/local/tmp/westlake/bin/sandbox-boot.sh com.github.ashutoshgngwr.noice'

# Compare visually
adb exec-out screencap -p > /tmp/noice-westlake.png
# (Compare against /tmp/noice-native.png captured from native run)

# Compare logcat shapes
adb shell pidof dalvikvm | xargs -I{} adb logcat -d --pid={} > /tmp/noice-wl-log.txt
diff <(grep -oE '[A-Z][a-zA-Z]+: ' /tmp/noice-wl-log.txt | sort -u) \
     <(grep -oE '[A-Z][a-zA-Z]+: ' /tmp/noice-native-log.txt | sort -u)
```

**Risks:** integration of M3–M6 may surface gaps that require returning to individual M's.

**Estimated effort:** 2–3 days

---

### M8 — McDonald's regression

> **CR38 update (2026-05-13):** Pre-scoping doc `docs/engine/CR38_M7_M8_INTEGRATION_SCOPING.md` refines this entry. Acceptance bar expanded from V1's narrow "SplashActivity.performResume" to the full e2e set in CR38 §5.2 (dashboard sections inflate + HTTP works + `MCD_APP_PROOF` markers + zero crashes). Top-1 M8-specific risk surfaced: SplashActivity → DashboardActivity multi-activity intent dispatch (V2 §8.4); per CR38 §7.2 budgeted at 0.5-1 person-day inside the §6.2 total estimate. Effort estimate narrowed to **1.0-1.5 person-days** (within V1's 0.5-3 day band).

**Goal:** McDonald's still launches and reaches `SplashActivity.performResume` post-pivot.

**Scope:** run existing McD test (`./scripts/check-real-mcd-proof.sh`).

**Acceptance:**
- `MCD_APP_PROOF` markers emitted as before.
- SplashActivity reaches `onResume`.
- Branded splash renders.

**Test plan:**
```bash
bash scripts/check-real-mcd-proof.sh
# Expect: 'MCD_APP_PROOF' present in output
```

**Risks:** if McDonald's depends on a service method we haven't covered in M4, it may regress. Mitigation: rerun this between every M4 sub-milestone, not just at end.

**Estimated effort:** 0.5 day (assuming green) or 1–3 days (if regressions surface)

---

## Phase 2: OHOS Port (only after M7+M8 green)

> **CR41 update (2026-05-13):** Pre-scoping doc `docs/engine/CR41_PHASE2_OHOS_ROADMAP.md` refines the §M9-M13 estimates downward in aggregate (Phase 2 total: V1 ~12-18 days → CR41 ~13 days expected, 15 with reserve). Key finding: OHOS standard-system kernel **already** ships `CONFIG_ANDROID_BINDER_IPC=y` in every standard board defconfig (verified at `kernel/linux/config/linux-5.10/type/standard_defconfig` and per-board configs); M9 collapses from "port binder.ko" to "verify and tune access." Top schedule risk is hardware acquisition (no OHOS standard-system phone in lab), not engineering. Phase 2 Builder should read CR41 §1 (architecture diff), §7 (timeline), §8 (cross-Phase-2 risks) first.

### M9 — `binder.ko` for OHOS kernel

**Goal:** Linux `binder.ko` builds against OHOS standard-system kernel headers and loads on a real OHOS phone.

> **CR41 finding (2026-05-13):** OHOS kernel 5.10 already ships binder built-in (not as a module), with `/dev/binder /dev/hwbinder /dev/vndbinder` registered statically. See CR41 §2.2.1 for the verification grep. M9 is therefore a configuration-verification milestone, not a porting milestone.

**Scope:**
- Locate OHOS standard-system kernel source.
- Apply `CONFIG_ANDROID_BINDER_IPC=y` (and `CONFIG_ANDROID_BINDER_DEVICES="binder"`).
- Build, load, verify `/dev/binder` appears.

**Deliverables:**
- OHOS-kernel-targeted `binder.ko`.
- Loading procedure documented.

**Acceptance:**
- On OHOS phone, `ls /dev/binder` succeeds after `insmod binder.ko`.

**Risks:** OHOS may sign/restrict kernel modules. Coordination with OHOS team likely needed.

**Estimated effort:** ~~3–5 days (with OHOS team), 2–3 weeks (without)~~ **CR41-revised: 0.5–2.0 person-days** (binder already enabled; effort is verification + possibly one-line config patch + SELinux uid grant). See CR41 §2.

---

### M10 — libbinder + servicemanager for OHOS

**Goal:** rebuild M1+M2 artifacts against OHOS sysroot + OHOS kernel headers.

**Scope:** mostly a build-script tweak. Source unchanged.

**Acceptance:** binder_smoke from M1 runs on OHOS phone, lists services after starting our servicemanager.

**Estimated effort:** 1–2 days *(CR41 confirms; `aosp-libbinder-port/Makefile` already ships the `aarch64-linux-ohos` musl target as default. M10 is verify + libc++ ABI tuning. See CR41 §3.)*

---

### M11 — Audio daemon to OHOS AudioRenderer

**Goal:** swap audio-daemon's backend from Android AAudio to OHOS AudioRenderer. Same daemon, different `#ifdef` branch.

**Acceptance:** noice audio plays on OHOS phone with audible quality matching Android.

**Estimated effort:** 2–3 days *(CR41 confirms; activate pre-scaffolded `OhosBackend.cpp` per M5 plan §4.3. ~350 LOC SPSC-ring + OnWriteData callback. See CR41 §4.)*

---

### M12 — Surface daemon to OHOS XComponent

**Goal:** swap surface-daemon's backend to direct OHOS XComponent surface output. Drop the SurfaceView pipe for native OHOS rendering.

**Acceptance:** noice UI renders on OHOS phone at ≥30 FPS.

**Estimated effort:** 4–5 days *(CR41 confirms; activate pre-scaffolded `XComponentBackend.cpp` per M6 plan §4.3. ~400 LOC OH_NativeWindow + SurfaceId handshake. See CR41 §5.)*

---

### M13 — noice on OHOS

**Goal:** noice's full UX works on an OHOS phone. Mirror of M7 acceptance criteria but on OHOS hardware.

**Estimated effort:** ~~2–3 days~~ **CR41-revised: 3.5–4.5 person-days** (integration testing + host HAP authoring in ArkUI ETS + ETS-to-native FFI + cross-platform regression. See CR41 §6.)

---

## Phase 3+ (future, post-OHOS validation)

### M14 — WebView (Chromium subprocess support)

WebView requires Chromium's renderer/GPU/network subprocesses, which use real binder for IPC. Needs M9+M10 done. Subprocesses run as separate processes (we'd allow this for WebView even in single-app context). ~2 weeks effort.

### M15 — Camera daemon

For Camera2-using apps. Mostly mirrors M5's structure. ~1 week.

### M16 — Media daemon

For MediaCodec/MediaPlayer-using apps. Mirrors M5. ~1.5 weeks.

### M17 — 10-app benchmark suite

Validate against TikTok, Instagram, YouTube, Spotify, Facebook, Maps, Zoom, Grab, Duolingo, Uber. Each surface gaps gets a targeted fix in service or daemon code (never in app-specific shims). ~3 weeks.

---

## Test Plan Master Summary

| Test | Source | Frequency |
|---|---|---|
| `binder_smoke` registers/lookups in sandbox | M1, M2 | After each M1/M2 change |
| `Hello World` dalvikvm boot | M3 | After M3 changes |
| Per-service transaction-code coverage | M4a-e | After each M4 sub-milestone |
| `audio_smoke` 440Hz tone | M5 | After each audio-daemon change |
| noice end-to-end (visual + logcat) | M7 | Before any milestone-completion claim |
| McDonald's regression | M8 | Before any milestone-completion claim (gating) |

**Master regression script:** `scripts/binder-pivot-regression.sh` (to be created in C0) runs all of the above in order and prints pass/fail.

---

## Risk Register

| ID | Risk | Likelihood | Impact | Mitigation |
|---|---|---|---|---|
| R1 | musl port of libbinder reveals deep bionic coupling | Medium | High | Time-box M1 to 5 days; if stuck, write minimal libbinder from scratch (smaller scope, AIDL-shaped) |
| R2 | userns+binderfs disabled on Pixel | Low | High | Pre-check with `cat /proc/sys/kernel/unprivileged_userns_clone`; if disabled, use Magisk/root path for testing only |
| R3 | BufferQueue / gralloc complexity blows M6 schedule | Medium | High | Phase 1 keeps SurfaceView pipe as backend; only M12 needs true gralloc port |
| R4 | OHOS team rejects binder.ko inclusion | Medium | Critical | Continue Phase 1 + start LiteOS-A scoping in parallel; document as P2 escalation |
| R5 | AOSP Android-version drift (16 → 17) breaks AIDL codes | Low | Medium | Pin to one AOSP version (Android 16) for now; document refresh procedure |
| R6 | C1 cleanup masks regressions before M4 services are live | Medium | Medium | Run McD regression after C1 immediately; rollback if it breaks |
| R7 | Per-app McD constants still needed by some code path | Medium | Low | C2 leaves WestlakeLauncher/MiniActivityManager hacks until M4 — controlled removal |

---

## Owners & Coordination

This document is the canonical work plan. Updates require PR (mention this file in commit messages). Agents claim milestones via `TaskUpdate` setting `owner` to their identifier. No two agents should own the same M-task simultaneously.

Coordination check-ins after each phase:
- After C1+C2+C3 deletion: verify McDonald's regression (M8).
- After M3: verify Hello World boots.
- After M4a-e: verify noice MainActivity.onCreate completes without service-NPE.
- After M5: verify standalone audio works.
- After M6: verify standalone surface rendering works.
- After M7: full noice e2e.
- After M8: McD regression green.

**Only after the above 8 green checkpoints does Phase 2 begin.**
