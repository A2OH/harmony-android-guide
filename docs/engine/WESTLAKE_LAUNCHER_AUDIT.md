# WestlakeLauncher.java Audit (Milestone C4)

**Author:** Architect agent (2026-05-12)
**Companion to:** `BINDER_PIVOT_MILESTONES.md` C4, `BINDER_PIVOT_DESIGN.md`
**File:** `shim/java/com/westlake/engine/WestlakeLauncher.java` (was 22,983 LOC; **12,668 LOC after CR14+CR16 execution**)

**STATUS:** C4 audit (this doc) **executed by CR14 + CR16 on 2026-05-12**.
The `[DELETE-after-M4]` tagged sections below have been processed in
CR14 (see `M4_DISCOVERY.md` §41) and the 4 CR14-deferred sections
finished off in CR16 (see `M4_DISCOVERY.md` §43). Sections that were
fully deleted, replaced with no-op stubs (entry points preserved for
external callers), or deferred to a follow-up CR are now annotated
`[DONE-CR14]`, `[STUBBED-CR14]`, `[DONE-CR16]`, `[STUBBED-CR16]`, or
`[DEFERRED-CR14]` respectively in their disposition columns. Below is
the original audit, preserved for traceability; see `M4_DISCOVERY.md`
§41 for the CR14 line-by-line summary and §43 for the CR16 summary.

This document maps the launcher's macro-sections to dispositions in the post-pivot plan. It is read-only audit; no code is touched here. Slimming happens during M7 (integration) once M3+M4 are validated.

## Disposition tags

- `[KEEP]` — load-bearing regardless of pivot.
- `[KEEP-but-shrink]` — load-bearing but per-app branches or marker emitters can drop.
- `[DELETE-after-M3]` — replaced by binder wiring (libbinder + servicemanager).
- `[DELETE-after-M4]` — replaced by Java service impl (M4a-e).
- `[DELETE-after-M6]` — replaced by surface-daemon / real frame pipeline.
- `[FOLD-into-X]` — move to a different file.
- `[REVIEW]` — needs human judgment before deletion.

---

## Section map

### S1. Class-level constants & static state — lines 28–168
Static singletons, surface/yelp dimensions, McD prop names, McD synthetic-fallback flags, McD section/recycler counters, image cache, glide probe IdentityHashMap, view-frame field cache, splash bytes, real-context handle.

**Disposition:** `[KEEP-but-shrink]`. Class-level state outlives pivot; McD-specific blocks (`sMcd*`, `sMcdImageCache`, `MCD_*_PROP`, hardcoded McD URLs at 146-151, `realIconsPng`) encode per-app knowledge and must move out.

### S2. Native JNI declarations & safe wrappers — lines 67–86, 169–281
14 native declarations + `safe*` wrappers. **Disposition:** `[KEEP-but-shrink]`. Keep `nativeLog`, `nativeReadFileBytes`, `nativePrintException`. Drop the rest after M3.

### S3. Framework policy / backend mode resolution — lines 283–452
`isRealFrameworkFallbackAllowed()`, `frameworkPolicyValue()`, McD-specific unsafe flags, `backendModeValue()`. **Disposition:** `[DELETE-after-M4]` **[DEFERRED-CR14]** — bodies kept since 30+ external shim files call these methods (Window, Context, MiniActivityManager, ApkLoader, Activity, WestlakeActivityThread, WestlakeInstrumentation, AppComponentFactory). Need either coordinated multi-file CR or callsite consolidation first. The strict-westlake-vs-control-android fork exists because we have two code paths; M4 unifies them.

### S4. ClassLoader & engine bootstrap — lines 454–565, 1998–2293
`engineClassLoader()`, `installSafeStandardStreams()`, `primeCharsetState()`, `repairClassLoaderPackageMaps()`, `loadAppClass()`. **Disposition:** `[KEEP-but-shrink]`. ClassLoader plumbing needed; `repairClassLoaderPackageMaps` and `primeCharsetState` are dalvikvm bug workarounds that should be upstream-fixed during M3.

### S5. Marker/trace/log infrastructure — lines 509–869
`stderrLog`, `startupLog`, `strictTrace`, `marker`, `noteMarker`, `appendCutoffCanaryMarker`, etc. **Disposition:** `[KEEP-but-shrink]`. Keep generic logging, route through real `android.util.Log` after M3.

### S6. Cutoff canary launch path — lines 1395–1903 (~510 LOC)
`launchCutoffCanaryStandalone()`, `launchCutoffCanaryViaWat()`, manifest scanning, L1/L3/L4 staging. **Disposition:** `[DELETE-after-M4]`. The cutoff canary was an experiment; after M3+M4 prove real framework boots, it's obsolete.

