# Westlake Engine — `shim/java/android/*` Shim Audit (Milestone C5)

**Author:** Architect agent (2026-05-12)
**Companion to:** `docs/engine/BINDER_PIVOT_DESIGN.md`, `docs/engine/BINDER_PIVOT_MILESTONES.md`

Read-only audit of the 10 largest custom and AOSP-derived classes in `shim/java/android/*` plus `ServiceManager.java`. Determines per-class disposition: keep / replace-with-AOSP / fold-into-service.

---

## 1. Per-Class Dispositions

### 1.1 `shim/java/android/view/View.java` (30,798 LOC)
- **Origin:** AOSP-derived (header: "Copyright (C) 2006 The Android Open Source Project", Apache 2.0).
- **Westlake additions:** ~24 grep hits for `Westlake|McDonald|mcd|MCD`. Custom additions include:
  - `sWestlakePerformClickMarkerCount`, `sWestlakeTouchLifecycleMarkerCount` static counters (lines 822-823).
  - `logWestlakePerformClickMarker(...)` inside `performClick()` (lines 7472-7504) — telemetry wrapper around the original click dispatch.
  - `shouldLogWestlakeTouchLifecycle` / `logWestlakeTouchLifecycleMarker` (lines 14373-14495) — touch lifecycle instrumentation.
  - `findDataBindingChildTag` / `logDataBindingTagProbe` / `isDataBindingLayoutTag` (lines 25320-25464) — Westlake's `getTag()` heuristic that derives a `layout/...` tag from child views by walking the data-binding stack frames. Contains a per-app `mcdonalds` stack-frame string match (line 25453).
  - `fastForwardWestlakeHeadlessAnimation` (lines 26106-26122) — animation fast-forward for headless render mode.
  - `shouldDrawRoundScrollbar` short-circuited (lines 30463-30490) — disabled wearable code commented out for the standalone ART path.
- **Disposition:** `[KEEP-with-shrink]`
- **Rationale:** The AOSP body is correctly upstream and load-bearing for all rendering. The Westlake instrumentation/marker code is diagnostic and ~600 LOC. Post-pivot, remove all `logWestlake*` markers, the `getTag()` data-binding heuristic (it's a band-aid for missing real data-binding code generation — should be handled by app's generated DataBinderMapper running unmodified), the headless-animation fast-forwarder (real WindowManager + Choreographer integration via M4b makes this obsolete), and the McDonald's stack-frame match.

### 1.2 `shim/java/android/widget/TextView.java` (13,697 LOC)
- **Origin:** AOSP-derived (header: "Copyright (C) 2006 The Android Open Source Project", Apache 2.0).
- **Westlake additions:** 0 hits for `Westlake|McDonald|mcd|MCD|noice`. The file is verbatim AOSP.
- **Disposition:** `[KEEP-AOSP-as-is]`
- **Rationale:** Pure AOSP; no modifications. Post-pivot, this file continues unchanged.

### 1.3 `shim/java/android/view/ViewGroup.java` (9,556 LOC)
- **Origin:** AOSP-derived (header: "Copyright (C) 2006 The Android Open Source Project").
- **Westlake additions:** ~39 hits. The custom region is the "scaled-touch redispatch" feature (lines 211-215 fields + lines 2848-3087 methods, total ~240 LOC):
  - `dispatchWestlakeScaledTouchStream`, `dispatchWestlakeScaledTouchDown`, `westlakeTouchableContentBottom`, `westlakeFindTouchRedirect`, `westlakeFindTouchableAt`, `westlakeFindNearestTouchableProjection`, `westlakeIsTouchableCandidate`, `logWestlakeScaledTouchMarker`, `westlakeViewSummary`, and a private inner class `WestlakeTouchRedirect`.
  - Purpose: when the rendered View tree is laid out at scale (renderer puts non-full-height content into a full-height surface), this code redirects the user's touch from where they tapped on the rendered surface to where the framework thinks the touchable child is positioned. This is a renderer-coupling hack symptomatic of the upstream rendering / layout pipeline being divergent.
