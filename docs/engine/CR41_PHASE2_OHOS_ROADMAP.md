# CR41 — Phase 2 OHOS Roadmap (M9 – M13 pre-scoping)

**Status:** scoping (read-only architect pre-flight for Phase 2)
**Author:** Architect agent (2026-05-13)
**Companion to:**
- `docs/engine/BINDER_PIVOT_MILESTONES.md` §M9-M13 (the canonical V1 entries this doc refines)
- `docs/engine/BINDER_PIVOT_DESIGN_V2.md` (V2 in-process Java substrate that survives Phase 1 → Phase 2)
- `docs/engine/M5_AUDIO_DAEMON_PLAN.md` §1.6 + §4.3 + §9.2 (AAudio → OHOS AudioRenderer backend swap design)
- `docs/engine/M6_SURFACE_DAEMON_PLAN.md` §1.7 + §4.3 + §9.2 (DLST pipe → OHOS XComponent backend swap design)
- `docs/engine/CR33_M6_SPIKE_REPORT.md` (memfd works on OnePlus 6 kernel 4.9; OHOS 5.10 inherits)
- `docs/engine/CR34_M5_SPIKE_REPORT.md` (AAudio backend probe; OHOS AudioRenderer is the M11 counterpart)
- `docs/engine/CR37_M5_AIDL_DISCOVERY.md` (transaction surface, exact; AIDL ABI does not drift between Phase 1 and Phase 2 — same dalvikvm, same framework.jar)
- `docs/engine/CR38_M7_M8_INTEGRATION_SCOPING.md` (Phase 1 final integration; the boot-sequence pattern M13 mirrors)

**Predecessor state assumed (Phase 1 finish line):**
- M1/M2/M3/M3++ complete (musl + bionic libbinder.so, servicemanager, dalvikvm wired)
- M4a/b/c/d/e/power complete (6 Java services + Tier-1 + fail-loud)
- M5 audio daemon complete with AAudio backend on cfb7c9e3
- M6 surface daemon complete with DLST-pipe backend on cfb7c9e3
- M7 noice e2e PASS, M8 McDonald's regression PASS
- V2 substrate (WestlakeActivity + WestlakeApplication + thin Resources + Window/PhoneWindow/DecorView/WindowManagerImpl stubs) carries over unchanged into Phase 2

**Anti-drift contract honored:** zero source-code edits, zero Westlake-shim changes, zero memory-file edits. All output in this NEW doc plus a small row in `PHASE_1_STATUS.md` §1.4 and small annotations on `BINDER_PIVOT_MILESTONES.md` §M9-M13 pointing here.

This doc is the canonical answer to "after Phase 1 PASS, how do we move the engine onto an OHOS standard-system phone?" — kernel changes, library swaps, boot sequence, person-day budgets, calendar with parallelism, and a top-to-bottom risk register.

---

## §1. Phase 1 → Phase 2 architecture diff (one diagram)

### 1.1 Substitution map (what swaps, what stays)

```
Phase 1 (Android cfb7c9e3, kernel 4.9)              Phase 2 (OHOS standard phone, kernel 5.10)
─────────────────────────────────────────────       ──────────────────────────────────────────

  +─────────────────────────────────+                 +─────────────────────────────────+
  │ dalvikvm process                │                 │ dalvikvm process                │
  │   bionic-static binary          │ ▒▒▒▒ swap ▒▒▒▒  │   musl-static binary            │
  │   -Xbootclasspath = framework.  │                 │   -Xbootclasspath = same        │
  │     jar + ext.jar + core-*.jar  │   IDENTICAL     │     (binary-identical reuse)    │
  │     + aosp-shim.dex (V2)        │ ◀──────────────▶│     + aosp-shim.dex (V2)        │
  │   App APK = noice.apk           │                 │   App APK = noice.apk           │
  │                                 │                 │                                 │
  │   V2 substrate (in-process):    │ ◀── unchanged ─▶│   V2 substrate (in-process):    │
  │     WestlakeActivity            │                 │     WestlakeActivity            │
  │     WestlakeApplication         │                 │     WestlakeApplication         │
  │     WestlakeContextImpl         │                 │     WestlakeContextImpl         │
  │     thin Resources/ArscParser   │                 │     thin Resources/ArscParser   │
  │     Window/PhoneWindow/DecorVw  │                 │     Window/PhoneWindow/DecorVw  │
  │     WindowManagerImpl           │                 │     WindowManagerImpl           │
  │                                 │                 │                                 │
  │   6 Java M4 services            │ ◀── unchanged ─▶│   6 Java M4 services            │
  │     (in-process,                │                 │     (in-process,                │
  │      local-elision routing)     │                 │      local-elision routing)     │
  │                                 │                 │                                 │
  │   libbinder.so                  │ ▒▒▒▒ swap ▒▒▒▒  │   libbinder.so                  │
  │     bionic ABI                  │                 │     musl ABI                    │
  │     /dev/vndbinder ioctl        │ ◀── same wire ─▶│     /dev/vndbinder ioctl        │
  │                                 │                 │                                 │
  +─────────────────────────────────+                 +─────────────────────────────────+
                │                                                       │
                │  binder syscalls                                      │  binder syscalls
                ▼                                                       ▼
  +─────────────────────────────────+                 +─────────────────────────────────+
  │ servicemanager (M2)             │ ▒▒▒▒ swap ▒▒▒▒  │ servicemanager (M2)             │
  │   bionic-static                 │                 │   musl-static                   │
  │   same C++ source, same logic   │ ◀── same wire ─▶│   same C++ source, same logic   │
  +─────────────────────────────────+                 +─────────────────────────────────+
  +─────────────────────────────────+                 +─────────────────────────────────+
  │ audio_daemon (M5)               │                 │ audio_daemon (M11)              │
  │   bionic-static                 │ ▒▒▒▒ swap ▒▒▒▒  │   musl-static                   │
  │   binder Bn surface ─────       │ ◀── unchanged ─▶│   binder Bn surface ─────       │
  │   AudioServiceImpl C++          │                 │   AudioServiceImpl C++          │
  │   ┌────────────────┐            │                 │   ┌────────────────┐            │
  │   │ AAudioBackend  │ ▒▒ swap ▒▒ │                 │   │ OhosBackend    │            │
  │   │ libaaudio.so   │            │ ◀── #ifdef ────▶│   │ libohaudio.so  │            │
  │   │ (push, blocking write)      │                 │   │ (pull, callback OnWriteData)│
  │   └───────┬────────┘            │                 │   └───────┬────────┘            │
  │           ▼                     │                 │           ▼                     │
  │   OnePlus 6 speaker             │                 │   OHOS phone speaker            │
  +─────────────────────────────────+                 +─────────────────────────────────+
  +─────────────────────────────────+                 +─────────────────────────────────+
  │ surface_daemon (M6)             │                 │ surface_daemon (M12)            │
  │   bionic-static                 │ ▒▒▒▒ swap ▒▒▒▒  │   musl-static                   │
  │   binder Bn surface ─────       │ ◀── unchanged ─▶│   binder Bn surface ─────       │
  │   SurfaceComposerImpl C++       │                 │   SurfaceComposerImpl C++       │
  │   memfd GraphicBuffer ─────     │ ◀── unchanged ─▶│   memfd GraphicBuffer ─────     │
  │   BufferQueueCore C++ ─────     │ ◀── unchanged ─▶│   BufferQueueCore C++ ─────     │
  │   ┌────────────────┐            │                 │   ┌────────────────┐            │
  │   │ DlstPipeBackend│ ▒▒ swap ▒▒ │                 │   │ XComponentBackend           │
  │   │ stdout DLST    │            │ ◀── #ifdef ────▶│   │ OH_NativeWindow│            │
  │   │ opcodes        │            │                 │   │ RequestBuffer/ │            │
  │   │                │            │                 │   │ FlushBuffer    │            │
  │   └───────┬────────┘            │                 │   └───────┬────────┘            │
  │           ▼                     │                 │           ▼                     │
  │   com.westlake.host APK         │                 │   OHOS XComponent in HAP        │
  │   Compose SurfaceView           │                 │   ArkUI ETS surface             │
  +─────────────────────────────────+                 +─────────────────────────────────+
  +─────────────────────────────────+                 +─────────────────────────────────+
  │ Host APK (Android)              │ ▒▒▒▒ swap ▒▒▒▒  │ Host HAP (OHOS)                 │
  │   Kotlin + Compose              │                 │   ArkUI ETS + XComponent        │
  │   SurfaceView consumer of DLST  │                 │   Direct surface consumer       │
  │   subprocess orchestrator       │                 │   subprocess orchestrator       │
  │   (forks daemons + dalvikvm)    │                 │   (forks daemons + dalvikvm)    │
  +─────────────────────────────────+                 +─────────────────────────────────+
```

