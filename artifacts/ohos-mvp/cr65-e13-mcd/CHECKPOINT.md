# CR65 E13 McD Spike — STOPPED at tooling pre-flight (2026-05-15)

Agent 23. Spike bound: 1 day / 4-hour cap. Actual elapsed: ~30 min before
hard tooling block surfaced; remainder spent diagnosing + checkpointing.

## Outcome: NO McD stage reached (not even Stage A)

| Stage | noice (cr64 baseline) | McD (cr65 attempt) |
|---|---|---|
| A (dex visible) | PASS | **BLOCKED** (d8 rejects 039 input at min-api 13) |
| B (Application.onCreate) | PASS | **BLOCKED** |
| C (Activity.onCreate) | PASS | **BLOCKED** |
| D (pixel pushed) | PASS | **BLOCKED** |

McD never reached the on-device run: the host-side d8 redex step fails
during the very first `[C2/H]` pipeline stage, so nothing was ever pushed
or executed.

## Why STOP per the brief

The McD APK has TWO structural mismatches with the noice-proven pipeline,
both pre-execution (host-side, before any board contact). The brief's
hard constraint says: "If McD ends up needing major new infrastructure
→ STOP and checkpoint." Each blocker below is exactly that kind of
infrastructure work.

### Blocker 1: McD ships dex format 039 (Android 14+); dalvik-kitkat speaks 035/036/038 only

All 33 of McD's `classes*.dex` files carry the magic `dex\n039\0`. The
noice pipeline's `[C2/H]` step runs:
```
d8 --min-api 13 --release --output … classes*.dex
```
d8 at `--min-api 13` flatly refuses dex-039 input:
```
Error: com.android.tools.r8.internal.Sb: Dex file with version '39' cannot be used with min sdk level 'H_MR2'.
```
This is a host-side d8 contract check, not an on-device runtime check.
Bumping to `--min-api 28` lets d8 consume the input — but it then EMITS
dex-039, which dalvik-kitkat rejects at the libdex magic-bytes gate
inside `DexSwapVerify.cpp:2808-2810`:
```
if ((memcmp(version, DEX_MAGIC_VERS, 4) != 0) &&            // 036
        (memcmp(version, DEX_MAGIC_VERS_API_13, 4) != 0) && // 035
        (memcmp(version, DEX_MAGIC_VERS_038, 4) != 0)) {    // 038
    ALOGE("ERROR: unsupported dex version …");
    return false;
}
```
This swap-phase check runs unconditionally — `-Xverify:none
-Xdexopt:none` do not bypass it. noice slips through because R8 emitted
its `classes.dex` at version **035** (the AppCompat-derived `noice 2.5.7`
build chain caps dex format at 035 for backwards compat); McD's
toolchain (`compileSdk=35`, `targetSdkVersion=35`, modern R8) emits 039
unconditionally.

The clean fix paths, none under 4 hours:
1. **apktool full-disassemble → re-assemble at api-level 13** — already
   have `artifacts/real-mcd/apktool_decoded/` with 119,275 smali files
   pre-extracted (per `smali_classes*/`). `apktool b --api-level 13`
   may emit 035 if no API-26+ opcodes are present, but the McD smali tree
   needs to be vetted (no `invoke-polymorphic` / `invoke-custom` / etc.
   were found in `smali_classes11`/`smali_classes20` — but uncovered
   regions remain). Risk: apktool's signing chain + manifest re-bind +
   resource arsc rebuild often hits stragglers on 185 MB APKs. Easily a
   half-day spike on its own.
2. **Teach dalvik-kitkat libdex to accept 039** — one-line `DexFile.h`
   `#define DEX_MAGIC_VERS_039 "039\0"` plus a memcmp addition in
   `DexSwapVerify.cpp:2808`. Risky because new dex format 039 may carry
   bytecode features (sealed-class enforcement, hidden-API list refs,
   different string-data layout) that the rest of dalvik-kitkat
   silently mis-interprets. Surfaces as bytecode-verification crashes
   downstream rather than the clean 035 path.
3. **Use d8's intermediate mode + a custom dex-version-downgrader pass** —
   no existing tool does this cleanly; would require dexlib2-based
   custom code. >1 day of build.

### Blocker 2: McD is heavily multi-dex (33 dexes); pipeline pushes only primary classes.dex

Even if dex.035 emission worked, the **manifest-named entry classes are
NOT in `classes.dex`**:

| Class | Apktool smali tree |
|---|---|
| `com.mcdonalds.app.application.McDMarketApplication` | `smali_classes11` + `smali_classes12` (synthetic + main) |
| `com.mcdonalds.mcdcoreapp.common.activity.SplashActivity` | `smali_classes20` |