### S7. APK bootstrap helpers — lines 1904–2010
`launchFileProperty()`, `argValue()`. **Disposition:** `[KEEP]`. Universal launch-config plumbing.

### S8. Application instantiation & class patching — lines 870–1038
`tryUnsafeAllocInstance()`, `primeAllocatedApplication()`, `patchProblematicAppClasses()`. **Disposition:** `[REVIEW]`. Violates anti-pattern §6.4 (reflection as the answer). Post-M4a should be unnecessary.

### S9. ICU + security provider bootstrap — lines 1068–1234
`bootstrapIcuDataPath()`, `bootstrapSecurityProviders()`. **Disposition:** `[KEEP-but-shrink]`. Universal non-zygote bootstrap. Slim only diagnostic probes.

### S10. Package name resolution & per-app fallbacks — lines 1245–1393
`packageFallbackForKnownApps()` hardcodes `com.mcdonalds.app`. **Disposition:** `[KEEP-but-shrink]`. Remove per-app McD fallback; keep generic normalization.

### S11. APK reading & resource helpers — lines 2294–3416
`buildLaunchIntent()`, `readFileBytes()`, `wireStandaloneActivityResources()`, `isHomeDashboardActivity()`, `isOrderProductDetailsActivity()`. **Disposition:** `[KEEP-but-shrink]`. Generic APK/res reading stays; per-McD-app activity matchers drop.

### S12. HTTP bridge & McD live image / menu JSON fetcher — lines 2418–3270 (~850 LOC)
`bridgeHttpGetBytes()`, `ensureMcdMenuJson()`, `mcdLiveAdapterImageBytes()`, `mcdMenuCategoryImageUrlForLabel()`. **Disposition:** `[DELETE-after-M4]`. Entirely McD-specific live image fetching; move to a separate `McdLiveBridge.java` or delete.

### S13. main() entry point — lines 3651–3662
**Disposition:** `[KEEP]`. Trivial entry shell.

### S14. mainImpl() body — lines 3664–5530 (~1,860 LOC)
The bootstrap sequence with WAT-vs-MiniActivityManager selection, per-app prefer-WAT list (showcase/yelplive/mcdprofile/materialxmlprobe/mcdonalds), per-app `appClass` fallbacks, per-app `forceMakeApplicationForNextLaunch`, McD pre-onCreate seeding. **Disposition:** `[KEEP-but-shrink]`. Post-M4 shrinks to ~300 LOC: parse args → init binder → register services → bind framework → launch APK → render loop. Per-app branches to delete: Counter SharedPreferences pre-seed (4043-4060), McD `isMcdonaldsAppLaunch` block (4120-4131), per-app `appClass` fallbacks (4638-4661), `prefer WAT` list (4702-4707), per-app `performResumeActivity` (4841-4862), `launchMcdProfileControlledActivity` (4692-4698), `pendingDashboardClass` McD auto-launch (4898-4899).

### S15. McD application/context pre-seeding — lines 4133–4232 (~100 LOC)
Threaded McD Application.onCreate with timeout for Hilt. **Disposition:** `[DELETE-after-M4]`. Workaround for McD Hilt DI not completing through current MiniActivityManager.

### S16. Cutoff canary in mainImpl — lines 4035-4040, 4256-4265, 4296-4351, 4418-4419, 4595-4605 (~110 LOC)
**Disposition:** `[DELETE-after-M4]`. Same as S6.

### S17. Standalone splash + branded fallback render — lines 5104–5301 (~200 LOC)
Manual inflate of splash layouts + programmatic McD splash via OHBridge (red bg, "McDonald's" in golden yellow). **Disposition:** `[DELETE-after-M6]`. Once surface-daemon drives rendering, the app's own SplashActivity draws.

### S18. buildRealSplashUI / buildMcDonaldsUI — lines 5555–5805
Hardcodes 9 McD menu items with prices. **Disposition:** `[DELETE-after-M4]`. Pure per-app McD shopping mock UI.

### S19. Dashboard scaffolding — lines 5806–8053 (~2,250 LOC)
**Largest section.** `buildProgrammaticDashboardFallbackRoot()`, `findDashboardFragmentInstance()`, `seedHomeDashboardFragmentInjectedMembers()`, `tryAttachHomeDashboardFragment()`, dashboard button helpers. **Disposition:** `[DELETE-after-M4]`. Identical pattern to `WestlakeFragmentLifecycle` — launcher-side McD HomeDashboardFragment seeding mirror.

### S20. Dashboard touch routing & fallback display — lines 8054–8624
`routeDashboardFallbackTouch()` with hardcoded y-coord-to-action mapping ("y >= 248 → start_order"; "y >= 740 → McCafe Iced Coffee"). **Disposition:** `[DELETE-after-M4]`. Entirely per-McD-app.