- **Disposition:** `[KEEP-with-shrink]`
- **Rationale:** The AOSP body is upstream and correct. The 240-LOC scaled-touch hack is the renderer's layout drift compensated at the dispatch level — it must die post-pivot when WindowManager (M4b) + real ViewRootImpl drives the same layout pass that the renderer reads. The hack is a 1:1 candidate for removal once M4b is green.

### 1.4 `shim/java/android/widget/AbsListView.java` (7,844 LOC)
- **Origin:** AOSP-derived (header: "Copyright (C) 2006 The Android Open Source Project").
- **Westlake additions:** 0 hits.
- **Disposition:** `[KEEP-AOSP-as-is]`
- **Rationale:** Pure AOSP. Stays unchanged.

### 1.5 `shim/java/android/widget/Editor.java` (7,661 LOC)
- **Origin:** AOSP-derived (header: "Copyright (C) 2012 The Android Open Source Project").
- **Westlake additions:** 0 hits.
- **Disposition:** `[KEEP-AOSP-as-is]`
- **Rationale:** Pure AOSP. Stays unchanged.

### 1.6 `shim/java/android/app/WestlakeActivityThread.java` (5,137 LOC)
- **Origin:** Custom Westlake (no AOSP header; doc comment explicitly says "stripped-down ActivityThread for the Westlake Engine").
- **Role:** Replaces AOSP's `ActivityThread.java`. Provides the `currentActivityThread()` singleton, `mActivities` token map, `ActivityClientRecord`, `PackageInfo` (LoadedApk stand-in), `makeApplication`, `performLaunchActivity`, lifecycle dispatch, etc. — but with Binder IPC, system-server calls, Configuration propagation, and ResourcesManager intentionally elided.
- **Per-app branches:** 89 hits for `mcdonalds|MCD|mcd`. Embedded special-casing for "com.mcdonalds." packages and "com.westlake.mcdprofile" classes (DataSourceHelper bootstrap, splash-layout fallback to `0x7f0e0530`, AppConfigurationManager mock with hardcoded `https://us-prod.api.mcd.com/...` URLs, `OrderProductDetails` PDP wait timer extensions, etc.).
- **Disposition:** `[REPLACE-with-AOSP]` (with caveat — see below)
- **Rationale:** Per `BINDER_PIVOT_DESIGN.md` §3.2 and §3.4: AOSP's real `ActivityThread` should run unmodified once `IActivityManager.Stub`, `IPackageManager.Stub`, `IWindowManager.Stub` are reachable via real ServiceManager (M3 + M4a/b/c). AOSP's ActivityThread already handles Application creation, ActivityClientRecord management, attach/detach, lifecycle dispatch — it just needs services to return non-null. WestlakeActivityThread is the substitution-at-wrong-level antipattern (§2.3). Replace with AOSP's ActivityThread, sinking its system-service calls into M4 stubs. **Caveat:** McDonald's-specific bootstrap (DataSourceHelper seeding, AppConfiguration mock) must be migrated to per-app profile files outside the framework path before this class can be deleted — these are app-bring-up hacks, not framework substitution.

### 1.7 `shim/java/android/view/LayoutInflater.java` (5,108 LOC)
- **Origin:** Custom Westlake (no AOSP header; doc comment: "Supports three inflation strategies … programmatic layout registry, binary layout XML from APK, fallback empty FrameLayout").
- **Role:** Westlake's homegrown layout inflater. Parses binary AXML from APK files (via `BinaryXmlParser`), maps tag names to View classes through `sTagClassMap` / `sFqnClassMap`, instantiates views.
- **Per-app branches:** 38 hits for Westlake + 666 hits for mcd/MCD. **The first 250 lines are nothing but McDonald's hardcoded resource IDs** (`MCD_LAYOUT_*`, `MCD_ID_*`, `MCD_COLOR_*`, `MCD_DIMEN_*`), plus per-class concrete instantiation branches for `com.mcdonalds.mcduikit.widget.McDToolBarView`, `McDTextView`, `McDAppCompatTextView`, etc. (lines 684-704). Also has `noice` widget mappings (lines 384-386) and a `PF-noice-027` FCV-tag bypass (lines 407-439).
- **Disposition:** `[REPLACE-with-AOSP]`
- **Rationale:** AOSP's real `LayoutInflater` + `PhoneLayoutInflater` use a much simpler reflection-based factory plus a `Factory2` extension point. AOSP framework correctly handles fully-qualified class names by reflection without our hardcoded `sFqnClassMap`. Post-pivot, AOSP's LayoutInflater should drop in once Context.getSystemService returns a working `LayoutInflaterService` (or whatever the modern AOSP equivalent is). The McDonald's resource-ID dictionary and per-class instantiation are violations of `feedback_no_per_app_hacks.md`. Migrate any layout-id remapping logic to a per-app profile file (NOT a framework class) and let AOSP's LayoutInflater drive inflation through standard `ClassLoader.loadClass`.

