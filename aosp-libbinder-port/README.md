# aosp-libbinder-port

AOSP `libbinder.so` + `servicemanager` cross-compiled out of the AOSP build tree
for use by the Westlake engine. Implements M1, M2, and M3 of the binder pivot
plan (see `docs/engine/BINDER_PIVOT_MILESTONES.md`).

## Overview

The Westlake engine is pivoting its substitution layer away from per-framework-
class shims and toward a single substitution point at the Binder service
boundary. The argument is captured in `docs/engine/BINDER_PIVOT_DESIGN.md`: own
the binder runtime, register Java service implementations through the real
`ServiceManager.addService` path, and let AOSP's framework.jar and AOSP native
libraries talk through real binder without modification.

This directory holds the artifacts that make that possible: a cross-compiled
`libbinder.so` (with libutils/libcutils/libbase trimmed to just what binder
needs), the AOSP `servicemanager` daemon, smoke tests, and the JNI plumbing
that lets `dalvikvm`'s `android.os.ServiceManager` reach our libbinder.

Two parallel builds coexist in the same Makefile:

| Variant | Toolchain | Output | Use case |
|---|---|---|---|
| `musl`   | OHOS Clang 15.0.4 + OHOS sysroot | `out/`        | Phase 2 OHOS phones; portable testing |
| `bionic` | NDK r25 + Android API 33 sysroot | `out/bionic/` | M3 dalvikvm test on OnePlus 6 (Android 15 LineageOS) |

Both variants compile the same source through the same patches; only the
toolchain, sysroot, and a small `bionic-overlay/` of newer UAPI headers differ.

## Directory layout

```
aosp-libbinder-port/
  README.md                       this file
  BUILD_PLAN.md                   M1 scoping doc (Architect, 2026-05-12)
  M3_NOTES.md                     M3 implementation notes + verification
  Makefile                        all targets (musl + bionic siblings)
  build.sh                        driver wrapper around the Makefile
  build_hello.sh                  builds HelloBinder.dex for M3 test

  aosp-src/                       sources copied from AOSP frameworks-native
    libbinder/                    libbinder common + kernel IPC + OS abstraction
                                  + AIDL inputs + a stripped liblog_stub/
  deps-src/                       libutils/libcutils/libbase/libsystem subsets
    libutils_binder/              RefBase, String8/16, Threads, Timers, ...
    libcutils/                    ashmem-host, native_handle, atrace_stub
    libbase/                      file, strings (C++17 backport),
                                  properties_stub (NO-OP SetProperty), ...
    libselinux_stub/              empty stub for servicemanager's SELinux paths
    libsystem/                    headers only
  bionic-overlay/                 newer kernel UAPI headers for bionic build
    include/linux/android/binder.h    AOSP mainline (has BR_FROZEN_BINDER etc.)
  patches/                        patches against AOSP source (3 + 1 marker)
  ohos-sysroot/                   constructed by build.sh sysroot (musl path)

  native/
    libandroid_runtime_stub.cc   M3 .so JNI bridge (now superseded by
                                  art-latest/stubs/binder_jni_stub.cc; kept
                                  as reference for future M4 A1 migration)

  test/
    binder_smoke.cc               M1 smoke: open SM, listServices, exit
    sm_smoke.cc                   M2 round-trip: addService + getService child/parent
    sm_registrar.cc               M3 long-lived registrar (sm_smoke variant that
                                  parks in joinThreadPool until SIGKILL)
    HelloBinder.java              M3 Java test: ServiceManager.getService through JNI

  sandbox-boot.sh                 M2 phone-side sandbox: stops vndservicemanager,
                                  starts ours on /dev/vndbinder, runs sm_smoke,
                                  tears down and restores vndservicemanager
  m3-dalvikvm-boot.sh             M3 phone-side: same setup + sm_registrar +
                                  dalvikvm HelloBinder.dex

  out/                            musl build outputs
    libbinder.so                  803 KB stripped
    servicemanager                223 KB stripped
    test/binder_smoke             66 KB
    test/sm_smoke                 65 KB
    libutils_static.a, libcutils_static.a, libbase_static.a
    aidl-gen/                     AIDL-generated .cpp/.h (target-independent)
    obj/                          .o tree

  out/bionic/                     bionic build outputs (M3)
    libbinder.so                  1.7 MB stripped
    libbinder_full_static.a       2.6 MB whole-archive bundle (for dalvikvm)
    libandroid_runtime_stub.so    46 KB (M3 .so variant; now unused)
    servicemanager                256 KB stripped
    test/sm_smoke                 71 KB
    test/sm_registrar             47 KB
    libutils_static.a, libcutils_static.a, libbase_static.a
    obj/                          .o tree
```

