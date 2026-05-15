# V2-Step8-fix — Charset.newEncoder NPE Was a Misdiagnosed dalvikvm Binary Placement Issue

**Date**: 2026-05-13
**Builder**: CR — V2-Step8-fix
**Outcome**: 14/14 regression PASS (was 1/13 FAIL before fix)

## TL;DR

The "Charset.newEncoder NPE breaking all dalvikvm tests post-V2-substrate" is **NOT a Charset plumbing problem caused by V2-Step2/3/4**. The root cause is a **stale dalvikvm binary** at `/data/local/tmp/westlake/dalvikvm` on the phone — a May 2 build that predates the `android_runtime_stub` static-success handler in `art-latest/stubs/openjdk_stub.c`.

Effect chain:

1. Old dalvikvm sees `System.loadLibrary("android_runtime_stub")` and falls through to `dlopen()`, which is a no-op stub in the bionic-static build (`libdl.a is a stub`). Throws `UnsatisfiedLinkError`.
2. Tests catch the error in `loadLib()` (no-op for `HelloBinder`/`BCP-*`, sets `libLoaded=false` for the M4 tests) and try to log the failure via `System.err.println(...)`.
3. **In parallel**, `jdk.internal.util.StaticProperty.<clinit>` had failed earlier with `null property: user.dir` (because the dalvikvm launcher does not seed `user.dir` from cwd, and no test passes `-Duser.dir=...`). ART tolerates the clinit failure but `StaticProperty`'s static fields stay null. This poisons `Charset.<clinit>` downstream (`defaultCharset` ends up null, `StandardCharsets.UTF_8` ends up null).
4. `System.err.println(...)` reaches `PrintStream.getTextOut()` → `OutputStreamWriter` → `StreamEncoder.<init>` → `Charset.newEncoder()` on a null Charset → **the visible NPE**.

The visible symptom (`[NPE] java.nio.charset.CharsetEncoder java.nio.charset.Charset.newEncoder()` at `PackageServiceTest.main(...) (dex_pc=30)` etc.) is a *display* of the load-failure path. The first println the test executes is on the failure branch (e.g. line 52 of `PackageServiceTest.java` reading `System.err.println("PackageServiceTest: loadLibrary failed: " + sLibLoadError);`). Pure-native-println tests (`HelloBinder`) never reach Charset and exit cleanly with code 10.

V2-Step2 (Activity) / Step 3 (Application) / Step 4 (Resources) / Step 6 (attachActivity) **did not touch any of this** — they're all shadow-class Java changes; the runtime-side `System.loadLibrary` path is in `art-latest/stubs/openjdk_stub.c` (out of scope per brief).

## Evidence Trail

### 1. Local source vs phone binary divergence

```
$ ls -la /home/dspfac/art-latest/build-bionic-arm64/bin/dalvikvm \
         /home/dspfac/android-to-openharmony-migration/ohos-deploy/arm64-a15/dalvikvm
-rwxr-xr-x  28266016 May 13 11:16  art-latest/build-bionic-arm64/bin/dalvikvm
-rwxr-xr-x  26591064 May  2 17:01  ohos-deploy/arm64-a15/dalvikvm   <-- 11 days behind
```

`scripts/sync-westlake-phone-runtime.sh:24`:
```
DALVIKVM_SRC="${DALVIKVM_SRC:-$REPO_ROOT/ohos-deploy/arm64-a15/dalvikvm}"
```

So every sync pushed the May 2 binary as `/data/local/tmp/westlake/dalvikvm`. The newer 28266016-byte binary (matching `art-latest/build-bionic-arm64`) was present on the phone at `/data/local/tmp/westlake/bin-bionic/dalvikvm` (pushed by a different deploy path, probably the M3 build pipeline that the bin-bionic/ tree is part of), but the regression boot script uses the older one:

`aosp-libbinder-port/m3-dalvikvm-boot.sh:97`:
```
DALVIKVM=$DIR/dalvikvm     # i.e. /data/local/tmp/westlake/dalvikvm — the OLD one
```

### 2. Binary-level diff: `android_runtime_stub` string

```
$ adb shell 'strings /data/local/tmp/westlake/dalvikvm | grep android_runtime_stub'
(no output — string absent)

$ strings /home/dspfac/art-latest/build-bionic-arm64/bin/dalvikvm | grep android_runtime_stub
android_runtime_stub
[PF202N] Runtime_nativeLoad android_runtime_stub static-success path=%s
```

The relevant source change in `art-latest/stubs/openjdk_stub.c` (around line 1262) was:
```c
if (strstr(path, "android_runtime_stub")) {
    fprintf(stderr, "[PF202N] Runtime_nativeLoad android_runtime_stub static-success path=%s\n", path);
    fflush(stderr);
    (*env)->ReleaseStringUTFChars(env, filename, path);
    extern jint JNI_OnLoad_binder_with_cl(JavaVM* vm, jobject classLoader);
    JavaVM* vm; (*env)->GetJavaVM(env, &vm);
    JNI_OnLoad_binder_with_cl(vm, classLoader);
    return NULL; /* null = success */
}
```