### 1.8 `shim/java/android/app/MiniActivityManager.java` (4,883 LOC)
- **Origin:** Custom Westlake (no AOSP header; doc comment: "manages the Activity back stack … Replaces Android's ActivityManagerService + ActivityTaskManagerService").
- **Role:** A single-process, in-memory back-stack manager. Holds `ArrayList<ActivityRecord>`, dispatches lifecycle methods through `Activity.attach()` (reflectively), implements `startActivity` / `finishActivity` / `startActivityForResult` round-trips, instantiates activities via `Unsafe.allocateInstance`, attaches a PhoneWindow reflectively, and seeds Hilt's `ApplicationComponentManager.singletonComponent`.
- **Per-app branches:** 47 hits for mcd/MCD. Embedded special cases include:
  - `CUTOFF_CANARY_PACKAGE` / `CUTOFF_CANARY_ACTIVITY` constants (lines 24-30) — Westlake test infrastructure.
  - `isMcdOrderProductDetailsRecord` (lines 95-99).
  - `shouldRunLegacyMcdBootstrap` (lines 81-93).
  - PDP wait timer extensions (line 4308: `long startWaitMs = mcdPdp ? 35000L : 10000L`).
  - Multiple `MCD_PROFILE_*` proof markers.
  - Hardcoded splash layout fallback `0x7f0e0530` (line 2521).
  - `seedMcdonaldsApplicationContext`, `seedMcdonaldsSdkPersistenceState` invocations (lines 3626, 3799).
- **Disposition:** `[FOLD-into-Mx-service]` (specifically: `[FOLD-into-M4a-WestlakeActivityManagerService]`)
- **Rationale:** Per `BINDER_PIVOT_DESIGN.md` §3.5: "Most of `MiniActivityManager` — replaced by `WestlakeActivityManagerService extends IActivityManager.Stub`, which sits in the same place architecturally but uses AOSP's expected Stub pattern." `BINDER_PIVOT_MILESTONES.md` §M4a confirms this is the planned replacement. The McD/canary branches are per-app hacks that must NOT survive the M4a migration — they get isolated to per-app profile files (see Section 3 below). The Hilt seeding (`ensureSingletonComponent` ~lines 364-400) is application-private state that the framework should NOT be responsible for — once AOSP's real `ActivityThread.handleBindApplication` runs unchanged, Hilt's own ApplicationContext init runs naturally.

### 1.9 `shim/java/android/widget/ListView.java` (4,183 LOC)
- **Origin:** AOSP-derived (header: "Copyright (C) 2006 The Android Open Source Project").
- **Westlake additions:** 0 hits.
- **Disposition:** `[KEEP-AOSP-as-is]`
- **Rationale:** Pure AOSP. Stays unchanged.