## Build

### Prerequisites

- OHOS Clang at `/home/dspfac/openharmony/prebuilts/clang/ohos/linux-x86_64/llvm/bin/clang++`
- OHOS musl + libcxx prebuilts and kernel uapi headers (the OHOS source tree
  at `/home/dspfac/openharmony` provides them; `build.sh sysroot` collates a
  consumable sysroot under `ohos-sysroot/`).
- AIDL host binary at `/home/dspfac/android-sdk/build-tools/34.0.0/aidl`.
- AOSP-side sources mirrored at `/home/dspfac/aosp-sources/` (referenced from
  the Makefile for `cmds/servicemanager` and `system/core/libcutils`).
- NDK r25 at `/home/dspfac/android-sdk/ndk/25.2.9519653/` (bionic path only).

### Musl path

```bash
cd /home/dspfac/android-to-openharmony-migration/aosp-libbinder-port
./build.sh all      # sysroot, AIDL, deps, libbinder, smoke, servicemanager, sm_smoke
```

Individual targets:

```bash
./build.sh sysroot         # rebuild ohos-sysroot/
./build.sh aidl            # AIDL .cpp/.h regen
./build.sh deps            # libutils + libcutils + libbase archives
./build.sh libbinder       # link libbinder.so
./build.sh smoke           # binder_smoke
./build.sh servicemanager  # AOSP servicemanager binary
./build.sh sm_smoke        # M2 round-trip test
```

### Bionic path (M3)

```bash
./build.sh bionic      # all-bionic: deps-bionic, libbinder-bionic,
                       # smoke-bionic, servicemanager-bionic, sm_smoke-bionic,
                       # sm_registrar-bionic, libandroid_runtime_stub-bionic
```

After a successful bionic build, the dalvikvm linkage step lives in
`/home/dspfac/art-latest/`. To rebuild dalvikvm with the binder JNI baked in:

```bash
make -C /home/dspfac/art-latest -f Makefile.bionic-arm64
```

This consumes `out/bionic/libbinder_full_static.a` and links it whole-archive,
adding ~1 MB to dalvikvm (26 MB to 27 MB).

## Toolchain

| Variant | Path | Notes |
|---|---|---|
| OHOS Clang | `/home/dspfac/openharmony/prebuilts/clang/ohos/linux-x86_64/llvm` | Clang 15.0.4 |
| OHOS triple | `aarch64-linux-ohos` | musl libc + libc++ |
| OHOS sysroot | `aosp-libbinder-port/ohos-sysroot/usr/` | Built by `build.sh sysroot` |
| Android NDK | `/home/dspfac/android-sdk/ndk/25.2.9519653/toolchains/llvm/prebuilt/linux-x86_64` | NDK r25, Clang 14.0.7 |
| Android triple | `aarch64-linux-android33` | API 33 sysroot (NDK r25) |
| AIDL | `/home/dspfac/android-sdk/build-tools/34.0.0/aidl` | Target-independent output, shared between variants |

The musl sysroot is constructed from `/home/dspfac/openharmony/third_party/musl`
headers, `linux-5.10` kernel UAPI, and an AOSP mainline `binder.h` pulled from
the AOSP gerrit at sysroot-build time (with a bionic-11 fallback if the network
is unavailable). The bionic sysroot uses NDK r25's bundled sysroot; the only
overlay is `bionic-overlay/include/linux/android/binder.h` (newer UAPI than NDK
r25 ships, to expose `BR_FROZEN_BINDER`, `BC_FREEZE_*`, etc.).

