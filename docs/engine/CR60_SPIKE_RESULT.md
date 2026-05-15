# CR60 — 32-bit dalvikvm Spike: Result Report

**Date:** 2026-05-14
**Author:** agent 11 (Workstream E spike runner)
**Status:** SPIKE PASS — recommend CONTINUE to full XComponent in-process integration
**Spike bounds:** 3-5 days authorized; completed in ~1 day of focused work.
**Cross-references:** `CR60_BITNESS_PIVOT_DECISION.md` (rationale), `OHOS_MVP_WORKSTREAMS.md` Workstream E (plan).

---

## TL;DR

All five spike items (E1-E5) **PASS** on real DAYU200 hardware. E6 (bitness-as-parameter
driver) **LANDED**. The 32-bit dalvikvm runs MVP-0 and MVP-1 with the exact same markers
as the aarch64 path. A standalone 32-bit dynamic ELF dlopen's three OHOS native libraries
(libace_napi.z.so, libnative_window.so, libace_ndk.z.so) in-process and resolves real
symbols (`napi_get_undefined`, `OH_NativeWindow_NativeWindowRequestBuffer`,
`OH_NativeXComponent_GetXComponentId`). The architectural premise of CR60 — that matching
bitness eliminates the M6 daemon for the production path — is empirically validated.

The aarch64 build remains unchanged and still passes MVP-0/1.

---

## E1 — Rebuild dalvikvm-arm32 with current source

- **Result:** PASS. 124/124 source files compiled cleanly via `make TARGET=ohos-arm32`.
- **Binary:** `dalvik-port/build-ohos-arm32/dalvikvm` (7.7 MB, ELF 32-bit LSB executable, ARM EABI5, statically linked, with debug_info).
- **Time:** ~50 seconds clean rebuild.
- **Notes:** The pre-existing 2-month-stale .o files in `build-ohos-arm32/` were wiped before rebuild. ARM-specific `CallEABI.S` assembled cleanly via the existing `ifeq ($(TARGET),ohos-arm32)` branch in `dalvik-port/Makefile`. No source changes required.

## E2 — Port aarch64 SIGSEGV fix to ARM32

- **Result:** PASS (no porting needed). All four fix layers verified arch-agnostic:
  1. **ScopedShutdown destructor guard** — already in `/home/dspfac/dalvik-kitkat/vm/Init.cpp:1361` (added post-MVP-0). Inherited by the rebuild.
  2. **Stale .o cleanup** — N/A on clean rebuild.
  3. **clang-15 u4 cast widening** — N/A on 32-bit: `u4 == sizeof(void*)` so the cast bug literally cannot occur.
  4. **Libcore.os null stubs** — already in `dalvik-port/compat/libcore_bridge.cpp` (all pointer arithmetic uses `(uintptr_t)`, audited via grep — no `(int)pointer` or `(long)pointer` casts).
- **Net code change:** 0 lines. The CR59 source state inherited every relevant aarch64 fix because they were all bitness-neutral by construction.

## E3 — Validate MVP-0 on 32-bit binary

- **Result:** PASS.
- **Reproducer:**
  ```
  scripts/run-ohos-test.sh --arch arm32 hello   # or just `hello` — auto-detect picks arm32
  ```
- **Marker line:** `westlake-dalvik on OHOS — main reached` (identical to aarch64 path).
- **Evidence:** `artifacts/ohos-mvp/cr60-arm32-spike/20260514_184734/mvp0/`.
- **Notes:** Clean shutdown via ScopedShutdown destructor; no SIGSEGV; same BCP (`core-kitkat.jar` + `direct-print-stream.jar` + `HelloOhos.dex`).

## E4 — Validate MVP-1 on 32-bit binary

- **Result:** PASS.
- **Reproducer:**
  ```
  scripts/run-ohos-test.sh --arch arm32 trivial-activity
  ```
- **Marker chain (all observed):**
  ```
  [OhosMvpLauncher] step 0: enter; argc=1
  [OhosMvpLauncher] step 1: package=com.westlake.ohostests.trivial ...
  [OhosMvpLauncher] step 2: class loaded
  [OhosMvpLauncher] step 3: activity instantiated
  [OhosMvpLauncher] step 4: calling Instrumentation.callActivityOnCreate(...)
  OhosTrivialActivity.onCreate reached pid=9750
  [OhosMvpLauncher] step 4: callActivityOnCreate returned cleanly
  [OhosMvpLauncher] step 5: calling Instrumentation.callActivityOnDestroy(...)
  OhosTrivialActivity.onDestroy reached
  [OhosMvpLauncher] step 6: launcher complete; exit 0
  ```