### S21. launchMcdProfileControlledActivity & lifecycle helpers — lines 8626–8806
Manual reflective Activity instantiation for "mcd profile" test app. **Disposition:** `[DELETE-after-M4]`. Bypasses MiniActivityManager and AOSP Instrumentation.

### S22. Per-app activity matchers — lines 8808–8867
`isShowcaseActivity()`, `isYelpLiveActivity()`, `isMaterialYelpActivity()`, `isMcdProfileActivity()`, `isMaterialXmlProbeActivity()`. **Disposition:** `[DELETE-after-M6]`. Move test apps elsewhere or delete.

### S23. yelp-live direct frame loop & UI primitives — lines 8868–10174 (~1,310 LOC)
Synthetic Yelp-mock UI painted via DLST canvas opcodes. **Disposition:** `[DELETE-after-M6]`.

### S24. material-yelp + mcd-profile + material-xml direct frame loops — lines 10176–11538 (~1,360 LOC)
**Disposition:** `[DELETE-after-M6]`. Per-test-app synthetic painters.

### S25. showcase direct frame loop & touch routing — lines 11540–17769
**Disposition:** `[DELETE-after-M6]`. Test-app touch dispatcher + synthetic UI painter.

### S26. Decor / layout normalization helpers — lines 11729–12182, 14087–14688, etc. (~2,400 LOC)
**Second-largest.** McD dashboard- and McD-PDP-specific manual measure/layout via reflection on private `View.mLeft/mTop/mRight/mBottom` fields. **Disposition:** `[DELETE-after-M4]`. Anti-pattern §6.4. Real `View.measure + View.layout` drive naturally post-M6.

### S27. McD Glide / SDK seeding — lines 13196–13594
Seeds McD's Glide internals, prunes ExifInterfaceImageHeaderParser, seeds McD `CoreManager`, `SDKParams$Builder`, McD product catalog with hardcoded calories. **Disposition:** `[DELETE-after-M4]`.

### S28. Generic reflection helpers — lines 13597–13998
`invokePublicOneArg()`, `getFragmentFieldReflective()`, etc. **Disposition:** `[DELETE-after-M4]`. All users are McD-fragment-seeding paths; orphans when those go.

### S29. Showcase frame rendering & view tree walker — lines 14689–17283 (~2,600 LOC)
**Largest section.** `writeShowcaseDirectFrame()`, `writeShowcaseXmlTreeFrame()`, `renderMcdOrderPdpProjection()`, `renderMcdDashboardProjection()`. Synthesizes McD-themed dashboard / product-detail-page from hardcoded coordinates. **Disposition:** `[DELETE-after-M6]`.

### S30. Showcase state-frame & DLST byte writer — lines 17470–17886
DLST opcode protocol (header `0x444C5354` "DLST" magic). **Disposition:** `[DELETE-after-M6]`. Surface daemon delivers real surface buffers post-M6.

### S31. View tree introspection helpers — lines 17888–18242
`safeViewChildCount()`, `safeViewLeft()`, etc. **Disposition:** `[KEEP-but-shrink]`. Keep safe-accessors; delete `logDashboardInstallProbe` after M4.

### S32. populateDashboardFallback — lines 18251–18314
**Disposition:** `[DELETE-after-M4]`. Branches into S19/S20.

### S33. renderLoop — lines 18316–18919 (~600 LOC)
`pendingDashboardClass` McD auto-launch, WestlakeNode/WestlakeRenderer offline render. **Disposition:** `[DELETE-after-M6]`. Surface daemon replaces offline render.

### S34. tryEmitNoiceFallbackFrame & strict standalone main loop — lines 18920–19103
Heartbeat daemon + strict-standalone pump loop. **Disposition:** `[DELETE-after-M4]`.

### S35. writeStrictStandaloneViewFrame — lines 19105–19299
Sole strict-mode frame emitter. **Disposition:** `[DELETE-after-M6]`.

### S36. McD activity recording — lines 19301–19325
`recordMcdOrderNavigation()`, `recordMcdCategoryNavigation()`. **Disposition:** `[DELETE-after-M4]`. Per-app diagnostic markers.

### S37. Generic touch routing — lines 19326–19704
`routeMcdPdpGenericAddHitClick()`, `routeMcdOrderPdpProjectedHitClick()`. **Disposition:** `[DELETE-after-M6]`. Offline-input-routing; real touch via real surface post-M6.

### S38. McD PDP stock button click + binding plumbing — lines 19715–20100
**Disposition:** `[DELETE-after-M4]`. Per-McD-PDP click handler.

