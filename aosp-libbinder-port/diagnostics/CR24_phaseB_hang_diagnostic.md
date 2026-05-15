# CR24 — Diagnose + fix noice PHASE B "hang" (recurring PF-arch-054 SIGBUS)

**Date:** 2026-05-13
**Phone:** OnePlus 6 (serial `cfb7c9e3`), LineageOS 22 (Android 15)
**Author:** Builder (CR24 agent)
**Status:** done — fix landed, 14/14 regression PASS (noice-discover now PASS)
**Parent brief:** CR24 — Diagnose + fix noice PHASE B classload hang
**Authoritative refs:**
- `diagnostics/CR13_phaseB_sigbus_report.md` — original PF-arch-054 root-cause analysis (recommended fix path #1 is exactly what CR24 applies)
- `diagnostics/CR18_primer_sigbus_bisection.md` — proved primer is not load-bearing for SystemServiceRouteTest
- `art-latest/patches/runtime/runtime.cc:2750-2765` — CR15's widen-the-guard patch (the lambda body)
- `M4_DISCOVERY.md` §42 (CR13) + §44 (CR15) + §46 (CR17)

---

## 0. Headline

The "PHASE B classload hang" described in the CR24 brief is **NOT a Java
static-init deadlock or an infinite loop** — it is the **same
PF-arch-054 SIGBUS at `0xfffffffffffffb17` documented in CR13**, but
**observed during the kernel signal-handler dump phase** rather than at
the SIGBUS instant.

- The crashing instruction is still `br x?` to the
  `kPFCutStaleNativeEntry` sentinel `0xfffffffffffffb17`.
- The crashing Java frame is still `BaseDexClassLoader.toString` invoked
  via `String.valueOf(noicePCL)` from
  `NoiceDiscoverWrapper.phaseB_classLoad:329`.
- The reason the brief reported "no SIGBUS, just a hang" is that the
  signal handler's stack-walker + memory-map dumper writes ~80 lines of
  output and triggers extensive kernel speculative-fault thrashing in
  the JIT/code pages while doing so (the simpleperf trace below shows
  ~50% of the spinning thread's cycles in
  `do_el0_ia_bp_hardening` / `__handle_speculative_fault` /
  `handle_pte_fault`).  On the previous session the user killed the
  process after 13 minutes because they thought it had hung; this
  session the dumper completed in ~18 seconds and the process exited
  cleanly with "Bus error".

**Fix applied (CR13 fix path #1):**
`NoiceDiscoverWrapper.java:329` no longer concatenates `noicePCL` into
its log string.  Instead it logs `System.identityHashCode(noicePCL)` +
`noicePCL.getClass().getName()`, neither of which invokes the patched
`toString` JNI lambda.  No other call site in the harness stringifies
the loader.  This sidesteps the corrupted `NewStringUTF` slot entirely.

**Result:**
- noice-discover.sh now reaches **PHASE G4** (the pre-reboot peak).
- Regression suite: **14/14 PASS** (previously 13/13 PASS + 1 noice FAIL).

The underlying art-latest bug (the `NewStringUTF` slot getting
overwritten with `kPFCutStaleNativeEntry` along the noice-discover
control-flow before the patched lambda body's guard runs) is **NOT
fixed by CR24** — the lambda body itself is fine; the corruption
happens earlier in the JNI trampoline.  CR24 sidesteps the corrupted
path; a follow-up CR can tackle the substrate bug if/when it bites a
different call site.  Per CR13's recommended fix path #1 plan, this is
the accepted resolution shape.

---

## 1. Reproduction transcript (pre-fix)

```
$ ADB shell "su -c 'bash /data/local/tmp/westlake/bin-bionic/noice-discover.sh'"
# ... PHASE A (70 probes, 7 hits) finishes cleanly ...
# === PHASE B: classload noice from /data/local/tmp/westlake/com_github_ashutoshgngwr_noice.apk ===
# [PFCUT] String.split intrinsic delimiter=58 limit=0 pieces=1
# ... ziparchive opens noice.apk + checks for classes2.dex (NOT found, correct: noice has no classes2.dex) ...
# ziparchive: Closing archive 0x700b611720
# [PFCUT] UnixFileSystem.getBooleanAttributes intrinsic path=/data/local/tmp/westlake/lib-bionic attrs=0x5
# *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** ***
# Fatal signal 7 (SIGBUS), code 1 (BUS_ADRALN) fault addr 0xfffffffffffffb17
# OS: Linux 4.9.337-g2e921a892c03 (aarch64)
# Cmdline: <unset>
# Thread: 9530 "main-256mb"
# Registers:
#      x0: 0x000000000000000b     x1: 0x0000006ff46a3d50     x2: 0x0000006ff46a3dd0     x3: 0x0000000000000000
#      ...
#     x15: 0x000000700ba118d1
#     x16: 0x00000000006744b4   <-- +loader_to_string JNI lambda entrypoint
#      ...
#      sp: 0x0000006ff46a3d50     pc: 0xfffffffffffffb17  <-- kPFCutStaleNativeEntry sentinel
#
# [PFCUT-SIGNAL] top_quick_method=0x700ba118d0 native=1 quick=0xd8f400 jni=0x6744b4 java.lang.String dalvik.system.BaseDexClassLoader.toString()
#   at dalvik.system.BaseDexClassLoader.toString(Native method)
#   at dalvik.system.BaseDexClassLoader.toString(Native method)
#   at java.lang.String.valueOf(String.java:4102)
#   at java.lang.StringBuilder.append(StringBuilder.java:179)
#   at NoiceDiscoverWrapper.phaseB_classLoad(NoiceDiscoverWrapper.java:329)
#   at NoiceDiscoverWrapper.main(NoiceDiscoverWrapper.java:210)
# ... ~80 lines of /proc/<pid>/maps dump ...
# Fault message:
# Bus error
```

## 2. Why the brief described it as a "hang"

Live process inspection during the dump phase confirmed the brief's
observation that the thread named `main-256mb` is `STAT=R / WCHAN=0`
spinning in userspace.  simpleperf trace (5s sample, 16,056 events on
the running thread):

```
Overhead  Symbol
24.83%    [kernel.kallsyms]  do_el0_ia_bp_hardening
20.32%    [kernel.kallsyms]  __handle_speculative_fault
19.33%    unknown            unknown[+75a8c057a0]
15.84%    [kernel.kallsyms]  handle_pte_fault
 6.57%    [kernel.kallsyms]  do_mem_abort
 4.25%    [kernel.kallsyms]  get_vma
 3.83%    [kernel.kallsyms]  do_page_fault
```

This is **kernel page-fault thrashing**, NOT a Java busy-loop.  The
ARM64 branch-predictor hardening kicks in for every instruction abort,
which is happening continuously as ART's signal-handler stack walker
unwinds through patched JNI entries.

A second simpleperf trace (2s, with DWARF callchain unwinding) showed
the smoking gun:

```
__kernel_rt_sigreturn
  -> art::HandleUnexpectedSignalLinux
  -> art::HandleUnexpectedSignalCommon
  -> art::HandleUnexpectedSignalCommonDump
  -> art::UContext::Dump
  -> android::base::StringPrintf
  -> vsnprintf
  -> fwrite (write)
  -> ext4_file_write_iter   <-- the dump is mid-write to noice-discover.log
```

So the thread IS in userspace, but it's in **ART's signal handler dump
path**, NOT in any Java method.  The "hang" is the dumper grinding
through the post-fault stack walk + memory-map enumeration.  In the
previous session that took >13 minutes (kernel TLB/MM pressure on the
JIT pages) and the user killed it before completion; in CR24's session
it took 18 seconds and exited with "Bus error".

This is consistent with CR13 H8: the lambda's `cbz x2` null-check
passes the sentinel because the sentinel != 0.  CR15 widened the guard
in the **lambda body** to also reject the sentinel — but the SIGBUS
crash happens **before** the lambda body runs, in the quick-JNI
trampoline's `br x?` dispatch.  The corruption is in the
JNINativeInterface function-table slot for `NewStringUTF` itself, not
in the loader_to_string code.

## 3. Why CR24 doesn't fix the art-latest bug

The corruption mechanism is documented in CR13 §3 (registers at fault
time) and CR13 §5 (H8 hypothesis confirmed).  CR15's widen-the-guard
patch is in the right shape but applies to the **return path** of
loader_to_string — after the broken slot has already been read into
`x2` (or wherever the ABI puts the function pointer) and `br` has
dispatched.  The patch would prevent a second-order crash if the
trampoline ever fell through to the lambda body with the sentinel in
hand, but in practice it never gets that far.

A full fix would require either:

- **Patching the `art_quick_generic_jni_trampoline` assembly**
  (`runtime/arch/arm64/quick_entrypoints_arm64.S`) to validate
  `NewStringUTF` before branching, OR
- **Replacing the lambda's return path** with a direct
  `art::mirror::String::AllocFromModifiedUtf8` call (no JNI dispatch),
  OR
- **Identifying WHO writes `kPFCutStaleNativeEntry` into the JNI
  function table** and preventing it (this is the open mystery from
  CR15 — the handoff notes mention `hw watchpoint on 0xfad8f8` as a
  candidate diagnostic).

All three require touching `art-latest/*`, which is explicitly out of
scope for CR24 ("don't rebuild dalvikvm unless absolutely necessary"
+ "art-latest/* is W1-B's concurrent scope").  Per CR13's fix-path
priority list, **path #1 (sidestep the corrupted call site) is the
correct CR24 response** and is what landed.

## 4. Fix detail

`aosp-libbinder-port/test/NoiceDiscoverWrapper.java:325-345` — replace
the one offending stringification call site:

```java
// BEFORE (CR23 and earlier):
noicePCL = new PathClassLoader(APK_PATH,
        NoiceDiscoverWrapper.class.getClassLoader());
println("PHASE B: PathClassLoader created: " + noicePCL);
// ^^^ "+ noicePCL" forces String.valueOf(noicePCL) -> patched
//     BaseDexClassLoader.toString JNI lambda -> SIGBUS at PC=sentinel.

// AFTER (CR24, 2026-05-13):
noicePCL = new PathClassLoader(APK_PATH,
        NoiceDiscoverWrapper.class.getClassLoader());
println("PHASE B: PathClassLoader created (identity hash=0x"
        + Integer.toHexString(System.identityHashCode(noicePCL))
        + ", class=" + noicePCL.getClass().getName() + ")");
// ^^^ System.identityHashCode + Class.getName are both intrinsic and
//     do NOT invoke the patched toString JNI lambda.
```

The replacement preserves log fidelity: the identity hash code uniquely
identifies the loader instance for cross-line correlation, and
`getClass().getName()` gives the loader's runtime type.  No other call
site in the discovery harness stringifies the loader.

Files touched:
- `aosp-libbinder-port/test/NoiceDiscoverWrapper.java` (one ~5-line
  replacement at line 329; verbose CR24 comment justifying the
  workaround)

Files rebuilt:
- `aosp-libbinder-port/out/NoiceDiscoverWrapper.dex` (32 KB) — pushed
  to `/data/local/tmp/westlake/dex/NoiceDiscoverWrapper.dex`.

Files NOT touched (per CR24 scope):
- `art-latest/*` — the underlying corruption mechanism remains
  unaddressed (see §3).  Future work.
- `shim/java/com/westlake/services/*` — the M4 services are
  production-self-sufficient (CR17) and this hang is in the harness
  flow, not in any service.
- `aosp-shim.dex` — unchanged (1.38 MB, post-CR19 build).
- Boot scripts, BCP, framework.jar — all unchanged.
- `shim/java/com/westlake/engine/WestlakeLauncher.java` — CR23-fix is
  actively editing (parallel agent).

## 5. Acceptance evidence

### 5.1 Post-fix discovery transcript (phases reached)

```
$ ADB shell "su -c 'grep -oE \"PHASE [A-Z][0-9]?\" /data/local/tmp/westlake/noice-discover.log | sort -u'"
PHASE A
PHASE B
PHASE C
PHASE D
PHASE E
PHASE F
PHASE G
PHASE G2
PHASE G3
PHASE G4
```

All 10 phases reached.  PHASE G4 is the pre-reboot peak from the
project's previous best run.

### 5.2 Phase outcomes

```
Phase outcomes:
  PHASE A: 7/70 services resolved
  PHASE B: 5/7 classes loadable
  PHASE C: PASSED — NoiceApplication instantiated
  PHASE D: PASSED (attachBaseContext with WestlakeContextImpl)
  PHASE E: PASSED unexpectedly
  PHASE F: drove 5 framework Singletons, 1 failed
  PHASE G2: PASSED — MainActivity instantiated via Instrumentation.newActivity
  PHASE G3: PASSED — Activity.attach succeeded
  PHASE G4: FAILED (expected — diagnoses needed service)
```

PHASE G4 fails at the expected discovery boundary:

```
cause[1]: java.lang.NullPointerException: Attempt to invoke InvokeType(2)
  method 'void android.content.res.Configuration.setTo(android.content.res.Configuration)'
  on a null object reference
```

This is a fresh M4-PRE15 candidate (Configuration.setTo on a null
target) and is outside the CR24 scope.  Discovery reaching this point
**is the success criterion**.

### 5.3 Regression suite

```
$ scripts/binder-pivot-regression.sh --full
[ 1] sm_smoke / sandbox (M1+M2)                   PASS ( 4s)
[ 2] HelloBinder (M3)                             PASS ( 4s)
[ 3] AsInterfaceTest (M3++)                       PASS ( 3s)
[ 4] BCP-shim (M3+)                               PASS ( 4s)
[ 5] BCP-framework (M3+ / PF-arch-053)            PASS ( 5s)
[ 6] ActivityServiceTest (M4a)                    PASS ( 3s)
[ 7] PowerServiceTest (M4-power)                  PASS ( 3s)
[ 8] SystemServiceRouteTest (CR3)                 PASS ( 5s)
[ 9] DisplayServiceTest (M4d)                     PASS ( 4s)
[10] NotificationServiceTest (M4e)                PASS ( 4s)
[11] InputMethodServiceTest (M4e)                 PASS ( 4s)
[12] WindowServiceTest (M4b)                      PASS ( 3s)
[13] PackageServiceTest (M4c)                     PASS ( 3s)
[14] noice-discover (W2/M4-PRE)                   PASS ( 6s)

Results: 14 PASS  0 FAIL  0 SKIP  (total 14, 85s)
REGRESSION SUITE: ALL PASS
```

**14/14 PASS** (previously 13/13 PASS + 1 noice FAIL).

## 6. Person-time

- 15 min — read brief, MEMORY.md, handoff, CR13/CR18, NoiceDiscoverWrapper.java
- 25 min — reproduce hang; diagnose with ps/wchan/strace/simpleperf;
  confirm signal-handler dump is what looked like a hang; correlate
  PC=0xfffffffffffffb17 with CR13's PF-arch-054 SIGBUS pattern.
- 5 min — apply 5-line fix in NoiceDiscoverWrapper.java per CR13 path #1.
- 5 min — rebuild + push NoiceDiscoverWrapper.dex.
- 10 min — verify noice-discover reaches PHASE G4 + regression 14/14 PASS.
- 15 min — write this diagnostic + update M4_DISCOVERY + PHASE_1_STATUS.

Total: ~1h 15m.

## 7. Open questions / follow-ups

1. **Underlying art-latest bug** (NewStringUTF slot corruption) remains
   open.  CR24 does NOT address it.  Reproducer: revert this fix, rerun
   `noice-discover.sh`, observe SIGBUS at PC=sentinel.  Next agent
   could pick up the CR15 thread: hw watchpoint on the
   JNINativeInterface table slot for `NewStringUTF` to identify the
   writer.

2. **PHASE G4 NPE on Configuration.setTo(null)** is a fresh
   M4-PRE15-or-later finding.  Likely requires planting a synthetic
   `mConfiguration` on `Activity` (via Unsafe + reflective field set),
   same pattern as M4-PRE12/13/14 plants of AssetManager / LocaleList /
   ResourcesImpl on the synthetic Resources path.  Out of scope for CR24.

3. **`String.valueOf(loader)` is now a known risk surface throughout
   the codebase.**  Any shim code that does string concatenation with a
   loader will trip the same fault.  Recommend an audit pass —
   `grep -rn "+ .*PathClassLoader\|+ .*ClassLoader\|+ .*loader" shim/`
   — but defer until and unless another path bites.  CR24's harness
   was the only known hot site.
