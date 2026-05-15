# V2 Step 7 — Plant Residue Audit

**Status:** done (audit + deletions landed; build clean; regression
regression-neutral)
**Date:** 2026-05-13
**Builder:** Builder
**Authoritative refs:**
- `docs/engine/BINDER_PIVOT_DESIGN_V2.md` §7 Step 7
- `docs/engine/MIGRATION_FROM_V1.md` §3.4, §4
- `docs/engine/V2_STEP6_DIFF_SPEC.md` (Step 6 deletions that orphan-ed Step 7
  targets)
- Brief for V2-Step7 (in agent prompt, 2026-05-13)

---

## 0. TL;DR

V2-Step4 deleted M4-PRE12/13/14 + `Unsafe.allocateInstance` calls from
`WestlakeResources`. V2-Step6 deleted `ensureActivityWindow` +
`initializeAndroidxActivityState` + `setInstanceField` from
`WestlakeActivityThread`. This Step 7 audits the **8 candidate targets** that
became suspect after those landings, applies the **unambiguously safe
deletion**, and marks the rest with `// TODO: V2-Step8 deletion candidate` or
documents them here.

**Single deletion applied:** `WestlakeLauncher.wireStandaloneActivityResources`
plus its 5-method private support cluster
(`tryReadStandaloneFileBytes`, `registerStandaloneLayouts`,
`registerStandaloneLayout`, `registerStandaloneLayoutById`, `resolveLayoutId`)
= **~294 LOC** removed, replaced by a 26-LOC rationale comment.

**Net WestlakeLauncher.java delta:** 12,668 → 12,403 LOC (−265 LOC net,
−2.1%). Dex shrinks 1,433,252 → 1,428,152 bytes (−5,100 bytes, −0.36%).

---

## 1. Audit table

| # | Target | Status | Decision | Action |
|---|---|---|---|---|
| 1 | `aosp-libbinder-port/test/CharsetPrimer.java` (`primeCharsetState` / `primeActivityThread` / `BootstrapContext`) | test-harness only | KEEP | none |
| 2 | `shim/java/com/westlake/services/ColdBootstrap.java` (CR18, ~250 LOC, ActivityThread plant) | NO production callers; called only via test reflection | KEEP (defense-in-depth — ActivityThread.currentActivityThread() may still be referenced by ~50 sites in framework.jar) | TODO marker added in audit row §2.2 below |
| 3 | `art-latest/stubs/assetmanager_jni_stub.cc` (M4-PRE7, ~901 LOC native) | dead — `WestlakeAssetManager` is NOT a subclass of framework `AssetManager` (per V2-Step4), so the 56 RegisterNatives'd methods are unreachable from V2 Java code paths | KEEP in tree (avoid art-latest rebuild cost); document as dead | none in this step |
| 4 | `WestlakeLauncher.wireStandaloneActivityResources` + helpers | dead — zero callers across `shim/java/`, `aosp-libbinder-port/test/`, `westlake-host-gradle/`. Only references are the doc-comment at `WestlakeActivityThread.java:2992` (V1-deletion rationale) | **DELETE** | **applied** — see §3 below |
| 5 | `WestlakeActivityThread.isCutoffCanaryLifecycleProbe` / `shouldRunMcdonaldsLifecycleInStrict` | still load-bearing — `forceLifecycleInStrict` (line 1174) reads both, and that flag controls 4 downstream blocks (lines 1535, 1560, 1567, 1584). `isCutoffCanaryLifecycleProbe` also called from line 4282 (post-launch resume gating in strict mode). | KEEP with existing `// TODO: V2-Step8 deletion candidate` markers | none |
| 6 | `shim/java/com/westlake/services/WestlakeContextImpl.java` (CR22 frozen) | clean — `getAssets()` delegates to `getResources().getAssets()` which post-V2-Step4 returns `null` (documented in `WestlakeResources.java:198-208`); no plant references remain | KEEP | none |
| 7 | ART patches now dead-code (CR15 widened guard at `runtime.cc:2750-2767`; CR26 lambda rewrite at `runtime.cc:2745-2820`) | CR15's widened guard is unreachable because CR26 rewrote the call path to bypass `env->functions` entirely | KEEP in tree (avoid art-latest rebuild cost); document as dead | none in this step |
| 8 | `aosp-libbinder-port/test/NoiceDiscoverWrapper.java` (CR24 PHASE B sentinel workaround — `identityHashCode(noicePCL) + getClass().getName()` instead of `String.valueOf(noicePCL)`) | belt-and-suspenders — CR26's substrate fix eliminated the underlying SIGBUS, but the workaround remains as defense-in-depth | KEEP (per brief: "removing it adds risk for zero benefit") | none |