### S39. McD PDP stock add machinery — lines 20100–21100 (~1,000 LOC)
RxJava+LiveData+Hilt observer dispatch re-implemented reflectively. Reflects on McD's obfuscated fields (`E0`, `t0`, `G0`, `s7`, `A7`). **Disposition:** `[DELETE-after-M4]`. Anti-pattern §6.2 + §6.4 + `feedback_no_per_app_hacks.md`.

### S40. McD PDP commit & RealmList / stock seeding — lines 21100–21859
Synthesizes McD CartProduct + Realm storage via reflection. **Disposition:** `[DELETE-after-M4]`.

### S41. Generic recycler hit + list view click routing — lines 22124–22535
`routeListViewItemClick()`, `findRecyclerHitAt()`, `invokeMcdPopularItemSemanticClick()`. **Disposition:** `[DELETE-after-M6]`. Real touch routing works after M6.

### S42. McD dashboard content injection — lines 22706–22904
Hardcoded 5-section McD dashboard. **Disposition:** `[DELETE-after-M4]`.

### S43. Real McD drawable icon rendering via real Android — lines 22906–22982
Uses real `Context.getDrawable()` via app_process64. **Disposition:** `[DELETE-after-M4]`. Experiment-time control-android probe.

---

## LOC Breakdown by Disposition

| Disposition | LOC (approx) | % | Includes |
|---|---|---|---|
| `[KEEP]` | ~80 | 0.3% | S13 (main entry), S7 (launch file properties) |
| `[KEEP-but-shrink]` | ~3,600 | 15.7% | S1, S2, S4, S5, S9, S10, S11, S14, S31 |
| `[DELETE-after-M3]` | 0 | 0% | — |
| `[DELETE-after-M4]` | ~10,800 | 47.0% | S3, S6, S8 (partial), S12, S15, S16, S18, S19, S20, S21, S26, S27, S28, S32, S34, S36, S38, S39, S40, S42, S43 |
| `[DELETE-after-M6]` | ~7,900 | 34.4% | S17, S22, S23, S24, S25, S29, S30, S33, S35, S37, S41 |
| `[REVIEW]` | ~600 | 2.6% | S8 (alloc/patch — depends on dalvikvm stability) |
| **Totals** | **~22,983** | **100%** | |

**Approximate post-pivot launcher size:** ~3,700 LOC (84% reduction).

---

## Top 5 Sections by Size

| Rank | Section | LOC | Disposition |
|---|---|---|---|
| 1 | S29 Showcase frame render + view tree walker + McD projections | ~2,600 | `[DELETE-after-M6]` |
| 2 | S26 Decor / layout normalization + McD section seeding | ~2,400 | `[DELETE-after-M4]` |
| 3 | S19 Dashboard scaffolding + Hilt fragment seeding | ~2,250 | `[DELETE-after-M4]` |
| 4 | S14 mainImpl body | ~1,860 | `[KEEP-but-shrink]` |
| 5 | S25+S24 showcase + material direct frame loops | ~1,360 | `[DELETE-after-M6]` |

---

## Concerning Code Patterns (Follow-up Tasks for Human Review)

1. **`Unsafe.allocateInstance` for framework objects** (S8, S19, S21). Anti-pattern §6.4. Plan: validate real-framework ctor works post-M4; remove Unsafe paths.

2. **Reflection on obfuscated McD field names** (S19, S26, S27, S38, S39, S40) — `K0`, `O0`, `T0`, `D`, `E`, `I`, `J`, `t0`, `E0`, `G0`, `s7`, `A7`. McD-build-specific (R8 minified); breaks silently on McD updates. Plan: enumerate and tag for removal-after-M4.

3. **Hardcoded view IDs** (S19/S26: `resolveKnownMcdViewId` hardcodes 17 McD layout IDs as `0x7f0b...`). Violates `feedback_no_per_app_hacks.md`. Plan: delete entirely after M4.

4. **`pendingDashboardClass` cross-class static** (S14, S33: shared static between WestlakeActivityThread and WestlakeLauncher). Cross-component coupling via mutable static. Plan: delete after M4a.

5. **`writeStrictStandaloneViewFrame` DLST byte protocol** (S30, S35) — custom serialized opcode protocol bypassing SurfaceFlinger. Plan: delete after M6.

6. **Per-app branch lists in mainImpl** (S14: `prefer WAT` list at 4702-4707; per-app `appClass` fallbacks; per-app `forceMakeApplicationForNextLaunch`; per-app `performResumeActivity`). Plan: C2 cleanup task should expand to remove these too.

7. **String-stability hacks** (S10: `copyString`, `stabilizeString`). ART-bug workarounds. Plan: file as dalvikvm task, remove from launcher.

8. **`Charset.cache2/gate/defaultCharset` reflection** (S4: `primeCharsetState`). Plan: fix Charset static init in dalvikvm; remove from launcher.

