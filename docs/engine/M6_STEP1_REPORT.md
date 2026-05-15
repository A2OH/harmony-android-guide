# M6_STEP1_REPORT.md — surface-daemon skeleton + process pattern

**Status:** done (smoke PASS 2026-05-13 on cfb7c9e3)
**Owner:** Builder (CR for M6-Step1)
**Companion to:** [`M6_SURFACE_DAEMON_PLAN.md`](M6_SURFACE_DAEMON_PLAN.md), [`CR33_M6_SPIKE_REPORT.md`](CR33_M6_SPIKE_REPORT.md), [`PHASE_1_STATUS.md`](PHASE_1_STATUS.md)
**Predecessor:** CR33 spike (all 4 memfd/AHB phases PASS, plan estimate retained)
**Successor:** M6-Step2 (ISurfaceComposer AIDL transaction-code dispatch — gated on CR35's AIDL discovery report)

---

## §1  Goal

Step 1 is the **first** of ~6 M6 increments.  Goal is **architectural**:
prove that the daemon process pattern works end-to-end against our Phase-1
binder substrate.  Specifically:

1. A new project skeleton exists in `aosp-surface-daemon-port/` that mirrors
   the `aosp-libbinder-port/` layout (Makefile, build.sh, native/, out/).
2. A daemon binary builds and links against the existing
   `aosp-libbinder-port/out/bionic/libbinder.so` substrate — no rebuild
   of libbinder, no parallel substrate.
3. The daemon successfully calls
   `defaultServiceManager()->addService("SurfaceFlinger", ...)` against
   our running servicemanager on the phone.
4. The daemon blocks on `IPCThreadState::self()->joinThreadPool(true)`,
   ready to receive future Binder transactions.
5. The service is **discoverable** via our SM's `listServices` round-trip.

Step 1 does NOT attempt any real `ISurfaceComposer` logic — it is pure
process-pattern + service-registration plumbing.  All `onTransact` calls
in this step log + return `NO_ERROR` with an empty `reply`.  Step 2 will
add real AIDL transaction-code dispatch.

---

## §2  What was built

### 2.1 Files created

```
aosp-surface-daemon-port/
├── README.md                             NEW
├── BUILD_PLAN.md                         NEW
├── Makefile                              NEW
├── build.sh                              NEW (chmod +x)
├── m6step1-smoke.sh                      NEW (chmod +x; on-phone smoke)
├── m6step1-smoke-run.log                 NEW (captured smoke transcript)
└── native/
    ├── surfaceflinger_main.cpp           NEW (~80 LOC)
    ├── WestlakeSurfaceComposer.h         NEW (~40 LOC)
    └── WestlakeSurfaceComposer.cpp       NEW (~60 LOC)
```

Total Step-1 net-new LOC: ~180 LOC of C++ + ~110 LOC of build/test glue.

Existing spike artifacts in `aosp-surface-daemon-port/spike/` are unchanged
— CR33 deliverables.  Step 2+ will reuse `spike/spike.cpp` §5 memfd skeleton.

### 2.2 Files modified (outside aosp-surface-daemon-port/)

| File | Change |
|------|--------|
| `docs/engine/M6_STEP1_REPORT.md` | NEW (this file) |
| `docs/engine/PHASE_1_STATUS.md` | one row added: M6-Step1 done |

### 2.3 Files NOT touched

Per Step 1 anti-drift contract:
- `shim/java/**` — CR32 active there, not part of M6 scope
- `art-latest/**` — stable
- `aosp-libbinder-port/**` — stable; only consumed as a build dependency
- `aosp-audio-daemon-port/**` — CR34 spike territory
- `aosp-shim.dex` — unchanged
- Memory files — unchanged

Zero per-app branches introduced (Step 1 is generic; same daemon for every
Android app that talks to `"SurfaceFlinger"`).  Zero Unsafe /
setAccessible.  N/A for Java-side restrictions (Step 1 is pure native code).

---

## §3  Build verification

### 3.1 Toolchain

Matches `aosp-libbinder-port/Makefile` bionic targets verbatim:

| Component | Path |
|-----------|------|
| NDK r25 clang++ | `/home/dspfac/android-sdk/ndk/25.2.9519653/toolchains/llvm/prebuilt/linux-x86_64/bin/clang++` |
| Sysroot | NDK r25 default |
| Target | `aarch64-linux-android33` |
| Flags | `-U__ANDROID__ -fno-rtti -fno-exceptions -fvisibility=hidden -fPIC -std=c++17 -O2 -D_GNU_SOURCE -static-libstdc++ -ldl` |

### 3.2 Inputs reused (read-only)

- `../aosp-libbinder-port/out/bionic/libbinder.so` (1.76 MB stripped)
- `../aosp-libbinder-port/out/bionic/libutils_static.a`
- `../aosp-libbinder-port/out/bionic/libcutils_static.a`
- `../aosp-libbinder-port/out/bionic/libbase_static.a`
- `../aosp-libbinder-port/out/aidl-gen/include/**` (AIDL-generated headers)
- `../aosp-libbinder-port/aosp-src/libbinder/include/binder/**` (Binder/Parcel/IServiceManager etc.)

### 3.3 Output

```
$ file out/bionic/surfaceflinger
out/bionic/surfaceflinger: ELF 64-bit LSB pie executable, ARM aarch64,
                          version 1 (SYSV), dynamically linked,
                          interpreter /system/bin/linker64, stripped

$ ls -lh out/bionic/surfaceflinger*
-rwxr-xr-x 1 dspfac dspfac 37K  out/bionic/surfaceflinger (stripped)
-rwxr-xr-x 1 dspfac dspfac 53K  out/bionic/surfaceflinger.unstripped
```

Build is first-try clean (no warnings beyond the unused-`-fuse-ld=lld`
note that's also present in aosp-libbinder-port's build output, harmless).

---

## §4  Smoke test result

### 4.1 Method

`m6step1-smoke.sh` runs entirely on-device.  Steps:

1. `setprop ctl.stop vndservicemanager`; poll-wait for it to die (≤15s).
   (Mirrors `aosp-libbinder-port/m3-dalvikvm-boot.sh:start_sm`'s 15s poll
   pattern.)
2. Spawn our `servicemanager` on `/dev/vndbinder` as uid=1000.
3. Spawn our `westlake-surface-daemon` on `/dev/vndbinder` as uid=1000.
4. Run `sm_smoke` (from M2): it forks a child that registers a test
   service, then in the parent calls `listServices` + `checkService`
   round-trip.  Our daemon's `"SurfaceFlinger"` should appear in the
   listServices output.
5. Tear down: kill all 3 westlake processes, restart device's
   vndservicemanager via `setprop ctl.start vndservicemanager`.

### 4.2 Result

PASS.  Captured transcript in `aosp-surface-daemon-port/m6step1-smoke-run.log`:

```
[m6-step1] stopping vndservicemanager
[m6-step1] starting our servicemanager on /dev/vndbinder
[m6-step1] servicemanager up (pid via shell=10011)
[m6-step1] starting westlake-surface-daemon on /dev/vndbinder
[m6-step1] surface daemon up (pid via shell=10018)
[m6-step1] --- surface daemon log so far ---
[wlk-surface-daemon pid=10021] starting; binder=/dev/vndbinder; will register as "SurfaceFlinger"
[sm-stub] WaitForProperty(servicemanager.ready=true) -> immediate true
[wlk-surface-daemon] defaultServiceManager() OK
[wlk-sf] WestlakeSurfaceComposer constructed
[wlk-surface-daemon] addService("SurfaceFlinger") OK; entering joinThreadPool
[m6-step1] ---
[m6-step1] running sm_smoke (verifies listServices)
[m6-step1] sm_smoke exit=0
[m6-step1] --- sm_smoke log ---
[sm_smoke/parent] waiting for child ready signal...
[sm_smoke/child pid=10033] opening /dev/vndbinder
[sm_smoke/child] addService("m6step1.smoke") -> status=0 (ok)
[sm_smoke/parent pid=10032] opening /dev/vndbinder
[sm_smoke/parent] defaultServiceManager() OK
[sm_smoke/parent] listServices() returned 3 names:
    - SurfaceFlinger
    - m6step1.smoke
    - manager
[sm_smoke/parent] listServices(): found m6step1.smoke — ok
[sm_smoke/parent] checkService("m6step1.smoke"): non-null binder matches (remote BpBinder at 0x...)
[sm_smoke/parent] addService("m6step1.smoke.parent") -> status=0 (ok)
[sm_smoke/parent] PASS: all checks ok. Reaping child 10033
[m6-step1] ---
[m6-step1] PASS: SurfaceFlinger appears in listServices output
[m6-step1] tearing down
[m6-step1] done; result=0
```

### 4.3 Acceptance check-list

| Check | Result |
|-------|--------|
| Daemon binary builds first-try | PASS |
| Daemon binary is ARM64 bionic dyn-linked | PASS (`file` output) |
| `ProcessState::initWithDriver("/dev/vndbinder")` succeeds | PASS |
| `startThreadPool()` succeeds | PASS |
| `defaultServiceManager()` returns non-null | PASS |
| `WestlakeSurfaceComposer` BBinder constructs cleanly | PASS |
| `addService("SurfaceFlinger", ...)` returns `NO_ERROR (0)` | PASS |
| Daemon enters `joinThreadPool(true)` and blocks | PASS |
| `listServices()` from sm_smoke includes `"SurfaceFlinger"` | PASS |
| Clean tear-down (no orphan processes; vndservicemanager restarts) | PASS |

All 10 checks pass.  Step 1 is **done**.

---

## §5  Architecture decisions taken in Step 1

### 5.1 Interface descriptor

`WestlakeSurfaceComposer::getInterfaceDescriptor()` returns the literal
string `"android.ui.ISurfaceComposer"` — the canonical Android-11
`ISurfaceComposer.h:DECLARE_META_INTERFACE` descriptor.  This matches what
peers calling `BpBinder::getInterfaceDescriptor()` expect.

**Open question for Step 2:** verify against the shipped `framework.jar`'s
`android.view.SurfaceComposer.DESCRIPTOR` constant.  Android-16 may have
changed the namespace (e.g., `android.gui.ISurfaceComposer`).  CR35's
AIDL discovery output will confirm; if mismatched, swap the string.

### 5.2 `onTransact` Step-1 behavior

`onTransact` logs `code` + `flags`, then returns `NO_ERROR` with the
parcel `reply` left empty.  We intentionally do NOT chain to
`BBinder::onTransact()` — that would route `SHELL_COMMAND_TRANSACTION` /
`INTERFACE_TRANSACTION` to BBinder defaults, which we'll wire correctly
in Step 2.

This means Step 1 lets `listServices` see the binder (the SM doesn't
issue transactions to look up names; it tracks them in its own map), but
any peer issuing a real transaction (e.g.,
`IBinder::transact(GET_DISPLAY_INFO, ...)`) gets a successful no-op
reply instead of the expected response.  That is **expected** Step 1
behavior — Step 2 will replace it with the real dispatch switch.

### 5.3 `/dev/vndbinder` (not `/dev/binder`)

The daemon defaults to `/dev/vndbinder` — same context our SM and M3-M5
substrate uses.  Real production deployment will switch to `/dev/binder`
after CR47 lands a binder-domain split (tracked separately).  CLI is
overridable: `surfaceflinger /dev/binder` works the same way.

### 5.4 No init-script integration yet

Step 1 daemon is launched by the on-phone smoke script directly.  Real
`init.westlake.rc` integration is Step 6's job (`PHASE_1_STATUS.md`
already tracks dalvikvm.cfg-style autostart hooks).

