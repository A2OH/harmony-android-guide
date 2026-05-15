# Westlake Binder Pivot — Architectural Design

**Status:** Draft (2026-05-12) — decision pending implementation; awaiting Phase 1 validation
**Supersedes:** Renderer-driven `WestlakeFragmentLifecycle` substitution path
**Related:** `docs/engine/ARCHITECTURE.md` (original "Android-as-Engine" design — still load-bearing for pure-Java framework layer)

---

## 1. Executive Summary

Westlake's current shim-and-bypass approach is hitting linear scaling with app coverage — each new app surfaces ~5 new framework-service gaps requiring custom shim code. The diagnostic for why is now clear: **we substitute at the framework-class level, where the API surface fans out across hundreds of classes.** The right substitution point is **the Binder service boundary** — a single uniform interface, AOSP-defined, that ALL framework→service calls flow through.

Decision: pivot to substituting at Binder. Build a Westlake-owned `libbinder.so`, `servicemanager`, and (Phase 2) ship the `binder.ko` kernel module to OHOS. AOSP framework.jar runs unmodified; Java service implementations extend the AOSP-generated `Stub` classes and get registered through the standard `ServiceManager.addService` pathway; AOSP's existing same-process `Stub.asInterface()` optimization elides marshaling for Java-to-Java calls; AOSP native libraries (`libaudioclient.so`, `libcamera2_jni.so`, etc.) continue to use Binder for service IPC, routed to Westlake-owned native daemons that translate to OHOS APIs.

The architecture is closer in spirit to a slimmed Anbox than to the current "engine-with-shim-layer" — but ~5× lighter than Anbox because we omit zygote, init, full HAL, SELinux, full system image, and isolation namespaces. Estimated resident footprint: ~150 MB active vs. Anbox's ~700 MB-1 GB.

The trade we accept: kernel-binder dependency on OHOS targets. The user has explicitly approved this trade — OHOS kernel cooperation is preferable to per-app shim work that doesn't converge.

---

## 2. Background: How We Got Here

### 2.1 Current state of the codebase

- **dalvikvm** — ART11, ARM64, 16 MB static binary, JIT-enabled. Runs on musl (OHOS sysroot). Validated against McDonald's APK (177 MB, 119 K classes, full Dagger/Hilt).
- **`shim/java/`** — ~2300 Java source files, ~300 K LOC total:
  - `android.*` — partial AOSP-derived framework (`View` 30 K LOC, `TextView` 13 K LOC, `ViewGroup` 9 K LOC; many widget shims; full `MiniActivityManager`, `WestlakeActivityThread`, `WestlakeInstrumentation`).
  - `androidx.*` — Hilt, lifecycle, fragment, activity, compose, recyclerview, constraintlayout shims.
  - `com.westlake.engine.*` — `WestlakeLauncher` (~23 K LOC), `WestlakeRenderer`, `WestlakeFragmentLifecycle` (3087 LOC — the panic-mode bypass added this week), `WestlakeContext`, `WestlakeResources`, `WestlakeLayoutInflater`, `WestlakeNavGraph`, `WestlakeVector`, `WestlakeTheme`, `WestlakeLauncher`, `DexLambdaScanner`.
  - Third-party app stubs: `com.google.*`, `com.facebook.*`, `com.airbnb.*`, `com.newrelic.*`, `com.mcdonalds.*`.
- **OHBridge** (`liboh_bridge.so`) — ~15 JNI bridges from Java/native into OHOS surface/audio/input edges.
- **~50 ART patches** (`PF-arch-001..051`) covering load-bearing dalvikvm bug fixes (long-jump null-vtable, SIGBUS in `PathClassLoader.toString`, Kotlin Intrinsics intrinsic, etc.) plus some that are now obsolete (AIOOB stack trace recovery was a diagnostic, not a fix).
- **Working apps:** Counter, Tip Calculator, TODO List, MockDonalds (toy), McDonald's (real). All single-activity, simple lifecycle, minimal multi-fragment navigation.

### 2.2 The pattern that broke

Westlake hits a wall on apps with multi-fragment navigation, deep dependency injection, or non-trivial NavController usage. **noice** (`com.github.ashutoshgngwr.noice` — multi-fragment NavController + BottomNavigation + Hilt) is the current target. Over the last seven days, the pattern of fixes has been:

1. App crashes at framework-service NPE
2. Fix the shim or seed the field
3. App advances ~50 dex bytecodes
4. Crash at the next framework-service NPE
5. Loop

Recent tasks #86–#97 in the task tracker (`B`: Hilt graph injector, `C`: Fragment lifecycle, `D`: WestlakeContext, `E`: PhoneLayoutInflater seeding, `F`: tree fusion, `G`: ViewModelStore + Lifecycle seeding, `H`: NavController seeding, `I`: LinkedHashMap seeding, `J`: AIOOB diagnostic, `K`: Gson Intrinsics decoding, `L`: Lazy<T> dex bytecode scan, `M`: enum field seeding) all follow this pattern. Each unblocked ~50 dex bytecodes.

The diagnostic culminated in a stack trace from `Service.getServiceWithMetadata()` returning null because Android 16's `ServiceManager.rawGetService` now wraps results in a metadata-carrying object that our shim doesn't produce. The fix would be another shim. The pattern would continue.

### 2.3 Root cause

The recent week's work is **off-architecture**. The shim layer was designed to be "thin AOSP" — we keep ~62 K LOC of AOSP source (`View`, `ViewGroup`, `TextView`, etc.) intact and stub only the system services around it. But our system-service stubs are at the *Java framework class* level (e.g., `MiniActivityManager` is what `getSystemService(ACTIVITY_SERVICE)` returns), and they replace AOSP's `IActivityManager` proxy with our own thin reimplementation. Every method AOSP framework wants to call on `IActivityManager` is a method we have to know about and implement.

The bypass added this week (`WestlakeFragmentLifecycle`) was a renderer-time band-aid: when the renderer walks the inflated View tree and finds an empty `FragmentContainerView`, it tries to reflectively run a fragment lifecycle to fill it. This skips `FragmentManager.addFragmentInternal()` (which the engine actually has, 833 LOC) and pulls in a parallel reflective driver that re-implements field seeding, ViewModelStore allocation, NavController initialization, Lazy<T> proxying, dex bytecode scanning for type discovery, etc. The bypass treats the symptom (empty FragmentContainerView) not the cause (FragmentManager's calls into our service layer didn't find what it expected).

**The engine's substitution layer is correct in intent; its substitution point is wrong.** Codex 2nd-opinion review (2026-05-12, run via `codex exec --dangerously-bypass-approvals-and-sandbox`) independently confirmed both readings:

> (a) "The boundary is right: framework/classes plus mini system services inside dalvikvm, with OHBridge only at platform edges."
> (b) "[WestlakeFragmentLifecycle] is off-architecture: renderer-triggered fragment instantiation… duplicates FragmentManager.addFragmentInternal."
> (e) "Per-app McD constants in FragmentManager/LayoutInflater are architectural debt; they will keep distorting deductions until removed."

### 2.4 The epistemic correction

We were doing **additive shimming** — observe an NPE, add a shim, observe the next NPE, add another shim. The right method is **subtractive validation** — start from a fully-working baseline (real Android, where noice renders perfectly) and remove layers one at a time, observing which removal breaks rendering. The layer whose removal *first* breaks rendering is the layer that's load-bearing; everything above it was unnecessary work. We have never done this experiment.

This document codifies the subtractive approach as the *only* permitted way to discover what Westlake needs to implement.

---

## 3. Decision: Substitute at the Binder Service Boundary

### 3.1 Why Binder is the right cut

Binder is **the** uniform service interface in Android. Every framework-to-system-service call goes through it. If we own the Binder layer:

- AOSP framework.jar runs **unmodified**. The framework looks up services by name through `ServiceManager`, gets back an `IBinder`, calls `IXxxService.Stub.asInterface(binder).someMethod(...)`. Whether the implementation is in our process, another process, or somewhere else, the framework doesn't care.
- **AOSP's same-process optimization works for free.** `Stub.asInterface(IBinder)` checks `queryLocalInterface()` and returns the local Java object directly when the service Binder lives in the caller's process. No Parcel marshaling, no IPC, just a method call. This means our in-process Java service impls don't pay Binder overhead — they get the API surface for free.
- **AOSP native libraries run unmodified.** `libaudioclient.so`, `libcamera2_jni.so`, `libgui.so` etc. link against `libbinder.so` and talk `/dev/binder`. If we provide a real binder (kernel + userspace), they work.
- **Service versioning resilience.** AIDL transaction codes are stable within an AOSP version. When we drop in a new framework.jar, we don't have to chase renamed framework classes — we just update transaction tables.
- **Single substitution point** for ALL service work, both Java and native. Replaces the current scatter of: shimmed framework classes + thin manager classes + JNI bridges + renderer bypasses.