9. **NewRelic class patching** (S8: hardcodes 3 NewRelic class names). Plan: identify NewRelic's actual failure mode; fix in framework jar or skip via classloader filter.

10. **`HomeDashboardFragment` reflective constructor field seeding** (S19: `seedHomeDashboardFragmentCtorState`). Allocs `io.reactivex.disposables.CompositeDisposable`, `com.mcdonalds.homedashboard.deals.DealsFragmentProvider`. Launcher-side equivalent of `WestlakeFragmentLifecycle`. Plan: C1's deletion should pull this entire helper family with it.

11. **`sMcdPdpDeferredStockAddLock` + worker thread** (S39: background thread reflectively invokes McD PDP fragment methods on main looper). Anti-pattern §6.3.

12. **Dead "UNREACHABLE programmatic splash" code** (S17, lines 5218-5282). Delete now or move to doc.

13. **`writeStandaloneFile` early McD context vs deferred McD context fork** (S14, lines 4133-4163 vs 4173-4225). Two places where McD Application is special-cased.

14. **`OHBridge.surfaceCreate(0, ...)`** with hardcoded `surfaceId=0` everywhere. Single-surface assumption. Will need rework when surface daemon supports multiple surfaces.

15. **Frequent `try { ... } catch (Throwable ignored) {}` swallows** — ~250 occurrences. Each defensive but loses error visibility. Plan: pass 3 of code review.

---

## Cleanup Sequencing After Milestones

| After milestone | Sections to delete | LOC freed |
|---|---|---|
| C1 (WestlakeFragmentLifecycle removed) | Verify S19/S26 references | 0 |
| C2 (per-app constants out of Fragment*/MAM) | S22 helpers fold into C2 audit doc | 0 |
| M3 (binder + servicemanager up) | S3 (framework policy fork simplifies) | ~170 |
| M4a (ActivityManagerService) | S6 cutoff canary, S15 McD pre-onCreate, S20 launchMcdProfileControlled | ~700 |
| M4 complete (a-e) | S19, S26, S27, S28, S38, S39, S40, S42 | ~8,000 |
| M6 (surface daemon) | S17, S22, S23, S24, S25, S29, S30, S33, S35, S37, S41 | ~7,900 |
| M7 (noice e2e) | Verify all KEEP-but-shrink sections shrunk | ~1,000 |
| **Total**: 22,983 → ~3,700 LOC | | **~17,800** |

---

## Methodology Notes

- File read in chunks (offsets 1, 200, 700, 1500, 2400, 3651, 4250, 4900, 5550, 6300, 8200, 8634, 12300, 15000, 17470, 18251, 18316, 18920, 19326, 21500, 22100, 22700) covering major bands of behavior.
- Method count: 699 method/inner-class signatures detected via grep.
- Section grouping is by purpose, not by line range: a single "section" may span discontinuous line blocks but represents a single architectural concern.
- LOC estimates are approximate (±10%) due to overlapping section purposes.
- Time spent: ~25 minutes of read-only file exploration.

End of WESTLAKE_LAUNCHER_AUDIT.md.

---

## CR14 Execution Summary (2026-05-12)

This section records the actual disposition CR14 applied to each
`[DELETE-after-M4]` tagged section above. The original section text is
preserved for traceability.