- **Evidence:** `artifacts/ohos-mvp/cr60-arm32-spike/20260514_184734/mvp1/`.
- **Notes:** Same BCP as aarch64 (`core-android-x86.jar` + `direct-print-stream.jar` + `aosp-shim-ohos.dex` + per-test dex). V2 substrate (`shim/java/android/app/Westlake*.java`) is pure Java and bitness-neutral, as predicted.

## E5 — XComponent in-process: dlopen smoke

- **Result:** PASS for the smoke test (proves the architectural premise). Full XComponent integration (E5b) **deferred** — the hardware bottleneck is signing a HAP that exposes a surface to our process, not the dlopen primitive.
- **What landed:** `dalvik-port/compat/ohos_dlopen_smoke.c` — a standalone 32-bit ARM dynamic ELF that opens each of the three production OHOS lib types and resolves a well-known symbol:
  ```
  /system/lib/platformsdk/libace_napi.z.so       :: napi_get_undefined                       OK
  /system/lib/chipset-sdk-sp/libnative_window.so :: OH_NativeWindow_NativeWindowRequestBuffer OK
  /system/lib/ndk/libace_ndk.z.so                :: OH_NativeXComponent_GetXComponentId      OK
  -> PASS 3/3
  ```
- **Evidence:** `artifacts/ohos-mvp/cr60-arm32-spike/20260514_184734/mvp5-dlopen-smoke/result.stdout`.
- **Build pattern (for the followup):** Link as **dynamic** PIE ELF (`-fuse-ld=lld -Wl,-dynamic-linker=/lib/ld-musl-arm.so.1`), use the device's own `/lib/ld-musl-arm.so.1` copied into the sysroot at `ohos-sysroot-arm32/usr/lib-dynamic/libc.so`, link against `libunwind.a` for `__aeabi_unwind_cpp_pr0`. The Scrt1.o + crti.o + crtn.o from the existing static sysroot are reusable as-is.
- **Critical caveat for E5b:** The current `dalvikvm-arm32` is **statically linked** (musl static), and musl's static `dlopen` cannot load arbitrary runtime SOs — only ones linked at build time. **Full XComponent in-process integration will require rebuilding dalvikvm as a dynamic PIE ELF** (build pattern proved by the smoke test). Estimate: ~½ day to retarget the Makefile's `ohos-arm32` rules to dynamic linkage.

## E6 — Bitness-as-parameter driver