The current `run-ohos-test.sh` `[D/H]` stage pushes the SINGLE
`classes.dex` and appends it to `-Xbootclasspath`. McD's primary
classes.dex is 12 MB of Activity-manager-adjacent infrastructure (per
apktool's distribution), with `androidx`/`google` bootstrap; the actual
app classes spread across classes2..classes33.

Fix path: multi-dex BCP plumbing — push every classes*.dex as a separate
BCP entry. Conceptually trivial; in practice needs careful classpath
ordering (the strip-set fix from CR64 depended on the OHOS shim coming
BEFORE the app dex; with 33 entries, order across app dexes also
matters for any AndroidX classes spread between them). Smaller than
Blocker 1 but still a real plumbing job (est. 1-2 hours alone).

## What this CR did contribute

`scripts/run-ohos-test.sh` extended with an `mcd` alias so `bash
scripts/run-ohos-test.sh --arch arm32 inproc-app --apk mcd` parses
correctly. The alias is wired (apk-path, activity_spec, app_class,
outdir_subdir) but currently bottoms out at d8 redex. No strip-set
extension was made (Blocker 1 prevents any STRIP_CLASSES experiment
from being meaningful — we never get to dex resolution time).

Edit is universal (one new alias case + supported-set update); zero
McD-specific code outside the parameter table that mirrors noice's
parameter table. No per-app branches in script body or launcher.

## Validation

| Test | Result |
|---|---|
| MVP-0 hello (arm32) | **PASS** |
| E12 inproc-app (hello-color-apk → BLUE on DSI) (arm32) | **PASS** |
| E13 noice (arm32) ALL stages A+B+C+D | **PASS** (rc=0 reason=OK fill=argb) |

No regression of agents 24/25's concurrent work — STRIP_CLASSES
untouched, no shim/java edits, no `dalvik-port/compat/` edits.

## Self-audit (pre-commit)

- [x] No `Unsafe.allocateInstance` / `setAccessible(true)` in new code
- [x] No per-app/per-package branches (mcd alias mirrors noice alias)
- [x] `grep -rn 'com\.mcdonalds\|com\.mcd\|mcdonalds_' shim/ scripts/build-shim-dex-ohos.sh` returns empty
- [x] `WestlakeContextImpl` untouched
- [x] No new methods on owned shim classes
- [x] No edits to `dalvik-port/compat/` (CR66 turf)
- [x] No edits to `SoftwareCanvas*` (CR67 turf)
- [x] noice E13 PASS after my edit (re-ran post-change: A=1 B=1 C=1 D=1)

## Recommendation for CR66+ (McD continuation)

McD on this pipeline needs a tooling workstream BEFORE the strip-set or
shim story matters. Suggested ordering:

1. **CR-A: dex.039 input/output strategy** (~half-day)
   - Spike apktool b --api-level 13 on the existing `apktool_decoded`
     tree; vet for API-26+ opcode usage across all 33 smali trees
     (`grep -rE "invoke-polymorphic|invoke-custom|const-method-handle"`
     returns zero in smali_classes11/12/20 but the full sweep is
     pending). If clean, this collapses to a SHA-stable
     `mcd-rebuilt-035.apk`.
   - Backstop: dalvik-kitkat libdex bypass (#define DEX_MAGIC_VERS_039,
     no semantic changes). Higher risk but tighter blast radius.

2. **CR-B: multidex BCP plumbing** (~1-2 hours after CR-A lands)
   - Extend `run-ohos-test.sh`'s `[D/H]` push stage to push every output
     classes*.dex and append each to `-Xbootclasspath`. Generic; useful
     for any future multidex app (Yelp, Material samples, etc.).

3. **CR-C: McD strip-set extension** (after Stage A reaches)
   - At that point the noice-proven loop reopens: run, hit NSME/NSFE,
     extend `STRIP_CLASSES`, re-run. Likely 0-2 additional entries
     (McD's R8 fingerprint is similar to noice — both use modern
     AndroidX + Hilt + Kotlin Coroutines).

4. **CR-D: McD-specific in-body NPEs** (after Stage C reaches)
   - `McDMarketApplication.onCreate` is likely heavy (Firebase init,
     GMS auth bootstrap, OkHttp pinning). Stub-out tactical via the
     existing daemon-port substrate (M5 audio, M6 surface) or the
     in-process pattern from `project_noice_inprocess_breakthrough.md`.

## Files touched (this CR)

- `scripts/run-ohos-test.sh` — added `mcd)` case to `--apk` alias table.
  +20 LOC including comments; mirrors `noice)` case structure exactly.

## Files NOT touched (per turf rules)

- `scripts/build-shim-dex-ohos.sh` — STRIP_CLASSES unchanged (no McD
  strip to add — Blocker 1 prevented reaching strip-resolution time).
- `shim/java/` — no new AOSP-default methods needed.
- `ohos-tests-gradle/inproc-app-launcher/` — launcher is already
  generic (`--apk-app` parameter), no McD-specific changes required.
- `dalvik-port/compat/` (CR66 turf).
- `SoftwareCanvas*` (CR67 turf).