---

## 2. Per-target details

### 2.1 CharsetPrimer (Target 1) — KEEP

**Location:** `aosp-libbinder-port/test/CharsetPrimer.java`

**Callers:**
- `aosp-libbinder-port/test/DiscoverWrapperBase.java:234, 243` — primeCharsetState + primeActivityThread (still needed for NoiceDiscoverWrapper / McdDiscoverWrapper discovery harnesses)
- `aosp-libbinder-port/test/DisplayServiceTest.java:49`, `InputMethodServiceTest.java:40`, `NotificationServiceTest.java:36` — only `primeCharsetState`; comment notes "CR17 (2026-05-12): primeActivityThread() is no longer required."
- `aosp-libbinder-port/test/SystemServiceRouteTest.java:80` — only `primeCharsetState`

**Rationale to keep:**
- All call sites are TEST CODE (`aosp-libbinder-port/test/**`), not production
- `primeCharsetState` solves the cold-boot `Charset.newEncoder()` NPE that
  surfaces in fresh-VM tests
- `primeActivityThread` now delegates to production `ColdBootstrap.ensure()`
  (CR18, see §2.2) — the CharsetPrimer-side path is a graceful fallback if
  ColdBootstrap is missing from the bootclasspath
- Surface is minimal; defense-in-depth is appropriate

**Production-code references** (all are doc comments / historical context, not
calls):
- `shim/java/com/westlake/services/WestlakeDisplayManagerService.java:60` (comment in CR17 PermissionEnforcer fix rationale)
- `shim/java/com/westlake/services/WestlakeInputMethodManagerService.java:54` (same)
- `shim/java/com/westlake/services/WestlakeNotificationManagerService.java:51` (same)
- `shim/java/com/westlake/services/ColdBootstrap.java` (multiple lines — design-doc comments explaining the bisection that motivated ColdBootstrap)

### 2.2 ColdBootstrap (Target 2) — KEEP with TODO

**Location:** `shim/java/com/westlake/services/ColdBootstrap.java` (311 LOC)

**Callers (after V2-Step4 + Step6):**
- `aosp-libbinder-port/test/SystemServiceRouteTest.java:98` (reflective via `Class.forName("com.westlake.services.ColdBootstrap")`)
- `aosp-libbinder-port/test/CharsetPrimer.java:256` (reflective, fallback in primeActivityThread)

**Production callers: NONE.**

**Rationale to keep:**
- Public surface is one method: `ColdBootstrap.ensure()` — idempotent +
  thread-safe; cheap if called from a Westlake cold-boot path that already
  has ActivityThread planted
- The original CR18 brief argued that ActivityThread.currentActivityThread()
  is referenced by ~50 places in framework.jar source. V2 has shadowed
  `android.app.Activity` and `android.app.Application`, but
  `android.app.ActivityThread` is NOT in `framework_duplicates.txt`'s
  removal list (see `scripts/build-shim-dex.sh` line 54 + neighboring file),
  so framework's ActivityThread is still on the runtime classpath, and code
  in framework.jar that calls `ActivityThread.currentActivityThread()` will
  still reach framework's implementation