| § | Section | Audit estimate | Audit disposition | CR14 actual | Resulting LOC delta |
|---|---|---|---|---|---|
| S3 | Framework policy / backend mode resolution | ~170 | `[DELETE-after-M4]` | `[DEFERRED-CR14]` → **`[STUBBED-CR16]`** — 4 entry methods stubbed (false / strict / westlake_only); helpers + caching state deleted | ~-105 |
| S6 | Cutoff canary launch path | ~510 | `[DELETE-after-M4]` | **`[DONE-CR14]`** (single false-returning stub kept for mainImpl) | ~-285 |
| S12 | HTTP bridge + McD live image / menu JSON | ~850 | `[DELETE-after-M4]` | `[DEFERRED-CR14]` → **`[STUBBED-CR16]`** — 6 entry methods stubbed (return null / fallback / "missing_bridge_dir" response); JSON menu helpers + utf8 codec kept (S29/S33 still call them off cached sMcdMenuJson) | ~-380 |
| S15 | McD application/context pre-seeding (mainImpl) | ~100 | `[DELETE-after-M4]` | `[DEFERRED-CR14]` → **`[DONE-CR16]`** — `seedMcdonaldsApplicationContext` call + McD-specific onCreate-deferred branch deleted; threaded onCreate workaround kept (applies to all apps) | ~-25 |
| S16 | Cutoff canary in mainImpl branches | ~110 | `[DELETE-after-M4]` | `[DEFERRED-CR14]` → **`[DONE-CR16]`** — 13 `if (cutoffCanaryLaunch)` branches in mainImpl deleted; canary helpers (`isCutoffCanaryLaunch`, `isExplicitCutoffCanaryLaunch`, `isExplicitNonCanaryLaunch`, `stagedCutoffCanaryPresent`, `launchFileBytesContain`, `safeNativeIsCutoffCanaryLaunch`, `describeProbeActivity`, `isCutoffCanaryActivity`, `readCutoffCanaryMarker`, `launchCutoffCanaryStandalone`, `cutoffCanaryStage`, `nativeIsCutoffCanaryLaunch` decl) + state fields + L3/L4 activity constants deleted | ~-350 |
| S18 | buildRealSplashUI / buildMcDonaldsUI | ~250 | `[DELETE-after-M4]` | **`[DONE-CR14]`** (zero callers found; pure delete) | ~-254 |
| S19 | Dashboard scaffolding | ~2,250 | `[DELETE-after-M4]` | **`[STUBBED-CR14]`** (13 entry points for S25/S26 callers) | ~-2,035 |
| S20 | Dashboard touch routing | ~570 | `[DELETE-after-M4]` | **`[STUBBED-CR14]`** (5 McD-specific stubs; 6 shared helpers kept intact) | ~-440 |
| S21 | launchMcdProfileControlledActivity helpers | ~180 | `[DELETE-after-M4]` | **`[DONE-CR14]`** (full delete + S14 mainImpl branch) | ~-189 |
| S26 | Decor / layout normalization | ~2,400 | `[DELETE-after-M4]` | **`[STUBBED-CR14]`** (15 entry points for S25/S29/S33/S37 callers) | ~-2,095 |
| S27 | McD Glide / SDK seeding | ~400 | `[DELETE-after-M4]` | **`[STUBBED-CR14]`** (7 entry points for S25/S26 callers) | ~-400 |
| S28 | Generic reflection helpers | ~400 | `[DELETE-after-M4]` | **`[DONE-CR14]`** (zero external callers after S26 delete; pure delete) | ~-461 |
| S32 | populateDashboardFallback | ~64 | `[DELETE-after-M4]` | **`[STUBBED-CR14]`** (entry preserved; WestlakeInstrumentation calls it) | ~-64 |
| S34 | strict standalone main loop | ~180 | `[DELETE-after-M4]` | **`[STUBBED-CR14]`** (5 entry points for S11/S14/S33) | ~-184 |
| S36 | McD activity recording | ~25 | `[DELETE-after-M4]` | **`[STUBBED-CR14]`** (entry preserved; LayoutInflater calls them) | ~-8 |
| S38 | McD PDP stock button click | ~385 | `[DELETE-after-M4]` | **`[STUBBED-CR14]`** (3 entry points, part of S38-40 wholesale stub) | (combined w/ S39+S40) |
| S39 | McD PDP stock add machinery | ~1,000 | `[DELETE-after-M4]` | **`[STUBBED-CR14]`** | (combined w/ S38+S40) |
| S40 | McD PDP commit + RealmList | ~760 | `[DELETE-after-M4]` | **`[STUBBED-CR14]`** | combined S38+S39+S40: ~-2,408 |
| S42 | McD dashboard content injection | ~200 | `[DELETE-after-M4]` | **`[DONE-CR14]`** (zero callers; pure delete) | ~-199 |
| S43 | Real McD drawable icon rendering | ~76 | `[DELETE-after-M4]` | **`[DONE-CR14]`** (zero callers; pure delete) | ~-77 |
| (orphan sweep) | 38 method stubs + 29 static fields | — | — | **`[DONE-CR14]`** (second-pass cleanup) | ~-200 |
| **Total** | | **~10,800** | | **~9,455 LOC removed** | (41% reduction) |

**Outcome:** 14 of 21 sections fully addressed (DONE or STUBBED with
external API intact); 4 deferred for valid reasons (S3 = external API
surface; S12 = external API; S15/S16 = mainImpl coupling, needs M4a
validation). Build clean, HelloBinder PASS on phone cfb7c9e3.

aosp-shim.dex: 1,577,644 → 1,393,148 bytes (-184,496 bytes / -11.7%).

---

## CR16 Execution Summary (2026-05-12)

CR16 picked up the 4 sections CR14 deferred (S3, S12, S15, S16) and
migrated them to no-op defaults / deleted call sites. All entry methods
that 30+ external shim files call are preserved (return safe defaults);
internal helpers and state fields that became orphan after stubbing were
deleted.

### S3 — Framework policy / backend mode resolution