### 1.2 What stays binary-identical across the Phase 1 → Phase 2 boundary

This is the single most important architectural finding for Phase 2 budgeting:

**Stays unchanged (no code edit, no rebuild required beyond a different sysroot link):**
- All Java code: framework.jar, ext.jar, core-*.jar, **aosp-shim.dex (V2 substrate)**, **the 6 M4 Java service classes** — every byte of the Java boot artifacts is dalvikvm-loaded and dalvikvm itself is the ABI boundary; OHOS-vs-Android is invisible at this layer.
- C++ source code for: libbinder, servicemanager, AudioServiceImpl, AudioTrackImpl, BufferQueueCore, GraphicBufferProducerImpl, SurfaceComposerImpl, GraphicBuffer-memfd, BitTube, VsyncThread, LayerState. **Same .cpp files, different sysroot, different output triple.**
- The binder wire protocol: kernel binder driver (OHOS kernel 5.10 ships `drivers/android/binder.c` verbatim; see §2.2.1 below — already CONFIG_ANDROID_BINDER_IPC=y in every standard board config). `/dev/vndbinder` ioctls round-trip identically.
- memfd-backed shared memory: OHOS standard kernel has `CONFIG_MEMFD_CREATE=y` (verified — see §2.2.1). The GraphicBuffer-memfd / audio cblk / IMemory paths all work without source change.
- AIDL transaction codes for IAudioFlinger, IAudioTrack, ISurfaceComposer, IGraphicBufferProducer, etc. — driven by the framework.jar version we ship, which is identical to Phase 1.

**Swaps (the only actual Phase 2 work):**
1. **Kernel config:** turn binder on in the OHOS kernel build target (likely already on in standard-system boards; verify and ship a one-line config patch if needed). → M9
2. **Build ABI:** rebuild every native artifact against `--target=aarch64-linux-ohos` instead of `--target=aarch64-linux-android24`. → M10 (libbinder.so + servicemanager) and §3/§4 below for daemons.
3. **Audio backend `.cpp`:** activate the already-scaffolded `OhosBackend.cpp` (M5 plan §4.3) — ~250-350 LOC of OH_AudioRenderer adapter. → M11
4. **Surface backend `.cpp`:** activate the already-scaffolded `XComponentBackend.cpp` (M6 plan §4.3) — ~300-400 LOC of OH_NativeWindow adapter. → M12
5. **Host APK → Host HAP:** rewrite the orchestrator wrapper (a few hundred LOC of Kotlin/Compose) as an ArkUI ETS HAP. → M13

**The substitution surface of Phase 2 is intentionally a thin border around the Phase-1 deliverables.** Approximately **80% of Phase 1's source code is reused unchanged**, and the remaining 20% is platform-bridge code that the M5 and M6 plans already pre-scoped behind clean `#ifdef`-selectable interfaces.

---

## §2. M9 — `binder.ko` for OHOS standard-system kernel

### 2.1 Inputs / preconditions

- Phase 1 complete (M7 + M8 green).
- An OHOS standard-system phone or developer board available for testing. **No such hardware in lab today (2026-05-13).** Sourcing risk is the #1 Phase 2 blocker — see §8.
- Access to the OHOS kernel build (this repo, `kernel/linux/linux-5.10/` + `kernel/linux/config/linux-5.10/`).
- OHOS kernel-module signing keys (only required if target board enforces module signing; most rk3568/hispark dev boards do not).

### 2.2 Approach

#### 2.2.1 Key finding: OHOS standard system kernel **already has binder enabled**

This was the central uncertainty going into Phase 2 pre-scoping. Verification is direct:

```
$ grep ANDROID_BINDER /home/dspfac/openharmony/kernel/linux/config/linux-5.10/type/standard_defconfig
CONFIG_ANDROID_BINDER_IPC=y
CONFIG_ANDROID_BINDER_DEVICES="binder,hwbinder,vndbinder"

$ grep ANDROID_BINDER /home/dspfac/openharmony/kernel/linux/config/linux-5.10/arch/arm64/configs/rk3568_standard_defconfig
CONFIG_ANDROID_BINDER_IPC=y
# CONFIG_ANDROID_BINDERFS is not set
CONFIG_ANDROID_BINDER_DEVICES="binder,hwbinder,vndbinder"
# CONFIG_ANDROID_BINDER_IPC_SELFTEST is not set

$ grep MEMFD /home/dspfac/openharmony/kernel/linux/config/linux-5.10/type/standard_defconfig
CONFIG_MEMFD_CREATE=y

$ grep ASHMEM /home/dspfac/openharmony/kernel/linux/config/linux-5.10/type/standard_defconfig
CONFIG_ASHMEM=y
```

Boards confirmed to inherit this: **rk3568, hispark_taurus_standard, hispark_phoenix_standard, unionpi_tiger, myd_imx8mm, qemu-arm-linux_standard, qemu-x86_64-linux_standard, qemu-riscv64-linux_standard** (per `find kernel/linux/config/linux-5.10 -name "*defconfig" -exec grep -l "ANDROID_BINDER_IPC=y" {} \;`).

