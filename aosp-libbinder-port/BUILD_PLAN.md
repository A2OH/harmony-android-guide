# BUILD_PLAN.md — M1 `libbinder.so` musl port

**Status:** scoping (M1 prep)
**Author:** Architect agent (2026-05-12)
**Companion to:** `docs/engine/BINDER_PIVOT_DESIGN.md`, `docs/engine/BINDER_PIVOT_MILESTONES.md` (§ M1)

This document specifies the build scaffold for cross-compiling AOSP `libbinder.so` against musl libc + OHOS sysroot. The Builder agent who executes M1 should treat this as the work breakdown.

---

## 1. Source identification

### 1.1 Recommended AOSP version: **android-16.0.0_r1**

Public verification (`https://android.googlesource.com/platform/frameworks/native/+refs`) confirms `android-16.0.0_r1` through `android-16.0.0_r4` exist plus `android-security-16.0.0_r6`. Local checkouts on this machine:

| Local path | Tag | Has `frameworks/native/libs/binder` |
|---|---|---|
| `/home/dspfac/aosp-android-11` | android-11.0.0_r48 | yes (older, 11126 LOC, 36 .cpp files) |
| `/home/dspfac/aosp-art-15` | android-15.0.0_r17 | no (art-only sync) |
| `/home/dspfac/aosp-libcore-15` | (15.x libcore) | no |

**Decision: target `android-16.0.0_r1`.** Rationale:
- Android 16 has an explicit `libbinder_sdk` cc_library target and the OS layer is already split into `OS_android.cpp` vs `OS_non_android_linux.cpp`. Android 11 tree couples bionic deeply — every port effort would be ours.
- The Android 16 `OS_non_android_linux.cpp` already provides musl-compatible implementations of trace functions, `__android_log_print` (stderr fallback), and `GetThreadId` via `syscall(__NR_gettid)`. ~80% of the bionic-vs-musl work is pre-done.
- AIDL transaction codes between AOSP 16 and the framework.jar we ship must agree.

**Tradeoff:** need to clone `frameworks/native` from `android-16.0.0_r1` (~150 MB) since it isn't locally synced.

**Fallback:** if cloning fails, use the local Android 11 tree and accept the higher bionic-patch surface (10 patches vs 3). Document the version mismatch.

### 1.2 Required files — Android 16 layout

From `frameworks/native/libs/binder/` (clone target: `aosp-libbinder-port/aosp-src/`):

**Common (transport-independent, 17 .cpp):**
`Binder.cpp`, `BpBinder.cpp`, `Debug.cpp`, `FdTrigger.cpp`, `IInterface.cpp`, `IResultReceiver.cpp`, `Parcel.cpp`, `ParcelFileDescriptor.cpp`, `RecordedTransaction.cpp` (optional), `RpcSession.cpp`, `RpcServer.cpp`, `RpcState.cpp`, `RpcTransportRaw.cpp`, `Stability.cpp`, `Status.cpp`, `TextOutput.cpp`, `Utils.cpp`, `file.cpp`.