### 5.5 Build artifact reuse pattern

The Makefile pulls includes + archives from
`../aosp-libbinder-port/out/bionic/`.  This couples the surface daemon's
build to libbinder's, but **avoids any rebuild of libbinder**, which is
desirable: the substrate is stable per `PHASE_1_STATUS.md` M3 row, and
recompiling libbinder.so risks ABI drift.

The same pattern would apply if M5's `aosp-audio-daemon-port/` builder
(CR34's spike successor) needs the same archives — it can reuse the
sibling outputs.

---

## §6  Next-step blockers

### 6.1 Step 2 (real ISurfaceComposer transaction dispatch)

Blocker: **CR35's AIDL transaction-code discovery output**.

The `onTransact` switch needs the actual numeric `code` values that
framework.jar's `Bp*` proxies send.  Android-11 `ISurfaceComposer.h`
hard-codes `BOOT_FINISHED=1`, `CREATE_CONNECTION=2`, ..., but framework
versions drift — we need to discover the codes from the **shipped**
framework.jar, not a reference source.

CR35 will:
1. Reflect against `framework.jar`'s `android.view.SurfaceComposer` /
   `BpSurfaceComposer` classes.
2. Read the `TRANSACTION_*` integer constants.
3. Publish a header (or .csv) that Step 2 can consume.

Step 2 then writes a switch like:

```cpp
status_t WestlakeSurfaceComposer::onTransact(uint32_t code, ...) {
    switch (code) {
    case TRANSACTION_GET_DISPLAY_INFO:
        return onGetDisplayInfo(data, reply);
    case TRANSACTION_GET_DISPLAY_CONFIGS:
        return onGetDisplayConfigs(data, reply);
    ...
    }
}
```

with canned 1080×2280@60Hz responses for the 4-5 Tier-1 codes (plan §2.2).

### 6.2 Step 2 prerequisites NOT blocked on CR35

- `WestlakeSurfaceComposer` can grow `onGetDisplayInfo` / `onGetDisplayConfigs`
  body code now using Android-11 reference signatures.  Just the dispatch
  switch keys depend on CR35.
- A test driver — call it `surface_smoke.cc` — can be drafted that issues
  `IBinder::transact(code, ...)` calls and asserts on the reply parcel.
  Can be authored in parallel; will plug-in once Step 2 lands codes.

### 6.3 No new risks discovered in Step 1

The Step-1 smoke run hit zero unexpected behavior.  Build was first-try
clean.  Smoke was second-try clean (first try hit a chmod issue from
adb push, fixed in 1 minute).  The grep regex `\s` was the second try
(toybox grep on Android doesn't support `\s`, swapped to `[[:space:]]`).

CR33's pre-M6 spike retired the largest risk (HWUI vs memfd).  Step 1's
clean pass confirms the secondary risks listed in `M6_SURFACE_DAEMON_PLAN.md` §8
rows #2-#5 are not in the Step-1 surface area:
- Risk #2 (BufferQueue protocol) — N/A; Step 1 has no BQ.
- Risk #3 (Choreographer vsync) — N/A; Step 2's `CREATE_DISPLAY_EVENT_CONNECTION` punts this.
- Risks #4-#5 (gralloc / fences) — N/A; Step 1 has no buffer plumbing.

---

## §7  Person-time spent

- File reads / pattern discovery: ~25 min (libbinder Makefile, sm_registrar.cc, m3-boot.sh, M6 plan, CR33 report)
- Code authoring (3 .cpp/.h files + Makefile + build.sh + README + BUILD_PLAN + smoke script): ~45 min
- Build + verify: ~5 min (first-try success)
- Phone smoke (including the chmod + grep fixes): ~10 min
- This report: ~25 min

**Total: ~1h 50min** (well inside the 3-4h budget).

---

## §8  Pointers for the next Builder

If you are picking up M6-Step2, read in this order:

1. This report §6 (what's blocked, what's not).
2. [`M6_SURFACE_DAEMON_PLAN.md`](M6_SURFACE_DAEMON_PLAN.md) §2 (the ~52
   ISurfaceComposer methods, classified by tier).
3. `/home/dspfac/aosp-android-11/frameworks/native/libs/gui/ISurfaceComposer.cpp`
   — the reference `onTransact` switch you'll be slimming down.
4. CR35's deliverable (once landed) — the real transaction codes.
5. This CR's [`native/WestlakeSurfaceComposer.cpp`](../../aosp-surface-daemon-port/native/WestlakeSurfaceComposer.cpp)
   — where you replace the Step-1 stub.

Acceptance for Step 2 per plan §3:
- At least 4 Tier-1 transaction codes dispatched with non-empty parcel
  replies that round-trip through `Parcel::writeStrongBinder`,
  `Parcel::writeInt32`, `Parcel::writeParcelable` correctly.
- A new `surface_smoke.cc` test harness that calls
  `getService("SurfaceFlinger") → IBinder::transact(GET_DISPLAY_INFO,
  ...)` and parses a non-empty reply.
- Existing M6-Step1 smoke still passes (no regression).