So:
- Every OHOS standard-system board ships kernel-built-in `/dev/binder`, `/dev/hwbinder`, `/dev/vndbinder`.
- `BINDERFS` (the post-Android-Q dynamic binder device mechanism) is **NOT** enabled — but this is irrelevant for Phase 2 because Phase 1 also runs against statically-registered binder devices (the OnePlus 6 kernel 4.9 has the same legacy layout, and `aosp-libbinder-port/Makefile`'s `WESTLAKE_KHEADER_FLAG` is already tuned to that mode).
- `MEMFD_CREATE=y` is set, so the GraphicBuffer-memfd path from M6 works unchanged.
- `ASHMEM=y` is set, so a fallback path for IMemory exists if needed (Phase 1 currently uses memfd via `ashmem-host.cpp`'s memfd-backed shim; both forks work).

#### 2.2.2 What M9 actually does

The naive expectation in `BINDER_PIVOT_MILESTONES.md` §M9 ("build binder.ko, insmod, verify /dev/binder appears") **is incorrect for OHOS standard-system boards.** Binder is compiled in, not a module — there is no `binder.ko` to insmod. The actual M9 task is:

1. **Verify** on the target OHOS device: `ls -l /dev/binder /dev/vndbinder /dev/hwbinder` succeeds out-of-the-box.
2. **If they don't appear:** check the device's actual kernel config (`/proc/config.gz` if available; otherwise the board's `defconfig` in the OHOS source tree). For boards where binder is somehow turned off, ship a one-line config patch to `kernel/linux/config/linux-5.10/<board>/arch/<arch>_defconfig` adding `CONFIG_ANDROID_BINDER_IPC=y`.
3. **If the device enforces SELinux** (some OHOS productized phones may): write a permissive SELinux rule allowing the orchestrator's uid to open `/dev/vndbinder`. This mirrors the Android adb-shell→data-local-tmp pattern Phase 1 uses.
4. **Pin a known-good kernel commit** in the OHOS source tree to be the M9 reference kernel (e.g. tag `OpenHarmony-v4.1-Release` or whichever ships with the target device). Build it, flash it, document the SHA.

#### 2.2.3 What's still possible to go wrong

- **OHOS productized phones may strip binder** to reduce attack surface. Developer boards (rk3568, hispark) keep it on per the config evidence above; production phones (Huawei retail, etc.) cannot be confidently predicted without a target device in hand.
- **The orchestrator's uid may not have access to /dev/vndbinder.** Standard-system OHOS uses MAC + DAC similar to AOSP; if the binder device's mode is `0600 root:root`, only root can open it. Mitigation: run the test bench as the system uid (developer mode), or use rooting tools if available, or ship a service-side wrapper.
- **`CONFIG_ANDROID_BINDERFS` is OFF** in every standard-system config inspected. This means we *cannot* dynamically create per-test binder contexts via `mount -t binder` inside a userns sandbox — same as the OnePlus 6 Phase 1 path. We use the static `/dev/vndbinder` directly. Phase 1 already runs this way; no surprise.

### 2.3 Effort estimate

| Sub-task | Person-days |
|---|---|
| Hardware acquisition + initial OHOS standard-system bringup (NOT counted; gating prerequisite) | (TBD; see §8 R1) |
| `ls /dev/binder` verification + capability check on target device | 0.25 |
| (If needed) one-line `CONFIG_ANDROID_BINDER_IPC=y` patch to board defconfig + rebuild + flash | 0.5 |
| (If needed) SELinux permissive label for the orchestrator's binder device access | 0.5 |
| (If needed) Kernel module re-signing (if board enforces) | 0.5 |
| Pin kernel commit + document procedure | 0.25 |
| **Total** | **0.5 - 2.0 person-days** (best case: 0.5 day if dev board "just works"; worst case: 2 days if config patch + SELinux + sign needed) |

V1 milestones-doc estimated 3-5 days (with OHOS team) or 2-3 weeks (without). **Phase 2 pre-scoping lowers this dramatically** because the kernel-side enablement is already done by the OHOS team — the binder driver is upstream-in-OHOS, every standard-system board defconfig enables it. M9 is no longer "port binder.ko to a new kernel"; it's "verify binder device nodes exist and grant our orchestrator access."

### 2.4 Risks

| # | Risk | Likelihood | Impact | Mitigation |
|---|---|---|---|---|
| M9-R1 | Target device's actual shipping kernel has binder turned off (only the source defconfig has it on) | Low-Med | High | Inspect `/proc/config.gz` on device; ship one-line config patch + custom kernel build. Adds 1-2 days. |
| M9-R2 | SELinux blocks `/dev/vndbinder` open from non-root uid | Med | High | Standard-system OHOS does have SELinux. Either run as system uid (developer mode), or write a permissive rule, or factory-unlock + remount /system rw and patch the rule. Adds 0.5-1 day. |
| M9-R3 | OHOS productized phone (retail) refuses to flash custom kernel | Med | Critical | Use a dev board (rk3568 / hispark) for M9-M13; defer retail phone to Phase 3. Hardware acquisition risk shifts from "phone" to "dev board" — much easier. See §8 R1. |
| M9-R4 | Binder driver in OHOS 5.10 has accumulated OHOS-specific deltas | Low | Med | Spot-check: `drivers/android/binder.c` exists in OHOS kernel source tree (verified). Diff against upstream 5.10 mainline is small (per `kernel/linux/patches/linux-5.10/`). No Phase-2-blocking deltas expected. Verify with 1-hour code-review during M9 setup. |

### 2.5 Code skeleton (not applicable)

M9 is configuration + verification, not new source code. No skeleton.

---

## §3. M10 — libbinder + servicemanager rebuilt for OHOS sysroot

### 3.1 Inputs / preconditions

- M9 PASS (binder device nodes accessible on OHOS target).
- `aosp-libbinder-port/Makefile` already has a working musl/OHOS variant. The file says (lines 1-10): *"musl (default targets):  cross-compile against OHOS musl + libcxx"*. The build emits to `aosp-libbinder-port/out/musl/`.
- OHOS NDK sysroot at `/home/dspfac/openharmony/out/sdk/sdk-native/os-irrelevant/sysroot/usr/lib/aarch64-linux-ohos/`. Already populated with `libc.so`, `libc++.so`, etc.

### 3.2 Approach

**Phase 2's libbinder is already built.** This is the single biggest under-estimate in `BINDER_PIVOT_MILESTONES.md` §M10 ("rebuild M1+M2 artifacts against OHOS sysroot"). The Makefile already targets `aarch64-linux-ohos`; the artifacts already exist (`out/musl/libbinder.so` + `out/musl/servicemanager`). What M10 actually needs is on-device verification + any libc++ namespace fixes.

Concrete M10 work:

1. **Confirm build artifacts:** `make -C aosp-libbinder-port all-musl` produces `out/musl/libbinder.so` and `out/musl/servicemanager`. If not done by Phase 1, add it now (zero source-change; the targets are wired).
2. **Push to OHOS target:** `hdc file send out/musl/libbinder.so /data/local/tmp/westlake/lib/` (the OHOS equivalent of `adb push`; `hdc` is OHOS's debug bridge).
3. **Smoke test:** run `binder_smoke` (from M1 acceptance) and `servicemanager_smoke` (from M2 acceptance) on the OHOS phone.
4. **Possible libc++ ABI fix:** the M5-Step2 report (CR37 + PHASE_1_STATUS row 151) documented that AOSP's NDK r25 builds use `std::__ndk1::` while OHOS platform libs may use `std::__1::` — same class of fix applied at the libbinder layer for our bionic build, may need re-tuning for OHOS musl build. The Makefile flag `-D_LIBCPP_ABI_NAMESPACE=__1` already exists (verified in Phase 1 work). Tune ABI namespace as needed during M10 bringup.
5. **OHOS init/service-startup integration (optional, for daemons-as-services later):** if we want servicemanager to start at OHOS system boot, write an OHOS `.cfg` init service file under `/system/etc/init/westlake-servicemanager.cfg`. Phase 2 M13 doesn't require this — the orchestrator HAP can fork-and-exec servicemanager directly, same as Phase 1 host APK does. **Defer init-service integration to Phase 3.**

### 3.3 Effort estimate

| Sub-task | Person-days |
|---|---|
| Verify `make all-musl` produces clean libbinder + servicemanager | 0.25 |
| Push to OHOS device + smoke test (binder_smoke, servicemanager smoke) | 0.25 |
| (Likely) libc++ `__1` vs `__ndk1` ABI namespace tuning (the M5-Step3 class of fix) | 0.5 |
| (Possible) OHOS-specific patches: HiLog stderr fallback, hdc-vs-adb log redirect, OHOS path constants | 0.5 |
| Document the OHOS bringup procedure | 0.25 |
| **Total** | **1.0 - 2.0 person-days** |

V1 estimated 1-2 days. **Confirmed and held.** The pre-existing musl target in the Phase 1 Makefile is the architectural win that keeps M10 at this size.

### 3.4 Risks

| # | Risk | Likelihood | Impact | Mitigation |
|---|---|---|---|---|
| M10-R1 | OHOS musl libc has divergences from the version `aosp-libbinder-port`'s Makefile assumes | Med | Med | OHOS uses `musl-1.2.0-fork` (per `third_party/musl/`); the Makefile uses the same. Spot-check declared functions; submit small patch if a function is missing. ~0.5 day if needed. |
| M10-R2 | OHOS libc++ inline-namespace ambiguity with platform libs we'll `dlopen` later (libohaudio.so, libnative_window.so) | High | Med | Already the resolution pattern from M5-Step2 (`-D_LIBCPP_ABI_NAMESPACE=__1`). Need to inspect what namespace OHOS platform libs use. Likely `__1` (mainline LLVM default); but OHOS NDK r10+ may have switched. Verify at M10 start; 0.5 day to flip the flag and rebuild. |
| M10-R3 | servicemanager assumes Android-style `/dev/binderfs/binder` path that OHOS doesn't have | Low | Med | servicemanager opens `/dev/vndbinder` (already configured per `aosp-libbinder-port/Makefile` `WESTLAKE_VNDR_BINDER=1` flag). OHOS has `/dev/vndbinder`. No issue. |
| M10-R4 | OHOS device has `SECCOMP` filters that block `ioctl(BINDER_*)` from non-system uids | Low | High | Same class as M9-R2 (run as system uid or write seccomp exception). Test by simply running `binder_smoke` and checking error code; if EPERM, escalate uid. ~0.5 day. |

### 3.5 Code skeleton (not applicable)

M10 reuses Phase 1's source code verbatim. New code is at most ~50 LOC of OHOS-specific HiLog stderr fallback (a fork of the existing `OS_non_android_linux.cpp` pattern) and ~10 LOC of OHOS path constants.

---

## §4. M11 — Audio daemon to OHOS AudioRenderer

### 4.1 Inputs / preconditions

- M10 PASS (musl libbinder + servicemanager run on OHOS phone).
- M5 Phase 1 PASS (audio_daemon with AAudioBackend works on cfb7c9e3).
- Pre-scoped extensively in `M5_AUDIO_DAEMON_PLAN.md` §4.3, §9.2.
- OHOS NDK ships `libohaudio.so` at `out/sdk/sdk-native/os-irrelevant/sysroot/usr/lib/aarch64-linux-ohos/libohaudio.so` (verified).
- API header at `/home/dspfac/openharmony/interface/sdk_c/multimedia/audio_framework/audio_renderer/native_audiorenderer.h` (verified).

### 4.2 Approach

The M5 plan §4.1 specified the AudioBackend interface to abstract this swap. M11 activates the already-scaffolded `OhosBackend.{h,cpp}` files.

#### 4.2.1 Push vs pull asymmetry (THE biggest architectural seam in Phase 2)

| | Phase 1 — AAudio | Phase 2 — OHOS AudioRenderer |
|---|---|---|
| Model | push (blocking write) OR pull (callback) — Phase 1 picked push | **pull only** (callback `OH_AudioRenderer_OnWriteData`) |
| Daemon-side flow | Per-track reader thread loops `audio_utils/fifo` → `AAudioStream_write` | OHOS callback fires on AudioRenderer's internal worker thread, asks daemon for N frames; daemon's `OnWriteData` impl drains `audio_utils/fifo` synchronously |
| Threading | Daemon owns the pumping thread | OHOS framework owns the pumping thread; daemon serves on demand |

M5 plan §4.3 anticipated this: *"OHOS AudioRenderer is **pull-based** (callback-driven), AAudio is **push-or-pull** depending on mode. Phase 1's AAudioBackend uses push (blocking write); M11's OhosBackend will use the OHOS callback model — that requires a different relationship to the ring buffer (callback drains it, not our thread pushing). This is the **single biggest architectural seam** between Phase 1 and Phase 2 audio paths."*

The `AudioBackend` interface (M5 plan §4.1) hides this behind opaque `write()` semantics. **Concretely:**

- AAudioBackend's `write()`: synchronously calls `AAudioStream_write` (the AAudio framework's blocking-write API). Returns the number of frames consumed.
- OhosBackend's `write()`: enqueues the bytes into an in-OhosBackend SPSC ring buffer (~4 KB), returns immediately. The OnWriteData callback (registered at `openOutput` time) drains the ring buffer on the OHOS framework's worker thread.

`AudioTrackImpl.cpp` does not change. Its per-track-reader-thread still calls `backend->write()`; the backend's choice of pump direction is invisible. Phase 1 ships pre-built — see M5 plan §3.3 for the OhosBackend.cpp stub file already in the plan's layout.

#### 4.2.2 Specific OHOS AudioRenderer surface used

From `native_audiostreambuilder.h` + `native_audiorenderer.h` + `native_audiostream_base.h` (verified in NDK headers):

```cpp
// Open a renderer
OH_AudioStreamBuilder *builder = nullptr;
OH_AudioStreamBuilder_Create(&builder, AUDIOSTREAM_TYPE_RENDERER);
OH_AudioStreamBuilder_SetSamplingRate(builder, 48000);
OH_AudioStreamBuilder_SetChannelCount(builder, 2);
OH_AudioStreamBuilder_SetSampleFormat(builder, AUDIOSTREAM_SAMPLE_F32LE);
OH_AudioRenderer_Callbacks cbs{};
cbs.OH_AudioRenderer_OnWriteData = [](OH_AudioRenderer *r, void *user, void *buf, int32_t bufLen) -> int32_t {
    auto *self = static_cast<OhosBackend::Stream*>(user);
    return self->drainTo(buf, bufLen);  // drain ring buffer
};
OH_AudioStreamBuilder_SetRendererCallback(builder, cbs, self);
OH_AudioRenderer *renderer = nullptr;
OH_AudioStreamBuilder_GenerateRenderer(builder, &renderer);
OH_AudioStreamBuilder_Destroy(builder);

// Lifecycle
OH_AudioRenderer_Start(renderer);
OH_AudioRenderer_Pause(renderer);
OH_AudioRenderer_Stop(renderer);
OH_AudioRenderer_Flush(renderer);
OH_AudioRenderer_Release(renderer);
```

Estimated ~350 LOC of `OhosBackend.cpp`, mostly the SPSC ring buffer + OnWriteData callback + lifecycle wrappers + dlopen of `libohaudio.so` (defensive — keeps the daemon binary load-time-portable across OHOS NDK versions).

#### 4.2.3 What changes elsewhere

- `aosp-audio-daemon-port/Makefile`: add `audio-daemon-musl` target with `WESTLAKE_OHOS_TARGET=1` define. Target ABI: `aarch64-linux-ohos`. Links to NDK `libohaudio.so`.
- `aosp-audio-daemon-port/src/AudioBackend.cpp` factory: `#if defined(WESTLAKE_OHOS_TARGET)` branch instantiates `OhosBackend`.
- `aosp-audio-daemon-port/src/AudioTrackImpl.cpp`: **no changes** (backend agnostic).
- Phase 1 AAudioBackend.cpp stays compilable (so a single source tree builds both ABIs); it's just not linked into the OHOS variant.

### 4.3 Effort estimate

| Sub-task | Person-days |
|---|---|
| OHOS sysroot cross-build path for audio-daemon (audio-daemon-musl target in Makefile) | 0.5 |
| `OhosBackend.cpp` — pull-model adapter for OH_AudioRenderer (~350 LOC; SPSC ring + callback + lifecycle) | 1.0 |
| `OhosBackend.h` (~50 LOC) | 0.1 |
| Pull-model vs push-model reader-thread review: confirm AudioTrackImpl.cpp doesn't need refactor (M5 plan §4.3 says no; verify) | 0.25 |
| Standalone `audio_smoke` (M5 §7.1) replayed on OHOS — 440 Hz tone audible | 0.5 |
| Binder-backed `audio_binder_smoke` (M5 §7.2) replayed on OHOS | 0.25 |
| Polish + regression entry + doc | 0.25 |
| **Total** | **2.5 - 3.0 person-days** |

V1 estimated 2-3 days. **Confirmed.** M5 plan §9.2 estimated 2.5 person-days; CR41 holds within that band.

### 4.4 Risks

| # | Risk | Likelihood | Impact | Mitigation |
|---|---|---|---|---|
| M11-R1 | OH AudioRenderer's `OnWriteData` callback timeout is shorter than our ring buffer's drain latency, causing underruns | Med | Med | Pre-prime the ring buffer with ~16 KB at `openOutput` so first callback always returns full data. Size ring per OHOS callback period (queried via `OH_AudioRenderer_GetFrameSizeInCallback`). |
| M11-R2 | OHOS audio framework requires permission `ohos.permission.MICROPHONE` for any stream open (including playback-only) | Low | Med | Phase 2 host HAP declares the playback-only permission (`ohos.permission.PLAYBACK_AUDIO` if it exists; otherwise no permission needed for output streams). 1 hour during HAP authoring. |
| M11-R3 | `libohaudio.so` has libc++ namespace inline-namespace conflict with our libbinder.so musl build (M5-Step2 saw this on Android with `__1` vs `__ndk1`) | Med | High | Same playbook as M10-R2. The Makefile flag is the lever. Expect to spend 0.5 day during M11 first-build to nail down the namespace alignment for OHOS. |
| M11-R4 | OH AudioRenderer's stream-type whitelist (MUSIC, RING, VOICE_CALL, ...) rejects unspecified types | Low | Low | Set `OH_AudioStreamBuilder_SetRendererInfo` with `AUDIOSTREAM_USAGE_MUSIC` at open time. Trivial. |
| M11-R5 | Phase 1's audio_smoke output (440 Hz sine) sounds different on OHOS device (sample format autoconversion glitch) | Low | Low | Use `AUDIOSTREAM_SAMPLE_F32LE` (float native) to bypass any format conversion. Same as Phase 1 AAudio path. |

### 4.5 Code skeleton

```cpp
// aosp-audio-daemon-port/src/OhosBackend.h (~50 LOC)
#pragma once
#include "AudioBackend.h"
#include <cstdint>

namespace westlake {

class OhosBackend : public AudioBackend {
public:
    OhosBackend();
    ~OhosBackend() override;

    bool probe() override;
    status_t openOutput(uint32_t sampleRate, int channelCount,
                        audio_format_t format, audio_output_flags_t flags,
                        BackendStream **outStream, uint32_t *latencyMs) override;
    status_t closeOutput(BackendStream *stream) override;
    ssize_t  write(BackendStream *stream, const void *data, size_t bytes) override;
    status_t startStream(BackendStream *stream) override;
    status_t stopStream(BackendStream *stream) override;
    status_t pauseStream(BackendStream *stream) override;
    status_t flushStream(BackendStream *stream) override;
    status_t getRenderPosition(BackendStream *stream,
                               uint64_t *framesRendered,
                               int64_t *timeNs) override;

private:
    void *mLibOhAudio = nullptr;  // dlopen handle for libohaudio.so
    // Function pointers loaded lazily — see OhosBackend.cpp::probe()
    int32_t (*pfn_OH_AudioStreamBuilder_Create)(void**, int32_t);
    int32_t (*pfn_OH_AudioStreamBuilder_Destroy)(void*);
    int32_t (*pfn_OH_AudioStreamBuilder_SetSamplingRate)(void*, int32_t);
    int32_t (*pfn_OH_AudioStreamBuilder_SetChannelCount)(void*, int32_t);
    int32_t (*pfn_OH_AudioStreamBuilder_SetSampleFormat)(void*, int32_t);
    int32_t (*pfn_OH_AudioStreamBuilder_SetRendererCallback)(void*, void*, void*);
    int32_t (*pfn_OH_AudioStreamBuilder_GenerateRenderer)(void*, void**);
    int32_t (*pfn_OH_AudioRenderer_Start)(void*);
    int32_t (*pfn_OH_AudioRenderer_Pause)(void*);
    int32_t (*pfn_OH_AudioRenderer_Stop)(void*);
    int32_t (*pfn_OH_AudioRenderer_Flush)(void*);
    int32_t (*pfn_OH_AudioRenderer_Release)(void*);

    struct Stream;  // forward; impl-private
};

}  // namespace westlake
```

(Full `OhosBackend.cpp` body lives at ~350 LOC; the M5 plan §3.3 already reserved its slot in the directory layout. CR41 does not write the implementation — that's M11's job.)

---

## §5. M12 — Surface daemon to OHOS XComponent

### 5.1 Inputs / preconditions

- M10 PASS (musl libbinder + servicemanager run on OHOS phone).
- M6 Phase 1 PASS (surface_daemon with DlstPipeBackend renders noice MainActivity on OnePlus 6 via Compose host SurfaceView).
- Pre-scoped extensively in `M6_SURFACE_DAEMON_PLAN.md` §4.3, §9.2.
- OHOS NDK ships:
  - `libnative_window.so` (per OH NativeWindow header `interface/sdk_c/graphic/graphic_2d/native_window/external_window.h`)
  - XComponent API in `interface/sdk_c/arkui/ace_engine/native/native_interface_xcomponent.h`
  - Both verified to expose `OH_NativeWindow_NativeWindowRequestBuffer`, `OH_NativeWindow_NativeWindowFlushBuffer`, `OH_NativeXComponent_RegisterCallback`, `OH_NativeXComponent_GetNativeWindow` (existence confirmed by grep).

### 5.2 Approach

The M6 plan §4.3 specified the SurfaceBackend interface for this swap. M12 activates the already-scaffolded `XComponentBackend.{h,cpp}`.

#### 5.2.1 OHOS XComponent surface acquisition (the orchestration choreography)

XComponent is an OHOS-side **UI component** declared in ArkUI ETS that hands the C++ side a `OHNativeWindow*`. The flow:

1. Host HAP's ArkUI ETS layout includes `XComponent` with a known id (e.g. `"westlake-surface"`):
   ```ets
   build() {
     XComponent({
       id: 'westlake-surface',
       type: XComponentType.SURFACE,
       libraryname: 'westlake_host_native'   // → libwestlake_host_native.so
     })
   }
   ```
2. ArkUI calls `OH_NativeXComponent_RegisterCallback` into `libwestlake_host_native.so`'s registered callback. The callback receives an `OH_NativeXComponent*` whose `OH_NativeXComponent_GetNativeWindow` returns an `OHNativeWindow*`.
3. Host HAP's native side passes the `OHNativeWindow*` (or its underlying SurfaceId, an integer) to the surface_daemon over a Unix socket / shared file / env var.
4. surface_daemon's `XComponentBackend::probe()` adopts the SurfaceId / NativeWindow pointer.
5. On each `presentBuffer()`:
   ```cpp
   OHNativeWindowBuffer *buffer = nullptr;
   int fenceFd = -1;
   OH_NativeWindow_NativeWindowRequestBuffer(window, &buffer, &fenceFd);
   // Map buffer's underlying memory (vendor-allocated by OHOS gfx stack)
   // Copy our memfd GraphicBuffer's pixels into the OHOS buffer.
   OH_NativeWindow_NativeWindowFlushBuffer(window, buffer, fenceFd, region);
   ```

Phase 1's DLST pipe goes away in M12 — the daemon now writes pixels directly to OHOS's gfx-stack buffer. **This is cleaner and faster than Phase 1**: a direct blit instead of a pipe-encode + opcode-decode + SurfaceView blit.

#### 5.2.2 Memfd → OHOS native buffer copy

This is the per-frame work:
- Source: our memfd-backed `GraphicBuffer` (already mmap'd in surface_daemon process from the BufferQueueProducer queue).
- Destination: `OH_NativeWindow_NativeWindowRequestBuffer`-acquired `OHNativeWindowBuffer`'s underlying memory.
- Format conversion: probably none (both sides are RGBA_8888 by default; verify via `OH_NativeWindow_NativeWindowHandleOpt(window, GET_FORMAT, &fmt)`).
- Memcpy of 1080×2280×4 = ~9.5 MB per frame at 60 FPS = ~570 MB/s — well within DDR4 bandwidth. **CPU-side memcpy is acceptable for M12 acceptance**; future Phase 3 optimization can adopt DMA-buf passing via `OH_NativeWindow_NativeWindowSetMetaData` if perf becomes a concern.

Estimated ~400 LOC of `XComponentBackend.cpp`, including the SurfaceId handshake protocol with the host HAP.

#### 5.2.3 What changes elsewhere

- `aosp-surface-daemon-port/Makefile`: add `surface-daemon-musl` target with `WESTLAKE_OHOS_TARGET=1`. Target ABI: `aarch64-linux-ohos`. Links to NDK `libnative_window.so` + `libace_ndk.z.so`.
- `aosp-surface-daemon-port/src/SurfaceBackend.cpp` factory: `#if defined(WESTLAKE_OHOS_TARGET)` branch instantiates `XComponentBackend`.
- `aosp-surface-daemon-port/src/SurfaceComposerImpl.cpp`: **no changes** (backend agnostic).
- Phase 1's DlstPipeBackend.cpp stays compilable; not linked into OHOS variant.
- New ArkUI ETS layout in the host HAP (M13 work; ~30 LOC of `pages/Index.ets`).

#### 5.2.4 Vsync alignment

Phase 1 used a software 60Hz tick. On OHOS, XComponent can notify on real display vsync via `OH_NativeXComponent_RegisterOnFrameCallback` (if present in the NDK) or via the `OnFrameUpdate` callback on the XComponent component. surface_daemon's VsyncThread can either keep its software tick (simpler) or wire to XComponent's real-vsync callback (cleaner; defer to Phase 3 polish).

### 5.3 Effort estimate

| Sub-task | Person-days |
|---|---|
| OHOS sysroot cross-build path for surface-daemon (surface-daemon-musl target in Makefile) | 0.5 |
| Host HAP authoring: ArkUI ETS XComponent + libwestlake_host_native.so callback registration | 0.75 |
| SurfaceId handshake protocol: host HAP → surface_daemon over Unix socket / file / env var | 0.5 |
| `XComponentBackend.cpp` (~400 LOC; OH_NativeWindow Request/Flush + memfd→native-buffer memcpy) | 1.25 |
| `XComponentBackend.h` (~50 LOC) | 0.1 |
| Standalone `surface_smoke` (M6 §7.1) replayed on OHOS — gradient frames visible | 0.5 |
| Binder-backed `surface_binder_smoke` (M6 §7.2) replayed on OHOS | 0.5 |
| Polish + regression entry + doc | 0.4 |
| **Total** | **4.0 - 4.5 person-days** |

V1 estimated 4-5 days. **Confirmed.** M6 plan §9.2 estimated 4 person-days; CR41 holds within that band, with a slight tilt-up if the SurfaceId handshake protocol is more elaborate than expected.

### 5.4 Risks

| # | Risk | Likelihood | Impact | Mitigation |
|---|---|---|---|---|
| M12-R1 | OH NativeWindow buffer format isn't RGBA_8888 by default — requires conversion | Med | Med | Query format via `OH_NativeWindow_NativeWindowHandleOpt(GET_FORMAT)` and implement on-the-fly conversion. Most likely formats are RGBA_8888 (default) or RGB_565 (low-power). Conversion is ~50 LOC, ~0.25 day if encountered. |
| M12-R2 | OH NativeWindow buffer is GPU-mapped, not CPU-writable; our memcpy stalls or fails | Low-Med | High | Some OHOS gfx implementations use `dma-buf` heaps that require `OH_NativeWindow_NativeWindowHandleOpt(SET_USAGE, USAGE_CPU_WRITE_OFTEN)`. Set this at handshake time. If still GPU-only: pivot to memfd-to-dma-buf import via `OH_NativeWindow_CreateNativeWindowBufferFromNativeBuffer` (if NDK exposes it) or fall back to a GLES blit in surface_daemon (~1 day extra work). Equivalent to M6-R1 (HWUI GPU coherence) — bounded by NDK feature availability. |
| M12-R3 | Host HAP → surface_daemon SurfaceId handshake races: daemon presents a frame before HAP has registered XComponent callback | Med | Med | Daemon blocks at `XComponentBackend::probe()` waiting for SurfaceId from host HAP (read from a Unix socket with timeout). If no SurfaceId arrives within N seconds, daemon exits with clear error message. Mirrors Phase 1's "first buffer arrives before SurfaceView is ready" handling. |
| M12-R4 | XComponent's worker thread for OH_NativeWindow APIs conflicts with surface_daemon's per-layer consumer threads (re-entry / threading model mismatch) | Med | Med | Confirm OH_NativeWindow_NativeWindowRequestBuffer is thread-safe per-window (NDK doc claims yes). If not, serialize via a single XComponentBackend internal worker thread that all per-layer consumer threads queue into. Adds ~50 LOC of thread-safety. |
| M12-R5 | XComponent vsync rate differs from our 60Hz software tick — causes frame skip/dup | Low | Low | Phase 1 already runs with software vsync against real-display vsync; minor visual jitter is acceptable for M13 acceptance. Wire to real OH vsync as Phase 3 polish. |
| M12-R6 | OHOS production phones lock down XComponent to ArkUI-only origins (refuse to expose NativeWindow to fork/exec'd processes) | Med | Critical | Phase 2 acceptance uses dev boards (rk3568, hispark) where this is not enforced. Productized phones are a Phase 3 concern. See §8 R1. |

### 5.5 Code skeleton

```cpp
// aosp-surface-daemon-port/src/XComponentBackend.h (~50 LOC)
#pragma once
#include "SurfaceBackend.h"
#include <cstdint>
#include <unordered_map>

namespace westlake {

class XComponentBackend : public SurfaceBackend {
public:
    XComponentBackend();
    ~XComponentBackend() override;

    bool probe() override;       // wait for SurfaceId handshake from host HAP
    status_t createLayer(uint32_t layerId, int32_t w, int32_t h, int32_t fmt) override;
    status_t destroyLayer(uint32_t layerId) override;
    status_t presentBuffer(uint32_t layerId, const void *data, size_t bytes,
                           int32_t w, int32_t h, int32_t stride,
                           int32_t format, nsecs_t presentTimeNs) override;
    void setLayerVisible(uint32_t layerId, bool visible) override;
    void setLayerZOrder(uint32_t layerId, int32_t z) override;
    void setLayerPosition(uint32_t layerId, int32_t x, int32_t y) override;
    nsecs_t now() const override;

private:
    void *mLibNativeWindow = nullptr;       // dlopen of libnative_window.so
    void *mOHNativeWindow = nullptr;        // OHNativeWindow* from host HAP
    // Per-layer state — Phase 2 single-layer simplification is fine for noice
    struct Layer {
        int32_t width, height, format;
        bool    visible;
        int32_t zOrder, posX, posY;
    };
    std::unordered_map<uint32_t, Layer> mLayers;

    // Function pointers loaded lazily
    int32_t (*pfn_OH_NativeWindow_NativeWindowRequestBuffer)(void*, void**, int32_t*);
    int32_t (*pfn_OH_NativeWindow_NativeWindowFlushBuffer)(void*, void*, int32_t, void*);
    int32_t (*pfn_OH_NativeWindow_NativeWindowAbortBuffer)(void*, void*);
    int32_t (*pfn_OH_NativeWindow_NativeWindowHandleOpt)(void*, int32_t, ...);
};

}  // namespace westlake
```

(Full `XComponentBackend.cpp` body lives at ~400 LOC; the M6 plan §3.3 already reserved its slot in the directory layout. CR41 does not write the implementation — that's M12's job.)

---

## §6. M13 — noice on OHOS phone (end-to-end)

### 6.1 Inputs / preconditions

- M9 PASS (binder accessible).
- M10 PASS (musl libbinder + servicemanager on OHOS).
- M11 PASS (audio daemon on OHOS, audible tone).
- M12 PASS (surface daemon on OHOS, visible gradient).
- noice's APK package available (same one Phase 1 uses; binary identical).
- Host HAP authored (M12 dependency; the HAP holds the XComponent + orchestrator).

### 6.2 Approach

M13 is the integration milestone — analogous to M7 (Phase 1 finish line) but on OHOS hardware. It glues M9-M12 into a single runnable Phase-2 bringup. CR38 §2's 12-step boot sequence transcribes cleanly to OHOS with minor substitutions.

#### 6.2.1 OHOS-adapted 12-step boot

| Step | Phase 1 (cfb7c9e3) | Phase 2 (OHOS phone) |
|---|---|---|
| 1 | `adb push` to `/data/local/tmp/westlake/` | `hdc file send` to `/data/local/tmp/westlake/` (hdc = OHOS debug bridge) |
| 2 | `setprop ctl.stop vndservicemanager` to clear phone's native SM | `param set persist.westlake.servicemanager.enable 1` + stop OHOS's `hwservicemanager` if it's there. Most OHOS phones don't run a /dev/vndbinder service manager natively, so this step may be a no-op. |
| 3 | Spawn M2 servicemanager (bionic) | Spawn M10 servicemanager (musl) |
| 4 | Spawn M5 audio_daemon (bionic, AAudio) | Spawn M11 audio_daemon (musl, OhosBackend) |
| 5 | Spawn M6 surface_daemon (bionic, DLST pipe) | Spawn M12 surface_daemon (musl, XComponentBackend); pipe stdout connection replaced by SurfaceId handshake (§5.2.1) |
| 6 | Start host APK (Compose SurfaceView) | Start host HAP (ArkUI XComponent) — `am start` analogue is `aa start` |
| 7 | Wire surface_daemon stdout → host APK stdin | Host HAP passes SurfaceId to surface_daemon via Unix socket (no DLST pipe) |
| 8 | Spawn dalvikvm (bionic) | Spawn dalvikvm (musl); same JVM, same framework.jar, same noice.apk |
| 9 | dalvikvm: WestlakeLauncher.main → ColdBootstrap.ensure | Identical |
| 10 | dalvikvm: load + instantiate noice's Application | Identical |
| 11 | Application.onCreate → Hilt DI → binder transactions | Identical |
| 12 | MainActivity launch → first frame visible on Compose SurfaceView | MainActivity launch → first frame visible on ArkUI XComponent surface |

The dalvikvm steps (8-12) are **bit-for-bit identical** to Phase 1. The orchestration substrate (1-7) swaps `adb` for `hdc`, host APK for host HAP, and DLST pipe for SurfaceId handshake.

#### 6.2.2 dalvikvm musl binary

A musl-built dalvikvm binary is required (Phase 1 ships a bionic one). The Phase 1 build infra (`art-latest/`) supports musl as a target — same source, different sysroot. M13 effort includes:
- `make dalvikvm-musl` in `art-latest/` (build infrastructure may already support it; verify and exercise).
- Strip + push to `/data/local/tmp/westlake/dalvikvm-musl`.
- Phase 1's bionic libbinder.so handling of `__1` vs `__ndk1` is bionic-specific; for musl the namespace is `__1` natively, so the M5-Step3 `_LIBCPP_ABI_NAMESPACE=__1` flag may *not* be needed at all here. Verify during M13 first run.

#### 6.2.3 noice APK loading on OHOS

OHOS does not run APKs natively — that's the whole point of our engine. The orchestrator:
1. Host HAP packages the `noice.apk` as a HAP-internal resource (or downloads it at runtime).
2. Host HAP extracts the APK to `/data/local/tmp/westlake/noice.apk` (writable application sandbox path on OHOS).
3. Spawns dalvikvm with `-cp /data/local/tmp/westlake/noice.apk` — same as Phase 1.
4. dalvikvm loads noice's classes from the APK via PathClassLoader exactly as on Android.

**The APK file format is not the issue.** dalvikvm parses APK ZIP entries directly; OHOS's filesystem doesn't care that the file extension is `.apk`. The same APK bytes work identically.

#### 6.2.4 Acceptance bar

Mirror of M7 §5.1 (CR38) acceptance, ported to OHOS:
1. Host HAP visible on OHOS phone screen (foregrounded, XComponent occupies main area).
2. Boot orchestration finishes in <30 s (servicemanager + audio_daemon + surface_daemon + dalvikvm all spawned and PASS hard-fail gates).
3. noice MainActivity's first frame visible inside XComponent.
4. User can navigate noice tabs (Library / Presets / Sleep Timer).
5. Tapping a sound preset produces audible playback through OHOS speaker.
6. 10-minute interactive session: no crashes, no SIGBUS, no fail-loud logs.
7. dalvikvm logs match Phase-1 noice-on-cfb7c9e3 logs at the major-event level (Activity create/resume, Fragment add, view inflate). Cross-environment log diff PASS.

### 6.3 Effort estimate

| Sub-task | Person-days |
|---|---|
| Build dalvikvm musl variant (`make dalvikvm-musl`); verify ABI flags right | 0.5 |
| Author host HAP boot orchestrator: ArkUI ETS layout + ETS-to-native FFI for fork-and-exec | 0.75 |
| ETS-to-native FFI to spawn 4 subprocesses (sm, audio, surface, dalvikvm) | 0.5 |
| hdc-based push/launch script (the `scripts/run-noice-ohos.sh` analog of `run-noice-westlake.sh`) | 0.5 |
| First-run smoke: all 4 subprocesses spawn + binder addService logs visible | 0.25 |
| First-frame smoke: noice MainActivity first frame visible in XComponent | 0.5 |
| Audio playback smoke: tap preset → audible | 0.25 |
| 10-min interactive regression on OHOS phone | 0.25 |
| Cross-platform log diff (Phase 1 noice vs Phase 2 noice) | 0.25 |
| Polish + docs + regression entry | 0.5 |
| **Total** | **3.5 - 4.5 person-days** |

V1 estimated 2-3 days. **CR41 revises slightly upward** because:
- Authoring an ArkUI ETS host HAP is novel work for this team; +0.5-1 day for ETS familiarity ramp-up.
- The ETS-to-native FFI for fork-and-exec is the trickiest piece (ArkUI runtime sandbox; may need permissions like `ohos.permission.RUN_ANY_CODE` or equivalent which may not be available on production phones).
- Cross-platform log diff is new work (Phase 1 didn't have a comparable target).

Effort estimate revision noted; V1's 2-3 day band is feasible if HAP authoring is delegated to someone with ArkUI experience. **Realistic estimate: 3.5-4.5 person-days.**

### 6.4 Risks

| # | Risk | Likelihood | Impact | Mitigation |
|---|---|---|---|---|
| M13-R1 | ArkUI HAP sandboxing prevents `fork()` / `execve()` from native code | High on production phones, Med on dev boards | Critical | Use a dev board (rk3568, hispark) where sandbox is permissive. Production phones require `system_basic` APL or higher; document as known Phase 3 concern. See §8 R3. |
| M13-R2 | OHOS doesn't grant the HAP permission to access `/dev/vndbinder` from a non-system uid | Med | Critical | Either install the HAP as a system app (signing with system cert) or run via developer mode + escalated uid. ~1 day for cert + redeploy. |
| M13-R3 | dalvikvm musl build doesn't exist; need to add musl target to art-latest build | Low-Med | Med | art-latest already supports cross-compile via the Phase 1 work; musl target may or may not be wired. Verify during M13 prep; ~0.5 day to wire if missing. |
| M13-R4 | noice APK's native libs (.so) inside the APK are bionic-only and dlopen fails | Med | High | noice is a Compose music-player app; native libs are likely zero or one small ffi lib. If a native lib is bionic-only, it fails dlopen → noice falls back to JVM-only path. Most likely no impact. Verify pre-M13 by `unzip -l noice.apk | grep .so`. |
| M13-R5 | hdc (OHOS debug bridge) lacks features `adb` has (e.g. `exec-out`, port forwarding) | Low | Low | hdc has all relevant Phase-1 commands (`file send`, `shell`, `start app`). Spot-check with the M9 dev board bringup. |
| M13-R6 | OHOS phone screen orientation, density, refresh rate differ from cfb7c9e3 — noice's layout may break | Low | Low | noice is responsive; layout adapts. M4d's WestlakeDisplayManagerService can be tuned to report the OHOS device's actual values. ~1 hour. |

### 6.5 Code skeleton (host HAP outline)

```ets
// entry/src/main/ets/pages/Index.ets  (~30 LOC)
import { westlake } from 'libwestlake_host_native.so'

@Entry
@Component
struct Index {
  build() {
    Column() {
      Text('Westlake Engine — OHOS Phase 2').fontSize(20)
      XComponent({
        id: 'westlake-surface',
        type: XComponentType.SURFACE,
        libraryname: 'westlake_host_native'
      })
        .onLoad((ctx) => {
          // ctx.OH_NativeXComponent_GetNativeWindow → native side starts daemons
          westlake.startEngine(ctx, 'com.github.ashutoshgngwr.noice')
        })
    }
  }
}
```

```cpp
// entry/src/main/cpp/westlake_host_native.cpp  (~200 LOC)
// Loaded as libwestlake_host_native.so by ArkUI on XComponent .onLoad
// Receives OH_NativeXComponent*, forks 4 subprocesses, passes SurfaceId.
#include <ace/xcomponent/native_interface_xcomponent.h>
// ... native APIs: napi_module entry, onSurfaceCreated callback,
//     fork-exec orchestrator that mirrors scripts/run-noice-westlake.sh.
```

(M13 implementation does not start until M11 + M12 are green. CR41 does not write the body.)

---

## §7. Combined Phase 2 timeline

### 7.1 Person-day totals

| Milestone | Best | Expected | Worst |
|---|---|---|---|
| M9  binder kernel verify + (config patch) | 0.5 | 1.0 | 2.0 |
| M10 libbinder/servicemanager musl rebuild | 1.0 | 1.5 | 2.0 |
| M11 audio daemon → OHOS AudioRenderer | 2.5 | 2.75 | 3.0 |
| M12 surface daemon → OHOS XComponent | 4.0 | 4.25 | 4.5 |
| M13 noice on OHOS e2e | 3.5 | 4.0 | 4.5 |
| **Subtotal pure engineering** | **11.5** | **13.5** | **16.0** |
| Phase 2 risk reserve (10-15%) | 1.2 | 1.5 | 2.0 |
| **Total Phase 2 person-days** | **~13** | **~15** | **~18** |

### 7.2 Calendar with parallelism

Dependency graph (from `BINDER_PIVOT_MILESTONES.md` §0):

```
M9 ─── M10 ─┬── M11
            ├── M12
            └── M13  (depends on M11+M12)
```

With 2 engineers in parallel (one per daemon track):

```
Day 0 ─────── 1 ─────── 2 ─────── 3 ─────── 4 ─────── 5 ─────── 6 ─────── 7 ─────── 8

  M9 (eng1)
  ──────
        M10 (eng1)
        ─────
              M11 (eng1: audio daemon swap)
              ───────────────────
              M12 (eng2: surface daemon swap)
              ──────────────────────────────
                                          M13 (eng1+eng2: e2e on OHOS phone)
                                          ──────────────────
                                                       PHASE 2 ✓
```

**~7 calendar days with 2 engineers (expected case);** ~10 calendar days worst case.

With 1 engineer (sequential):

```
Day 0 ─────────────────────────── 15

  M9 ── M10 ── M11 ─────── M12 ────────── M13 ──── ✓
```

**~13-15 calendar days with 1 engineer;** ~18 days worst case.

### 7.3 Hardware acquisition gate

The single largest schedule risk is **getting an OHOS standard-system device onto the bench**. Per §8 R1, none currently in lab. Calendar slippage from hardware-acquisition delay is independent of the engineering budget above and can be 0-30 days depending on procurement.

**Schedule contingency:** acquire a rk3568 dev board (well-supported, OHOS standard-system Tier-A) **during Phase 1 final integration** (CR38 era; in parallel with M7/M8). That way M9 can start the day Phase 1 PASS, with hardware already in hand.

---

## §8. Open risks across Phase 2

Top risks beyond per-milestone risks already in §2.4 / §3.4 / §4.4 / §5.4 / §6.4:

| # | Risk | Likelihood | Impact | Mitigation |
|---|---|---|---|---|
| Phase2-R1 | **OHOS standard-system phone hardware unavailable** | **High** | **Blocking** | Acquire rk3568 dev board (Tier-A OHOS support; widely available retail in CN) during Phase 1 final integration. ~$80, ~2 weeks lead time. Defer production phone targets to Phase 3. |
| Phase2-R2 | OHOS productized phones lock down `/dev/vndbinder` for non-system uids (SELinux + DAC) | Med | High (on production phones) | Use dev boards for M9-M13 (root + permissive SELinux). Production phones become Phase 3 concern requiring system-cert signing + dedicated permission audit. |
| Phase2-R3 | ArkUI HAP sandbox prevents native `fork()` / `execve()` for spawning daemons | Med | Critical (production phones), Low (dev boards) | Dev boards permit `RUN_ANY_CODE` permission with developer signing. Production phones limit to `system_basic` APL; need OHOS-team coordination to elevate. |
| Phase2-R4 | OHOS NDK version skew: NDK ABI on target device differs from NDK headers we build against | Med | Med | Pin to a specific OHOS release tag (e.g. OpenHarmony-v4.1-Release) and use that exact NDK for the M10-M12 builds. Re-verify each M when refreshing NDK. ~0.5 day per refresh. |
| Phase2-R5 | OHOS kernel binder driver has accumulated OHOS-specific deltas vs upstream that change wire protocol | Low | High | Spot-check `kernel/linux/linux-5.10/drivers/android/binder.c` against upstream — verified to be present and substantially unchanged. ~1 hour code review. |
| Phase2-R6 | OHOS doesn't ship `libaudioclient.so` equivalent — our daemon's BpAudioFlinger client code may need rebuild for OHOS-only consumers (if any) | Low | Low | M11 daemon only acts as Bn server; clients are dalvikvm framework.jar processes whose libaudioclient.so we already provide. No new OHOS client. |
| Phase2-R7 | hdc (OHOS debug bridge) is less mature than adb; lacks some convenience features | Low | Low | Workarounds documented in OpenHarmony docs; ~1 day to write parallel hdc-based `run-*.sh` scripts. |
| Phase2-R8 | noice + V2 substrate references some Android-specific path string (`/system/etc/...`, `/data/data/...`) that doesn't exist on OHOS | Med | Med | OHOS standard system retains `/data` and `/system` mounts. Most paths work as-is. Spot-check during M13 first run; ~0.5 day to patch any mismatches in `WestlakeContextImpl.getDataDir` / `getFilesDir`. |
| Phase2-R9 | OHOS phone display density / orientation differs from cfb7c9e3 → noice layout breaks | Low | Low | M4d service reports OHOS device's actual density. noice is responsive. ~1 hour to tune. |
| Phase2-R10 | Cross-platform regression: a code path that works on bionic/AAudio fails on musl/OHOS-AudioRenderer (or vice versa) — could only be caught with both runs | Med | Med | Both Phase 1 and Phase 2 regressions run together in CI from M13 onwards. ~0.5 day of regression-suite plumbing. |

**Top-2 ranked risks for Phase 2:** Phase2-R1 (hardware acquisition) and Phase2-R3 (HAP fork/exec sandbox). Both blocking, both bounded by mitigation timeline.

---

## §9. Alignment check with V1 `BINDER_PIVOT_MILESTONES.md` §M9-M13

| | V1 estimate | CR41 estimate | Delta | Reason |
|---|---|---|---|---|
| M9  | 3-5 days (with OHOS team), 2-3 weeks (without) | 0.5-2.0 days | **MAJOR REDUCTION** | V1 assumed binder.ko had to be ported. CR41 verified the OHOS standard-system kernel **already has CONFIG_ANDROID_BINDER_IPC=y** in every board defconfig inspected. No porting needed; just configuration verification. |
| M10 | 1-2 days | 1.0-2.0 days | **HELD** | V1 correctly identified this as a build-script tweak. Phase 1's `aosp-libbinder-port/Makefile` already ships a musl variant; M10 is verify + libc++ ABI tuning. |
| M11 | 2-3 days | 2.5-3.0 days | **HELD** | M5 plan §9.2 estimated 2.5 days; CR41 confirms. |
| M12 | 4-5 days | 4.0-4.5 days | **HELD** | M6 plan §9.2 estimated 4.0 days; CR41 confirms. SurfaceId handshake protocol is novel but bounded. |
| M13 | 2-3 days | 3.5-4.5 days | **MODEST EXPANSION** | V1 didn't account for HAP authoring effort (ArkUI ETS is a new substrate for the team) or for cross-platform log-diff regression. Realistic estimate is ~3.5-4.5 person-days. |
| **Total** | **12-18 days** (V1 best-worst, excluding M9 kernel port worst-case 3-week tail) | **11.5-16.0 days** | **NARROWED** | Net: CR41 keeps M10-M13 in V1's band; M9 collapses from a 3-week tail risk to a 2-day max. |

**V1 milestones doc remains the authoritative work breakdown.** CR41 refines numbers downward (M9) and shifts a half-day upward (M13). No milestone reshuffles, no new milestones introduced, no scope expansion. Net Phase 2 effort: **~13 person-days expected, ~15 with reserve, ~18 worst case.**

The CR41 architectural insight that V1 didn't have:

1. **OHOS kernel already has binder.** This collapses M9 from a porting milestone to a verification milestone.
2. **Phase 1's M1 Makefile already ships a musl/OHOS target.** This collapses M10 from a rebuild milestone to a verification + libc++ tuning milestone.
3. **M5 + M6 plans pre-scoped the backend abstractions cleanly.** M11 and M12 are activation of already-scaffolded code, not new design work.
4. **The dalvikvm + framework.jar + V2 substrate + 6 M4 services are binary-portable.** Phase 1's Java work transitions to Phase 2 without code change.
5. **The single new substrate to author in Phase 2 is the host HAP.** This is the only piece of substantially new code (orchestrator + ArkUI ETS layout + ETS-to-native FFI), and even there the orchestrator logic mirrors Phase 1's host APK 1:1.

---

## §10. Critical files for the Phase 2 Builder to read first

1. `docs/engine/BINDER_PIVOT_MILESTONES.md` §M9-M13 — V1 canonical milestone breakdown
2. `docs/engine/BINDER_PIVOT_DESIGN_V2.md` — V2 substrate (which carries unchanged into Phase 2)
3. `docs/engine/M5_AUDIO_DAEMON_PLAN.md` §4.3 + §9.2 — OhosBackend pre-scoping
4. `docs/engine/M6_SURFACE_DAEMON_PLAN.md` §4.3 + §9.2 — XComponentBackend pre-scoping
5. `docs/engine/CR33_M6_SPIKE_REPORT.md` — memfd works on Phase-1 substrate; OHOS 5.10 inherits
6. `docs/engine/CR34_M5_SPIKE_REPORT.md` — AAudio probe; OHOS AudioRenderer is the Phase-2 counterpart
7. `docs/engine/CR38_M7_M8_INTEGRATION_SCOPING.md` §2 — boot sequence pattern M13 mirrors
8. `aosp-libbinder-port/Makefile` — already has `aarch64-linux-ohos` musl target
9. `/home/dspfac/openharmony/kernel/linux/config/linux-5.10/type/standard_defconfig` — proves binder is on
10. `/home/dspfac/openharmony/interface/sdk_c/multimedia/audio_framework/audio_renderer/native_audiorenderer.h` — M11 target API
11. `/home/dspfac/openharmony/interface/sdk_c/multimedia/audio_framework/common/native_audiostreambuilder.h` — M11 builder API
12. `/home/dspfac/openharmony/interface/sdk_c/graphic/graphic_2d/native_window/external_window.h` — M12 target API
13. `/home/dspfac/openharmony/interface/sdk_c/arkui/ace_engine/native/native_interface_xcomponent.h` — M12 XComponent + M13 host HAP API
14. `/home/dspfac/openharmony/out/sdk/sdk-native/os-irrelevant/sysroot/usr/lib/aarch64-linux-ohos/` — OHOS NDK runtime libs the daemons link against

---

## §11. Critical insights (across CR41)

1. **Phase 2's binder substrate is essentially free.** OHOS kernel ships binder enabled; OHOS NDK ships `libohaudio.so` and OH NativeWindow as direct equivalents to AAudio + SurfaceFlinger. The Phase 2 engineering surface is `OhosBackend.cpp` + `XComponentBackend.cpp` + host HAP — three small, well-scoped components.

2. **The Phase 1 M5 + M6 plans deserve credit for pre-scoping Phase 2.** Their `#ifdef OHOS_TARGET` backend abstraction means Phase 2 doesn't have to revisit the daemon shape — only swap one C++ file each. This is the architectural payoff that makes Phase 2 budget-able as ~13 person-days instead of ~6 weeks.

3. **The single largest schedule risk is hardware acquisition.** Engineering-side, Phase 2 is bounded and tractable. Procuring an OHOS standard-system dev board (or, harder, a production phone) is the gating prerequisite that can dominate calendar.

4. **Phase 2 ends at "noice on an OHOS dev board."** Productized OHOS phones (sandboxed HAP runtimes, signed APL elevation) are Phase 3 concerns — out of scope for M9-M13.

5. **Cross-platform regression matters.** From M13 onwards, both Phase 1 (cfb7c9e3) and Phase 2 (OHOS) regressions should run in CI together. A weekly cross-platform log diff catches the class of bug where one ABI path works but the other silently degrades.

---

## §12. Definition of done (Phase 2)

Phase 2 is complete when **all six** of the following hold:

1. M9 PASS: `/dev/vndbinder` accessible on OHOS dev board from our orchestrator's uid.
2. M10 PASS: musl libbinder + musl servicemanager run on OHOS dev board; `binder_smoke` returns clean.
3. M11 PASS: musl audio_daemon runs on OHOS dev board; `audio_smoke` emits 440Hz tone audible through OHOS speaker.
4. M12 PASS: musl surface_daemon runs on OHOS dev board; `surface_smoke` renders gradient frames visible in XComponent.
5. M13 PASS: noice MainActivity + interactive 10-min session on OHOS dev board; sound preset plays; no crashes.
6. Cross-platform regression: Phase 1 noice + Phase 2 noice both PASS in CI in the same run; log-diff PASS at major-event level.

After Phase 2 DONE, the engine has demonstrated its core architectural claim: **same dalvikvm + same framework.jar + same V2 substrate + same Java services + same daemon source code runs unchanged on both Android (Phase 1) and OHOS (Phase 2)**. The platform-specific layer is reduced to two `.cpp` backend files (~750 LOC combined) and a host HAP orchestrator (~250 LOC).

That is the Phase-2 thesis. End of CR41 roadmap.