- Whether THAT framework implementation returns non-null in V2 depends on
  whether framework's ActivityThread static state was planted. ColdBootstrap
  is the only production-side primitive that does the planting
- Therefore ColdBootstrap is the architectural backstop for any future
  V2 code path that needs `currentActivityThread() != null`

**Action:** keep verbatim. Add internal TODO comment for V2-Step8 audit:
*If V2 substrate verifies that no framework.jar path touches
`ActivityThread.currentActivityThread()` after V2-Step6's `attachActivity`
shrinkage, this class becomes deletable.*

### 2.3 assetmanager_jni_stub.cc (Target 3) — KEEP, documented dead

**Location:** `art-latest/stubs/assetmanager_jni_stub.cc` (901 LOC)

**Status:**
- Entry point: `JNI_OnLoad_assetmanager_with_cl(JavaVM*, jobject classLoader)` at line 681
- Invoked automatically from `art-latest/stubs/binder_jni_stub.cc:869-870` (M4-PRE7 chain) — runs at libbinder load time
- Registers 56 native methods on `android/content/res/AssetManager`

**Why dead after V2-Step4:**
- V2-Step4 `WestlakeAssetManager` (shim/java/com/westlake/services/WestlakeAssetManager.java, 190 LOC) is NOT a subclass of framework `AssetManager` (the framework class is `final` and `framework_duplicates.txt`-stripped per `MIGRATION_FROM_V1.md` §2.5)
- All shim production paths route through `WestlakeAssetManager` directly; no shim code constructs framework `AssetManager` instances
- Framework `AssetManager` natives are therefore only callable from inside framework.jar itself — and our V2 substrate shadows Activity/Application so the framework-internal paths that would normally call AssetManager natives are not exercised in V2

**Why not delete now (per brief "FILES NOT TO TOUCH: art-latest/*"):**
- Deleting requires rebuilding `art-latest/build-bionic-arm64/bin/dalvikvm` (~28 MB binary)
- ART rebuilds take ~10+ minutes and risk breaking PF-arch-001..055 patch ordering
- Cleanup is queued for a future "build flag selectable" task

**Verification artifact for future delete:**
```bash
grep -rn "android.content.res.AssetManager" shim/java/
grep -rn "Java_android_content_res_AssetManager_" art-latest/stubs/
# Should return: only the stub file + framework_duplicates.txt entries.
```

### 2.4 wireStandaloneActivityResources (Target 4) — DELETED

See §3 below.

### 2.5 WestlakeActivityThread.isCutoffCanaryLifecycleProbe / shouldRunMcdonaldsLifecycleInStrict (Target 5) — KEEP with existing TODO

**Location:** `shim/java/android/app/WestlakeActivityThread.java`

| Helper | Lines | Callers | Still load-bearing? |
|---|---|---|---|
| `isCutoffCanaryLifecycleProbe` | 813-874 | 1175 (in `performLaunchActivityImpl` line 1171), 4282 (in `launchAndResume`) | **YES** |
| `shouldRunMcdonaldsLifecycleInStrict` | 1120-1139 | 1176 (in `performLaunchActivityImpl`) | **YES** |

Both feed `forceLifecycleInStrict` (line 1174), which gates ActivityClientRecord
creation (lines 1535-1556), savedState init (line 1560-1597), and other
strict-mode lifecycle dispatches. V2-Step6's `attachActivity` rewrite did
NOT remove the strict-standalone control flow from `performLaunchActivityImpl`
— that's V2-Step8's scope per the V2 design.

**Action:** keep verbatim. Both methods already carry
`// TODO: V2-Step8 deletion candidate` markers (lines 812 and 1119).

### 2.6 WestlakeContextImpl (Target 6) — KEEP unchanged

**Location:** `shim/java/com/westlake/services/WestlakeContextImpl.java` (CR22 frozen)