Entry methods stubbed to constants:
- `frameworkPolicyValue()` → returns `"westlake_only"`
- `isRealFrameworkFallbackAllowed()` → returns `false`
- `backendModeValue()` → returns `"strict"`
- `isControlAndroidBackend()` → returns `false`

Deleted: `isTruthyConfigValue`, `isTruthyFileValue` (orphan helpers);
`sBackendModeResolved`, `sControlAndroidBackendCached`,
`sResolvedBackendMode` (caching state); `BACKEND_MODE_PROP`,
`BACKEND_MODE_TARGET_OHOS`, `BACKEND_MODE_CONTROL_ANDROID` constants.

**External caller behaviour preserved.** Every external caller takes the
form `if (X) { control-android path; } else { strict-westlake path; }`
or `boolean strictStandalone = !X;`. With stubs always returning the
westlake-only / strict path, the M4-correct behaviour is now the only
behaviour. The 5 isMcd*-style flag methods listed in the task brief
(`isDashboardSyntheticFallbackEnabled`, `isMcdUnsafe*`,
`isMcdProbeFlagFileEnabled`) had already been deleted in CR14's orphan
sweep and have no current callers; no need to re-stub.

### S12 — HTTP bridge + McD live image / menu JSON

Entry methods stubbed:
- `bridgeHttpLastStatus()` → `0`
- `bridgeHttpLastError()` → `null`
- `bridgeHttpGetBytes(...)` → `null`
- `bridgeHttpRequest(...)` → `new BridgeHttpResponse(0, "{}", new byte[0], "missing_bridge_dir", false, url)`
- `mcdLiveAdapterLabel(rid, pos, fallback)` → `fallback`
- `mcdLiveAdapterImageBytes(rid, pos)` / `(rid, pos, label)` → `null`

`BridgeHttpResponse` inner class kept (return type of `bridgeHttpRequest`).

Deleted internal helpers: `cacheMcdBridgeResponse`, `ensureMcdMenuJson`,
`logMcdPromoImageFallback`, `bridgeHttpErrorResponse`,
`bridgeHttpBridgeDir`, `clampBridgeHttpMaxBytes`,
`clampBridgeHttpTimeout`, `normalizeBridgeHttpMethod`,
`decodeBridgeMetaUtf8`, `stringToUtf8`, `bridgeBase64Encode`,
`bridgeBase64Decode`, `bridgeBase64Value`, `BRIDGE_BASE64`,
`bytesToUtf8`, `parseBridgeMetaInt`, `parseBridgeMetaValue`. Deleted
state: `sHttpBridgeSeq`, `sHttpBridgeLastStatus`,
`sHttpBridgeLastError`, `sMcdHeroJson`, `sMcdMarketingJson`,
`sMcdPromoImageFallbackLogged`, `MCD_MENU_JSON_URL`.

Kept: `sMcdMenuJson` (stays null without fetch — callers gracefully fall
back), `sMcdImageCache` (stays empty), `MCD_PROMOTION_SEED_IMAGE_URL`,
`mcdMenuProductImageUrlForLabel`, `mcdMenuCategoryImageUrlForLabel`,
`mcdMenuCategoryNameForLabel`, `mcdMenuNameMatches`,
`normalizeMcdMenuName`, `extractJsonStringValue`, `mcdLiveMenuOffset`,
`nthMcdMenuEnglishValue`, `nthMcdMenuEnglishImageUrl`,
`stringArrayContains`, `utf8BytesToString`, `appendUtf8CodePoint` —
these are called from [DELETE-after-M6] sections that still want JSON
parsing of any cached menu blob.

**External caller behaviour preserved.** `WestlakeHttpTransport`
explicitly handles the `"missing_bridge_dir"` error code by returning
`null` (then okhttp falls through to real native path). `McDHttpClient`
/ `McDRequestManager` read `bridge.status` / `bridge.error` /
`bridge.body` defensively — the all-zero/empty response yields a 500
shadow response with empty body, exactly as designed. `LayoutInflater`
wraps the McD live calls in try/catch with `return null` fallback.

### S15 — McD application/context pre-seeding in mainImpl

Deleted: the 12-line `if (isMcdonaldsAppLaunch) { WestlakeActivityThread.seedMcdonaldsApplicationContext(...) }`
call site after `MiniServer.currentSetApplication(customApp)`. M4a +
WestlakeContextImpl now handle this through the regular lifecycle.

Deleted the McD-specific branch in the threaded `Application.onCreate`
block (`if (isMcdonaldsAppLaunch) { startupLog("...deferred..."); }`).
McD's `onCreate` now runs through the same threaded-with-timeout path as
every other app.

Deleted helper: `isMcdonaldsPackageOrClass` (now orphan).

### S16 — Cutoff canary in mainImpl branches