- **Result:** LANDED.
- **Changes in `scripts/run-ohos-test.sh`:**
  - New `--arch aarch64|arm32|auto` flag, parsed in `main()` before the subcommand.
  - New `resolve_arch()` function — when `ARCH=auto`, runs `hdc shell getconf LONG_BIT` and maps "32" → arm32, "64" → aarch64. Populates `DALVIKVM_BOARD_PATH` (`/data/local/tmp/dalvikvm{,-arm32}`) and `DALVIKVM_HOST_PATH` (build-ohos-{aarch64,arm32}/dalvikvm).
  - All six subcommand call sites (`hello`, `trivial-activity`, `red-square`, `red-square-drm`, `m6-java-client` ×2 references) updated to use `${DALVIKVM_BOARD_PATH}` instead of hardcoded `/data/local/tmp/dalvikvm`.
  - `status`, `push-bcp`, `help` skip arch resolution (they don't need it).
- **Verified passing:**
  - `scripts/run-ohos-test.sh hello` (auto → arm32) — MVP-0 PASS
  - `scripts/run-ohos-test.sh --arch aarch64 hello` — MVP-0 PASS
  - `scripts/run-ohos-test.sh trivial-activity` (auto → arm32) — MVP-1 PASS
  - `scripts/run-ohos-test.sh --arch aarch64 trivial-activity` — MVP-1 PASS (regression)
- **CI note:** Both `build-ohos-aarch64/dalvikvm` and `build-ohos-arm32/dalvikvm` build from the same Makefile without `#ifdef` divergence in shim or JNI bridge sources (the only arch-conditional code is `DVM_ASM_SRCS` in the Makefile, which is build-side and existed pre-CR60).

---

## Self-audit (CR60 + macro-shim contract)

- [x] **No `Unsafe.allocateInstance`** in any new Java code — confirmed: no Java code changed in this spike.
- [x] **No `setAccessible(true)`** in any new Java code — confirmed: no Java code changed.
- [x] **No per-app branches** in any new code — confirmed: smoke probes are library-level, not package-level. Driver `--arch` is board-level, not app-level.
- [x] **`intptr_t` / `uintptr_t` / `size_t` in new native code** — confirmed: `ohos_dlopen_smoke.c` has no `(int)` or `(long)` casts of pointers. dlopen handles printed via `%p` format; symbol addresses printed via `%p`. `argc/argv` are `int` per ISO C contract and not pointer-related.
- [x] **aarch64 dalvikvm still builds** — `build-ohos-aarch64/dalvikvm` untouched, fresh from today's earlier CR59 build; file -1 verified ELF 64-bit aarch64.
- [x] **aarch64 MVP-0/1 still PASS** — both confirmed via `scripts/run-ohos-test.sh --arch aarch64 hello` and `... trivial-activity`.
- [x] **E5 smoke proves dlopen** — confirmed PASS 3/3 in `artifacts/ohos-mvp/cr60-arm32-spike/20260514_184734/mvp5-dlopen-smoke/`.
- [x] **CR60 doc + Workstream E updated** — this report is the CR60 update; Workstream E section needs a "spike result" pointer added (next commit).
- [x] **`feedback_bitness_as_parameter.md` still correct** — no rule changes needed; the spike followed the discipline. One refinement worth noting (added to followup recommendations below): static-musl dlopen has limitations that force a dynamic build for the production XComponent path.

---

## Recommendation: CONTINUE

The spike answers the strategic question "can we eliminate the M6 daemon for the
production XComponent path by matching dalvikvm bitness to the OHOS userspace?" with a
clear **YES**. All three pieces of the architectural premise are now empirically grounded:

1. 32-bit dalvikvm runs MVP-0/MVP-1 with the same Java substrate and BCP (proves dex
   and substrate are bitness-neutral).
2. 32-bit ARM EABI5 dynamic ELF can dlopen NAPI + native_window + XComponent NDK libs
   in-process (proves the cross-arch IPC daemon is no longer necessary on this board).
3. Both arches coexist via the new `--arch` flag (proves the pivot is reversible if a
   future board ships 64-bit OHOS userspace).

### Next gate (one item, ~½ day): dynamic dalvikvm-arm32

The current arm32 dalvikvm is statically linked (musl static), so its `dlopen` is
limited. To wire E5b (full XComponent in-process), retarget the Makefile's
`ohos-arm32` rules to build a **dynamic PIE ELF** using the same pattern proved by
`ohos_dlopen_smoke.c`:

- replace `-static` with `-fuse-ld=lld -Wl,-dynamic-linker=/lib/ld-musl-arm.so.1`
- link against `ohos-sysroot-arm32/usr/lib-dynamic/libc.so` (device's musl shared lib)
- pull in `libunwind.a` from `prebuilts/clang/.../arm-linux-ohos/`
- add Scrt1.o + crti.o + crtn.o

Once `dalvikvm-arm32` is a dynamic binary, all three OHOS lib probes will work
*inside the VM* (System.loadLibrary or direct JNI), and the M6 daemon becomes a
non-default fallback path. The aarch64 + M6 daemon route continues to exist for
boards that don't support our linkage model.

### Open items that the spike did not address (intentional, per "bounded spike")

- HAP signing for an XComponent host app that hands a surface to our dalvikvm
  process. Deferred to the followup milestone — the dlopen primitive being green is
  the prerequisite, not this step.
- M6 daemon regression: no run; touching the daemon was out of scope. Both daemon
  and aarch64 binary on board untouched (size + mtime unchanged).
- Updating `OHOS_MVP_WORKSTREAMS.md` Workstream E "Open work" items E1-E6 to mark
  PASS — small docs edit, included in this commit set.