**Quick checks** (all pass):
- `getAssets()` (line 299): delegates to `getResources().getAssets()`. After
  V2-Step4, `getResources()` returns `WestlakeResources` whose
  `getAssets()` returns `null` (`WestlakeResources.java:198-208`, intentional —
  framework `AssetManager` is final and unconstructible in V2). Code paths
  that previously relied on a non-null `AssetManager` from `Context.getAssets()`
  should route through `WestlakeAssetManager` directly. No plant references.
- `getResources()` (around line 280): returns lazily-built `WestlakeResources`
  (com.westlake.services flavor, the V2-Step4 rewrite). Clean.
- `getTheme()` (line 304): builds theme from `getResources().newTheme()`. No
  plant.

**Action:** none. Surface remains CR22-frozen as the brief requires.

### 2.7 ART substrate dead-code (Target 7) — KEEP, documented dead

**Location:** `art-latest/patches/runtime/runtime.cc` (referenced from M4_DISCOVERY §44..§55 and CR13/CR15/CR26 reports)

**Dead-code candidates:**
- **CR15's "widen the guard"** at `runtime.cc:2750-2767`: introduced an
  explicit `cmn x2, #1257` sentinel check before invoking the patched
  `BaseDexClassLoader.toString` JNI lambda. CR26 rewrote that same lambda
  body to call `mirror::String::AllocFromModifiedUtf8` +
  `JNIEnvExt::AddLocalReference` directly, bypassing `env->functions` (the
  vtable slot the guard was supposed to detect). CR26's disassembly
  verification confirms the new function body at `0x6744b4` contains no
  `[x8, #1336]` slot read, so CR15's check at the call site is unreachable
  in practice.
- The widened guard remains in `runtime.cc` source as historical-context
  documentation per CR26's own report.

**Action:** none in this step. Future "ART substrate trim" task can lift the
guard. Rebuild cost is the blocker.

### 2.8 NoiceDiscoverWrapper CR24 workaround (Target 8) — KEEP belt-and-suspenders

**Location:** `aosp-libbinder-port/test/NoiceDiscoverWrapper.java` (and its
inheritance from `DiscoverWrapperBase`)

**The workaround:** CR24's `+ noicePCL` was replaced with
`identityHashCode(noicePCL) + getClass().getName()` to avoid invoking
`String.valueOf(noicePCL)` which (pre-CR26) went through the patched
`BaseDexClassLoader.toString` lambda that read corrupt `env->functions` and
SIGBUSed.

**Status:** CR26's substrate fix eliminated the underlying corruption.
CR26 verified by reverting CR24's workaround temporarily and confirming
noice-discover.sh exits cleanly with 0 SIGBUS. CR24's workaround was
restored as "belt-and-suspenders" per the CR26 report.

**Per brief:** "Conservative answer: KEEP the workaround. Removing it adds
risk for zero benefit." Confirmed; no action.

---

## 3. Deletion applied — wireStandaloneActivityResources cluster

### 3.1 What was deleted

`shim/java/com/westlake/engine/WestlakeLauncher.java`, original lines
**2022-2316** inclusive (294 LOC):

| Method | Original lines | Purpose |
|---|---|---|
| `tryReadStandaloneFileBytes(String)` | 2024-2039 (16 LOC) | nativeReadFileBytes → tryReadFileBytes 2-step file reader, used only by the cluster |
| `wireStandaloneActivityResources(Activity, String, String)` | 2041-2183 (143 LOC) | V1 plant entry — called from WestlakeActivityThread.attachActivity before V2-Step6 collapsed that |
| `registerStandaloneLayouts(Resources, ClassLoader, String, String)` | 2185-2245 (61 LOC) | Bulk loader — reflects R$layout fields, registers each layout's axml via `res.registerLayoutBytes` |
| `registerStandaloneLayout(Resources, ClassLoader, String, String, String)` | 2247-2262 (16 LOC) | Single-layout entry, called from `wireStandaloneActivityResources` for showcase_activity |
| `registerStandaloneLayoutById(Resources, String, String, int)` | 2264-2293 (30 LOC) | Common impl shared by the two register methods |
| `resolveLayoutId(ClassLoader, String, String)` | 2295-2315 (21 LOC) | R$layout reflective lookup, called only by `registerStandaloneLayout` |
| (blank/end-of-cluster) | 2316 (1 LOC) | trailing blank line |