### 1.10 `shim/java/android/app/WestlakeInstrumentation.java` (2,457 LOC)
- **Origin:** Custom Westlake. Extends our (also custom) base `Instrumentation` (which is itself a 30-LOC custom stub at `shim/java/android/app/Instrumentation.java`).
- **Role:** Adds AppComponentFactory integration (for Hilt DI injection), `execStartActivity()` delegation to `WestlakeActivityThread`, robust error handling, and Application lifecycle callbacks dispatch.
- **Per-app branches:** 67 hits for mcd/MCD. The bulk of the file (~1,600 LOC of 2,457) is McDonald's-specific seeding:
  - `seedMcdonaldsDataSourceHelper`, `seedNamedMcdonaldsAccountProfileInteractor`, `seedNamedMcdonaldsRestaurantInteractor`, `createKnownMcdonaldsConcreteReturn`, `seedMcdonaldsActivityInterfaceFields`, `createMcdonaldsInterfaceProxy`, etc.
  - Hardcoded class names for `com.mcdonalds.mcdcoreapp.helper.interfaces.AccountProfileInteractor`, `RestaurantModuleInteractor`, `LoyaltyModuleInteractor`, etc.
- **Disposition:** `[REPLACE-with-AOSP]` for the framework body; `[FOLD-into-mcd-profile]` for the McD seeding code
- **Rationale:** AOSP's `Instrumentation` already handles `callActivityOnCreate`, `newActivity`, `execStartActivity`, `onException`, AppComponentFactory integration, lifecycle callbacks. Once the M3-wired ServiceManager + M4 services are in place, AOSP's real Instrumentation works unmodified. The ~1.6 K LOC of McDonald's-specific field seeding has no AOSP equivalent and shouldn't — it's per-app bring-up, not framework code. Migrate to `westlake-host/profiles/mcdonalds/InstrumentationHook.java` or similar; framework path runs AOSP-stock.

### 1.11 `shim/java/android/os/ServiceManager.java` (63 LOC)
- **Origin:** Custom Westlake (no AOSP header; comment: "Minimal hidden-API compatible ServiceManager shim").
- **Role:** In-process `sCache: HashMap<String, IBinder>` and `sServiceManager: IServiceManager` static fields. `getService` returns a placeholder `new Binder(name)` when nothing is registered, masking missing services as "present-but-empty."
- **Disposition:** `[FOLD-into-M3]`
- **Rationale:** Per `BINDER_PIVOT_MILESTONES.md` §M3: "Remove `shim/java/android/os/ServiceManager.java` from `aosp-shim.dex` so that AOSP framework's real `ServiceManager.java` (which calls into native libbinder via `android_os_ServiceManager_getService` JNI) is used instead." Replaced wholesale by M3's wiring of dalvikvm against Westlake's `libbinder.so` and `servicemanager`. The dependent files `IServiceManager.java` and `ServiceManagerNative.java` should also be removed in M3.

---

## 2. Disposition Summary Table

| Disposition | Classes | Total LOC | Notes |
|---|---|---|---|
| `[KEEP-AOSP-as-is]` | TextView (13,697), AbsListView (7,844), Editor (7,661), ListView (4,183) | **33,385** | Pristine AOSP-derived; no Westlake modifications. |
| `[KEEP-with-shrink]` | View (30,798), ViewGroup (9,556) | **40,354** (-~1,000 LOC shrinkable) | AOSP-derived; remove ~1 K LOC of Westlake markers, the scaled-touch hack, headless-animation fast-forward, and the data-binding `getTag()` heuristic. |
| `[REPLACE-with-AOSP]` | WestlakeActivityThread (5,137), LayoutInflater (5,108), WestlakeInstrumentation (2,457) | **12,702** | Custom; AOSP has unmodified equivalents that should run post-pivot once M3+M4 services are in place. |
| `[FOLD-into-Mx-service]` | MiniActivityManager (4,883), ServiceManager (63) | **4,946** | MAM folds into M4a's WestlakeActivityManagerService; ServiceManager folds into M3's real-binder wiring. |
| **TOTALS** | 11 classes | **91,387 LOC** audited | **~17,648 LOC deletable post-M4 (~19%)**; ~33,385 LOC stays verbatim; ~40,354 LOC keep with light shrink. |

---

## 3. Per-App Branches Found (Candidates for C2-style Cleanup)

These per-app McDonald's/canary branches violate `feedback_no_per_app_hacks.md` and need to be migrated to per-app profile files before / during M4. Listed in priority order (largest first):