### 3.2 What this means concretely

The engine's substitution layer has four ingredients:

1. **`libbinder.so`** — Westlake-owned, AOSP source recompiled against musl. Handles `Binder`, `BBinder`, `BpBinder`, `IPCThreadState`, `Parcel`, `ProcessState`. Talks to `/dev/binder` for cross-process IPC, but in the common single-process case never actually issues an ioctl (AOSP elides it via `localBinder()`).
2. **`servicemanager`** — AOSP source recompiled against musl. The "phone book" daemon that the kernel hands `binder context 0` to. Apps and services use it to register/look up named Binder objects.
3. **Java service implementations** — Java classes extending the AOSP-generated `IXxxService.Stub` classes. Live in-process inside dalvikvm. Registered via `ServiceManager.addService("xxx", new WestlakeXxxService())` at engine boot. Same-process optimization makes Java-to-Java calls effectively direct method dispatch.
4. **Native daemons** — separate native processes that implement specific service contracts over real Binder. Required for AOSP native libraries that go through libbinder (audio, camera, media, surface). Each daemon translates its service contract to OHOS APIs internally.

The boundary between Westlake and OHOS lives **inside each native daemon** — typically the bottom ~500 lines of C++ per daemon, behind `#ifdef OHOS_TARGET`. Everything above that line is AOSP-shaped Binder service code, identical across platforms.

### 3.3 Architecture diagram

```
┌─ Linux kernel ───────────────────────────────────────────────┐
│  CONFIG_ANDROID_BINDER_IPC=y                                 │
│  /dev/binder (provided by Westlake's binder.ko on OHOS)      │
└──────────────────────────────────────────────────────────────┘

┌─ userspace (Westlake on OHOS or Android) ────────────────────┐
│                                                              │
│ ┌─ servicemanager (~3 MB resident) ──────────────────────┐  │
│ │  AOSP source, musl rebuild                             │  │
│ │  Receives binder context 0 from kernel                 │  │
│ └─────────────────────────────────────────────────────────┘  │
│                                                              │
│ ┌─ dalvikvm process (~70 MB resident) ───────────────────┐  │
│ │                                                         │  │
│ │ ┌─ AOSP framework.jar (unchanged) ────────────────┐   │  │
│ │ │  • View / ViewGroup / TextView / Layout (62K)    │   │  │
│ │ │  • Activity / Fragment / FragmentManager / Hilt  │   │  │
│ │ │  • ActivityManager / WindowManager / Resources   │   │  │
│ │ │  • Handler / Looper / MessageQueue / AsyncTask   │   │  │
│ │ └──────────────────────────────────────────────────┘   │  │
│ │                                                         │  │
│ │ ┌─ App's classes.dex (unchanged) ─────────────────┐   │  │
│ │ │  noice / McDonald's / TikTok / Instagram        │   │  │
│ │ └──────────────────────────────────────────────────┘   │  │
│ │                                                         │  │
│ │ ┌─ Westlake Java service impls (in-process) ──────┐   │  │
│ │ │  WestlakeActivityManagerService extends          │   │  │
│ │ │      IActivityManager.Stub                       │   │  │
│ │ │  WestlakeWindowManagerService extends            │   │  │
│ │ │      IWindowManager.Stub                         │   │  │
│ │ │  WestlakePackageManagerService extends           │   │  │
│ │ │      IPackageManager.Stub                        │   │  │
│ │ │  WestlakeNotificationManagerService extends      │   │  │
│ │ │      INotificationManager.Stub                   │   │  │
│ │ │  WestlakeInputMethodManagerService extends       │   │  │
│ │ │      IInputMethodManager.Stub                    │   │  │
│ │ │  WestlakeDisplayManagerService extends           │   │  │
│ │ │      IDisplayManager.Stub                        │   │  │
│ │ │  (registered with servicemanager at boot;        │   │  │
│ │ │   same-process Stub.asInterface elides marshal)  │   │  │
│ │ └──────────────────────────────────────────────────┘   │  │
│ │                                                         │  │
│ │ ┌─ AOSP native libs (unchanged) ──────────────────┐   │  │
│ │ │  libaudioclient.so → uses libbinder → audio-d   │   │  │
│ │ │  libgui.so         → uses libbinder → surface-d │   │  │
│ │ │  libcamera2_jni.so → uses libbinder → camera-d  │   │  │
│ │ │  libmediandk.so    → uses libbinder → media-d   │   │  │
│ │ └──────────────────────────────────────────────────┘   │  │
│ │                                                         │  │
│ │ ┌─ Westlake libbinder.so (musl rebuild) ──────────┐   │  │
│ │ │  ProcessState, IPCThreadState, Parcel, BBinder,  │   │  │
│ │ │  BpBinder. Talks to /dev/binder.                 │   │  │
│ │ └──────────────────────────────────────────────────┘   │  │
│ └─────────────────────────────────────────────────────────┘  │
│                                                              │
│ ┌─ Westlake native daemons (lazy-launched) ──────────────┐  │
│ │  westlake-audio-daemon (~15 MB)                         │  │
│ │      implements IAudioFlinger over real binder          │  │
│ │      backend: AAudio (Android) | AudioRenderer (OHOS)   │  │
│ │  westlake-surface-daemon (~25 MB)                       │  │
│ │      implements ISurfaceComposer over real binder       │  │
│ │      backend: SurfaceView pipe (Android) |              │  │
│ │               XComponent surface (OHOS)                 │  │
│ │  westlake-camera-daemon (~10 MB, lazy)                  │  │
│ │      backend: Camera2 (Android) | CameraKit (OHOS)      │  │
│ │  westlake-media-daemon (~20 MB, lazy)                   │  │
│ │      backend: MediaCodec (Android) | OHOS MediaKit      │  │
│ └─────────────────────────────────────────────────────────┘  │
└──────────────────────────────────────────────────────────────┘
```