Surgically deleted 13 dispersed `if (cutoffCanaryLaunch)` /
`if (cutoffCanaryTargetLaunch)` branches in mainImpl (resource setup,
package resolution, ApkInfo override, eager activity resolve, splash
skip, headless probe, AM direct launch markers). All branches led into
S6 (deleted CR14) or only set canary-package-specific overrides on
locals that the strict-westlake fallthrough already handles correctly.

Deleted helpers (now orphan): `isCutoffCanaryLaunch`,
`isExplicitCutoffCanaryLaunch`, `isExplicitNonCanaryLaunch`,
`launchFileBytesContain`, `stagedCutoffCanaryPresent`,
`describeProbeActivity`, `isCutoffCanaryActivity`,
`readCutoffCanaryMarker`, `launchCutoffCanaryStandalone`,
`cutoffCanaryStage`, `safeNativeIsCutoffCanaryLaunch`, plus the
`nativeIsCutoffCanaryLaunch` declaration, `sBootCutoffCanaryLaunch`
field, and `CUTOFF_CANARY_ACTIVITY` / `CUTOFF_CANARY_L3_ACTIVITY` /
`CUTOFF_CANARY_L4_ACTIVITY` constants.

Kept: `appendCutoffCanaryMarker` (external callers in View, ViewGroup,
Activity, etc. still emit cutoff canary markers; the function still
writes to a log file), `appendCutoffCanaryTrace` (used by marker /
noteMarker), `CUTOFF_CANARY_PACKAGE` (used in `persistLaunchPackage`),
`CUTOFF_CANARY_STAGE_PROP` (used by `--canary-stage` arg parsing),
`CUTOFF_CANARY_MARKER_PATH` / `CUTOFF_CANARY_PUBLIC_MARKER_PATH` /
`CUTTOFF_CANARY_TRACE_PATH` / `CUTOFF_CANARY_PUBLIC_TRACE_PATH` (still
referenced by logging helpers).

### CR16 LOC + dex deltas

| Metric | Pre-CR16 (post-CR14) | Post-CR16 | Delta |
|---|---|---|---|
| WestlakeLauncher.java LOC | 13,528 | 12,668 | **-860 LOC (-6.4%)** |
| aosp-shim.dex bytes | 1,393,148 | 1,380,264 | **-12,884 bytes (-0.93%)** |

### Verification

- **Build:** Clean (`scripts/build-shim-dex.sh` succeeds; pre-existing
  warnings about McDListener / RequestProvider default interface methods
  unchanged).
- **HelloBinder smoke test (M3 baseline)**: **PASS** on phone
  `cfb7c9e3`. `getService("westlake.test.echo")` returns non-null,
  exit code 0.
- **AsInterfaceTest**: **PASS** on phone `cfb7c9e3`.

### Outcome

All 4 CR14-deferred sections fully migrated. The full CR14+CR16 reduction
on WestlakeLauncher.java is now **22,983 → 12,668 LOC (−10,315 LOC,
−44.9%)** with the same external-caller API surface preserved
throughout.

---

## CR23-fix Note (2026-05-13)

A subsequent investigation (CR23-fix, see `M4_DISCOVERY.md` §49) checked
the hypothesis that CR14 + CR16 deletions of `WestlakeLauncher.java`
sections broke the real-McD VM bootstrap (post-session McD validation
showed `vm_pid=missing`, all dashboard markers absent vs the pre-session
2026-05-04 baseline with `mcd_stock_dashboard_view_attached` PASS +
strict frame rendered).  **The hypothesis was disproved.**  The actual
breakage was not in any deleted section — it was the **PF-arch-053 BCP
shim deployment** (CR15/CR17) putting `framework.jar` + `ext.jar` +
`services.jar` on `-Xbootclasspath`, which flipped the runtime class
resolution for `android.content.Context` (concrete shim class → abstract
framework class; `new Context()` now throws `InstantiationError`) and
`android.app.Activity` (shim's 6-arg `attach` → framework's 17+-arg
`attach`; calling 6-arg now throws `NoSuchMethodError`).  Plus
`services.jar` on BCP collides with the launched APK's R8-obfuscated
Guava ImmutableMap.

CR23-fix did NOT un-delete any `[DONE-CR14]` / `[STUBBED-CR14]` /
`[DONE-CR16]` / `[STUBBED-CR16]` section.  All dispositions in this
audit remain correct.  CR23-fix made the dalvikvm sandbox tolerate the
framework.jar BCP shift in `shim/java/android/app/WestlakeActivityThread.java`
and the host APK's BCP construction in
`westlake-host-gradle/app/src/main/java/com/westlake/host/WestlakeVM.kt`;
no launcher edits were needed.

See `M4_DISCOVERY.md` §49 for full diagnosis + fix details.