## Sandbox setup (OnePlus 6, kernel 4.9 — pre-binderfs)

The dev phone is an OnePlus 6 running Android 15 LineageOS with kernel 4.9.337.
That kernel predates `binderfs` (≥5.0), so the standard "fresh mount per test"
sandbox isn't available. Instead we exploit
`CONFIG_ANDROID_BINDER_DEVICES="binder,hwbinder,vndbinder"` from the kernel
config: the binder driver creates one `/dev/<name>` character device per name
in that list, each with its own context manager.

`sandbox-boot.sh` claims `/dev/vndbinder` by:

1. Stopping the system's `vndservicemanager` (`setprop ctl.stop`).
2. Starting our servicemanager bound to `/dev/vndbinder`.
3. Running the test against `/dev/vndbinder` (via `BINDER_DEVICE` env var).
4. Killing our servicemanager and restarting `vndservicemanager`.

The system's `/dev/binder` and `/dev/hwbinder` are never touched, so AOSP
services on the device keep running normally.

### The `BINDER_SET_CONTEXT_MGR` UID-sticky quirk

The 4.9.337 binder driver pins the context-manager UID for each
`/dev/<name>` device to whoever first claimed it via `BINDER_SET_CONTEXT_MGR`.
`vndservicemanager` runs as AID_SYSTEM (uid 1000); after it dies the kernel
still rejects `BINDER_SET_CONTEXT_MGR` from any other uid with `-EPERM`.

Workaround: every script that brings up our servicemanager invokes it via
`su 1000 -c '...'`. The sandbox scripts make this explicit; any future M4
boot orchestrator on this kernel needs the same dance. On post-binderfs
kernels (≥5.0) a fresh binderfs mount sidesteps the issue.

## Tests

### M1 — binder_smoke

```bash
adb push out/libbinder.so out/test/binder_smoke /data/local/tmp/westlake/...
adb shell '... LD_LIBRARY_PATH=... binder_smoke'
# Expect exit 0, lists services from whatever binder device it found
```

### M2 — sm_smoke (round-trip)

```bash
adb push out/libbinder.so out/servicemanager out/test/sm_smoke .../westlake/lib/
adb push aosp-libbinder-port/sandbox-boot.sh /data/local/tmp/westlake/bin/
adb shell 'su -c "bash /data/local/tmp/westlake/bin/sandbox-boot.sh test"'
# Expect sm_smoke PASS:
#   - addService("westlake.test.echo") -> status=0
#   - listServices() returns ["manager", "westlake.test.echo"]
#   - checkService("westlake.test.echo") -> non-null BpBinder
```

Same shape passes in the bionic variant from `out/bionic/`.

### M3 — dalvikvm + HelloBinder.dex

```bash
adb shell 'su -c "bash /data/local/tmp/westlake/bin-bionic/m3-dalvikvm-boot.sh test"'
# Expect dalvikvm exit 0:
#   HelloBinder: starting M3 end-to-end test
#   WLK-binder-jni: nativeListServices: 2 names
#   HelloBinder: getService("westlake.test.echo") -> NativeBinderProxy{...} (non-null)
#   HelloBinder: PASS
#   HelloBinder: exiting with code 0
```

The boot script's stages are documented in its header. Stage 3 (`sm_registrar`)
keeps `westlake.test.echo` alive across the dalvikvm run; without it the
`sm_smoke` child would exit and tear the service down before HelloBinder runs.

### Running the regression suite

`scripts/binder-pivot-regression.sh` runs every smoke + service test in this
directory back-to-back against the OnePlus 6 and prints pass/fail with timings.
This is the canonical entry point for swarm agents — one command instead of
remembering seven individual invocations. Specified in
`docs/engine/BINDER_PIVOT_MILESTONES.md` §"Test Plan Master Summary".

