# CR60 E12 Stage 2 Checkpoint — DexClassLoader gap (2026-05-15 00:44 PT)

## What works (E12 smoke, committed `442e312e`)

E12 stage 1 PASSES end-to-end: a real Android Activity from
`:hello-color-apk` (loaded via -Xbootclasspath, NOT DexClassLoader) runs
under `dalvikvm-arm32-dynamic` and its ColorView.onDraw output reaches
the DAYU200 DSI panel via libdrm_inproc_bridge.so. Markers
`inproc-app-launcher present rc=0 ... fill=argb` + `HelloColorApk.onCreate
reached pid=15083` confirm the View → SoftwareCanvas → JNI → DRM
pipeline is intact.

## What blocks stage 2 (DexClassLoader → noice/McD)

The brief asks E12 final to demonstrate APK loading via DexClassLoader so
arbitrary APKs (noice, McD, …) can be loaded at runtime without rebuilding
the BCP. The InProcessAppLauncher.java I wrote is DexClassLoader-shaped
(documented as such in its header), but the underlying primitive is
non-functional in this codebase.

### Sub-step reached

Reading `shim/java/dalvik/system/BaseDexClassLoader.java` to wire the
launcher's stage 2.

### Exact blocker

`shim/java/dalvik/system/BaseDexClassLoader.findClass(String name)`
(lines 31-50) does NOT actually load classes from the dexPath. Its body
delegates to the parent classloader and `Class.forName`, with a
literal comment:

```java
// In the engine model, all DEX files are on the boot classpath.
// Try Class.forName with the parent loader first.
```

`dalvik.system.DexClassLoader` derives from this and is functionally a
no-op for actually opening a dex file. The runtime side
(`dvmRawDexFileOpen` / `dvmDexClassLookup`) is presumably wired in
the dalvik VM, but the BaseDexClassLoader shim never calls into it —
it short-circuits to the parent before any dex-file open happens.

### Why this blocks E12 stage 2

The brief's stretch goal ("point the launcher at noice") requires
loading the noice APK at runtime — it cannot be pre-installed on BCP
(the McD APK alone is 185 MB; the BCP is loaded at VM init). Without
a functioning DexClassLoader, the only path to load a runtime APK
through this codebase's existing shim is to either:

  (a) Fix the BaseDexClassLoader.findClass shim to actually parse the
      dex file and lookup classes through it. Requires either:
       - native JNI wrappers around `dvmRawDexFileOpen` and the
         class-lookup machinery, OR
       - a Java-side dex-to-bytecode walker.
      Estimated 1-2 days for either. Out of scope for E12 smoke
      timebox.

  (b) Use Westlake-host's ApkLoader pattern (the binder-pivot V2
      substrate has its own classloader work at
      `shim/java/android/app/ApkLoader.java`, currently uncommitted in
      this tree from agent 14's V2 work). Pulling that into the
      OHOS Phase 2 codebase is the BIG architectural move documented
      in `CR41_PHASE2_OHOS_ROADMAP.md` — months not hours.

## Hypothesis

The simplest path forward for noice/McD on OHOS is NOT to fix
DexClassLoader. It's to:

  1. Pre-process the noice APK on the host: extract classes.dex, run
     `dx --dex` to convert it to a dex.035 format dalvik-kitkat can
     consume, and pre-stage it on the board next to aosp-shim-ohos.dex.
  2. Push the noice classes.dex to /data/local/tmp/westlake/bcp/.
  3. Pass it on -Xbootclasspath at VM init time, same way :hello-color-apk
     rides today.
  4. Drive its MainActivity through the same InProcessAppLauncher.

This is "in-process loading without DexClassLoader" — it's how MVP-1's
:trivial-activity, MVP-2's :red-square, E9b's :hello, and now E12
stage 1's :hello-color-apk all work. The cost is rebuild-on-change
(can't hot-swap APKs without restarting dalvikvm), but for the gate
"prove ONE real Activity pixel reaches the panel from noice" that's
acceptable.

## Recommendation: proceed to stage 2 via the BCP shortcut

Option (a) — fix BaseDexClassLoader — is the right long-term answer but
costs ~2 days and was not in this session's gate. Option (c) — BCP
shortcut — should reach a noice MainActivity.onCreate marker in ~1 day
if the existing CR59 Hilt fixes hold up on OHOS (they're bitness-neutral
per E2; the OHOS BCP has the same aosp-shim-ohos.dex and core-android-x86
the Westlake-host V2 substrate uses on phone).

## Files

- Working stage-1 commit: `442e312e` (this session's E12 smoke PASS).
- Driver subcommand: `bash scripts/run-ohos-test.sh --arch arm32 inproc-app`
  takes an optional `<pkg>/<Activity>` arg (defaults to
  `com.westlake.ohostests.helloc/.MainActivity`). For stage 2 / noice,
  the subcommand would need:
    - a `--apk <path>` arg that pushes the dex to board + appends to BCP,
    - documentation of which CR59-era runtime patches the OHOS-side dalvikvm
      needs (Hilt-startup, ICU-clinit). Per memory notes those are in
      Westlake-host V2 already and should port cleanly.
- Reference: `aosp-libbinder-port/test/NoiceProductionLauncher.java`
  documents the Android-phone heavyweight noice launcher pattern.
  Most of its logic is binder/lifecycle/intent-rewrite — NOT DexClassLoader.
  The DexClassLoader call in that file is the same shim-no-op pattern, so
  Westlake-host either pre-stages the dex in BCP or has a separate
  classloader path I haven't traced.

## Next-agent action

  1. Continue from commit `442e312e`. Don't redo E11 or E12 smoke.
  2. Decide path: fix DexClassLoader (option a, 1-2 days) OR BCP shortcut
     (option c, ~1 day) OR pull V2-substrate ApkLoader (option b, weeks).
  3. If option c: extract noice APK's classes.dex, dx-convert it to .035,
     stage on board, point inproc-app at it. Catch the first
     CR59-style blocker.

## Self-audit at checkpoint

  - no Unsafe.allocateInstance / setAccessible in any of this session's code
  - no per-app branches anywhere; InProcDrawSource is a generic interface
  - no setenforce 0 / chmod hacks
  - all native code uses intptr_t / size_t
  - both E11 commit + E12 smoke commit pass full regression
  - this checkpoint is its own .md file, NOT bundled into any commit yet