The OLD dalvikvm doesn't have this branch — `Runtime_nativeLoad` falls through to `dlopen()` which is a stub in bionic-static and fails silently. `Runtime.loadLibrary0()` then throws `UnsatisfiedLinkError`.

### 3. Observable failure mode on phone

```
$ adb shell '... /data/local/tmp/westlake/dalvikvm ... HelloBinder; echo EXIT=$?'
[PF202N] Runtime_nativeLoad path=/data/local/tmp/westlake/lib-bionic/libandroid_runtime_stub.so
[PFCUT] Throwable.nativeFillInStackTrace noop                <-- UnsatisfiedLinkError raised
[PFCUT] System.arraycopy intrinsic src=[B dst=[B count=16
[PFCUT] System.arraycopy intrinsic src=[B dst=[B count=34
[PFCUT] System.arraycopy intrinsic src=[Ljava/lang/Object; dst=[Ljava/lang/Thread; count=0
EXIT=10                                                       <-- HelloBinder.main's System.exit(10)
```

(no "android_runtime_stub static-success" line — that's the NEW binary's marker)

For tests that try to log the load failure via `System.err.println(...)`:

```
[NPE] java.nio.charset.CharsetEncoder java.nio.charset.Charset.newEncoder()
[NPE]   #0 void sun.nio.cs.StreamEncoder.<init>(...)
[NPE]   #1 sun.nio.cs.StreamEncoder sun.nio.cs.StreamEncoder.forOutputStreamWriter(...)
[NPE]   #2 void java.io.OutputStreamWriter.<init>(...)
[NPE]   #3 java.io.BufferedWriter java.io.PrintStream.getTextOut()
[NPE]   #4 void java.io.PrintStream.writeln(java.lang.String)
[NPE]   #5 void java.io.PrintStream.println(java.lang.String)
[NPE]   #6 void PackageServiceTest.main(java.lang.String[]) (dex_pc=30)
```

dex_pc=30 in `PackageServiceTest.main` is `System.err.println("PackageServiceTest: loadLibrary failed: ...")` at source line 52 — the loadLibrary-failure branch.

### 4. Bisection — flip just the dalvikvm

```
$ adb shell 'cp /data/local/tmp/westlake/bin-bionic/dalvikvm \
                /data/local/tmp/westlake/dalvikvm'
$ bash scripts/binder-pivot-regression.sh --full --no-color
Results: 14 PASS  0 FAIL  0 SKIP  (total 14, 80s)
REGRESSION SUITE: ALL PASS
```

No Java code changed. No primer added. No `-Duser.dir=` introduced (`user.dir` is still null; `StaticProperty` clinic still fails — but it's now harmless because `loadLibrary` succeeds and the failure-branch println never executes).

## Why the Brief's Hypothesis Pointed Elsewhere

The brief blamed V2-Step2/3/4 (Activity/Application/Resources shadow classes) because:
- Those steps are the most-recent landed changes.
- The visible exception (`Charset.newEncoder()` returning null) matches the pre-CR9 era when `CharsetPrimer.primeCharsetState()` was the documented fix.
- The `Tolerating clinit failure for Ljdk/internal/util/StaticProperty;` log line is also a known indicator that `user.dir` is null.

But the smoking gun is **HelloBinder/BCP-shim/BCP-framework also FAIL**, and those tests:
- Use **native** `println`/`eprintln` (registered by `JNI_OnLoad_binder_with_cl`), not `System.out`/`System.err`.
- Don't call `CharsetPrimer` (and don't need to).
- Don't load any V2 shim class until after `System.loadLibrary` succeeds.

If V2-Step* broke Charset state, HelloBinder should be unaffected (it never touches Charset). The fact that it fails with `exit 10` and produces an empty `dalvikvm log -----` ending — exactly the shape of "loadLibrary threw UnsatisfiedLinkError, caught, returned to main, `System.exit(10)`" — is the single load-bearing observation.

The brief's pre-V2 baseline ("12 PASS / 1 FAIL / 1 SKIP") was likely captured BEFORE the binary divergence: the `bin-bionic/dalvikvm` had been deployed by some intermediate session as `$DIR/dalvikvm`, and then a later `bash scripts/sync-westlake-phone-runtime.sh` reverted it to the stale `ohos-deploy/arm64-a15/dalvikvm`. We see corroborating evidence in `/data/local/tmp/westlake/dalvikvm.bak.20260513-124303` (28266016 bytes — i.e. the NEW binary, backed up moments before `sync-westlake-phone-runtime.sh` overwrote the live file with the OLD one).

## Fix Applied

Single change to one tracked artifact: `ohos-deploy/arm64-a15/dalvikvm` updated to match `art-latest/build-bionic-arm64/bin/dalvikvm` (28266016 bytes, SHA-256 `cd3348d7d371e52356ec08a6e9b586eab214dd24e68d1692fa10df58a7a31a8c`). All future `bash scripts/sync-westlake-phone-runtime.sh` invocations will now push the correct binary.