```bash
bash scripts/binder-pivot-regression.sh             # --full (default; ~5-7 min)
bash scripts/binder-pivot-regression.sh --quick     # sm_smoke + M3 only (~1 min)
bash scripts/binder-pivot-regression.sh --phase=4   # stop after M4 service tests
bash scripts/binder-pivot-regression.sh --no-color  # ANSI off (for logs)
```

Exit code 0 means every non-SKIPped test passed. Exit 1 means one or more
failed; the per-test verdict, the last 15 lines of failing output, and a
final summary list make it easy to see what regressed. Exit 2 is a setup
error (adb missing, device offline, required artifact absent).

What each phase verifies:

| Phase | Test | Source | Verifies |
|---|---|---|---|
| 1 | `sm_smoke / sandbox` | `sandbox-boot.sh` + `bin/sm_smoke` (musl) | M1+M2: AOSP libbinder + servicemanager round-trip on `/dev/vndbinder` |
| 2 | `HelloBinder` | `m3-dalvikvm-boot.sh test` | M3: dalvikvm calls `ServiceManager.listServices/getService` through JNI |
| 2 | `AsInterfaceTest` | `m3-dalvikvm-boot.sh --test AsInterfaceTest` | M3++: same-process `Stub.asInterface` returns the same `BBinder` instance |
| 3 | `BCP-shim` | `m3-dalvikvm-boot.sh --bcp-shim` | PF-arch-053: slim `aosp-shim.dex` on `-Xbootclasspath` no longer SIGBUSes |
| 3 | `BCP-framework` | `m3-dalvikvm-boot.sh --bcp-shim --bcp-framework` | PF-arch-053: framework.jar + ext.jar + services.jar on BCP no longer SIGBUSes |
| 4 | `ActivityServiceTest` | `--test ActivityServiceTest` | M4a: `WestlakeActivityManagerService` Tier-1 (getCurrentUserId, getRunningAppProcesses, etc.) |
| 4 | `PowerServiceTest` | `--test PowerServiceTest` | M4-power: `WestlakePowerManagerService` (isInteractive, wakeLock cycle) |
| 4 | `SystemServiceRouteTest` | `--test SystemServiceRouteTest` | CR3: `WestlakeContextImpl.getSystemService` routes via SM + reflective Manager wrap |
| 4 | `DisplayServiceTest` | `--test DisplayServiceTest` | M4d: `WestlakeDisplayManagerService` (in flight) |
| 4 | `NotificationServiceTest` | `--test NotificationServiceTest` | M4e: `WestlakeNotificationManager` (in flight) |
| 4 | `InputMethodServiceTest` | `--test InputMethodServiceTest` | M4e: `WestlakeInputMethodManager` (in flight) |
| 4 | `WindowServiceTest` | `--test WindowServiceTest` | M4b: `WestlakeWindowManagerService` (in flight) |
| 5 | `noice-discover` | `noice-discover.sh` | W2/M4-PRE: noice's `Application.onCreate` reaches PHASE E (post-M4-PRE2 high-water mark) |

If a test dex isn't on the device (e.g. M4d/M4e dexes while those agents are
mid-build), the suite SKIPs rather than failing. A SKIPped test counts toward
"all pass" — only a real FAIL flips the suite exit code.

**CR7 note (2026-05-12):** `m3-dalvikvm-boot.sh` and `sandbox-boot.sh` source
`lib-boot.sh` for synchronous vndservicemanager teardown (poll-until-dead +
SIGKILL fallback instead of flat sleep). When pushing updated scripts to the
phone, push `lib-boot.sh` alongside them (`bin-bionic/` for m3, alongside the
others) — the scripts include an inline fallback if the helper is absent, but
having the file present produces clearer diagnostics. See
`docs/engine/M4_DISCOVERY.md` §31 for the underlying race.

The script does NOT rebuild or push artifacts. Refresh with:

```bash
bash scripts/build-shim-dex.sh                                # aosp-shim.dex
(cd aosp-libbinder-port && ./build.sh bionic)                 # libbinder, SM, sm_smoke, sm_registrar (bionic)
(cd aosp-libbinder-port && ./build_hello.sh && ./build_asinterface.sh && ...)   # individual test dexes
# Then push artifacts to the phone using the pattern in docs/engine/PHASE_1_STATUS.md §7.2.
```

Environment overrides: `ADB="..."`, `WESTLAKE_DIR="..."` if your layout differs.

## Patches

Six small patches in total, all in this directory.

### `patches/0001-imemory-drop-stdatomic.patch`

Drops `#include <stdatomic.h>` from `IMemory.cpp` (collides with libcxx's
`<atomic>` under the OHOS toolchain), pulls `memory_order_*` from `std::`, and
provides a no-op stub for `android_errorWriteWithInfoLog` (Android-only event
logger).

### `patches/0002-libbase-strings-cxx17.patch`

Backports `std::string_view::starts_with` / `ends_with` (C++20) to manual
size+substr checks for C++17.

### `patches/0003-parcel-kheader-syst.patch`

Makes `Parcel.cpp`'s non-Android kernel-binder header (the 4-byte transaction
magic written by every transaction) selectable at compile time via a build
flag. Without an explicit override, AOSP defaults to `'UNKN'`, which is
rejected as `BR_FAILED_REPLY` by both the host phone's Android system
servicemanager (expects `'SYST'`) and any stock OHOS-vendor binder endpoint
(expects `'VNDR'`).

The patch leaves the AOSP default intact and adds a three-way selector:

| Build flag (passed via Makefile `KHEADER_FLAG`) | `kHeader` value | Use case |
|---|---|---|
| `-DWESTLAKE_KHEADER_SYST=1` | `'SYST'` | **Default today.** Phase 1 sandbox on the OnePlus 6 (talks to phone's `system_server` peers) and any future "system process" deployment. |
| `-DWESTLAKE_KHEADER_VNDR=1` | `'VNDR'` | Phase 2 production OHOS where libbinder needs to interoperate with stock OHOS-vendor binder endpoints on `/dev/vndbinder`. |
| (neither defined) | `'UNKN'` | AOSP default, unmodified. For builds that don't talk to either kind of peer. |

The Makefile's `KHEADER_FLAG := -DWESTLAKE_KHEADER_SYST=1` is the single
switch — flip it to `-DWESTLAKE_KHEADER_VNDR=1` (or unset entirely) to
rebuild for a different peer profile. Both the musl and bionic builds
inherit the flag through `DEFINES`, so a single Makefile edit covers both
variants.

Originally (pre-CR6) this patch hardcoded `'SYST'` unconditionally, which
broke the "AOSP default behavior is unchanged when our defines aren't set"
invariant. CR6 (Codex Tier-4 review) made the choice selectable so Phase 2
won't need a fresh patch.

### `patches/0004-parcel-accept-any-kheader-on-recv.patch`

Widens `Parcel::enforceInterface`'s receive-side `kHeader` comparison from a
single-value equality check to a set-membership check over the four canonical
magic values: `'SYST'`, `'VNDR'`, `'RECO'`, `'UNKN'`. The send side (in
`Parcel::writeInterfaceToken`) is unchanged — we still write `kHeader` so peers
can identify us as a Westlake/AOSP-shim caller.

Why both sides need different rules:

| Side | What it does | Why |
|---|---|---|
| Send | Writes our `kHeader` (`'SYST'` under today's Makefile) | Peer libbinders use the 4-byte magic as a self-identifying breadcrumb; keeping it lets logs and crash reports identify which build of libbinder originated the call. |
| Receive | Accepts any of `'SYST'`, `'VNDR'`, `'RECO'`, `'UNKN'` | Peer libbinders are compiled with their own `kHeader` based on partition (`__ANDROID_VNDK__`, `__ANDROID_RECOVERY__`, or plain `__ANDROID__`), and a strict equality check rejects every cross-partition / cross-vendor transaction. The four-byte magic is a breadcrumb, not a gate. |

Concrete trigger (CR11, 2026-05-12): after a OnePlus 6 reboot, the phone's
`vndservicemanager` started writing `'VNDR'` on replies; our libbinder (baked
with `WESTLAKE_KHEADER_SYST=1` from CR6) rejected every transaction with
`Expecting header 0x53595354 but found 0x564e4452. Mixing copies of libbinder?`.
177 KB of those errors made `noice-discover` wedge before PHASE A could
complete. Patch 0004 widens the receive check; PHASE A and PHASE B now run
cleanly.

The patch is forward-apply-safe and reverse-apply-safe, and it does not touch
the `kHeader` definitions from patch 0003 or the `writeInterfaceToken`
send-side write.

### `patches/bionic-include-prefix.h`

Documentation-only marker file. Records the syslog/LOG_INFO token-paste hazard
in the bionic build and notes that the mitigation is in
`aosp-src/libbinder/liblog_stub/include/log/log.h` (the `ALOG` macro was
patched there rather than via a -include prefix).

### M3-finish change: `scripts/framework_duplicates.txt`

Not a patch per se but worth listing. Removed these classes so they live in
`aosp-shim.dex` instead of being stripped (because framework.jar isn't on the
bootclasspath in M3):

```
android/os/IBinder
android/os/IInterface
android/os/Parcel
android/os/Binder
android/os/RemoteException
android/os/ServiceManager
android/os/IServiceManager
android/os/ServiceManagerNative
```

These get re-added when M4's A1 migration puts framework.jar on the
bootclasspath and lets the AOSP versions win.

## Known limitations

- **Phase 1 is bionic-only on OnePlus 6.** OHOS uses the musl variant (M9-M10
  in the milestones doc). The musl libbinder.so and servicemanager are
  cross-compiled and pass `binder_smoke` shape but have not yet been put
  through `/dev/vndbinder` end-to-end on an OHOS device. That work is M10.
- **framework.jar bootclasspath SIGBUS.** Loading framework.jar on the dalvikvm
  bootclasspath crashes this build inside PathClassLoader setup. M3 sidesteps
  this by putting `aosp-shim.dex` (with our `ServiceManager` shim) on the
  regular classpath. M4-A1 will need to fix the SIGBUS before AOSP's real
  framework.jar can drive binder transactions.
- **Cross-process `onTransact` dispatch not implemented.** Same-process
  `Stub.asInterface` will elide marshaling for Java service impls registered
  in dalvikvm. The C++ libbinder runtime can issue/receive transactions
  cross-process (sm_smoke proves it), but the Java `Binder.onTransact` JNI
  upcall isn't wired in M3's binder_jni_stub. M4-A1 will add the BinderProxy
  and JavaBBinder JNI cluster.
- **`addService()` from Java is a stub.** `nativeAddService` on the JNI side
  registers a fresh C++ BBinder under the requested name (so listServices
  sees it) but doesn't bind it back to a Java-side BBinder subclass. M3 tests
  register services via the external `sm_registrar` binary.
- **`waitForService()` does not wait.** Falls through to `getService()`. Add
  the 5-second poll loop when a caller needs it.

## Cross-references

- `docs/engine/BINDER_PIVOT_DESIGN.md` — design, rationale, open questions
- `docs/engine/BINDER_PIVOT_MILESTONES.md` — M1-M13 acceptance criteria
- `docs/engine/AGENT_SWARM_PLAYBOOK.md` — multi-agent coordination conventions
- `docs/engine/PHASE_1_STATUS.md` — current Phase 1 progress and pipeline replay
- `docs/engine/ANDROID_SHIM_AUDIT.md` — what shim/java/android/* shrinks to after M3
- `docs/engine/WESTLAKE_LAUNCHER_AUDIT.md` — same for WestlakeLauncher.java
- `aosp-libbinder-port/BUILD_PLAN.md` — M1 scoping (source roster, patch surface)
- `aosp-libbinder-port/M3_NOTES.md` — M3 implementation notes + verification