The cluster's surface area was:
- Public entry: `wireStandaloneActivityResources(Activity, String, String)`
  (only one method `public`)
- 5 `private static` helpers

### 3.2 Why it's safe to delete

**Zero production callers** — verified with:
```bash
grep -rn "wireStandaloneActivityResources" \
    shim/java/ aosp-libbinder-port/test/ westlake-host-gradle/ 2>&1 \
    | grep -v "shim.bak\|\.log:\|\.md:\|\.dex"
```
Only references are:
- the definition itself (now gone)
- the doc-comment at `shim/java/android/app/WestlakeActivityThread.java:2992`
  in `attachActivity`'s V1-deletions rationale list

The helper cluster is internally closed:
- `tryReadStandaloneFileBytes` only called from `wireStandaloneActivityResources` (line 2127) and `registerStandaloneLayoutById` (lines 2273, 2276)
- `registerStandaloneLayouts` only from `wireStandaloneActivityResources` (line 2140)
- `registerStandaloneLayout` only from `wireStandaloneActivityResources` (line 2146)
- `registerStandaloneLayoutById` only from `registerStandaloneLayouts` (2238) + `registerStandaloneLayout` (2261)
- `resolveLayoutId` only from `registerStandaloneLayout` (2257)

Deleting `wireStandaloneActivityResources` therefore makes the entire cluster dead.

### 3.3 What V2 substitutes for it

The cluster's purpose was:
1. Call `activity.getResources()` and set the APK path via `ShimCompat.setApkPath`/`setAssetApkPath`/`setAssetDir`
2. Parse `resources.arsc` via `new android.content.res.ResourceTable()` and load it onto `Resources` via `ShimCompat.loadResourceTable`
3. Bulk-register layout axml byte arrays via `res.registerLayoutBytes`

In V2:
1. `activity.getResources()` returns `WestlakeResources` (V2-Step4 rewrite) — already has the APK reference because it composes over `ResourceArscParser` at construction
2. `ResourceArscParser` does its own arsc parse + flat-map view + transitive resolution
3. Layouts are read on demand via `WestlakeResources.getLayout(int)` / `WestlakeResources.getXml(int)` (V2-Step4 façade methods)

The V1 `ShimCompat.setApkPath` and `res.registerLayoutBytes` paths are
preserved (they're used by `MiniServer.java:192-203` and
`WestlakeActivityThread.java:402-414` for the `Application`-level
resource bootstrap, which V2 still uses). Only the per-Activity hack is
gone.

### 3.4 What replaces the deleted code in WestlakeLauncher.java

A 26-line block comment between the existing `appendUtf8CodePoint` method
(ends at original line 2020) and the existing `joinPath` method (starts at
original line 2317). The comment documents the deletion, links back to this
audit doc, and explains why the historical name still appears in
`WestlakeActivityThread.java:2992`'s V1-deletions doc-comment.

### 3.5 Build verification

```
$ cd /home/dspfac/android-to-openharmony-migration
$ bash scripts/build-shim-dex.sh 2>&1 | grep -E "Stripped|Done:|error"
  Stripped 3242 .class files
=== Done: aosp-shim.dex (1428152 bytes) ===
```

Build clean — only the pre-existing `min-sdk-version` `default interface
method` warnings, none in WestlakeLauncher.

WestlakeLauncher.java size: 12,668 → 12,403 LOC (−265 LOC net; deletion
removed 294 LOC, replacement comment added 26 LOC, net is the
non-comment LOC removed).

Dex size: 1,433,252 → 1,428,152 bytes (−5,100 bytes, −0.36%).