| Class | LOC of per-app code | Examples |
|---|---|---|
| `shim/java/android/view/LayoutInflater.java` | ~250 LOC (top of file) | `MCD_LAYOUT_*`, `MCD_ID_*`, `MCD_COLOR_*`, `MCD_DIMEN_*` constants; hardcoded `com.mcdonalds.mcduikit.widget.McD*` instantiation branches (lines 684-704); `noice.widget.SwipeRefreshLayout` mappings (lines 384-386); `PF-noice-027` FCV bypass. |
| `shim/java/android/app/WestlakeInstrumentation.java` | ~1,600 LOC | Entire McD seeding subsystem: `seedMcdonalds*`, `createMcdonaldsInterfaceProxy`, `seedMcdonaldsActivityInterfaceFields`, hardcoded `AccountProfileInteractor`/`RestaurantModuleInteractor`/`LoyaltyModuleInteractor` instantiation. |
| `shim/java/android/app/MiniActivityManager.java` | ~600 LOC | `isMcdOrderProductDetailsRecord`, `shouldRunLegacyMcdBootstrap`, `CUTOFF_CANARY_*` constants, PDP timer extension (`mcdPdp ? 35000L : 10000L`), splash-layout fallback `0x7f0e0530`, hardcoded `https://us-prod.api.mcd.com/...` URLs (in WAT but cross-referenced), `seedMcdonaldsApplicationContext` / `seedMcdonaldsSdkPersistenceState` calls. |
| `shim/java/android/app/WestlakeActivityThread.java` | ~300 LOC | `com.westlake.mcdprofile.*` and `com.mcdonalds.*` package gates; `westlake.mcd.datasource.bootstrap` system property; hardcoded interface→impl resolutions for `OrderModuleInteractor` → `com.mcdonalds.order.util.OrderModuleImplementation` etc. |
| `shim/java/android/view/View.java` | ~30 LOC | `cls.indexOf("mcdonalds") < 0` filter in `findDataBindingChildTag` stack walk (line 25453). |

**Total per-app code identified across these 5 files: ~2,780 LOC** — all candidates for either deletion (when M4 services + AOSP Instrumentation replace these) or migration to `westlake-host/profiles/<app>/` profile files. Suggested cleanup task IDs: C2-McD-LayoutInflater, C2-McD-Instrumentation, C2-McD-MiniActivityManager, C2-McD-WAT, C2-McD-View.

---

## 4. Parallelization Recommendations for M4

The dispositions reveal a natural fan-out for parallel agent work during M4:

### Independent (can run in parallel after M3 is green):

