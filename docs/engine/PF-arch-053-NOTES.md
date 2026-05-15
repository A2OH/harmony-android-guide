# PF-arch-053 — Bootclasspath PathClassLoader SIGBUS Resolution

**Status:** RESOLVED (verified 2026-05-12)
**Type:** Regression-test addition (no runtime code change required)
**Authoritative patch:** `art-latest/patches/PF-arch-053-bootclasspath-pathclassloader-fix.patch`

## TL;DR

The historical SIGBUS at `fault_addr=0xfffffffffffffb17` during
PathClassLoader initialization with `-Xbootclasspath:aosp-shim.dex` is
already fixed by earlier work and no longer reproducible.  M4 is unblocked.

## Background

M3's `M3_NOTES.md` carried this warning:

> aosp-shim.dex on -cp (NOT -Xbootclasspath).  When the shim dex is on
> the bootclasspath this dalvikvm build crashes with SIGBUS during
> PathClassLoader setup (one of the 778 shim classes corrupts the
> early-clinit path).  Moving the shim dex to the regular classpath
> (which is loaded lazily by the system class loader) avoids the bug...

That warning is now obsolete.

## Why the SIGBUS happened (historical)

The fault address `0xfffffffffffffb17` is the Westlake-specific
**stale-native-entry sentinel** (`kPFCutStaleNativeEntry`).  It marks a
JNI entry point that was never bound to real code.

Definitions:

| File | Line |
|---|---|
| `art-latest/patches/runtime/class_linker.cc` | 169 |
| `art-latest/patches/runtime/art_method.cc` | 391 |
| `art-latest/patches/runtime/interpreter/interpreter.cc` | 62 |
| `art-latest/patches/runtime/instrumentation.cc` | 66 |
| `art-latest/patches/runtime/native/jdk_internal_misc_Unsafe.cc` | 62 |

When code dispatches to it (via `BLR x0` in
`art_quick_generic_jni_trampoline` or its IMT/proxy siblings), the CPU
load-faults.

Mechanism (now fixed):

1. The fat `aosp-shim.dex` (4.8 MB, 3835 classes) contained ~3000 classes
   whose fully-qualified names duplicated `framework.jar` classes (e.g.
   `android.view.View`, `android.os.Bundle`, all of `android.widget.*`).
2. ART's boot classpath enumeration walked entries left-to-right.  The
   shim came first; the duplicate-but-incomplete shim class won.
3. Many shim classes declared `native` methods that no native code in
   `dalvikvm` actually implements.  Without an `OHBridge.RegisterNatives`
   binding, `EntryPointFromJni` stayed at the sentinel.
4. First time `ClassLinker::LinkCode` traversed one of those methods, it
   stomped the JNI entry to `GetJniDlsymLookupStub()`, but bionic-static
   `dlsym` returns NULL (libdl.a stub).  Subsequent dispatches resolved
   back to the sentinel.
5. A trampoline `BLR` to the sentinel = SIGBUS at fault_addr=fffffb17.

The PathClassLoader path was a hot site because boot-class init touches
many classes early.  But the actual root cause was sentinel-poisoned
duplicate-named native methods, not PathClassLoader itself.

## Why it no longer happens

The bug was systemically resolved by these earlier patches (none filed
specifically as "PathClassLoader BCP" fixes, but the combination closes
it):

### Slim-shim work (architectural cleanup, 2026-05-07)

* `scripts/build-shim-dex.sh` — adds a class-stripping step between
  `javac` and `dx`.
* `scripts/framework_duplicates.txt` — 1813 fully-qualified class names
  that duplicate `framework.jar`.  Stripped before dex packaging.

Result: `aosp-shim.dex` went from 4,821,120 → 1,419,564 bytes; class
count from 3835 → 754.  The duplicate-named native-method classes are
gone; the sentinel is no longer reachable from boot dispatch.

### PF-arch-019 (entry-point preservation, 2026-05-11)