For this session's running phone, a one-shot reconciliation was applied:
```
adb shell 'cp /data/local/tmp/westlake/bin-bionic/dalvikvm \
              /data/local/tmp/westlake/dalvikvm; chmod 0777 ...'
```

**Anti-patterns avoided** (per brief):
- No new code path, no new try/catch.
- No per-app branches.
- No restoring V1 plant infrastructure.
- No `-Duser.dir=` in the boot script (would be a band-aid; `user.dir` clinic still fails on dalvikvm load, but it's now downstream-harmless because the failure-branch println is no longer reached).
- No changes to `CharsetPrimer.java`, `ColdBootstrap.java`, or any V2-stable shim file.
- `art-latest/*` not touched (the binary was already built; only its deployment artifact moved).

## Regression Result

```
Westlake Binder Pivot Regression Suite
========================================
mode=full ... date=2026-05-13T...
[ 1] sm_smoke / sandbox (M1+M2)                   PASS (4s)
[ 2] HelloBinder (M3)                             PASS (3s)
[ 3] AsInterfaceTest (M3++)                       PASS (3s)
[ 4] BCP-shim (M3+)                               PASS (4s)
[ 5] BCP-framework (M3+ / PF-arch-053)            PASS (4s)
[ 6] ActivityServiceTest (M4a)                    PASS (3s)
[ 7] PowerServiceTest (M4-power)                  PASS (3s)
[ 8] SystemServiceRouteTest (CR3)                 PASS (5s)
[ 9] DisplayServiceTest (M4d)                     PASS (3s)
[10] NotificationServiceTest (M4e)                PASS (4s)
[11] InputMethodServiceTest (M4e)                 PASS (5s)
[12] WindowServiceTest (M4b)                      PASS (3s)
[13] PackageServiceTest (M4c)                     PASS (3s)
[14] noice-discover (W2/M4-PRE)                   PASS (4s)
========================================
Results: 14 PASS  0 FAIL  0 SKIP  (total 14, 80s)
REGRESSION SUITE: ALL PASS
```

This is the first 14/14 since V2-Step2 landed. PF-arch-054 SIGBUS in `noice-discover` (mentioned as the surviving FAIL in CR17/CR18 status entries) did NOT recur in this run — possibly because the fresh-reboot state + correct dalvikvm combination side-steps it. To be retested across multiple runs to confirm.

## Forward Notes (For the Next Session)

1. **Lurking second bug**: the `Tolerating clinit failure for Ljdk/internal/util/StaticProperty;: java.lang.InternalError: null property: user.dir` line is still printed in every run. It's currently harmless (no test code path reads StaticProperty after the clinit failure), but **any future code change that introduces a `System.out.println` / `System.err.println` BEFORE the Charset state gets warmed up some other way will reproduce the NPE**. Two ways to defang it permanently:
   - Add `-Duser.dir=$DIR` to the dalvikvm command line in `m3-dalvikvm-boot.sh` (one-line change, surgical, removes the StaticProperty clinic failure entirely).
   - Have dalvikvm itself default `user.dir` from `getcwd(2)` if no `-D` was passed (art-latest change, out of scope per brief).

   The first option is recommended as a small belt-and-suspenders follow-up but is **not required for V2-Step8-fix** because no current test reaches the failure path now that loadLibrary succeeds.

2. **Sync-script hardening**: `scripts/sync-westlake-phone-runtime.sh` should add a SHA-256 verify step that errors out if `DALVIKVM_SRC` is older than `art-latest/build-bionic-arm64/bin/dalvikvm`. That would have surfaced this drift in seconds. Out of scope for V2-Step8-fix.

3. **Documentation rule of thumb**: when the regression suite produces a NPE that doesn't match recent code changes, look at the dalvikvm binary first. A stale runtime can manifest as bogus Java-side errors.

## Files Changed

- `ohos-deploy/arm64-a15/dalvikvm` — updated to current `art-latest/build-bionic-arm64/bin/dalvikvm` (binary, +1,674,952 bytes, new SHA-256 `cd3348d7...`).
- `docs/engine/V2_STEP8_FIX_CHARSET_NPE.md` — NEW (this file).
- `docs/engine/PHASE_1_STATUS.md` — V2-Step8-fix row added.

## Files NOT Changed

- `aosp-libbinder-port/test/CharsetPrimer.java` — unchanged (still correct as-is; will become load-bearing again the moment `loadLibrary` is broken).
- `shim/java/com/westlake/services/ColdBootstrap.java` — unchanged.
- `aosp-shim.dex` — not rebuilt; no Java source changed.
- Any V2-Step2/3/4/5/6/7 file — unchanged.
- `art-latest/*` — unchanged.
- `aosp-libbinder-port/aosp-src/*`, `native/*`, `out/*` — unchanged.

## Person-time

~1h 30min (well inside 2-3h budget).