**Track A — AOSP framework restoration:**
- **A1.** Replace `shim/java/android/os/ServiceManager.java` + `IServiceManager.java` + `ServiceManagerNative.java` with AOSP source (sub-task of M3).
- **A2.** Replace `shim/java/android/view/LayoutInflater.java` with AOSP `LayoutInflater.java` + `PhoneLayoutInflater.java` (independent of M4a/b/c — LayoutInflater calls don't go through Binder).
- **A3.** Replace `shim/java/android/app/Instrumentation.java` (the base 30-LOC custom stub) + delete `WestlakeInstrumentation.java` with AOSP `Instrumentation.java` (depends on M4a being able to handle Instrumentation's calls into IActivityManager).
- **A4.** Replace `shim/java/android/app/WestlakeActivityThread.java` with AOSP `ActivityThread.java` (depends on M4a, M4b, M4c; can run after A3).

**Track B — Shrink AOSP-derived shims:**
- **B1.** Remove Westlake markers from View.java (lines 822-823, 7472-7504, 14373-14495, 25320-25464, 26106-26122, 30463-30490) — independent of all M-tracks.
- **B2.** Remove the scaled-touch redispatch hack from ViewGroup.java (lines 211-215, 2848-3087) — must wait until M4b's WindowManager is correctly driving layout (otherwise breaks touch).

**Track C — Per-app code migration:**
- **C1.** Move McD-specific seeding from WestlakeInstrumentation to a `westlake-host/profiles/mcdonalds/InstrumentationHook.java`.
- **C2.** Move McD-specific Activity bootstrap from MiniActivityManager to `westlake-host/profiles/mcdonalds/ActivityBootstrap.java`.
- **C3.** Move McD layout-id dictionary from LayoutInflater to a `westlake-host/profiles/mcdonalds/LayoutOverrides.json` consumed by a profile-aware Factory2.
- These can run in parallel with each other and gate the deletion of MiniActivityManager / WestlakeInstrumentation / WestlakeActivityThread.

### Sequential dependencies:
- A1 (ServiceManager) → A3 / A4 (those need real services through M3).
- M4a (WestlakeActivityManagerService) → A4 (WestlakeActivityThread replacement).
- M4b (WestlakeWindowManagerService) → B2 (ViewGroup scaled-touch removal).
- C1 / C2 / C3 must complete before MAM / WI / WAT can be deleted entirely.

### Optimal staffing (under fast-path):
- One agent each on A2 (LayoutInflater swap), B1 (View marker removal), C1+C2+C3 (per-app migration); these are fully independent.
- M4a, M4b, M4c, M4d, M4e are independent of each other (per `BINDER_PIVOT_MILESTONES.md` dependency graph) — five agents in parallel.
- Sequential at the end: M4a green → A4 (WAT swap); M4b green → B2 (ViewGroup shrink).

---

## 5. Open Items / Risks Identified During Audit

1. **`fastForwardWestlakeHeadlessAnimation` in View.java (line 26106-26122)** — this is called from `startAnimation()`. If renderer-driven animation is reinstated post-pivot under real Choreographer (via M4b WindowManager), this body must be deleted; if Choreographer remains absent in Phase-1 sandbox, deletion would break animations. **Action:** gate deletion on M4b acceptance criteria, not C5 time.

2. **`getTag()` data-binding heuristic in View.java** — currently masks an absence of generated DataBinderMapper output at runtime. If post-pivot AOSP's real data-binding generated code runs unmodified (Hilt's `<App>_HiltModules.class`, `<App>DataBinderMapperImpl`), this heuristic becomes harmful — it returns null fast and breaks Lazy<T> initialization paths in some apps. **Action:** delete this heuristic in C2 phase before A4 (WAT swap) so app-side data-binding sees AOSP's native pathway.

3. **Westlake's base `Instrumentation.java` is also custom (not just `WestlakeInstrumentation` extending it)** — when replacing with AOSP Instrumentation, both files swap. Confirm via M4 testing that no other shim code subclasses our custom `Instrumentation`.

4. **`MiniActivityManager.ensureSingletonComponent()` reaches into `dagger.hilt.android.internal.managers.ApplicationComponentManager.singletonComponent`** (line 365). This is Hilt-internal state. Post-pivot, when AOSP's real ActivityThread runs Application.onCreate naturally, Hilt's own bootstrap fires inside that path — no framework-level seeding needed. Verify in M4a acceptance test that Hilt initialization is automatic with the AOSP path.

5. **`shim/java/com/westlake/services/` does not yet exist** — M4 deliverables introduce it. Architects building M4 services should target this directory consistently with `BINDER_PIVOT_DESIGN.md` §3.2 and `BINDER_PIVOT_MILESTONES.md` §M4a deliverables.

---

## 6. Audit Methodology

Approximately 35 minutes (under the 2-3 hour budget): file structure exploration, line-count survey, copyright-header inspection, grep-based modification surveys per file, partial content inspection (first 200 lines, key marker regions, and per-app branch confirmation for each of the 11 classes). The Westlake additions in View.java and ViewGroup.java were also examined to confirm shrink-vs-keep recommendations.

---

## 7. Critical Files for Implementation

The files most critical for implementing this audit's recommendations:

- `/home/dspfac/android-to-openharmony-migration/shim/java/android/os/ServiceManager.java`
- `/home/dspfac/android-to-openharmony-migration/shim/java/android/app/WestlakeActivityThread.java`
- `/home/dspfac/android-to-openharmony-migration/shim/java/android/app/MiniActivityManager.java`
- `/home/dspfac/android-to-openharmony-migration/shim/java/android/app/WestlakeInstrumentation.java`
- `/home/dspfac/android-to-openharmony-migration/shim/java/android/view/LayoutInflater.java`

End of ANDROID_SHIM_AUDIT.md.