### 3.4 What we keep

- **dalvikvm** — musl static build is correct and stays.
- **AOSP framework.jar** — drops in unchanged. ~60+ MB total (we deploy a slimmed subset already).
- **AOSP-derived widget/layout source** in `shim/java/android/*` — these are AOSP files; they stay as-is.
- **OHBridge** — slims down significantly. Today it handles JNI replacements for service-bound APIs; after the pivot it only handles the surface output edge (buffer push → XComponent), audio buffer transport, raw input events, and file descriptor passing. Maybe 5 JNI methods instead of 50+.
- **Working app validation**: McDonald's, MockDonalds, Counter, Tip Calculator, TODO List. These must continue to run post-pivot (regression gate).

### 3.5 What we delete

- **`WestlakeFragmentLifecycle.java`** (3087 LOC) — the reflective fragment-lifecycle bypass. Replaced by FragmentManager-driven lifecycle, which works correctly once the underlying services are correct.
- **`DexLambdaScanner.java`** — the dex bytecode parser used to discover Lazy<T> types. Unnecessary once Fragment lifecycle is FragmentManager-driven (Lazy initializers run naturally; no need to substitute their value types).
- **`EagerLazy` proxy machinery** in `WestlakeFragmentLifecycle` — gone.
- **Field-seeding scaffolding** in `WestlakeLauncher` and elsewhere — the patterns of `Unsafe.allocateInstance` + reflective field injection are symptomatic of bypassing the framework's natural construction paths. Each instance gets revisited after the pivot.
- **Per-app hacks** — `MCD_SIMPLE_PRODUCT_HOLDER_ID`, `MCD_ORDER_PDP_FRAGMENT`, `isMcdPdpTarget`, etc., currently in `FragmentTransactionImpl` and `FragmentManager`. These violate the `feedback_no_per_app_hacks.md` rule and must be quarantined or removed.
- **Most of `MiniActivityManager`** — replaced by `WestlakeActivityManagerService extends IActivityManager.Stub`, which sits in the same place architecturally but uses AOSP's expected Stub pattern.
- **Many `android.*` framework class shims** — once AOSP framework.jar loads and reaches our services through Binder, most class-level shims become unnecessary. Audit and remove per cleanup task C5.

### 3.6 What we build