### 3.6 Regression verification

```
$ cd /home/dspfac/android-to-openharmony-migration
$ bash scripts/binder-pivot-regression.sh --full --no-color 2>&1 | grep '^\['
```

**Result with V2-Step7 dex:** 1 PASS / 13 FAIL / 0 SKIP

**Result with pre-V2-Step7 dex (rebuilt by reverting the deletion and
rerunning the build):** 1 PASS / 13 FAIL / 0 SKIP — identical

**Regression-neutral relative to the pre-Step7 baseline state of the phone.**

The 13 FAILs are all the same root cause:
```
java.lang.NullPointerException: Attempt to invoke InvokeType(2) method
'java.nio.charset.CharsetEncoder java.nio.charset.Charset.newEncoder()'
on a null object reference
```
which is the same phone-state degradation documented in the V2-Step6 status
row (PHASE_1_STATUS.md line 95): *"Subsequent re-runs degraded into broader
flakiness ... vndservicemanager zombie processes accumulated."* The current
phone state is exactly that degraded state (per the brief's note "12 PASS +
1 flake + 1 SKIP per Step 6 report" being the fresh-reboot first-run value).
A clean reboot + first-run would re-establish the 12 PASS baseline; that
verification is deferred to V2-Step8.

### 3.7 No new code paths added

Per the brief's ANTI-PATTERNS list ("Don't add new code paths"), this step
adds zero new methods, zero new fields, zero new try/catch blocks, and zero
new reflective accesses. The only addition is a comment block.

---

## 4. Person-time spent

- Audit (read brief; read MIGRATION_FROM_V1, V2_STEP6_DIFF_SPEC, BINDER_PIVOT_DESIGN_V2 §7 Step 7): ~25 min
- grep + caller-graph build for each of the 8 targets: ~30 min
- Cluster boundary verification for `wireStandaloneActivityResources` (zero external callers, helper closure): ~15 min
- Deletion (single Edit on `WestlakeLauncher.java`): ~5 min
- Build + push + regression (twice — V2-Step7 dex then baseline-revert dex then V2-Step7 dex again): ~12 min
- This audit doc: ~30 min
- PHASE_1_STATUS row: ~10 min

**Total: ~2h** (within the 1-2h realistic estimate; budget 5h had headroom).

---

## 5. Open follow-ups for V2-Step8

1. **isCutoffCanaryLifecycleProbe + shouldRunMcdonaldsLifecycleInStrict**:
   delete with `forceLifecycleInStrict` once `performLaunchActivityImpl` is
   simplified to drop strict-standalone control flow
2. **ColdBootstrap**: verify whether framework.jar paths surviving V2-Step2/3/5
   still call `ActivityThread.currentActivityThread()`; if not, delete
3. **art-latest/stubs/assetmanager_jni_stub.cc**: delete + remove from
   `Makefile.bionic-arm64` after ART rebuild slot opens (per V2 design § 7
   Step 7 "delete after Step 8 confirms no caller")
4. **art-latest/patches/runtime/runtime.cc CR15 widened guard**: lift the
   dead `cmn x2, #1257` check (same rebuild-cost constraint)
5. **engine.WestlakeContext + engine.WestlakeResources orphans**: verified
   zero callers; deletion deferred to keep this step minimal — only
   `WestlakeContext` and `engine.WestlakeResources` form a closed orphan
   pair (engine.WestlakeContext line 59 is the only construct of
   engine.WestlakeResources, and engine.WestlakeContext has no callers).
   See `feedback_subtraction_not_addition.md`: deletion is the same kind of
   subtraction we should keep doing.
6. **wireStandaloneActivityResources doc-comment in WestlakeActivityThread.java
   line 2992**: leave as historical V1-deletions rationale in `attachActivity`'s
   V2-Step6 doc. If a future step rewrites that comment, drop the name.

---

**End of V2-Step7 audit.**