`ClassLinker::LinkCode` no longer stomps `EntryPointFromJni` when it has
been validly bound by `RegisterNatives`.  Uses
`IsJniDlsymLookupStub` / `IsJniDlsymLookupCriticalStub` /
`PFCutIsStaleNativeEntry` to decide.

### PF-arch-004 + framework_register_stubs.cpp

Replaces `dlsym` runtime registration walks with a direct extern table;
all 31 `register_android_*` hooks resolve.

### PF-arch-013 (NAR + VMRuntime stubs)

29 `dalvik.system.VMRuntime` stubs in `ohbridge_stub.c` cover the natives
that `framework.jar`'s `ActivityThread` bootstrap calls.  Adds
`NativeAllocationRegistry.applyFreeFunction` null-guard so the GC
finalizer trampoline doesn't `BLR x0` through a null function pointer.

The cumulative effect: no class on the boot classpath leaks the sentinel
into dispatch; `framework.jar`'s natives bind cleanly through OHBridge;
`-Xbootclasspath:aosp-shim.dex` works without SIGBUS.

## What this commit (PF-arch-053) adds

No C++/asm/header change.  Documentation + regression test only:

| File | Purpose |
|---|---|
| `aosp-libbinder-port/test/bcp-sigbus-repro.sh` (new) | Acceptance test, three modes |
| `aosp-libbinder-port/m3-dalvikvm-boot.sh` (modified) | `--bcp-shim` and `--bcp-framework` flags |
| `art-latest/patches/PF-arch-053-*.patch` (new) | Trace of resolution + verification artifact |
| `docs/engine/PF-arch-053-NOTES.md` (this file) | Explainer for future agents |

## Acceptance test

```bash
$ bash aosp-libbinder-port/test/bcp-sigbus-repro.sh
[bcp-sigbus-repro] MODE=baseline flags=''
[bcp-sigbus-repro]   PASS
[bcp-sigbus-repro] MODE=bcp-shim flags='--bcp-shim'
[bcp-sigbus-repro]   PASS
[bcp-sigbus-repro] MODE=bcp-framework flags='--bcp-shim --bcp-framework'
[bcp-sigbus-repro]   PASS
[bcp-sigbus-repro] SUCCESS: PF-arch-053 verified — no PathClassLoader-BCP
                  SIGBUS in any mode
```

In each mode HelloBinder reports `PASS` with `listServices() returned 2
entries` (including `westlake.test.echo` from `sm_registrar`),
`getService` returns a non-null `NativeBinderProxy`, and dalvikvm exits 0.

## Regression contract

The following changes should re-run `bcp-sigbus-repro.sh` and confirm
green:

* Adds to `aosp-shim.dex` (especially classes whose names overlap
  `framework.jar`).
* Edits to `scripts/framework_duplicates.txt` (removing entries
  re-exposes the historical fault site).
* Edits to ART runtime patches under `art-latest/patches/runtime/`,
  particularly `class_linker.cc::LinkCode`, `art_method.cc`, and
  anything touching `kPFCutStaleNativeEntry` / `PFCutIsStaleNativeEntry`.
* Edits to OHBridge native-binding registration tables in
  `art-latest/stubs/ohbridge_stub.c` /
  `framework_register_stubs.cpp` / `binder_jni_stub.cc`.

## Files NOT touched

In keeping with the task brief and the parallel W1-B agent's scope:

* `art-latest/Makefile.bionic-arm64` — not edited.
* `art-latest/stubs/binder_jni_stub.cc` — not edited (W1-B's domain).
* Any source under `art-latest/` proper — not edited.
* `shim/java/` — not edited.
* `aosp-libbinder-port/native/` — not edited.

## See also

* `docs/engine/BINDER_PIVOT_DESIGN.md`
* `docs/engine/BINDER_PIVOT_MILESTONES.md`
* `aosp-libbinder-port/M3_NOTES.md` (the note that flagged the
  historical issue)
* `art-latest/patches/runtime/class_linker.cc:169` (sentinel
  definition)