| Component | LOC estimate | Source basis | Platform-specific bits |
|---|---|---|---|
| `libbinder.so` (Westlake) | ~5 K C++ (mostly verbatim) | AOSP `frameworks/native/libs/binder` | musl recompile, TLS slot patches, optional shared-memory transport |
| `servicemanager` (Westlake) | ~2 K C (mostly verbatim) | AOSP `frameworks/native/cmds/servicemanager` | musl recompile |
| `binder.ko` build for OHOS | n/a (it's the upstream Linux module) | Linux kernel `drivers/android/binder.c` | OHOS kernel headers, Kconfig flip |
| `WestlakeActivityManagerService` | ~1.5 K Java | AOSP `services/core/java/com/android/server/am/ActivityManagerService.java` (slimmed) | None — pure Java |
| `WestlakeWindowManagerService` | ~1 K Java | AOSP `services/core/java/com/android/server/wm/WindowManagerService.java` (slimmed) | None |
| `WestlakePackageManagerService` | ~1 K Java | AOSP `services/core/java/com/android/server/pm/...` (slimmed) | Knows about loaded APKs |
| `WestlakeNotificationManagerService` | ~500 LOC | Stub, bridge to OHOS notifications | OHOS adapter inside daemon path |
| `WestlakeInputMethodManagerService` | ~500 LOC | Stub | OHOS adapter when needed |
| `WestlakeDisplayManagerService` | ~500 LOC | Returns sensible display info | OHOS adapter for real display metrics |
| `westlake-audio-daemon` | ~3 K C++ | Implements `IAudioFlinger.Stub`; backend abstraction (AAudio vs AudioRenderer) | ~500 LOC OHOS adapter |
| `westlake-surface-daemon` | ~5 K C++ | Implements `ISurfaceComposer.Stub`, BufferQueue protocol | ~500 LOC OHOS XComponent adapter |
| `westlake-camera-daemon` (later) | ~3 K C++ | Implements `ICameraService.Stub` | OHOS CameraKit adapter |
| `westlake-media-daemon` (later) | ~3 K C++ | Implements `IMediaPlayerService.Stub` | OHOS MediaKit adapter |
| Boot orchestrator | ~500 LOC | Start servicemanager, register Java services, lazy-launch daemons | None |

**Net code growth:** roughly +20 K LOC of native+Java service code, -~30 K LOC of removed shim/bypass/scaffolding. Total engine codebase shrinks net.

### 3.7 Tradeoffs

| Dimension | Current (Option 2 incomplete) | Binder-pivot (Option 3 trimmed) | Anbox (Option 3 full) |
|---|---|---|---|
| Resident memory | ~50 MB | ~150 MB active | ~700 MB-1 GB |
| Cold boot | <1 s | ~2-3 s | ~20-30 s |
| Per-app launch | ~10 s (33 DEX) | ~3 s (warm VM + lazy daemons) | ~5 s (zygote fork) |
| AOSP native libs supported | ~0 (we replace via JNI shims) | ~all (via real binder) | ~all |
| WebView support | No | Yes (Chromium subprocess via real binder) | Yes |
| Multi-process app support | No | Yes | Yes |
| Kernel changes required | None | binder.ko in OHOS kernel (Phase 2) | binder.ko + namespace + cgroup + selinux |
| Per-app shim work | Linear with app count | Approaches zero (services converge) | Zero |
| Architecture proximity to real Android | Far | Close | Identical |

### 3.8 Performance budgets

- **Service-call latency:** in-process Java-to-Java via `Stub.asInterface` elision = ~50 ns (one virtual call). Java-to-daemon via real Binder = ~6 µs round-trip. Acceptable for service calls (typical app makes <1000/sec). Hot paths (SurfaceFlinger frame submit) need shared-memory buffer transport, not Parcel — this is how AOSP itself works (gralloc handles, BufferQueue).
- **Frame budget:** 16.6 ms at 60 Hz. SurfaceFlinger compositor work happens in the daemon process — frame submit is one Binder transaction handing off a `GraphicBuffer` handle (kernel-duplicated fd, zero-copy data plane).
- **Memory pressure:** lazy-launching daemons keeps idle footprint at ~75 MB (dalvikvm + servicemanager + Java services). Audio/surface daemons launch on first use. Camera/media daemons only launch for apps that need them.

---

## 4. Validation Strategy

### 4.1 Phase 1 — Android sandbox (no OHOS dependency)

**Why:** the Pixel 7 Pro already has a working `binder.ko`, working `binderfs`, AOSP source for everything we need, and a real noice binary running natively as the comparison reference. Zero OHOS dependencies. Failure modes are debuggable with adb, logcat, strace, gdb, and side-by-side comparison.

**Mechanism:** Linux user namespace + a fresh `binderfs` mount give us an isolated binder context where our `servicemanager` lives and our daemons register, all running in unprivileged user space:

```
unshare -Uirmpf bash
mkdir -p /tmp/wlk-binderfs
mount -t binder binder /tmp/wlk-binderfs    # private binder context
export ANDROID_BINDER_DEVICE=/tmp/wlk-binderfs/binder
./westlake-servicemanager &
./westlake-audio-daemon &
./westlake-surface-daemon &
./dalvikvm com.github.ashutoshgngwr.noice/.MainActivity
```

Inside the namespace, our libbinder talks to our binderfs mount (not the phone's `/dev/binder`). The phone's real system_server, surfaceflinger, audioserver are invisible — we have a parallel universe.

**Acceptance:** noice renders main content + bottom nav, audio plays when a sound is tapped, no crashes for 10 minutes of interactive use, and logcat shape matches noice running natively (modulo our `WL`/`PF` log lines).

### 4.2 Phase 2 — OHOS port

**Once Phase 1 passes:** the engine binaries (libbinder, servicemanager, daemons, dalvikvm) recompile for OHOS musl + OHOS kernel headers. Daemon backends swap from Android-side (AAudio, SurfaceView pipe) to OHOS-side (AudioRenderer, XComponent). framework.jar drops in unchanged. The boundary between portable code and platform-specific code is exactly one `#ifdef OHOS_TARGET` block per daemon — typically ~500 lines.

**Acceptance:** noice running on an OHOS phone with identical UX to the Phase 1 result. Audio plays through OHOS AudioRenderer. UI composites through OHOS XComponent.

### 4.3 Phase 3+ (future, post-validation)

- WebView/Chromium subprocess support (need real binder for Chromium's renderer/GPU/network processes)
- Camera and media daemons
- 10-app benchmark suite (TikTok, Instagram, YouTube, Spotify, Facebook, Maps, Zoom, Grab, Duolingo, Uber)
- Persistent warm-VM model for fast app switching
- Multi-app concurrency (if needed)

---

## 5. Open Architectural Questions

### 5.1 Does binder.ko apply to LiteOS-A targets?

Binder is a Linux kernel module. **OHOS LiteOS-A is not Linux** — it's a different kernel for small/embedded devices. binder.ko does not apply there. If LiteOS-A targets are in scope, that's a separate architecture (likely "transpile to native ArkTS" — which has its own completeness problem). **Recommendation:** scope this pivot to standard-system OHOS (Linux-based) targets only.

### 5.2 BufferQueue ABI compatibility

AOSP's BufferQueue protocol (between client apps and SurfaceFlinger) changes occasionally between Android versions. Our surface-daemon needs to honor the same ABI as the AOSP framework version we ship. Pinning to one framework version simplifies; supporting multiple needs version-specific transaction tables.

### 5.3 Permission model

Real Binder propagates the caller's UID/PID. Many framework permission checks call `Binder.getCallingUid()`. In our setup the caller is always our own UID, so checks pass trivially — but if an app checks "is this caller MY UID or SYSTEM_UID?", the answer matters. **Resolution:** boot-time configure caller UID as `SYSTEM_UID` (1000) for service-side calls, app UID for app-side calls. Use `IPCThreadState` to thread it through.

### 5.4 ashmem / memfd / shared-memory regions

AOSP services pass large data (audio buffers, surface frames, asset chunks) via shared-memory regions, not Parcel marshaling. We need either:
- ashmem support (kernel module — bundled with binder in mainline)
- memfd-based shared memory (more portable)

**Recommendation:** target memfd; bundle a thin ashmem-shim that maps ashmem APIs to memfd if needed.

### 5.5 ServiceManager handle 0 setup

The kernel-binder driver assigns `binder context 0` to whoever opens `/dev/binder` first with a specific ioctl. In our sandbox setup, ordering matters: `servicemanager` must start before any client opens its binder context. **Recommendation:** the boot orchestrator (the script/binary that brings up the engine) hard-orders servicemanager first, then waits for it to be ready before launching anything else.

### 5.6 What about the existing AOSP-extracted framework jar?

We currently deploy ~5 MB of AOSP source as `framework-aosp.jar`. Most of this is layout/text/animation/drawable code (pure Java, doesn't talk to services). Some classes (e.g., `ActivityManager.java`, `WindowManager.java`) DO talk to services. Post-pivot, these become "framework classes that call into Binder, which finds our service impls." We should audit `framework-aosp.jar` post-pivot and shrink wherever AOSP's real source can replace our extracted subset.

### 5.7 What about WebView?

Chromium's WebView is fundamentally multi-process. Even with real binder, we need:
- Spawn Chromium's renderer subprocess (sandboxed)
- GPU subprocess (talks to surface daemon)
- Network subprocess (talks to system network)
- IPC between them via real binder

This is Phase 3 work, but the binder pivot is a prerequisite. Without real Binder, WebView is unreachable.

### 5.8 Parcel kHeader: which 4-byte magic on the wire?

Every kernel-binder transaction payload starts with a 4-byte `kHeader` that the peer's `Parcel::readInterfaceToken()` validates before dispatching. AOSP picks one of `'SYST'` / `'VNDR'` / `'RECO'` / `'UNKN'` depending on which `__ANDROID_*` macro is defined at compile time. Our libbinder lands in AOSP's "non-Android" branch (musl naturally; bionic via `-U__ANDROID__` so the source goes through the host code path — see `aosp-libbinder-port/Makefile` `BIONIC_DEFINES`), where AOSP defaults to `'UNKN'`. That value is rejected by both the OnePlus 6's `system_server` peers (expect `'SYST'`) and any stock OHOS-vendor binder endpoint (expects `'VNDR'`).

**Resolution:** `aosp-libbinder-port/patches/0003-parcel-kheader-syst.patch` makes the choice selectable at compile time via the Makefile's `KHEADER_FLAG`:

- `-DWESTLAKE_KHEADER_SYST=1` → `'SYST'` (today's default for both musl and bionic; Phase 1 sandbox interop)
- `-DWESTLAKE_KHEADER_VNDR=1` → `'VNDR'` (Phase 2 stock-OHOS-vendor interop)
- neither defined → `'UNKN'` (AOSP default, unmodified)

Westlake peers (our libbinder and our servicemanager) build with the same flag, so cross-process calls between them always work. Calls to a stock vendor binder endpoint require switching to the `VNDR` build at Phase 2. The flag is documented in `aosp-libbinder-port/README.md` and gated to one switch in the Makefile — flip and rebuild, no patch edit needed.

**CR11 follow-up (2026-05-12) — the send/receive asymmetry:** patch 0003 controls only the *send* side (what we write in `writeInterfaceToken`). The *receive* side (in `Parcel::enforceInterface`) historically used a strict `if (header != kHeader)` equality check that rejected anything peers wrote with a different magic. On 2026-05-12, after a OnePlus 6 reboot, the phone's `vndservicemanager` started writing `'VNDR'` on replies; our libbinder (baked `'SYST'`) rejected every transaction with `Expecting header 0x53595354 but found 0x564e4452. Mixing copies of libbinder?` and the discovery harness wedged on PHASE A.

`aosp-libbinder-port/patches/0004-parcel-accept-any-kheader-on-recv.patch` widens the receive-side comparison to a set-membership check over `{'SYST', 'VNDR', 'RECO', 'UNKN'}` — the four canonical AOSP magic values. The send side still writes `kHeader` so peers can identify us. The four-byte magic remains a useful self-identifying breadcrumb in logs and crash reports, but is no longer a gate. This mirrors AOSP's own behavior across partitions at the kernel-binder layer (the partition split is enforced higher up via SELinux, not via this magic).

**Symmetry table:**

| Operation | Where | Behavior | Rationale |
|---|---|---|---|
| Send | `writeInterfaceToken` (line ~1001) | Writes `kHeader` (today: `'SYST'` via `WESTLAKE_KHEADER_SYST=1`) | Peer can identify us in logs; survives unmodified through patch 0003. |
| Receive | `enforceInterface` (line ~1087) | Accepts `'SYST' \| 'VNDR' \| 'RECO' \| 'UNKN'` | Cross-partition / cross-vendor interop; patch 0004. |

If a future Phase 2 deployment needs to *also* limit which peers we'll accept (e.g., "only accept `'VNDR'` peers"), the right knob is a build-flag-selectable receive whitelist mirroring patch 0003, not a re-tightening of the equality check.

---

## 6. Anti-Patterns to Avoid

These are codified in `feedback_*.md` memory files and must be enforced by every agent and every PR:

### 6.1 Per-app hardcoded shortcuts (`feedback_no_per_app_hacks.md`)

No `if (className.equals("com.example.MyApp"))`, no `if (resourceId == 0x7f0b171c)`. Only generic Android API shims. The existing `MCD_*` constants in `FragmentTransactionImpl.java` and `FragmentManager.java` are violations and must be removed during cleanup.

### 6.2 Additive shimming

Don't observe an NPE, add a shim, move on. The right reflex on an NPE is: "what layer is missing? what would real Android provide here? where does that go in our architecture?" If the answer is "it goes in a service that lives behind Binder," add the service method (and verify it via Phase-1 sandbox), don't class-shim it.

### 6.3 Renderer-time bypasses

The renderer renders. It does not run lifecycles, it does not seed fields, it does not inflate fragments. `WestlakeFragmentLifecycle.runLifecycleAndConvert` is the canonical example of what NOT to do. The renderer's input is a fully-populated View tree from the framework; if that tree is empty, the bug is upstream.

### 6.4 Reflection as the answer

Avoid `Unsafe.allocateInstance` for framework objects, avoid `Method.setAccessible(true)`, avoid `Field.get/set` on framework internals. These are signs of bypassing a framework path we should be participating in correctly. (Reflection on app-provided extension points, e.g., manifest-declared activity classes, is fine — that's the framework's own pattern.)

### 6.5 Speculative completeness

Don't implement service methods we haven't observed being called. Discover via subtraction (the Phase-1 logging during noice run reveals exactly which Binder transactions hit each service). Implement only what's been observed. Speculative implementation accumulates dead code that drifts from AOSP semantics.

---

## 7. Decision Log

| Date | Decision | Reason | Reversibility |
|---|---|---|---|
| 2026-05-12 | Adopt Binder substitution boundary | User-confirmed; codex 2nd-opinion confirmed; pattern of additive shimming proven non-converging | Reversible — current shim path still in tree; pivot is additive at first |
| 2026-05-12 | Validate on Android phone first | OHOS kernel dependency too risky without architecture proven | n/a |
| 2026-05-12 | Accept OHOS kernel binder dependency | User: "prioritize running all APK without change over OHOS kernel changes" | OHOS-team decision required before Phase 2 ships |
| 2026-05-12 | Scope to standard-system OHOS (Linux); defer LiteOS-A | binder.ko is Linux-only | Reversible if LiteOS-A becomes priority — different architecture needed |
| 2026-05-12 | Delete `WestlakeFragmentLifecycle` post-pivot | Confirmed off-architecture by codex review | Reversible while not yet deleted; will become irreversible once references are removed |

---

## 8. Glossary

- **Binder**: Android's primary IPC mechanism. A kernel module providing single-copy synchronous-and-async RPC between processes, with object references, death notifications, and UID propagation.
- **binderfs**: Linux feature (≥5.0) that lets userspace create independent binder contexts. Used here to sandbox Westlake's binder universe from the host system's.
- **`/dev/binder`**: the character device the binder module exposes. Apps interact with it via ioctl + mmap.
- **`servicemanager`**: AOSP daemon that the kernel hands "context 0" to. The phone book for all named services.
- **`Stub.asInterface`**: AIDL-generated method that returns either the local Java implementation (same-process) or a marshaling proxy (cross-process). Critical optimization for our use case.
- **`localBinder()`**: AOSP libbinder equivalent on the native side, same purpose.
- **`IPCThreadState`**: AOSP libbinder per-thread state holding the current transaction's caller info.
- **gralloc**: Android's graphics buffer allocator. Hardware HAL; we bypass it by having surface-daemon allocate buffers via OHOS-equivalent mechanism (or memfd-based for portable testing).
- **AAudio / OpenSL ES**: Android audio APIs; AAudio is the modern low-latency one.
- **XComponent / OH_Drawing / OH AudioRenderer**: OHOS native APIs; targets of our daemon-internal bridge code.