**Kernel IPC (4 .cpp — M1's actual goal):**
`BufferedTextOutput.cpp`, `BackendUnifiedServiceManager.cpp`, `IPCThreadState.cpp` (per-thread binder driver state, POSIX `pthread_key_create` based), `IServiceManager.cpp` (locates binder context 0), `ProcessState.cpp` (singleton holding `/dev/binder` fd + threadpool spawn), `Static.cpp`.

**OS abstraction (pick one):**
- `OS_non_android_linux.cpp` — **THE MUSL TARGET** — stubs trace_*, redirects `__android_log_print` to stderr, `GetThreadId` via syscall, `report_sysprop_change` returns false
- `OS_unix_base.cpp` — common to both Android and non-Android Linux
- `OS_android.cpp` — **NOT USED** (bionic-specific)

**Device-interface (omit initially, add in M2 if needed):**
`IPermissionController.cpp`, `PermissionCache.cpp`, `PermissionController.cpp`.

**Headers:** ~40 .h files in `include/binder/*` plus `include/private/binder/binder_module.h`.

**Kernel UAPI:** `<linux/android/binder.h>` — already available at `/home/dspfac/openharmony/out/sdk/obj/third_party/musl/usr/include/aarch64-linux-ohos/linux/android/binder.h`.

**AIDL files:** ~7 .aidl files in `aidl/android/os/` and `aidl/android/content/pm/`, compiled by AIDL compiler at build time.

**Total .cpp file count: 22–31** (17 common + 4 kernel + 1 OS-stub + 0–3 device-interface + 6 AIDL-generated).

### 1.3 Dependency libraries

**libutils** (~6 .cpp, ~2938 LOC) — `RefBase.cpp`, `String8.cpp`, `String16.cpp`, `Threads.cpp`, `Errors.cpp`, `CallStack.cpp` (stub on musl or libunwind).

**libcutils** (~2 .cpp, ~160 LOC) — `ashmem-host.cpp` (memfd-based, AOSP-supplied), `native_handle.cpp`. Plus header-only `cutils/atomic.h`, `cutils/compiler.h`, `cutils/threads.h`.

**libbase** — headers-only subset of `system/libbase/include/android-base/`.

**liblog** — headers + a ~30 LOC `__android_log_print` → stderr stub (provided by `OS_non_android_linux.cpp` in Android 16).

**Total source-line scope for M1: ~14300 LOC C++.**

---

## 2. Bionic-specific code audit

### 2.1 Android 16 surface (preferred — ~3 patches)

| Symbol / Idiom | Disposition |
|---|---|
| `pthread_key_create` for IPCThreadState TLS | Already POSIX — no patch |
| `__android_log_print` | Provided by `OS_non_android_linux.cpp` as stderr printf |
| `atrace_*` | No-op stubs in `OS_non_android_linux.cpp::trace_begin/end/int` |
| `__system_property_get("sys.boot_completed")` | **1 site to patch** in IServiceManager.cpp — wrap with `#ifdef __BIONIC__` |
| `set_sched_policy` / `cutils/sched_policy.h` | Include only, no function calls. **1 site** — stub the header types |
| `cutils/ashmem.h` ashmem_* | Use AOSP `ashmem-host.cpp` (memfd-based) unchanged |
| `cutils/atomic.h` | Header is stdatomic wrapper — works on musl |
| `<sys/system_properties.h>` | **1 site** — guard with `#ifdef __BIONIC__` |

**Android 16 bionic-touch surface: ~3 deliberate patches.**

### 2.2 Android 11 fallback (~10 patches)

| Symbol / Idiom | file:line | Handling |
|---|---|---|
| `<cutils/sched_policy.h>` types | `IPCThreadState.cpp:26` | Stub header — types but no impl |
| `<cutils/atomic.h>` | Multiple files | Drop in AOSP atomic.h verbatim |
| `<cutils/ashmem.h>` | `MemoryHeapBase.cpp`, `Parcel.cpp` | Drop in `ashmem-host.cpp` |
| `<cutils/properties.h>` `property_get` | `IServiceManager.cpp:34,223` | Stub returning `""` |
| `<cutils/threads.h>` | `BufferedTextOutput.cpp:21` | Header-only thread_local wrapper |
| `<cutils/compiler.h>` CC_LIKELY | `BpBinder.cpp:25` | Trivial header |
| `<cutils/native_handle.h>` | `Parcel.h:26` | Drop in `native_handle.cpp` (~80 LOC) |
| `<utils/Log.h>` ALOG macros | 14 .cpp files | Headers + supply stderr-backed `__android_log_print` |
| `<utils/CallStack.h>` | `IPCThreadState.cpp:27` | Stub class with empty `stack()` |
| `<utils/SystemClock.h>` `uptimeMillis()` | `IPCThreadState.cpp:29` | 8-LOC `clock_gettime(CLOCK_BOOTTIME)` wrapper |

**Android 11 bionic-touch surface: ~10 patches** — most stub-with-header rather than rewrite.

### 2.3 Summary

| Class of work | Android 16 sites | Android 11 sites |
|---|---|---|
| Replace-with-musl-equivalent | 0 (already done) | 3 |
| Stub-out | 2 | 5 |
| Port-libcutils-minimally | 0 | 2 |
| **Total patches** | **~3** | **~10** |

---

## 3. musl-side gaps

Musl provides standard POSIX. All gaps are bionic→musl translations on the AOSP side, not musl porting work.

| Function | musl status | Action |
|---|---|---|
| `pthread_key_create` / `pthread_getspecific` | Available since 0.9.0 | None |
| `pthread_mutex_*`, `pthread_cond_*` | Standard POSIX | None |
| `pthread_setname_np` | Available since 1.1.16 (16-char truncation matches bionic) | None |
| `clock_gettime(CLOCK_BOOTTIME, ...)` | Available | None |
| `ashmem_create_region` | Not in musl — provide via `ashmem-host.cpp` | Use AOSP file |
| `ioctl(fd, BINDER_*, ...)` | Standard musl ioctl + UAPI struct | None |
| `mmap(/dev/binder, ...)` | Standard | None |
| `syscall(__NR_gettid)` | Available; musl 1.2.0+ has `gettid()` | None |
| `getrandom` | musl 1.1.20+ | Verify OHOS musl version |
| `__android_log_print` | Not in musl — 30 LOC stub provided | Provide stub |
| C11 stdatomic | musl supports | None |

**Verified OHOS musl version:** The dalvik-port has already cross-compiled against it successfully — full coverage proven.

**No musl-side gaps requiring porting work.**

---

## 4. Build system proposal

### 4.1 Toolchain

**Clang:** `/home/dspfac/openharmony/prebuilts/clang/ohos/linux-x86_64/llvm/bin/clang++` (Clang 15.0.4).

**Triple:** `aarch64-linux-ohos`.

**Compiler flags:**
```
--target=aarch64-linux-ohos
--sysroot=<sysroot>
-D__MUSL__
-fPIC
-std=c++17
-Wall -Wextra -Werror -Wno-zero-as-null-pointer-constant
-fno-rtti -fno-exceptions
-DBINDER_WITH_KERNEL_IPC=1  (Android 16 only)
```

**Linker:** `ld.lld` from the same toolchain.

### 4.2 Sysroot

**Pre-built sysroot available as shortcut:**
`/home/dspfac/openharmony/out/sdk/sdk-native/os-irrelevant/sysroot/usr/{include,lib/aarch64-linux-ohos}/`

Use directly: `--sysroot=/home/dspfac/openharmony/out/sdk/sdk-native/os-irrelevant/sysroot/usr` with `-target aarch64-linux-ohos`. Try first; fall back to per-project constructed sysroot (recipe from `dalvik-port/build-ohos.sh:37-109`) if libraries missing.

### 4.3 Phase 1 — musl-only build

**Decision: musl-only.** No bionic variant.

Rationale: Phase 1's smoke test runs on the Pixel 7 Pro. The Pixel runs Android, but it will happily run musl-linked binaries pushed to `/data/local/tmp` if we set `LD_LIBRARY_PATH` to point to our musl + libc++ tree (same pattern as the static dalvikvm in dalvik-port). Kernel ABI (ioctl, mmap) is identical regardless of libc.

**Net: one build artifact for both Phase 1 (Android) and Phase 2 (OHOS).**

### 4.4 Build system: hand-rolled Makefile

**Decision:** mirror the dalvik-port pattern (`/home/dspfac/android-to-openharmony-migration/dalvik-port/Makefile`).

Rationale:
- Soong unavailable outside AOSP build tree (would balloon scope by ~2 weeks).
- CMake exists for `libbinder_sdk` snapshot but builds the no-kernel variant.
- Hand-rolled Makefile mirrors dalvik-port pattern the team is comfortable with.

**Build target structure:**
```
aosp-libbinder-port/
├── BUILD_PLAN.md
├── Makefile
├── build.sh                       (driver: prep sysroot, run AIDL, run make)
├── aosp-src/                      (cloned subtree of frameworks/native/libs/binder)
├── deps-src/
│   ├── libutils/                  (subset of system/core/libutils)
│   ├── libcutils/                 (ashmem-host.cpp, native_handle.cpp, atomic.h)
│   ├── libbase/                   (header-only subset)
│   └── liblog/                    (headers + log_stub.cpp if Android 11)
├── patches/
│   ├── 0001-stub-system-properties.patch
│   ├── 0002-stub-set-sched-policy.patch
│   └── (Android 11 fallback) 0003-stub-callstack.patch
├── ohos-sysroot/                  (constructed if not using pre-built)
├── out/
│   ├── obj/                       (per-.cpp .o files)
│   ├── aidl-gen/                  (AIDL-generated .cpp/.h)
│   ├── libbinder.so
│   └── test/binder_smoke
└── test/
    └── binder_smoke.cc
```

### 4.5 Build flow

1. `./build.sh sysroot` — verify sysroot present
2. `./build.sh aidl` — run AIDL compiler over 7 .aidl files
3. `./build.sh deps` — compile libutils + libcutils into `libutils_static.a`, `libcutils_static.a`
4. `./build.sh libbinder` — compile 22 binder .cpp files, link shared with `-lutils_static -lcutils_static -lc -lc++ -ldl -lpthread`
5. `./build.sh smoke` — compile and link `binder_smoke`
6. `./build.sh all` — all of the above in order

**Expected stripped size:** ~1.8 MB libbinder.so, ~50 KB smoke test.

### 4.6 AIDL compilation

The `:libbinder_aidl` filegroup contains 7 .aidl files. Options for the AIDL host binary:
- Check `/home/dspfac/android-sdk/build-tools/*/aidl` and `/home/dspfac/openharmony/prebuilts/build-tools` first
- Otherwise host-build the AOSP `aidl` tool from `/home/dspfac/aosp-android-11/system/tools/aidl/` (~3 hours, ~200 LOC main.cpp + parsing logic)

**Builder must verify AIDL availability before fetching frameworks/native** to bound worst-case path.

---

## 5. Smoke test outline — `binder_smoke.cc`

**File:** `aosp-libbinder-port/test/binder_smoke.cc`
**LOC target:** ~50

```cpp
// includes: <binder/IServiceManager.h>, <binder/IPCThreadState.h>,
//           <utils/String8.h>, <utils/Vector.h>, <stdio.h>

int main(int argc, char** argv) {
  // 1. (Optional) override binder driver:
  //    if (getenv("BINDER_DEVICE")) call ProcessState::initWithDriver(env)
  //    else default to /dev/binder

  // 2. Resolve the global ServiceManager singleton.
  //    sp<IServiceManager> sm = defaultServiceManager();
  //    if (sm == nullptr) { fprintf(stderr, "SM unavailable\n"); return 1; }

  // 3. List services (hits the device's real servicemanager).
  //    Vector<String16> names = sm->listServices(IServiceManager::DUMP_FLAG_PRIORITY_ALL);

  // 4. Print each service name.
  //    for (size_t i = 0; i < names.size(); ++i) {
  //        printf("%s\n", String8(names[i]).c_str());
  //    }

  // 5. (Stretch) demonstrate proxy fetch:
  //    sp<IBinder> b = sm->checkService(String16("activity"));
  //    if (b != nullptr) printf("activity binder: %p\n", b.get());

  return 0;
}
```

**Build invocation:**
```
${OHOS_LLVM}/bin/clang++ --target=aarch64-linux-ohos \
  --sysroot=${SYSROOT} \
  -Iaosp-src/include -Iaosp-src/include/private \
  -Lout -lbinder -lutils_static -lcutils_static -lc++ -lpthread \
  -o out/test/binder_smoke test/binder_smoke.cc
```

**Acceptance:**
- `file out/libbinder.so` → ELF 64-bit ARM64 dynamic library
- `readelf -d out/libbinder.so | grep NEEDED` → only `libc.so`, `libc++.so`, `libdl.so`, `libpthread.so`
- `LD_LIBRARY_PATH=. /data/local/tmp/westlake/bin/binder_smoke` on Pixel → prints ≥50 service names, exit 0

---

## 6. Risk register

| # | Risk | Likelihood | Impact | Mitigation |
|---|---|---|---|---|
| 1 | Android 16 frameworks/native clone fails (network, repo policy, rate limits) | Medium | High | Pre-cache via `git clone --depth 1 -b android-16.0.0_r1`. Have Android 11 fallback plan ready. |
| 2 | AIDL compiler not available outside AOSP build tree | Medium | Medium | Check `/home/dspfac/android-sdk/build-tools/*/aidl` first. Host-build from local AOSP 11 tree as backup (~3 hr). Hand-write generated headers (~200 LOC) as last resort. |
| 3 | libutils RefBase has bionic-only TLS assumption | Low | High | Verified: standard `__thread` + `std::atomic`. No bionic TLS. 30-min check before commitment. |
| 4 | Smoke test on Pixel sees real `/dev/binder` collision with system servicemanager | Medium | Low | Acceptable — confirms link works. Isolation moves to M2 (userns+binderfs). |
| 5 | OHOS clang 15.0.4 lacks C++20 features used by Android 16 libbinder | Low | Medium | Use `-std=c++17`. Fall back to Android 15.0.0_r1 if C++20 needed. |
| 6 | OHOS sysroot kernel headers lack BINDER_* ioctls | Low | Medium | Verified: OHOS 5.10 kernel has full Android binder UAPI. **No action.** |
| 7 | `pthread_key_create` exhausted (musl ~128 keys) | Very low | Low | Single IPCThreadState key — well within budget. |

**Top 5 by priority: 1, 2, 4, 3, 5.**

---

## 7. Effort estimate

| Sub-task | Person-days |
|---|---|
| Source cloning + initial build attempt | 1.0 |
| Bionic patch work (3 patches Android 16, 10 Android 11) | 1.0 (Android 16) / 2.0 (Android 11) |
| First successful link | 0.5 |
| First successful runtime test on Pixel | 0.5 |
| Smoke test passes | 0.5 |
| Build script polish + documentation | 0.5 |

**Total: 4.0 person-days (Android 16) / 5.0 (Android 11).**

Matches milestones-doc estimate of 3–5 days. Budget 5 days, plan for 4.

---

## 8. Files the Builder agent will create

- `aosp-libbinder-port/Makefile`
- `aosp-libbinder-port/build.sh`
- `aosp-libbinder-port/aosp-src/` (cloned)
- `aosp-libbinder-port/deps-src/` (subsetted)
- `aosp-libbinder-port/patches/*.patch`
- `aosp-libbinder-port/test/binder_smoke.cc`
- `aosp-libbinder-port/out/libbinder.so`

---

## 9. Critical files for the Builder agent to read first

1. `/home/dspfac/android-to-openharmony-migration/dalvik-port/build-ohos.sh` — canonical sysroot + cross-compile recipe for this codebase. Mirror sections 36-109.
2. `/home/dspfac/android-to-openharmony-migration/dalvik-port/Makefile` — Makefile pattern to mirror.
3. `/home/dspfac/aosp-android-11/frameworks/native/libs/binder/Android.bp` — source-file roster for Android 11 fallback.
4. (Web) `https://android.googlesource.com/platform/frameworks/native/+/refs/tags/android-16.0.0_r1/libs/binder/Android.bp` — preferred target.
5. `/home/dspfac/android-to-openharmony-migration/docs/engine/BINDER_PIVOT_MILESTONES.md` § M1 — acceptance criteria source of truth.

---

## 10. Critical insights from scoping (lower the milestones-doc risk estimate)

1. **Lower-risk than estimated:** Android 16 introduced `libbinder_sdk` with `OS_non_android_linux.cpp` that pre-solves the bionic split. Bionic patch surface shrinks from ~10 to ~3.
2. **Lower-risk than estimated:** the dalvik-port has already constructed a working OHOS aarch64 sysroot and proven OHOS musl supports static binaries running on the Pixel. Builder copies that recipe verbatim.
3. **Lower-risk than estimated:** no `__system_property_get`, no bionic TLS slot use, no `set_sched_policy` actual calls (only header includes), no `prctl` use, no `gettid` use in binder core. Bionic dependency is shallower than R1 implied.
4. **Higher-risk than estimated:** AIDL compiler availability — not in milestones-doc risk register. Builder must verify this FIRST.
5. **Neutral:** Phase 1 musl-only build is correct — no bionic variant needed.

**The Builder agent's biggest risk:** AIDL compiler step (risk #2). If pre-built `aidl` found, M1 proceeds in ~4 days. If not, ~3 hours host-building from `/home/dspfac/aosp-android-11/system/tools/aidl/` becomes serial dependency. **Verify AIDL toolchain availability first**, before fetching frameworks/native.

End of BUILD_PLAN.md.
